package com.techbdhost.support;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.telephony.SmsMessage;


public class OTPBroadCastReceiver extends BroadcastReceiver{


    @Override
    public void onReceive(Context context, Intent intent) {
        // Get Bundle object contained in the SMS intent passed in
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            // Get the SMS message
            Object[] pdus = (Object[]) bundle.get("pdus");
            if (pdus != null && pdus.length > 0) {
                SmsMessage[] smsMessages = new SmsMessage[pdus.length];
                StringBuilder smsStrBuilder = new StringBuilder();

                for (int i = 0; i < pdus.length; i++) {
                    smsMessages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                    String smsBody = smsMessages[i].getDisplayMessageBody();
                    smsStrBuilder.append(smsBody);
                }

                String smsStr = smsStrBuilder.toString();

                String sender = smsMessages[0].getOriginatingAddress();
                // Check if the sender is yours or perform any other filtering if needed

                Intent smsIntent = new Intent("android.provider.Telephony.SMS_RECEIVED");
                smsIntent.putExtra("message", smsStr);
                smsIntent.putExtra("Sender", sender);
                LocalBroadcastManager.getInstance(context).sendBroadcast(smsIntent);
            }
        }
    }

}