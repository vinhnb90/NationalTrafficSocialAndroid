package com.vn.ntsc.repository.model.notification.push;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by nankai on 3/28/2018.
 */

public class NotificationAlert implements Parcelable {

    public final static String TAG_LOC_KEY = "loc-key";
    public final static String TAG_LOC_ARGS = "loc-args";

    @SerializedName(TAG_LOC_KEY)
    public String lockey;
    @SerializedName(TAG_LOC_ARGS)
    public String[] logArgs;

    public NotificationAlert() {
    }

    protected NotificationAlert(Parcel in) {
        lockey = in.readString();
        logArgs = in.createStringArray();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(lockey);
        dest.writeStringArray(logArgs);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<NotificationAlert> CREATOR = new Creator<NotificationAlert>() {
        @Override
        public NotificationAlert createFromParcel(Parcel in) {
            return new NotificationAlert(in);
        }

        @Override
        public NotificationAlert[] newArray(int size) {
            return new NotificationAlert[size];
        }
    };
}
