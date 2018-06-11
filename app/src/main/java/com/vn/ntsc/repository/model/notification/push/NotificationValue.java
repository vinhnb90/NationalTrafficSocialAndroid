package com.vn.ntsc.repository.model.notification.push;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by nankai on 4/2/2018.
 */

public class NotificationValue implements Parcelable {

    @SerializedName("aps")
    public NotificationAps aps;
    @SerializedName("badge")
    public int badge;

    protected NotificationValue(Parcel in) {
        aps = in.readParcelable(NotificationAps.class.getClassLoader());
        badge = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(aps, flags);
        dest.writeInt(badge);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<NotificationValue> CREATOR = new Creator<NotificationValue>() {
        @Override
        public NotificationValue createFromParcel(Parcel in) {
            return new NotificationValue(in);
        }

        @Override
        public NotificationValue[] newArray(int size) {
            return new NotificationValue[size];
        }
    };
}
