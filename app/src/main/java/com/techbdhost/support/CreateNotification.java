package com.techbdhost.support;

import android.app.Application;
import android.content.Context;
import android.app.Activity;
import android.app.Service;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Notification;
import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;
import android.app.Notification;
import android.app.NotificationManager;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import android.widget.*;
import java.util.Timer;
import java.util.TimerTask;
import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;
import android.os.IBinder;
import android.content.Intent;

import android.os.RemoteException;

 public  class CreateNotification extends Service{
    static Activity c;
    static Timer t;
	static TimerTask tt;
    static long startTime = System.currentTimeMillis();
    private androidx.core.app.NotificationCompat.Builder mBuilder ;
    private androidx.core.app.NotificationManagerCompat  notificationManager;
    
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    
    @Override
    public void onCreate() {
       startTime = System.currentTimeMillis();
       notifys();
	}
    
    public void notifys(){
        
            RemoteViews contentView = new RemoteViews(this.getPackageName(), R.layout.running_notification);
   
            NotificationManager notificationManager = (NotificationManager) this.getSystemService(c.NOTIFICATION_SERVICE);
	 	   //androidx.core.app.NotificationCompat.Builder builder; 
		
		    int notificationId = 1;
		    String channelId = "channel-01";
		    String channelName = "Channel Name";
		    int importance = NotificationManager.IMPORTANCE_LOW;
		    
		    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
			        NotificationChannel mChannel = new NotificationChannel(
			                channelId, channelName, importance);
			        notificationManager.createNotificationChannel(mChannel);
			    }
		    
	 	  mBuilder = new androidx.core.app.NotificationCompat.Builder(this, channelId)
		            .setSmallIcon(R.drawable.app_icon)
                    .setContent(contentView)
		            .setAutoCancel(false)
		            .setOngoing(false);
		            
           tt = new TimerTask() {
				@Override
				public void run() {
                    try{
                    contentView.setTextViewText(R.id.textview5, "Ongoing AUTOServer "+ _TimeFROMMILLSECOND(startTime));
                    notificationManager.notify(1, mBuilder.build());
                   } catch(OutOfMemoryError e){
                   } catch(RuntimeException e){
                   } catch(InternalError e){
                    notificationManager.cancel(1);
                    tt.cancel();
                    stopForeground(1);
                    notifys();
                   }
         
				}
			};
		    startForeground(1,mBuilder.getNotification());
			t = new Timer();
    		t.scheduleAtFixedRate(tt,5000,1000);
            
		    notificationManager.notify(1, mBuilder.build());

    }
        
    
    
    
    public String _TimeFROMMILLSECOND(long mills){
        
        return (String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(System.currentTimeMillis()-mills),
        TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis()-mills) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(System.currentTimeMillis()-mills)),
        TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()-mills) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis()-mills))));
    }
	
    @Override
    public void onDestroy() {
        androidx.core.app.NotificationManagerCompat  notificationManager = androidx.core.app.NotificationManagerCompat.from(this);
    	notificationManager.cancel(1);
        tt.cancel();
        stopForeground(1);
    
        super.onDestroy();
    }
 }