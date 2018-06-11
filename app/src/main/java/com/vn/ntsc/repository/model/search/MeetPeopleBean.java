package com.vn.ntsc.repository.model.search;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.vn.ntsc.core.model.ServerResponse;

/**
 * Created by hnc on 22/08/2017.
 */

public class MeetPeopleBean extends ServerResponse {
    @SerializedName("user_id")
    public String userId;
    @SerializedName("user_name")
    public String userName;
    @SerializedName("email")
    public String email;
    @SerializedName("is_online")
    public boolean isOnline;
    @SerializedName("long")
    public double lng;
    @SerializedName("lat")
    public double lat;
    @SerializedName("dist")
    public double distance;
    @SerializedName("ava_id")
    public String avaId;
    @SerializedName("thumbnail_url")
    public String thumbnailUrl;
    @SerializedName("age")
    public int age;
    @SerializedName("gender")
    public int gender;
    @SerializedName("status")
    public String status;
    @SerializedName("unread_num")
    public int unreadNum;
    @SerializedName("region")
    public int region;
    @SerializedName("is_fav")
    public int isFav;
    @SerializedName("is_contacted")
    public boolean isContacted;
    @SerializedName("abt")
    public String abt;
    @SerializedName("voice_call_waiting")
    public boolean voiceCallWaiting;
    @SerializedName("video_call_waiting")
    public boolean videoCallWaiting;
    @SerializedName("last_login")
    public String isLastLogin;


    public MeetPeopleBean() {
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.userId);
        dest.writeString(this.userName);
        dest.writeString(this.email);
        dest.writeByte(this.isOnline ? (byte) 1 : (byte) 0);
        dest.writeDouble(this.lng);
        dest.writeDouble(this.lat);
        dest.writeDouble(this.distance);
        dest.writeString(this.avaId);
        dest.writeString(this.thumbnailUrl);
        dest.writeInt(this.age);
        dest.writeInt(this.gender);
        dest.writeString(this.status);
        dest.writeInt(this.unreadNum);
        dest.writeInt(this.region);
        dest.writeInt(this.isFav);
        dest.writeByte(this.isContacted ? (byte) 1 : (byte) 0);
        dest.writeString(this.abt);
        dest.writeByte(this.voiceCallWaiting ? (byte) 1 : (byte) 0);
        dest.writeByte(this.videoCallWaiting ? (byte) 1 : (byte) 0);
        dest.writeString(this.isLastLogin);
    }

    protected MeetPeopleBean(Parcel in) {
        super(in);
        this.userId = in.readString();
        this.userName = in.readString();
        this.email = in.readString();
        this.isOnline = in.readByte() != 0;
        this.lng = in.readDouble();
        this.lat = in.readDouble();
        this.distance = in.readDouble();
        this.avaId = in.readString();
        this.thumbnailUrl = in.readString();
        this.age = in.readInt();
        this.gender = in.readInt();
        this.status = in.readString();
        this.unreadNum = in.readInt();
        this.region = in.readInt();
        this.isFav = in.readInt();
        this.isContacted = in.readByte() != 0;
        this.abt = in.readString();
        this.voiceCallWaiting = in.readByte() != 0;
        this.videoCallWaiting = in.readByte() != 0;
        this.isLastLogin = in.readString();
    }

    public static final Parcelable.Creator<MeetPeopleBean> CREATOR = new Parcelable.Creator<MeetPeopleBean>() {
        @Override
        public MeetPeopleBean createFromParcel(Parcel source) {
            return new MeetPeopleBean(source);
        }

        @Override
        public MeetPeopleBean[] newArray(int size) {
            return new MeetPeopleBean[size];
        }
    };
}
