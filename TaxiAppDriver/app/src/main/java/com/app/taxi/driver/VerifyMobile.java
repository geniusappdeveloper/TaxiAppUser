package com.app.taxi.driver;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.app.taxi.driver.commonFunctions.NetworkUtil;
import com.app.taxi.driver.volleyapi.OnApihit;
import com.app.taxi.driver.volleyapi.VolleyBase;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class VerifyMobile extends AppCompatActivity implements OnApihit {
    private Intent loginIntent, verifyIntent, profileIntent;
    TextView resendCode, changeNumber;
    RelativeLayout verifyback_layout;
    EditText verifyOtpEdit;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_mobile);
        preferences = getSharedPreferences(SplashScreen.PREFERENCES, Context.MODE_PRIVATE);
        verifyOtpEdit = (EditText) findViewById(R.id.verifymobile_otp_editbox);
        resendCode = (TextView) findViewById(R.id.resendcode_textbox);
        changeNumber = (TextView) findViewById(R.id.changenumber_textbox);
        verifyOtpEdit.setText(preferences.getString(SplashScreen.UniqueCode, ""));
    }

    public void verifyMobileBtn(View v) {

        switch (v.getId()) {
            case R.id.verifymobile_button:


                Map<String, String> postparams = new HashMap<>();
                postparams.put("user_id", preferences.getString(SplashScreen.DriverID, ""));
                postparams.put("unique_code", preferences.getString(SplashScreen.UniqueCode, ""));
                Log.e("verify param", postparams.toString());
                if (NetworkUtil.getConnectivityStatus(VerifyMobile.this)) {
                    new VolleyBase(VerifyMobile.this).main(postparams, "verifyCode", 1);
                } else {
                    NetworkUtil.openAlert(VerifyMobile.this);
                }


                break;

            case R.id.verifymobile_back_layout:

                verifyIntent = new Intent(VerifyMobile.this, LoginScreen.class);
                /*loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);*/
                startActivity(verifyIntent);
                finish();
                break;

            case R.id.resendcode_textbox:
                Map<String, String> params = new HashMap<>();
                params.put("user_id", preferences.getString(SplashScreen.DriverID, ""));
                if (NetworkUtil.getConnectivityStatus(VerifyMobile.this)) {
                    new VolleyBase(VerifyMobile.this).main(params, "ResendCode", 2);
                } else {
                    NetworkUtil.openAlert(VerifyMobile.this);
                }


        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        loginIntent = new Intent(VerifyMobile.this, LoginScreen.class);
        startActivity(loginIntent);
        finish();

    }

    @Override
    public void success(String Response, int index) {


        Log.e(" verify response", Response);
        if (index == 1) {
            JSONObject jsonObject = null;
            try {

                jsonObject = new JSONObject(Response);
                String status = jsonObject.getString("status");
                String message = jsonObject.getString("message");
                if (status.equalsIgnoreCase("1")) {
                    Log.e(" verify response", Response);
                    JSONObject jObj = jsonObject.getJSONObject("result");
                    Log.e("unique verify", preferences.getString(SplashScreen.UniqueCode, ""));
                  /*String unique_code = jObj.getString("unique_code");

                    SharedPreferences.Editor edit = preferences.edit();
                    edit.putString(SplashScreen.UniqueCode , unique_code);
                    edit.commit();*/
                    Toast.makeText(VerifyMobile.this, preferences.getString(SplashScreen.UniqueCode, ""), Toast.LENGTH_SHORT).show();
                    Toast.makeText(VerifyMobile.this, message, Toast.LENGTH_LONG).show();


                    profileIntent = new Intent(VerifyMobile.this, ProfileScreen.class);
                    startActivity(profileIntent);
                    finish();


                } else {
                    Toast.makeText(VerifyMobile.this, message, Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (index == 2) {
            Log.e("resendcode", Response);
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(Response);
                String status = jsonObject.getString("status");
                String message = jsonObject.getString("message");
                if (status.equalsIgnoreCase("1")) {
                    Toast.makeText(VerifyMobile.this, message, Toast.LENGTH_LONG).show();
                    JSONObject jObj = jsonObject.getJSONObject("result");
                    String unique_code = jObj.getString("unique_code");
                    SharedPreferences.Editor edit = preferences.edit();
                    edit.putString(SplashScreen.UniqueCode, unique_code);
                    edit.commit();
                    verifyOtpEdit.setText(preferences.getString(SplashScreen.UniqueCode, ""));
                    Toast.makeText(VerifyMobile.this, unique_code, Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(VerifyMobile.this, message, Toast.LENGTH_LONG).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void error(VolleyError error, int index) {

    }
}
