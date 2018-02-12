package app.user.webservices;

import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import app.taxiapp.user.R;
import app.user.activity.Home;
import app.user.global.Global;

public class CheckRequestStatus {
    String message;
    SharedPreferences.Editor editor;
    SharedPreferences sharedPreferences;

    public void check(final Context context, final String user_id, final String request_id) {

        sharedPreferences = context.getSharedPreferences(Global.pref_name, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        final RequestQueue requestQueue = Volley.newRequestQueue(context);

        StringRequest myRqst = new StringRequest(Request.Method.POST, Global.BASE_URL + "CheckRequestStatus", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.e("Response CheckStatus", response);
                try {

                    JSONObject json = new JSONObject(response);

                    String status = json.getString("status");
                    message = json.getString("message");

                    if (status.equalsIgnoreCase("1")) {

                        Log.e("HERE", "1");
                        String result = json.getString("result");

                        JSONObject o = new JSONObject(result);
                        Log.e("HERE", "2");
                        String request_process_status = o.getString("request_process_status");
                        String request_status = o.getString("request_status");
                        String driver_latitude = o.getString("driver_latitude");
                        String driver_longitude = o.getString("driver_longitude");
                        String pickup_latitude = o.getString("pickup_latitude");
                        String pickup_longitude = o.getString("pickup_longitude");
                        String dest_latitude = o.getString("dest_lat");
                        String dest_longitude = o.getString("dest_lng");
                        String username = o.getString("username");
                        String request_id = o.getString("request_id");
                        String driver_id = o.getString("driver_id");
                        String driver_name = o.getString("driver_name");
                        String driver_mobile = o.getString("driver_mobile");
                        String car_number = o.getString("car_number");
                        String driver_profile_pic = o.getString("driver_profile_pic");


                        if (request_status.equalsIgnoreCase("A") && request_process_status.equalsIgnoreCase("Not Started yet")) {
                            Log.e("REQUEST STATUS", " IS Accepted ");
                            Global.map_sts = "1";
                            Home.footer_recent.setVisibility(View.GONE);
                            Home.footer_fare.setVisibility(View.GONE);
                            Home.driver_details.setVisibility(View.VISIBLE);


                            Glide.with(context).load(driver_profile_pic).asBitmap().placeholder(R.drawable.progress_animation).into(Home.driver_image);
                            Home.drivername.setText(driver_name);
                            Home.rating_txt.setText("4");
                            Home.car_details.setText(car_number);
                            Home.driver_number.setText(driver_mobile);
                         //   Home.ratingbar.setRating(4);

                            LatLng or = new LatLng(Double.parseDouble(driver_latitude), Double.parseDouble(driver_longitude));
                            LatLng d = new LatLng(Double.parseDouble(pickup_latitude), Double.parseDouble(pickup_longitude));
                            Home.googleMap.clear();
                            Home.isRunning = true;
                            Global.drawpath(context, or, d, Home.googleMap);

                        }
                        if (request_process_status.equalsIgnoreCase("Arrived")) {
                            Home.isRunning = false;
                            Log.e("REQUEST STATUS", " IS Arrived ");
                            Global.map_sts = "2";
                            Home.googleMap.clear();

                            Home.driver_details.setVisibility(View.GONE);
                        } else if (request_process_status.equalsIgnoreCase("Started")) {
                            Global.map_sts = "1";
                            LatLng or = new LatLng(Double.parseDouble(pickup_latitude), Double.parseDouble(pickup_longitude));
                            LatLng d = new LatLng(Double.parseDouble(dest_latitude), Double.parseDouble(dest_longitude));
                            Home.googleMap.clear();
                            Home.isRunning = true;
                            Global.drawpath(context, or, d, Home.googleMap);

                        } else if (request_process_status.equalsIgnoreCase("Completed")) {


                            NotificationManager nMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                            nMgr.cancelAll();
                            Home.isRunning = false;
                            Log.e("REQUEST STATUS", " IS Completed ");
                            Global.map_sts = "2";

                        }
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IllegalArgumentException e) {
                } catch (IllegalStateException e) {

                }

                System.out.println("Success " + response);
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "Login " + error, Toast.LENGTH_SHORT).show();


                System.out.println("Error " + error);

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("user_id", user_id);
                params.put("request_id", request_id);

                Log.e("parms CHECK STATUS", params.toString());

                return params;
            }
        };

        myRqst.setRetryPolicy(new DefaultRetryPolicy(
                20000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(myRqst);

    }
}
