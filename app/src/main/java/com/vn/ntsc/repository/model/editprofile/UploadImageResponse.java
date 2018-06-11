package com.vn.ntsc.repository.model.editprofile;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.vn.ntsc.core.model.ServerResponse;

/**
 * Created by dev22 on 8/30/17.
 */
public class UploadImageResponse extends ServerResponse {
    @SerializedName("data")
    public ImageDataResponse data;

    public UploadImageResponse(int code, ImageDataResponse data) {
        this.code = code;
        this.data = data;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeParcelable(this.data, flags);
    }

    public UploadImageResponse() {
    }

    protected UploadImageResponse(Parcel in) {
        super(in);
        this.data = in.readParcelable(ImageDataResponse.class.getClassLoader());
    }

    public static final Parcelable.Creator<UploadImageResponse> CREATOR = new Parcelable.Creator<UploadImageResponse>() {
        @Override
        public UploadImageResponse createFromParcel(Parcel source) {
            return new UploadImageResponse(source);
        }

        @Override
        public UploadImageResponse[] newArray(int size) {
            return new UploadImageResponse[size];
        }
    };
}

