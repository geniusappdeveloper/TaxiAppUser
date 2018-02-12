package com.app.taxi.driver;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.ahmadrosid.lib.drawroutemap.DrawRouteMaps;
import com.android.volley.VolleyError;
import com.app.taxi.driver.commonFunctions.NetworkUtil;
import com.app.taxi.driver.stripe.activity.PaymentActivity;
import com.app.taxi.driver.volleyapi.CommonFunctions;
import com.app.taxi.driver.volleyapi.OnApihit;
import com.app.taxi.driver.volleyapi.VolleyBase;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;

import de.hdodenhof.circleimageview.CircleImageView;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class HomeScreen extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, OnMapReadyCallback, OnApihit {
    //___________________________________Navigation drawer______

    private boolean mStarted;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    Button acceptbtn, declineBtn, callbtn_accept , cancelbtn_accept ,arrivebtn_accept;
    Intent acceptscreen, profileScreen;
    boolean Checkpath = true;
    LinearLayout nav_drawer, afteraccept_layout;
    CircleImageView headerImage;
    int time=0 , time_check=0;
    ToggleButton homescreen_togglebutton ;
    //String request_Status;
    RelativeLayout pickUpLocLayout, dropLocLayout;
    TextView timeview;
    NavigationView navigationView;
    View headerview;
    long MillisecondTime, StartTime, TimeBuff, UpdateTime = 0L ;
    private int seconds=0;
    long startTime,difference;
    private boolean startRun;
    int Seconds, Minutes, MilliSeconds ;
    String currentAddress;
    private SharedPreferences preferences;
    Marker markerCurrent = null,markerPick = null, markerDrop = null;
    public static Context context;
    //___________________________________

    double currentLat;
    double dest_lat;
    double dest_long;
    double currentlong;
    double pickup_lat;
    double pickup_long;
    int pickupPath = 0;
    GoogleMap mMap;
    SupportMapFragment mapFragment;
    public double latitude = -33.852, longitude = 151.211;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location mCurrentLocation;
    String mLastUpdateTime;

    String pickupAddress, destinationAdderess;
    CircleImageView afteraccpeting_circleView;
    boolean isMarkerRotating = false;
    private static final String TAG = "LocationActivity";
    private static final long INTERVAL = 1000 * 10;
    private static final long FASTEST_INTERVAL = 1000 * 5;
    LatLng CurrentLatLong;
    int accept = 0;
    LatLng destinationLatLong;
    String tim;
    LatLng pickupLatLong;
    SharedPreferences.Editor editor;
    TextView headerDriverName;
    private static final int REQUEST_CODE_LOCATION = 2;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    ImageView headerImageView;
    TextView pickuplocation_textview, destination_textview, userName, userPickLoc, userDestLoc;
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigation_drawer);
        context = HomeScreen.this;
        if(savedInstanceState != null){
            seconds = savedInstanceState.getInt("seconds");
            startRun=savedInstanceState.getBoolean("startRun");
        }
        preferences = getSharedPreferences(SplashScreen.PREFERENCES, Context.MODE_PRIVATE);
        //   Picasso.with(getApplicationContext()).load(profile_pic).into(profilescreen_circleView);
        //-----------------------------------GETTING THE TOKEN FOR NOTIFICATIONS-------------------
        sendRefreshToken();
//-------------------------------INITIALIZING ALL THE VARIABLES USED --------------------------------------
        intializeAllVariables(); // initializing all the variables used in this activity

        showHeaderImageAndText();
        //_________________________________________________________________________________________
checkLocationPermission();
        checkToggleRefresh();

        Utility.checkLocationPermission(HomeScreen.this);
        Utility.checkPermission(HomeScreen.this);
//__________________________________sending userid and device token and device type ______________
        Log.e("sendDriverData", "sendDriverData");


        //______________________________________________________________

        if (!isGooglePlayServicesAvailable()) {
            finish();
        }
        createLocationRequest();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();






//___________________________________________________________________________________________________________________________

        editor = preferences.edit();
        nav_drawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawerLayout.openDrawer(GravityCompat.START);


                //-------------setting user name and image on drawer-------------------------------
                // Picasso.with(getApplicationContext()).load(SplashScreen.DeviceProfilePic).into(headerImage);
                //     id_user_name_drawer.setText(SplashScreen.DriverFirstName);

            }
        });
        Log.e("pickupAddress", preferences.getString(SplashScreen.NOTIFICATION_TYPE, "" + "llllllll"));
        Log.e("pickupAddress", preferences.getString(SplashScreen.PICKUP_ADDRESS, ""));
        Log.e("dest_LatLng adrress", preferences.getString(SplashScreen.DESTINATION_ADDRESS, ""));
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Log.e("on create", "On create");
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(HomeScreen.this);
        navigationView.setNavigationItemSelectedListener(this);

        headerImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                profileScreen = new Intent(HomeScreen.this, ProfileScreen.class);
                startActivity(profileScreen);
                finish();

            }
        });
