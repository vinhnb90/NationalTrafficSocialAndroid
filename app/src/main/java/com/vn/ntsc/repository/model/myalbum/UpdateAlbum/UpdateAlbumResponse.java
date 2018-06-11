package com.vn.ntsc.repository.model.myalbum.UpdateAlbum;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.vn.ntsc.core.model.ServerResponse;
import com.vn.ntsc.repository.model.myalbum.LoadAlbum.LoadAlbumResponse;

/**
 * Created by ThoNh on 11/14/2017.
 */

public class UpdateAlbumResponse extends ServerResponse {

    /**
     * data : {"album_id":"5a0a909541afe7355fb866b3","privacy":2,"album_des":"Hà nội trong tôit","album_name":"Hà nội mùa lá rụng"}
     */

    @SerializedName("data")
    public LoadAlbumResponse.DataBean data;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeParcelable(this.data, flags);
    }

    public UpdateAlbumResponse() {
    }

    protected UpdateAlbumResponse(Parcel in) {
        super(in);
        this.data = in.readParcelable(LoadAlbumResponse.DataBean.class.getClassLoader());
    }

    public static final Parcelable.Creator<UpdateAlbumResponse> CREATOR = new Parcelable.Creator<UpdateAlbumResponse>() {
        @Override
        public UpdateAlbumResponse createFromParcel(Parcel source) {
            return new UpdateAlbumResponse(source);
        }

        @Override
        public UpdateAlbumResponse[] newArray(int size) {
            return new UpdateAlbumResponse[size];
        }
    };
}
