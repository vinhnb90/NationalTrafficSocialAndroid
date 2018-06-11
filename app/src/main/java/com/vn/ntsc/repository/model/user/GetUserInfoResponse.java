package com.vn.ntsc.repository.model.user;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.vn.ntsc.core.model.ServerResponse;

/**
 * Created by ThoNh on 9/5/2017.
 */

public class GetUserInfoResponse extends ServerResponse {
    @SerializedName("data")
    public UserInfoResponse data;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeParcelable(this.data, flags);
    }

    public GetUserInfoResponse() {
    }

    protected GetUserInfoResponse(Parcel in) {
        super(in);
        this.data = in.readParcelable(UserInfoResponse.class.getClassLoader());
    }

    public static final Parcelable.Creator<GetUserInfoResponse> CREATOR = new Parcelable.Creator<GetUserInfoResponse>() {
        @Override
        public GetUserInfoResponse createFromParcel(Parcel source) {
            return new GetUserInfoResponse(source);
        }

        @Override
        public GetUserInfoResponse[] newArray(int size) {
            return new GetUserInfoResponse[size];
        }
    };
}
