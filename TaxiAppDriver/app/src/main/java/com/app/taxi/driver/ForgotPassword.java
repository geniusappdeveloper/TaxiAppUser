package com.app.taxi.driver;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.app.taxi.driver.volleyapi.CommonFunctions;

public class ForgotPassword extends AppCompatActivity {
    private Intent loginIntent;
    EditText emailVerify;
    String strEmailVerify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        emailVerify = (EditText) findViewById(R.id.forgotpass_email_editbox);
    }

    public void forgotBack(View v) {
        switch (v.getId()) {
            case R.id.forgot_passwordback_layout:
                loginIntent = new Intent(ForgotPassword.this, LoginScreen.class);
                /*loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);*/
                startActivity(loginIntent);
                finish();
        }

    }

    public void forgotPassBtn(View v) {
        strEmailVerify = emailVerify.getText().toString().trim();
        switch (v.getId()) {
            case R.id.forgotpassword_button:
                if (strEmailVerify.equalsIgnoreCase("")) {
                    emailVerify.setError("Enter your email to verify");
                    CommonFunctions.requestFocus(emailVerify, ForgotPassword.this);
                } else if (!(isValidEmail(strEmailVerify))) {
                    emailVerify.setError("Enter valid email");
                    CommonFunctions.requestFocus(emailVerify, ForgotPassword.this);
                }
        }
    }

    public boolean isValidEmail(CharSequence target) {
        if (TextUtils.isEmpty(target)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }
}
