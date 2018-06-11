package com.vn.ntsc.repository.model.timeline;

import android.os.Parcel;
import android.os.Parcelable;

import com.vn.ntsc.core.model.ServerResponse;

/**
 * Created by nankai on 8/9/2017.
 */

public class LikeBuzzResponse extends ServerResponse {

    public static final Parcelable.Creator<LikeBuzzResponse> CREATOR = new Parcelable.Creator<LikeBuzzResponse>() {
        @Override
        public LikeBuzzResponse createFromParcel(Parcel source) {
            return new LikeBuzzResponse(source);
        }

        @Override
        public LikeBuzzResponse[] newArray(int size) {
            return new LikeBuzzResponse[size];
        }
    };

    public LikeBuzzResponse() {
        super();
    }

    protected LikeBuzzResponse(Parcel in) {
        super(in);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
    }
}
