package com.techbdhost.support;

import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.ArrayList;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class Const {
    
    static Const _instance;
    
        Context context;
        static SharedPreferences sharedPref;
        static SharedPreferences.Editor sharedPrefEditor;
        private static ArrayList<HashMap<String, Object>> ConfigList = new ArrayList<>();
        private static ArrayList<HashMap<String, Object>> AppConfiglist = new ArrayList<>();
    
        public static Const instance(Context context) {
            if (_instance == null) {
                _instance = new Const();
                _instance.configSessionUtils(context);
            }
               return _instance;
        }

    public static Const instance() {
           return _instance;
    }

    public void configSessionUtils(Context context) {
        this.context = context;
        sharedPref = context.getSharedPreferences("SERVER_CONFIG", Activity.MODE_PRIVATE);
        sharedPrefEditor = sharedPref.edit();
        
    }
    
    public  static void StringSAVE(String key, String value) {
        sharedPrefEditor.putString(key, value);
        sharedPrefEditor.commit();
    }
    
    public  static String FetchVALUE(String key) {
        return sharedPref.getString(key,"");
    }
    
    public static String FetchJSONVALUE(String key) {
        ConfigList = new Gson().fromJson(FetchVALUE("SERVER_CONFIG"), new TypeToken<ArrayList<HashMap<String, Object>>>(){}.getType());
        return ConfigList.get((int)0).get(key).toString();
    }
    
    public static String FetchAPPCONFIG(String key) {
        AppConfiglist = new Gson().fromJson(FetchVALUE("AutoConfig"), new TypeToken<ArrayList<HashMap<String, Object>>>(){}.getType());
        return AppConfiglist.get((int)0).get(key).toString();
    }
    
    public static final String MAIN(){
        return (Const.instance().FetchJSONVALUE("AccessDOMAIN"));
    }

    public static final String CheckAccess(){
        return MAIN()+"SiMSupport/CheckAccess.php";
    }
    
    public static final String AppData(){
        return MAIN()+"SiMSupport/LoadConfig.php";
    }
    public static final String ReceivedSMS(){
        return MAIN()+"SiMSupport/ReceivedSMS.php";
    }
    public static final String GetRequest(){
        return MAIN()+"SiMSupport/GetRequest.php";
    }
    
    public static final String PushRequest(){
        return MAIN()+"SiMSupport/PushRequest.php";
    }
    
 }


