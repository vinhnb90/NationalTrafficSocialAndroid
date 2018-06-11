package com.vn.ntsc.repository.model.logout;

import android.os.Parcel;
import android.os.Parcelable;

import com.vn.ntsc.core.model.ServerResponse;

/**
 * Created by nankai on 9/1/2017.
 */

public class LogoutResponse extends ServerResponse{

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
    }

    public LogoutResponse() {
    }

    protected LogoutResponse(Parcel in) {
        super(in);
    }

    public static final Parcelable.Creator<LogoutResponse> CREATOR = new Parcelable.Creator<LogoutResponse>() {
        @Override
        public LogoutResponse createFromParcel(Parcel source) {
            return new LogoutResponse(source);
        }

        @Override
        public LogoutResponse[] newArray(int size) {
            return new LogoutResponse[size];
        }
    };
}
