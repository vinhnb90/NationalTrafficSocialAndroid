package com.vn.ntsc.repository.model.notification;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.vn.ntsc.core.model.BaseBean;
import com.vn.ntsc.repository.model.notification.push.NotificationType;

/**
 * Created by ThoNh on 8/30/2017.
 */

/*
sample item
{
      "is_seen":0,
      "video_call_waiting":true,
      "noti_id":"599fd444e4b070a829196afc",
      "time":"20170825073948",
      "noti_user_id":"57a80e3de4b062b2d356ebb5",
      "voice_call_waiting":true,
      "noti_type":19,
      "noti_user_name":"namham",
      "is_read":1,
      "last_login":"20170829042432",
      "dist":14162.0,
      "noti_buzz_id":"599fd444e4b0b63d83544885"
    }
 */

public class NotificationItem extends BaseBean {

    @SerializedName("is_seen")
    public int isSeen;
    @SerializedName("video_call_waiting")
    public boolean isVideoWaiting;
    @SerializedName("noti_id")
    public String notiId;
    @SerializedName("time")
    public String time;
    @SerializedName("noti_user_id")
    public String notiUserId;
    @SerializedName("voice_call_waiting")
    public boolean isVoiceWaiting;
    @SerializedName("noti_type")
    @NotificationType
    public int notiType;
    @SerializedName("noti_user_name")
    public String notiUserName;
    @SerializedName("is_read")
    public int isRead;
    @SerializedName("last_login")
    public String lastLogin;
    @SerializedName("dist")
    public double distance;
    @SerializedName("noti_buzz_id")
    public String notifyBuzzId;
    @SerializedName("content")
    public String content;

    @SerializedName("url")
    public String url;

    @SerializedName("ownerid")
    public String ownerId;
    @SerializedName("stream_id")
    public String streamId;
    @SerializedName("stream_status")
    public int streamStatus; //0 == off; 1 == ON

    public NotificationItem() {

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.isSeen);
        dest.writeByte(this.isVideoWaiting ? (byte) 1 : (byte) 0);
        dest.writeString(this.notiId);
        dest.writeByte(this.isVoiceWaiting ? (byte) 1 : (byte) 0);
        dest.writeInt(this.notiType);
        dest.writeString(this.notiUserName);
        dest.writeInt(this.isRead);
        dest.writeString(this.lastLogin);
        dest.writeDouble(this.distance);
        dest.writeString(this.notifyBuzzId);
        dest.writeString(this.streamId);
        dest.writeInt(this.streamStatus);
    }

    protected NotificationItem(Parcel in) {
        this.isSeen = in.readInt();
        this.isVideoWaiting = in.readByte() != 0;
        this.notiId = in.readString();
        this.isVoiceWaiting = in.readByte() != 0;
        this.notiType = in.readInt();
        this.notiUserName = in.readString();
        this.isRead = in.readInt();
        this.lastLogin = in.readString();
        this.distance = in.readDouble();
        this.notifyBuzzId = in.readString();
        this.streamId = in.readString();
        this.streamStatus = in.readInt();
    }

    public static final Parcelable.Creator<NotificationItem> CREATOR = new Parcelable.Creator<NotificationItem>() {
        @Override
        public NotificationItem createFromParcel(Parcel source) {
            return new NotificationItem(source);
        }

        @Override
        public NotificationItem[] newArray(int size) {
            return new NotificationItem[size];
        }
    };
}
