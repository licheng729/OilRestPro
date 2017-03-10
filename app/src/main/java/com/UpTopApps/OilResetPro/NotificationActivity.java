package com.UpTopApps.OilResetPro;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.UpTopApps.OilResetPro.helper.DataClass_RP;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

public class NotificationActivity extends AppCompatActivity {

    private NotificationActivity context = NotificationActivity.this;
    private AdView mAdView;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    Boolean has_paid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        String txt = getIntent().getStringExtra("body");
        TextView tv = (TextView) findViewById(R.id.not_txt);
        tv.setText(txt);

        sharedPref = context.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        final String auth_token = sharedPref.getString(Constants.AUTH_TOKEN, "");

        has_paid = sharedPref.getBoolean(Constants.HAS_PAID, false);

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
    }

    @Override
    public void onBackPressed() {
        finish();
    }

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

    }
}
