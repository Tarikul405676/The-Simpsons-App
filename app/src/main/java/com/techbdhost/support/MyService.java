package com.techbdhost.support;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;
import android.content.Intent;
import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.Notification;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import android.os.*;
import android.content.*;
import android.app.*;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.*;
import org.json.*;

import android.media.MediaPlayer;
import android.provider.Settings.Secure;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.*;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.android.volley.toolbox.JsonObjectRequest;

import com.android.volley.toolbox.HurlStack;
import java.text.DecimalFormat;
import android.telephony.SmsManager;
import android.Manifest;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.provider.Settings.Secure;
import java.util.concurrent.Semaphore;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.content.BroadcastReceiver;
import android.net.Uri;
import android.telecom.TelecomManager;
import android.telecom.PhoneAccountHandle;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.romellfudi.ussdlibrary.*;

public class MyService extends Service {
    
    
    
	 private Context myContext = this;
     public static boolean isServiceRunning =false;
     MediaPlayer mp;
     
     private  USSDApi ussdApi;
     //PowerLoad key used for get amount key from server
     private String [] PowerloadKey;
     private String [] AddBalanceSMSList;
     private String [] PowerLoadMessage;
     
     private String ROBIPowerNextCommand ="";
     private String PIN ="";
     
     private   int i = 0;
     private String ExtracCodeFromMessage = "";
     
     private ArrayList<HashMap<String, Object>> RechargeListMap = new ArrayList<>();
     private ArrayList<String> CommandString = new ArrayList<>();
     private ArrayList<String> ReadAbleSMSList = new ArrayList<>();
 	private ArrayList<HashMap<String, Object>> WaitLISTMAP = new ArrayList<>();
    
     

    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        isServiceRunning =true;
        
