package com.vn.ntsc.repository.model.poststatus.uploadsetting;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.vn.ntsc.core.model.BaseBean;

/**
 * Created by Robert on 2017 Oct 24
 */
public class UploadSettingBean extends BaseBean {

    @SerializedName("max_audio_number")
    public int maxAudioNumber;
    @SerializedName("total_file_size")
    public int totalFileSize;//Total file size in Megabyte
    @SerializedName("max_video_number")
    public int maxVideoNumber;
    @SerializedName("default_audio_img")
    public String defaultAudioImg;
    @SerializedName("max_file_size")
    public int maxFileSize;//File size in Megabyte
    @SerializedName("max_image_number")
    public int maxImageNumber;
    @SerializedName("max_length_buzz")//max_comment_length_allowed
    public int maxCommentLengthAllowed;
    @SerializedName("max_file_per_album")
    public int maxFilePerAlbum;
    @SerializedName("total_file_upload")
    public int totalFileUpload;

    public UploadSettingBean(int maxAudioNumber, int totalFileSize, int maxVideoNumber, String defaultAudioImg, int maxFileSize, int maxImageNumber, int maxCommentLengthAllowed, int maxFilePerAlbum, int totalFileUpload) {
        this.maxAudioNumber = maxAudioNumber;
        this.totalFileSize = totalFileSize;
        this.maxVideoNumber = maxVideoNumber;
        this.defaultAudioImg = defaultAudioImg;
        this.maxFileSize = maxFileSize;
        this.maxImageNumber = maxImageNumber;
        this.maxCommentLengthAllowed = maxCommentLengthAllowed;
        this.maxFilePerAlbum = maxFilePerAlbum;
        this.totalFileUpload = totalFileUpload;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.maxAudioNumber);
        dest.writeInt(this.totalFileSize);
        dest.writeInt(this.maxVideoNumber);
        dest.writeString(this.defaultAudioImg);
        dest.writeInt(this.maxFileSize);
        dest.writeInt(this.maxImageNumber);
        dest.writeInt(this.maxCommentLengthAllowed);
        dest.writeInt(this.maxFilePerAlbum);
        dest.writeInt(this.totalFileUpload);
    }

    public UploadSettingBean() {
    }

    protected UploadSettingBean(Parcel in) {
        this.maxAudioNumber = in.readInt();
        this.totalFileSize = in.readInt();
        this.maxVideoNumber = in.readInt();
        this.defaultAudioImg = in.readString();
        this.maxFileSize = in.readInt();
        this.maxImageNumber = in.readInt();
        this.maxCommentLengthAllowed = in.readInt();
        this.maxFilePerAlbum = in.readInt();
        this.totalFileUpload = in.readInt();
    }

    public static final Parcelable.Creator<UploadSettingBean> CREATOR = new Parcelable.Creator<UploadSettingBean>() {
        @Override
        public UploadSettingBean createFromParcel(Parcel source) {
            return new UploadSettingBean(source);
        }

        @Override
        public UploadSettingBean[] newArray(int size) {
            return new UploadSettingBean[size];
        }
    };
}
