package com.vn.ntsc.repository.model.myalbum.DeleteAlbum;

import android.os.Parcel;
import android.os.Parcelable;

import com.vn.ntsc.core.model.ServerResponse;

/**
 * Created by ThoNh on 11/14/2017.
 */

public class DelAlbumResponse  extends ServerResponse{

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
    }

    public DelAlbumResponse() {
    }

    protected DelAlbumResponse(Parcel in) {
        super(in);
    }

    public static final Parcelable.Creator<DelAlbumResponse> CREATOR = new Parcelable.Creator<DelAlbumResponse>() {
        @Override
        public DelAlbumResponse createFromParcel(Parcel source) {
            return new DelAlbumResponse(source);
        }

        @Override
        public DelAlbumResponse[] newArray(int size) {
            return new DelAlbumResponse[size];
        }
    };
}
