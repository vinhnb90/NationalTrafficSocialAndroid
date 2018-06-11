package com.vn.ntsc.repository.model.myalbum.DeleteImageInAlbum;

import android.os.Parcel;
import android.os.Parcelable;

import com.vn.ntsc.core.model.ServerResponse;

/**
 * Created by ThoNh on 11/14/2017.
 */

public class DelAlbumImageResponse extends ServerResponse {


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
    }

    public DelAlbumImageResponse() {
    }

    protected DelAlbumImageResponse(Parcel in) {
        super(in);
    }

    public static final Parcelable.Creator<DelAlbumImageResponse> CREATOR = new Parcelable.Creator<DelAlbumImageResponse>() {
        @Override
        public DelAlbumImageResponse createFromParcel(Parcel source) {
            return new DelAlbumImageResponse(source);
        }

        @Override
        public DelAlbumImageResponse[] newArray(int size) {
            return new DelAlbumImageResponse[size];
        }
    };
}
