package com.techbdhost.support;

import android.animation.*;
import android.app.*;
import android.app.Activity;
import android.content.*;
import android.content.SharedPreferences;
import android.content.res.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.media.*;
import android.net.*;
import android.os.*;
import android.text.*;
import android.text.style.*;
import android.util.*;
import android.view.*;
import android.view.View.*;
import android.view.animation.*;
import android.webkit.*;
import android.widget.*;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import androidx.annotation.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.*;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import com.android.volley.*;
import com.github.ybq.android.spinkit.*;
import com.romellfudi.ussdlibrary.*;
import com.suke.widget.*;
import java.io.*;
import java.text.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.regex.*;
import org.json.*;
import android.provider.Settings.Secure;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.android.volley.toolbox.JsonObjectRequest;

import com.android.volley.toolbox.HurlStack;
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

import android.content.pm.PackageManager;
import androidx.core.content.ContextCompat;

public class HomeActivity extends AppCompatActivity {
	
	private  USSDApi ussdApi;
	private HashMap<String, Object> EventMap = new HashMap<>();
	private String fontName = "";
	private String typeace = "";
	
	private ArrayList<String> simList = new ArrayList<>();
	private ArrayList<HashMap<String, Object>> EventList = new ArrayList<>();
	
	private LinearLayout linear2;
	private ScrollView vscroll1;
	private LinearLayout linear65;
	private SwitchButton switch1;
	private TextView app_name1;
	private TextView app_name2;
	private LinearLayout linear6;
	private LinearLayout linear57;
	private LinearLayout linear58;
	private LinearLayout linear53;
	private TextView name;
	private TextView good_morning;
	private LinearLayout linear16;
	private LinearLayout linear59;
	private LinearLayout linear50;
	private Switch enable_sim1;
	private ImageView sim1_img;
	private LinearLayout linear55;
	private TextView sim1_name;
	private TextView textview44;
	private LinearLayout linear60;
	private Switch enable_sim2;
	private ImageView sim2_img;
	private LinearLayout linear61;
	private TextView sim2_name;
	private TextView textview49;
	private LinearLayout linear54;
	private RecyclerView recyclerview1;
	private TextView textview46;
	
	private SharedPreferences SaveUser;
	private Calendar EventCalander = Calendar.getInstance();
	
	@Override
	protected void onCreate(Bundle _savedInstanceState) {
		super.onCreate(_savedInstanceState);
		setContentView(R.layout.home);
		initialize(_savedInstanceState);
		initializeLogic();
	}
	
