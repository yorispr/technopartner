package com.technopartner.technopartner;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by YORIS on 6/10/17.
 */

public class RequestToken {
    Context ctx;
    String resp = "";
    String username="";
    String password="";
    public RequestToken(Context ctx, String username, String password){
        this.ctx = ctx;
        this.username = username;
        this.password = password;
    }

    public void token(){
        RequestQueue queue = Volley.newRequestQueue(ctx);
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                "http://maxxapi.technopartner.rocks/oauth/access_token", null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            Log.d("Login", response.toString());
                            SharedPreferences pref = ctx.getSharedPreferences("access_pref", 0); // 0 - for private mode
                            SharedPreferences.Editor editor = pref.edit();
                            editor.putString("access_token",response.getString("access_token"));
                            editor.putString("token_type",response.getString("token_type"));
                            editor.putString("expires_in",response.get("expires_in").toString());
                            editor.apply();
                        }catch (JSONException je){}
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("Login", "Error: " + error.getMessage());
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("client_secret", "0a40f69db4e5fd2f4ac65a090f31b823");
                params.put("client_id", "e78869f77986684a");
                params.put("grant_type", "password");
                params.put("username", username);
                params.put("password", password);

                return params;
            }


        };
        queue.add(jsonObjReq);
    }
}
