package com.vn.ntsc.repository.model.signup;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.vn.ntsc.core.model.ServerResponse;
import com.vn.ntsc.repository.model.login.AuthenticationBean;

/**
 * Created by dev22 on 8/16/17.
 */
public class SignUpResponse extends ServerResponse {
    @SerializedName("data")
    public AuthenticationBean authenData;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeParcelable(this.authenData, flags);
    }

    public SignUpResponse() {
    }

    protected SignUpResponse(Parcel in) {
        super(in);
        this.authenData = in.readParcelable(AuthenticationBean.class.getClassLoader());
    }

    public static final Parcelable.Creator<SignUpResponse> CREATOR = new Parcelable.Creator<SignUpResponse>() {
        @Override
        public SignUpResponse createFromParcel(Parcel source) {
            return new SignUpResponse(source);
        }

        @Override
        public SignUpResponse[] newArray(int size) {
            return new SignUpResponse[size];
        }
    };
}
