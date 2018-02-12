package app.user.global;


import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import app.taxiapp.user.R;
import app.user.activity.Fare;
import app.user.activity.Home;
import app.user.webservices.CheckRequestStatus;


/**
 * Created by user1 on 11/9/2016.
 */


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    SharedPreferences sharedPreferences;
// {driver_id=9, driver_longitude=30.7080852, rating=4, pickup_longitude=76.6896822, type=Accept, request_id=4, pickup_latitude=30.7112163, driver_image=http://18.218.130.74/taxi/img/profile_images/9_1516350455.jpg, message=Request Accepted, driver_latitude=30.7080852, driver_name=sonalsonal, car_number=hshshsjsjs, driver_mobile=8699231814, noti_type=A, noti_to=User}
//message  {driver_id=9, driver_longitude=30.709706, pickup_longitude=76.6884122, type=Accept, request_id=57, pickup_latitude=30.7080817, driver_image=http://18.218.130.74/taxi/img/profile_images/9_1516350455.jpg, message=Request Accepted, driver_latitude=30.709706, driver_name=sonalsonal, car_number=hshshsjsjs, driver_mobile=8699231814, noti_type=A, noti_to=User}
    // {driver_id=9, message=Ride Started, noti_type=RS, noti_to=User}

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.e(TAG, "message  " + remoteMessage.getData());

        sharedPreferences = getSharedPreferences(Global.pref_name, Context.MODE_PRIVATE);

        if (remoteMessage.getData().get("noti_type").equalsIgnoreCase("A")) {
            Log.e("inside", "value noti" + remoteMessage.getData().get("driver_name"));
            Global.request_id = remoteMessage.getData().get("request_id");

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("request_id", remoteMessage.getData().get("request_id"));
            editor.putString("driver_id", remoteMessage.getData().get("driver_id"));
            editor.commit();

            if (sharedPreferences.getString("request_id", "") != null) {

                CheckRequestStatus c = new CheckRequestStatus();
                c.check(Home.context, sharedPreferences.getString("user_id", ""), Global.request_id);
            }

        } else if (remoteMessage.getData().get("noti_type").equalsIgnoreCase("fare")) {


        } else if (remoteMessage.getData().get("noti_type").equalsIgnoreCase("RS")) {
            if (sharedPreferences.getString("request_id", "") != null) {
                CheckRequestStatus c = new CheckRequestStatus();
                c.check(Home.context, sharedPreferences.getString("user_id", ""), sharedPreferences.getString("request_id", ""));
            }
        } else if (remoteMessage.getData().get("noti_type").equalsIgnoreCase("RC")) {
            // {driver_id=9, with_surcharge=2.2232909798237, fare=74.109699327456, source_location=Sector 74, Sahibzada Ajit Singh Nagar, Punjab 140308, India, message=Ride Completed, dest_location=S.C.O., 44, Chandigarh - Panchkula Rd, Manimajra, Chandigarh, 160101, India, noti_type=RC, noti_to=User}
            Home.isRunning = false;
            Intent i = new Intent(Home.context, Fare.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.putExtra("fare", remoteMessage.getData().get("fare"));
            i.putExtra("with_surcharge", remoteMessage.getData().get("with_surcharge"));
            i.putExtra("source_location", remoteMessage.getData().get("source_location"));
            i.putExtra("dest_location", remoteMessage.getData().get("dest_location"));
            startActivity(i);
            ((Activity) Home.context).finish();


        }
        sendNotification(remoteMessage.getData(), remoteMessage.getData().get("message"));
    }


    private void sendNotification(Map<String, String> data, String messageBody) {
        Intent intent = new Intent(this, Home.class);
        intent.putExtra("i", "noti");
        intent.putExtra("sts", "A");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Taxi App")
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }


}