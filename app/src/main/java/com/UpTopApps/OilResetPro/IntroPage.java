package com.UpTopApps.OilResetPro;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Joel on 3/5/2017.
 */

public class IntroPage implements Parcelable {

    //    public String title;
//    public String desc;
    public int imgResource;

    public IntroPage(int imgResource) {
        this.imgResource = imgResource;
    }

    public IntroPage(Parcel in){

        this.imgResource = in.readInt();
    }

    public static final Parcelable.Creator<IntroPage> CREATOR
            = new Parcelable.Creator<IntroPage>() {
        public IntroPage createFromParcel(Parcel in) {
            return new IntroPage(in);
        }

        public IntroPage[] newArray(int size) {
            return new IntroPage[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(imgResource);
    }
}
