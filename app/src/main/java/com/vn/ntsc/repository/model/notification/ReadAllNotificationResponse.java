package com.vn.ntsc.repository.model.notification;

import android.os.Parcel;
import android.os.Parcelable;

import com.vn.ntsc.core.model.ServerResponse;

/**
 * Created by ThoNh on 11/9/2017.
 */

public class ReadAllNotificationResponse extends ServerResponse {

    public ReadAllNotificationResponse(){

    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
    }

    protected ReadAllNotificationResponse(Parcel in) {
        super(in);
    }

    public static final Parcelable.Creator<ReadAllNotificationResponse> CREATOR = new Parcelable.Creator<ReadAllNotificationResponse>() {
        @Override
        public ReadAllNotificationResponse createFromParcel(Parcel source) {
            return new ReadAllNotificationResponse(source);
        }

        @Override
        public ReadAllNotificationResponse[] newArray(int size) {
            return new ReadAllNotificationResponse[size];
        }
    };
}
