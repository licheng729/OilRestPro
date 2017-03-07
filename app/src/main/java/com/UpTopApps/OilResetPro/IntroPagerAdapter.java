package com.UpTopApps.OilResetPro;


import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.Fragment;

import java.util.ArrayList;

/**
 * Created by Joel on 3/5/2017.
 */

public class IntroPagerAdapter extends FragmentPagerAdapter {

    public ArrayList<IntroPage> pages;
    public IntroPagerAdapter(FragmentManager fm, ArrayList<IntroPage> pages){
        super(fm);
        this.pages = pages;
    }



    @Override
    public Fragment getItem(int i) {
        // TODO Auto-generated method stub
        IntroFragment fragment = new IntroFragment();
        Bundle arguments = new Bundle();
        arguments.putParcelable(fragment.TAG_ITEM, pages.get(i));
        fragment.setArguments(arguments);
        return fragment;
    }


    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return pages.size();
    }
}
