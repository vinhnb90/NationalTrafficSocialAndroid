package com.vn.ntsc.repository.model.poststatus;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.vn.ntsc.core.model.ServerResponse;


/**
 * Created by Robert on 2017 Aug 28.
 */

public class PostStatusResponse extends ServerResponse {

    @SerializedName("data")
    public PostStatusBean data;
    public String tempId;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeParcelable(this.data, flags);
        dest.writeString(this.tempId);
    }

    public PostStatusResponse() {
    }

    protected PostStatusResponse(Parcel in) {
        super(in);
        this.data = in.readParcelable(PostStatusBean.class.getClassLoader());
        this.tempId = in.readString();
    }

    public static final Parcelable.Creator<PostStatusResponse> CREATOR = new Parcelable.Creator<PostStatusResponse>() {
        @Override
        public PostStatusResponse createFromParcel(Parcel source) {
            return new PostStatusResponse(source);
        }

        @Override
        public PostStatusResponse[] newArray(int size) {
            return new PostStatusResponse[size];
        }
    };
}