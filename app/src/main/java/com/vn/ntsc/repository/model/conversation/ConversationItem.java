package com.vn.ntsc.repository.model.conversation;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.vn.ntsc.core.model.BaseBean;

/**
 * Created by ThoNH on 25/08/2017.
 */

public class ConversationItem extends BaseBean {

    @SerializedName("frd_id")
    public String friendId;
    @SerializedName("frd_name")
    public String name;
    @SerializedName("is_online")
    public boolean isOnline;
    @SerializedName("last_msg")
    public String lastMessage;
    @SerializedName("is_own")
    public boolean isOwn;
    @SerializedName("sent_time")
    public String sentTime;
    @SerializedName("unread_num")
    public int unreadNum;
    @SerializedName("long")
    public double longtitude;
    @SerializedName("lat")
    public double lattitude;
    @SerializedName("dist")
    public double distance;
    @SerializedName("ava_id")
    public String avaId;
    @SerializedName("thumbnail_url")
    public String thumbnailUrl;
    @SerializedName("gender")
    public int gender;
    @SerializedName("msg_type")
    public String messageType;
    @SerializedName("is_anonymous")
    public boolean anonymous;
    @SerializedName("voice_call_waiting")
    public boolean isVoiceCallWaiting;
    @SerializedName("video_call_waiting")
    public boolean isVideoCallWaiting;

    public ConversationItem() {
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.friendId);
        dest.writeString(this.name);
        dest.writeByte(this.isOnline ? (byte) 1 : (byte) 0);
        dest.writeString(this.lastMessage);
        dest.writeByte(this.isOwn ? (byte) 1 : (byte) 0);
        dest.writeString(this.sentTime);
        dest.writeInt(this.unreadNum);
        dest.writeDouble(this.longtitude);
        dest.writeDouble(this.lattitude);
        dest.writeDouble(this.distance);
        dest.writeString(this.avaId);
        dest.writeString(this.thumbnailUrl);
        dest.writeInt(this.gender);
        dest.writeString(this.messageType);
        dest.writeByte(this.anonymous ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isVoiceCallWaiting ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isVideoCallWaiting ? (byte) 1 : (byte) 0);
    }

    protected ConversationItem(Parcel in) {
        this.friendId = in.readString();
        this.name = in.readString();
        this.isOnline = in.readByte() != 0;
        this.lastMessage = in.readString();
        this.isOwn = in.readByte() != 0;
        this.sentTime = in.readString();
        this.unreadNum = in.readInt();
        this.longtitude = in.readDouble();
        this.lattitude = in.readDouble();
        this.distance = in.readDouble();
        this.avaId = in.readString();
        this.thumbnailUrl = in.readString();
        this.gender = in.readInt();
        this.messageType = in.readString();
        this.anonymous = in.readByte() != 0;
        this.isVoiceCallWaiting = in.readByte() != 0;
        this.isVideoCallWaiting = in.readByte() != 0;
    }

    public static final Parcelable.Creator<ConversationItem> CREATOR = new Parcelable.Creator<ConversationItem>() {
        @Override
        public ConversationItem createFromParcel(Parcel source) {
            return new ConversationItem(source);
        }

        @Override
        public ConversationItem[] newArray(int size) {
            return new ConversationItem[size];
        }
    };
}