//-----------------------------------------------Called when the driver will get the ride--------------------------
        /*if (preferences.getString(SplashScreen.NOTIFICATION_TYPE, "").equalsIgnoreCase("pr")) {

            gotRide(); // when the driver will get the ride this method will be invoked to show locations and accept and decline btns

        }*/

        if(preferences.getString(SplashScreen.RIDE_STATUS,"").equalsIgnoreCase("A")){

            pickupPath=2;
            Log.e("preferences ride_status" , "A");
            arrivebtn_accept.setText("Start Ride");
            updateUI();
        }
        else if(preferences.getString(SplashScreen.RIDE_STATUS,"").equalsIgnoreCase("S"))
        {
            pickupPath=2;
            Log.e("preferences ride_status" , "S");
            arrivebtn_accept.setText("Ride Completed");
            updateUI();
        }
        else if(preferences.getString(SplashScreen.RIDE_STATUS,"").equalsIgnoreCase("C"))
        {

            Log.e("preferences ride_status" , "C");
          /*  editor.putString(SplashScreen.aterAccept_Check, "false");
            editor.putString(SplashScreen.USER_NAME,"");
            editor.putString(SplashScreen.USER_MOBILE,"");
            editor.putString(SplashScreen.USER_IMAGE,"");
            editor.putString(SplashScreen.REQUEST_USER_ID,"");
            editor.putString(SplashScreen.NOTIFICATION_TYPE,"");
            editor.putString(SplashScreen.PICKUP_LATITUDE,"");
            editor.putString(SplashScreen.PICKUP_LONGITUDE,"");
            editor.putString(SplashScreen.DESTINATION_LATITUDE,"");
            editor.putString(SplashScreen.DESTINATION_LONGITUDE,"");
            editor.putString(SplashScreen.PICKUP_ADDRESS,"");
            editor.putString(SplashScreen.DESTINATION_ADDRESS,"");
            editor.putString(SplashScreen.RIDE_STATUS,"");
            editor.commit();
            pickuplocation_textview.setText(preferences.getString(SplashScreen.PICKUP_ADDRESS, ""));
            destination_textview.setText(preferences.getString(SplashScreen.DESTINATION_ADDRESS, ""));
            pickupPath=0;
            updateUI();*/
        }
        try {
            HomeScreen.this.registerReceiver(new notification(), new IntentFilter("com.app.noti"));
        }
        catch (Exception ex)
        {

        }
        //___________________________________________________________________________________________________________________
        acceptbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.e("USERNAMEEEE", preferences.getString(SplashScreen.USER_NAME, ""));
                Log.e("imageeeee", preferences.getString(SplashScreen.USER_IMAGE, ""));
                Log.e("aceptierjerer", (preferences.getString(SplashScreen.aterAccept_Check, "")));
                Map<String, String> params = new HashMap<>();
                if (preferences.getString(SplashScreen.REQUEST_USER_ID, "") != null) {

                    params.put("request_id", preferences.getString(SplashScreen.REQUEST_USER_ID, ""));
                    Log.e("request_id", preferences.getString(SplashScreen.REQUEST_USER_ID, ""));
                    params.put("driver_id", preferences.getString(SplashScreen.DriverID, ""));
                    params.put("request_status", "A");
                    if (NetworkUtil.getConnectivityStatus(HomeScreen.this)) {
                        new VolleyBase(HomeScreen.this).main(params, "DriverRequestAction", 1);
                    } else {
                        NetworkUtil.openAlert(HomeScreen.this);
                    }
                }
            }
        });
        declineBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                declineRide();
            }
        });

        rideAccepted(); // called when the ride will be accpeted by the driver

        cancelbtn_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(arrivebtn_accept.getText().toString().equalsIgnoreCase("Start ride")
                        ||arrivebtn_accept.getText().toString().equalsIgnoreCase("Ride completed")){
                    cancelbtn_accept.setEnabled(false);
                  //  cancelbtn_accept.setBackgroundColor(Color.RED);
                    Toast.makeText(HomeScreen.this,"YOU CANNOT CANCEL THE RIDE TILL YOU COMPLETE" ,Toast.LENGTH_LONG).show();

                }
                else{
                    declineRide();
                }


            }
        });
        callbtn_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    Log.e("calllll btnnnnnnnnnn" , preferences.getString(SplashScreen.USER_MOBILE,"" )+" kkk");
                   Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", preferences.getString(SplashScreen.USER_MOBILE , ""), null));
                    if (ActivityCompat.checkSelfPermission(HomeScreen.this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    startActivity(callIntent);
                }catch(Exception e){

                    Toast.makeText(HomeScreen.this , e.toString(),Toast.LENGTH_LONG).show();

                }
            }
        });

        arrivebtn_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String , String> params = new HashMap<>();
              //  params.put("driver_id",preferences.getString(SplashScreen.DriverID,"") );
                params.put("driver_id",preferences.getString(SplashScreen.DriverID,"") );
                params.put("request_id",preferences.getString(SplashScreen.REQUEST_USER_ID,"") );
                Log.e("request_id", preferences.getString(SplashScreen.REQUEST_USER_ID, ""));
                params.put("source_lat",preferences.getString(SplashScreen.PICKUP_LATITUDE,"") );
                params.put("source_lng",preferences.getString(SplashScreen.PICKUP_LONGITUDE,"") );
                params.put("dest_lat",preferences.getString(SplashScreen.DESTINATION_LATITUDE,"") );
                params.put("dest_lng",preferences.getString(SplashScreen.DESTINATION_LONGITUDE,"") );
                Log.e("arrive button text" ,arrivebtn_accept.getText().toString() );
                if(arrivebtn_accept.getText().toString().equalsIgnoreCase("Arrived")){
                    Log.e("put ride_process_status" , "A");
                    startTime = System.currentTimeMillis();

                    params.put("request_process_status","A" );
                    Log.e("REQUEST STssATUS"  , preferences.getString(SplashScreen.RIDE_STATUS,""));
                }
                else if(arrivebtn_accept.getText().toString().equalsIgnoreCase("Start ride")){
                    Log.e("put ride_process_status" , "S");
                 //  check_difference();
                   difference = System.currentTimeMillis() - startTime;
                    startTime=0;
                    tim= String.format("%d ,%d , %d ",
                            TimeUnit.MINUTES.toHours(TimeUnit.MILLISECONDS.toMinutes(difference)),
                            TimeUnit.MILLISECONDS.toMinutes(difference),
                            TimeUnit.MILLISECONDS.toSeconds(difference) -
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(difference))
                    );
                    Log.e("mmmmmmmmmmmmmmm",tim);
                    tim = tim.replaceAll(",",":");
                    tim = tim.replaceAll("\\s","");
                    Log.e("replaced",tim);


                    params.put("request_process_status","S" );
                    params.put("wait_time",tim);
                Log.e("paramssss" , params.toString());

                    Log.e("timerrrrrrrrrrr" , timeview.getText().toString()+"mmmmmm");
                    Log.e("REQUEST STssATUS"  , preferences.getString(SplashScreen.RIDE_STATUS,""));
                }
                else if(arrivebtn_accept.getText().toString().equalsIgnoreCase("Ride Completed")){
                    Log.e("put ride_process_status" , "C");
                    params.put("request_process_status","C" );
                    Log.e("REQUEST STssATUS"  , preferences.getString(SplashScreen.RIDE_STATUS,""));
                }
               // params.put("dest_lat",preferences.getString(SplashScreen.DESTINATION_LATITUDE,"") );

                if (NetworkUtil.getConnectivityStatus(HomeScreen.this)) {
                    new VolleyBase(HomeScreen.this).main(params, "RideRequestProcess", 4);
                } else {
                    NetworkUtil.openAlert(HomeScreen.this);
                }


            }
        });
        homescreen_togglebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkToggleStatus();
            }
        });

    }

    private void check_difference() {

    }

    private void checkToggleRefresh() {
        if(preferences.getString(SplashScreen.onlineOffline,"").equalsIgnoreCase("Y")){
            homescreen_togglebutton.setChecked(true);
        }
        else if(preferences.getString(SplashScreen.onlineOffline,"").equalsIgnoreCase("N")){
            homescreen_togglebutton.setChecked(false);
        }

    }

    private void checkToggleStatus() {
        if(homescreen_togglebutton.isChecked()){
            homescreen_togglebutton.setChecked(true);
            Map<String ,String > params = new HashMap<>();
            params.put( "driver_id" , preferences.getString(SplashScreen.DriverID,"") );
            params.put("is_online" , "Y");
            if (NetworkUtil.getConnectivityStatus(HomeScreen.this)) {
                new VolleyBase(HomeScreen.this).main(params, "showOnline", 5);
                Toast.makeText(HomeScreen.this,"You Are Now Online" , Toast.LENGTH_LONG).show();
            } else {
                NetworkUtil.openAlert(HomeScreen.this);
            }
        }
        else if(!homescreen_togglebutton.isChecked()){
            homescreen_togglebutton.setChecked(false);
            Map<String ,String> params = new HashMap<>();
            params.put( "driver_id" , preferences.getString(SplashScreen.DriverID,"") );
            params.put("is_online" , "N");
            if (NetworkUtil.getConnectivityStatus(HomeScreen.this)) {
                new VolleyBase(HomeScreen.this).main(params, "showOnline", 5);
                Toast.makeText(HomeScreen.this,"You Are Now Offline" , Toast.LENGTH_LONG).show();
            } else {
                NetworkUtil.openAlert(HomeScreen.this);
            }
        }





    }

    private void intializeAllVariables() {

        mDrawerLayout = (DrawerLayout) findViewById(R.id.acceptdrawer_layout);
        timeview   = (TextView)findViewById(R.id.timer);
        afteraccept_layout = (LinearLayout) findViewById(R.id.afteraccept_layout);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        headerview = navigationView.getHeaderView(0);
        headerImageView = (ImageView) headerview.findViewById(R.id.header_circleView);
        headerDriverName = (TextView) headerview.findViewById(R.id.driver_name_header);
        acceptbtn = (Button) findViewById(R.id.acceptbtn_homescreen);
        cancelbtn_accept = (Button)findViewById(R.id.cancelbtn_accept);
        arrivebtn_accept = (Button)findViewById(R.id.arrivebtn_accept);
        declineBtn = (Button) findViewById(R.id.declinebtn_homescreen);
        homescreen_togglebutton = (ToggleButton)findViewById(R.id.homescreen_togglebutton);
        callbtn_accept = (Button) findViewById(R.id.callbtn_accept);
        userName = (TextView) findViewById(R.id.username_textbox);
        userPickLoc = (TextView) findViewById(R.id.pickupaccepting_textview);
        userDestLoc = (TextView) findViewById(R.id.dropaccepting_textview);
        pickUpLocLayout = (RelativeLayout) findViewById(R.id.pickuplocation_layout);
        dropLocLayout = (RelativeLayout) findViewById(R.id.droplocation_layout);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        afteraccpeting_circleView = (CircleImageView) findViewById(R.id.afteraccpeting_circleView);
        pickuplocation_textview = (TextView) findViewById(R.id.pickuplocation_textview);
        destination_textview = (TextView) findViewById(R.id.droplocation_textview);
        nav_drawer = (LinearLayout) findViewById(R.id.homescreen_navigation_layout);

    }

    private void showHeaderImageAndText() {

        headerDriverName.setText(preferences.getString(SplashScreen.DriverFirstName, ""));
        if (preferences.getString(SplashScreen.Driver_PIC_PATH, "") != null &&
                (preferences.getString(SplashScreen.Driver_PIC_PATH, "")) != "") {
            Picasso.with(getApplicationContext()).load(preferences.getString(SplashScreen.Driver_PIC_PATH, "")).into(headerImageView);
        } else {
            Picasso.with(getApplicationContext()).load(R.drawable.profile_pic).into(headerImageView);
        }

    }


    public void gotRide() {

        acceptbtn.setVisibility(View.VISIBLE);
        declineBtn.setVisibility(View.VISIBLE);
        pickUpLocLayout.setVisibility(View.VISIBLE);
        dropLocLayout.setVisibility(View.VISIBLE);
        pickuplocation_textview.setText(preferences.getString(SplashScreen.PICKUP_ADDRESS, ""));
        destination_textview.setText(preferences.getString(SplashScreen.DESTINATION_ADDRESS, ""));
        pickup_lat = new Double(preferences.getString(SplashScreen.PICKUP_LATITUDE,""));
        pickup_long = new Double(preferences.getString(SplashScreen.PICKUP_LONGITUDE,""));
        dest_lat = new Double(preferences.getString(SplashScreen.DESTINATION_LATITUDE, ""));
        dest_long = new Double(preferences.getString(SplashScreen.DESTINATION_LONGITUDE, ""));
               /* double picklat = Double.parseDouble(preferences.getString(SplashScreen.PICKUP_LATITUDE,""));
                double picklong = Double.parseDouble(preferences.getString(SplashScreen.PICKUP_LONGITUDE,""));
                String pick  = CommonFunctions.AddressFromLatLng(HomeScreen.this, picklat, picklong);
                pickuplocation_textview.setText(pick);*/
        pickupPath = 1;
        Log.e("gotRide_pickup_loc" , preferences.getString(SplashScreen.PICKUP_ADDRESS,"") );
        Log.e("gotRide_Destination_loc" , preferences.getString(SplashScreen.DESTINATION_ADDRESS, "") );
    }

    //---------------------------------------// Called when the ride will be accepted by the driver
    public void rideAccepted() {
        if (preferences.getString(SplashScreen.aterAccept_Check, "").equalsIgnoreCase("A")) {

            afteraccept_layout.setVisibility(View.VISIBLE);
            if(preferences.getString(SplashScreen.RIDE_STATUS,"").equalsIgnoreCase("")){

                cancelbtn_accept.setVisibility(View.VISIBLE);
                arrivebtn_accept.setText("Arrived");
            }
            acceptbtn.setVisibility(View.GONE);
            declineBtn.setVisibility(View.GONE);
            pickUpLocLayout.setVisibility(View.GONE);
            dropLocLayout.setVisibility(View.GONE);
            userName.setText(preferences.getString(SplashScreen.USER_NAME, ""));
            userPickLoc.setText(preferences.getString(SplashScreen.PICKUP_ADDRESS, ""));
            userDestLoc.setText(preferences.getString(SplashScreen.DESTINATION_ADDRESS, ""));
            if (CommonFunctions.profilePicPath != null) {
                Picasso.with(getApplicationContext()).load(preferences.getString(SplashScreen.USER_IMAGE, "")).into(afteraccpeting_circleView);
            } else {
                Picasso.with(getApplicationContext()).load(preferences.getString(SplashScreen.USER_IMAGE, "")).into(afteraccpeting_circleView);
            }

        }
    }
