package com.cenesbeta.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;

import com.cenesbeta.R;

import androidx.core.app.NotificationCompat;

/**
 * Created by rohan on 6/11/17.
 */

public class AlarmReceiver extends BroadcastReceiver {


    NotificationCompat.Builder builder;

    @Override
    public void onReceive(Context context, Intent intent) {
        //Toast.makeText(context, "I'm running", Toast.LENGTH_LONG).show();

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent notificationIntent = null;//new Intent(context, AlarmActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Uri alarmSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);

        try {
            Bundle b = intent.getExtras();
            String alarmName = b.getString("alarmName");
            String alarmSound = b.getString("alarmSound");
            System.out.println("alarmName: " + alarmName + "alarmSound: " + alarmSound);

            builder = new NotificationCompat.Builder(context);
            builder.setSmallIcon(R.mipmap.ic_ceneslogos);
            //builder.setColor(context.getResources().getColor(R.color.cenes_new_orange));
            builder.setContentTitle("Cenes Alarm");
            if(alarmName == null || alarmName.isEmpty()){
                builder.setContentText("Alarm");
            } else {
                builder.setContentText(alarmName);
            }
            builder.setSound(alarmSoundUri);
            builder.setAutoCancel(true);
            builder.setContentIntent(pendingIntent);
            builder.setDefaults(Notification.DEFAULT_VIBRATE);
            //builder.setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});
            notificationManager.notify(0, builder.build());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
