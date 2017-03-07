package com.UpTopApps.OilResetPro;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Joel on 3/5/2017.
 */

public class Adapter extends BaseAdapter {

    //	private Activity context;
    private ArrayList<String> data; // Original Values
    private LayoutInflater inflater;

    public Adapter(Activity context, ArrayList<String> data) {
        super();
//		this.context = context;
        this.data = data;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v;

        if(convertView == null){
            v = inflater.inflate(R.layout.adapter, null);
        }else {
            v = convertView;
        }
        TextView tv = (TextView) v.findViewById(R.id.txt_view);
        tv.setText(data.get(position));

        return v;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }
}