// call when the ride is declined by the driver or the driver first accepted and then cancelled the ride
public void declineRide(){
    acceptbtn.setVisibility(View.GONE);
    afteraccept_layout.setVisibility(View.GONE);
    declineBtn.setVisibility(View.GONE);
    pickUpLocLayout.setVisibility(View.GONE);
    dropLocLayout.setVisibility(View.GONE);
    Log.e("declinedddd", "DRIVE DECLINED");
    Map<String, String> params = new HashMap<>();
    if (preferences.getString(SplashScreen.REQUEST_USER_ID, "") != null) {
        params.put("request_id", CommonFunctions.requestUserId);
        params.put("driver_id", preferences.getString(SplashScreen.DriverID, ""));
        params.put("request_status", "D");

        if (NetworkUtil.getConnectivityStatus(HomeScreen.this)) {
            new VolleyBase(HomeScreen.this).main(params, "DriverRequestAction", 2);

                       /* nextscreen = new Intent(LoginScreen.this, HomeScreen.class);
                        nextscreen.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(nextscreen);*/
        } else {
            NetworkUtil.openAlert(HomeScreen.this);
        }
    }
    editor.putString(SplashScreen.aterAccept_Check, "false");
    editor.putString(SplashScreen.USER_NAME,"");
    editor.putString(SplashScreen.USER_MOBILE,"");
    editor.putString(SplashScreen.USER_IMAGE,"");
    editor.putString(SplashScreen.REQUEST_USER_ID,"");
    editor.putString(SplashScreen.NOTIFICATION_TYPE,"");
    editor.putString(SplashScreen.PICKUP_LATITUDE,"");
    editor.putString(SplashScreen.PICKUP_LONGITUDE,"");
    editor.putString(SplashScreen.DESTINATION_LATITUDE,"");
    editor.putString(SplashScreen.DESTINATION_LONGITUDE,"");
    editor.putString(SplashScreen.PICKUP_ADDRESS,"");
    editor.putString(SplashScreen.DESTINATION_ADDRESS,"");
    editor.putString(SplashScreen.RIDE_STATUS,"");
    editor.commit();
    pickuplocation_textview.setText(preferences.getString(SplashScreen.PICKUP_ADDRESS, ""));
    destination_textview.setText(preferences.getString(SplashScreen.DESTINATION_ADDRESS, ""));
    pickupPath=0;
    updateUI();
}
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.e("Onmapready", "Ready");
        //   updateUI();
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.payments) {
            Intent x = new Intent(HomeScreen.this , AddCardDetails.class);
            startActivity(x);

        } else if (id == R.id.ridehistory) {
            Intent logInt = new Intent(HomeScreen.this, RideHistory.class);
            startActivity(logInt);
        } else if (id == R.id.about) {
            ;
        } else if (id == R.id.logout) {
            /*Toast.makeText(this, "log out", Toast.LENGTH_LONG).show();*/
            //    SplashScreen.DriverID = " ";
            logout();
        }

        return false;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.e("OnSTART", "Onstart");
        // Log.d(TAG, "onStart fired ..............");
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.e("onStop  ", "ONSTOp");
        mGoogleApiClient.disconnect();
