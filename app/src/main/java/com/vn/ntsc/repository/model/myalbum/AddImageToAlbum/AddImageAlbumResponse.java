package com.vn.ntsc.repository.model.myalbum.AddImageToAlbum;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.vn.ntsc.core.model.ServerResponse;
import com.vn.ntsc.repository.model.myalbum.LoadAlbum.LoadAlbumResponse;

/**
 * Created by ThoNh on 11/14/2017.
 */

public class AddImageAlbumResponse extends ServerResponse {

    /**
     * data : {"album_id":"5a12a3a241afe763f026afd6","time":1511145778802,"image_list":[{"thumbnail_height":0.26666666666666666,"original_url":"http://10.64.100.22:81/image/original_image/201711/20/f2783c82-2ba7-4094-bfee-6d6abf5c4902.png","thumbnail_width":0.2633333333333333,"thumbnail_url":"http://10.64.100.22:81/image/thumbnail/201711/20/f4ff731e-2a19-4893-bbad-5c0ed73a5d40.jpg","file_id":"5a12a3cb41afe76800986a67","original_height":80,"original_width":79}],"privacy":0,"album_des":"yy","user_id":"5a03e3ce41afe7451cbb8736","album_name":"yy"}
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

    public AddImageAlbumResponse() {
    }

    protected AddImageAlbumResponse(Parcel in) {
        super(in);
        this.data = in.readParcelable(LoadAlbumResponse.DataBean.class.getClassLoader());
    }

    public static final Parcelable.Creator<AddImageAlbumResponse> CREATOR = new Parcelable.Creator<AddImageAlbumResponse>() {
        @Override
        public AddImageAlbumResponse createFromParcel(Parcel source) {
            return new AddImageAlbumResponse(source);
        }

        @Override
        public AddImageAlbumResponse[] newArray(int size) {
            return new AddImageAlbumResponse[size];
        }
    };

    @Override
    public String toString() {
        return "AddImageAlbumResponse{" +
                "data=" + data +
                '}';
    }
}
