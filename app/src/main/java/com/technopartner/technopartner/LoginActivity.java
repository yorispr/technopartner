package com.technopartner.technopartner;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private Button btnLogin;
    private EditText email,password;
    SharedPreferences pref;
    RequestToken req;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        email = (EditText)findViewById(R.id.edtEmail);
        password = (EditText)findViewById(R.id.edtPassword);

        btnLogin = (Button)findViewById(R.id.btnSignup);
        pref = getApplicationContext().getSharedPreferences("access_pref", 0); // 0 - for private mode
        pDialog = new ProgressDialog(this);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pDialog.setMessage("Harap tunggu..");
                pDialog.show();
                token();
            }
        });
    }

    public void token(){
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest jsonObjReq = new StringRequest(Request.Method.POST,
                "http://maxxapi.technopartner.rocks/oauth/access_token",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            Log.d("Login", response.toString());
                            JSONObject json = new JSONObject(response);
                            SharedPreferences pref = getApplicationContext().getSharedPreferences("access_pref", 0); // 0 - for private mode
                            SharedPreferences.Editor editor = pref.edit();
                            editor.putString("access_token",json.getString("access_token"));
                            editor.putString("token_type",json.getString("token_type"));
                            editor.putString("expires_in",json.get("expires_in").toString());
                            editor.apply();
                            login("http://maxxapi.technopartner.rocks/api/login",email.getText().toString().trim(),password.getText().toString().trim());
                        }catch (JSONException je){
                            je.printStackTrace();
                        }
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
                params.put("username", email.getText().toString().trim());
                params.put("password", password.getText().toString().trim());

                //Log.d("param",params.toString());
                return params;
            }


        };
        queue.add(jsonObjReq);
    }

    private void login(String url, final String username, final String password){
        RequestQueue queue = Volley.newRequestQueue(this);

        HashMap<String, String> params = new HashMap<String, String>();
       // params.put("email", "support@technopartner.id");
       // params.put("password", "1234567");
        params.put("email",username);
        params.put("password",password);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, new JSONObject(params),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        String status = "";
                        try{
                                status = response.getString("status");
                                SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
                                SharedPreferences.Editor editor = pref.edit();
                                editor.putString("token",response.getString("token"));
                                editor.putString("username",response.getString("username"));
                                editor.putString("mobile_phone_user",response.getString("mobile_phone_user"));
                                editor.putString("virtual_card",response.getString("virtual_card"));
                                editor.putString("level_akses",response.getString("level_akses"));
                                editor.apply();

                        }catch (JSONException je){je.printStackTrace();}

                        if(status.equals("success")){
                            pDialog.hide();
                            startActivity(new Intent(LoginActivity.this,ProfileActivity.class));
                            finish();
                        }

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
