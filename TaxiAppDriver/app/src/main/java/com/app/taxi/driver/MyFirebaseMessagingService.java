package com.app.taxi.driver;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.app.taxi.driver.volleyapi.CommonFunctions;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

/**
 * Created by Admin on 1/10/2018.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private SharedPreferences preferences;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.e("MESSAGE", "NOTI" + remoteMessage.getData());
        preferences = getSharedPreferences(SplashScreen.PREFERENCES, Context.MODE_PRIVATE);
        sendNotification(remoteMessage.getData(), remoteMessage.getData().get("message"));


        Intent i=new Intent();
        i.setAction("com.app.noti");
        HomeScreen.context.sendBroadcast(i);
    }

    private void sendNotification(Map<String, String> data, String messageBody) {

        /*Intent showIntent = new Intent(MyFirebaseMessagingService.this, HomeScreen.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, showIntent, 0);*/
       Intent intent = null;
        intent = new Intent(HomeScreen.context, HomeScreen.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(HomeScreen.context, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("TAXI DRIVER")
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notificationBuilder.build());
        SharedPreferences.Editor editor = preferences.edit();


        // CommonFunctions.pickUpLatitude = data.get("source_latitude");
        //     CommonFunctions.pickUpLongitude = data.get("source_longitude");
        //     CommonFunctions.destinationLatitude = data.get("destination_latitude");
        //       CommonFunctions.destinationLongitude = data.get("destination_longitude");
        /*CommonFunctions.userFullame = data.get("username");
        CommonFunctions.userPhoneNumber = data.get("mobile");
        CommonFunctions.userImage = data.get("image");*/
        //   CommonFunctions.notificatioType = data.get("noti_type");
        // CommonFunctions.requestUserId = data.get("request_id");
        editor.putString(SplashScreen.USER_NAME, data.get("username"));
        editor.putString(SplashScreen.USER_MOBILE, data.get("mobile"));
        editor.putString(SplashScreen.USER_IMAGE, data.get("image"));
        editor.putString(SplashScreen.REQUEST_USER_ID, data.get("request_id"));
        editor.putString(SplashScreen.NOTIFICATION_TYPE,   data.get("noti_type"));
        editor.putString(SplashScreen.PICKUP_LATITUDE,   data.get("source_latitude"));
        editor.putString(SplashScreen.PICKUP_LONGITUDE,   data.get("source_longitude"));
        editor.putString(SplashScreen.DESTINATION_LATITUDE,   data.get("destination_latitude"));
        editor.putString(SplashScreen.DESTINATION_LONGITUDE,   data.get("destination_longitude"));
        editor.putString(SplashScreen.DESTINATION_ADDRESS,   data.get("destination_addresss"));
        editor.putString(SplashScreen.PICKUP_ADDRESS,   data.get("source_addresss"));
        editor.commit();

        CommonFunctions.notificationTo = data.get("noti_to");
        CommonFunctions.notificationMessage = data.get("message");
    }



}
