package com.techbdhost.support;

import android.animation.*;
import android.app.*;
import android.app.Activity;
import android.content.*;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.media.*;
import android.net.*;
import android.net.Uri;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import java.util.regex.*;
import org.json.*;
import com.romellfudi.ussdlibrary.OverlayShowingService;
import com.romellfudi.ussdlibrary.SplashLoadingService;
import com.romellfudi.ussdlibrary.USSDApi;
import com.romellfudi.ussdlibrary.USSDController;
import android.view.accessibility.AccessibilityManager.AccessibilityStateChangeListener;
import  android.view.accessibility.AccessibilityManager;

public class PermissionActivity extends AppCompatActivity {
	
	private LinearLayout linear2;
	private ImageView img_header;
	private TextView txt_title;
	private TextView txt_msg;
	private TextView error_txt;
	private TextView textview1;
	
	private Intent i = new Intent();
	private SharedPreferences SaveUser;
	
	@Override
	protected void onCreate(Bundle _savedInstanceState) {
		super.onCreate(_savedInstanceState);
		setContentView(R.layout.permission);
		initialize(_savedInstanceState);
		initializeLogic();
	}
	
	private void initialize(Bundle _savedInstanceState) {
		linear2 = findViewById(R.id.linear2);
		img_header = findViewById(R.id.img_header);
		txt_title = findViewById(R.id.txt_title);
		txt_msg = findViewById(R.id.txt_msg);
		error_txt = findViewById(R.id.error_txt);
		textview1 = findViewById(R.id.textview1);
		SaveUser = getSharedPreferences("SaveUser", Activity.MODE_PRIVATE);
		
		textview1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View _view) {
				androidx.core.app.ActivityCompat.requestPermissions(PermissionActivity.this, new String[] {
						
						android.Manifest.permission.READ_EXTERNAL_STORAGE,
						android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
						android.Manifest.permission.READ_PHONE_STATE,
						android.Manifest.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS,
						android.Manifest.permission.CALL_PHONE,
						android.Manifest.permission.RECEIVE_SMS,
						android.Manifest.permission.READ_SMS
						
					}, 1001);
			}
		});
	}
	
	private void initializeLogic() {
		//Make Screen On
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		_NavStatusBarColor("#ffffff", "#ffffff");
		_DARK_ICONS();
		_setBackground(textview1, 10, 0, "#FFAB00", true);
		_RequestOfPermission();
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
			
		}
	}
	
	
	public void _RequestOfPermission() {
	}
	 @Override
	 public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		 super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		  if (requestCode == 1001) {
				//User has clicked "grant" or "deny" in the runtime dialog
				
				if(USSDController.verifyAccesibilityAccess(PermissionActivity.this)){
				     error_txt.setVisibility(View.GONE);
				     Const.StringSAVE("Permit","DONE!");
				 	i.setClass(getApplicationContext(), SetupHostActivity.class);
				     startActivity(i);
				    }
				
			  }
		
		
		
		
		//Accessibility Listener added here 
		
		AccessibilityManager am = (AccessibilityManager) getSystemService(Context.ACCESSIBILITY_SERVICE);
		    am.addAccessibilityStateChangeListener(new AccessibilityManager.AccessibilityStateChangeListener() {
			        @Override
			        public void onAccessibilityStateChanged(boolean isTrue) {
							if(isTrue){
							 	error_txt.setVisibility(View.GONE);
					             Const.StringSAVE("Permit","DONE!");
							 	i.setClass(getApplicationContext(), SetupHostActivity.class);
					             startActivity(i);
							}else{
								 error_txt.setVisibility(View.VISIBLE);
							}
				        }
			    });
		    
		
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