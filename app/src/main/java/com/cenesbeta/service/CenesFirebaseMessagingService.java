package com.cenesbeta.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.cenesbeta.R;
import com.cenesbeta.activity.CenesBaseActivity;
import com.cenesbeta.leolin.shortcurtbadger.ShortcutBadger;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

import java.util.Map;


/**
 * Created by mandeep on 5/10/17.
 */

public class CenesFirebaseMessagingService extends FirebaseMessagingService {

    public static final String TAG = "CenesFirebaseMsgService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // ...

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        sendNotification(remoteMessage);

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

    private void sendNotification(RemoteMessage remoteMessage) {
        Map<String, String> notification = remoteMessage.getData();

        String CHANNEL_ID = "cenes_channel";// The id of the channel.

        int badgeCount = 1;
        if (getSharedPreferences("CenesPrefs",MODE_PRIVATE).getInt("badgeCounts",0) != 0) {
            badgeCount = getSharedPreferences("CenesPrefs", MODE_PRIVATE).getInt("badgeCounts",0) + 1;
        }
        SharedPreferences.Editor editor = getSharedPreferences("CenesPrefs", MODE_PRIVATE).edit();
        editor.putInt("badgeCounts", badgeCount);
        editor.apply();

        ShortcutBadger.applyCount(getApplicationContext(), badgeCount); //for 1.1.4+

        try {
            PendingIntent pendingIntent = null;
            Intent intent = null;
            if (notification.containsKey("payload")) {
                Log.d(TAG, "Payload : " + notification.get("payload"));
                JSONObject payloadObj = new JSONObject(notification.get("payload"));
                if (payloadObj.getString("type").equals("Gathering")) {

                    if (payloadObj.has("status") && payloadObj.getString("status").equals("AcceptAndDecline")) {
                        intent = new Intent(this, CenesBaseActivity.class);
                    } else if (payloadObj.has("status") && !payloadObj.getString("status").equalsIgnoreCase("old")) {
                        intent = new Intent(this, CenesBaseActivity.class);
                        intent.putExtra("dataFrom", "push");
                        intent.putExtra("eventId", payloadObj.getLong("id"));
                        intent.putExtra("message", "Your have been invited to...");
                        intent.putExtra("title", payloadObj.getString("title"));
                    } else if (payloadObj.has("status") && payloadObj.getString("status").equalsIgnoreCase("old")) {
                        intent = new Intent(this, CenesBaseActivity.class);
                        intent.putExtra("dataFrom", "gathering_push");
                        intent.putExtra("eventId", payloadObj.getLong("id"));
                    } else {
                        intent = new Intent(this, CenesBaseActivity.class);
                    }
                } else if (payloadObj.getString("type").equals("Reminder")) {
                    intent = new Intent(this, CenesBaseActivity.class);
                    /* intent.putExtra("dataFrom","push");
                    intent.putExtra("reminderId",payloadObj.getLong("notificationTypeId"));*/
                }
            } else {
                intent = new Intent(this, CenesBaseActivity.class);
            }
            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            }
            //Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                    //.setLargeIcon(R.drawable.ic_ceneslogos_push)
                    .setSmallIcon(R.drawable.ic_ceneslogos)
                    .setContentTitle(notification.get("title"))
                    .setContentText(notification.get("body"))
                    .setPriority(Notification.PRIORITY_DEFAULT)
                    .setAutoCancel(true).setChannelId(CHANNEL_ID)
                    .setSound(Uri.parse("android.resource://"
                            + getApplicationContext().getPackageName() + "/" + R.raw.cenes_notification_ringtone))
                    .setContentIntent(pendingIntent);


            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            Notification notificationObj = notificationBuilder.build();

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel mChannel = new NotificationChannel(
                        CHANNEL_ID, "Cenes Channel", importance);
                notificationManager.createNotificationChannel(mChannel);
            }


            notificationManager.notify(0, notificationObj);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
