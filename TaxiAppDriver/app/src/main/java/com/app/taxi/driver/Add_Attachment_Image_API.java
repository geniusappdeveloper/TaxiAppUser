package com.app.taxi.driver;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ListView;


import com.app.taxi.driver.volleyapi.CommonFunctions;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by Ravinder Pal Singh on 11/26/2016.
 */

public class Add_Attachment_Image_API extends AsyncTask<String, Void, String> {
    SharedPreferences sh;
    private URL connectURL;
    private String imagepath = "" ,  licensepath="";
    private String response;
    byte[] dataToServer;
    private String fname, about, lname;
    Context c;
    int status;
    private final ProgressDialog dialog;
    DataOutputStream dos = null;
    String jResponse, msg, product_id;
    String user_id,task_id,attachment_type,file;

    ArrayList<HashMap<String,String>> arry_attachments=new ArrayList<HashMap<String, String>>();

    String message="";
    ListView listView;

    String cat_id,subcat_id,sub_value,product_value,status_Stock;
    public Add_Attachment_Image_API(Context mContext, String user_id, String imagepath/*,String licensepath*/) {
        Log.e("manjot_res",imagepath+"");
        this.imagepath=imagepath;
        /*this.licensepath = licensepath;*/
        this.c = mContext;
        this.user_id = user_id;
        dialog = new ProgressDialog(c);

    }

    @Override
    protected void onPreExecute()
    {
        dialog.setMessage("Uploading.....");
        dialog.show();
        dialog.setCancelable(false);
        try {
            connectURL = new URL(CommonFunctions.URL+"EditProfile");
        } catch (Exception ex) {
            Log.i("URL FORMATION", "MALFORMATED URL");
        }
    }

