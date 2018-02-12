package com.app.taxi.driver;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.app.taxi.driver.commonFunctions.NetworkUtil;
import com.app.taxi.driver.volleyapi.CommonFunctions;
import com.app.taxi.driver.volleyapi.OnApihit;
import com.app.taxi.driver.volleyapi.VolleyBase;
import com.hbb20.CountryCodePicker;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegisterScreen extends AppCompatActivity implements OnApihit {
    CountryCodePicker registerccp;
    EditText editFirstName, editLastName, editEmail, editPhoneNum, editPassword, editconfirmPass;
    String strFirstName, strLastName, strEmail, strPhoneNum, strPassword, strconfirmPass, strStatus, strMessage, strUniqueCode,
            strUserId;
    TextInputLayout inputLayoutFirstName, inputLayoutLastName, inputLayoutEmail, inputLayoutPhoneNum,
            inputLayoutPassword, inputLayoutconfirmPass;
    Intent verifyMobile, loginIntent;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_screen);
        preferences = getSharedPreferences(SplashScreen.PREFERENCES, Context.MODE_PRIVATE);
        registerccp = (CountryCodePicker) findViewById(R.id.register_ccp);
        registerccp.hideNameCode(true);
        editFirstName = (EditText) findViewById(R.id.firstname_editbox);
        editLastName = (EditText) findViewById(R.id.lastname_editbox);
        editEmail = (EditText) findViewById(R.id.register_email_editbox);
        editPhoneNum = (EditText) findViewById(R.id.register_phonenumber_editbox);
        editPassword = (EditText) findViewById(R.id.registerpassword_editbox);
        editconfirmPass = (EditText) findViewById(R.id.register_confirmpassword_editbox);

    }

    public void register(View v) {
        switch (v.getId()) {
            case R.id.register_button:
                strFirstName = editFirstName.getText().toString().trim();
                strLastName = editLastName.getText().toString().trim();
                strEmail = editEmail.getText().toString().trim();
                strPhoneNum = editPhoneNum.getText().toString().trim();
                strPassword = editPassword.getText().toString().trim();
                strconfirmPass = editconfirmPass.getText().toString().trim();

                if (strFirstName.equalsIgnoreCase("")) {
                    editFirstName.setError("Enter First Name");
                    CommonFunctions.requestFocus(editFirstName, RegisterScreen.this);
                } else if (strLastName.equalsIgnoreCase("")) {
                    editLastName.setError("Enter Last Name");
                    CommonFunctions.requestFocus(editLastName, RegisterScreen.this);
                } else if (strEmail.equalsIgnoreCase("")) {
                    editEmail.setError("Enter Email");
                    CommonFunctions.requestFocus(editEmail, RegisterScreen.this);
                } else if (!(CommonFunctions.isValidEmail(strEmail))) {
                    editEmail.setError("Email Is Not Valid");
                    CommonFunctions.requestFocus(editEmail, RegisterScreen.this);
                } else if (!(CommonFunctions.isValidPhone(strPhoneNum))) {
                    editPhoneNum.setError("Enter valid phone number");
                    CommonFunctions.requestFocus(editPhoneNum, RegisterScreen.this);
                } else if (!(strPhoneNum.length() >= 7 && strPhoneNum.length() <= 15)) {
                    editPhoneNum.setError("Phone Number not valid");
                    CommonFunctions.requestFocus(editPhoneNum, RegisterScreen.this);
                } else if (strPhoneNum.equalsIgnoreCase("")) {
                    ;
                    editPassword.setError("Enter Phone Number");
                    CommonFunctions.requestFocus(editPhoneNum, RegisterScreen.this);
                } else if (strPassword.equalsIgnoreCase("")) {
                    editPassword.setError("Enter Password");
                    CommonFunctions.requestFocus(editPassword, RegisterScreen.this);
                } else if (!(strPassword.length() >= 6 && strPassword.length() <= 20)) {
                    editPassword.setError("Password must be between 6-20");
                    CommonFunctions.requestFocus(editPassword, RegisterScreen.this);
                } else if (strconfirmPass.equalsIgnoreCase("")) {
                    editconfirmPass.setError("Enter Confirm Password");
                    CommonFunctions.requestFocus(editconfirmPass, RegisterScreen.this);
                } else if (!(strconfirmPass.equals(strPassword))) {
                    editconfirmPass.setError("Password Doesn't Match");
                    CommonFunctions.requestFocus(editconfirmPass, RegisterScreen.this);
                } else {

                    Map<String, String> params = new HashMap<>();
                    params.put("email", strEmail);
                    params.put("first_name", strFirstName);
                    params.put("last_name", strLastName);
                    params.put("password", strPassword);
                    params.put("country_code", registerccp.getSelectedCountryCodeWithPlus().trim());
                    params.put("phone_number", strPhoneNum);
                    params.put("user_type", "D");
                    if (NetworkUtil.getConnectivityStatus(RegisterScreen.this)) {
                        new VolleyBase(RegisterScreen.this).main(params, "register", 1);
                    } else {
                        NetworkUtil.openAlert(RegisterScreen.this);
                    }


                }
                break;
            case R.id.register_back_layout:
                loginIntent = new Intent(RegisterScreen.this, LoginScreen.class);
                loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                startActivity(loginIntent);
                finish();

        }
    }


    @Override
    public void success(String Response, int index) {
        Log.e("Register response", Response);
        try {
            JSONObject jsonObject = new JSONObject(Response);
            strStatus = jsonObject.getString("status");
            strMessage = jsonObject.getString("message");
            if (strStatus.equalsIgnoreCase("1")) {
                JSONObject jsonobj = jsonObject.getJSONObject("result");
                strUserId = jsonobj.getString("user_id");
                strUniqueCode = jsonobj.getString("unique_code");

                //_________________________________ ADDING IN SHARED PREFERENCES

                SharedPreferences.Editor editor = preferences.edit();
                editor.putString(SplashScreen.DriverFirstName, strFirstName); // firstname
                editor.putString(SplashScreen.DriverLastName, strLastName);//lastname
                editor.putString(SplashScreen.DriverPhoneNumber, strPhoneNum); // phone number of the driver
                editor.putString(SplashScreen.DriverEmail, strEmail);//email
                editor.putString(SplashScreen.CountryCode, registerccp.getSelectedCountryCodeWithPlus().trim());
                editor.putString(SplashScreen.UniqueCode, strUniqueCode); // otp
                editor.putString(SplashScreen.DriverID, strUserId); //Id of driver
                editor.putString(SplashScreen.UserType , "D");
                editor.commit();

                Log.e("firstname", preferences.getString(SplashScreen.DriverFirstName, ""));
                Log.e("lastname", preferences.getString(SplashScreen.DriverLastName, ""));
                Log.e("phone number", preferences.getString(SplashScreen.DriverPhoneNumber, ""));
                Log.e("email", preferences.getString(SplashScreen.DriverEmail, ""));
                Log.e("country code", preferences.getString(SplashScreen.CountryCode, ""));
                Log.e("unique id", preferences.getString(SplashScreen.UniqueCode, ""));
                Log.e("Driver id", preferences.getString(SplashScreen.DriverID, ""));


                Toast.makeText(RegisterScreen.this, strMessage, Toast.LENGTH_LONG).show();
                verifyMobile = new Intent(RegisterScreen.this, VerifyMobile.class);
                verifyMobile.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(verifyMobile);
            } else {
                Toast.makeText(RegisterScreen.this, strMessage, Toast.LENGTH_LONG).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void error(VolleyError error, int index) {

    }
}
