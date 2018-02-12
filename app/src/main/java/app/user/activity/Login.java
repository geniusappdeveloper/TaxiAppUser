package app.user.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.hbb20.CountryCodePicker;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import app.taxiapp.user.R;
import app.user.global.Global;
import app.user.global.NetworkUtil;
import app.user.volleyapi.OnApihit;
import app.user.volleyapi.VolleyBase;


public class Login extends AppCompatActivity implements OnApihit {
    CountryCodePicker ccp;
    TextView forgotpassword, register;
    Intent nextscreen;
    Button login;
    EditText phone, password;
    RelativeLayout back;
    ProgressDialog progress_dialog;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        sharedPreferences = getSharedPreferences(Global.pref_name, Context.MODE_PRIVATE);
        progress_dialog = new ProgressDialog(Login.this);

        ccp = (CountryCodePicker) findViewById(R.id.code);
        ccp.hideNameCode(true);

        forgotpassword = (TextView) findViewById(R.id.forgotpassword);
        back = (RelativeLayout) findViewById(R.id.back);
        login = (Button) findViewById(R.id.login_button);
        phone = (EditText) findViewById(R.id.phone_number);
        password = (EditText) findViewById(R.id.loginpassword);


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //--------VALIDATIONS--------------------------------
                if (phone.getText().toString().equalsIgnoreCase("")) {
                    phone.setError("Please enter phone number");
                    requestFocus(phone);
                } else if (!phone.getText().toString().matches(Global.numberPattern)) {
                    phone.setError("Enter a valid phone number");
                    requestFocus(phone);
                } else if (phone.getText().toString().length() > 15 || phone.getText().toString().length() < 7) {
                    phone.setError("Enter a valid phone number");
                    requestFocus(phone);
                } else if (password.getText().toString().equalsIgnoreCase("")) {
                    password.setError("Please enter password");
                    requestFocus(password);
                } else if (password.getText().toString().length() > 20 || password.getText().toString().length() < 6) {
                    password.setError("Password must be between 6 to 20 characters");
                    requestFocus(password);
                } else {
                    progress_dialog.show();

                    Map<String, String> params = new HashMap<>();

                    params.put("country_code", ccp.getSelectedCountryCodeWithPlus().trim());
                    params.put("phone_number", phone.getText().toString().trim());
                    params.put("password", password.getText().toString().trim());
                    params.put("user_type", "U");

                    if (NetworkUtil.getConnectivityStatus(Login.this)) {
                        new VolleyBase(Login.this).main(params, "login", 1);
                    } else {
                        NetworkUtil.openAlert(Login.this);
                    }
                }

            }
        });

//-----------------------------
        forgotpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Login.this, ForgotPassword.class);
                startActivity(i);
            }
        });


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Login.this, Select.class);
                startActivity(i);
                finish();
            }
        });
    }


    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    @Override
    public void success(String Response, int index) {

        Log.e("LOGIN ", "RESPONSE " + Response + index);
        if (progress_dialog.isShowing()) {
            progress_dialog.dismiss();
        }
        try {

            JSONObject json = new JSONObject(Response);

            String status = json.getString("status");
            String message = json.getString("message");


            //    {"status":"1","message":"success","result":{"id":7,"first_name":"jyotika","last_name":"joshi","phone_number":"9417809543","country_code":"+501","user_type":"U","is_verified":"Y"}}1
            if (status.equalsIgnoreCase("1")) {

                JSONObject j = new JSONObject(json.getString("result"));
                String user_id = j.getString("id");
                String firstname = j.getString("first_name");
                String last_name = j.getString("last_name");
                String phone_number = j.getString("phone_number");
                String country_code = j.getString("country_code");
                String user_type = j.getString("user_type");
                String is_verified = j.getString("is_verified");

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("user_id", user_id);
                editor.putString("name", firstname+" "+last_name);
                editor.putString("email","");
                editor.putString("number", phone_number);
                editor.apply();
                editor.commit();


                if (is_verified.equals("Y")) {
                    Intent i = new Intent(Login.this, Home.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    i.putExtra("i", "");
                    startActivity(i);
                    finish();
                } else {
                    Intent i = new Intent(Login.this, VerifyMobile.class);
                    i.putExtra("i", "");
                    i.putExtra("Number", ccp.getSelectedCountryCodeWithPlus().trim() + phone.getText().toString().trim());
                    startActivity(i);
                }
            }
            Global.toast(Login.this, message);
        } catch (JSONException e) {
        }
    }

    @Override
    public void error(VolleyError error, int index) {
        Log.e("LOGIN ", "error " + error);
        if (progress_dialog.isShowing()) {
            progress_dialog.dismiss();
        }
    }
}
