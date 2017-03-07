package com.UpTopApps.OilResetPro;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import com.UpTopApps.OilResetPro.helper.DataClass_RP;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        DataClass_RP dClass = DataClass_RP.sharedRPDataClass();

        dClass = dClass.LoadCurUser(this);
        // Get token


        Context context = SplashActivity.this;
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

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                    editor.putString(Constants.DEVICE_TOKEN, token);
                if(is_first_time){
                    startActivity(introActivityIntent);
                }else {
                    if(!is_login){
                        startActivity(loginActivityIntent);
                    }else {
                        startActivity(homeActivityIntent);
                    }
                }

                finish();
            }
        }, 2000);
    }

}
