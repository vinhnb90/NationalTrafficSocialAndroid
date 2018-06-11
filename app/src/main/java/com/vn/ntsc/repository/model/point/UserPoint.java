package com.vn.ntsc.repository.model.point;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by dev22 on 11/21/17.
 * {"data":{"point":719986},"code":0}
 */
public class UserPoint implements Parcelable {
    @SerializedName("point")
    public int point;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.point);
    }

    public UserPoint() {
    }

    protected UserPoint(Parcel in) {
        this.point = in.readInt();
    }

    public static final Creator<UserPoint> CREATOR = new Creator<UserPoint>() {
        @Override
        public UserPoint createFromParcel(Parcel source) {
            return new UserPoint(source);
        }

        @Override
        public UserPoint[] newArray(int size) {
            return new UserPoint[size];
        }
    };
}
