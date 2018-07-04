package com.vn.ntsc.repository.model.media;

import android.os.Parcel;
import android.os.Parcelable;

import com.vn.ntsc.core.model.BaseBean;
import com.vn.ntsc.repository.TypeView;

/**
 * Created by ThoNh on 11/21/2017.
 */

public class MediaEntity extends BaseBean {
    private static final String TAG = MediaEntity.class.getSimpleName();

    /**
     * Position in adapter
     */
    public int mPosition;


    /**
     * Origin Url
     */
    public String mUrl;

    /**
     * @see TypeView.MediaDetailType#IMAGE_TYPE
     * @see TypeView.MediaDetailType#VIDEO_TYPE
     * @see TypeView.MediaDetailType#AUDIO_TYPE
     */
    public String mType;


    /**
     * Thumbnail Url
     */
    public String mThumbnail;

    private int mCurrentPauseTime;

    public MediaEntity(int position, String url, @TypeView.MediaDetailType String type, String thumbnail) {
        mPosition = position;
        mUrl = url;
        mType = type;
        mThumbnail = thumbnail;
    }


    public int getCurrentPauseTime() {
        return mCurrentPauseTime;
    }

    public void setCurrentPauseTime(int currentPauseTime) {
        mCurrentPauseTime = currentPauseTime;
    }


    @Override
    public String toString() {
        return "MediaEntity{" +
                "mPosition=" + mPosition +
                ", mUrl='" + mUrl + '\'' +
                ", mType='" + mType + '\'' +
                ", mCurrentPauseTime='" + mCurrentPauseTime + '\'' +
                '}';
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mPosition);
        dest.writeString(this.mUrl);
        dest.writeInt(this.mCurrentPauseTime);
        dest.writeString(this.mType);
        dest.writeString(this.mThumbnail);
    }

    protected MediaEntity(Parcel in) {
        this.mPosition = in.readInt();
        this.mUrl = in.readString();
        this.mCurrentPauseTime = in.readInt();
        this.mType = in.readString();
        this.mThumbnail = in.readString();
    }

    public static final Parcelable.Creator<MediaEntity> CREATOR = new Parcelable.Creator<MediaEntity>() {
        @Override
        public MediaEntity createFromParcel(Parcel source) {
            return new MediaEntity(source);
        }

        @Override
        public MediaEntity[] newArray(int size) {
            return new MediaEntity[size];
        }
    };
}
