package com.vn.ntsc.repository.model.media;

import android.os.Parcel;
import android.os.Parcelable;

import com.vn.ntsc.core.model.BaseBean;
import com.vn.ntsc.repository.TypeView;
import com.vn.ntsc.ui.mediadetail.base.ActivityLifeCycleListener;
import com.vn.ntsc.ui.mediadetail.base.MediaAdapter;
import com.vn.ntsc.utils.LogUtils;

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


    /**
     * If Media is Audio, Video, use mPlayImmediately for play immediately after load buffer
     */
    private boolean mPlayImmediately;

    private ActivityLifeCycleListener mListener;

    private MediaAdapter.OnPageChangeListener mOnPageChangeListener;

    public MediaEntity(int position, String url, @TypeView.MediaDetailType String type, String thumbnail) {
        mPosition = position;
        mUrl = url;
        mType = type;
        mThumbnail = thumbnail;
    }

    public void setPlayImmediately(boolean playImmediately) {
        mPlayImmediately = playImmediately;
    }

    public boolean isPlayImmediately() {
        return mPlayImmediately;
    }

    public void setActivityListener(ActivityLifeCycleListener listener) {
        this.mListener = listener;
    }


    public void setOnPageChangeListener(MediaAdapter.OnPageChangeListener onPageChangeListener) {
        this.mOnPageChangeListener = onPageChangeListener;
    }


    public void onActivityResume() {
        if (mListener != null)
            mListener.onActivityResume();
        LogUtils.e(TAG, "onActivityResume ------->");
    }


    public void onActivityPause() {
        if (mListener != null)
            mListener.onActivityPause();
        LogUtils.e(TAG, "onActivityPause ------->");
    }


    public void onActivityDestroy() {
        if (mListener != null)
            mListener.onActivityDestroy();
        LogUtils.e(TAG, "onActivityDestroy ------->");
    }


    public void onActivityStop() {
        if (mListener != null)
            mListener.onActivityStop();
        LogUtils.e(TAG, "onActivityStop ------->");
    }


    public void onActivityConfigChange(int mOrientation, boolean isOrientationSettingOn) {
        if (mListener != null)
            mListener.onActivityConfigChange(mOrientation, isOrientationSettingOn);
        LogUtils.e(TAG, "onActivityConfigChange ------->");
    }


    @Override
    public String toString() {
        return "MediaEntity{" +
                "mPosition=" + mPosition +
                ", mUrl='" + mUrl + '\'' +
                ", mType='" + mType + '\'' +
                ", mListener=" + mListener +
                ", mOnPageChangeListener=" + mOnPageChangeListener +
                '}';
    }


    public void onPageComing(int position) {
        LogUtils.e(TAG, "onPageComing -------> Origin position: " + mPosition + " -----> fucking position:" + position);
        if (mOnPageChangeListener != null)
            mOnPageChangeListener.onPageComing();
    }

    public void onPageLeaving(int position) {
        LogUtils.e(TAG, "onPageLeaving -------> Origin position: " + mPosition + " -----> fucking position:" + position);
        if (mOnPageChangeListener != null)
            mOnPageChangeListener.onPageLeaving();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mPosition);
        dest.writeString(this.mUrl);
        dest.writeString(this.mType);
        dest.writeString(this.mThumbnail);
        dest.writeByte(this.mPlayImmediately ? (byte) 1 : (byte) 0);
    }

    protected MediaEntity(Parcel in) {
        this.mPosition = in.readInt();
        this.mUrl = in.readString();
        this.mType = in.readString();
        this.mThumbnail = in.readString();
        this.mPlayImmediately = in.readByte() != 0;
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
