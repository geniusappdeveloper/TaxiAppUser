package com.app.taxi.driver;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.app.taxi.driver.volleyapi.CommonFunctions;

public class SplashScreen extends AppCompatActivity {
    private SharedPreferences preferences;
    public static final String PREFERENCES = "DriverPrefs";
    public static final String DriverFirstName = "DriverFirstName";
    public static final String DriverLastName = "DriverLastName";
    public static final String DriverPhoneNumber = "DriverPhoneNumber";
    public static final String DriverEmail = "DriverEmail";
    public static final String CountryCode = "CountryCode";
    public static  final String Driver_PIC_PATH = "Driver_PIC_PATH";
    public static final String UniqueCode = "UniqueCode";
    public static final String DriverID = "DriverID";
    public static final String DriverVerify = "DriverVerify";
    public static final String UserType = "UserType";
    public static final String DeviceToken = "SplashscreenToken";
    public static final String NOTIFICATION_TYPE = "notificationtype";
    public static final String DeviceProfilePic = "SplashscreenProfilePic";

    public static final String RIDE_STATUS = "ridestatus";
    public static final String car_number= "SplashscreenCardNum";
    public static final String PICKUP_LATITUDE= "PICKUPLATITUDE";
    public static final String PICKUP_LONGITUDE= "PICKUPLONGITUDE";
    public static final String DESTINATION_LATITUDE= "DESTINATIONLATITUDE";
    public static final String DESTINATION_LONGITUDE= "DESTINATIONLONGITUDE";
    public static final String DESTINATION_ADDRESS = "DESTINATION_ADDRESS";
    public static final String PICKUP_ADDRESS = "PICKUP_ADDRESS";
    public static final String REQUEST_USER_ID = "REQUEST_USER_ID";
    public static final String USER_MOBILE= "USER_MOBILE";
    public static final String USER_IMAGE= "USER_IMAGE";
    public static final String USER_NAME= "USER_NAME";

    public static final String CAR_CAPACITY = "CAR_CAPACITY";
    public static final String Driver_License_Pic = "Driver_License_Pic";
    public static final String make = "make";
    public static final String model = "model";
    public static final String social_security_number = "social_security_number";
    public static final String aterAccept_Check = "aterAccept_Check";

    public static final String fare = "fare";
    public static final String tax = "tax";
    public static final String totalBill = "totalBill";

    public static final String onlineOffline= "N";

     /*editor.putString(SplashScreen.car_number, car_number);
            editor.putString(SplashScreen.license_info, license_info);
            editor.putString(SplashScreen.make, make);
            editor.putString(SplashScreen.model, model);
            editor.putString(SplashScreen.social_security_number, social_security_number);
*/



    private static int TIME_OUT = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        preferences = getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);



        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(preferences.getString(DriverID,"").equalsIgnoreCase("")){

                    Intent i = new Intent(SplashScreen.this, LoginScreen.class);
                    startActivity(i);
                    finish();
                }
                else{

                    Intent i = new Intent(SplashScreen.this, HomeScreen.class);
                    startActivity(i);
                    finish();
                }
            }
        }, TIME_OUT);
    }
}
