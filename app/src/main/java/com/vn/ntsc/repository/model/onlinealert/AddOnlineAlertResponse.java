package com.vn.ntsc.repository.model.onlinealert;

import android.os.Parcel;
import android.os.Parcelable;

import com.vn.ntsc.core.model.ServerResponse;

/**
 * Created by nankai on 9/19/2017.
 */

public class AddOnlineAlertResponse extends ServerResponse {

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
    }

    public AddOnlineAlertResponse() {
    }

    protected AddOnlineAlertResponse(Parcel in) {
        super(in);
    }

    public static final Parcelable.Creator<AddOnlineAlertResponse> CREATOR = new Parcelable.Creator<AddOnlineAlertResponse>() {
        @Override
        public AddOnlineAlertResponse createFromParcel(Parcel source) {
            return new AddOnlineAlertResponse(source);
        }

        @Override
        public AddOnlineAlertResponse[] newArray(int size) {
            return new AddOnlineAlertResponse[size];
        }
    };
}
