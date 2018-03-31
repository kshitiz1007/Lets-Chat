package com.example.singhkshitiz.letschat;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by KSHITIZ on 3/22/2018.
 * -----WHENEVER NOTIFICATION IS RECEIEVED-----
 */

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    //---opening the application on recieving notification---
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        try{
            String notification_title= remoteMessage.getNotification().getTitle();
            String notification_message=remoteMessage.getNotification().getBody();
            String click_action=remoteMessage.getNotification().getClickAction();

            String from_user_id=remoteMessage.getData().get("from_user_id");
            //Log.e("from_user_id in FMS is:",from_user_id);

            //----BUILDING NOTIFICATION LAYOUT----
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(notification_title)
                    .setContentText(notification_message)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

            //--CLICK ACTION IS PROVIDED---
            Intent resultIntent = new Intent(click_action);
            resultIntent.putExtra("user_id",from_user_id);

            PendingIntent resultPendingIntent = PendingIntent.getActivity(this,0,resultIntent,PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(resultPendingIntent);


            int mNotificationId=(int)System.currentTimeMillis();
            NotificationManager mNotifyManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
            mNotifyManager.notify(mNotificationId,mBuilder.build());

           // Log.e("from_user_id 5 is:",from_user_id);
        }catch(Exception e){
            Log.e("Exception is ",e.toString());
        }
    }
}
