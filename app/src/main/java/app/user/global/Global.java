package app.user.global;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.ahmadrosid.lib.drawroutemap.DrawMarker;
import com.ahmadrosid.lib.drawroutemap.DrawRouteMaps;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import app.taxiapp.user.R;
import app.user.volleyapi.LruBitmapCache;

/**
 * Created by user1 on 12/27/2017.
 */

public class Global extends MultiDexApplication {
    static Context con;
    public static String BASE_URL = "http://18.218.130.74/taxi/userServices/";
    //  public static String BASE_URL = "http://192.168.0.125/taxi/userServices/";
    public static Double lat = 0.0;
    public static Double lng = 0.0;
    public static String Loc_name= "";
    public static String pref_name = "Shared_p";
    public static String device_id = "";
    public static String emailPattern = "[a-zA-Z0-9._-]+[a-zA-Z0-9]+@[a-z]+\\.+[a-z]+";
    public static String namePattern = "[a-zA-z ]+";
    public static String numberPattern = "[0-9]+";
    public static String request_id = "";
    public static String map_sts = "";
    public static final String TAG = Global.class.getSimpleName();
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    private static Global mInstance;
    public static LatLng pickup_latLng;
    public static LatLng drop_latlng;
    public static String pickup_add;
    public static String drop_add;

    public static String car_type = "Personal";

    public static ArrayList<HashMap<String, String>> getFair() {
        return fair;
    }

    public static void setFair(ArrayList<HashMap<String, String>> fair) {
        Global.fair = fair;
    }

    public static ArrayList<HashMap<String, String>> fair = new ArrayList<>();

    public static void toast(Context con, String msg) {

        Toast.makeText(con, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    public static synchronized Global getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public ImageLoader getImageLoader() {
        getRequestQueue();
        if (mImageLoader == null) {
            mImageLoader = new ImageLoader(this.mRequestQueue,
                    new LruBitmapCache());
        }
        return this.mImageLoader;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public static String AddressFromLatLng(Context c, Double latitude, Double longitude) {
        String address = "", knownName = "";
        try {

            con = c;
            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(con, Locale.getDefault());

            addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

            address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
            knownName = addresses.get(0).getFeatureName();

            Log.e("knownName", address);

        } catch (IOException e) {

        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        return address;
    }


    public static LatLng getLatLngFromAddress(Context c, String str) {
        con = c;
        LatLng newLatLng = null;
        Geocoder geocoder = new Geocoder(con, Locale.getDefault());
        String result = null;
        try {
            List
                    addressList = geocoder.getFromLocationName(str, 1);
            if (addressList != null && addressList.size() > 0) {
                Address address = (Address) addressList.get(0);

                Log.e(TAG, "" + address.getLatitude());

                newLatLng = new LatLng(address.getLatitude(), address.getLongitude());
            }
        } catch (IOException e) {
            Log.e(TAG, "Unable to connect to Geocoder", e);
        }
        return newLatLng;
    }

    public static void drawpath(Context c, LatLng origin, LatLng destination, GoogleMap googleMap) {

        Log.e("values", "log" + origin + " " + destination + " " + googleMap);
        con = c;
        DrawRouteMaps.getInstance(con)
                .draw(origin, destination, googleMap);
        DrawMarker.getInstance(con).draw(googleMap, origin, R.drawable.blue_loc, "Origin Location");
        DrawMarker.getInstance(con).draw(googleMap, destination, R.drawable.blue_loc, "Destination Location");

        LatLngBounds bounds = new LatLngBounds.Builder()
                .include(origin)
                .include(destination).build();

        Activity activity = (Activity) con;
        Point displaySize = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(displaySize);
        // googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, displaySize.x, 250, 30));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(origin));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(11));
        Log.e("MAP", "END");
    }
}
