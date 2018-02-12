package com.app.taxi.driver.volleyapi;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;

import java.util.Map;

/*import extras.Global;*/

/**
 * Created by Varinder on 7/22/2016.
 */
public class VolleyBase {
    String request;
    OnApihit api;
    public static String fulllink="";
    public VolleyBase(OnApihit api){
        this.api=api;
    }
    public String main(final Map<String, String> params, String link, final int index){



        //====================Its just for fetching result from google in Edit vacation rental fragment===========================
        if(index==4444) {

            fulllink = link;
        }

        else
        {
            /*fulllink = Global.URL + link;*/
            fulllink = CommonFunctions.URL + link;

        }

        StringRequest postRequest = new StringRequest(Request.Method.POST, fulllink,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        request=response;
                        try {
                            api.success(response, index);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        api.error(error, index);
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                return params;
            }

            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }
        };


        postRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        CommonFunctions.getInstance().addToRequestQueue(postRequest);


       return request;
    }
}
