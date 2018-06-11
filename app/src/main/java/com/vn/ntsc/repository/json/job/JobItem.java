package com.vn.ntsc.repository.json.job;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.vn.ntsc.core.model.BaseBean;

/**
 * Created by ThoNh on 9/21/2017.
 */

public class JobItem extends BaseBean {
    @SerializedName("name")
    public String name;

    @SerializedName("value")
    public String value;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.value);
    }

    public JobItem() {
    }

    protected JobItem(Parcel in) {
        this.name = in.readString();
        this.value = in.readString();
    }

    public static final Parcelable.Creator<JobItem> CREATOR = new Parcelable.Creator<JobItem>() {
        @Override
        public JobItem createFromParcel(Parcel source) {
            return new JobItem(source);
        }

        @Override
        public JobItem[] newArray(int size) {
            return new JobItem[size];
        }
    };

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
