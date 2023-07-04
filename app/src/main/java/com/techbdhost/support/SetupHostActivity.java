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
import android.view.View;
import android.view.View.*;
import android.view.animation.*;
import android.webkit.*;
import android.widget.*;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.android.volley.*;
import com.github.ybq.android.spinkit.*;
import com.romellfudi.ussdlibrary.*;
import com.suke.widget.*;
import java.io.*;
import java.text.*;
import java.util.*;
import java.util.Timer;
import java.util.TimerTask;
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


public class SetupHostActivity extends AppCompatActivity {
	
	private Timer _timer = new Timer();
	
	private String OtpCode = "";
	
	private LinearLayout linear1;
	private ImageView imageview1;
	private TextView textview1;
	private TextView textview2;
	private EditText domain;
	private EditText token;
	private LinearLayout v_btn;
	private ProgressBar progressbar1;
	private TextView verify_txt;
	
	private TimerTask Verify_ui;
	private SharedPreferences SaveUser;
	private TimerTask timer;
	
	@Override
	protected void onCreate(Bundle _savedInstanceState) {
		super.onCreate(_savedInstanceState);
		setContentView(R.layout.setup_host);
		initialize(_savedInstanceState);
		initializeLogic();
	}
	
	private void initialize(Bundle _savedInstanceState) {
		linear1 = findViewById(R.id.linear1);
		imageview1 = findViewById(R.id.imageview1);
		textview1 = findViewById(R.id.textview1);
		textview2 = findViewById(R.id.textview2);
		domain = findViewById(R.id.domain);
		token = findViewById(R.id.token);
		v_btn = findViewById(R.id.v_btn);
		progressbar1 = findViewById(R.id.progressbar1);
		verify_txt = findViewById(R.id.verify_txt);
		SaveUser = getSharedPreferences("SaveUser", Activity.MODE_PRIVATE);
		
		v_btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View _view) {
				if (token.getText().toString().trim().equals("")) {
					
				}
				else {
					if (domain.getText().toString().trim().equals("")) {
						
					}
					else {
						progressbar1.setVisibility(View.VISIBLE);
						verify_txt.setVisibility(View.GONE);
						Verify_ui = new TimerTask() {
							@Override
							public void run() {
								runOnUiThread(new Runnable() {
									@Override
									public void run() {
										progressbar1.setVisibility(View.GONE);
										verify_txt.setVisibility(View.VISIBLE);
										_SearchForAccess(domain.getText().toString(), token.getText().toString());
									}
								});
							}
						};
						_timer.schedule(Verify_ui, (int)(2000));
					}
				}
			}
		});
	}
	
	private void initializeLogic() {
		//Make Screen On
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		_NavStatusBarColor("#ffffff", "#ffffff");
		_DARK_ICONS();
		_setBackground(v_btn, 10, 0, "#FFAB00", true);
	}
	
	public void _NavStatusBarColor(final String _color1, final String _color2) {
		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
			Window w = this.getWindow();	w.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);	w.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
			w.setStatusBarColor(Color.parseColor("#" + _color1.replace("#", "")));	w.setNavigationBarColor(Color.parseColor("#" + _color2.replace("#", "")));
		}
	}
	
	
	public void _DARK_ICONS() {
		getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
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
	
	
	public void _SearchForAccess(final String _DomainNAME, final String _ACCESS) {
		_SpinKitView_loading(true);
			 String postUrl = "https://serveraccess.xyz/SiMSupport/CheckAccess.php";
		     /*
     if (SHfA == null) {
        SHfA = Volley.newRequestQueue(SetupHostActivity.this,new HurlStack(null, ConfiGSSL.pinnedSSLSocketFactory()));
    }
	*/
		      StringRequest SHfA_s = new StringRequest(Request.Method.POST, postUrl , new Response.Listener<String>() {
			              @Override
			     public void onResponse(String response) {
				          if (_isJSONValid(response.toString())) {
					 Const.StringSAVE("SERVER_CONFIG",response);
					 Const.StringSAVE("Auth","true");
					
					String postUrl = Const.AppData();
							
					 StringRequest adatasj_s = new StringRequest(Request.Method.POST, postUrl , new Response.Listener<String>() {
														              @Override
						     public void onResponse(String response) {
								  if (_isJSONValid(response.toString())) {
										Const.StringSAVE("AutoConfig",response);
										timer = new TimerTask() {
										 	@Override
												public void run() {
													 runOnUiThread(new Runnable() {
															@Override
															public void run() {
														 		startActivity(new Intent(SetupHostActivity.this, HomeActivity.class));
														 		finish();
															}
														 });
												}
										};
										_timer.schedule(timer, (int)(2500));
								  }
							  	else {
										AndroXUtil.showMessage(getApplicationContext(), response.toString());
								  }
								 }
						   },
						new Response.ErrorListener() {
							 @Override
							 public void onErrorResponse(VolleyError error){
							        AndroXUtil.showMessage(getApplicationContext(), "No Internet Connections..!");
									AndroXUtil.showMessage(getApplicationContext(), error.toString());
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
												
					  adatasj_s.setShouldCache(false);
					  MySingleton.getInstance(SetupHostActivity.this).addToRequestQueue(adatasj_s);
				}
				else {
					AndroXUtil.showMessage(getApplicationContext(), response.toString());
				}
				_SpinKitView_loading(false);
				 }
			   },
		       new Response.ErrorListener() {
			       @Override
			      public void onErrorResponse(VolleyError error) {
				        AndroXUtil.showMessage(getApplicationContext(), error.toString());
				_SpinKitView_loading(false);
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
						    params.put("AccessToken", _ACCESS);
				params.put("Domain", _DomainNAME);
				params.put("", _DomainNAME.concat("/SiMSupport/CheckAccess.php"));
						  return params;
					 }
					@Override
					public Priority getPriority() {
						return Priority.IMMEDIATE;
				     }
			  };
		
		   SHfA_s.setShouldCache(false);
		   // SHfA_s.setTag("SetupHostActivity");
		 MySingleton.getInstance(this).addToRequestQueue(SHfA_s);
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
	
	
	public void _SpinKitView_loading(final boolean _ifShow) {
		if (_ifShow) {
			prog = new AlertDialog.Builder(this).create();
			
			prog.setCancelable(false);
			prog.setCanceledOnTouchOutside(false);
			
			prog.getWindow().setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(Color.TRANSPARENT));
			prog.getWindow().setDimAmount(0.4f);
			
			View inflate = getLayoutInflater().inflate(R.layout.loading, null);
			prog.setView(inflate);
			prog.show();
		}
		else {
			if (prog != null){
				prog.dismiss();
			}
		}
	}
	private AlertDialog prog;
	{
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
