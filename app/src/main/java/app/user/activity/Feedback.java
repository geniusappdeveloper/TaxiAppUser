package app.user.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;

import java.util.HashMap;
import java.util.Map;

import app.taxiapp.user.R;
import app.user.global.Global;
import app.user.global.NetworkUtil;
import app.user.volleyapi.OnApihit;
import app.user.volleyapi.VolleyBase;

public class Feedback extends AppCompatActivity implements OnApihit {
    RelativeLayout back;
    RatingBar minimumRating;
    Button rate, help;
    SharedPreferences sharedPreferences;
    EditText comment;
    String date, start, end, request_id;
    TextView final_pickup, final_drop, ride_details;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        sharedPreferences = getSharedPreferences(Global.pref_name, Context.MODE_PRIVATE);
        back = (RelativeLayout) findViewById(R.id.back);
        minimumRating = (RatingBar) findViewById(R.id.myRatingBar);
        rate = (Button) findViewById(R.id.rate);
        help = (Button) findViewById(R.id.help);
        comment = (EditText) findViewById(R.id.comment);
        final_pickup = (TextView) findViewById(R.id.final_pickup);
        final_drop = (TextView) findViewById(R.id.final_drop);
        ride_details = (TextView) findViewById(R.id.ride_details);

        date = getIntent().getStringExtra("date");
        start = getIntent().getStringExtra("start");
        end = getIntent().getStringExtra("end");
        request_id = getIntent().getStringExtra("request_id");

        final_pickup.setText(start);
        final_drop.setText(end);
        ride_details.setText(date);


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(Feedback.this, Home.class);
                i.putExtra("i", "");
                startActivity(i);
                finish();
            }
        });


        rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, String> params = new HashMap<>();

                params.put("user_id", sharedPreferences.getString("user_id", ""));
                params.put("rating", String.valueOf(minimumRating.getRating()));
                params.put("request_id", request_id);
                params.put("comment", comment.getText().toString());

                Log.e("feedback params", " print  " + params);

                if (NetworkUtil.getConnectivityStatus(Feedback.this)) {

                    new VolleyBase(Feedback.this).main(params, "feedback", 1);


                } else {
                    NetworkUtil.openAlert(Feedback.this);
                }
            }
        });

        minimumRating.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent event) {
                float touchPositionX = event.getX();
                float width = minimumRating.getWidth();
                float starsf = (touchPositionX / width) * 5.0f;
                int stars = (int) starsf + 1;
                minimumRating.setRating(stars);
                return true;
            }
        });
    }

    @Override
    public void success(String Response, int index) {
        Log.e("RESPONSE", "FEEDBACK" + Response);

        Intent i = new Intent(Feedback.this, Home.class);
        i.putExtra("i", "");
        startActivity(i);
        finish();
    }

    @Override
    public void error(VolleyError error, int index) {

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(Feedback.this, Home.class);
        i.putExtra("i", "");
        startActivity(i);
        finish();
    }
}
