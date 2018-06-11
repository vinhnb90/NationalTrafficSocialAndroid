package com.vn.ntsc.repository.model.onlinealert;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.vn.ntsc.core.model.ServerResponse;

/**
 * Created by nankai on 9/19/2017.
 */

public class GetOnlineAlertResponse extends ServerResponse{

    @SerializedName("data")
    public GetOnlineAlertBean data;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeParcelable(this.data, flags);
    }

    public GetOnlineAlertResponse() {
    }

    protected GetOnlineAlertResponse(Parcel in) {
        super(in);
        this.data = in.readParcelable(GetOnlineAlertBean.class.getClassLoader());
    }

    public static final Parcelable.Creator<GetOnlineAlertResponse> CREATOR = new Parcelable.Creator<GetOnlineAlertResponse>() {
        @Override
        public GetOnlineAlertResponse createFromParcel(Parcel source) {
            return new GetOnlineAlertResponse(source);
        }

        @Override
        public GetOnlineAlertResponse[] newArray(int size) {
            return new GetOnlineAlertResponse[size];
        }
    };
}
