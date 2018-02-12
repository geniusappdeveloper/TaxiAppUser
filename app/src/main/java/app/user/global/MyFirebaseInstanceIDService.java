package app.user.global;

import android.content.Context;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by user1 on 11/9/2016.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";
    Context context;

    public MyFirebaseInstanceIDService(Context context) {
        this.context = context;
        onTokenRefresh();
    }
    public MyFirebaseInstanceIDService() {

    }
    @Override
    public void onTokenRefresh() {
        //Getting registration token
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
       //Displaying token on logcat
        Log.e(TAG, "Refreshed token: " + refreshedToken);
        Global.device_id = refreshedToken;
        sendRegistrationToServer(refreshedToken);
    }
    private void sendRegistrationToServer(String token) {
        //You can implement this method to store the token on your server

    }
}