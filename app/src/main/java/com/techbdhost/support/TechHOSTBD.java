package com.techbdhost.support;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Process;
import android.util.Log;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.content.SharedPreferences;

public class TechHOSTBD extends Application {

    private static Context mApplicationContext;
    private Thread.UncaughtExceptionHandler uncaughtExceptionHandler;
    
    public static Context getContext() {
        return mApplicationContext;
    }

    @Override
    public void onCreate() {
        mApplicationContext = getApplicationContext();
        this.uncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();

        Thread.setDefaultUncaughtExceptionHandler(
            new Thread.UncaughtExceptionHandler() {
                @Override
                public void uncaughtException(Thread thread, Throwable throwable) {
//                    Intent intent = new Intent(getApplicationContext(), DebugActivity.class);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                    intent.putExtra("error", Log.getStackTraceString(throwable));
//
//                    PendingIntent pendingIntent =
//                        PendingIntent.getActivity(
//                            getApplicationContext(),
//                            11111,
//                            intent,
//                            PendingIntent.FLAG_ONE_SHOT
//                        );
//
//                    AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//                    am.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, 1000, pendingIntent);
//
//                  // SketchLogger.broadcastLog(Log.getStackTraceString(throwable));
//                    Process.killProcess(Process.myPid());
//                    System.exit(1);
//
//                    uncaughtExceptionHandler.uncaughtException(thread, throwable);
                }
            });
        //SketchLogger.startLogging();
        Const.instance(this);
        super.onCreate();
       this.registerActivityLifecycleCallbacks(new LifecycleCallbacks());

    }
    
    private class LifecycleCallbacks implements Application.ActivityLifecycleCallbacks {
       @Override
       public void onActivityCreated(final Activity activity, final Bundle savedInstanceState) {
           
       }
       @Override
       public void onActivityDestroyed(final Activity activity) {
           //MySingleton.getInstance(activity).CancelAll();
       }
       @Override
       public void onActivityPaused(final Activity activity) {
       }
       @Override
       public void onActivityResumed(final Activity activity) {
           
       }
       @Override
       public void onActivitySaveInstanceState(final Activity activity, final Bundle outState) {}
       @Override
       public void onActivityStarted(final Activity activity) {}
       @Override
       public void onActivityStopped(final Activity activity) {}
    }
    
 }


