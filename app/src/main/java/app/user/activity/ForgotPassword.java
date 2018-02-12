package app.user.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.android.volley.VolleyError;

import java.util.HashMap;
import java.util.Map;

import app.taxiapp.user.R;
import app.user.global.Global;
import app.user.global.NetworkUtil;
import app.user.volleyapi.OnApihit;
import app.user.volleyapi.VolleyBase;


public class ForgotPassword extends AppCompatActivity implements OnApihit {
    EditText email;
    Button reset;
    RelativeLayout back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        email = (EditText) findViewById(R.id.f_email);
        reset = (Button) findViewById(R.id.reset);
        back = (RelativeLayout) findViewById(R.id.back);


        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (email.getText().toString().equalsIgnoreCase("")) {
                    email.setError("Enter email");
                } else if (!email.getText().toString().matches(Global.emailPattern)) {
                    email.setError("Enter a valid email");
                } else {
                    Map<String, String> params = new HashMap<>();

                    params.put("email", email.getText().toString());

                    if (NetworkUtil.getConnectivityStatus(ForgotPassword.this)) {
                        new VolleyBase(ForgotPassword.this).main(params, "ForgetPassword", 1);
                    } else {
                        NetworkUtil.openAlert(ForgotPassword.this);
                    }
                }
            }
        });


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(ForgotPassword.this, Login.class);
                startActivity(i);
                finish();
            }
        });
    }

    @Override
    public void success(String Response, int index) {
        Log.e("FORGOT ", "RESPONSE " + Response + index);
    }

    @Override
    public void error(VolleyError error, int index) {

    }
}
