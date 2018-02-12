package com.app.taxi.driver;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;



import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.android.volley.VolleyError;
import com.app.taxi.driver.commonFunctions.NetworkUtil;
import com.app.taxi.driver.volleyapi.OnApihit;
import com.app.taxi.driver.volleyapi.VolleyBase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class RideHistory extends AppCompatActivity implements OnApihit {
    RecyclerView ride_history;
    RideHistoryAdapter historyAdapter;
    RelativeLayout back;
    SharedPreferences preferences;
    ArrayList<HashMap<String,String>> ride_data = new ArrayList<HashMap<String, String>>();
    HashMap<String, String> hmap;
String user_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_history);
preferences = getSharedPreferences(SplashScreen.PREFERENCES , Context.MODE_PRIVATE);
        back = (RelativeLayout) findViewById(R.id.back);
user_id = user_id = preferences.getString(SplashScreen.DriverID, "");
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(RideHistory.this, HomeScreen.class);
                i.putExtra("i", "");
                startActivity(i);
                finish();
            }
        });
        ride_history = (RecyclerView) findViewById(R.id.ride_list);

        LinearLayoutManager llm = new LinearLayoutManager(RideHistory.this,
                LinearLayoutManager.VERTICAL, false);
        ride_history.setLayoutManager(llm);


        Map<String, String> params = new HashMap<>();
        params.put("driver_id", user_id);

        if (NetworkUtil.getConnectivityStatus(RideHistory.this)) {
            new VolleyBase(RideHistory.this).main(params, "driverRidesHistory", 1);
        } else {
            NetworkUtil.openAlert(RideHistory.this);
        }

    }

    @Override
    public void success(String Response, int index) {
if (index==1){
    try {
        Log.e("RIDE HISTORY RESPONSE" ,Response.toString() );
        JSONObject jsonObject = new JSONObject(Response);
String status = jsonObject.getString("status");
String message = jsonObject.getString("message");

        JSONArray jsonArray = jsonObject.getJSONArray("result");
for(int i = 0 ; i<jsonArray.length();i++){

    JSONObject jobj = jsonArray.getJSONObject(i);
    hmap = new HashMap<>();
    String username = jobj.getString("username");
    String user_image = jobj.getString("user_image");
String bill = jobj.getString("bill");
String date = jobj.getString("date");
String pickup_location = jobj.getString("pickup_location");
String pickup_destination = jobj.getString("pickup_destination");
hmap.put("username" , username);
    hmap.put("user_image" ,user_image );
    hmap.put("bill" ,bill );
    hmap.put("date" ,date );
    hmap.put("pickup_location" ,pickup_location );
    hmap.put("pickup_destination" , pickup_destination);

    ride_data.add(hmap);

}

        historyAdapter = new RideHistoryAdapter(RideHistory.this , ride_data);
        ride_history.setAdapter(historyAdapter);

    } catch (JSONException e) {
        e.printStackTrace();
    }
}
    }

    @Override
    public void error(VolleyError error, int index) {

    }
}
