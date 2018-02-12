package com.app.taxi.driver;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.app.taxi.driver.volleyapi.CommonFunctions;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class PaymentScreen extends AppCompatActivity {
    TextView userName, pickUpAdderess, destinationAddress, rideFare, surcharge, totalBill;
    public static CircleImageView paymentscreen_circleView;
SharedPreferences preferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_screen);
        preferences = getSharedPreferences(SplashScreen.PREFERENCES, Context.MODE_PRIVATE);
        userName = (TextView) findViewById(R.id.username);
        pickUpAdderess = (TextView) findViewById(R.id.pickup);
        destinationAddress = (TextView) findViewById(R.id.destination);
        rideFare = (TextView) findViewById(R.id.ride_amount);
        surcharge = (TextView) findViewById(R.id.tax);
        totalBill = (TextView) findViewById(R.id.total_bill);
        paymentscreen_circleView = (CircleImageView)findViewById(R.id.paymentscreen_circleView);
        userName.setText(preferences.getString(SplashScreen.USER_NAME,""));
        pickUpAdderess.setText(preferences.getString(SplashScreen.PICKUP_ADDRESS,""));
        destinationAddress.setText(preferences.getString(SplashScreen.DESTINATION_ADDRESS,""));
        rideFare.setText(preferences.getString(SplashScreen.fare,""));
        surcharge.setText(preferences.getString(SplashScreen.tax,""));
        totalBill.setText(preferences.getString(SplashScreen.totalBill,""));
        if (CommonFunctions.profilePicPath != null) {
            Picasso.with(getApplicationContext()).load(preferences.getString(SplashScreen.USER_IMAGE, "")).into(paymentscreen_circleView);
        } else {
            Picasso.with(getApplicationContext()).load(preferences.getString(SplashScreen.USER_IMAGE, "")).into(paymentscreen_circleView);
        }


    }

    public void paymentSwitch(View view) {
        switch (view.getId()) {
            case R.id.payment_btn:
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString(SplashScreen.USER_NAME,"");
                editor.putString(SplashScreen.USER_MOBILE,"");
                editor.putString(SplashScreen.USER_IMAGE,"");
                editor.putString(SplashScreen.REQUEST_USER_ID,"");
                editor.putString(SplashScreen.NOTIFICATION_TYPE,"");
                editor.putString(SplashScreen.PICKUP_LATITUDE,"");
                editor.putString(SplashScreen.PICKUP_LONGITUDE,"");
                editor.putString(SplashScreen.DESTINATION_LATITUDE,"");
                editor.putString(SplashScreen.DESTINATION_LONGITUDE,"");
                editor.putString(SplashScreen.PICKUP_ADDRESS,"");
                editor.putString(SplashScreen.DESTINATION_ADDRESS,"");
                editor.putString(SplashScreen.fare,"");
                editor.putString(SplashScreen.tax,"");
                editor.putString(SplashScreen.totalBill,"");
                editor.commit();
                Intent navIntent = new Intent(PaymentScreen.this, HomeScreen.class);
                startActivity(navIntent);
                finish();
                break;
        }

    }
}