       if (Const.FetchVALUE("EnableSupportServer").equals("true")) {
          if (Const.FetchVALUE("isSiM1Enable").equals("true") && Const.FetchVALUE("SiM1Enable").equals("true")) {
            SyncRechargeSiM1();
          }else if (Const.FetchVALUE("isSiM2Enable").equals("true") && Const.FetchVALUE("SiM2Enable").equals("true")) {
            SyncRechargeSiM2();
          }
          setEvent("0","SiM Support server is running now.....");
          LocalBroadcastManager.getInstance(this).registerReceiver(SmSReceiver, new IntentFilter("android.provider.Telephony.SMS_RECEIVED"));
       }else{
          Const.StringSAVE("EnableSupportServer","false");
          stopSelf();
       }
    }
    
    
    public boolean CheckPowerLoad(final String _Amount){
        
        for (int i = 0; i < PowerloadKey.length; i++) {
          if (PowerloadKey[i].replaceAll("\\>(\\d+)","").equals(_Amount)) {
             if(PowerloadKey[i].contains(">")){
                 ROBIPowerNextCommand = PowerloadKey[i].replaceAll("(\\d+)\\>","");
             }else{
                 ROBIPowerNextCommand = "";
             }
             AndroXUtil.showMessage(getApplicationContext(), ROBIPowerNextCommand);
             return true;
          }else{
             ROBIPowerNextCommand = "";
          }
        }
        return false;
    }
    
    public void SyncRechargeSiM1(){
      if (Const.FetchVALUE("EnableSupportServer").equals("true")) {
    	if (Const.FetchJSONVALUE("EnableAUTOR").equals("true")) {
          String postUrl = Const.GetRequest();
          
          StringRequest SyncRechargeSiM1_s = new StringRequest(Request.Method.POST, postUrl , new Response.Listener<String>() {
			
			@Override
	        public void onResponse(String response) {
               if (!response.toString().equals("[]") && _isJSONValid(response)) {
                 RechargeListMap = new Gson().fromJson(response.toString(), new TypeToken<ArrayList<HashMap<String, Object>>>(){}.getType());
                 SelectCommandSiM1(RechargeListMap.get((int)0).get("balance").toString(),RechargeListMap.get((int)0).get("phone").toString(),RechargeListMap.get((int)0).get("type").toString());
                 
                   if(CheckPowerLoad(RechargeListMap.get((int)0).get("balance").toString())){
                    SelectCommandSiM1POWERLOAD(RechargeListMap.get((int)0).get("balance").toString(),RechargeListMap.get((int)0).get("phone").toString(),RechargeListMap.get((int)0).get("type").toString());
                    runPowerLoadCommand("RECHARGE",RechargeListMap.get((int)0).get("balance").toString(),0,CommandString);
                    setEvent("0","New  PowerLoad Received");
                   }else{
                    runCommand("RECHARGE",0,CommandString);
                   }
                   setEvent("0","New  request received for SiM1...EventLOG");
                   
		       }else{
                if (Const.FetchVALUE("isSiM2Enable").equals("true") && Const.FetchVALUE("SiM2Enable").equals("true")) {
           	   SyncRechargeSiM2();
         		}else{
            	   SyncRechargeSiM1();
         		}		
               }
               UPDATEConfig2();
            }
  	    },
           new Response.ErrorListener() {
	
	        @Override
	        public void onErrorResponse(VolleyError error) {
             SyncRechargeSiM2();
		            
		    }
  	    }){
	        @Override
	        public Map<String, String> getHeaders() throws AuthFailureError {
		      Map<String,String> params = new HashMap<String, String>();
		      params.put("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
		      return params;
		    }
	
	        @Override
	        protected Map<String,String> getParams(){
		     Map<String,String> params = new HashMap<String, String>(); 
			 params.put("SiMType", Const.FetchVALUE("SiM1ID"));
		      return params;
		    }
	
		    @Override
		    public Priority getPriority() {
		 	return Priority.IMMEDIATE;
	    	}
			
	      };
           SyncRechargeSiM1_s.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 30, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
           SyncRechargeSiM1_s.setShouldCache(false);
           MySingleton.getInstance(this).addToRequestQueue(SyncRechargeSiM1_s);
        }else{
          UPDATEConfig2();
        }
      }

    }
    
    public void SyncRechargeSiM2(){
      if (Const.FetchVALUE("EnableSupportServer").equals("true")) {
    	if (Const.FetchJSONVALUE("EnableAUTOR").equals("true")) {
          String postUrl = Const.GetRequest();
	
         StringRequest SyncRechargeSiM2_s = new StringRequest(Request.Method.POST, postUrl , new Response.Listener<String>() {
			
			@Override
	        public void onResponse(String response) {
              if (!response.toString().equals("[]") && _isJSONValid(response)) {
                 RechargeListMap = new Gson().fromJson(response.toString(), new TypeToken<ArrayList<HashMap<String, Object>>>(){}.getType());
                 SelectCommandSiM2(RechargeListMap.get((int)0).get("balance").toString(),RechargeListMap.get((int)0).get("phone").toString(),RechargeListMap.get((int)0).get("type").toString());
                 
                   if(CheckPowerLoad(RechargeListMap.get((int)0).get("balance").toString())){
                    SelectCommandSiM2POWERLOAD(RechargeListMap.get((int)0).get("balance").toString(),RechargeListMap.get((int)0).get("phone").toString(),RechargeListMap.get((int)0).get("type").toString());
                    runPowerLoadCommand("RECHARGE",RechargeListMap.get((int)0).get("balance").toString(),1,CommandString);
                    setEvent("0","New  PowerLoad Received for SiM2");
                   }else{
                    runCommand("RECHARGE",1,CommandString);
                   }
                   setEvent("0","New  request received for SiM2....");
                   
		       }else{
                if (Const.FetchVALUE("isSiM1Enable").equals("true") && Const.FetchVALUE("SiM1Enable").equals("true")) {
           	   SyncRechargeSiM1();
         		}else{
            	   SyncRechargeSiM2();
         		}
               }
               UPDATEConfig2();
            }
  	    },
           new Response.ErrorListener() {
	
	        @Override
	        public void onErrorResponse(VolleyError error) {
             SyncRechargeSiM1();
		            
		    }
  	    }){
	        @Override
	        public Map<String, String> getHeaders() throws AuthFailureError {
		      Map<String,String> params = new HashMap<String, String>();
		      params.put("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
		      return params;
		    }
	
	        @Override
	        protected Map<String,String> getParams(){
		     Map<String,String> params = new HashMap<String, String>(); 
			 params.put("SiMType", Const.FetchVALUE("SiM2ID"));
		      return params;
		    }
	
		    @Override
		    public Priority getPriority() {
		 	return Priority.IMMEDIATE;
	    	}
			
	      };
           SyncRechargeSiM2_s.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 30, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
           SyncRechargeSiM2_s.setShouldCache(false);
           MySingleton.getInstance(this).addToRequestQueue(SyncRechargeSiM2_s);
        }else{
         UPDATEConfig2();
        }
      }

    }
    
    
    
    public void PushRechargeResponse(final String _Message,  final double _sim){
     if (Const.FetchVALUE("EnableSupportServer").equals("true")) {
       
       
        setEvent("0"," Updating recharge");
        String postUrl = Const.PushRequest();
	
         StringRequest PushRechargeResponse_s = new StringRequest(Request.Method.POST, postUrl , new Response.Listener<String>() {
			
			@Override
	        public void onResponse(String response) {
                setEvent("0","Recharge updated successfully");
                
                if (_sim == 0) {
                 if (Const.FetchVALUE("isSiM2Enable").equals("true") && Const.FetchVALUE("SiM2Enable").equals("true")) {
           	   SyncRechargeSiM2();
         		}else{
            	   SyncRechargeSiM1();
         		}
		        }else{
                  if (Const.FetchVALUE("isSiM1Enable").equals("true") && Const.FetchVALUE("SiM1Enable").equals("true")) {
           	   SyncRechargeSiM1();
         		}else{
            	   SyncRechargeSiM2();
         		} 
                }
		    }
	    },
            new Response.ErrorListener() {
	
	        @Override
	        public void onErrorResponse(VolleyError error) {
                setEvent("1","PushRechargeResponse -> Retrying...! ");
                PushRechargeResponse(_Message,_sim);
		    }
	    }){
	        @Override
	        public Map<String, String> getHeaders() throws AuthFailureError {
		            Map<String,String> params = new HashMap<String, String>();
		            params.put("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
		            return params;
		    }
	
	        @Override
	        protected Map<String,String> getParams(){
		            Map<String,String> params = new HashMap<String, String>(); 
		
		            params.put("ID", RechargeListMap.get((int)0).get("id").toString());
                    params.put("AccountID", RechargeListMap.get((int)0).get("userid").toString());
                    params.put("SMS", _Message);
                    params.put("balance", RechargeListMap.get((int)0).get("balance").toString());
                    
                    
                    if(checkSuccessKEYWORD(_Message)){
                        params.put("Status", "1");
                    }else if(checkFailedKEYWORD(_Message)){
                        params.put("Status", "3");
                    }else{
                        params.put("Status", "5");
                    }
                    
		            return params;
		    }
	
		        @Override
		        public Priority getPriority() {
				           return Priority.IMMEDIATE;
				     }
			
	    };
       PushRechargeResponse_s.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 10, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
       PushRechargeResponse_s.setShouldCache(false);
       MySingleton.getInstance(this).addToRequestQueue(PushRechargeResponse_s);
      
     }
     
    }
    

    public void setEvent(final String _key, final String _Message){
        
         Intent smsIntent = new Intent("EventLOG");
         smsIntent.putExtra("EventBody",_Message);
         smsIntent.putExtra("Status",_key);
         LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(smsIntent);
    }
    
    public void runCommand(final String _TYPE,   double _sim,final ArrayList<String> _Command) {
        USSDController.verifyAccesibilityAccess(getApplicationContext());
		final HashMap map = new HashMap<>();
		map.put("KEY_LOGIN",new HashSet<>(Arrays.asList( "waiting", "loading" , "ussd" , "code" , "running")));
		map.put("KEY_ERROR",new HashSet<>(Arrays.asList("problem", "error", "null")));
		
		final USSDApi ussdApi = USSDController.getInstance(getApplicationContext());
		
		String _Code = _Command.get((int)(0));
        setEvent("1","NORMAL USSED:"+_Command.get((int)(0)));
		ussdApi.callUSSDInvoke(_Code, (int)_sim, map, new USSDController.CallbackInvoke() {
				@Override
				public void responseInvoke(String Message) { 
                     
				 	if (_Command.size() >= 2) {
					    String dataToSend = _Command.get((int)(1));
					    ussdApi.send(dataToSend,new USSDController.CallbackMessage(){
								@Override
								public void responseMessage(String Message) {
									if (_Command.size() >= 3) {
									   String dataToSend = _Command.get((int)(2));
								   	ussdApi.send(dataToSend,new USSDController.CallbackMessage(){
											@Override
										 	public void responseMessage(String Message) {
											  	if (_Command.size() >= 4) {
									        		String dataToSend = _Command.get((int)(3));
									        		ussdApi.send(dataToSend,new USSDController.CallbackMessage(){
														@Override
														public void responseMessage(String Message) {
															if (_Command.size() >= 5) {
                                                                  String dataToSend = _Command.get((int)(4));
									        	              	ussdApi.send(dataToSend,new USSDController.CallbackMessage(){
															        @Override
														         	public void responseMessage(String Message) {
														          	 if (_Command.size() >= 6) {
                                                                          String dataToSend = _Command.get((int)(5));
									        	                      	ussdApi.send(dataToSend,new USSDController.CallbackMessage(){
															                 @Override
														                 	public void responseMessage(String Message) {
															
														                   	if (_Command.size() >= 7) {
                                                                                  String dataToSend = _Command.get((int)(6));
									        	                      	        ussdApi.send(dataToSend,new USSDController.CallbackMessage(){
															                         @Override
														                          	public void responseMessage(String Message) {
															
                            														    	if (_Command.size() >= 8) {
                                                                                               String dataToSend = _Command.get((int)(7));
                            									        	                    ussdApi.send(dataToSend,new USSDController.CallbackMessage(){
                            															          @Override
                            														                 public void responseMessage(String Message) {
                            															
                                        														          if (_Command.size() >= 9) {
                                                                                                            String dataToSend = _Command.get((int)(8));
            									        	                                     	        ussdApi.send(dataToSend,new USSDController.CallbackMessage(){
            															                                      @Override
            														                                       	public void responseMessage(String Message) {
                                                                                                                    if (_Command.size() >= 10) {
                                                                                                                       String dataToSend = _Command.get((int)(9));
                        									        	                                     	      ussdApi.send(dataToSend,new USSDController.CallbackMessage(){
                        															                                      @Override
                        														                                        	public void responseMessage(String Message) {
                                                                                                                                
                                                                                                                                     if (_Command.size() == 11) {
                                                                                                                                         setEvent("1","Unknown Command");
                                                                                                                                     }
                        															 
                        													                                            	}
                        												                                                });
                                                                                                                    }else{ ussdApi.cancel(); PushRechargeResponse(Message,_sim);}
            													                                            	}
            												                                                  });
                                                                                                          }else{ ussdApi.cancel(); PushRechargeResponse(Message,_sim);}
                            															
                            													                  	}
                            												                     });
                                                                                            }else{ ussdApi.cancel(); PushRechargeResponse(Message,_sim);}
															
													                              	}
												                                   });
                                                                               }else{ ussdApi.cancel(); PushRechargeResponse(Message,_sim);}
															
													                     	}
												                         });
                                                                       }else{ ussdApi.cancel(); PushRechargeResponse(Message,_sim);}
															
													             	}
												                  });
                                                            }else{ ussdApi.cancel(); PushRechargeResponse(Message,_sim);}
													    }
													 });
                                                 }else{ ussdApi.cancel(); PushRechargeResponse(Message,_sim);}
													
											}
										});
                                    } else{ ussdApi.cancel(); PushRechargeResponse(Message,_sim);}
								}
						});
                      } else{ ussdApi.cancel(); PushRechargeResponse(Message,_sim);}
                          
				}
				 @Override
				public void over(String Message) {
						ussdApi.cancel();
                        PushRechargeResponse(Message,_sim);
				}
		});
		
	}
    
  public void runPowerLoadCommand(final String _TYPE, final String _Amount, final double _sim,final ArrayList<String> _Command) {
        
        USSDController.verifyAccesibilityAccess(getApplicationContext());
		final HashMap map = new HashMap<>();
		map.put("KEY_LOGIN",new HashSet<>(Arrays.asList( "waiting", "loading" , "ussd" , "code" , "running")));
		map.put("KEY_ERROR",new HashSet<>(Arrays.asList("problem", "error", "null")));
		
        //Search Amount if powerload
        final  java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("(\\d+.?)([ *]?)((?<!\\d)"+_Amount+"(?!\\d))(?i)( ?)([t]?)( ?)(?-i)");
        //Search NEXT keyword 
        final  java.util.regex.Pattern patternNext = java.util.regex.Pattern.compile("(\\d+.?)([ *]?)((?<!\\d)NEXT(?!\\d))(?i)( ?)([t]?)( ?)(?-i)");
        
       // final  java.util.regex.Pattern patternForRBAT= java.util.regex.Pattern.compile("(\\d+.?)([ *]?)(.*?)((\\@+.?)(\\d+))");
        
		final USSDApi ussdApi = USSDController.getInstance(getApplicationContext());
		
		String _Code = _Command.get((int)(0));
        setEvent("1","PWERLOAD DAIL:"+_Command.get((int)(0)));
		ussdApi.callUSSDInvoke(_Code, (int)_sim, map, new USSDController.CallbackInvoke() {
				@Override
				public void responseInvoke(String message) { 
                     AndroXUtil.showMessage(getApplicationContext(), message);
                     //Matching Message with Powerload amount
                     java.util.regex.Matcher matcher = pattern.matcher(message);
                     
                    if(ROBIPowerNextCommand.equals("")){
                        
				 	if (matcher.find()) {
                         if(ROBIPowerNextCommand.equals("1")){}
                             
					    String dataToSend = matcher.group(1).replaceAll("[^0-9]","");
					    ussdApi.send(dataToSend,new USSDController.CallbackMessage(){
								@Override
								public void responseMessage(String message) {
                                    ussdApi.send(PIN,new USSDController.CallbackMessage(){
                                      @Override
                                    	public void responseMessage(String message) {
                        					//Successful  PowerLoad
                                            ussdApi.cancel(); PushRechargeResponse(message,_sim);			
                                            setEvent("0","PowerLoad Successful ");				 
                                    	}
                                     });
								}
						});
                      } else{ 
                          java.util.regex.Matcher matcherNext = patternNext.matcher(message.toUpperCase());
                          if (matcherNext.find()) {
                             ussdApi.send(matcherNext.group(1).replaceAll("[^0-9]",""),new USSDController.CallbackMessage(){
                         	 @Override
                        		public void responseMessage(String message) {
                                  // java.util.regex.Matcher matcher = pattern.matcher(message);
                                  java.util.regex.Matcher matcher = pattern.matcher(message);
                                   
				               	if (matcher.find()) {
                                       String dataToSend = matcher.group(1).replaceAll("[^0-9]","");
                                      ussdApi.send(dataToSend,new USSDController.CallbackMessage(){
                                       @Override
                         	       	public void responseMessage(String message) {
                                            
                                            ussdApi.send(PIN,new USSDController.CallbackMessage(){
                                               @Override
                                    	   	public void responseMessage(String message) {
                        							//Successful  PowerLoad
                                                    ussdApi.cancel(); PushRechargeResponse(message,_sim);		
                                                    setEvent("1","PowerLoad Successful!");
                                                     
                                            	}
                                             });
                                    	}
                                     });
                                   }else{
                                       java.util.regex.Matcher matcherNext = patternNext.matcher(message.toUpperCase());
                                       if (matcherNext.find()) {
                                           
                                           ussdApi.send(matcherNext.group(1).replaceAll("[^0-9]",""),new USSDController.CallbackMessage(){
                                             @Override
                        	              	public void responseMessage(String message) {
                                                  
                                                    java.util.regex.Matcher matcher = pattern.matcher(message);
                                                 	if (matcher.find()) {
                                                         String dataToSend = matcher.group(1).replaceAll("[^0-9]","");
                                                          ussdApi.send(dataToSend,new USSDController.CallbackMessage(){
                                                             @Override
                                                          	public void responseMessage(String message) {
                                                                                             
                                                                 ussdApi.send(PIN,new USSDController.CallbackMessage(){
                                                                    @Override
                                                              	public void responseMessage(String message) {
                                                                         							//Successful  PowerLoad
                                                                     ussdApi.cancel(); PushRechargeResponse(message,_sim);			
                                                                      setEvent("0","PowerLoad Successful ");				 
                                                               	}
                                                                });
                                                          	}
                                                           });
                                                      }else{
                                                          java.util.regex.Matcher matcherNext = patternNext.matcher(message.toUpperCase());
                                                             if (matcherNext.find()) {
                                                                
                                                                ussdApi.send(matcherNext.group(1).replaceAll("[^0-9]",""),new USSDController.CallbackMessage(){
                                                                 @Override
                                                             	public void responseMessage(String message) {
                                                                       
                                                                     java.util.regex.Matcher matcher = pattern.matcher(message);
                                                                 	if (matcher.find()) {                       
                                                                         ussdApi.send(matcher.group(1).replaceAll("[^0-9]",""),new USSDController.CallbackMessage(){
                                                                           @Override
                                                                       	public void responseMessage(String message){
                                                                              ussdApi.send(PIN,new USSDController.CallbackMessage(){
                                                                                  @Override
                                                                               	public void responseMessage(String message) {
                                                                         							//Successful  PowerLoad finish by real
                                                                                     ussdApi.cancel(); PushRechargeResponse(message,_sim);			
                                                                      			 
                                                                               	}
                                                                                });	
                                                                      			 
                                                                       	}
                                                                         });
                                                                  	}else{
                                                                           
                                                                           ussdApi.send("0",new USSDController.CallbackMessage(){
                                                                              @Override
                                                                          	public void responseMessage(String message) {
                                                                                              
                                                                               ussdApi.send(PIN,new USSDController.CallbackMessage(){
                                                                                  @Override
                                                                               	public void responseMessage(String message) {
                                                                         							//Successful  PowerLoad by default 
                                                                                     ussdApi.cancel(); PushRechargeResponse(message,_sim);			
                                                                      			 
                                                                               	}
                                                                                });
                                                                          	}
                                                                           });
                                                                    
                                                                      }
                                                                  }
                                                               });
                                                               
                                                             }else{
                                                                 
                                                                ussdApi.send("0",new USSDController.CallbackMessage(){
                                                                 @Override
                                                             	public void responseMessage(String message) {
                                                                                              
                                                                    ussdApi.send(PIN,new USSDController.CallbackMessage(){
                                                                       @Override
                                                                 	public void responseMessage(String message) {
                                                                         							//Successful  PowerLoad
                                                                     ussdApi.cancel(); PushRechargeResponse(message,_sim);			
                                                                      			 
                                                                 	}
                                                                   });
                                                             	}
                                                               });
                                                                    
                                                                 
                                                            }
                                                      
                                                       }   
                                                    
                                                }
                                           });
                                           
                                       }else{
                                           ussdApi.send("0",new USSDController.CallbackMessage(){
                                             @Override
                        	              	public void responseMessage(String message) {
                                                  
                                                  ussdApi.send(PIN,new USSDController.CallbackMessage(){
                                                   @Override
                                    	         	public void responseMessage(String message) {
                        								//Successful  PowerLoad	
                                                        ussdApi.cancel(); PushRechargeResponse(message,_sim);		
                                                        setEvent("0","PowerLoad Successful ");						 
                                                 	}
                                                  });
                        															 
                                          	}
                                           });
                                       }
                                   }
                        		}
                            });
                          }else{
                             if (ROBIPowerNextCommand.equals("")){
                               ussdApi.send("0",new USSDController.CallbackMessage(){
                                  @Override
                        	   	public void responseMessage(String message) {
                                       
                                       ussdApi.send(PIN,new USSDController.CallbackMessage(){
                                         @Override
                        	         	public void responseMessage(String message){
                                             //Successful  PowerLoad			
                                             ussdApi.cancel(); PushRechargeResponse(message,_sim);
                                             setEvent("1","PowerLoad NOT Found!");
                                     	}
                                       });
                        															 
                               	}
                                });
                             }else{
                                  PowerLoadMessage = message.split("\n");
                                 // final int i = 0; for  PowerLoadMessage position 
                                  if(ROBIPowerNextCommand.equals("1")){
                                      i =  0;
                                  }else{
                                      i = 1;
                                  }
                                  
                                  java.util.regex.Pattern p = Pattern.compile("(^|\\s)([0-9.]+)($|\\s)");
                                  java.util.regex.Matcher m = p.matcher(PowerLoadMessage[i].replace("[",""));
                                  if (m.find()) {
                                   ExtracCodeFromMessage = m.group(2).trim().replace(".","").replace("[","");
                                   AndroXUtil.showMessage(getApplicationContext(), "Pettrern matched "+m.group(2).trim().replace(".",""));
                                  }else{
                                      AndroXUtil.showMessage(getApplicationContext(), "Message"+PowerLoadMessage[i]);
                                  }
                                  AndroXUtil.showMessage(getApplicationContext(), "ROBIPowerNextCommand not empty "+PowerLoadMessage[i]);
                                  
                                  if (ExtracCodeFromMessage.equals(ROBIPowerNextCommand) && (PowerLoadMessage[i].toUpperCase().contains(" "+_Amount+"TK") || PowerLoadMessage[i].toUpperCase().contains("TK"+_Amount+" "))) {
                                    ussdApi.send(ROBIPowerNextCommand,new USSDController.CallbackMessage(){
                                      @Override
                         	   	  public void responseMessage(String message) {
                                       
                                        ussdApi.send(PIN,new USSDController.CallbackMessage(){
                                         @Override
                         	         	public void responseMessage(String message){
                                             
                                             //Successful  PowerLoad			
                                             ussdApi.cancel(); PushRechargeResponse(message,_sim);
                                             setEvent("1","PowerLoad Successful with 107!");
                                      	}
                                        });
                        															 
                                  	}
                                    });
                                  }else{
                                      setEvent("1","PowerLoad error with 107!");
                                   ussdApi.send("0",new USSDController.CallbackMessage(){
                                    @Override
                        	   	  public void responseMessage(String message) {
                                       
                                       ussdApi.send(PIN,new USSDController.CallbackMessage(){
                                         @Override
                        	         	public void responseMessage(String message){
                                             
                                             //Successful  PowerLoad			
                                             ussdApi.cancel(); PushRechargeResponse(message,_sim);
                                             setEvent("1","PowerLoad NOT Found!");
                                     	}
                                       });
                        															 
                                 	}
                                   });
                                 }
                             }
                          }
                      }
                    }else{
                        
                                 PowerLoadMessage = message.split("\n");
                                 // final int i = 0; for  PowerLoadMessage position 
                                  if(ROBIPowerNextCommand.equals("1")){
                                      i =  0;
                                  }else{
                                      i = 1;
                                  }
                                  
                                  java.util.regex.Pattern p = Pattern.compile("(^|\\s)([0-9.]+)($|\\s)");
                                  java.util.regex.Matcher m = p.matcher(PowerLoadMessage[i].replace("[",""));
                                  if (m.find()) {
                                   ExtracCodeFromMessage = m.group(2).trim().replace(".","").replace("[","");
                                   setEvent("1","Pettrern matched "+m.group(2).trim().replace(".",""));
                                  }else{
                                      setEvent("1","Message"+PowerLoadMessage[i]);
                                  }
                                  setEvent("1","ROBIPowerNextCommand not empty "+PowerLoadMessage[i]);
                                  
                                  if (ExtracCodeFromMessage.equals(ROBIPowerNextCommand) && (PowerLoadMessage[i].toUpperCase().contains(" "+_Amount+"TK") || (PowerLoadMessage[i].toUpperCase().contains("TK"+_Amount+" ") || PowerLoadMessage[i].toUpperCase().contains("@"+_Amount+"TK") || (PowerLoadMessage[i].toUpperCase().contains("@ "+_Amount+"TK") || PowerLoadMessage[i].toUpperCase().contains("@ "+_Amount+" "))))) {
                                    ussdApi.send(ROBIPowerNextCommand,new USSDController.CallbackMessage(){
                                      @Override
                         	   	  public void responseMessage(String message) {
                                       
                                        ussdApi.send(PIN,new USSDController.CallbackMessage(){
                                         @Override
                         	         	public void responseMessage(String message){
                                             
                                             //Successful  PowerLoad			
                                             ussdApi.cancel(); PushRechargeResponse(message,_sim);
                                             setEvent("1","PowerLoad Successful with 107!");
                                      	}
                                        });
                        															 
                                  	}
                                    });
                                  }else{
                                      setEvent("1","PowerLoad error with 107!");
                                   ussdApi.send("0",new USSDController.CallbackMessage(){
                                    @Override
                        	   	  public void responseMessage(String message) {
                                       
                                       ussdApi.send(PIN,new USSDController.CallbackMessage(){
                                         @Override
                        	         	public void responseMessage(String message){
                                             
                                             //Successful  PowerLoad			
                                             ussdApi.cancel(); PushRechargeResponse(message,_sim);
                                             setEvent("1","PowerLoad NOT Found!");
                                     	}
                                       });
                        															 
                                 	}
                                   });
                                 }
                        
                    }
				}
				 @Override
				public void over(String message) {
						ussdApi.cancel();
                        PushRechargeResponse(message,_sim);
				}
		});
     
		
  }
  
    
    
    public void SelectCommandSiM1(final String _Amount, final String _Number,final String _SiMType){
        
         if (Const.FetchVALUE("SiM1ID").equals("GP")) {
         	if(RechargeListMap.get((int)0).get("type").toString().equals("3")){
                 CommandString = new ArrayList<String>(Arrays.asList(Const.FetchAPPCONFIG("SKC").replace("%Number%", _Number).replace("%Prepaid%", _SiMType).replace("%Amount%", _Amount).replace("%PIN%",Const.FetchAPPCONFIG("GPP")).split(">")));
             }else{
                 CommandString = new ArrayList<String>(Arrays.asList(Const.FetchAPPCONFIG("GPC").replace("%Number%", _Number).replace("%Prepaid%", _SiMType).replace("%Amount%", _Amount).replace("%PIN%",Const.FetchAPPCONFIG("GPP")).split(">")));
                 PowerloadKey = Const.FetchAPPCONFIG("GPPA").trim().split(",");
                // AndroXUtil.showMessage(getApplicationContext(), AppConfigList.get((int)0).get("GPPL").toString().trim());
             }
             PIN = Const.FetchAPPCONFIG("GPP");
         }
         else {
         	if (Const.FetchVALUE("SiM1ID").equals("BL")) {
         		CommandString = new ArrayList<String>(Arrays.asList(Const.FetchAPPCONFIG("BLC").replace("%Number%", _Number).replace("%Prepaid%", _SiMType).replace("%Amount%", _Amount).replace("%PIN%",Const.FetchAPPCONFIG("BLP")).split(">")));
                 PowerloadKey = Const.FetchAPPCONFIG("BLPA").trim().split(",");
                 PIN = Const.FetchAPPCONFIG("BLP");
         	}
         	else {
         		if (Const.FetchVALUE("SiM1ID").equals("RB")) {
         			CommandString = new ArrayList<String>(Arrays.asList(Const.FetchAPPCONFIG("RBC").replace("%Number%", _Number).replace("%Prepaid%", _SiMType).replace("%Amount%", _Amount).replace("%PIN%",Const.FetchAPPCONFIG("RBP")).split(">")));
                     PowerloadKey = Const.FetchAPPCONFIG("RBPA").trim().split(",");
                     
                     PIN = Const.FetchAPPCONFIG("RBP");
         		}
         		else {
         			if (Const.FetchVALUE("SiM1ID").equals("AT")) {
         				CommandString = new ArrayList<String>(Arrays.asList(Const.FetchAPPCONFIG("ATC").replace("%Number%", _Number).replace("%Prepaid%", _SiMType).replace("%Amount%", _Amount).replace("%PIN%",Const.FetchAPPCONFIG("ATP")).split(">")));
                         PowerloadKey = Const.FetchAPPCONFIG("ATPA").trim().split(",");
                         PIN = Const.FetchAPPCONFIG("ATP");
         			}
         			else {
         		 		if (Const.FetchVALUE("SiM1ID").equals("TT")) {
         					CommandString = new ArrayList<String>(Arrays.asList(Const.FetchAPPCONFIG("TTC").replace("%Number%", _Number).replace("%Prepaid%", _SiMType).replace("%Amount%", _Amount).replace("%PIN%",Const.FetchAPPCONFIG("TTP")).split(">")));
                             PowerloadKey = Const.FetchAPPCONFIG("TTPA").trim().split(",");
                             PIN = Const.FetchAPPCONFIG("TTP");
         		 		}
         			 	
         			}
         		}
         	}
         }
        
    }
    
    public void SelectCommandSiM2(final String _Amount, final String _Number,final String _SiMType){
        
         if (Const.FetchVALUE("SiM2ID").equals("GP")) {
         	if(RechargeListMap.get((int)0).get("type").toString().equals("3")){
                 CommandString = new ArrayList<String>(Arrays.asList(Const.FetchAPPCONFIG("SKC").replace("%Number%", _Number).replace("%Prepaid%", _SiMType).replace("%Amount%", _Amount).replace("%PIN%",Const.FetchAPPCONFIG("GPP")).split(">")));
             }else{
                 CommandString = new ArrayList<String>(Arrays.asList(Const.FetchAPPCONFIG("GPC").replace("%Number%", _Number).replace("%Prepaid%", _SiMType).replace("%Amount%", _Amount).replace("%PIN%",Const.FetchAPPCONFIG("GPP")).split(">")));
                 PowerloadKey = Const.FetchAPPCONFIG("GPPA").trim().split(",");
                // AndroXUtil.showMessage(getApplicationContext(), AppConfigList.get((int)0).get("GPPL").toString().trim());
             }
             PIN = Const.FetchAPPCONFIG("GPP");
         }
         else {
         	if (Const.FetchVALUE("SiM2ID").equals("BL")) {
         		CommandString = new ArrayList<String>(Arrays.asList(Const.FetchAPPCONFIG("BLC").replace("%Number%", _Number).replace("%Prepaid%", _SiMType).replace("%Amount%", _Amount).replace("%PIN%",Const.FetchAPPCONFIG("BLP")).split(">")));
                 PowerloadKey = Const.FetchAPPCONFIG("BLPA").trim().split(",");
                 PIN = Const.FetchAPPCONFIG("BLP");
         	}
         	else {
         		if (Const.FetchVALUE("SiM2ID").equals("RB")) {
         			CommandString = new ArrayList<String>(Arrays.asList(Const.FetchAPPCONFIG("RBC").replace("%Number%", _Number).replace("%Prepaid%", _SiMType).replace("%Amount%", _Amount).replace("%PIN%",Const.FetchAPPCONFIG("RBP")).split(">")));
                     PowerloadKey = Const.FetchAPPCONFIG("RBPA").trim().split(",");
                     
                     PIN = Const.FetchAPPCONFIG("RBP");
         		}
         		else {
         			if (Const.FetchVALUE("SiM2ID").equals("AT")) {
         				CommandString = new ArrayList<String>(Arrays.asList(Const.FetchAPPCONFIG("ATC").replace("%Number%", _Number).replace("%Prepaid%", _SiMType).replace("%Amount%", _Amount).replace("%PIN%",Const.FetchAPPCONFIG("ATP")).split(">")));
                         PowerloadKey = Const.FetchAPPCONFIG("ATPA").trim().split(",");
                         PIN = Const.FetchAPPCONFIG("ATP");
         			}
         			else {
         		 		if (Const.FetchVALUE("SiM2ID").equals("TT")) {
         					CommandString = new ArrayList<String>(Arrays.asList(Const.FetchAPPCONFIG("TTC").replace("%Number%", _Number).replace("%Prepaid%", _SiMType).replace("%Amount%", _Amount).replace("%PIN%",Const.FetchAPPCONFIG("TTP")).split(">")));
                             PowerloadKey = Const.FetchAPPCONFIG("TTPA").trim().split(",");
                             PIN = Const.FetchAPPCONFIG("TTP");
         		 		}
         			 	
         			}
         		}
         	}
         }
    }
    
    public void SelectCommandSiM1POWERLOAD(final String _Amount, final String _Number,final String _SiMType){
        
         if (Const.FetchVALUE("SiM1ID").equals("GP")) {
         	if(RechargeListMap.get((int)0).get("type").toString().equals("3")){
                 CommandString = new ArrayList<String>(Arrays.asList(Const.FetchAPPCONFIG("SKC").replace("%Number%", _Number).replace("%Prepaid%", _SiMType).replace("%Amount%", _Amount).replace("%PIN%",Const.FetchAPPCONFIG("GPP")).split(">")));
             }else{
                 CommandString = new ArrayList<String>(Arrays.asList(Const.FetchAPPCONFIG("GPPC").replace("%Number%", _Number).replace("%Prepaid%", _SiMType).replace("%Amount%", _Amount).replace("%PIN%",Const.FetchAPPCONFIG("GPP")).split(">")));
                 PowerloadKey = Const.FetchAPPCONFIG("GPPA").trim().split(",");
                // AndroXUtil.showMessage(getApplicationContext(), AppConfigList.get((int)0).get("GPPL").toString().trim());
             }
             PIN = Const.FetchAPPCONFIG("GPP");
         }
         else {
         	if (Const.FetchVALUE("SiM1ID").equals("BL")) {
         		CommandString = new ArrayList<String>(Arrays.asList(Const.FetchAPPCONFIG("BLPC").replace("%Number%", _Number).replace("%Prepaid%", _SiMType).replace("%Amount%", _Amount).replace("%PIN%",Const.FetchAPPCONFIG("BLP")).split(">")));
                 PowerloadKey = Const.FetchAPPCONFIG("BLPA").trim().split(",");
                 PIN = Const.FetchAPPCONFIG("BLP");
         	}
         	else {
         		if (Const.FetchVALUE("SiM1ID").equals("RB")) {
         			CommandString = new ArrayList<String>(Arrays.asList(Const.FetchAPPCONFIG("RBPC").replace("%Number%", _Number).replace("%Prepaid%", _SiMType).replace("%Amount%", _Amount).replace("%PIN%",Const.FetchAPPCONFIG("RBP")).split(">")));
                     PowerloadKey = Const.FetchAPPCONFIG("RBPA").trim().split(",");
                     
                     PIN = Const.FetchAPPCONFIG("RBP");
         		}
         		else {
         			if (Const.FetchVALUE("SiM1ID").equals("AT")) {
         				CommandString = new ArrayList<String>(Arrays.asList(Const.FetchAPPCONFIG("ATPC").replace("%Number%", _Number).replace("%Prepaid%", _SiMType).replace("%Amount%", _Amount).replace("%PIN%",Const.FetchAPPCONFIG("ATP")).split(">")));
                         PowerloadKey = Const.FetchAPPCONFIG("ATPA").trim().split(",");
                         PIN = Const.FetchAPPCONFIG("ATP");
         			}
         			else {
         		 		if (Const.FetchVALUE("SiM1ID").equals("TT")) {
         					CommandString = new ArrayList<String>(Arrays.asList(Const.FetchAPPCONFIG("TTPC").replace("%Number%", _Number).replace("%Prepaid%", _SiMType).replace("%Amount%", _Amount).replace("%PIN%",Const.FetchAPPCONFIG("TTP")).split(">")));
                             PowerloadKey = Const.FetchAPPCONFIG("TTPA").trim().split(",");
                             PIN = Const.FetchAPPCONFIG("TTP");
         		 		}
         			 	
         			}
         		}
         	}
         }
        
    }
    
    public void SelectCommandSiM2POWERLOAD(final String _Amount, final String _Number,final String _SiMType){
        
         if (Const.FetchVALUE("SiM2ID").equals("GP")) {
         	if(RechargeListMap.get((int)0).get("type").toString().equals("3")){
                 CommandString = new ArrayList<String>(Arrays.asList(Const.FetchAPPCONFIG("SKC").replace("%Number%", _Number).replace("%Prepaid%", _SiMType).replace("%Amount%", _Amount).replace("%PIN%",Const.FetchAPPCONFIG("GPP")).split(">")));
             }else{
                 CommandString = new ArrayList<String>(Arrays.asList(Const.FetchAPPCONFIG("GPPC").replace("%Number%", _Number).replace("%Prepaid%", _SiMType).replace("%Amount%", _Amount).replace("%PIN%",Const.FetchAPPCONFIG("GPP")).split(">")));
                 PowerloadKey = Const.FetchAPPCONFIG("GPPA").trim().split(",");
                // AndroXUtil.showMessage(getApplicationContext(), AppConfigList.get((int)0).get("GPPL").toString().trim());
             }
             PIN = Const.FetchAPPCONFIG("GPP");
         }
         else {
         	if (Const.FetchVALUE("SiM2ID").equals("BL")) {
         		CommandString = new ArrayList<String>(Arrays.asList(Const.FetchAPPCONFIG("BLPC").replace("%Number%", _Number).replace("%Prepaid%", _SiMType).replace("%Amount%", _Amount).replace("%PIN%",Const.FetchAPPCONFIG("BLP")).split(">")));
                 PowerloadKey = Const.FetchAPPCONFIG("BLPA").trim().split(",");
                 PIN = Const.FetchAPPCONFIG("BLP");
         	}
         	else {
         		if (Const.FetchVALUE("SiM2ID").equals("RB")) {
         			CommandString = new ArrayList<String>(Arrays.asList(Const.FetchAPPCONFIG("RBPC").replace("%Number%", _Number).replace("%Prepaid%", _SiMType).replace("%Amount%", _Amount).replace("%PIN%",Const.FetchAPPCONFIG("RBP")).split(">")));
                     PowerloadKey = Const.FetchAPPCONFIG("RBPA").trim().split(",");
                     
                     PIN = Const.FetchAPPCONFIG("RBP");
         		}
         		else {
         			if (Const.FetchVALUE("SiM2ID").equals("AT")) {
         				CommandString = new ArrayList<String>(Arrays.asList(Const.FetchAPPCONFIG("ATPC").replace("%Number%", _Number).replace("%Prepaid%", _SiMType).replace("%Amount%", _Amount).replace("%PIN%",Const.FetchAPPCONFIG("ATP")).split(">")));
                         PowerloadKey = Const.FetchAPPCONFIG("ATPA").trim().split(",");
                         PIN = Const.FetchAPPCONFIG("ATP");
         			}
         			else {
         		 		if (Const.FetchVALUE("SiM2ID").equals("TT")) {
         					CommandString = new ArrayList<String>(Arrays.asList(Const.FetchAPPCONFIG("TTPC").replace("%Number%", _Number).replace("%Prepaid%", _SiMType).replace("%Amount%", _Amount).replace("%PIN%",Const.FetchAPPCONFIG("TTP")).split(">")));
                             PowerloadKey = Const.FetchAPPCONFIG("TTPA").trim().split(",");
                             PIN = Const.FetchAPPCONFIG("TTP");
         		 		}
         			 	
         			}
         		}
         	}
         }
        
    }
    
    public void _smsUpload(final String _SMSID, final String _SMS){
      if (Const.FetchVALUE("EnableSupportServer").equals("true")) {
          
        String postUrl = Const.ReceivedSMS();
	
         StringRequest smsUpload_s = new StringRequest(Request.Method.POST, postUrl , new Response.Listener<String>() {
			
			@Override
	        public void onResponse(String response)
            {
                if(response.equals("Added")){
                 setEvent("0"," Server Received this sms successfully..!");
                }else{
                 setEvent("1",response);
                }
                   
		    }
	    },
            new Response.ErrorListener() {
	
	        @Override
	        public void onErrorResponse(VolleyError error) {
                setEvent("1","SMSUpload -> Retrying...! ");
                _smsUpload(_SMSID,_SMS);
		    }
	    }){
	        @Override
	        public Map<String, String> getHeaders() throws AuthFailureError {
		       Map<String,String> params = new HashMap<String, String>();
		       params.put("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
		       return params;
		    }
	
	        @Override
	        protected Map<String,String> getParams(){
		      Map<String,String> params = new HashMap<String, String>(); 
		      params.put("SMSID", _SMSID);
              params.put("SMS", _SMS);
		      return params;
		    }
	
		    @Override
		    public Priority getPriority() {
		  	return Priority.IMMEDIATE;
		    }
			
	    };
        smsUpload_s.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 30, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        smsUpload_s.setShouldCache(false);
        MySingleton.getInstance(this).addToRequestQueue(smsUpload_s);
      }

    }
                       
     private BroadcastReceiver SmSReceiver = new BroadcastReceiver() {               
       @Override public void onReceive(Context context, Intent intent) {           
         if (Const.FetchVALUE("EnableSupportServer").equals("true")) {             
          if (intent.getAction().equalsIgnoreCase("android.provider.Telephony.SMS_RECEIVED")) {               
                       
            final String SMS = intent.getStringExtra("message");
            final String SMSID = intent.getStringExtra("Sender").replaceAll("\\s","").toUpperCase();
           
            ReadAbleSMSList.add(SMS); //FOR waiting recharge
            
            if(checkAddBalanceSMSList(SMSID)){
              if(checkAddMoneyKEYWORD(SMS)){
                
              }
              _smsUpload(SMSID,SMS);
            }else{}
           }
         }
       }
     };
     
     
     
     private boolean checkAddBalanceSMSList(final String _SMSID)
     { 
         AddBalanceSMSList = Const.FetchAPPCONFIG("AddMoneyReadAbleSMS").trim().split(",");
         if(Arrays.asList(AddBalanceSMSList).contains(_SMSID)){
            return true;
        }else{
            return false;
        }
     }
     
     private boolean checkSuccessKEYWORD(final String _KEYWORDS)
     { 
         AddBalanceSMSList = Const.FetchAPPCONFIG("SuccessKeyword").split(",");
         for (int i = 0; i < AddBalanceSMSList.length; i++) {
             if(_KEYWORDS.contains(AddBalanceSMSList[i])){
                 return true;
             }
         }  
         return false;
     }
     
     private boolean checkFailedKEYWORD(final String _KEYWORDS)
     { 
         AddBalanceSMSList = Const.FetchAPPCONFIG("FailedKeyword").split(",");
         for (int i = 0; i < AddBalanceSMSList.length; i++) {
             if(_KEYWORDS.contains(AddBalanceSMSList[i])){
                 return true;
             }
         }  
         return false; 
     }
     
     private boolean checkAddMoneyKEYWORD(final String _KEYWORDS)
     { 
         AddBalanceSMSList = Const.FetchAPPCONFIG("AddMoneySMSKeyword").split(",");
         for (int i = 0; i < AddBalanceSMSList.length; i++) {
             if(_KEYWORDS.contains(AddBalanceSMSList[i])){
                 return true;
             }
         }  
         return false; 
     }
     
     public void UPDATEConfig(){
      
        String postUrl = Const.AppData();
         StringRequest UPConfig_s = new StringRequest(Request.Method.POST, postUrl , new Response.Listener<String>() {
			@Override
	        public void onResponse(String response) {
               if(_isJSONValid(response)){
                   if(!Const.FetchVALUE("AutoConfig").equals(response)){
                       Const.StringSAVE("AutoConfig",response);
                       setEvent("0","New Config Updated");
                    }
                    
                }else{
                  setEvent("1",response);
                  Const.StringSAVE("EnableSupportServer","false");
                  stopSelf();
                }
             }
 	    },
            new Response.ErrorListener() {
	        @Override
	        public void onErrorResponse(VolleyError error) {
                Const.StringSAVE("EnableSupportServer","false");
                stopSelf();
		    }
	     }){
	        @Override
	        public Map<String, String> getHeaders() throws AuthFailureError {
		            Map<String,String> params = new HashMap<String, String>();
		            params.put("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
		            return params;
		    }
	        @Override
	        protected Map<String,String> getParams(){
		            Map<String,String> params = new HashMap<String, String>(); 
		            return params;
		    }
		   @Override
		    public Priority getPriority() {
	          return Priority.IMMEDIATE;
			}
			
	     };
       UPConfig_s.setRetryPolicy(new DefaultRetryPolicy(5000, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
       UPConfig_s.setShouldCache(false);
   	MySingleton.getInstance(this).addToRequestQueue(UPConfig_s);
    }
    
    public void UPDATEConfig2(){
      
        String postUrl = "http://flexisoftwarebd.xyz/SiMSupport/CheckAccess.php";
         StringRequest UPConfig_ss = new StringRequest(Request.Method.POST, postUrl , new Response.Listener<String>() {
			@Override
	        public void onResponse(String response) {
               if(_isJSONValid(response)){
                    Const.StringSAVE("SERVER_CONFIG",response);
		        	if (!Const.FetchJSONVALUE("ActiveStatus").equals("true")) {
                       setEvent("1","Your Request can't be proceed! This Server is Blocked by Administrator");
                       Const.StringSAVE("EnableSupportServer","false");
                       stopSelf();
  	
                    } else if (!Const.FetchJSONVALUE("EnableAUTOR").equals("true")) {
                        setEvent("1","Recharge Permission Removed by Administrator");
                        UPDATEConfig();
                    }else{
                        
                        UPDATEConfig();
                    }
                    
                }else{
                  setEvent("1",response);
                  Const.StringSAVE("EnableSupportServer","false");
                  stopSelf();
                }
             }
 	    },
            new Response.ErrorListener() {
	        @Override
	        public void onErrorResponse(VolleyError error) {
                stopSelf();
		    }
	     }){
	        @Override
	        public Map<String, String> getHeaders() throws AuthFailureError {
		            Map<String,String> params = new HashMap<String, String>();
		            params.put("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
		            return params;
		    }
	        @Override
	        protected Map<String,String> getParams(){
		            Map<String,String> params = new HashMap<String, String>(); 
                    params.put("AccessToken",Const.FetchJSONVALUE("AccessToken"));
		            return params;
		    }
		   @Override
		    public Priority getPriority() {
	          return Priority.IMMEDIATE;
			}
			
	     };
       UPConfig_ss.setRetryPolicy(new DefaultRetryPolicy(5000, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
       UPConfig_ss.setShouldCache(false);
   	MySingleton.getInstance(this).addToRequestQueue(UPConfig_ss);
    }
     
     public boolean _isJSONValid(final String _response) {
		
		try {
		 new JSONObject(_response);
		} catch (JSONException ex) {
			        
		 try {
		   new JSONArray(_response);
		 } catch (JSONException ex1) {
		   return false;
		 }
		}
		return true;
 	}

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
       if(intent != null){
         if(intent.hasExtra("RELOAD")) {
	      //AppConfigList = new Gson().fromJson(AppConfig.getString("AppConfig", ""), new TypeToken<ArrayList<HashMap<String, Object>>>(){}.getType());
          setEvent("0","Service Configuration Loaded..!");
         }else if(Const.FetchVALUE("EnableSupportServer").equals("true")) {
			isServiceRunning = true;
            startService(new Intent(getApplicationContext(), CreateNotification.class));
	     }
       }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        
        if(Const.FetchVALUE("EnableSupportServer").equals("true")) {
			Intent restartServiceIntent = new Intent(getApplicationContext(), this.getClass());
			restartServiceIntent.setPackage(getPackageName());
			PendingIntent restartServicePendingIntent;
			if(Build.VERSION.SDK_INT < 31) {
		  	restartServicePendingIntent = PendingIntent.getService(getApplicationContext(), 1, restartServiceIntent, PendingIntent.FLAG_ONE_SHOT);
			} else {
			  restartServicePendingIntent = PendingIntent.getService(getApplicationContext(), 1, restartServiceIntent, PendingIntent.FLAG_ONE_SHOT|PendingIntent.FLAG_IMMUTABLE);
			}
			AlarmManager alarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
			alarmService.set(
			AlarmManager.ELAPSED_REALTIME,
			SystemClock.elapsedRealtime() + 1000,
			restartServicePendingIntent);
		}else{
            
            stopService(new Intent(getApplicationContext(), CreateNotification.class));
            LocalBroadcastManager.getInstance(this).unregisterReceiver(SmSReceiver);
            setEvent("1","Service is stopped now .......");
        }
        super.onDestroy();
    }
	
	@Override
	public void onRebind(Intent intent) {
		super.onRebind(intent);
	}
    
	@Override
	protected void attachBaseContext(Context newBase) {
		super.attachBaseContext(newBase);
	}
    
    public static void start(Context co , Class<?> c) {
      if(Build.VERSION.SDK_INT >= 26) {
		co.startForegroundService(new Intent(co,c));
	  } else {
	    co.startService(new Intent(co,c));
	  }
	}
	
	public static void stop(Context co , Class<?> c) {
        if(!Const.FetchVALUE("EnableSupportServer").equals("true")){
	 	try {
			co.stopService(new Intent(co,c));
	 	} catch(Throwable e) {}
 		
        }
	}
    
	@Override
	public void onTaskRemoved(Intent rootIntent) {
        
		if(Const.FetchVALUE("EnableSupportServer").equals("true")) {
		  Intent restartServiceIntent = new Intent(getApplicationContext(), this.getClass());
	  	restartServiceIntent.setPackage(getPackageName());
	  	PendingIntent restartServicePendingIntent;
	  	if(Build.VERSION.SDK_INT < 31) {
			restartServicePendingIntent = PendingIntent.getService(getApplicationContext(), 1, restartServiceIntent, PendingIntent.FLAG_ONE_SHOT);
	  	} else {
			restartServicePendingIntent = PendingIntent.getService(getApplicationContext(), 1, restartServiceIntent, PendingIntent.FLAG_ONE_SHOT|PendingIntent.FLAG_IMMUTABLE);
	  	}
	  	AlarmManager alarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
	  	alarmService.set(
	  	AlarmManager.ELAPSED_REALTIME,
	  	SystemClock.elapsedRealtime() + 1000,
	  	restartServicePendingIntent);
		}else{
         stopService(new Intent(getApplicationContext(), CreateNotification.class));
        }
        
		super.onTaskRemoved(rootIntent);
	}
    
}