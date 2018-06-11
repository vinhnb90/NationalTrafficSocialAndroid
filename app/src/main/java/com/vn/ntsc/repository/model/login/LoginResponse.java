package com.vn.ntsc.repository.model.login;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.vn.ntsc.core.model.ServerResponse;


/**
 * Created by nankai on 11/29/2016.
 */

public class LoginResponse extends ServerResponse {

    public static final Parcelable.Creator<LoginResponse> CREATOR = new Parcelable.Creator<LoginResponse>() {
        @Override
        public LoginResponse createFromParcel(Parcel source) {
            return new LoginResponse(source);
        }

        @Override
        public LoginResponse[] newArray(int size) {
            return new LoginResponse[size];
        }
    };
    @SerializedName("data")
    public AuthenticationBean authenData;

    public LoginResponse() {
    }

    protected LoginResponse(Parcel in) {
        super(in);
        this.authenData = in.readParcelable(AuthenticationBean.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeParcelable(this.authenData, flags);
    }
}