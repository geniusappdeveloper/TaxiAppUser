package com.app.taxi.driver;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.app.taxi.driver.commonFunctions.NetworkUtil;
import com.app.taxi.driver.stripe.activity.PaymentActivity;
import com.app.taxi.driver.volleyapi.CommonFunctions;
import com.app.taxi.driver.volleyapi.OnApihit;
import com.app.taxi.driver.volleyapi.VolleyBase;
import com.hbb20.CountryCodePicker;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;

public class ProfileScreen extends AppCompatActivity implements OnApihit {

    private static final int SELECT_FILE = 0;
    private static final String IMAGE_DIRECTORY_NAME = "";
    Bitmap bm = null;
    String profile_pic_path , profile_pic_path_licence;
    File f;
    private static final int REQUEST_CAMERA = 1;
    private Intent navIntent;
    Uri selectedImage;
    int checkButtonClick=0;
    ImageView profilescreen_backgroundimage;
    EditText editProFirstName, editProLastName, editProEmail, editProPhoneNum, editProCarNum, editSeatCapacity, editProSecurityNum, editPromake,
            editProModel;
    EditText editPhoneNum;
    Button Profilebtn ,uploadLicense_btn,uploadCardDetails_btn;
    public static CircleImageView profilescreen_circleView , licensePic_circleView;
    LinearLayout profileEdit;
    String strFirstNameProfile, strLastName, strEmailProfile, strPhonenumProfile, strCarNum, strSeatCapacity, strSecurityNum, strMake, strModel;
    private SharedPreferences preferences;
    String user_id;
    SharedPreferences.Editor editor ;

