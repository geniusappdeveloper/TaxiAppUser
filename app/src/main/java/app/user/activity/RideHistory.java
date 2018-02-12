package app.user.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;

import com.android.volley.VolleyError;

import app.taxiapp.user.R;
import app.user.adapter.RideHistoryAdapter;
import app.user.volleyapi.OnApihit;

public class RideHistory extends AppCompatActivity implements OnApihit{
    RecyclerView ride_history;
    RideHistoryAdapter historyAdapter;
    RelativeLayout back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_history);

        back = (RelativeLayout) findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(RideHistory.this, Home.class);
                i.putExtra("i", "");
                startActivity(i);
                finish();
            }
        });


        ride_history = (RecyclerView) findViewById(R.id.ride_list);
        historyAdapter = new RideHistoryAdapter();
        LinearLayoutManager llm = new LinearLayoutManager(RideHistory.this, LinearLayoutManager.VERTICAL, false);
        ride_history.setLayoutManager(llm);
        ride_history.setAdapter(historyAdapter);

    }

    @Override
    public void success(String Response, int index) {

    }

    @Override
    public void error(VolleyError error, int index) {

    }
}
