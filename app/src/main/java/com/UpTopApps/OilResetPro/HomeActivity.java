package com.UpTopApps.OilResetPro;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.UpTopApps.OilResetPro.helper.DataClass_RP;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {

    private HomeActivity context = HomeActivity.this;
    private AdView mAdView;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    private DataClass_RP dClass;
    String url = "http://api.devfj.com/get-all-procedures";
    RequestQueue queue;
    private ProgressDialog mProgressDialog;
    boolean has_paid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Button btn_start = (Button) findViewById(R.id.btn_start);
        Button btn_use_vinli = (Button) findViewById(R.id.use_vinli);

        sharedPref = context.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        final String auth_token = sharedPref.getString(Constants.AUTH_TOKEN, "");

        has_paid = sharedPref.getBoolean(Constants.HAS_PAID, false);
        queue = Volley.newRequestQueue(this);

        if(!has_paid){
            // Initialize the Mobile Ads SDK.
            MobileAds.initialize(this, "ca-app-pub-6367600362410330/8364426808");

            // Gets the ad view defined in layout/ad_fragment.xml with ad unit ID set in
            // values/strings.xml.
            mAdView = (AdView) findViewById(R.id.adView);
            mAdView.setVisibility(View.VISIBLE);
            // Create an ad request. Check your logcat output for the hashed device ID to
            // get test ads on a physical device. e.g.
            // "Use AdRequest.Builder.addTestDevice("ABCDEF012345") to get test ads on this device."
            AdRequest adRequest = new AdRequest.Builder()
                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                    .build();

            // Start loading the ad in the background.
            mAdView.loadAd(adRequest);
        }

        btn_start.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (dClass.mainArray.size() > 0) {
                    startActivity(new Intent(context, YearsActivity.class));
                }else {

                    showProgressDialog();
                    JsonObjectRequest jsObjRequest = new JsonObjectRequest
                            (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                                @Override
                                public void onResponse(JSONObject response) {
                                    // mTxtDisplay.setText("Response: " + response.toString());
                                    hideProgressDialog();
                                    String res = null;

                                    try {
                                       // Log.d("Response: ",  response.toString());
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
                                                startActivity(new Intent(context, YearsActivity.class));

                                       // }else {
                                          //  Toast.makeText(EmailLoginActivity.this,res,Toast.LENGTH_LONG).show();
                                       // }
                                    }catch (JSONException ex){
                                        Log.d("Response: ",  ex.getMessage());
                                    }catch (NumberFormatException ex2){
                                        //Toast.makeText(EmailLoginActivity.this,res,Toast.LENGTH_LONG).show();
                                    }

                                }
                            }, new Response.ErrorListener() {

                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    // TODO Auto-generated method stub
                                    hideProgressDialog();

                                }
                            }){
                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            Map<String, String>  params = new HashMap<String, String>();
                            params.put("Authorization", auth_token);

                            return params;
                        }
                    };
                    queue.add(jsObjRequest);
                }
            }
        });

        btn_use_vinli.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                startActivity(new Intent(context, VinliLoginActivity.class));
            }
        });

        app_launched();
    }

    /** Called when leaving the activity */
    @Override
    public void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }

    /** Called when returning to the activity */
    @Override
    public void onResume() {
        super.onResume();

        has_paid = sharedPref.getBoolean(Constants.HAS_PAID, false);

        if(!has_paid){

            // Gets the ad view defined in layout/ad_fragment.xml with ad unit ID set in
            // values/strings.xml.
            mAdView = (AdView) findViewById(R.id.adView);
            mAdView.setVisibility(View.VISIBLE);
            // Create an ad request. Check your logcat output for the hashed device ID to
            // get test ads on a physical device. e.g.
            // "Use AdRequest.Builder.addTestDevice("ABCDEF012345") to get test ads on this device."
            AdRequest adRequest = new AdRequest.Builder()
                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                    .build();

            // Start loading the ad in the background.
            mAdView.loadAd(adRequest);
        }

        dClass = DataClass_RP.sharedRPDataClass();

        if(dClass.dataArray.size()>0){
            dClass.mainArray.clear();
            dClass.mainArray.addAll(dClass.dataArray);
            dClass.dataArray.clear();
            dClass.SaveCurUser(context);
        }

        if(dClass.back){
            dClass.back = false;
        }
    }

    /** Called before the activity is destroyed */
    @Override
    public void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }

    private String APP_PNAME = "com.UpTopApps.OilResetPro";

    private final static int DAYS_UNTIL_PROMPT = 1;
    //  private final static int LAUNCHES_UNTIL_PROMPT = 7;

    public  void app_launched() {
        if (sharedPref.getBoolean("dontshowagain", false)) { return ; }

        // Increment launch counter
        long launch_count = sharedPref.getLong("launch_count", 0) + 1;
        editor.putLong("launch_count", launch_count);

        // Get date of first launch
        Long date_firstLaunch = sharedPref.getLong("date_firstlaunch", 0);
        if (date_firstLaunch == 0) {
            date_firstLaunch = System.currentTimeMillis();
            editor.putLong("date_firstlaunch", date_firstLaunch);
        }

        if (System.currentTimeMillis() >= date_firstLaunch + (DAYS_UNTIL_PROMPT * 24 * 60 * 60 * 1000)) {
            showRateDialog();
        }

        editor.commit();
    }


    public  void showRateDialog() {

        final Dialog dialog1 = new Dialog(context);
        dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog1.setContentView(R.layout.custom_alert_rate);

        TextView btn_rate_now = (TextView) dialog1.findViewById(R.id.btn_rate_now);
        TextView btn_rate_later = (TextView) dialog1.findViewById(R.id.btn_rate_later);
        TextView btn_rate_no = (TextView) dialog1.findViewById(R.id.btn_rate_no);

        btn_rate_now.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + APP_PNAME)));
                dialog1.dismiss();
            }
        });

        btn_rate_later.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dialog1.dismiss();
            }
        });

        btn_rate_no.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (editor != null) {
                    editor.putBoolean("dontshowagain", true);
                    editor.commit();
                }
                dialog1.dismiss();
            }
        });

        dialog1.show();


    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }

}
