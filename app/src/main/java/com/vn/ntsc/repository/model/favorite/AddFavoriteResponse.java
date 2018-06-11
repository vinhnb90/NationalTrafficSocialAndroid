package com.vn.ntsc.repository.model.favorite;

import android.os.Parcel;
import android.os.Parcelable;

import com.vn.ntsc.core.model.ServerResponse;

/**
 * Created by nankai on 8/11/2017.
 */

public class AddFavoriteResponse extends ServerResponse {

    public AddFavoriteResponse() {
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

    protected AddFavoriteResponse(Parcel in) {
        super(in);
    }

    public static final Parcelable.Creator<AddFavoriteResponse> CREATOR = new Parcelable.Creator<AddFavoriteResponse>() {
        @Override
        public AddFavoriteResponse createFromParcel(Parcel source) {
            return new AddFavoriteResponse(source);
        }

        @Override
        public AddFavoriteResponse[] newArray(int size) {
            return new AddFavoriteResponse[size];
        }
    };
}
