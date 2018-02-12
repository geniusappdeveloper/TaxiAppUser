package app.user.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RelativeLayout;

import com.android.volley.VolleyError;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import app.taxiapp.user.R;
import app.user.adapter.RecentLocationAdapter;
import app.user.global.Global;
import app.user.global.NetworkUtil;
import app.user.volleyapi.OnApihit;
import app.user.volleyapi.VolleyBase;

import static app.user.global.Global.lng;
import static app.user.global.Global.pickup_add;


public class GooglePlacesAutocompleteActivity extends Activity implements OnApihit {
    private static final String LOG_TAG = "Places";
    AutoCompleteTextView pickup, drop;
    RecyclerView recent_searches;
    RecentLocationAdapter loc_adapter;
    RelativeLayout back;
    LatLng pickup_latLng, drop_latlng;
    String pick_add = "";
    private static final String TAG = "GooglePlacesActivity";
    SharedPreferences sharedPreferences;

    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";
    private static final String API_KEY = "AIzaSyCUeRPMbtDB-il_BVY939pDg3Ua2TCL4FQ";

    @Override

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_location);
        sharedPreferences = getSharedPreferences(Global.pref_name, Context.MODE_PRIVATE);

        pickup = (AutoCompleteTextView) findViewById(R.id.pickup);
        drop = (AutoCompleteTextView) findViewById(R.id.drop);
        back = (RelativeLayout) findViewById(R.id.back);

        //----------------------------------------------

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Global.pickup_latLng = pickup_latLng;
                Global.drop_latlng = drop_latlng;*/
                Intent i = new Intent(GooglePlacesAutocompleteActivity.this, Home.class);
                i.putExtra("i", "");
                startActivity(i);
                finish();
            }
        });


        pickup.setText(Global.AddressFromLatLng(GooglePlacesAutocompleteActivity.this, Global.lat, lng));
        LatLng lng = new LatLng(Global.lat, Global.lng);
        pickup_latLng = lng;
        pickup_add = Global.AddressFromLatLng(GooglePlacesAutocompleteActivity.this, Global.lat, Global.lng);

        pickup.setAdapter(new GooglePlacesAutocompleteAdapter(this, R.layout.list_item));

        pickup.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String str = (String) parent.getItemAtPosition(position);


                pickup_latLng = Global.getLatLngFromAddress(GooglePlacesAutocompleteActivity.this, str);
                pickup_add = str;

            }
        });

        drop.setAdapter(new GooglePlacesAutocompleteAdapter(this, R.layout.list_item));

        drop.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String str = (String) parent.getItemAtPosition(position);
                Log.e("pickup_latLng", " lat  " + Global.lat + Global.lng);
                drop_latlng = Global.getLatLngFromAddress(GooglePlacesAutocompleteActivity.this, str);

                if (!drop.getText().equals("")) {

                    Global.pickup_add = pickup_add;
                    Global.drop_add = str;

                    Global.pickup_latLng = pickup_latLng;
                    Global.drop_latlng = drop_latlng;
                    Map<String, String> params = new HashMap<>();

                    params.put("user_id", sharedPreferences.getString("user_id", ""));
                    params.put("source_lat", String.valueOf(pickup_latLng.latitude));
                    params.put("source_lng", String.valueOf(pickup_latLng.longitude));
                    params.put("dest_lat", String.valueOf(drop_latlng.latitude));
                    params.put("dest_lng", String.valueOf(drop_latlng.longitude));

                    Log.e("params", " fare  " + params);

                    if (NetworkUtil.getConnectivityStatus(GooglePlacesAutocompleteActivity.this)) {

                        new VolleyBase(GooglePlacesAutocompleteActivity.this).main(params, "fareEstimation", 1);

                    } else {
                        NetworkUtil.openAlert(GooglePlacesAutocompleteActivity.this);
                    }

                } else {

                }

            }
        });


        recent_searches = (RecyclerView) findViewById(R.id.recent_searches);

        loc_adapter = new RecentLocationAdapter();
        LinearLayoutManager llm = new LinearLayoutManager(GooglePlacesAutocompleteActivity.this, LinearLayoutManager.VERTICAL, false);
        recent_searches.setLayoutManager(llm);
        recent_searches.setAdapter(loc_adapter);

    }


    public static ArrayList autocomplete(String input) {
        Log.e(LOG_TAG, "autocomplete");
        ArrayList resultList = null;

        HttpURLConnection conn = null;

        StringBuilder jsonResults = new StringBuilder();

        try {

            StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);

            sb.append("?key=" + API_KEY);

            // sb.append("&types=address&components=country:in|country:bz&location=" + Global.lat + "," + lng + "&radius=500");
            sb.append("&components=country:in|country:bz&location=" + Global.lat + "," + lng + "&radius=500");
            sb.append("&input=" + URLEncoder.encode(input, "utf8"));


            Log.e(LOG_TAG, "autocomplete " + sb.toString());
            URL url = new URL(sb.toString());

            conn = (HttpURLConnection) url.openConnection();

            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            // Load the results into a StringBuilder
            Log.e(LOG_TAG, "autocomplete 2");
            int read;

            char[] buff = new char[1024];

            while ((read = in.read(buff)) != -1) {

                jsonResults.append(buff, 0, read);

            }

        } catch (MalformedURLException e) {

            Log.e(LOG_TAG, "Error processing Places API URL", e);

            return resultList;

        } catch (IOException e) {

            Log.e(LOG_TAG, "Error connecting to Places API", e);

            return resultList;

        } finally {

            if (conn != null) {

                conn.disconnect();

            }

        }

        try {
            Log.e(LOG_TAG, "autocomplete 4");
            // Create a JSON object hierarchy from the results

            JSONObject jsonObj = new JSONObject(jsonResults.toString());

            JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");

            // Extract the Place descriptions from the results

            resultList = new ArrayList(predsJsonArray.length());

            for (int i = 0; i < predsJsonArray.length(); i++) {

                System.out.println(predsJsonArray.getJSONObject(i).getString("description"));

                System.out.println("============================================================");

                resultList.add(predsJsonArray.getJSONObject(i).getString("description"));

            }

        } catch (JSONException e) {

            Log.e(LOG_TAG, "Cnt process JSON results", e);

        }
        Log.e(LOG_TAG, "autocomplete 5  " + resultList);

        return resultList;

    }

    @Override
    public void success(String Response, int index) {
        Log.e("Response", "FARE" + Response);

        try {
            JSONObject json = new JSONObject(Response);
            String status = json.getString("status");
            String message = json.getString("message");

            JSONObject j = new JSONObject(json.getString("result"));
            String Personal = j.getString("Personal");
            String Plus = j.getString("Plus");
            String time_in_min = j.getString("time_in_min");
            String source_lat = j.getString("source_lat");
            String source_lng = j.getString("source_lng");
            String dest_lat = j.getString("dest_lat");
            String dest_lng = j.getString("dest_lng");

            ArrayList<HashMap<String, String>> fare = new ArrayList<>();

            HashMap map = new HashMap();
            map.put("Personal", Personal);
            map.put("Plus", Plus);
            map.put("time", time_in_min);
            map.put("source_lat", source_lat);
            map.put("source_lng", source_lng);
            map.put("dest_lat", dest_lat);
            map.put("dest_lng", dest_lng);

            fare.add(map);

            Global.setFair(fare);

            Intent i = new Intent(GooglePlacesAutocompleteActivity.this, Home.class);
            i.putExtra("i", "fare");
            startActivity(i);
            finish();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void error(VolleyError error, int index) {
        Log.e("error", "FARE" + error);
    }

    class GooglePlacesAutocompleteAdapter extends ArrayAdapter implements Filterable {

        private ArrayList resultList;

        public GooglePlacesAutocompleteAdapter(Context context, int textViewResourceId) {

            super(context, textViewResourceId);
            Log.e(LOG_TAG, "adapt");
        }


        @Override

        public int getCount() {

            return resultList.size();

        }

        @Override

        public String getItem(int index) {

            return (String) resultList.get(index);

        }

        @Override

        public Filter getFilter() {

            Filter filter = new Filter() {
                @Override

                protected FilterResults performFiltering(CharSequence constraint) {

                    FilterResults filterResults = new FilterResults();

                    if (constraint != null) {

                        // Retrieve the autocomplete results.

                        resultList = autocomplete(constraint.toString());

                        // Assign the data to the FilterResults

                        filterResults.values = resultList;

                        filterResults.count = resultList.size();

                    }

                    return filterResults;

                }

                @Override

                protected void publishResults(CharSequence constraint, FilterResults results) {

                    if (results != null && results.count > 0) {

                        notifyDataSetChanged();

                    } else {

                        notifyDataSetInvalidated();

                    }

                }

            };

            return filter;

        }

    }


}


