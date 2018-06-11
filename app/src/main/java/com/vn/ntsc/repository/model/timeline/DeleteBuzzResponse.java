package com.vn.ntsc.repository.model.timeline;

import android.os.Parcel;
import android.os.Parcelable;

import com.vn.ntsc.core.model.ServerResponse;

/**
 * Created by nankai on 8/16/2017.
 */

public class DeleteBuzzResponse extends ServerResponse {

    public DeleteBuzzResponse() {
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

    protected DeleteBuzzResponse(Parcel in) {
        super(in);
    }

    public static final Parcelable.Creator<DeleteBuzzResponse> CREATOR = new Parcelable.Creator<DeleteBuzzResponse>() {
        @Override
        public DeleteBuzzResponse createFromParcel(Parcel source) {
            return new DeleteBuzzResponse(source);
        }

        @Override
        public DeleteBuzzResponse[] newArray(int size) {
            return new DeleteBuzzResponse[size];
        }
    };
}
