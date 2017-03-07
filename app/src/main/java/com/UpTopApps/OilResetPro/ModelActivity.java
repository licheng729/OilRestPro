package com.UpTopApps.OilResetPro;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.UpTopApps.OilResetPro.helper.DataClass_RP;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

public class ModelActivity extends AppCompatActivity {

    private ModelActivity context = ModelActivity.this;
    private DataClass_RP dClass = DataClass_RP.sharedRPDataClass();
    private ArrayList<String> model = new ArrayList<String>();
    private ArrayList<CarsData> search = new ArrayList<CarsData>();
    private LinearLayout lay_adds;
    private ListView list_model;
    private Adapter adp;
    private AdapterSearch adpSearch;
    private boolean isSearching;
    List<CarsData> list = new ArrayList<CarsData>();
    private AdView mAdView;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    boolean has_paid;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_model);

        sharedPref = context.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        editor = sharedPref.edit();

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

        String year = getIntent().getStringExtra("year");
        String make = getIntent().getStringExtra("make");

//		list = new RushSearch().whereEqual("year", year).and().whereEqual("make", make).find(CarsData.class);
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


        for (int i = 0; i < dClass.mainArray.size(); i++) {
            if(dClass.mainArray.get(i).getYear().equals(year) && dClass.mainArray.get(i).getMake().equals(make)){
                list.add(dClass.mainArray.get(i));
            }

        }

        for (int i = 0; i < list.size(); i++) {
            model.add(list.get(i).getModel());
        }

        TextView tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText(year+"      -      "+make);


//		removeDuplicates(model);

        Collections.sort(model, new Comparator<String>() {
            public int compare(String a, String b) {
                // todo: handle null
                return a.compareTo(b);
            }
        });

        Collections.sort(list, new Comparator<CarsData>() {
            public int compare(CarsData a, CarsData b) {
                // todo: handle null
                return a.getModel().compareTo(b.getModel());
            }
        });

//		Log.e("SIZES:", "Listmain: "+list.size()+"   model: "+model.size());

        ImageView btn_gotop = (ImageView) findViewById(R.id.btn_gotop);
        ImageView btn_setting = (ImageView) findViewById(R.id.btn_setting);
        final EditText edit_search = (EditText) findViewById(R.id.edit_search);
        final Button btn_cancel = (Button) findViewById(R.id.btn_cancel);
        list_model = (ListView) findViewById(R.id.list_model);

        btn_gotop.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                list_model.smoothScrollToPosition(0);
            }
        });

        btn_setting.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                startActivity(new Intent(context,SettingsActivity.class));
            }
        });

        btn_cancel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(edit_search.getWindowToken(), 0);
                btn_cancel.setVisibility(View.INVISIBLE);
                edit_search.setText("");
                defaultApply();
            }
        });


        edit_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId,KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(edit_search.getWindowToken(), 0);
                    Search(v.getText().toString().toLowerCase());
                    return true;
                }
                return false;
            }
        });

        edit_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                // adapter.getFilter().filter(s.toString());

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() > 0) {
                    btn_cancel.setVisibility(View.VISIBLE);
                    Search(s.toString().toLowerCase());
                } else {
                    btn_cancel.setVisibility(View.INVISIBLE);
                    defaultApply();
                }
            }
        });

        defaultApply();

        list_model.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position,long arg3) {
                // TODO Auto-generated method stub
                if(isSearching){
                    Intent redirect = new Intent(context, CarDetailsActivity.class);
                    redirect.putExtra("data", search.get(position));
                    startActivity(redirect);
                }else{
                    Intent redirect = new Intent(context, CarDetailsActivity.class);
                    redirect.putExtra("data", list.get(position));
                    startActivity(redirect);
//					Log.e("Clicked:", "Clieck: "+model.get(position)+"  "+list.get(position).getModel());

                }
            }
        });

    }

    private void defaultApply(){
        adp = new Adapter(context, model);
        list_model.setAdapter(adp);
        isSearching = false;
    }

    private void removeDuplicates(ArrayList list) {
        HashSet set = new HashSet(list);
        list.clear();
        list.addAll(set);
    }

    private void Search(String s) {
        search.clear();
        for (int i = 0; i < dClass.mainArray.size(); i++) {
            if (dClass.mainArray.get(i).getSearch().contains(s) || dClass.mainArray.get(i).getSearch2().contains(s)) {
                search.add(dClass.mainArray.get(i));
            }
        }
        adpSearch = new AdapterSearch(context, search);
        list_model.setAdapter(adpSearch);
        isSearching = true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        dClass = DataClass_RP.sharedRPDataClass();

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

        if(dClass.back){
            finish();
        }

    }
}
