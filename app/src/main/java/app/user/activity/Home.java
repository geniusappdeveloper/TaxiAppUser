package app.user.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import app.taxiapp.user.R;
import app.user.global.Global;
import app.user.global.MyFirebaseInstanceIDService;
import app.user.global.NetworkUtil;
import app.user.strip.activity.PaymentActivity;
import app.user.volleyapi.OnApihit;
import app.user.volleyapi.VolleyBase;
import app.user.webservices.CheckRequestStatus;
import de.hdodenhof.circleimageview.CircleImageView;


public class Home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, OnApihit,
        OnMapReadyCallback, LocationListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    public static RelativeLayout menu, pick, footer_fare, final_address, driver_details;
    public static LinearLayout footer_recent, l_personal, l_plus;
    public static RatingBar ratingbar;
    Button confirm;
    public static TextView edit_profile, personal_price, plus_price, final_pickup, final_drop, txt_personal, txt_plus, drivername, driver_number, rating_txt, car_details;
    ImageView img_pers, img_plus;
    String value;
    public static GoogleMap googleMap;
    LocationRequest mLocationRequest;
    SharedPreferences sharedPreferences;
    GoogleApiClient mGoogleApiClient;
    private static final long INTERVAL = 1000 * 10;
    private static final long FASTEST_INTERVAL = 1000 * 5;
    Location mCurrentLocation;
    LocationManager locManager;
    double lat;
    double lng;
    LatLng latLng;
    public static Boolean isRunning = false;
    public static Context context;
    ProgressDialog progress_dialog;
    public static CircleImageView driver_image;
    String driver_name;
    Context con;
    Handler mHandler;
    public static Runnable mUpdateTimeTask;
    Handler handler;
    CircleImageView profile_pic;
    TextView name;
    MyFirebaseInstanceIDService service;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        context = Home.this;
        progress_dialog = new ProgressDialog(Home.this);
        service = new MyFirebaseInstanceIDService(Home.this);
        sharedPreferences = getSharedPreferences(Global.pref_name, Context.MODE_PRIVATE);
        value = getIntent().getStringExtra("i");
        Log.e("on", "home");
        checkLocationPermission();

        if (!isGooglePlayServicesAvailable()) {
            Log.e("on", "home finish");
            finish();
        }


        createLocationRequest();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerview = navigationView.getHeaderView(0);
        profile_pic = (CircleImageView) headerview.findViewById(R.id.profile_pic);
        name = (TextView) headerview.findViewById(R.id.name);
        edit_profile = (TextView) headerview.findViewById(R.id.edit_profile);


        name.setText(sharedPreferences.getString("name", ""));
        profile_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};
                AlertDialog.Builder builder = new AlertDialog.Builder(Home.this);
                builder.setTitle("Add Photo!");
                builder.setItems(options, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (options[item].equals("Take Photo")) {

                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(intent, 1);


                        } else if (options[item].equals("Choose from Gallery")) {
                            Log.e("1", "enterd1");
                            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(intent, 2);
                            Log.e("4", "enterd4");
                        } else if (options[item].equals("Cancel")) {
                            dialog.dismiss();
                        }
                    }
                });
                builder.show();
            }
        });


        menu = (RelativeLayout) findViewById(R.id.menu);
        pick = (RelativeLayout) findViewById(R.id.pick);
        footer_fare = (RelativeLayout) findViewById(R.id.footer_fare);
        personal_price = (TextView) findViewById(R.id.personal_price);
        plus_price = (TextView) findViewById(R.id.plus_price);
        final_pickup = (TextView) findViewById(R.id.final_pickup);
        final_drop = (TextView) findViewById(R.id.final_drop);
        final_address = (RelativeLayout) findViewById(R.id.final_address);
        footer_recent = (LinearLayout) findViewById(R.id.footer_recent);
        confirm = (Button) findViewById(R.id.confirm_ride);
        l_personal = (LinearLayout) findViewById(R.id.l_personal);
        l_plus = (LinearLayout) findViewById(R.id.l_plus);
        img_pers = (ImageView) findViewById(R.id.img_pers);
        img_plus = (ImageView) findViewById(R.id.img_plus);
        txt_personal = (TextView) findViewById(R.id.txt_personal);
        txt_plus = (TextView) findViewById(R.id.txt_plus);
        driver_details = (RelativeLayout) findViewById(R.id.driver_details);
        driver_image = (CircleImageView) findViewById(R.id.driver_image);
        drivername = (TextView) findViewById(R.id.drivername);
        rating_txt = (TextView) findViewById(R.id.rating_txt);
        car_details = (TextView) findViewById(R.id.car_details);
        driver_number = (TextView) findViewById(R.id.driver_number);
        ratingbar = (RatingBar) findViewById(R.id.myRatingBar);


        footer_recent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Home.this, PaymentActivity.class);
                startActivity(i);
            }
        });


        if (value.equalsIgnoreCase("fare")) {

            final_address.setVisibility(View.VISIBLE);
            pick.setVisibility(View.GONE);
            footer_fare.setVisibility(View.VISIBLE);
            footer_recent.setVisibility(View.GONE);
            Log.e("to check", "print " + Global.AddressFromLatLng(Home.this, Double.parseDouble(Global.getFair().get(0).get("dest_lat")), Double.parseDouble(Global.getFair().get(0).get("dest_lng"))));
            personal_price.setText(Global.getFair().get(0).get("Personal"));
            plus_price.setText(Global.getFair().get(0).get("Plus"));
            final_pickup.setText(Global.pickup_add);
            final_drop.setText(Global.drop_add);
        }
        if (value.equalsIgnoreCase("noti")) {

            CheckRequestStatus c = new CheckRequestStatus();
            c.check(Home.this, sharedPreferences.getString("user_id", ""), sharedPreferences.getString("request_id", ""));
        }


        FragmentManager fmanager = getSupportFragmentManager();
        Fragment fragment = fmanager.findFragmentById(R.id.map);
        SupportMapFragment mapFragment = (SupportMapFragment) fragment;
        mapFragment.getMapAsync(Home.this);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);

        //------------------NEARBYDRIVERS-------------
        Map<String, String> paramss = new HashMap<>();

        paramss.put("user_id", sharedPreferences.getString("user_id", ""));

        Log.e("GetNearDriver params", " print  " + paramss);
        if (NetworkUtil.getConnectivityStatus(Home.this)) {

            new VolleyBase(Home.this).main(paramss, "GetNearDriver", 2);

        } else {
            NetworkUtil.openAlert(Home.this);
        }
        //-----------------
