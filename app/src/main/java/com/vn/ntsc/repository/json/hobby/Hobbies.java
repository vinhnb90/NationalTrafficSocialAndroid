package com.vn.ntsc.repository.json.hobby;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.vn.ntsc.core.model.BaseBean;

import java.util.List;

/**
 * Created by ThoNh on 9/21/2017.
 */

public class Hobbies extends BaseBean{

    @SerializedName("hobby")
    public List<HobbyItem> hobbies;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.hobbies);
    }

    public Hobbies() {
    }

    protected Hobbies(Parcel in) {
        this.hobbies = in.createTypedArrayList(HobbyItem.CREATOR);
    }

    public static final Parcelable.Creator<Hobbies> CREATOR = new Parcelable.Creator<Hobbies>() {
        @Override
        public Hobbies createFromParcel(Parcel source) {
            return new Hobbies(source);
        }

        @Override
        public Hobbies[] newArray(int size) {
            return new Hobbies[size];
        }
    };

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
