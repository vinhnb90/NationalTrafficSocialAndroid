package com.vn.ntsc.repository.model.notification;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.vn.ntsc.core.model.ServerResponse;

import java.util.List;

/**
 * Created by ThoNh on 9/1/2017.
 */

public class OnlineNotificationResponse extends ServerResponse {

    @SerializedName("data")
    public List<OnlineNotificationItem> mData;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeTypedList(this.mData);
    }

    public OnlineNotificationResponse() {
    }

    protected OnlineNotificationResponse(Parcel in) {
        super(in);
        this.mData = in.createTypedArrayList(OnlineNotificationItem.CREATOR);
    }

    public static final Parcelable.Creator<OnlineNotificationResponse> CREATOR = new Parcelable.Creator<OnlineNotificationResponse>() {
        @Override
        public OnlineNotificationResponse createFromParcel(Parcel source) {
            return new OnlineNotificationResponse(source);
        }

        @Override
        public OnlineNotificationResponse[] newArray(int size) {
            return new OnlineNotificationResponse[size];
        }
    };
}
