package com.app.taxi.driver.volleyapi;

import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by Admin on 12/29/2017.
 */

public class CommonFunctions extends  MultiDexApplication   {
    public static String profilePicPath;
    public static String requestUserId ;
    public static String pickUpLatitude ;
    public static String pickUpLongitude ;
    public static String destinationLatitude ;
    public static String destinationLongitude ;
    public static String userFullame ;
    public static String userPhoneNumber ;
    public static String userImage ;
    public static String notificationMessage ;
    public static String notificatioType ;
    public static String notificationTo;


    private Window window;
private static Context con;
public static String recentToken;
    public static final String TAG = CommonFunctions.class
            .getSimpleName();

    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;

    private static CommonFunctions mInstance;

    //______________________________URL
        public static String URL = "http://18.218.130.74/taxi/userServices/";
    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }


    public static synchronized CommonFunctions getInstance()
    {
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
//____________________________________________________________________________________________________________________________
    // bring cursor focus on the mentioned editbox
    public static void requestFocus(View view , Context context) {
        if (view.requestFocus()) {
            ((Activity)context).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }
//________________________________________________________________________________________________________________________
//TO VALIDATE EMAIL
    public static boolean isValidEmail(CharSequence target) {
        if (TextUtils.isEmpty(target)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }
//____________________________________________________________________________________________________________________________
//TO VALIDATE PHONE NUMBER
public static  boolean isValidPhone(String phone)
{
    if (TextUtils.isEmpty(phone)) {
        return false;
    } else {
        return android.util.Patterns.PHONE.matcher(phone).matches();
    }
}
/////___________________________________get location name according to latitude and longitude___________________


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
        } catch (IOException e) {

        }
        catch (NullPointerException e){
            e.printStackTrace();
        }
        catch (IndexOutOfBoundsException e){
            e.printStackTrace();
        }
        return address;
    }


//__________________________________________________________________
public static final String UPLOAD_URL = VolleyBase.fulllink + "uploadpic";
    public static final String GET_PICS_URL = VolleyBase.fulllink + "getpics";
}