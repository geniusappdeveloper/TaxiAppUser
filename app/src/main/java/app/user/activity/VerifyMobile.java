package app.user.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import app.taxiapp.user.R;
import app.user.global.Global;
import app.user.global.NetworkUtil;
import app.user.volleyapi.OnApihit;
import app.user.volleyapi.VolleyBase;


public class VerifyMobile extends AppCompatActivity implements OnApihit {
    TextView set_phone, changenumber, resend;
    EditText otp;
    Button submit;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_mobile);

        sharedPreferences = getSharedPreferences(Global.pref_name, Context.MODE_PRIVATE);

        set_phone = (TextView) findViewById(R.id.set_phone);
        changenumber = (TextView) findViewById(R.id.changenumber);
        resend = (TextView) findViewById(R.id.resend);
        otp = (EditText) findViewById(R.id.otp);
        submit = (Button) findViewById(R.id.submit);

        set_phone.setText(getIntent().getStringExtra("Number"));

        final Dialog dialog = new Dialog(VerifyMobile.this, android.R.style.Theme_Translucent_NoTitleBar);
        dialog.setContentView(R.layout.dialog);
        dialog.show();

        final TextView ok = (TextView) dialog.findViewById(R.id.ok);

        TextView txt_otp = (TextView) dialog.findViewById(R.id.txt_otp);

        txt_otp.setText("OTP Is "+getIntent().getStringExtra("Otp"));

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();

            }
        });


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Map<String, String> params = new HashMap<>();

                params.put("user_id", sharedPreferences.getString("user_id", ""));
                params.put("unique_code", otp.getText().toString().trim());
                if (NetworkUtil.getConnectivityStatus(VerifyMobile.this)) {
                    new VolleyBase(VerifyMobile.this).main(params, "verifyCode", 1);
                } else {
                    NetworkUtil.openAlert(VerifyMobile.this);
                }

            }
        });

        resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", sharedPreferences.getString("user_id", ""));

                if (NetworkUtil.getConnectivityStatus(VerifyMobile.this)) {
                    new VolleyBase(VerifyMobile.this).main(params, "ResendCode", 2);
                } else {
                    NetworkUtil.openAlert(VerifyMobile.this);
                }
            }
        });

    }

    @Override
    public void success(String Response, int index) {
        Log.e("REGISTER", " Response " + Response + index);

        if (index == 1) {
            try {
                JSONObject jsonObject = new JSONObject(Response);
                String status = jsonObject.getString("status");
                String message = jsonObject.getString("message");


                if (status.equalsIgnoreCase("1")) {
                    Intent intent = new Intent(VerifyMobile.this, Home.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("i", "");
                    startActivity(intent);
                    finish();
                } else if (status.equalsIgnoreCase("0")) {
                    Global.toast(VerifyMobile.this, message);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else if (index == 2) {
            try {
                JSONObject jsonObject = new JSONObject(Response);
                String status = jsonObject.getString("status");
                String message = jsonObject.getString("message");

                Global.toast(VerifyMobile.this, message);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void error(VolleyError error, int index) {

    }
}