//---------------------------------------

        Map<String, String> params = new HashMap<>();

        params.put("user_id", sharedPreferences.getString("user_id", ""));
        params.put("device_token", Global.device_id);
        params.put("device_type", "A");
        params.put("latitude", String.valueOf(lat));
        params.put("longitude", String.valueOf(lng));

        Log.e("params", " print  " + params);
        if (NetworkUtil.getConnectivityStatus(Home.this)) {
            if (!Global.device_id.equalsIgnoreCase("")) {
                new VolleyBase(Home.this).main(params, "GetDeviceToken", 1);
            }
        } else {
            NetworkUtil.openAlert(Home.this);
        }
//----------------------------

        l_personal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("click", "personal");
                Global.car_type = "Personal";
                txt_personal.setTextColor(Color.BLACK);
                txt_plus.setTextColor(Color.GRAY);
                img_pers.setBackgroundResource(R.drawable.personal_black);
                img_plus.setBackgroundResource(R.drawable.plus_gray);
                personal_price.setTextColor(Color.BLACK);
                plus_price.setTextColor(Color.GRAY);
            }
        });


        l_plus.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.e("click", "plus");
                Global.car_type = "Plus";
                txt_personal.setTextColor(Color.GRAY);
                txt_plus.setTextColor(Color.BLACK);
                img_pers.setBackgroundResource(R.drawable.personal_gray);
                img_plus.setBackgroundResource(R.drawable.plus_black);
                personal_price.setTextColor(Color.GRAY);
                plus_price.setTextColor(Color.BLACK);
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progress_dialog.show();
                progress_dialog.setMessage("Seaching nearby drivers");
                progress_dialog.setCancelable(false);

                footer_fare.setVisibility(View.GONE);
                //------------CONFIRM RIDE API------

                Map<String, String> params = new HashMap<>();

                params.put("user_id", sharedPreferences.getString("user_id", ""));
                params.put("source_lat", Global.getFair().get(0).get("source_lat"));
                params.put("source_lng", Global.getFair().get(0).get("source_lng"));
                params.put("dest_lat", Global.getFair().get(0).get("dest_lat"));
                params.put("dest_lng", Global.getFair().get(0).get("dest_lng"));
                params.put("category", Global.car_type);
                params.put("payment_mode", "CC");
                params.put("source_location", Global.pickup_add);
                params.put("dest_location", Global.drop_add);

                Log.e("CONFIRM RIDE params", " print  " + params);
                if (NetworkUtil.getConnectivityStatus(Home.this)) {

                    new VolleyBase(Home.this).main(params, "SendRequest", 3);

                } else {
                    NetworkUtil.openAlert(Home.this);
                }
            }
        });

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.openDrawer(GravityCompat.START);

            }
        });


        pick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(Home.this, GooglePlacesAutocompleteActivity.class);
                startActivity(i);
            }
        });


        edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Home.this, Profile.class);
                startActivity(i);
            }
        });

        //------------------------HANDLER----------------------------

        mUpdateTimeTask = new Runnable() {

            @Override
            public void run() {
                // Log.e("HANDLER", "IN");

                try {
                    // isRunning = true;

                    if (isRunning) {
                        Log.e("HANDLER", "START");
                        CheckRequestStatus c = new CheckRequestStatus();
                        c.check(Home.this, sharedPreferences.getString("user_id", ""), sharedPreferences.getString("request_id", ""));
                    }

                } catch (OutOfMemoryError ee) {

                } catch (Exception e) {

                    e.printStackTrace();
                }
                handler.postDelayed(this, 20000);
            }
        };

        handler = new Handler();
        handler.removeCallbacks(mUpdateTimeTask);
        handler.post(mUpdateTimeTask);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.home) {
            Intent i = new Intent(Home.this, Home.class);
            i.putExtra("i", "");
            startActivity(i);
        }
        if (id == R.id.history) {
            Intent i = new Intent(Home.this, RideHistory.class);
            i.putExtra("i", "");
            startActivity(i);
        }
        if (id == R.id.settings) {
            Intent i = new Intent(Home.this, Settings.class);
            i.putExtra("i", "");
            startActivity(i);
            finish();
        }

        return true;
    }

    @Override
    public void onBackPressed() {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
        finish();

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.e("HOME", "ONMAP READY");
        this.googleMap = googleMap;
        googleMap.setMyLocationEnabled(true);

        if (value.equalsIgnoreCase("fare")) {
            Global.drawpath(context, Global.pickup_latLng, Global.drop_latlng, Home.googleMap);
        }
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        startLocationUpdates();
    }

    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(Home.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Home.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                Log.e("prermission 1", "granted");
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new android.app.AlertDialog.Builder(this)
                        .setTitle("title_location_permission")
                        .setMessage("location_permission")
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(Home.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                Log.e("prermission 2", "granted");
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            //return false;
        }
        PendingResult<Status> pendingResult = LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, Home.this);
        Log.e("HOME", "Location update started ..............: ");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e("onConnectionSuspended 1", "onConnectionSuspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e("onConnectionFailed 1", "onConnectionFailed");

    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;

        updateUI();
        Log.e("HOME", "onLocationChanged");
    }

    private boolean isGooglePlayServicesAvailable() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            GooglePlayServicesUtil.getErrorDialog(status, this, 0).show();
            return false;
        }
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Log.d(TAG, "onStart fired ..............");
        mGoogleApiClient.connect();
        Log.e("HOME", "onStart");
    }

    @Override
    public void onStop() {
        super.onStop();
        //   Log.d(TAG, "onStop fired ..............");
        mGoogleApiClient.disconnect();
        Log.e("HOME", "onStop");
        //  Log.d(TAG, "isConnected ...............: " + mGoogleApiClient.isConnected());
    }


    private void updateUI() {

        Log.e("HOME", "updateUI");

        if (null != mCurrentLocation) {
            lat = mCurrentLocation.getLatitude();
            lng = mCurrentLocation.getLongitude();

            Global.lat = lat;
            Global.lng = lng;
            Global.Loc_name = mCurrentLocation.toString();


            Log.e("lat", "latitude: " + lat);
            Log.e("long", "longitude: " + lng);

            latLng = new LatLng(lat, lng);
            Log.e("latlong: ", latLng.toString());

            try {
                if (Global.map_sts.equalsIgnoreCase("1")) {
                } else {
                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    googleMap.animateCamera(CameraUpdateFactory.zoomTo(11));
                    Log.e("HOME", "ONMAP READY  " + latLng);
                }

            } catch (NullPointerException e) {
            } catch (IllegalStateException e) {
            }

//------------DEVICE ID API------
         /*   Map<String, String> params = new HashMap<>();

            params.put("user_id", sharedPreferences.getString("user_id", ""));
            params.put("device_token", Global.device_id);
            params.put("device_type", "A");
            params.put("latitude", String.valueOf(lat));
            params.put("longitude", String.valueOf(lng));

            Log.e("params", " print  " + params);
            if (NetworkUtil.getConnectivityStatus(Home.this)) {
                if (!Global.device_id.equalsIgnoreCase("")) {
                    new VolleyBase(Home.this).main(params, "GetDeviceToken", 1);
                }
            } else {
                NetworkUtil.openAlert(Home.this);
            }*/


        } else {
            Log.e("HOME", "location is null");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
            Log.e("HOME", "Location update resumed .....................");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            stopLocationUpdates();
        } catch (IllegalStateException e) {
        }
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, Home.this);
        Log.e("HOME", "Location update stopped .......................");
    }


    @Override
    public void success(String Response, int index) {

        if (index == 1) {
            Log.e("DEVICE ID", "Response" + Response);
        }
        if (index == 2) {
            Log.e("NearbyDrivers", "Response" + Response);

            try {
                JSONObject jsonObject = new JSONObject(Response);
                String status = jsonObject.getString("status");
                String message = jsonObject.getString("message");
                String result = jsonObject.getString("result");

                if (status.equalsIgnoreCase("1")) {

                    JSONArray array = new JSONArray(result);

                    for (int i = 0; i < array.length(); i++) {


                        JSONObject ob = array.getJSONObject(i);
                        String id = ob.getString("id");
                        String first_name = ob.getString("first_name");
                        String last_name = ob.getString("last_name");
                        String latitude = ob.getString("latitude");
                        String longitude = ob.getString("longitude");
                        String distance = ob.getString("distance");//distance.substring(0, 3) +
                        if (!latitude.equalsIgnoreCase("") && !longitude.equalsIgnoreCase("")) {
                            googleMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude))).title("KM").icon(BitmapDescriptorFactory.fromResource(R.drawable.car)));
                        }
                    }

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (index == 3) {
            Log.e("SendRequest", "Response" + Response);
            try {
                if (progress_dialog.isShowing()) {
                    progress_dialog.dismiss();
                }

                JSONObject jsonObject = new JSONObject(Response);
                String status = jsonObject.getString("status");
                String message = jsonObject.getString("message");
                String request_id = jsonObject.getString("request_id");

                footer_fare.setVisibility(View.GONE);
                Global.toast(Home.this, message);

                Global.request_id = request_id;

                if (message.equalsIgnoreCase("No Driver, please retry")) {

                    pick.setVisibility(View.VISIBLE);
                    final_address.setVisibility(View.GONE);
                    googleMap.clear();

                }

              /*  SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("request_id", request_id);
                editor.commit();
*/
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
            } catch (IllegalStateException e) {
            }
        }
        if (index == 4) {
            Log.e("Response", "Check  STATUS" + Response);
        }

    }

    @Override
    public void error(VolleyError error, int index) {
        Log.e("DEVICE ID", "error" + error);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {

            if (requestCode == 1) {

                Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
                File destination = new File(Environment.getExternalStorageDirectory(),
                        System.currentTimeMillis() + ".jpg");
                FileOutputStream fo;
                try {
                    destination.createNewFile();
                    fo = new FileOutputStream(destination);
                    fo.write(bytes.toByteArray());
                    fo.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String ProfilePic = destination.getAbsolutePath();


                Log.e("picturepath", "" + ProfilePic + "   " + bytes + "" + bytes.toByteArray());

                Glide.with(Home.this).load(ProfilePic).asBitmap().placeholder(R.drawable.progress_animation).into(profile_pic);
            } else if (requestCode == 2) {

                Log.e("2", "entered2");

                Uri selectedImage = data.getData();
                String[] filePath = {MediaStore.Images.Media.DATA};
                Cursor c = Home.this.getContentResolver().query(selectedImage, filePath, null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                String picturePath = c.getString(columnIndex);
                c.close();
                Log.e("3", "entered3");

                Bitmap thumbnail = (BitmapFactory.decodeFile(picturePath));
                Log.e("PARAMS", picturePath);
                Glide.with(Home.this).load(picturePath).placeholder(R.drawable.progress_animation).into(profile_pic);
            }
        }
    }

    public boolean checkLocationPermission() {
        Log.e("calling", "permission");
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                Log.e("prermission 1", "granted");
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new android.app.AlertDialog.Builder(this)
                        .setTitle("title_location_permission")
                        .setMessage("location_permission")
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(Home.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                Log.e("prermission 2", "granted");
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            Log.e("prermission", "granted");
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        Log.e("prermission", "prermission");
                        //Request location updates:
                        if (mGoogleApiClient.isConnected()) {
                            startLocationUpdates();
                            Log.e("HOME", "Location update resumed .....................");
                        }
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Log.e("prermission", " not prermission");
                }
                return;
            }

        }
    }
}
