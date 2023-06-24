package com.techbdhost.support;

import android.animation.*;
import android.app.*;
import android.content.*;
import android.content.Intent;
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
import android.view.View.*;
import android.view.animation.*;
import android.webkit.*;
import android.widget.*;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.annotation.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.android.volley.*;
import com.github.ybq.android.spinkit.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
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

import com.android.volley.toolbox.HurlStack;


public class MainActivity extends AppCompatActivity {
	
	private Timer _timer = new Timer();
	
	private LinearLayout linear1;
	private ImageView imageview1;
	private LinearLayout linear2;
	
	private Intent intent = new Intent();
	private TimerTask timer;
	
	@Override
	protected void onCreate(Bundle _savedInstanceState) {
		super.onCreate(_savedInstanceState);
		setContentView(R.layout.main);
		initialize(_savedInstanceState);
		initializeLogic();
	}
	
	private void initialize(Bundle _savedInstanceState) {
		linear1 = findViewById(R.id.linear1);
		imageview1 = findViewById(R.id.imageview1);
		linear2 = findViewById(R.id.linear2);
	}
	
	private void initializeLogic() {
		final ShimmerTextView b = new ShimmerTextView(this);
		b.setText(getString(R.string.app_name));
		b.setLayoutParams(new LinearLayout.LayoutParams(android.widget.LinearLayout.LayoutParams.WRAP_CONTENT, android.widget.LinearLayout.LayoutParams.WRAP_CONTENT));
		b.setTextSize(30);
		b.setTextColor(Color.parseColor("#3f51b5"));
		b.setTypeface(Typeface.DEFAULT_BOLD);
		//b.setTypeface(Typeface.createFromAsset(getAssets(),"fonts/fnt1.ttf"),0);
		linear2.addView(b);
		
		final Shimmer shimmer = new Shimmer();
		shimmer.start(b);
		if (Const.FetchVALUE("Permit").equals("")) {
			startActivity(new Intent(MainActivity.this, PermissionActivity.class));
			finish();
		}
		else {
			if (Const.FetchVALUE("Auth").equals("")) {
				startActivity(new Intent(MainActivity.this, SetupHostActivity.class));
				finish();
			}
			else {
				_GetCompanyDATA();
			}
		}
	}
	
	public void _ShimmerEffect() {
	}
	public interface ShimmerViewBase {
		
		    public float getGradientX();
		    public void setGradientX(float gradientX);
		    public boolean isShimmering();
		    public void setShimmering(boolean isShimmering);
		    public boolean isSetUp();
		    public void setAnimationSetupCallback(ShimmerViewHelper.AnimationSetupCallback callback);
		    public int getPrimaryColor();
		    public void setPrimaryColor(int primaryColor);
		    public int getReflectionColor();
		    public void setReflectionColor(int reflectionColor);
	}
	
	
	
	public static class ShimmerViewHelper {
		
		    public interface AnimationSetupCallback {
			        void onSetupAnimation(View target);
			    }
		
		    private static final int DEFAULT_REFLECTION_COLOR = 0xFFFFEB3B;
		
		    private View view;
		    private Paint paint;
		
		    // center position of the gradient
		    private float gradientX;
		
		    // shader applied on the text view
		    // only null until the first global layout
		    private LinearGradient linearGradient;
		
		    // shader's local matrix
		    // never null
		    private Matrix linearGradientMatrix;
		
		    private int primaryColor;
		
		    // shimmer reflection color
		    private int reflectionColor;
		
		    // true when animating
		    private boolean isShimmering;
		
		    // true after first global layout
		    private boolean isSetUp;
		
		    // callback called after first global layout
		    private AnimationSetupCallback callback;
		
		    public ShimmerViewHelper(View view, Paint paint, AttributeSet attributeSet) {
			        this.view = view;
			        this.paint = paint;
			        init(attributeSet);
			    }
		
		    public float getGradientX() {
			        return gradientX;
			    }
		
		    public void setGradientX(float gradientX) {
			        this.gradientX = gradientX;
			        view.invalidate();
			    }
		
