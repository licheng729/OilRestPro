package com.UpTopApps.OilResetPro;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import com.UpTopApps.OilResetPro.helper.DataClass_RP;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SplashActivity extends AppCompatActivity {
    String url = "http://api.devfj.com/get-all-procedures";
    RequestQueue queue;
    private DataClass_RP dClass;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        dClass = DataClass_RP.sharedRPDataClass();


        dClass = dClass.LoadCurUser(this);
        // Get token


       final Context context = SplashActivity.this;
        FirebaseApp.initializeApp(context);
        final String token = FirebaseInstanceId.getInstance().getToken();
        SharedPreferences sharedPref = context.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        final  SharedPreferences.Editor  editor = sharedPref.edit();

        final boolean is_first_time = sharedPref.getBoolean(Constants.IS_FIRST_TIME, true);
        final boolean is_login = sharedPref.getBoolean(Constants.IS_LOGGED_IN, false);

        final Intent loginActivityIntent = new Intent(this, LoginButtonsActivity.class);
        final Intent introActivityIntent = new Intent(this, IntroActivity.class);
        final Intent homeActivityIntent = new Intent(this, HomeActivity.class);

        queue = Volley.newRequestQueue(this);

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                    editor.putString(Constants.DEVICE_TOKEN, token);
                if(is_first_time){

                    if (dClass.mainArray.size() > 0) {
                        startActivity(introActivityIntent);
                   }else {
                        showProgressDialog();
                        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                                    @Override
                                    public void onResponse(JSONObject response) {
                                        // mTxtDisplay.setText("Response: " + response.toString());

                                        String res = null;

                                        try {
                                            Log.d("Response: ",  response.toString());
                                            res = response.getString("data");
                                            JSONArray jsonArray = new JSONArray(res);
                                            Log.d("Response: ",  res);
                                            //  if(res.equals("success")){

                                            dClass.dataArray.clear();
                                            for (int i = 0; i < jsonArray.length(); i++) {
                                                CarsData obj = new CarsData(jsonArray.getJSONObject(i));
                                                dClass.dataArray.add(obj);
                                            }

                                            dClass.mainArray.clear();
                                            dClass.mainArray.addAll(dClass.dataArray);
                                            dClass.dataArray.clear();
                                            dClass.SaveCurUser(context);
                                            hideProgressDialog();
                                            startActivity(introActivityIntent);
                                            finish();
                                        }catch (JSONException ex){
                                            Log.d("Response: ",  ex.getMessage());
                                            hideProgressDialog();
                                            startActivity(introActivityIntent);
                                            finish();
                                        }catch (NumberFormatException ex2){
                                            hideProgressDialog();
                                            //Toast.makeText(EmailLoginActivity.this,res,Toast.LENGTH_LONG).show();
                                            startActivity(introActivityIntent);
                                            finish();
                                        }

                                    }
                                }, new Response.ErrorListener() {

                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        // TODO Auto-generated method stub
                                        hideProgressDialog();
                                        startActivity(introActivityIntent);
                                       finish();

                                    }
                                });

                        jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(
                                180000,
                                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                        queue.add(jsObjRequest);
                    }


                }else {
                    if(!is_login){
                        startActivity(loginActivityIntent);
                    }else {
                        startActivity(homeActivityIntent);
                    }

                    finish();
                }


            }
        }, 2000);
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(SplashActivity.this);
            mProgressDialog.setMessage("Loading Procedures");
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setCanceledOnTouchOutside(false);
        }

        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }

}
