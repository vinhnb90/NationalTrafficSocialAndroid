package com.vn.ntsc.repository.model.notification.push;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by nankai on 3/28/2018.
 */

public class NotificationMessage implements Parcelable {

    @SerializedName("msg_type")
    public String msgType;
    @SerializedName("value")
    public NotificationValue value;

    protected NotificationMessage(Parcel in) {
        msgType = in.readString();
        value = in.readParcelable(NotificationValue.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(msgType);
        dest.writeParcelable(value, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<NotificationMessage> CREATOR = new Creator<NotificationMessage>() {
        @Override
        public NotificationMessage createFromParcel(Parcel in) {
            return new NotificationMessage(in);
        }

        @Override
        public NotificationMessage[] newArray(int size) {
            return new NotificationMessage[size];
        }
    };
}
