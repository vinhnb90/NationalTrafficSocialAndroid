package com.vn.ntsc.repository.model.online.notification;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.vn.ntsc.core.model.ServerResponse;

/**
 * Created by ThoNh on 3/29/2018.
 */

public class OnlineNotificationNumberResponse extends ServerResponse {
    @SerializedName("count")
    public int mNumberNotification;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(this.mNumberNotification);
    }

    public OnlineNotificationNumberResponse() {
    }

    protected OnlineNotificationNumberResponse(Parcel in) {
        super(in);
        this.mNumberNotification = in.readInt();
    }

    public static final Parcelable.Creator<OnlineNotificationNumberResponse> CREATOR = new Parcelable.Creator<OnlineNotificationNumberResponse>() {
        @Override
        public OnlineNotificationNumberResponse createFromParcel(Parcel source) {
            return new OnlineNotificationNumberResponse(source);
        }

        @Override
        public OnlineNotificationNumberResponse[] newArray(int size) {
            return new OnlineNotificationNumberResponse[size];
        }
    };
}
