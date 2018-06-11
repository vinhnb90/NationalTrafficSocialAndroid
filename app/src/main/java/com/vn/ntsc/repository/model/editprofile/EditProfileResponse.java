package com.vn.ntsc.repository.model.editprofile;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.vn.ntsc.core.model.ServerResponse;
import com.vn.ntsc.repository.model.login.AuthenticationBean;

/**
 * Created by dev22 on 8/24/17.
 */
public class EditProfileResponse extends ServerResponse {
    @SerializedName("data")
    public AuthenticationBean data;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeParcelable(this.data, flags);
    }

    public EditProfileResponse() {
    }

    protected EditProfileResponse(Parcel in) {
        super(in);
        this.data = in.readParcelable(AuthenticationBean.class.getClassLoader());
    }

    public static final Parcelable.Creator<EditProfileResponse> CREATOR = new Parcelable.Creator<EditProfileResponse>() {
        @Override
        public EditProfileResponse createFromParcel(Parcel source) {
            return new EditProfileResponse(source);
        }

        @Override
        public EditProfileResponse[] newArray(int size) {
            return new EditProfileResponse[size];
        }
    };
}
