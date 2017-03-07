package com.UpTopApps.OilResetPro;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.UpTopApps.OilResetPro.helper.DataClass_RP;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.AdListener;

public class CarDetailsActivity extends AppCompatActivity implements OnClickListener {

    private AdView mAdView;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    boolean has_paid;

    Bundle bundle;
    TextView reset_txt1;
    //	DataClass_RP dataClass;
    // LinearLayout Navi;
    // Button ;
    CarDetailsActivity context = CarDetailsActivity.this;
    private DataClass_RP dClass = DataClass_RP.sharedRPDataClass();
    ImageView btn_setting;
    //	RevMob revmob;
//	private String reset;
    CarsData data;
    ImageView btn_video, btn_share;
    private InterstitialAd mInterstitialAd;
    boolean haveVideo = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_details);

        ImageView btn_home = (ImageView) findViewById(R.id.btn_home);
        btn_home.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
//				startActivity(new Intent(context, ActivityHome.class));
                dClass.back = true;
                finish();
            }
        });

        sharedPref = context.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        editor = sharedPref.edit();

         has_paid = sharedPref.getBoolean(Constants.HAS_PAID, false);

        if(!has_paid){
            // Initialize the Mobile Ads SDK.
            MobileAds.initialize(this, "ca-app-pub-6367600362410330/9841160007");

            mInterstitialAd = new InterstitialAd(context);
            // Defined in res/values/strings.xml
            mInterstitialAd.setAdUnitId("ca-app-pub-6367600362410330/9841160007");

            showInterstitial();

            mInterstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {

                }
            });

         /*   // Gets the ad view defined in layout/ad_fragment.xml with ad unit ID set in
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
            mAdView.loadAd(adRequest);*/
        }




        btn_setting = (ImageView) findViewById(R.id.btn_setting);
        btn_setting.setOnClickListener(this);
        btn_video = (ImageView) findViewById(R.id.btn_video);
        btn_video.setOnClickListener(this);
        btn_share = (ImageView) findViewById(R.id.btn_share);
        btn_share.setOnClickListener(this);
//		Sewew = (LinearLayout) findViewById(R.id.Sewew);
        this.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        reset_txt1 = (TextView) findViewById(R.id.reset_txt1);

        data = (CarsData) getIntent().getSerializableExtra("data");
//		reset = (String) getIntent().getCharSequenceExtra("reset");
        String reset = data.getDesc();
        String[] addrLines = reset.split("\r");
        StringBuffer formatedAddress = new StringBuffer();

        for (String line : addrLines) {
            formatedAddress.append(line.trim() + "\n \n");
        }
        reset_txt1.setText("" + formatedAddress.toString());

        if(data.getVideo_url().equalsIgnoreCase("none")){
            btn_video.setImageResource(R.mipmap.btn_submit_video);
            haveVideo = false;
        }
        else{
            btn_video.setImageResource(R.mipmap.btn_video);
            haveVideo = true;
        }

    }

    @Override
    public void onClick(View v) {

        if (v == btn_setting) {
            Intent inet = new Intent(getApplicationContext(),
                    SettingsActivity.class);
            startActivityForResult(inet, 10);
//			Sewew.setVisibility(View.INVISIBLE);
        }
        else if (v == btn_video) {
            if (haveVideo) {
//				Intent intent = new Intent(Cars_Details_Activity.this,PlayVideo.class);
                String Id = getVideoId(data.getVideo_url());

                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("http://www.youtube.com/watch?v="+Id));
                startActivity(i);
            } else {


                Intent intent = new Intent (Intent.ACTION_VIEW , Uri.parse("mailto:" + "info@oilresetpro.com"));
                intent.putExtra(Intent.EXTRA_SUBJECT, "New video submission");
                intent.putExtra(Intent.EXTRA_TEXT, "Write your message here...");
                startActivity(intent);

            }
        }
        else if (v == btn_share) {

            Intent share = new Intent(android.content.Intent.ACTION_SEND);
            share.setType("text/plain");
            share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

            // Add data to the intent, the receiving app will decide
            // what to do with it.
            share.putExtra(Intent.EXTRA_SUBJECT,"Link to share");
            share.putExtra(Intent.EXTRA_TITLE,getString(R.string.app_name));
//	           share.putExtra(Intent.EXTRA_TEXT, "http://play.google.com/store/apps/details?id=com.UpTopApps.OilResetPro");
            share.putExtra(Intent.EXTRA_TEXT, "http://onelink.to/r4t9jy");
            startActivity(Intent.createChooser(share, "Share link!"));
        }
    }

    private String getVideoId(String urls) {

        String vid = null;

        int vindex = urls.indexOf(".be/");

//		int ampIndex = urls.indexOf("&", vindex);
//
//		if (ampIndex < 0) {

        vid = urls.substring(vindex + 4);

//		} else {
//
//			vid = urls.substring(vindex + 2, ampIndex);
//		}

        return vid;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.cars__details_, menu);
        return true;
    }



    //	@Override
    protected void onResume() {
        super.onResume();

        dClass = DataClass_RP.sharedRPDataClass();

        if(dClass.back){
            finish();
        }
        has_paid = sharedPref.getBoolean(Constants.HAS_PAID, false);
        if(!has_paid){
            showInterstitial();
        }


    }

    private void showInterstitial() {
        // Show the ad if it's ready. Otherwise toast and restart the game.
        if (mInterstitialAd != null && mInterstitialAd.isLoaded()) {
            Log.d("ad", "loading ad");
            mInterstitialAd.show();
        } else {
            loadAd();
        }
    }

    private void loadAd() {
        // Request a new ad if one isn't already loaded, hide the button, and kick off the timer.
        if (!mInterstitialAd.isLoading() && !mInterstitialAd.isLoaded()) {
            AdRequest adRequest = new AdRequest.Builder().build();
            mInterstitialAd.loadAd(adRequest);
        }

    }
}
