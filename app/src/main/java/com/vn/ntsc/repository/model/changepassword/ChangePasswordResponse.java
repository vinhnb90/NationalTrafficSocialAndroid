package com.vn.ntsc.repository.model.changepassword;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.vn.ntsc.core.model.ServerResponse;
import com.vn.ntsc.repository.model.login.AuthenticationBean;

/**
 * Created by dev22 on 8/22/17.
 */
public class ChangePasswordResponse extends ServerResponse {
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

    public ChangePasswordResponse() {
    }

    protected ChangePasswordResponse(Parcel in) {
        super(in);
        this.authenData = in.readParcelable(AuthenticationBean.class.getClassLoader());
    }

    public static final Parcelable.Creator<ChangePasswordResponse> CREATOR = new Parcelable.Creator<ChangePasswordResponse>() {
        @Override
        public ChangePasswordResponse createFromParcel(Parcel source) {
            return new ChangePasswordResponse(source);
        }

        @Override
        public ChangePasswordResponse[] newArray(int size) {
            return new ChangePasswordResponse[size];
        }
    };
}
