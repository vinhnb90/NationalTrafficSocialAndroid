package com.vn.ntsc.repository.model.myalbum.LoadAlbum;

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

public class LoadAlbumResponse extends ServerResponse {

    @SerializedName("data")
    public List<DataBean> data;

    public static class DataBean extends BaseBean {
        /**
         * number_image : 0
         * album_id : 5a0a958141afe7370d71de53
         * time : 1510617873749
         * image_list : []
         * privacy : 0
         * user_id : 5a03e3ce41afe7451cbb8736
         * album_des : Hà nội trong tôi
         * album_name : Hà nội mùa lá rụng
         */
        @SerializedName("number_image")
        public int numberImage;
        @SerializedName("album_id")
        public String albumId;
        @SerializedName("time")
        public long time;
        @SerializedName("privacy")
        public int privacy;
        @SerializedName("user_id")
        public String userId;
        @SerializedName("album_des")
        public String albumDes;
        @SerializedName("album_name")
        public String albumName;
        @SerializedName("image_list")
        public ItemImageInAlbum imageList;

        public boolean isUploading = false;

        public boolean isCreateNew = false;


        public DataBean() {
        }

        @Override
        public String toString() {
            return "DataBean{" +
                    "numberImage=" + numberImage +
                    ", albumId='" + albumId + '\'' +
                    ", time=" + time +
                    ", privacy=" + privacy +
                    ", userId='" + userId + '\'' +
                    ", albumDes='" + albumDes + '\'' +
                    ", albumName='" + albumName + '\'' +
                    ", imageList=" + imageList +
                    ", isUploading=" + isUploading +
                    '}';
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.numberImage);
            dest.writeString(this.albumId);
            dest.writeLong(this.time);
            dest.writeInt(this.privacy);
            dest.writeString(this.userId);
            dest.writeString(this.albumDes);
            dest.writeString(this.albumName);
            dest.writeParcelable(this.imageList, flags);
            dest.writeByte(this.isUploading ? (byte) 1 : (byte) 0);
        }

        protected DataBean(Parcel in) {
            this.numberImage = in.readInt();
            this.albumId = in.readString();
            this.time = in.readLong();
            this.privacy = in.readInt();
            this.userId = in.readString();
            this.albumDes = in.readString();
            this.albumName = in.readString();
            this.imageList = in.readParcelable(ItemImageInAlbum.class.getClassLoader());
            this.isUploading = in.readByte() != 0;
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


        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof DataBean)) return false;

            DataBean bean = (DataBean) o;

            if (!albumId.equals(bean.albumId)) return false;
            return userId.equals(bean.userId);
        }

        @Override
        public int hashCode() {
            int result = albumId.hashCode();
            result = 31 * result + userId.hashCode();
            result = 31 * result + (isUploading ? 1 : 0);
            result = 31 * result + (isCreateNew ? 1 : 0);
            return result;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeTypedList(this.data);
    }

    public LoadAlbumResponse() {
    }

    protected LoadAlbumResponse(Parcel in) {
        super(in);
        this.data = in.createTypedArrayList(DataBean.CREATOR);
    }

    public static final Parcelable.Creator<LoadAlbumResponse> CREATOR = new Parcelable.Creator<LoadAlbumResponse>() {
        @Override
        public LoadAlbumResponse createFromParcel(Parcel source) {
            return new LoadAlbumResponse(source);
        }

        @Override
        public LoadAlbumResponse[] newArray(int size) {
            return new LoadAlbumResponse[size];
        }
    };


}