/*SharedPreferences.Editor edit = preferences.edit();
edit.putString(SplashScreen.onlineOffline,"N");
edit.commit();*/
        //  Log.d(TAG, "isConnected ...............: " + mGoogleApiClient.isConnected());
    }

    private boolean isGooglePlayServicesAvailable() {
        Log.e("isGooglePlayServices", "isGooglePlayServicesAvailable");
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            GooglePlayServicesUtil.getErrorDialog(status, this, 0).show();
            return false;
        }

    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.e("onConnected", "onConnected");
        //  Log.d(TAG, "onConnected - isConnected ...............: " + mGoogleApiClient.isConnected());
        startLocationUpdates();

    }

    protected void startLocationUpdates() {
        Log.e("startLocationUpdates", "startLocationUpdates");
        if (ActivityCompat.checkSelfPermission(HomeScreen.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.e("permissionnnn", "permission");
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Log.e("permissionnnn outttt ", "permission outttt");
        PendingResult<Status> pendingResult = LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, HomeScreen.this);
        Log.e("Location update started", "Location update started ..............: ");
     /*   if (ActivityCompat.checkSelfPermission(HomeScreen.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(HomeScreen.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
           Log.e("permissionnnn", "permission");

            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }*/


        //Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        //onLocationChanged(location);
        updateUI();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e("onConnectionSuspended", "onConnectionSuspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e("onConnectionFailed", "onConnectionFailed");
        Log.e(TAG, "Connection failed: " + connectionResult.toString());
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.e("onLocationChanged", "onLocationChanged");
        //    Log.d(TAG, "Firing onLocationChanged..............................................");
        mCurrentLocation = location;
        //   mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());

        updateUI();
        //   markerPick.setVisible(false);
    }


    private void updateUI() {
        Log.e("updateUI", "updateUI");
        //  Log.d(TAG, "UI update initiated .............");
        if (null != mCurrentLocation) {

            Log.e("timeeeeeeeeeeeeeeee" , time+"");
            Log.e("timeeeeeeeeeeecheck" , time_check+"");
            currentLat = mCurrentLocation.getLatitude();
            currentlong = mCurrentLocation.getLongitude();
            Map<String, String> params = new HashMap<>();
            params.put("user_id", preferences.getString(SplashScreen.DriverID, ""));
            params.put("device_token", CommonFunctions.recentToken);
            params.put("device_type", "A");
            params.put("latitude", currentLat + "");
            params.put("longitude", currentlong + "");
            if (NetworkUtil.getConnectivityStatus(HomeScreen.this)) {
                new VolleyBase(HomeScreen.this).main(params, "GetDeviceToken", 3);

                       /* nextscreen = new Intent(LoginScreen.this, HomeScreen.class);
                        nextscreen.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(nextscreen);*/
            } else {
                NetworkUtil.openAlert(HomeScreen.this);
            }


           // Log.e("lat", "pickupadress: " + So);
          //  Log.e("long", "longitude: " + currentlong);
            Log.e("userid" , preferences.getString(SplashScreen.DriverID,"").toString());
            Log.e("request_id", preferences.getString(SplashScreen.REQUEST_USER_ID, ""));
          //  Log.e("device token" , CommonFunctions.recentToken);
          //  Log.e("device type" , "A");

            CurrentLatLong = new LatLng(currentLat, currentlong);
            // String pickupaddress = CommonFunctions.AddressFromLatLng(HomeScreen.this, currentLat, currentlong);
            pickupLatLong = new LatLng(pickup_lat , pickup_long);

            destinationLatLong = new LatLng(dest_lat, dest_long);
            // String destinationaddress = CommonFunctions.AddressFromLatLng(HomeScreen.this, dest_lat, dest_long);
            Log.e("latlong: ", CurrentLatLong.toString());


            // mMap.addMarker(new MarkerOptions().position(CurrentLatLong).title("Marker"));

            if (Checkpath == true) {

               /* DrawMarker.getInstance(HomeScreen.this).draw(mMap, originlatLng, R.drawable.pickupcar2from_marker, pickupaddress);
                DrawMarker.getInstance(HomeScreen.this).draw(mMap, destinationLatLong, R.drawable.pickupcar2to_marker, destinationaddress);*/
                if (markerCurrent != null) {
                    markerCurrent.remove();
                }
                if(markerPick!=null){
                    markerPick.remove();
                }
               /* markerCurrent = mMap.addMarker(new MarkerOptions()
                        .position(CurrentLatLong)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.driver_car))
                        .title(currentAddress));*/
                Log.e("pickupPath" , pickupPath+"");
                currentAddress = CommonFunctions.AddressFromLatLng(HomeScreen.this,currentLat,currentlong);
                if(pickupPath==0){
                    mMap.clear();
                 //   markerCurrent.remove();

                        markerCurrent = mMap.addMarker(new MarkerOptions()
                                .position(CurrentLatLong)
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.driver_car))
                                .title(currentAddress));
                    Log.e("markercurrentttttt" , markerCurrent.toString());


                }
                if(pickupPath ==1){
                 //   markerCurrent.remove();
                    DrawRouteMaps.getInstance(HomeScreen.this)
                            .draw(/*origin*/CurrentLatLong, pickupLatLong, mMap);

    markerCurrent = mMap.addMarker(new MarkerOptions()
            .position(CurrentLatLong)
            .icon(BitmapDescriptorFactory.fromResource(R.drawable.driver_car))
            .title(currentAddress));



                    markerPick = mMap.addMarker(new MarkerOptions()
                            .position(pickupLatLong)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.drop_icon))
                            .title(preferences.getString(SplashScreen.PICKUP_ADDRESS, "")));
                }
                if(pickupPath==2){
                    mMap.clear();
                    //markerCurrent.remove();
                    DrawRouteMaps.getInstance(HomeScreen.this)
                            .draw(/*origin*/CurrentLatLong, destinationLatLong, mMap);
                    markerPick = mMap.addMarker(new MarkerOptions()
                            .position(CurrentLatLong)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.driver_car))
                            .title(currentAddress));

                    markerDrop = mMap.addMarker(new MarkerOptions()
                            .position(destinationLatLong)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.drop_icon))
                            .title(preferences.getString(SplashScreen.DESTINATION_ADDRESS, "")));
                }
                Checkpath = false;
            }


            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(CurrentLatLong, 13);
            mMap.animateCamera(cameraUpdate);
            if(pickupPath==1){
                double head = bearingBetweenLocations(CurrentLatLong, pickupLatLong);
                // Log.e("HEAD ROTATION", head + "");
                // Log.e("checkpathhhhhh", Checkpath + "");
                rotateMarker(markerCurrent, head);
              //  Log.e("")

            }
            else if(pickupPath==2){
                double head = bearingBetweenLocations(CurrentLatLong, destinationLatLong);
                // Log.e("HEAD ROTATION", head + "");
                // Log.e("checkpathhhhhh", Checkpath + "");
                rotateMarker(markerCurrent, head);
            }
            //   googleMap.moveCamera(CameraUpdateFactory.newLatLng(CurrentLatLong));

            //    SharedPreferences.Editor ed = sharedPreferences.edit();
            //   ed.putString(GlobalClass.Current_Lat, lat);
            //   ed.putString(GlobalClass.Current_Long, lng);
            //  ed.commit();