	private void initialize(Bundle _savedInstanceState) {
		linear2 = findViewById(R.id.linear2);
		vscroll1 = findViewById(R.id.vscroll1);
		linear65 = findViewById(R.id.linear65);
		switch1 = findViewById(R.id.switch1);
		app_name1 = findViewById(R.id.app_name1);
		app_name2 = findViewById(R.id.app_name2);
		linear6 = findViewById(R.id.linear6);
		linear57 = findViewById(R.id.linear57);
		linear58 = findViewById(R.id.linear58);
		linear53 = findViewById(R.id.linear53);
		name = findViewById(R.id.name);
		good_morning = findViewById(R.id.good_morning);
		linear16 = findViewById(R.id.linear16);
		linear59 = findViewById(R.id.linear59);
		linear50 = findViewById(R.id.linear50);
		enable_sim1 = findViewById(R.id.enable_sim1);
		sim1_img = findViewById(R.id.sim1_img);
		linear55 = findViewById(R.id.linear55);
		sim1_name = findViewById(R.id.sim1_name);
		textview44 = findViewById(R.id.textview44);
		linear60 = findViewById(R.id.linear60);
		enable_sim2 = findViewById(R.id.enable_sim2);
		sim2_img = findViewById(R.id.sim2_img);
		linear61 = findViewById(R.id.linear61);
		sim2_name = findViewById(R.id.sim2_name);
		textview49 = findViewById(R.id.textview49);
		linear54 = findViewById(R.id.linear54);
		recyclerview1 = findViewById(R.id.recyclerview1);
		textview46 = findViewById(R.id.textview46);
		SaveUser = getSharedPreferences("SaveUser", Activity.MODE_PRIVATE);
		
		enable_sim1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton _param1, boolean _param2) {
				final boolean _isChecked = _param2;
				if (_isChecked) {
					Const.StringSAVE("SiM1Enable","true");
				}
				else {
					Const.StringSAVE("SiM1Enable","false");
				}
			}
		});
		
		enable_sim2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton _param1, boolean _param2) {
				final boolean _isChecked = _param2;
				if (_isChecked) {
					Const.StringSAVE("SiM2Enable","true");
				}
				else {
					Const.StringSAVE("SiM2Enable","false");
				}
			}
		});
	}
	
	private void initializeLogic() {
		getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
		getWindow().setStatusBarColor(0xFFFFFFFF);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		_GetPhoneSiM();
		_setBackground(linear2, 0, 5, "#ffffff", false);
		_setBackground(linear16, 7, 2, "#b2dfdb", false);
		_setBackground(linear59, 7, 2, "#FFDBCF", false);
		String str = Const.FetchJSONVALUE("CompanyNAME");
		if(str.split("\\w+").length>1){
			 String lastName = str.substring(str.lastIndexOf(" ")+1);
			 String firstName = str.substring(0, str.lastIndexOf(' '));
			 name.setText("Hello "+firstName);
		}else{
			 name.setText("Hello "+str);
		}
		_SET_GOOD_MORING(good_morning);
		_CheckBroadCastPermission();
		if(isServiceRunning (this ,MyService.class)) {switch1.setChecked(true);}else{switch1.setChecked(false);}
		 switch1.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() { 
				@Override public void onCheckedChanged(SwitchButton view, boolean isChecked) { 
					  if (isChecked) {
					                      
					        if(Build.VERSION.SDK_INT >=  Build.VERSION_CODES.M) {
						                                                	
						          Intent intent = new Intent();
						      	String packageName = HomeActivity.this.getPackageName();
						      	PowerManager pm = (PowerManager) HomeActivity.this.getSystemService(Context.POWER_SERVICE);
						                                                	
						     	if (!pm.isIgnoringBatteryOptimizations(packageName)) {
							       	 intent.setAction(android.provider.Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
							        	intent.setData(Uri.parse("package:" + packageName));
							        	startActivity(intent);
							                                                		
							      } else {
							                                                		
							         	if(isServiceRunning (HomeActivity.this ,MyService.class)) {
								              stopService(new Intent(getApplicationContext(), MyService.class));
								         }
							         	else {
								              if(USSDController.verifyAccesibilityAccess(HomeActivity.this)){
									                 startService(new Intent(getApplicationContext(), MyService.class));
									               }else{setEvent("fail","AUTOController -> verifyAccesibilityAccess FAILED! Please give this permission");}
								         }
							      }
						        } else {
						                                                	
						     	if(isServiceRunning (HomeActivity.this ,MyService.class)) {
							       	stopService(new Intent(getApplicationContext(), MyService.class));
							     }
						         else {
							                                                		
							           if(USSDController.verifyAccesibilityAccess(HomeActivity.this)){
								             startService(new Intent(getApplicationContext(), MyService.class));
								           }else{setEvent("fail","AUTOController -> verifyAccesibilityAccess FAILED! Please give this permission");}
							     }
						        }
					        Const.StringSAVE("EnableSupportServer","true");
						  } else {
					                       
					        if(isServiceRunning (HomeActivity.this ,MyService.class)) {
						          stopService(new Intent(getApplicationContext(), MyService.class));
						        }
					        Const.StringSAVE("EnableSupportServer","false");
					  }
				}
			 });
		
		LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter("EventLOG"));
		 }
	 
	private BroadcastReceiver receiver = new BroadcastReceiver() {
		    @Override 
		      public void onReceive(Context context, Intent intent) {
			
			          if (intent.getAction().equals("EventLOG")) {
				              
				               final String EventBody = intent.getStringExtra("EventBody");
				               final String Status = intent.getStringExtra("Status");
				               
				               EventMap = new HashMap<>();
				               EventMap.put("EventLOG", new SimpleDateFormat("MMM dd, hh:mm:ss ").format(EventCalander.getTime()).concat(EventBody));
				               EventMap.put("Status", Status);
				               EventList.add(EventMap);
				               
				               EventCalander = Calendar.getInstance();
							   
							   
							   recyclerview1.setAdapter(new Recyclerview1Adapter(EventList));
				               recyclerview1.setHasFixedSize(false);
				               recyclerview1.setLayoutManager(new LinearLayoutManager(HomeActivity.this));
				               recyclerview1.smoothScrollToPosition((int)EventList.size() - 1);
				               
				          }
			     }
		 };
	{
	}
	
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
	}
	public void _SET_GOOD_MORING(final TextView _txt) {
		Calendar c = Calendar.getInstance();
		int timeOfDay = c.get(Calendar.HOUR_OF_DAY);
		
		if(timeOfDay >= 0 && timeOfDay < 12){
				
				
			           
				
				
				
					_txt.setText("Good Morning!");
				
			
		}else if(timeOfDay >= 12 && timeOfDay < 16){
				
				
			    _txt.setText("Good Afternoon!");
				
				
				
		}else if(timeOfDay >= 16 && timeOfDay < 21){
				
			    _txt.setText("Good Evening!");
				
			    
			    
				
		}else if(timeOfDay >= 21 && timeOfDay < 24){
				
			    _txt.setText("Good Night!");
				
			    
			    
		}
	}
	
	
	public void _SiMIndex(final ImageView _img, final String _MatchKey) {
		if (_MatchKey.equals("Robi")) {
			_img.setImageResource(R.drawable.ic_robi);
			if(getResources().getResourceEntryName(_img.getId()).equals("sim1_img")){
				 Const.StringSAVE("SiM1ID","RB");
			}else{
				 Const.StringSAVE("SiM2ID","RB");
			}
		}
		else {
			if (_MatchKey.equals("Airtel")) {
				_img.setImageResource(R.drawable.ic1_1);
				if(getResources().getResourceEntryName(_img.getId()).equals("sim1_img")){
					 Const.StringSAVE("SiM1ID","AT");
				}else{
					 Const.StringSAVE("SiM2ID","AT");
				}
			}
			else {
				if (_MatchKey.equals("Banglalink")) {
					_img.setImageResource(R.drawable.ic1_2);
					if(getResources().getResourceEntryName(_img.getId()).equals("sim1_img")){
						 Const.StringSAVE("SiM1ID","BL");
					}else{
						 Const.StringSAVE("SiM2ID","BL");
					}
				}
				else {
					if (_MatchKey.equals("Grameenphone")) {
						_img.setImageResource(R.drawable.ic_grameenphone);
						if(getResources().getResourceEntryName(_img.getId()).equals("sim1_img")){
							 Const.StringSAVE("SiM1ID","GP");
						}else{
							 Const.StringSAVE("SiM2ID","GP");
						}
					}
					else {
						_img.setImageResource(R.drawable.ic1_4);
						if(getResources().getResourceEntryName(_img.getId()).equals("sim1_img")){
							 Const.StringSAVE("SiM1ID","TT");
						}else{
							 Const.StringSAVE("SiM2ID","TT");
						}
					}
				}
			}
		}
	}
	
	
	public void _setBackground(final View _view, final double _radius, final double _shadow, final String _color, final boolean _ripple) {
		if (_ripple) {
			android.graphics.drawable.GradientDrawable gd = new android.graphics.drawable.GradientDrawable();
			gd.setColor(Color.parseColor(_color));
			gd.setCornerRadius((int)_radius);
			_view.setElevation((int)_shadow);
			android.content.res.ColorStateList clrb = new android.content.res.ColorStateList(new int[][]{new int[]{}}, new int[]{Color.parseColor("#9e9e9e")});
			android.graphics.drawable.RippleDrawable ripdrb = new android.graphics.drawable.RippleDrawable(clrb , gd, null);
			_view.setClickable(true);
			_view.setBackground(ripdrb);
		}
		else {
			android.graphics.drawable.GradientDrawable gd = new android.graphics.drawable.GradientDrawable();
			gd.setColor(Color.parseColor(_color));
			gd.setCornerRadius((int)_radius);
			_view.setBackground(gd);
			_view.setElevation((int)_shadow);
		}
	}
	
	
	public void _MakeMerqueText(final TextView _view) {
		_view.setEllipsize(TextUtils.TruncateAt.MARQUEE);
		_view.setMarqueeRepeatLimit(-1);
		_view.setSingleLine(true);
		_view.setSelected(true);
		
	}
	
	
	public void _allSideReiidi(final String _color, final String _colors, final View _view) {
		android.graphics.drawable.GradientDrawable IEHACAI = new android.graphics.drawable.GradientDrawable();
		IEHACAI.setColor(Color.parseColor(_color));
		IEHACAI.setCornerRadii(new float[] { 10, 10, 10, 10, 10, 10, 10, 10 });
		IEHACAI.setStroke(3, Color.parseColor(_colors));
		
		
		android.content.res.ColorStateList clrb = new android.content.res.ColorStateList(new int[][]{new int[]{}}, new int[]{Color.parseColor("#9e9e9e")});
			android.graphics.drawable.RippleDrawable ripdrb = new android.graphics.drawable.RippleDrawable(clrb , IEHACAI, null);
			_view.setClickable(true);
			_view.setBackground(ripdrb);
	}
	
	
	public void _GetPhoneSiM() {
		try {
			    List<SubscriptionInfo> subscriptionInfos = SubscriptionManager.from(getApplicationContext()).getActiveSubscriptionInfoList();
			
			    if (subscriptionInfos != null && subscriptionInfos.size() >= 1) {
				        _SiMIndex(sim1_img, subscriptionInfos.get(0).getCarrierName().toString());
				        sim1_name.setText(subscriptionInfos.get(0).getCarrierName().toString());
				        _MakeMerqueText(sim1_name);
				        Const.StringSAVE("isSiM1Enable", "true");
				    } else {
				        enable_sim1.setEnabled(false);
				        Const.StringSAVE("isSiM1Enable", "false");
				        sim1_name.setText("No Service");
				        _MakeMerqueText(sim1_name);
				    }
			
			    if (subscriptionInfos != null && subscriptionInfos.size() >= 2) {
				        _SiMIndex(sim2_img, subscriptionInfos.get(1).getCarrierName().toString());
				        sim2_name.setText(subscriptionInfos.get(1).getCarrierName().toString());
				        _MakeMerqueText(sim2_name);
				        Const.StringSAVE("isSiM2Enable", "true");
				    } else {
				        enable_sim2.setEnabled(false);
				        Const.StringSAVE("isSiM2Enable", "false");
				        sim2_name.setText("No Service");
				        _MakeMerqueText(sim2_name);
				    }
			
			    // Check if SIM 1 is present but deactivated
			    if (subscriptionInfos != null && subscriptionInfos.size() >= 1 && !isSimActive(subscriptionInfos.get(0))) {
				        enable_sim1.setEnabled(false);
				        Const.StringSAVE("isSiM1Enable", "false");
				        sim1_name.setText("No Service");
				        _MakeMerqueText(sim1_name);
				    }
			
			    // Check if SIM 2 is present but deactivated
			    if (subscriptionInfos != null && subscriptionInfos.size() >= 2 && !isSimActive(subscriptionInfos.get(1))) {
				        enable_sim2.setEnabled(false);
				        Const.StringSAVE("isSiM2Enable", "false");
				        sim2_name.setText("No Service");
				        _MakeMerqueText(sim2_name);
				    }
		} catch (Exception e) {
			    showMessage("Auto SIM Detection Failed😥");
		}
		
		
	}
	// Method to check if a SIM is active
	private boolean isSimActive(SubscriptionInfo subscriptionInfo) {
		    TelephonyManager telephonyManager = getSystemService(TelephonyManager.class);
		    int simState = telephonyManager.getSimState(subscriptionInfo.getSimSlotIndex());
		    return simState == TelephonyManager.SIM_STATE_READY;
	}
	{
	}
	
	
	public boolean _CheckBroadCastPermission() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(android.Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
			
			  int receiveSMS = ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECEIVE_SMS); int readSMS = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_SMS);
			   List<String> listPermissionsNeeded = new ArrayList<>();
			
			   if (receiveSMS != PackageManager.PERMISSION_GRANTED) {
				    listPermissionsNeeded.add(Manifest.permission.RECEIVE_SMS);
				   }
			   if (readSMS != PackageManager.PERMISSION_GRANTED) {
				    listPermissionsNeeded.add(android.Manifest.permission.READ_SMS);
				   }
			   if (!listPermissionsNeeded.isEmpty()) {
				    ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), 1);
				    return false;
				   }
			    return true;
		}
		return true;
	}
	
	
	public void _CheckServiceIsRuning() {
	}
	public boolean isServiceRunning (Context c,Class<?> serviceClass) {
			ActivityManager activityManager = (ActivityManager)c.getSystemService (Context.ACTIVITY_SERVICE);
			
			List<ActivityManager.RunningServiceInfo> services = activityManager.getRunningServices(Integer.MAX_VALUE);
			
			for (ActivityManager.RunningServiceInfo runningServiceInfo : services) {
					
					if(runningServiceInfo.service.getClassName().equals(serviceClass.getName())) { 
							return true;
					}
					
			}
			return false;
	}
	{
	}
	
	
	public void _setEVEN(final String _Subject, final String _Message) {
		
	}
	   public void setEvent(final String _Subject, final String _Message){
		        
		         Intent smsIntent = new Intent("EventLOG");
		         smsIntent.putExtra("EventBody",_Message);
		         smsIntent.putExtra("Status",_Subject);
		         LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(smsIntent);
		    }
	{
	}
	
	public class Recyclerview1Adapter extends RecyclerView.Adapter<Recyclerview1Adapter.ViewHolder> {
		
		ArrayList<HashMap<String, Object>> _data;
		
		public Recyclerview1Adapter(ArrayList<HashMap<String, Object>> _arr) {
			_data = _arr;
		}
		
		@Override
		public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			LayoutInflater _inflater = getLayoutInflater();
			View _v = _inflater.inflate(R.layout.logs, null);
			RecyclerView.LayoutParams _lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			_v.setLayoutParams(_lp);
			return new ViewHolder(_v);
		}
		
		@Override
		public void onBindViewHolder(ViewHolder _holder, final int _position) {
			View _view = _holder.itemView;
			
			final TextView mesg = _view.findViewById(R.id.mesg);
			
			RecyclerView.LayoutParams _lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			_view.setLayoutParams(_lp);
			if (_data.get((int)_position).get("Status").toString().equals("0")) {
				mesg.setText(Html.fromHtml("<font color=#4caf50>".concat(_data.get((int)_position).get("EventLOG").toString().concat(" -- </font>"))));
			}
			else {
				mesg.setText(Html.fromHtml("<font color=#d81b60>".concat(_data.get((int)_position).get("EventLOG").toString().concat("-- : </font>"))));
			}
		}
		
		@Override
		public int getItemCount() {
			return _data.size();
		}
		
		public class ViewHolder extends RecyclerView.ViewHolder {
			public ViewHolder(View v) {
				super(v);
			}
		}
	}
	
	@Deprecated
	public void showMessage(String _s) {
		Toast.makeText(getApplicationContext(), _s, Toast.LENGTH_SHORT).show();
	}
	
	@Deprecated
	public int getLocationX(View _v) {
		int _location[] = new int[2];
		_v.getLocationInWindow(_location);
		return _location[0];
	}
	
	@Deprecated
	public int getLocationY(View _v) {
		int _location[] = new int[2];
		_v.getLocationInWindow(_location);
		return _location[1];
	}
	
	@Deprecated
	public int getRandom(int _min, int _max) {
		Random random = new Random();
		return random.nextInt(_max - _min + 1) + _min;
	}
	
	@Deprecated
	public ArrayList<Double> getCheckedItemPositionsToArray(ListView _list) {
		ArrayList<Double> _result = new ArrayList<Double>();
		SparseBooleanArray _arr = _list.getCheckedItemPositions();
		for (int _iIdx = 0; _iIdx < _arr.size(); _iIdx++) {
			if (_arr.valueAt(_iIdx))
			_result.add((double)_arr.keyAt(_iIdx));
		}
		return _result;
	}
	
	@Deprecated
	public float getDip(int _input) {
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, _input, getResources().getDisplayMetrics());
	}
	
	@Deprecated
	public int getDisplayWidthPixels() {
		return getResources().getDisplayMetrics().widthPixels;
	}
	
	@Deprecated
	public int getDisplayHeightPixels() {
		return getResources().getDisplayMetrics().heightPixels;
	}
}