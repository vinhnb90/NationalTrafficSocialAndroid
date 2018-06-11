package com.vn.ntsc.repository.publicfile;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.vn.ntsc.core.model.ServerResponse;
import com.vn.ntsc.repository.model.timeline.datas.sub.ListBuzzChild;

import java.util.List;

/**
 * Created by ThoNh on 11/23/2017.
 */

public class PublicFileResponse extends ServerResponse {
    @SerializedName("data")
    public List<ListBuzzChild> mData;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeTypedList(this.mData);
    }

    public PublicFileResponse() {
    }

    protected PublicFileResponse(Parcel in) {
        super(in);
        this.mData = in.createTypedArrayList(ListBuzzChild.CREATOR);
    }

    public static final Parcelable.Creator<PublicFileResponse> CREATOR = new Parcelable.Creator<PublicFileResponse>() {
        @Override
        public PublicFileResponse createFromParcel(Parcel source) {
            return new PublicFileResponse(source);
        }

        @Override
        public PublicFileResponse[] newArray(int size) {
            return new PublicFileResponse[size];
        }
    };
}
