package com.vn.ntsc.repository.model.myalbum;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.vn.ntsc.core.model.BaseBean;

public class ItemImageInAlbum extends BaseBean {

    @SerializedName("thumbnail_height")
    public double thumbnailHeight;
    @SerializedName("time")
    public long time;
    @SerializedName("original_url")
    public String originalUrl;
    @SerializedName("thumbnail_width")
    public double thumbnailWidth;
    @SerializedName("thumbnail_url")
    public String thumbnailUrl;
    @SerializedName("file_id")
    public String fileId;
    @SerializedName("original_height")
    public int originalHeight;
    @SerializedName("original_width")
    public int originalWidth;

    public boolean isSelected = false;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(this.thumbnailHeight);
        dest.writeLong(this.time);
        dest.writeString(this.originalUrl);
        dest.writeDouble(this.thumbnailWidth);
        dest.writeString(this.thumbnailUrl);
        dest.writeString(this.fileId);
        dest.writeInt(this.originalHeight);
        dest.writeInt(this.originalWidth);
        dest.writeByte(this.isSelected ? (byte) 1 : (byte) 0);
    }

    public ItemImageInAlbum() {
    }

    protected ItemImageInAlbum(Parcel in) {
        this.thumbnailHeight = in.readDouble();
        this.time = in.readLong();
        this.originalUrl = in.readString();
        this.thumbnailWidth = in.readDouble();
        this.thumbnailUrl = in.readString();
        this.fileId = in.readString();
        this.originalHeight = in.readInt();
        this.originalWidth = in.readInt();
        this.isSelected = in.readByte() != 0;
    }

    public static final Parcelable.Creator<ItemImageInAlbum> CREATOR = new Parcelable.Creator<ItemImageInAlbum>() {
        @Override
        public ItemImageInAlbum createFromParcel(Parcel source) {
            return new ItemImageInAlbum(source);
        }

        @Override
        public ItemImageInAlbum[] newArray(int size) {
            return new ItemImageInAlbum[size];
        }
    };
}