    private final int IMG_REQUEST = 1;
    Intent galleryIntent;
    CountryCodePicker registerccp;
    TextView id_user_name_drawer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_screen);
        preferences = getSharedPreferences(SplashScreen.PREFERENCES, Context.MODE_PRIVATE);
        Utility.checkPermission(ProfileScreen.this);
        Profilebtn = (Button) findViewById(R.id.save_profile_btn);
        uploadLicense_btn = (Button)findViewById(R.id.uploadLicense_btn);
        uploadCardDetails_btn = (Button) findViewById(R.id.uploadCardDetails_btn);
        profilescreen_circleView = (CircleImageView) findViewById(R.id.profilescreen_circleView);
        licensePic_circleView =  (CircleImageView) findViewById(R.id.licensePic_circleView);
        editSeatCapacity=(EditText)findViewById(R.id.profilescreen_carcapacity_editbox);
        profilescreen_backgroundimage = (ImageView) findViewById(R.id.profilescreen_backgroundimage);
        profileEdit = (LinearLayout) findViewById(R.id.profilescreen_edit_layout);
        user_id = preferences.getString(SplashScreen.DriverID, "");
        editProFirstName = (EditText) findViewById(R.id.profilescreen_firstname_editbox);
        editProLastName = (EditText) findViewById(R.id.profilescreen_lastname_editbox);
        editProEmail = (EditText) findViewById(R.id.profilescreen_email_editbox);
        editProPhoneNum = (EditText) findViewById(R.id.profilescreen_phonenum_editbox);
        editProCarNum = (EditText) findViewById(R.id.profilescreen_carnum_editbox);
       // editProLicenseNum = (EditText) findViewById(R.id.profilescreen_licensenum_editbox);
        editProSecurityNum = (EditText) findViewById(R.id.profilescreen_socialsecnum_editbox);
        editPromake = (EditText) findViewById(R.id.profilescreen_make_editbox);
        editProModel = (EditText) findViewById(R.id.profilescreen_model_editbox);
        id_user_name_drawer = (TextView) findViewById(R.id.id_user_name_drawer);
        editor = preferences.edit();
        // registerccp = (CountryCodePicker) findViewById(R.id.register_ccp);
        // registerccp.hideNameCode(true);



        //----------------get profile api hit------------------------------------------------
        Map<String, String> params = new HashMap<>();
        params.put("user_id", user_id);

        if (NetworkUtil.getConnectivityStatus(ProfileScreen.this)) {
            new VolleyBase(ProfileScreen.this).main(params, "GetProfile", 2);
        } else {
            NetworkUtil.openAlert(ProfileScreen.this);
        }


       /* //______________SetText_________________________
        editProFirstName.setText(preferences.getString(SplashScreen.DriverFirstName, ""));
        editProLastName.setText(preferences.getString(SplashScreen.DriverLastName, ""));
        editProEmail.setText(preferences.getString(SplashScreen.DriverEmail, ""));
        editProPhoneNum.setText(preferences.getString(SplashScreen.DriverPhoneNumber, ""));*/
    }

    public void profileSwitch(View v) {
        strFirstNameProfile = editProFirstName.getText().toString().trim();
        strLastName = editProLastName.getText().toString().trim();
        strEmailProfile = editProEmail.getText().toString().trim();
        strPhonenumProfile = editProPhoneNum.getText().toString().trim();
        strCarNum = editProCarNum.getText().toString().trim();
       // strLicenseNum = editProLicenseNum.getText().toString().trim();
        strMake = editPromake.getText().toString().trim();
        strSeatCapacity = editSeatCapacity.getText().toString().trim();
        strModel = editProModel.getText().toString().trim();
        strSecurityNum = editProSecurityNum.getText().toString().trim();
        switch (v.getId()) {
            case R.id.profilescreen_back_layout:
                navIntent = new Intent(ProfileScreen.this, HomeScreen.class);
                /*loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);*/
                startActivity(navIntent);
                finish();
                break;
            case R.id.profilescreen_circleView:

                selectImage();
                break;
            case R.id.profilescreen_edit_layout:
                Profilebtn.setVisibility(View.VISIBLE);
                profileEdit.setVisibility(View.GONE);
                enableAll(); //  enabling every field to edit on profile screen
                break;
            case R.id.uploadLicense_btn:
               // selectImage_licence();
             //   selectImage(checkButtonClick);

                break;
            case R.id.uploadCardDetails_btn:
                Intent cardIntent = new Intent(ProfileScreen.this, AddCardDetails.class);
                /*loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);*/
                startActivity(cardIntent);
             break;
            case R.id.save_profile_btn:
                if (strFirstNameProfile.equalsIgnoreCase("")) {
                    editProFirstName.setError("Enter First Name");
                    CommonFunctions.requestFocus(editProFirstName, ProfileScreen.this);
                } else if (strLastName.equalsIgnoreCase("")) {
                    editProLastName.setError("Enter Last Name");
                    CommonFunctions.requestFocus(editProLastName, ProfileScreen.this);
                } else if (strEmailProfile.equalsIgnoreCase("")) {
                    editProEmail.setError("Enter Email");
                    CommonFunctions.requestFocus(editProEmail, ProfileScreen.this);
                } else if (!(CommonFunctions.isValidEmail(strEmailProfile))) {
                    editProEmail.setError("Enter Valid Email");
                    CommonFunctions.requestFocus(editProEmail, ProfileScreen.this);
                } else if (strPhonenumProfile.equalsIgnoreCase("")) {
                    editProPhoneNum.setError("Enter Phone Number");
                    CommonFunctions.requestFocus(editProPhoneNum, ProfileScreen.this);
                } else if (!(strPhonenumProfile.length() >= 10 && strPhonenumProfile.length() <= 15)) {
                    editProPhoneNum.setError("Phone Number must be between 10-15");
                    CommonFunctions.requestFocus(editProPhoneNum, ProfileScreen.this);
                } else if (!(CommonFunctions.isValidPhone(strPhonenumProfile))) {
                    editProEmail.setError("Enter Valid Phone Number");
                    CommonFunctions.requestFocus(editProPhoneNum, ProfileScreen.this);
                } else if (strCarNum.equalsIgnoreCase("")) {
                    editProCarNum.setError("Enter Car Number");
                    CommonFunctions.requestFocus(editProCarNum, ProfileScreen.this);
                } else if (strSeatCapacity.equalsIgnoreCase("")) {
                    editSeatCapacity.setError("Enter Car seat capacity");
                    CommonFunctions.requestFocus(editSeatCapacity, ProfileScreen.this);
               }
                else if (strMake.equalsIgnoreCase("")) {
                    editPromake.setError("Enter Make");
                    CommonFunctions.requestFocus(editPromake, ProfileScreen.this);
                } else if (strModel.equalsIgnoreCase("")) {
                   editProModel.setError("Enter Model");
                   CommonFunctions.requestFocus(editProModel, ProfileScreen.this);
                }
                else if (strSecurityNum.equalsIgnoreCase("")) {
                    editProSecurityNum.setError("Enter Social Security Number");
                    CommonFunctions.requestFocus(editProSecurityNum, ProfileScreen.this);
                } else {

                    Add_Attachment_Image_API add_attachment_image_api = new Add_Attachment_Image_API(
                            ProfileScreen.this,


                            preferences.getString(SplashScreen.DriverID, ""), profile_pic_path/*,profile_pic_path_licence*/);
                    add_attachment_image_api.execute(preferences.getString(SplashScreen.DriverID, ""), profile_pic_path/*,profile_pic_path_licence*/);
                    // CommonFunctions.profilePicPath = profile_pic_path;
//____________________________________License pic upload____________________________

                    Map<String, String> params = new HashMap<>();

                    Log.e("lllll1", strFirstNameProfile);
                    Log.e("lllll2", strLastName);
                    Log.e("lllll3", strPhonenumProfile);
                    Log.e("lllll4", strCarNum);

                    Log.e("lllll5", user_id);

                    Log.e("lllll6", strMake);
                    Log.e("lllll7", strModel);
                    Log.e("lllll8", strSecurityNum);

                    //  strLicenseNum

                    params.put("user_id", user_id);
                    params.put("first name", strFirstNameProfile); // sending phone num
                    params.put("last_name", strLastName);//sending password
                    //==  params.put("country_code", ccp.getSelectedCountryCodeWithPlus().trim()); //sending countrycode
                    //==   params.put("phone_number", strLoginPhonenum); // sending phone num
                    //==   params.put("car_number", strLoginPassword);//sending password
                    //     params.put("country_code",registerccp.getSelectedCountryCodeWithPlus().trim()); //sending countrycode
                    params.put("phone_number", strPhonenumProfile); // sending phone num
                    params.put("car_number", strCarNum);//sending password
                    //==============================
               //     params.put("license_info", strLicenseNum);
                    params.put("car_capacity" , strSeatCapacity);
                    params.put("make", strMake);
                    params.put("model", strModel);
                    params.put("social_security_number", strSecurityNum);


                //    Log.e("strLicenseNum", strLicenseNum);

                    if (NetworkUtil.getConnectivityStatus(ProfileScreen.this)) {
                        new VolleyBase(ProfileScreen.this).main(params, "EditProfile", 1);

                       /* nextscreen = new Intent(LoginScreen.this, HomeScreen.class);
                        nextscreen.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(nextscreen);*/
                    } else {
                        NetworkUtil.openAlert(this);
                    }
                    Log.e("user_id", user_id);
   // Log.e("profile_pic_path", profile_pic_path);

                    disableAll();
                }
        }
    }

    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Gallery",
                "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileScreen.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result = Utility.checkPermission(ProfileScreen.this);
                String userChoosenTask;
                if (items[item].equals("Take Photo")) {
                    userChoosenTask = "Take Photo";
                    if (result)
                        cameraIntent();
                //    licensePic_circleView.setVisibility(View.VISIBLE);
                    Toast.makeText(ProfileScreen.this, userChoosenTask, Toast.LENGTH_SHORT).show();
                } else if (items[item].equals("Choose from Gallery")) {
                    userChoosenTask = "Choose from Gallery";
                    if (result)
                        Toast.makeText(ProfileScreen.this, userChoosenTask, Toast.LENGTH_SHORT).show();
                    galleryIntent();
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }


    private void selectImage_licence() {
        final CharSequence[] items = {"Take Photo", "Choose from Gallery",
                "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileScreen.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result = Utility.checkPermission(ProfileScreen.this);
                String userChoosenTask;
                if (items[item].equals("Take Photo")) {
                    userChoosenTask = "Take Photo";
                    if (result)
                       // cameraIntent();
                        cameraIntent_licence();

                    //    licensePic_circleView.setVisibility(View.VISIBLE);
                    Toast.makeText(ProfileScreen.this, userChoosenTask, Toast.LENGTH_SHORT).show();
                } else if (items[item].equals("Choose from Gallery")) {
                    userChoosenTask = "Choose from Gallery";
                    if (result)
                        galleryIntent_licence();
                        Toast.makeText(ProfileScreen.this, userChoosenTask, Toast.LENGTH_SHORT).show();
                    //galleryIntent();
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }


    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    private void cameraIntent_licence() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 3);
    }

    private void galleryIntent() {
        Log.e("galleryPath", "kkkkkkkkkkkkkkkkkkkkkkkkk");
        //  Intent intent = new Intent();
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 2);
       /* intent.setType("image*//*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(intent, 2);
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);*/
    }
    private void galleryIntent_licence() {
        Log.e("galleryPath", "kkkkkkkkkkkkkkkkkkkkkkkkk");
        //  Intent intent = new Intent();
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 4);
       /* intent.setType("image*//*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(intent, 2);
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);*/
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 2) {
                Log.e("galleryPath", "ggggggggggggggggg");
                {

                    //==========changes=============
                    {

                        Uri selectedImage = data.getData();
                        String[] filePath = {MediaStore.Images.Media.DATA};
                        Log.e("DATA_FILE", filePath + "");
                        Cursor c = getContentResolver().query(selectedImage, filePath, null, null, null);
                        c.moveToFirst();
                        int columnIndex = c.getColumnIndex(filePath[0]);
                        String picturePath = c.getString(columnIndex);
                        c.close();
                        Log.e("3", "enterd3");
                        Bitmap thumbnail = (BitmapFactory.decodeFile(picturePath));
                        profile_pic_path = picturePath;

                        //    f = storeImage(thumbnail);


                        Log.e("PARAMS", thumbnail + "");
                        Log.e("PARAMS", picturePath);
                        Log.e("efgervgfervfg",selectedImage+" ggg");
                        Picasso.with(getApplicationContext()).load(selectedImage).into(profilescreen_circleView);
                    }

                    //==================================
                }
            } else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
            //---save image on restart-----------------------------
            //   img_edt.setImageResource(R.drawable.save);
            //   Toast.makeText(ProfileScreen.this, "Call camera picture", Toast.LENGTH_SHORT).show();
        }
        else if (requestCode == 3)
            onCaptureImageResult_licnce(data);
        //---save image on restart-----------------------------
        //   img_edt.setImageResource(R.drawable.save);
        //   Toast.makeText(ProfileScreen.this, "Call camera picture", Toast.LENGTH_SHORT).show();
        if (requestCode == 4) {
            Log.e("galleryPath", "ggggggggggggggggg");
            {
                //==========changes=============
                {

                    Uri selectedImage = data.getData();
                    String[] filePath = {MediaStore.Images.Media.DATA};
                    Log.e("DATA_FILE", filePath + "");
                    Cursor c = getContentResolver().query(selectedImage, filePath, null, null, null);
                    c.moveToFirst();
                    int columnIndex = c.getColumnIndex(filePath[0]);
                    String picturePath = c.getString(columnIndex);
                    c.close();
                    Log.e("3", "enterd3");
                    Bitmap thumbnail = (BitmapFactory.decodeFile(picturePath));
                    profile_pic_path_licence = picturePath;

                    //    f = storeImage(thumbnail);

                    Log.e("PARAMS", thumbnail + "");
                    Log.e("PARAMS", picturePath);
                    Picasso.with(getApplicationContext()).load(selectedImage).into(licensePic_circleView);
                }

                //==================================
            }
        }
    }

    //-------to create file from bitmap(converting bitmap into file)-----------------------
    public File storeImage(Bitmap image) {
        File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
        Log.e("NEW FILE", pictureFile.toString());
        f = new File(pictureFile.toString());
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            image.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        } catch (FileNotFoundException e) {
            Log.d("Error", "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d("Error", "Error accessing file: " + e.getMessage());
        }
        return pictureFile;
    }

    //------------to store image to file method(Gallery)----------------------
    private File getOutputMediaFile(int type) {
        //  Food IMAGE_DIRECTORY_NAME;
        // External sdcard location
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), IMAGE_DIRECTORY_NAME);


        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
             /*   Log.d(IMAGE_DIRECTORY_NAME, "Oops! Failed create "
                        + IMAGE_DIRECTORY_NAME + " directory");*/
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
        } else {
            return null;
        }
        return mediaFile;
    }

    //------------camera method----------------------------------------------
    private void onCaptureImageResult(Intent data) {
       {
            bm = (Bitmap) data.getExtras().get("data");
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
            File destination = new File(Environment.getExternalStorageDirectory(),
                    System.currentTimeMillis() + ".jpg");
            profile_pic_path = destination.getAbsolutePath();
            Log.e("pic_profile_camera", profile_pic_path);
            Picasso.with(getApplicationContext()).load(profile_pic_path).into(profilescreen_circleView);
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
            profilescreen_circleView.setImageBitmap(bm);

        }


    }

    //------------camera method----------------------------------------------
    private void onCaptureImageResult_licnce(Intent data) {
        {
            bm = (Bitmap) data.getExtras().get("data");
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
            File destination = new File(Environment.getExternalStorageDirectory(),
                    System.currentTimeMillis() + ".jpg");
            profile_pic_path_licence = destination.getAbsolutePath();
            Log.e("pic_profile_camera", profile_pic_path_licence);
            Picasso.with(getApplicationContext()).load(profile_pic_path_licence).into(licensePic_circleView);
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
            licensePic_circleView.setImageBitmap(bm);

        }


    }
    /* public String imageToString(Bitmap bitmap){
         ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
         bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
         byte[] imageBytes = byteArrayOutputStream.toByteArray();
         return Base64.encodeToString(imageBytes,Base64.DEFAULT);
     }*/
    public void enableAll() {
        editProFirstName.setEnabled(true);
        editProEmail.setEnabled(true);
        editProPhoneNum.setEnabled(true);
        editProCarNum.setEnabled(true);
       // editProLicenseNum.setEnabled(true);
        editProLastName.setEnabled(true);
        editProModel.setEnabled(true);
        editPromake.setEnabled(true);
        editSeatCapacity.setEnabled(true);
        uploadLicense_btn.setEnabled(true);
        uploadCardDetails_btn.setEnabled(true);
        editProSecurityNum.setEnabled(true);
            profilescreen_circleView.setEnabled(true);
    }

    public void disableAll() {
        Profilebtn.setVisibility(View.GONE);
        uploadLicense_btn.setEnabled(false);
        uploadCardDetails_btn.setEnabled(false);
        profileEdit.setVisibility(View.VISIBLE);
        editSeatCapacity.setEnabled(false);
        editProLastName.setEnabled(false);
        editProModel.setEnabled(false);
        editPromake.setEnabled(false);
        editProFirstName.setEnabled(false);
        editProEmail.setEnabled(false);
          profilescreen_circleView.setEnabled(false);
        editProPhoneNum.setEnabled(false);
        editProCarNum.setEnabled(false);
//        editProLicenseNum.setEnabled(false);
        editProSecurityNum.setEnabled(false);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        navIntent = new Intent(ProfileScreen.this, HomeScreen.class);
        startActivity(navIntent);
        finish();

    }

    @Override
    public void success(String Response, int index) throws JSONException {

        if (index == 1) {
            //  JSONObject jsonObject=new JSONObject(Response);

        }

        if (index == 2) {

            JSONObject jsonObject = new JSONObject(Response);
            String message = jsonObject.getString("message");
            Log.e("grtwkfervg", Response.toString());

            JSONObject jsonObject1 = jsonObject.getJSONObject("result");

            String f_name = jsonObject1.getString("first_name");
            String l_name = jsonObject1.getString("last_name");
            String id = jsonObject1.getString("id");
            String email = jsonObject1.getString("email");
            String phone_num = jsonObject1.getString("phone_number");
            String country_code = jsonObject1.getString("country_code");
            String user_type = jsonObject1.getString("user_type");
            String car_Capacity = jsonObject1.getString("car_capacity");
            String profile_pic = jsonObject1.getString("profile_pic");
            String licence_pic = jsonObject1.getString("license_info");
            // CommonFunctions.profilePicPath = profile_pic;
            String car_number = jsonObject1.getString("car_number");
     //       String license_info = jsonObject1.getString("license_info");
            String make = jsonObject1.getString("make");
            String model = jsonObject1.getString("model");
            String social_security_number = jsonObject1.getString("social_security_number");

         //   Log.e("kjfngvr",license_info);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(SplashScreen.Driver_PIC_PATH , profile_pic);
            editor.putString(SplashScreen.DriverFirstName, f_name);
            editor.putString(SplashScreen.DriverLastName, l_name);
            editor.putString(SplashScreen.DriverPhoneNumber, phone_num);
            editor.putString(SplashScreen.CountryCode, country_code);
            editor.putString(SplashScreen.DriverID, id);
            editor.putString(SplashScreen.DriverEmail, email);
          //  editor.putString(SplashScreen.CAR_CAPACITY,car_Capacity);
           // editor.putString(SplashScreen.DeviceProfilePic, profile_pic);
            editor.putString(SplashScreen.UserType, user_type);
            editor.putString(SplashScreen.Driver_License_Pic,licence_pic);

editor.putString(SplashScreen.CAR_CAPACITY ,car_Capacity );
            editor.putString(SplashScreen.car_number, car_number);
           // editor.putString(SplashScreen.license_info, licence_pic);
            editor.putString(SplashScreen.make, make);
            editor.putString(SplashScreen.model, model);
            editor.putString(SplashScreen.social_security_number, social_security_number);
            editor.commit();




            //______________SetText_________________________
            editProFirstName.setText(preferences.getString(SplashScreen.DriverFirstName, ""));
            editProLastName.setText(preferences.getString(SplashScreen.DriverLastName, ""));
            editProEmail.setText(preferences.getString(SplashScreen.DriverEmail, ""));
            editProPhoneNum.setText(preferences.getString(SplashScreen.DriverPhoneNumber, ""));
          editSeatCapacity.setText(preferences.getString(SplashScreen.CAR_CAPACITY,""));
            id_user_name_drawer.setText(preferences.getString(SplashScreen.DriverFirstName, ""));


            if (profile_pic!=null)
            {
                Picasso.with(getApplicationContext()).load(profile_pic).into(profilescreen_circleView);
            }
            else {
                Picasso.with(getApplicationContext()).load(R.drawable.profile_pic).into(profilescreen_circleView);
            }

            /*if(licence_pic!=null){
                Picasso.with(getApplicationContext()).load(licence_pic).into(licensePic_circleView);
            }

            else {
                Picasso.with(getApplicationContext()).load(R.drawable.profile_pic).into(licensePic_circleView);
            }*/
            editProCarNum.setText(preferences.getString(SplashScreen.car_number, ""));
           // editProLicenseNum.setText(preferences.getString(SplashScreen.license_info, ""));
            editProSecurityNum.setText(preferences.getString(SplashScreen.social_security_number, ""));
            editPromake.setText(preferences.getString(SplashScreen.make, ""));
            editProModel.setText(preferences.getString(SplashScreen.model, ""));



            hideSoftKeyboard();

        }


    }












    @Override
    public void error(VolleyError error, int index) {

    }


    public void hideSoftKeyboard()
    {
        if(getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }
}
