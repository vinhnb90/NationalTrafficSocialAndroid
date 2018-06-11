package com.vn.ntsc.repository.model.listpublicimage;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.vn.ntsc.core.model.ServerResponse;
import com.vn.ntsc.repository.model.timeline.datas.sub.ListBuzzChild;

import java.util.List;

/**
 * Created by nankai on 8/29/2017.
 */

public class ListPublicImageResponse extends ServerResponse {

    @SerializedName("data")
    public List<ListBuzzChild> listImage;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeTypedList(this.listImage);
    }

    public ListPublicImageResponse() {
    }

    protected ListPublicImageResponse(Parcel in) {
        super(in);
        this.listImage = in.createTypedArrayList(ListBuzzChild.CREATOR);
    }

    public static final Parcelable.Creator<ListPublicImageResponse> CREATOR = new Parcelable.Creator<ListPublicImageResponse>() {
        @Override
        public ListPublicImageResponse createFromParcel(Parcel source) {
            return new ListPublicImageResponse(source);
        }

        @Override
        public ListPublicImageResponse[] newArray(int size) {
            return new ListPublicImageResponse[size];
        }
    };
}
