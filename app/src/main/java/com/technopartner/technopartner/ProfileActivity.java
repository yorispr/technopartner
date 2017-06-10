package com.technopartner.technopartner;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.technopartner.technopartner.R.id.imageView;


public class ProfileActivity extends AppCompatActivity {

    private TextView topgreet,topname,topbalance,topbeans,kartu,sheetbalance,sheetbeans;
    private ImageView qrcode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        topgreet = (TextView)findViewById(R.id.txttopgreet);
        topname = (TextView)findViewById(R.id.txttopnama);
        topbalance = (TextView)findViewById(R.id.txttopbalance);
        topbeans = (TextView)findViewById(R.id.txttopbeans);

        kartu = (TextView)findViewById(R.id.sheetkartu);
        sheetbalance = (TextView)findViewById(R.id.txtbalance);
        sheetbeans = (TextView)findViewById(R.id.txtbeans);

        qrcode = (ImageView)findViewById(R.id.imageView4);

        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 1); // 0 - for private mode
//        Log.d("token",pref.getString("token",null));

        getprofile("http://maxxapi.technopartner.rocks/api/v2/home",pref.getString("token",null));
    }

    private void getprofile(String url, String token){
        RequestQueue queue = Volley.newRequestQueue(this);

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("token", token);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, new JSONObject(params),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            topgreet.setText(response.getString("salam"));
                            topbalance.setText("IDR "+response.getString("balance"));
                            topname.setText(response.getString("username"));
                            topbeans.setText(response.getString("beans"));

                            kartu.setText(response.getJSONObject("primaryCard").getString("card_name"));
                            sheetbalance.setText("IDR "+response.getJSONObject("primaryCard").getString("balance"));
                            sheetbeans.setText(response.getJSONObject("primaryCard").getString("beans"));

                            Glide.with(getApplicationContext()).load(response.getJSONObject("primaryCard").getString("barcode")).into(qrcode);


                        }catch (JSONException je){}

                        Log.d("Login", response.toString());
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("Login", "Error: " + error.getMessage());
            }
        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                SharedPreferences pref = getApplicationContext().getSharedPreferences("access_pref", 0); // 0 - for private mode

                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                String auth = pref.getString("token_type",null)+" "+pref.getString("access_token",null);
                headers.put("Authorization", auth);
                return headers;
            }

        };
        queue.add(jsonObjReq);

    }


}
