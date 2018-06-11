package com.vn.ntsc.repository.model.favorite;

import android.os.Parcel;
import android.os.Parcelable;

import com.vn.ntsc.core.model.ServerResponse;

/**
 * Created by hnc on 11/08/2017.
 */

public class RemoveFavoriteResponse extends ServerResponse {

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
    }

    public RemoveFavoriteResponse() {
    }

    protected RemoveFavoriteResponse(Parcel in) {
        super(in);
    }

    public static final Parcelable.Creator<RemoveFavoriteResponse> CREATOR = new Parcelable.Creator<RemoveFavoriteResponse>() {
        @Override
        public RemoveFavoriteResponse createFromParcel(Parcel source) {
            return new RemoveFavoriteResponse(source);
        }

        @Override
        public RemoveFavoriteResponse[] newArray(int size) {
            return new RemoveFavoriteResponse[size];
        }
    };
}
