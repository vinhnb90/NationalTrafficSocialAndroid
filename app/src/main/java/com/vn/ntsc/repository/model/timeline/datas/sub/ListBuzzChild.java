package com.vn.ntsc.repository.model.timeline.datas.sub;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.vn.ntsc.core.model.BaseBean;
import com.vn.ntsc.widget.views.images.mediadetail.video.MediaStateListener;

/**
 * Created by nankai on 9/22/2017.
 */

public class ListBuzzChild extends BaseBean {

    @SerializedName("buzz_id")
    public String buzzId;

    @SerializedName("cmt_num")
    public int commentNumber;

    @SerializedName("like")
    public LikeBean like;

    @SerializedName("buzz_time")
    public String buzzTime;

    @SerializedName("thumbnail_url")
    public String thumbnailUrl;

    @SerializedName("buzz_type")
    public String buzzType;

    @SerializedName("is_app")
    public int isApp;

    @SerializedName("user_id")
    public String userId;

    @SerializedName("buzz_val")
    public String buzzVal;

    @SerializedName("url")
    public String url;

    // id of file
    @SerializedName("file_id")
    public String fileId;

    //live stream
    @SerializedName("stream_id")
    public String streamId;

    @SerializedName("stream_status")
    public String streamStatus;

    @SerializedName("view_number")
    public String viewNumber;

    @SerializedName("current_view")
    public String currentViewNumber;

    @SerializedName("stream_end_time")
    public String streamEndTime;

    @SerializedName("stream_duration")
    public String streamDuration;

    @SerializedName("duration")
    public String duration;

    @SerializedName("thumbnail_height")
    public float thumbnailHeight;
    @SerializedName("thumbnail_width")
    public float thumbnailWidth;

    @SerializedName("file_duration")
    public int mDuration ;

    // using for pause, play, stop, release media video
    public MediaStateListener mVideoStateListener;

    public boolean isPlay ;

    public void setVideoStateListener(MediaStateListener videoStateListener) {
        mVideoStateListener = videoStateListener;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ListBuzzChild)) return false;

        ListBuzzChild that = (ListBuzzChild) o;

        if (!buzzId.equals(that.buzzId)) return false;
        if (userId != null ? !userId.equals(that.userId) : that.userId != null) return false;
        return streamId != null ? streamId.equals(that.streamId) : that.streamId == null;
    }

    @Override
    public int hashCode() {
        int result = buzzId.hashCode();
        result = 31 * result + (userId != null ? userId.hashCode() : 0);
        result = 31 * result + (streamId != null ? streamId.hashCode() : 0);
        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.buzzId);
        dest.writeInt(this.commentNumber);
        dest.writeParcelable(this.like, flags);
        dest.writeString(this.buzzTime);
        dest.writeString(this.thumbnailUrl);
        dest.writeString(this.buzzType);
        dest.writeInt(this.isApp);
        dest.writeString(this.userId);
        dest.writeString(this.buzzVal);
        dest.writeString(this.url);
        dest.writeString(this.fileId);
        dest.writeString(this.streamId);
        dest.writeString(this.streamStatus);
        dest.writeString(this.viewNumber);
        dest.writeString(this.currentViewNumber);
        dest.writeString(this.streamEndTime);
        dest.writeString(this.streamDuration);
        dest.writeString(this.duration);
        dest.writeFloat(this.thumbnailHeight);
        dest.writeFloat(this.thumbnailWidth);
        dest.writeInt(this.mDuration);
        dest.writeByte(this.isPlay ? (byte) 1 : (byte) 0);
    }

    public ListBuzzChild() {
    }

    protected ListBuzzChild(Parcel in) {
        this.buzzId = in.readString();
        this.commentNumber = in.readInt();
        this.like = in.readParcelable(LikeBean.class.getClassLoader());
        this.buzzTime = in.readString();
        this.thumbnailUrl = in.readString();
        this.buzzType = in.readString();
        this.isApp = in.readInt();
        this.userId = in.readString();
        this.buzzVal = in.readString();
        this.url = in.readString();
        this.fileId = in.readString();
        this.streamId = in.readString();
        this.streamStatus = in.readString();
        this.viewNumber = in.readString();
        this.currentViewNumber = in.readString();
        this.streamEndTime = in.readString();
        this.streamDuration = in.readString();
        this.duration = in.readString();
        this.thumbnailHeight = in.readFloat();
        this.thumbnailWidth = in.readFloat();
        this.mDuration = in.readInt();
        this.isPlay = in.readByte() != 0;
    }

    public static final Parcelable.Creator<ListBuzzChild> CREATOR = new Parcelable.Creator<ListBuzzChild>() {
        @Override
        public ListBuzzChild createFromParcel(Parcel source) {
            return new ListBuzzChild(source);
        }

        @Override
        public ListBuzzChild[] newArray(int size) {
            return new ListBuzzChild[size];
        }
    };
}
