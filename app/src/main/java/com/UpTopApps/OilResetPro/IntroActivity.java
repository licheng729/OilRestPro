package com.UpTopApps.OilResetPro;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;

public class IntroActivity extends FragmentActivity implements View.OnClickListener {

    IntroPagerAdapter mAdapter;

    ViewPager mViewPager;

    ImageView btn_skip, btn_next, img_1, img_2, img_3, img_4;
    int size;
    private ArrayList<IntroPage> introPages;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    private IntroActivity context = IntroActivity.this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        sharedPref = context.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
         editor = sharedPref.edit();



        mViewPager = (ViewPager) findViewById(R.id.intro_container);
        btn_skip = (ImageView) findViewById(R.id.btn_skip);
        btn_next = (ImageView) findViewById(R.id.btn_next);
        img_1 = (ImageView) findViewById(R.id.img_1);
        img_2 = (ImageView) findViewById(R.id.img_2);
        img_3 = (ImageView) findViewById(R.id.img_3);
        img_4 = (ImageView) findViewById(R.id.img_4);

        btn_skip.setOnClickListener(this);
        btn_next.setOnClickListener(this);

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {

            }

            @Override
            public void onPageSelected(int i) {
                setcurrentImage();
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        introPages = new ArrayList<IntroPage>();
        introPages.add(new IntroPage(R.mipmap.intro_4));
        introPages.add(new IntroPage(R.mipmap.intro_1));
        introPages.add(new IntroPage(R.mipmap.intro_2));
        introPages.add(new IntroPage(R.mipmap.intro_3));

        setData();

    }

    private void setcurrentImage(){

        int pos = mViewPager.getCurrentItem();

        if (pos == 0) {
            img_1.setImageResource(R.mipmap.pagination_selected);
            img_2.setImageResource(R.mipmap.pagination_unselected);
            img_3.setImageResource(R.mipmap.pagination_unselected);
            img_4.setImageResource(R.mipmap.pagination_unselected);
            btn_skip.setVisibility(View.VISIBLE);
            btn_next.setImageResource(R.mipmap.btn_next);
        }
        else if (pos == 1) {

            img_1.setImageResource(R.mipmap.pagination_unselected);
            img_2.setImageResource(R.mipmap.pagination_selected);
            img_3.setImageResource(R.mipmap.pagination_unselected);
            img_4.setImageResource(R.mipmap.pagination_unselected);
            btn_skip.setVisibility(View.VISIBLE);
            btn_next.setImageResource(R.mipmap.btn_next);
        }
        else if (pos == 2) {

            img_1.setImageResource(R.mipmap.pagination_unselected);
            img_2.setImageResource(R.mipmap.pagination_unselected);
            img_3.setImageResource(R.mipmap.pagination_selected);
            img_4.setImageResource(R.mipmap.pagination_unselected);
            btn_skip.setVisibility(View.VISIBLE);
            btn_next.setImageResource(R.mipmap.btn_next);
        }
        else if (pos == 3) {

            img_1.setImageResource(R.mipmap.pagination_unselected);
            img_2.setImageResource(R.mipmap.pagination_unselected);
            img_3.setImageResource(R.mipmap.pagination_unselected);
            img_4.setImageResource(R.mipmap.pagination_selected);
            btn_skip.setVisibility(View.INVISIBLE);
            btn_next.setImageResource(R.mipmap.btn_done);
        }

    }

    public void setData() {
        size = introPages.size();
        mAdapter = new IntroPagerAdapter(getSupportFragmentManager(), introPages);
        mViewPager.setAdapter(mAdapter);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        final boolean is_login = sharedPref.getBoolean(Constants.IS_LOGGED_IN, false);
        if (v == btn_skip) {
            if(!is_login){
                editor.putBoolean(Constants.IS_FIRST_TIME, false);
                editor.commit();
              startActivity(new Intent(IntroActivity.this, LoginButtonsActivity.class));
            }
            finish();
        }
        if (v == btn_next) {

            int current = mViewPager.getCurrentItem();

            if(current != introPages.size()-1){
                mViewPager.setCurrentItem(current+1);
            }else{
                if(!is_login){
                    editor.putBoolean(Constants.IS_FIRST_TIME, false);
                    editor.commit();
                    startActivity(new Intent(IntroActivity.this, LoginButtonsActivity.class));
                }
                finish();
            }
            setcurrentImage();
        }

    }
}
