package com.vn.ntsc.repository.model.timeline;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.vn.ntsc.core.model.ServerResponse;
import com.vn.ntsc.repository.model.timeline.datas.BuzzBean;

/**
 * Created by nankai on 8/10/2017.
 */

public class BuzzDetailResponse extends ServerResponse {

    @SerializedName("data")
    public BuzzBean data;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeParcelable(this.data, flags);
    }

    public BuzzDetailResponse() {
    }

    protected BuzzDetailResponse(Parcel in) {
        super(in);
        this.data = in.readParcelable(BuzzBean.class.getClassLoader());
    }

    public static final Parcelable.Creator<BuzzDetailResponse> CREATOR = new Parcelable.Creator<BuzzDetailResponse>() {
        @Override
        public BuzzDetailResponse createFromParcel(Parcel source) {
            return new BuzzDetailResponse(source);
        }

        @Override
        public BuzzDetailResponse[] newArray(int size) {
            return new BuzzDetailResponse[size];
        }
    };
}
