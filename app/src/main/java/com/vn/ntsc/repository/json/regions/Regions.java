package com.vn.ntsc.repository.json.regions;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.vn.ntsc.core.model.BaseBean;

import java.util.List;

/**
 * Created by ThoNh on 9/21/2017.
 */

public class Regions extends BaseBean {

    @SerializedName("regions")
    public List<RegionItem> regions;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.regions);
    }

    public Regions() {
    }

    protected Regions(Parcel in) {
        this.regions = in.createTypedArrayList(RegionItem.CREATOR);
    }

    public static final Parcelable.Creator<Regions> CREATOR = new Parcelable.Creator<Regions>() {
        @Override
        public Regions createFromParcel(Parcel source) {
            return new Regions(source);
        }

        @Override
        public Regions[] newArray(int size) {
            return new Regions[size];
        }
    };

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
