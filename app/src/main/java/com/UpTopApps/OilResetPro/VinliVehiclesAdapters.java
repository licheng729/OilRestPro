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
 * Created by Joel on 3/16/2017.
 */

public class VinliVehiclesAdapters extends BaseAdapter {

    //	private Activity context;
    private ArrayList<CarsData> data; // Original Values
    private LayoutInflater inflater;

    public VinliVehiclesAdapters(Activity context, ArrayList<CarsData> data) {
        super();
//		this.context = context;
        this.data = data;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v;

        if(convertView == null){
            v = inflater.inflate(R.layout.vinli_vehicles_adapter, null);
        }else {
            v = convertView;
        }
        TextView tv = (TextView) v.findViewById(R.id.txt_view);
        TextView tv2 = (TextView) v.findViewById(R.id.txt_view2);
        TextView tv3 = (TextView) v.findViewById(R.id.txt_view3);
        tv.setText(data.get(position).getModel());
        tv2.setText(data.get(position).getMake());
        tv3.setText(data.get(position).getYear());

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
