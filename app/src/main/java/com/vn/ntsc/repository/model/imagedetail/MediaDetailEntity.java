package com.vn.ntsc.repository.model.imagedetail;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ThoNh on 9/11/2017.
 */

public class MediaDetailEntity implements Parcelable {

    @SerializedName("media_type")
    public int mMediaType;

    @SerializedName("media_title")
    public String mTitleMedia;

    @SerializedName("media_url")
    public String mUrl;

    @SerializedName("number_like")
    public int mNumberLike;

    @SerializedName("number_comment")
    public int mNumberComment;

    private int mPosPlayedEarlier;

    public MediaDetailEntity(int mediaType, String titleMedia, String url, int numberLike, int numberComment) {
        mMediaType = mediaType;
        mTitleMedia = titleMedia;
        mUrl = url;
        mNumberLike = numberLike;
        mNumberComment = numberComment;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mMediaType);
        dest.writeString(this.mTitleMedia);
        dest.writeString(this.mUrl);
        dest.writeInt(this.mNumberLike);
        dest.writeInt(this.mNumberComment);
    }

    public MediaDetailEntity() {
    }

    protected MediaDetailEntity(Parcel in) {
        this.mMediaType = in.readInt();
        this.mTitleMedia = in.readString();
        this.mUrl = in.readString();
        this.mNumberLike = in.readInt();
        this.mNumberComment = in.readInt();
    }

    public static final Creator<MediaDetailEntity> CREATOR = new Creator<MediaDetailEntity>() {
        @Override
        public MediaDetailEntity createFromParcel(Parcel source) {
            return new MediaDetailEntity(source);
        }

        @Override
        public MediaDetailEntity[] newArray(int size) {
            return new MediaDetailEntity[size];
        }
    };
}
