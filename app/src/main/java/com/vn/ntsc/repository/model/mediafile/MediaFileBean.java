package com.vn.ntsc.repository.model.mediafile;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.vn.ntsc.core.model.BaseBean;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Robert on 2017 Sep 06.
 */
public class MediaFileBean extends BaseBean {

    public static final int NONE = 0;//Nothing
    public static final int IMAGE = 1;//Media type of image when show
    public static final int VIDEO = 2;//Media type of video when show
    public static final int AUDIO = 3;//Media type of audio when show


    @IntDef({ IMAGE, VIDEO, AUDIO })
    @Retention(RetentionPolicy.SOURCE)
    public @interface MediaType {
    }

    @IntDef({ IMAGE, VIDEO, AUDIO })
    @Retention(RetentionPolicy.SOURCE)
    public @interface SpecialMediaFileType {
    }

    @MediaFileBean.MediaType
    public int mediaType;

    /**
     * The index of media file on MediaStore
     */
    public long id = 0;

    /**
     * The media file is selected or not yet
     */
    public boolean isCheck;

    /**
     * The index of file selected
     */
    public int mOrder = 0;

    /**
     * use for video file, if is empty with image file
     */
    public String duration = "";

    /**
     * The URI of media file
     */
    public String mediaUri;
    public long fileSizeInKB = 0;//Use when picked or unpicked
    public int pickedPosition = 0;

    public MediaFileBean(long id, @MediaFileBean.SpecialMediaFileType int tileType) {
        this(id, null, tileType, false);
    }

    public MediaFileBean(long id, @NonNull String mediaUri, @MediaFileBean.MediaType int mediaType) {
        this(id, mediaUri, IMAGE, false);
        this.mediaType = mediaType;
    }

    public MediaFileBean(long id, String uri, String duration, @MediaFileBean.MediaType int mediaType) {
        this(id,uri, mediaType, false);
        this.duration = duration;
    }

    public MediaFileBean(long id, @Nullable String mediaUri, @MediaFileBean.MediaType int mediaType, boolean isCheck) {
        this.id = id;
        this.mediaUri = mediaUri;
        this.mediaType = mediaType;
        this.isCheck = isCheck;
    }

    @Override
    public String toString() {
        return "MediaFileBean{" +
                "id=" + id +
                ", mediaType=" + mediaType +
                ", isCheck=" + isCheck +
                ", mOrder='" + mOrder + '\'' +
                ", duration='" + duration + '\'' +
                ", fileSizeInKB='" + fileSizeInKB + '\'' +
                ", pickedPosition='" + pickedPosition + '\'' +
                ", mediaUri='" + mediaUri + '\'' +
                '}';
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeLong(this.id);
        dest.writeString(this.mediaUri);
        dest.writeInt(this.mediaType);
        dest.writeString(this.duration);
        dest.writeByte(this.isCheck ? (byte) 1 : (byte) 0);
        dest.writeInt(this.mOrder);
        dest.writeLong(this.fileSizeInKB);
        dest.writeInt(this.pickedPosition);

    }

    protected MediaFileBean(Parcel in) {

        this.id = in.readLong();
        this.mediaUri = in.readString();
        this.mediaType = in.readInt();
        this.duration = in.readString();
        this.isCheck = in.readByte() != 0;
        this.mOrder = in.readInt();
        this.fileSizeInKB = in.readLong();
        this.pickedPosition = in.readInt();

    }

    public static final Parcelable.Creator<MediaFileBean> CREATOR = new Parcelable.Creator<MediaFileBean>() {
        @Override
        public MediaFileBean createFromParcel(Parcel source) {
            return new MediaFileBean(source);
        }

        @Override
        public MediaFileBean[] newArray(int size) {
            return new MediaFileBean[size];
        }
    };

}
