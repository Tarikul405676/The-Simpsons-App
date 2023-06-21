package com.techbdhost.support;

import android.content.Context;
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
import com.techbdhost.*;

public class MySingleton {
private static MySingleton mInstance;
private RequestQueue mRequestQueue;
private static Context mContext;

  private MySingleton(Context context){
    mContext = context;
    mRequestQueue = getRequestQueue();
  }
  public static synchronized MySingleton getInstance(Context context){
    if(mInstance == null){
        mInstance = new MySingleton(context);
    }
    return mInstance;
  }
  public RequestQueue getRequestQueue(){
    if(mRequestQueue == null){
        try{
          //mRequestQueue = Volley.newRequestQueue(mContext.getApplicationContext(),new HurlStack(null, ConfiGSSL.pinnedSSLSocketFactory()));
          mRequestQueue = Volley.newRequestQueue(mContext.getApplicationContext());
        } catch (OutOfMemoryError e) {}
    }
    return mRequestQueue;
  }
  public<T> void addToRequestQueue(Request<T> request){
    getRequestQueue().getCache().clear();
    getRequestQueue().add(request);
  }
  
   
   
   public void CancelAll(){
      if (getRequestQueue() != null) {
        getRequestQueue().cancelAll(mContext.getApplicationContext());
        /*
        mRequestQueue.cancelAll(new RequestQueue.RequestFilter() {
          @Override
          public boolean apply(Request<?> request) {
            // Log.d("DEBUG","request running: "+request.getTag().toString());
            return true;
          }
        });
         */
      }
   }
}