package com.UpTopApps.OilResetPro;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.UpTopApps.OilResetPro.helper.DataClass_RP;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class VinliVehiclesDetail extends AppCompatActivity {

    List<CarsData> list = new ArrayList<CarsData>();
    private DataClass_RP dClass = DataClass_RP.sharedRPDataClass();
    private ArrayList<String> model = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vinli_vehicles_detail);

        ArrayList<CarsData> carsDatas = new ArrayList<CarsData>();
        Intent intent = getIntent();
        carsDatas = ( ArrayList<CarsData>)  intent.getSerializableExtra("cars_datas");
       ListView list_vehicles = (ListView) findViewById(R.id.list_vehicle);
        VinliVehiclesAdapters vinliVehiclesAdapters = new VinliVehiclesAdapters(VinliVehiclesDetail.this, carsDatas);
        list_vehicles.setAdapter(vinliVehiclesAdapters);

        for (int i = 0; i < dClass.mainArray.size(); i++) {
            for (int i2 = 0; i2 < carsDatas.size(); i2++){
                if(dClass.mainArray.get(i).getYear().equals(carsDatas.get(i2).getYear()) && dClass.mainArray.get(i).getMake().equals(carsDatas.get(i2).getMake())){
                    list.add(dClass.mainArray.get(i));
                }
            }


        }

        for (int i = 0; i < list.size(); i++) {
            model.add(list.get(i).getModel());
        }

        list_vehicles.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                // TODO Auto-generated method stub
                Intent redirect = new Intent(VinliVehiclesDetail.this, CarDetailsActivity.class);
                redirect.putExtra("data", list.get(position));
                startActivity(redirect);

            }
        });

    }
}