    @Override
    protected String doInBackground(String... params) {
        FileInputStream fileInputStream = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        String Tag = "3rd";
        String existingfilename="";

        Log.e("FILE TYPE & FILE NAME", " "+imagepath);
    //    Log.e("FILE TYPE & FILE NAME", " "+licensepath);

        try {

            HttpURLConnection conn = (HttpURLConnection) connectURL.openConnection();

            conn.setDoInput(true);

            conn.setDoOutput(true);

            conn.setUseCaches(false);

            conn.setRequestMethod("POST");


            conn.setRequestProperty("Connection", "Keep-Alive");


            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);


            dos = new DataOutputStream(conn.getOutputStream());
            dos.writeBytes(twoHyphens + boundary + lineEnd);

            //---------post_id

            dos.writeBytes("Content-Disposition: form-data; name=\"user_id\"" + lineEnd);
            dos.writeBytes("Content-Type: text/plain;charset=UTF-8" + lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes("" + params[0] + lineEnd);
            dos.writeBytes(twoHyphens + boundary + lineEnd);



            Log.e("Data", " " + " " + imagepath);
          //  Log.e("Data", " " + " " + licensepath);
            //--------------image--------------
            if (imagepath.equals(""))
            {
                dos.writeBytes("Content-Disposition: form-data; name=\"image\";filename=\"" + "" + "\"" + lineEnd);
            }
            else
                {
                File f = new File(imagepath);
                fileInputStream = new FileInputStream(f);
                existingfilename = f.getName();
                dos.writeBytes("Content-Disposition: form-data; name=\"image\";filename=\"" + existingfilename + "\"" + lineEnd);

                if (existingfilename.endsWith(".jpg")) {
                    dos.writeBytes("Content-type: image/jpg;" + lineEnd);
                }
                if (existingfilename.endsWith(".png")) {
                    dos.writeBytes("Content-type: image/png;" + lineEnd);
                }
                if (existingfilename.endsWith(".gif")) {
                    dos.writeBytes("Content-type: image/gif;" + lineEnd);
                }
                if (existingfilename.endsWith(".jpeg")) {
                    dos.writeBytes("Content-type: image/jpeg;" + lineEnd);
                }

                if (existingfilename.endsWith(".doc")) {
                    dos.writeBytes("Content-type: document/doc;" + lineEnd);
                }
                if (existingfilename.endsWith(".txt")) {
                    dos.writeBytes("Content-type: text/txt;" + lineEnd);
                }
                if (existingfilename.endsWith(".mp4")) {
                    dos.writeBytes("Content-type: video/mp4;" + lineEnd);
                }
                if (existingfilename.endsWith(".avi")) {
                    dos.writeBytes("Content-type: video/avi;" + lineEnd);
                }
                if (existingfilename.endsWith(".ogg")) {
                    dos.writeBytes("Content-type: video/ogg;" + lineEnd);
                }
                if (existingfilename.endsWith(".3gp")) {
                    dos.writeBytes("Content-type: video/3gp;" + lineEnd);
                }
                if (existingfilename.endsWith(".mp3")) {
                    dos.writeBytes("Content-type: audio/mp3;" + lineEnd);
                }

                dos.writeBytes(lineEnd);

                int bytesAvailable = fileInputStream.available();
                int maxBufferSize = 1024*1024;
                int bufferSize = Math.min(bytesAvailable, maxBufferSize);
                byte[] buffer = new byte[bufferSize];

                int bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {
                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                }

                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
                Log.e(Tag, "File is written");
                fileInputStream.close();
            }

            /*if (licensepath.equals(""))
            {
                dos.writeBytes("Content-Disposition: form-data; name=\"image\";filename=\"" + "" + "\"" + lineEnd);
            }
            else
            {
                File f = new File(licensepath);
                fileInputStream = new FileInputStream(f);
                existingfilename = f.getName();
                dos.writeBytes("Content-Disposition: form-data; name=\"image\";filename=\"" + existingfilename + "\"" + lineEnd);

                if (existingfilename.endsWith(".jpg")) {
                    dos.writeBytes("Content-type: image/jpg;" + lineEnd);
                }
                if (existingfilename.endsWith(".png")) {
                    dos.writeBytes("Content-type: image/png;" + lineEnd);
                }
                if (existingfilename.endsWith(".gif")) {
                    dos.writeBytes("Content-type: image/gif;" + lineEnd);
                }
                if (existingfilename.endsWith(".jpeg")) {
                    dos.writeBytes("Content-type: image/jpeg;" + lineEnd);
                }

                if (existingfilename.endsWith(".doc")) {
                    dos.writeBytes("Content-type: document/doc;" + lineEnd);
                }
                if (existingfilename.endsWith(".txt")) {
                    dos.writeBytes("Content-type: text/txt;" + lineEnd);
                }
                if (existingfilename.endsWith(".mp4")) {
                    dos.writeBytes("Content-type: video/mp4;" + lineEnd);
                }
                if (existingfilename.endsWith(".avi")) {
                    dos.writeBytes("Content-type: video/avi;" + lineEnd);
                }
                if (existingfilename.endsWith(".ogg")) {
                    dos.writeBytes("Content-type: video/ogg;" + lineEnd);
                }
                if (existingfilename.endsWith(".3gp")) {
                    dos.writeBytes("Content-type: video/3gp;" + lineEnd);
                }
                if (existingfilename.endsWith(".mp3")) {
                    dos.writeBytes("Content-type: audio/mp3;" + lineEnd);
                }

                dos.writeBytes(lineEnd);

                int bytesAvailable = fileInputStream.available();
                int maxBufferSize = 1024*1024;
                int bufferSize = Math.min(bytesAvailable, maxBufferSize);
                byte[] buffer = new byte[bufferSize];

                int bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {
                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                }

                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
                Log.e(Tag, "File is written");
                fileInputStream.close();
            }*/
            InputStream is = conn.getInputStream();

            int ch;

            StringBuffer b = new StringBuffer();
            while ((ch = is.read()) != -1) {
                b.append((char) ch);
            }
            jResponse = b.toString();

            response = "true";
            dos.close();

            dos.flush();
            Log.e("Pic Upload ", jResponse);

        }

        catch (Exception e)
        {

            e.printStackTrace();
        }



        return jResponse;
    }
    protected void onPostExecute(String result) {
        super.onPostExecute(result);


//----------------------------------------------------
      /*   try {

          JSONObject jsonObject=new JSONObject(result);
            String status=jsonObject.getString("status");
            String message=jsonObject.getString("message");

            if (status.equalsIgnoreCase("1"))
            {
                Toast.makeText(c, message, Toast.LENGTH_SHORT).show();
                Log.e("UPLOADES",message);

                if (dialog.isShowing() && dialog != null)
                    dialog.dismiss();
            }
            else {
                Toast.makeText(c, message, Toast.LENGTH_SHORT).show();
                Log.e("UPLOADES",message);
                    dialog.dismiss();
            }
            
        } catch (JSONException e) {
            e.printStackTrace();
        }*/


       if (dialog.isShowing() && dialog != null)
        {
            dialog.dismiss();
        }

        }


    }