		    public boolean isShimmering() {
			        return isShimmering;
			    }
		
		    public void setShimmering(boolean isShimmering) {
			        this.isShimmering = isShimmering;
			    }
		
		    public boolean isSetUp() {
			        return isSetUp;
			    }
		
		    public void setAnimationSetupCallback(AnimationSetupCallback callback) {
			        this.callback = callback;
			    }
		
		    public int getPrimaryColor() {
			        return primaryColor;
			    }
		
		    public void setPrimaryColor(int primaryColor) {
			        this.primaryColor = primaryColor;
			        if (isSetUp) {
				            resetLinearGradient();
				        }
			    }
		
		    public int getReflectionColor() {
			        return reflectionColor;
			    }
		
		    public void setReflectionColor(int reflectionColor) {
			        this.reflectionColor = reflectionColor;
			        if (isSetUp) {
				            resetLinearGradient();
				        }
			    }
		
		    private void init(AttributeSet attributeSet) {
			
			        reflectionColor = DEFAULT_REFLECTION_COLOR;
			
			        if (attributeSet != null) {
				            /*
            TypedArray a = view.getContext().obtainStyledAttributes(attributeSet, R.styleable.ShimmerView, 0, 0);
            if (a != null) {
                try {
                    reflectionColor = a.getColor(R.styleable.ShimmerView_reflectionColor, DEFAULT_REFLECTION_COLOR);
                } catch (Exception e) {
                    android.util.Log.e("ShimmerTextView", "Error while creating the view:", e);
                } finally {
                    a.recycle();
                }
            }
            */
				            reflectionColor = DEFAULT_REFLECTION_COLOR;
				        }
			
			        linearGradientMatrix = new Matrix();
			    }
		
		    private void resetLinearGradient() {
			
			        // our gradient is a simple linear gradient from textColor to reflectionColor. its axis is at the center
			        // when it's outside of the view, the outer color (textColor) will be repeated (Shader.TileMode.CLAMP)
			        // initially, the linear gradient is positioned on the left side of the view
			        linearGradient = new LinearGradient(-view.getWidth(), 0, 0, 0,
			                new int[]{
				                        primaryColor,
				                        reflectionColor,
				                        primaryColor,
				                },
			                new float[]{
				                        0,
				                        0.5f,
				                        1
				                },
			                Shader.TileMode.CLAMP
			        );
			
			        paint.setShader(linearGradient);
			    }
		
		    protected void onSizeChanged() {
			
			        resetLinearGradient();
			
			        if (!isSetUp) {
				            isSetUp = true;
				
				            if (callback != null) {
					                callback.onSetupAnimation(view);
					            }
				        }
			    }
		
		    /**
     * content of the wrapping view's onDraw(Canvas)
     * MUST BE CALLED BEFORE SUPER STATEMENT
     */
		    public void onDraw() {
			
			        // only draw the shader gradient over the text while animating
			        if (isShimmering) {
				
				            // first onDraw() when shimmering
				            if (paint.getShader() == null) {
					                paint.setShader(linearGradient);
					            }
				
				            // translate the shader local matrix
				            linearGradientMatrix.setTranslate(2 * gradientX, 0);
				
				            // this is required in order to invalidate the shader's position
				            linearGradient.setLocalMatrix(linearGradientMatrix);
				
				        } else {
				            // we're not animating, remove the shader from the paint
				            paint.setShader(null);
				        }
			
			    }
	}
	
	
	public class ShimmerTextView extends TextView implements ShimmerViewBase {
		
		    private ShimmerViewHelper shimmerViewHelper;
		
		    public ShimmerTextView(Context context) {
			        super(context);
			        shimmerViewHelper = new ShimmerViewHelper(this, getPaint(), null);
			        shimmerViewHelper.setPrimaryColor(getCurrentTextColor());
			    }
		
		    public ShimmerTextView(Context context, AttributeSet attrs) {
			        super(context, attrs);
			        shimmerViewHelper = new ShimmerViewHelper(this, getPaint(), attrs);
			        shimmerViewHelper.setPrimaryColor(getCurrentTextColor());
			    }
		
