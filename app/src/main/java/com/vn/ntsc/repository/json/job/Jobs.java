package com.vn.ntsc.repository.json.job;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.vn.ntsc.core.model.BaseBean;

import java.util.List;

/**
 * Created by ThoNh on 9/21/2017.
 */

public class Jobs extends BaseBean{

    @SerializedName("male")
    public List<JobItem> jobsMale;

    @SerializedName("female")
    public List<JobItem> jobsFemale;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.jobsMale);
        dest.writeTypedList(this.jobsFemale);
    }

    public Jobs() {
    }

    protected Jobs(Parcel in) {
        this.jobsMale = in.createTypedArrayList(JobItem.CREATOR);
        this.jobsFemale = in.createTypedArrayList(JobItem.CREATOR);
    }

    public static final Parcelable.Creator<Jobs> CREATOR = new Parcelable.Creator<Jobs>() {
        @Override
        public Jobs createFromParcel(Parcel source) {
            return new Jobs(source);
        }

        @Override
        public Jobs[] newArray(int size) {
            return new Jobs[size];
        }
    };

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
