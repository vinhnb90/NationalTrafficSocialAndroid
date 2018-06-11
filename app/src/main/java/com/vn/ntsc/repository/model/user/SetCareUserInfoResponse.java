package com.vn.ntsc.repository.model.user;

import android.os.Parcel;
import android.os.Parcelable;

import com.vn.ntsc.core.model.ServerResponse;

/**
 * Created by nankai on 8/3/2017.
 */

public class SetCareUserInfoResponse extends ServerResponse {

    public static final Parcelable.Creator<SetCareUserInfoResponse> CREATOR = new Parcelable.Creator<SetCareUserInfoResponse>() {
        @Override
        public SetCareUserInfoResponse createFromParcel(Parcel source) {
            return new SetCareUserInfoResponse(source);
        }

        @Override
        public SetCareUserInfoResponse[] newArray(int size) {
            return new SetCareUserInfoResponse[size];
        }
    };

    public SetCareUserInfoResponse() {
    }

    protected SetCareUserInfoResponse(Parcel in) {
        super(in);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
    }
}