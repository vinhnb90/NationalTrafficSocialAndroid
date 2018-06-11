package com.vn.ntsc.repository.model.chat;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.tux.socket.models.Media;
import com.vn.ntsc.core.model.ServerResponse;

import java.util.List;

/**
 * Created by ducng on 11/2/2017.
 * upload file response
 */
public class UploadFileResponse extends ServerResponse {
    @SerializedName("data")
    public List<Media.FileBean> listData;

    /**
     * store origin file path when upload success
     */
    public String originFilePath;

    public List<Media.FileBean> getListData() {
        return listData;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeTypedList(this.listData);
    }

    public UploadFileResponse() {
    }

    protected UploadFileResponse(Parcel in) {
        super(in);
        this.listData = in.createTypedArrayList(Media.FileBean.CREATOR);
    }

    public static final Parcelable.Creator<UploadFileResponse> CREATOR = new Parcelable.Creator<UploadFileResponse>() {
        @Override
        public UploadFileResponse createFromParcel(Parcel source) {
            return new UploadFileResponse(source);
        }

        @Override
        public UploadFileResponse[] newArray(int size) {
            return new UploadFileResponse[size];
        }
    };
}