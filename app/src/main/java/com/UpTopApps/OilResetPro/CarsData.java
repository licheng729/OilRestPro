package com.UpTopApps.OilResetPro;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by Joel on 3/6/2017.
 */

public class CarsData implements Serializable {

    public CarsData() {
        // TODO Auto-generated constructor stub
    }


    private String video_url;
    private String year;
    private String make;
    private String model;
    private String desc;
    private String search;
    private String search2;

    public CarsData(JSONObject obj) throws JSONException {
        video_url=obj.getString("video");

        year = obj.get("year").toString().replace("\ufeff", "");


        make=obj.getString("make");
        model=obj.getString("model");
        desc=obj.getString("reset");
        search = year+" "+model+" "+make;
        search2 = year+" "+make+" "+model;
        search = search.toLowerCase();
        search2 = search2.toLowerCase();
    }

    public String getVideo_url() {
        return video_url;
    }

    public void setVideo_url(String video_url) {
        this.video_url = video_url;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getSearch2() {
        return search2;
    }

    public void setSearch2(String search2) {
        this.search2 = search2;
    }
}
