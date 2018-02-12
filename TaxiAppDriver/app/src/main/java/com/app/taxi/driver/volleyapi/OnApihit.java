package com.app.taxi.driver.volleyapi;

import com.android.volley.VolleyError;

import org.json.JSONException;

/**
 * Created by Tejinder on 7/22/2016.
 */
public interface OnApihit {
   void success(String Response, int index) throws JSONException;
    void error(VolleyError error, int index);
}
