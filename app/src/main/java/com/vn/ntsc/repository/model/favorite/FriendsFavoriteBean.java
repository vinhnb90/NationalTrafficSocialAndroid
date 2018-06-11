package com.vn.ntsc.repository.model.favorite;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.vn.ntsc.core.model.BaseBean;

/**
 * Created by hnc on 10/08/2017.
 */

public class FriendsFavoriteBean extends BaseBean {

    @SerializedName("user_id")
    public String userId;

    @SerializedName("user_name")
    public String userName;

    @SerializedName("gender")
    public int gender;

    @SerializedName("age")
    public int age;

    @SerializedName("thumbnail_url")
    public String thumbnailUrl;

    @SerializedName("original_url")
    public String originalUrl;

    @SerializedName("is_frd")
    public int isFriend;

    @SerializedName("is_online")
    public boolean isOnline;

    @SerializedName("long")
    public double lng;

    @SerializedName("lat")
    public double lat;

    @SerializedName("dist")
    public double distance;

    @SerializedName("chk_time")
    public long checkTime;

    @SerializedName("status")
    public String status;

    @SerializedName("unread_num")
    public int unreadNumber;

    @SerializedName("voice_call_waiting")
    public boolean isVoiceCallWaitting;

    @SerializedName("video_call_waiting")
    public boolean isVideoCallWaitting;

    @SerializedName("last_login")
    public String lastLogin;

    @SerializedName("last_login_time")
    public String checkOutTime;

    @SerializedName("is_fav")
    public int isFav;

    @SerializedName("region")
    public int region;

    @SerializedName("job")
    public int job;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.userId);
        dest.writeString(this.userName);
        dest.writeInt(this.gender);
        dest.writeInt(this.age);
        dest.writeString(this.thumbnailUrl);
        dest.writeString(this.originalUrl);
        dest.writeInt(this.isFriend);
        dest.writeByte(this.isOnline ? (byte) 1 : (byte) 0);
        dest.writeDouble(this.lng);
        dest.writeDouble(this.lat);
        dest.writeDouble(this.distance);
        dest.writeLong(this.checkTime);
        dest.writeString(this.status);
        dest.writeInt(this.unreadNumber);
        dest.writeByte(this.isVoiceCallWaitting ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isVideoCallWaitting ? (byte) 1 : (byte) 0);
        dest.writeString(this.lastLogin);
        dest.writeString(this.checkOutTime);
        dest.writeInt(this.isFav);
        dest.writeInt(this.region);
        dest.writeInt(this.job);
    }

    public FriendsFavoriteBean() {
    }

    protected FriendsFavoriteBean(Parcel in) {
        this.userId = in.readString();
        this.userName = in.readString();
        this.gender = in.readInt();
        this.age = in.readInt();
        this.thumbnailUrl = in.readString();
        this.originalUrl = in.readString();
        this.isFriend = in.readInt();
        this.isOnline = in.readByte() != 0;
        this.lng = in.readDouble();
        this.lat = in.readDouble();
        this.distance = in.readDouble();
        this.checkTime = in.readLong();
        this.status = in.readString();
        this.unreadNumber = in.readInt();
        this.isVoiceCallWaitting = in.readByte() != 0;
        this.isVideoCallWaitting = in.readByte() != 0;
        this.lastLogin = in.readString();
        this.checkOutTime = in.readString();
        this.isFav = in.readInt();
        this.region = in.readInt();
        this.job = in.readInt();
    }

    public static final Parcelable.Creator<FriendsFavoriteBean> CREATOR = new Parcelable.Creator<FriendsFavoriteBean>() {
        @Override
        public FriendsFavoriteBean createFromParcel(Parcel source) {
            return new FriendsFavoriteBean(source);
        }

        @Override
        public FriendsFavoriteBean[] newArray(int size) {
            return new FriendsFavoriteBean[size];
        }
    };
}
