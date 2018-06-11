package com.vn.ntsc.repository.model.point;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.vn.ntsc.core.model.ServerResponse;

/**
 * Created by dev22 on 11/21/17.
 * response get point
 */
public class GetPointResponse extends ServerResponse {
    @SerializedName("data")
    public UserPoint mUserPoint;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeParcelable(this.mUserPoint, flags);
    }

    public GetPointResponse() {
    }

    protected GetPointResponse(Parcel in) {
        super(in);
        this.mUserPoint = in.readParcelable(UserPoint.class.getClassLoader());
    }

    public static final Parcelable.Creator<GetPointResponse> CREATOR = new Parcelable.Creator<GetPointResponse>() {
        @Override
        public GetPointResponse createFromParcel(Parcel source) {
            return new GetPointResponse(source);
        }

        @Override
        public GetPointResponse[] newArray(int size) {
            return new GetPointResponse[size];
        }
    };
}
