package com.vn.ntsc.repository.model.myalbum.LoadImageInAlbum;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.vn.ntsc.core.model.BaseBean;
import com.vn.ntsc.core.model.ServerResponse;
import com.vn.ntsc.repository.model.myalbum.ItemImageInAlbum;

import java.util.List;

/**
 * Created by ThoNh on 11/14/2017.
 */

public class LoadAlbumImageResponse extends ServerResponse {


    @SerializedName("data")
    public DataBean data;






    public static class DataBean extends BaseBean{
        /**
         * privacy : 2
         * album_des : Hà nội trong tôi
         * album_name : Hà nội mùa lá rụng
         * list_image : [{"thumbnail_height":1.7933333333333332,"time":1510617953288,"original_url":"http://10.64.100.22:81/image/original_image/201711/14/71f74181-4cc5-48e6-8644-6a4dfb9b68c4.png","thumbnail_width":3.183333333333333,"thumbnail_url":"http://10.64.100.22:81/image/thumbnail/201711/14/a92cac7c-57f4-43de-a258-c07fa3c5f3e9.jpg","file_id":"5a0a95d041afe7348980a084","original_height":538,"original_width":955}]
         */

        @SerializedName("privacy")
        public int privacy;
        @SerializedName("album_des")
        public String albumDes;
        @SerializedName("album_name")
        public String albumName;
        @SerializedName("list_image")
        public List<ItemImageInAlbum> listImage;


        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.privacy);
            dest.writeString(this.albumDes);
            dest.writeString(this.albumName);
            dest.writeTypedList(this.listImage);
        }

        public DataBean() {
        }

        protected DataBean(Parcel in) {
            this.privacy = in.readInt();
            this.albumDes = in.readString();
            this.albumName = in.readString();
            this.listImage = in.createTypedArrayList(ItemImageInAlbum.CREATOR);
        }

        public static final Parcelable.Creator<DataBean> CREATOR = new Parcelable.Creator<DataBean>() {
            @Override
            public DataBean createFromParcel(Parcel source) {
                return new DataBean(source);
            }

            @Override
            public DataBean[] newArray(int size) {
                return new DataBean[size];
            }
        };
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeParcelable(this.data, flags);
    }

    public LoadAlbumImageResponse() {
    }

    protected LoadAlbumImageResponse(Parcel in) {
        super(in);
        this.data = in.readParcelable(DataBean.class.getClassLoader());
    }

    public static final Parcelable.Creator<LoadAlbumImageResponse> CREATOR = new Parcelable.Creator<LoadAlbumImageResponse>() {
        @Override
        public LoadAlbumImageResponse createFromParcel(Parcel source) {
            return new LoadAlbumImageResponse(source);
        }

        @Override
        public LoadAlbumImageResponse[] newArray(int size) {
            return new LoadAlbumImageResponse[size];
        }
    };
}