		    public ShimmerTextView(Context context, AttributeSet attrs, int defStyle) {
			        super(context, attrs, defStyle);
			        shimmerViewHelper = new ShimmerViewHelper(this, getPaint(), attrs);
			        shimmerViewHelper.setPrimaryColor(getCurrentTextColor());
			    }
		
		    @Override
		    public float getGradientX() {
			        return shimmerViewHelper.getGradientX();
			    }
		
		    @Override
		    public void setGradientX(float gradientX) {
			        shimmerViewHelper.setGradientX(gradientX);
			    }
		
		    @Override
		    public boolean isShimmering() {
			        return shimmerViewHelper.isShimmering();
			    }
		
		    @Override
		    public void setShimmering(boolean isShimmering) {
			        shimmerViewHelper.setShimmering(isShimmering);
			    }
		
		    @Override
		    public boolean isSetUp() {
			        return shimmerViewHelper.isSetUp();
			    }
		
		    @Override
		    public void setAnimationSetupCallback(ShimmerViewHelper.AnimationSetupCallback callback) {
			        shimmerViewHelper.setAnimationSetupCallback(callback);
			    }
		
		    @Override
		    public int getPrimaryColor() {
			        return shimmerViewHelper.getPrimaryColor();
			    }
		
		    @Override
		    public void setPrimaryColor(int primaryColor) {
			        shimmerViewHelper.setPrimaryColor(primaryColor);
			    }
		
		    @Override
		    public int getReflectionColor() {
			        return shimmerViewHelper.getReflectionColor();
			    }
		
		    @Override
		    public void setReflectionColor(int reflectionColor) {
			        shimmerViewHelper.setReflectionColor(reflectionColor);
			    }
		
		    @Override
		    public void setTextColor(int color) {
			        super.setTextColor(color);
			        if (shimmerViewHelper != null) {
				            shimmerViewHelper.setPrimaryColor(getCurrentTextColor());
				        }
			    }
		
		    @Override
		    public void setTextColor(android.content.res.ColorStateList colors) {
			        super.setTextColor(colors);
			        if (shimmerViewHelper != null) {
				            shimmerViewHelper.setPrimaryColor(getCurrentTextColor());
				        }
			    }
		
		    @Override
		    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
			        super.onSizeChanged(w, h, oldw, oldh);
			        if (shimmerViewHelper != null) {
				            shimmerViewHelper.onSizeChanged();
				        }
			    }
		
