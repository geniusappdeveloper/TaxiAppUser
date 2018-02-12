package com.app.taxi.driver;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.app.taxi.driver.commonFunctions.NetworkUtil;
import com.app.taxi.driver.commonFunctions.NewProgressBar;
import com.app.taxi.driver.volleyapi.CommonFunctions;
import com.app.taxi.driver.volleyapi.OnApihit;
import com.app.taxi.driver.volleyapi.VolleyBase;
import com.hbb20.CountryCodePicker;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class LoginScreen extends AppCompatActivity implements OnApihit {

    private SharedPreferences preferences;
    CountryCodePicker ccp;
    private ProgressDialog dialog;
    TextView forgotpassword, register_account;
    Intent nextscreen;
    EditText editphonenum, editpassword;
    TextInputLayout inputLayoutPassword;
    String strLoginPhonenum, strLoginPassword, strStatus, strMessage,
            strUserId, strFirstName, strLastName, strPhoneNum, strCountryCode, strUserType, strVerified, strProfilepic,strEmail;
NewProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);
        ccp = (CountryCodePicker) findViewById(R.id.ccp);
        ccp.hideNameCode(true);
        preferences = getSharedPreferences(SplashScreen.PREFERENCES, Context.MODE_PRIVATE);
        forgotpassword = (TextView) findViewById(R.id.login_forgotpassword_text);
        register_account = (TextView) findViewById(R.id.register_account_textview);
        editphonenum = (EditText) findViewById(R.id.login_phonenumber_editbox);
        editpassword = (EditText) findViewById(R.id.loginpassword_editbox);
        inputLayoutPassword = (TextInputLayout) findViewById(R.id.loginpassword_floatingtext);


    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_button:
                strLoginPhonenum = editphonenum.getText().toString().trim();
                strLoginPassword = editpassword.getText().toString().trim();

                if (strLoginPhonenum.equalsIgnoreCase("")) {
                    editphonenum.setError("Enter Phone number");
                    CommonFunctions.requestFocus(editphonenum, LoginScreen.this);
                } else if (!(CommonFunctions.isValidPhone(strLoginPhonenum))) {
                    editphonenum.setError("Enter valid phone number");
                    CommonFunctions.requestFocus(editphonenum, LoginScreen.this);
                } else if (!(strLoginPhonenum.length() >= 7 && strLoginPhonenum.length() <= 15)) {
                    editphonenum.setError("phoneNumber not valid");
                    CommonFunctions.requestFocus(editphonenum, LoginScreen.this);
                } else if (strLoginPassword.equalsIgnoreCase("")) {
                    editpassword.setError("enter password");
                    CommonFunctions.requestFocus(editpassword, LoginScreen.this);
                } else if (!(strLoginPassword.length() >= 6 && strLoginPassword.length() <= 20)) {
                    editpassword.setError("Password must be between 6-20");
                    CommonFunctions.requestFocus(editpassword, LoginScreen.this);
                } else {

                    Map<String, String> params = new HashMap<>();

                    params.put("country_code", ccp.getSelectedCountryCodeWithPlus().trim()); //sending countrycode
                    params.put("phone_number", strLoginPhonenum); // sending phone num
                    params.put("password", strLoginPassword);//sending password
                    params.put("user_type", "D"); //  D is user for the driver
                    if (NetworkUtil.getConnectivityStatus(LoginScreen.this)) {
                       progressBar = new NewProgressBar(LoginScreen.this);
                       progressBar.show();
                        new VolleyBase(LoginScreen.this).main(params, "login", 1);

                       /* nextscreen = new Intent(LoginScreen.this, HomeScreen.class);
                        nextscreen.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(nextscreen);*/
                    } else {
                        NetworkUtil.openAlert(LoginScreen.this);
                    }

                }
                break;
            case R.id.login_forgotpassword_text:
                nextscreen = new Intent(LoginScreen.this, ForgotPassword.class);
                startActivity(nextscreen);
                break;

            case R.id.register_account_textview:
                nextscreen = new Intent(LoginScreen.this, RegisterScreen.class);
                startActivity(nextscreen);
                break;

        }
    }


    @Override
    public void success(String Response, int index) {
        Log.e("LOGIN", "RESPONSE " + Response);
        try {
            JSONObject jsonObject = new JSONObject(Response);
            strStatus = jsonObject.getString("status");
            strMessage = jsonObject.getString("message");
            if (strStatus.equalsIgnoreCase("1")) {
                JSONObject jObject = jsonObject.getJSONObject("result");

                strFirstName = jObject.getString("first_name");
                strLastName = jObject.getString("last_name");
                strPhoneNum = jObject.getString("phone_number");
                strCountryCode = jObject.getString("country_code");
                strUserId = jObject.getString("id");
                strUserType = jObject.getString("user_type");
                strVerified = jObject.getString("is_verified");
                strProfilepic   = jObject.getString("profile_pic") ;
                strEmail = jObject.getString("email");

                SharedPreferences.Editor editor = preferences.edit();
                editor.putString(SplashScreen.DriverFirstName, strFirstName);
                editor.putString(SplashScreen.DriverLastName, strLastName);
                editor.putString(SplashScreen.DriverPhoneNumber, strPhoneNum);
                editor.putString(SplashScreen.CountryCode, strCountryCode);
                editor.putString(SplashScreen.DriverID, strUserId);
                editor.putString(SplashScreen.DriverVerify, strVerified);
                editor.putString(SplashScreen.UserType, strUserType);
                editor.putString(SplashScreen.Driver_PIC_PATH , strProfilepic);
                editor.putString(SplashScreen.DriverEmail , strEmail);
                editor.commit();
                Log.e("driver profile image" , strProfilepic);
                Log.e("Email" , strEmail);
                //  Toast.makeText(LoginScreen.this, strMessage, Toast.LENGTH_LONG).show();
                Toasty.success(LoginScreen.this , "Successfully logged in ",Toast.LENGTH_SHORT).show();
                nextscreen = new Intent(LoginScreen.this, HomeScreen.class);
                nextscreen.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                progressBar.dismiss();
                startActivity(nextscreen);
            } else {
                progressBar.dismiss();
                //Toast.makeText(LoginScreen.this, strMessage, Toast.LENGTH_LONG).show();
                Toasty.error(LoginScreen.this , strMessage,Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void error(VolleyError error, int index) {

    }
}
