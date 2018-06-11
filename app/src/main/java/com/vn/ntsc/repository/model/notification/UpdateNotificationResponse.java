package com.vn.ntsc.repository.model.notification;

import android.os.Parcel;
import android.os.Parcelable;

import com.vn.ntsc.core.model.ServerResponse;

/**
 * Created by nankai on 8/22/2017.
 */

public class UpdateNotificationResponse extends ServerResponse {

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
    }

    public UpdateNotificationResponse() {
    }

    protected UpdateNotificationResponse(Parcel in) {
        super(in);
    }

    public static final Parcelable.Creator<UpdateNotificationResponse> CREATOR = new Parcelable.Creator<UpdateNotificationResponse>() {
        @Override
        public UpdateNotificationResponse createFromParcel(Parcel source) {
            return new UpdateNotificationResponse(source);
        }

        @Override
        public UpdateNotificationResponse[] newArray(int size) {
            return new UpdateNotificationResponse[size];
        }
    };
}
