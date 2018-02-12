package com.app.taxi.driver;

import android.content.SharedPreferences;
import android.util.Log;

import com.app.taxi.driver.volleyapi.CommonFunctions;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by Admin on 1/10/2018.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    private SharedPreferences preferences;

    @Override
    public void onTokenRefresh() {
//Intialized preferences
     //   preferences = getSharedPreferences(SplashScreen.PREFERENCES, Context.MODE_PRIVATE);

        // Get updated InstanceID token.
        CommonFunctions.recentToken = FirebaseInstanceId.getInstance().getToken();


        Log.e("MyFirebaseService TOKEN", CommonFunctions.recentToken);

        // Adding data to the preferences
      //  SharedPreferences.Editor edit = preferences.edit();
      //  edit.putString(SplashScreen.DeviceToken, recentToken);
     //   edit.commit();

    }
}
