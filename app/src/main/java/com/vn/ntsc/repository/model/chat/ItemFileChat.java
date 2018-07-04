package com.vn.ntsc.repository.model.chat;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.vn.ntsc.core.model.BaseBean;
import com.vn.ntsc.repository.TypeView;

/**
 * Created by Doremon on 2/28/2018.
 */

public class ItemFileChat extends BaseBean {
    /**
     * time_sender : 20180228042303902
     * thumbnail_url : https://10.64.100.22:281/image/thumbnail/201802/28/4f95db97-feb3-4550-9bda-d91231c92ac1.jpg
     * original_url : https://10.64.100.22:281/image/original_image/201802/28/9c771eb4-7f01-4c6e-9353-d137345ebe34.jpg
     * file_id : 5a962ea741afe701531df157
     * file_duration : 297
     * type : video
     * file_url : https://10.64.100.22:281/file/201802/28/017de922-d521-4ccb-b2ec-d9e992a9e579.mp4
     */

    @SerializedName("time_sender")
    public String timeSender;
    @SerializedName("thumbnail_url")
    public String thumbnailUrl;
    @SerializedName("original_url")
    public String originalUrl;
    @SerializedName("file_id")
    public String fileId;
    @SerializedName("file_duration")
    public int fileDuration;
    @SerializedName("type")
    public @TypeView.MediaDetailType
    String type;
    @SerializedName("file_url")
    public String fileUrl;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.timeSender);
        dest.writeString(this.thumbnailUrl);
        dest.writeString(this.originalUrl);
        dest.writeString(this.fileId);
        dest.writeInt(this.fileDuration);
        dest.writeString(this.type);
        dest.writeString(this.fileUrl);
    }

    public ItemFileChat() {
    }

    protected ItemFileChat(Parcel in) {
        this.timeSender = in.readString();
        this.thumbnailUrl = in.readString();
        this.originalUrl = in.readString();
        this.fileId = in.readString();
        this.fileDuration = in.readInt();
        this.type = in.readString();
        this.fileUrl = in.readString();
    }

    public static final Parcelable.Creator<ItemFileChat> CREATOR = new Parcelable.Creator<ItemFileChat>() {
        @Override
        public ItemFileChat createFromParcel(Parcel source) {
            return new ItemFileChat(source);
        }

        @Override
        public ItemFileChat[] newArray(int size) {
            return new ItemFileChat[size];
        }
    };
}
