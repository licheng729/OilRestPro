package com.UpTopApps.OilResetPro;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


public class IntroFragment extends Fragment {

    public static final String TAG_ITEM = "TAGITEM";

    public IntroFragment() {
        // Required empty public constructor
    }

    TextView mIntroTextView;
    ImageView mImageView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_intro, container, false);
        mImageView = (ImageView) rootView.findViewById(R.id.introImageView);
        IntroPage introView = (IntroPage) getArguments().get(TAG_ITEM);
        mImageView.setImageResource(introView.imgResource);
        return rootView;
    }

}
