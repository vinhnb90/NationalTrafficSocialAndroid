package com.vn.ntsc.repository.model.editprofile;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class ImageDataResponse implements Parcelable {
    @SerializedName("is_app")
    public int isApp;

    @SerializedName("image_id")
    public String imageId;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.isApp);
        dest.writeString(this.imageId);
    }

    public ImageDataResponse() {
    }

    protected ImageDataResponse(Parcel in) {
        this.isApp = in.readInt();
        this.imageId = in.readString();
    }

    public static final Creator<ImageDataResponse> CREATOR = new Creator<ImageDataResponse>() {
        @Override
        public ImageDataResponse createFromParcel(Parcel source) {
            return new ImageDataResponse(source);
        }

        @Override
        public ImageDataResponse[] newArray(int size) {
            return new ImageDataResponse[size];
        }
    };
}
