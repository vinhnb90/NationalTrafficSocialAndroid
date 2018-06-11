package com.vn.ntsc.repository.model.timeline;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.vn.ntsc.core.model.ServerResponse;
import com.vn.ntsc.repository.model.timeline.datas.BuzzBean;

import java.util.List;

/**
 * Created by nankai on 8/8/2017.
 */

public class BuzzListResponse extends ServerResponse {

    @SerializedName("data")
    public List<BuzzBean> data;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeTypedList(this.data);
    }

    public BuzzListResponse() {
    }

    protected BuzzListResponse(Parcel in) {
        super(in);
        this.data = in.createTypedArrayList(BuzzBean.CREATOR);
    }

    public static final Parcelable.Creator<BuzzListResponse> CREATOR = new Parcelable.Creator<BuzzListResponse>() {
        @Override
        public BuzzListResponse createFromParcel(Parcel source) {
            return new BuzzListResponse(source);
        }

        @Override
        public BuzzListResponse[] newArray(int size) {
            return new BuzzListResponse[size];
        }
    };
}
