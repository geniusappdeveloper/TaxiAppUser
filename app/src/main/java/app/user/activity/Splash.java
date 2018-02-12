package app.user.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import app.taxiapp.user.R;

import static app.user.global.Global.pref_name;


public class Splash extends AppCompatActivity {
    private static int TIME_OUT = 3000;
    boolean gps_enabled = false;
    Handler handler;
    public Runnable mUpdateTimeTask;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    ProgressDialog progress_dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        progress_dialog = new ProgressDialog(Splash.this);

        sharedPreferences = getSharedPreferences(pref_name, Context.MODE_PRIVATE);
        LocationManager locManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // getting GPS status
        gps_enabled = locManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        Log.e("on","splash");


 if (!gps_enabled) {

            Toast.makeText(this, "Enable Location", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
            finish();
        } else {

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (sharedPreferences.getString("user_id", "").equalsIgnoreCase("")) {


                        Intent i = new Intent(Splash.this, Select.class);
                        startActivity(i);
                        finish();
                    } else {
                        Intent i = new Intent(Splash.this, Home.class);
                        i.putExtra("i", "");
                        startActivity(i);
                        finish();

                    }
                }
            }, TIME_OUT);

            //   Log.e("thread", "1");

          /*  mUpdateTimeTask = new Runnable() {

                @Override
                public void run() {
                    //   Log.e("thread", "2");
                    if (sharedPreferences.getString("user_id", "").equalsIgnoreCase("")) {
                        isRunning = false;
                        Log.e("thread", "2");
                    } else {
                        Log.e("thread", "3");
                    }
                    try {
                        isRunning = true;

                        if (isRunning) {
                            Log.e("thread", "splash");
                            if (mGoogleApiClient.isConnected()) {
                                startLocationUpdates();
                                Log.e("SPLASH", "Location update  .....................");
                            }
                        }

                    } catch (OutOfMemoryError ee) {
                        Toast.makeText(Splash.this, "" + ee, Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {

                        e.printStackTrace();
                    }
                    handler.postDelayed(this, 3000);
                }
            };

            handler = new Handler();
            handler.removeCallbacks(mUpdateTimeTask);
            handler.post(mUpdateTimeTask);*/
      }
    }





}

