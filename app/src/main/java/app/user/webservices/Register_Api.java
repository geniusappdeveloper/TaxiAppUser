package app.user.webservices;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import app.user.global.Global;

/**
 * Created by user1 on 12/27/2017.
 */

public class Register_Api {
    String message;
    ProgressDialog dialog;

    public void reg_Api(final Context context, final String f_name, final String l_name, final String email, final String code, final String phone, final String password) {

        dialog = new ProgressDialog(context);
        dialog.setMessage("Please Wait..");

        dialog.show();

        final RequestQueue requestQueue = Volley.newRequestQueue(context);

        StringRequest myRqst = new StringRequest(Request.Method.POST, Global.BASE_URL + "login", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (requestQueue == null) {
                    Log.e("Response", response);
                    try {

                        dialog.dismiss();


                        JSONObject json = new JSONObject(response);


                        String status = json.getString("status");

                        message = json.getString("status_message");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    System.out.println("Success " + response);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "Login " + error, Toast.LENGTH_SHORT).show();


                System.out.println("Error " + error);
                dialog.dismiss();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("phone_number", phone);
                params.put("country_code", code);
                params.put("password", password);

                Log.e("param_register", params.toString());

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
