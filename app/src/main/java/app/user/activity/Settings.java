package app.user.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import app.taxiapp.user.R;
import app.user.global.Global;

public class Settings extends AppCompatActivity {
    RelativeLayout back;
    TextView logout,user_name,user_email,user_number;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        sharedPreferences = getSharedPreferences(Global.pref_name, Context.MODE_PRIVATE);

        back = (RelativeLayout) findViewById(R.id.back);
        logout = (TextView) findViewById(R.id.logout);
        user_name = (TextView) findViewById(R.id.user_name);
        user_email = (TextView) findViewById(R.id.user_email);
        user_number = (TextView) findViewById(R.id.user_number);



        user_name.setText(sharedPreferences.getString("name",""));
        user_email.setText(sharedPreferences.getString("email",""));
        user_number.setText(sharedPreferences.getString("number",""));


        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getSharedPreferences(Global.pref_name, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.commit();
                Intent i = new Intent(Settings.this, Select.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                finish();
            }
        });


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(Settings.this, Home.class);
                i.putExtra("i", "");
                startActivity(i);
                finish();
            }
        });

    }
}
