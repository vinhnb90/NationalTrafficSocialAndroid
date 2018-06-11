package com.vn.ntsc.repository.model.user;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.vn.ntsc.core.model.ServerResponse;

/**
 * Created by nankai on 8/3/2017.
 */

public class GetUserStatusResponse extends ServerResponse {

    public static final int ACTIVE = 1;
    public static final int DEACTIVE = 0;
    public static final int INACTIVE = -1;
    public static final Parcelable.Creator<GetUserStatusResponse> CREATOR = new Parcelable.Creator<GetUserStatusResponse>() {
        @Override
        public GetUserStatusResponse createFromParcel(Parcel source) {
            return new GetUserStatusResponse(source);
        }

        @Override
        public GetUserStatusResponse[] newArray(int size) {
            return new GetUserStatusResponse[size];
        }
    };
    @SerializedName("user_status")
    public int userStatus;

    public GetUserStatusResponse() {
    }

    protected GetUserStatusResponse(Parcel in) {
        super(in);
        this.userStatus = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(this.userStatus);
    }
}