//            tvLocation.setText("At Time: " + mLastUpdateTime + "\n" +
//                    "Latitude: " + lat + "\n" +
//                    "Longitude: " + lng + "\n" +
//                    "Accuracy: " + mCurrentLocation.getAccuracy() + "\n" +
//                    "Provider: " + mCurrentLocation.getProvider());

        } else {
            //  Log.e(TAG, "location is null ...............");
        }
    }


    private double bearingBetweenLocations(LatLng latLng1, LatLng latLng2) {

        double PI = 3.14159;
        double lat1 = latLng1.latitude * PI / 180;
        double long1 = latLng1.longitude * PI / 180;
        double lat2 = latLng2.latitude * PI / 180;
        double long2 = latLng2.longitude * PI / 180;

        double dLon = (long2 - long1);

        double y = Math.sin(dLon) * Math.cos(lat2);
        double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1)
                * Math.cos(lat2) * Math.cos(dLon);

        double brng = Math.atan2(y, x);

        brng = Math.toDegrees(brng);
        brng = (brng + 360) % 360;

        return brng;
    }

    private void rotateMarker(final Marker marker, final double toRotation) {

        if (!isMarkerRotating) {
            final Handler handler = new Handler();
            final long start = SystemClock.uptimeMillis();
            if(marker!=null){
                final float startRotation = marker.getRotation();
                final long duration = 1000;

                final LinearInterpolator interpolator = new LinearInterpolator();

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        isMarkerRotating = true;

                        long elapsed = SystemClock.uptimeMillis() - start;
                        float t = interpolator.getInterpolation((float) elapsed / duration);

                        double rot = t * toRotation + (1 - t) * startRotation;

                        marker.setRotation((float) (-rot > 180 ? rot / 2 : rot));
                        if (t < 1.0) {
                            // Post again 16ms later.
                            handler.postDelayed(this, 16);
                            // markerPick.remove();

                        } else {
                            isMarkerRotating = false;
                        }
                        Checkpath = true;

                    }
                });
            }

        }
    }

    protected void stopLocationUpdates() {
        Log.e("stopLocationUpdates", "stopLocationUpdates");
if(mGoogleApiClient.isConnected()){
    LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, HomeScreen.this);
}
else{
    mGoogleApiClient.isConnected();
}

        // Log.d(TAG, "Location update stopped .......................");
    }

    @Override
    public void onResume() {
        Log.e("onResume", "onResume");
        super.onResume();
        if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
            Log.e("onResume", "Location update resumed .....................");
        }
    }

    @Override
    public void onPause() {
        Log.e("onPause", "onPause");
        super.onPause();
        stopLocationUpdates();
    }

    @Override
    public void success(String Response, int index) throws JSONException {
        if (index == 1) {
//Toast.makeText(HomeScreen.this ,"data sent" , Toast.LENGTH_LONG).show(); try {
            try {
                JSONObject jsonObject = new JSONObject(Response);
                Log.e("accceptttttt log", Response.toString());
                String strStatus = jsonObject.getString("status");
                String strMessage = jsonObject.getString("message");

                Log.e("strstatus_accept" , strStatus+111);
                Log.e("strmessage_accept" , strMessage+222);
                if(strStatus.equals("1")){
                    String request_Status = jsonObject.getString("request_status");
                    editor.putString(SplashScreen.aterAccept_Check, request_Status);
                    editor.commit();
                    rideAccepted();// Called when the ride will be accepted by the driver
                }
                else if (strStatus.equals("0")){
                    Log.e("AUTODECLINE RIDE" , strMessage);
                    Toast.makeText(HomeScreen.this , "RIDE ACCEPTED BY OTHER DRIVER " , Toast.LENGTH_LONG).show();
                    declineRide();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        else if (index ==4){
         try{
            JSONObject jsonObject = new JSONObject(Response);
            Log.e("ARRIVED BUTTON STATUS" , Response.toString());

            String strStatus = jsonObject.getString("status");
            String strMessage = jsonObject.getString("message");
            String   rideStatus = jsonObject.getString("request_process_status");
             Log.e("Ride STATUS" , rideStatus);
             if(rideStatus.equalsIgnoreCase("A")){

                 arrivebtn_accept.setText("Start Ride");
                 pickupPath=2;
                 editor.putString(SplashScreen.RIDE_STATUS,rideStatus);
                 editor.commit();
                 Log.e("RIDE STATUS AAAA" , preferences.getString(SplashScreen.RIDE_STATUS,""));
                 cancelbtn_accept.setVisibility(View.GONE);
                 updateUI();
             }
             else if(rideStatus.equalsIgnoreCase("S")){
                 arrivebtn_accept.setText("Ride Completed");
                // cancelbtn_accept.setEnabled(false);
                 pickupPath=2;
                 editor.putString(SplashScreen.RIDE_STATUS,rideStatus);
                 editor.commit();
                 Log.e("RIDE STATUS SSSS" , preferences.getString(SplashScreen.RIDE_STATUS,""));
                 updateUI();
             }
             else if(rideStatus.equalsIgnoreCase("C")){
                 //arrivebtn_accept.setText("Completed");
                 afteraccept_layout.setVisibility(View.GONE);
                 editor.putString(SplashScreen.aterAccept_Check, "false");
                 editor.putString(SplashScreen.RIDE_STATUS,"");
                 editor.commit();
                 JSONObject jObject = jsonObject.getJSONObject("result");
                 String fare =  jObject.getString("fare");
                 String tax =  jObject.getString("with_surcharge");
                 String totalBill =  jObject.getString("total_amount");
                 editor.putString(SplashScreen.fare,fare);
                 editor.putString(SplashScreen.tax,tax);
                 editor.putString(SplashScreen.totalBill,totalBill);
                 editor.commit();
                 Log.e("CCCC RIde STATUS" , preferences.getString(SplashScreen.RIDE_STATUS,"")+11111);

               //  pickuplocation_textview.setText(preferences.getString(SplashScreen.PICKUP_ADDRESS, ""));
                // destination_textview.setText(preferences.getString(SplashScreen.DESTINATION_ADDRESS, ""));
                 pickupPath=0;
                 mMap.clear();
                 Toast.makeText(HomeScreen.this,"RIDE SUCCESSFULLY COMPLETED" , Toast.LENGTH_LONG).show();
                 Intent payIntent = new Intent(HomeScreen.this , PaymentScreen.class);
                 startActivity(payIntent);
                 //updateUI();
             }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        }

        else if(index==5){
            JSONObject jsonObject = new JSONObject(Response);
            Log.e("ONLINE OFFLINE" , Response.toString());
            String is_online = jsonObject.getString("result");
                SharedPreferences.Editor edit = preferences.edit();
                edit.putString(SplashScreen.onlineOffline,is_online);
                edit.commit();

        }


    }

    @Override
    public void error(VolleyError error, int index) {

    }

    public void sendRefreshToken() {
        MyFirebaseInstanceIDService myFirebaseInstanceIDService = new MyFirebaseInstanceIDService();
        myFirebaseInstanceIDService.onTokenRefresh();
        Log.e("tokennnn", CommonFunctions.recentToken);

        /*

        Map<String ,String> params = new HashMap<>();
        params.put("device_token" , CommonFunctions.recentToken);
*/

    }

    public void logout(){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(SplashScreen.DriverFirstName,"");
        editor.putString(SplashScreen.DriverLastName,"");
        editor.putString(SplashScreen.DriverPhoneNumber,"");
        editor.putString(SplashScreen.CountryCode,"");
        editor.putString(SplashScreen.DriverID,"");
        editor.putString(SplashScreen.DriverVerify,"");
        editor.putString(SplashScreen.UserType,"");
        editor.putString(SplashScreen.DeviceProfilePic,"");
        editor.putString(SplashScreen.Driver_PIC_PATH,"");
        editor.putString(SplashScreen.DriverEmail,"");
        editor.putString(SplashScreen.UniqueCode,"");
        editor.putString(SplashScreen.car_number, "");
        editor.putString(SplashScreen.Driver_License_Pic, "");
        editor.putString(SplashScreen.make, "");
        editor.putString(SplashScreen.model, "");
        editor.putString(SplashScreen.social_security_number, "");
        editor.putString(SplashScreen.RIDE_STATUS,"");

        editor.commit();
        Intent logInt = new Intent(HomeScreen.this, LoginScreen.class);
        logInt.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(logInt);

    }

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Permission necessary")
                        .setMessage("Location permission is necessary")
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(HomeScreen.this,
                                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();

            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            android.Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        //Request location updates:
                        //   locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 400, 1, (LocationListener) this);
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                }
                return;
            }

        }
    }


    class notification extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equalsIgnoreCase("com.app.noti"))
            {
               /* new GetTask_Api().get_task_List_api(MainActivity.context);*/
                if (preferences.getString(SplashScreen.NOTIFICATION_TYPE, "").equalsIgnoreCase("pr")) {

                    gotRide(); // when the driver will get the ride this method will be invoked to show locations and accept and decline btns

                }
            }
        }
    }



}
