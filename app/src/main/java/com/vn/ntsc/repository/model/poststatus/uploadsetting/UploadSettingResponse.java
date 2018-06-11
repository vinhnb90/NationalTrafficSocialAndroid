package com.vn.ntsc.repository.model.poststatus.uploadsetting;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.vn.ntsc.core.model.ServerResponse;


/**
 * Created by Robert on 2017 Aug 28.
 */
public class UploadSettingResponse extends ServerResponse {

    @SerializedName("data")
    public UploadSettingBean setting;

    protected UploadSettingResponse(Parcel in) {
        super(in);
        this.code = in.readInt();
    }


    public static final Parcelable.Creator<UploadSettingResponse> CREATOR = new Parcelable.Creator<UploadSettingResponse>() {
        @Override
        public UploadSettingResponse createFromParcel(Parcel source) {
            return new UploadSettingResponse(source);
        }

        @Override
        public UploadSettingResponse[] newArray(int size) {
            return new UploadSettingResponse[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
    }
}