		    @Override
		    public void onDraw(Canvas canvas) {
			        if (shimmerViewHelper != null) {
				            shimmerViewHelper.onDraw();
				        }
			        super.onDraw(canvas);
			    }
	}
	
	
	public class ShimmerButton extends Button implements ShimmerViewBase {
		
		    private ShimmerViewHelper shimmerViewHelper;
		
		    public ShimmerButton(Context context) {
			        super(context);
			        shimmerViewHelper = new ShimmerViewHelper(this, getPaint(), null);
			        shimmerViewHelper.setPrimaryColor(getCurrentTextColor());
			    }
		
		    public ShimmerButton(Context context, AttributeSet attrs) {
			        super(context, attrs);
			        shimmerViewHelper = new ShimmerViewHelper(this, getPaint(), attrs);
			        shimmerViewHelper.setPrimaryColor(getCurrentTextColor());
			    }
		
		    public ShimmerButton(Context context, AttributeSet attrs, int defStyle) {
			        super(context, attrs, defStyle);
			        shimmerViewHelper = new ShimmerViewHelper(this, getPaint(), attrs);
			        shimmerViewHelper.setPrimaryColor(getCurrentTextColor());
			    }
		
		    @Override
		    public float getGradientX() {
			        return shimmerViewHelper.getGradientX();
			    }
		
		    @Override
		    public void setGradientX(float gradientX) {
			        shimmerViewHelper.setGradientX(gradientX);
			    }
		
		    @Override
		    public boolean isShimmering() {
			        return shimmerViewHelper.isShimmering();
			    }
		
		    @Override
		    public void setShimmering(boolean isShimmering) {
			        shimmerViewHelper.setShimmering(isShimmering);
			    }
		
		    @Override
		    public boolean isSetUp() {
			        return shimmerViewHelper.isSetUp();
			    }
		
		    @Override
		    public void setAnimationSetupCallback(ShimmerViewHelper.AnimationSetupCallback callback) {
			        shimmerViewHelper.setAnimationSetupCallback(callback);
			    }
		
		    @Override
		    public int getPrimaryColor() {
			        return shimmerViewHelper.getPrimaryColor();
			    }
		
		    @Override
		    public void setPrimaryColor(int primaryColor) {
			        shimmerViewHelper.setPrimaryColor(primaryColor);
			    }
		
		    @Override
		    public int getReflectionColor() {
			        return shimmerViewHelper.getReflectionColor();
			    }
		
		    @Override
		    public void setReflectionColor(int reflectionColor) {
			        shimmerViewHelper.setReflectionColor(reflectionColor);
			    }
		
		    @Override
		    public void setTextColor(int color) {
			        super.setTextColor(color);
			        if (shimmerViewHelper != null) {
				            shimmerViewHelper.setPrimaryColor(getCurrentTextColor());
				        }
			    }
		
		    @Override
		    public void setTextColor(android.content.res.ColorStateList colors) {
			        super.setTextColor(colors);
			        if (shimmerViewHelper != null) {
				            shimmerViewHelper.setPrimaryColor(getCurrentTextColor());
				        }
			    }
		
		    @Override
		    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
			        super.onSizeChanged(w, h, oldw, oldh);
			        if (shimmerViewHelper != null) {
				            shimmerViewHelper.onSizeChanged();
				        }
			    }
		
		    @Override
		    public void onDraw(Canvas canvas) {
			        if (shimmerViewHelper != null) {
				            shimmerViewHelper.onDraw();
				        }
			        super.onDraw(canvas);
			    }
	}
	
	public class Shimmer {
		
		    public static final int ANIMATION_DIRECTION_LTR = 0;
		    public static final int ANIMATION_DIRECTION_RTL = 1;
		
		    private static final int DEFAULT_REPEAT_COUNT = 500;
		    private static final long DEFAULT_DURATION = 2000;
		    private static final long DEFAULT_START_DELAY = 0;
		    private static final int DEFAULT_DIRECTION = ANIMATION_DIRECTION_LTR;
		
		    private int repeatCount;
		    private long duration;
		    private long startDelay;
		    private int direction;
		    private android.animation.Animator.AnimatorListener animatorListener;
		
		    private android.animation.ObjectAnimator animator;
		
		    public Shimmer() {
			        repeatCount = DEFAULT_REPEAT_COUNT;
			        duration = DEFAULT_DURATION;
			        startDelay = DEFAULT_START_DELAY;
			        direction = DEFAULT_DIRECTION;
			    }
		
		    public int getRepeatCount() {
			        return repeatCount;
			    }
		
		    public Shimmer setRepeatCount(int repeatCount) {
			        this.repeatCount = repeatCount;
			        return this;
			    }
		
		    public long getDuration() {
			        return duration;
			    }
		
		    public Shimmer setDuration(long duration) {
			        this.duration = duration;
			        return this;
			    }
		
		    public long getStartDelay() {
			        return startDelay;
			    }
		
		    public Shimmer setStartDelay(long startDelay) {
			        this.startDelay = startDelay;
			        return this;
			    }
		
		    public int getDirection() {
			        return direction;
			    }
		
		    public Shimmer setDirection(int direction) {
			
			        if (direction != ANIMATION_DIRECTION_LTR && direction != ANIMATION_DIRECTION_RTL) {
				            throw new IllegalArgumentException("The animation direction must be either ANIMATION_DIRECTION_LTR or ANIMATION_DIRECTION_RTL");
				        }
			
			        this.direction = direction;
			        return this;
			    }
		
		    public android.animation.Animator.AnimatorListener getAnimatorListener() {
			        return animatorListener;
			    }
		
		    public Shimmer setAnimatorListener(android.animation.Animator.AnimatorListener animatorListener) {
			        this.animatorListener = animatorListener;
			        return this;
			    }
		
		    public <V extends View & ShimmerViewBase> void start(final V shimmerView) {
			
			        if (isAnimating()) {
				            return;
				        }
			
			        final Runnable animate = new Runnable() {
				            @Override
				            public void run() {
					
					                shimmerView.setShimmering(true);
					
					                float fromX = 0;
					                float toX = shimmerView.getWidth();
					                if (direction == ANIMATION_DIRECTION_RTL) {
						                    fromX = shimmerView.getWidth();
						                    toX = 0;
						                }
					
					                animator = android.animation.ObjectAnimator.ofFloat(shimmerView, "gradientX", fromX, toX);
					                animator.setRepeatCount(repeatCount);
					                animator.setDuration(duration);
					                animator.setStartDelay(startDelay);
					                animator.addListener(new android.animation.Animator.AnimatorListener() {
						                    @Override
						                    public void onAnimationStart(android.animation.Animator animation) {
							                    }
						
						                    @Override
						                    public void onAnimationEnd(android.animation.Animator animation) {
							                        shimmerView.setShimmering(false);
							
							
							                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
								                            shimmerView.postInvalidate();
								                        } else {
								                            shimmerView.postInvalidateOnAnimation();
								                        }
							
							                        animator = null;
							                    }
						
						                    @Override
						                    public void onAnimationCancel(android.animation.Animator animation) {
							
							                    }
						
						                    @Override
						                    public void onAnimationRepeat(android.animation.Animator animation) {
							
							                    }
						                });
					
					                if (animatorListener != null) {
						                    animator.addListener(animatorListener);
						                }
					
					                animator.start();
					            }
				        };
			
			        if (!shimmerView.isSetUp()) {
				            shimmerView.setAnimationSetupCallback(new ShimmerViewHelper.AnimationSetupCallback() {
					                @Override
					                public void onSetupAnimation(final View target) {
						                    animate.run();
						                }
					            });
				        } else {
				            animate.run();
				        }
			    }
		
		    public void cancel() {
			        if (animator != null) {
				            animator.cancel();
				        }
			    }
		
		    public boolean isAnimating() {
			        return animator != null && animator.isRunning();
			    }
	}
	{
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
	
	
	public void _GetCompanyDATA() {
			 String postUrl = "http://flexisoftwarebd.xyz/SiMSupport/CheckAccess.php";
		     /*
     if (adata == null) {
        adata = Volley.newRequestQueue(MainActivity.this,new HurlStack(null, ConfiGSSL.pinnedSSLSocketFactory()));
    }
	*/
		      StringRequest adata_s = new StringRequest(Request.Method.POST, postUrl , new Response.Listener<String>() {
			              @Override
			     public void onResponse(String response) {
				          if (_isJSONValid(response.toString())) {
					
					Const.StringSAVE("SERVER_CONFIG",response);
					if (Const.FetchJSONVALUE("ActiveStatus").equals("true")) {
						timer = new TimerTask() {
							@Override
							public void run() {
								runOnUiThread(new Runnable() {
									@Override
									public void run() {
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
																				 	startActivity(new Intent(MainActivity.this, HomeActivity.class));
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
										  MySingleton.getInstance(MainActivity.this).addToRequestQueue(adatasj_s);
									}
								});
							}
						};
						_timer.schedule(timer, (int)(2500));
					}
					else {
						finishAffinity();
					}
				}
				else {
					AndroXUtil.showMessage(getApplicationContext(), response.toString());
				}
				 }
			   },
		       new Response.ErrorListener() {
			       @Override
			      public void onErrorResponse(VolleyError error) {
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
						    params.put("AccessToken", Const.FetchJSONVALUE("AccessToken"));
						  return params;
					 }
					@Override
					public Priority getPriority() {
						return Priority.IMMEDIATE;
				     }
			  };
		
		   adata_s.setShouldCache(false);
		   // adata_s.setTag("MainActivity");
		 MySingleton.getInstance(this).addToRequestQueue(adata_s);
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
