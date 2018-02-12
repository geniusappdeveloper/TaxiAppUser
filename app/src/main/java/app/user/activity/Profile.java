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

import com.android.volley.VolleyError;
import com.hbb20.CountryCodePicker;

import java.util.HashMap;
import java.util.Map;

import app.taxiapp.user.R;
import app.user.global.Global;
import app.user.global.NetworkUtil;
import app.user.volleyapi.OnApihit;
import app.user.volleyapi.VolleyBase;

public class Profile extends AppCompatActivity implements OnApihit {

    CountryCodePicker registerccp;
    Button save;
    EditText firstname, lastname, email, password, c_password, phone;
    RelativeLayout back;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    ProgressDialog progress_dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_profile);

        back = (RelativeLayout) findViewById(R.id.back);
        sharedPreferences = getSharedPreferences(Global.pref_name, Context.MODE_PRIVATE);
        progress_dialog = new ProgressDialog(Profile.this);

        registerccp = (CountryCodePicker) findViewById(R.id.register_ccp);
        registerccp.hideNameCode(true);

        save = (Button) findViewById(R.id.save);
        firstname = (EditText) findViewById(R.id.firstname);
        lastname = (EditText) findViewById(R.id.lastname);
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        c_password = (EditText) findViewById(R.id.confirmpassword);
        phone = (EditText) findViewById(R.id.phone);
        back = (RelativeLayout) findViewById(R.id.back);


        //------------CLICKS----------

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //--------VALIADTIONS-----------
                if (firstname.getText().toString().equalsIgnoreCase("")) {
                    firstname.setError("Please enter firstname");
                    requestFocus(firstname);
                } else if (!firstname.getText().toString().matches(Global.namePattern)) {
                    firstname.setError("Enter a valid firstname");
                    requestFocus(firstname);
                } else if (firstname.getText().toString().length() > 26) {
                    firstname.setError("Firstname must be less than 26 characters");
                    requestFocus(firstname);
                } else if (!lastname.getText().toString().matches(Global.namePattern)) {
                    lastname.setError("Enter a valid lastname");
                    requestFocus(lastname);
                } else if (lastname.getText().toString().length() > 26) {
                    lastname.setError("Lastname must be less than 26 characters");
                    requestFocus(lastname);
                } else if (email.getText().toString().equalsIgnoreCase("")) {
                    email.setError("Please enter email");
                    requestFocus(email);
                } else if (!email.getText().toString().matches(Global.emailPattern)) {
                    email.setError("Enter a valid email");
                    requestFocus(email);
                } else if (phone.getText().toString().equalsIgnoreCase("")) {
                    phone.setError("Please enter phone number");
                    requestFocus(phone);
                } else if (!phone.getText().toString().matches(Global.numberPattern)) {
                    phone.setError("Enter a valid phone number");
                    requestFocus(phone);
                } else if (phone.getText().toString().length() > 10 || phone.getText().toString().length() < 7) {
                    phone.setError("Enter a valid phone number");
                    requestFocus(phone);
                } else if (password.getText().toString().equalsIgnoreCase("")) {
                    password.setError("Please enter password");
                    requestFocus(password);
                } else if (password.getText().toString().length() > 20 || password.getText().toString().length() < 8) {
                    password.setError("Password must be between 8 to 20 characters");
                    requestFocus(password);
                } else if (c_password.getText().toString().equalsIgnoreCase("")) {
                    c_password.setError("Please confirm password");
                    requestFocus(c_password);
                } else if (!c_password.getText().toString().matches(password.getText().toString())) {
                    c_password.setError("Password does not match");
                    requestFocus(c_password);
                } else {
                    progress_dialog.show();
                    Map<String, String> params = new HashMap<>();
                    params.put("email", email.getText().toString().trim());
                    params.put("first_name", firstname.getText().toString().trim());
                    params.put("last_name", lastname.getText().toString().trim());
                    params.put("password", password.getText().toString().trim());
                    params.put("country_code", registerccp.getSelectedCountryCodeWithPlus().toString().trim());
                    params.put("phone_number", phone.getText().toString().trim());
                    params.put("user_type", "U");

                    Log.e("Register", "params " + params);
                    if (NetworkUtil.getConnectivityStatus(Profile.this)) {
                        new VolleyBase(Profile.this).main(params, "", 1);
                    } else {
                        NetworkUtil.openAlert(Profile.this);
                    }
                }
            }
        });


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(Profile.this, Home.class);
                i.putExtra("i", "");
                startActivity(i);
                finish();
            }
        });
    }

    @Override
    public void success(String Response, int index) {
        Log.e("RESPONSE", "PROFILE " + Response);
    }

    @Override
    public void error(VolleyError error, int index) {

    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }
}
