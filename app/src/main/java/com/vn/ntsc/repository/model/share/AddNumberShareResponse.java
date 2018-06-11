package com.vn.ntsc.repository.model.share;

import android.os.Parcel;
import android.os.Parcelable;

import com.vn.ntsc.core.model.ServerResponse;

/**
 * Created by nankai on 11/24/2017.
 */

public class AddNumberShareResponse extends ServerResponse {

    public AddNumberShareResponse() {
        super();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
    }

    protected AddNumberShareResponse(Parcel in) {
        super(in);
    }

    public static final Parcelable.Creator<AddNumberShareResponse> CREATOR = new Parcelable.Creator<AddNumberShareResponse>() {
        @Override
        public AddNumberShareResponse createFromParcel(Parcel source) {
            return new AddNumberShareResponse(source);
        }

        @Override
        public AddNumberShareResponse[] newArray(int size) {
            return new AddNumberShareResponse[size];
        }
    };
}
