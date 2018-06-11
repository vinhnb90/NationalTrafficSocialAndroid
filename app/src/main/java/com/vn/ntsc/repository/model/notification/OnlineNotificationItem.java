package com.vn.ntsc.repository.model.notification;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.vn.ntsc.core.model.BaseBean;

/**
 * Created by ThoNh on 9/1/2017.
 */

public class OnlineNotificationItem extends BaseBean {

    @SerializedName("region")
    public int region;
    @SerializedName("user_name")
    public String userName;
    @SerializedName("abt")
    public String abt;
    @SerializedName("is_purchase")
    public int isPurchase;
    @SerializedName("last_login_time")
    public String lastLoginTime;
    @SerializedName("age")
    public int age;
    @SerializedName("ava_id")
    public String avaId;
    @SerializedName("job")
    public int job;
    @SerializedName("gender")
    public int gender;
    @SerializedName("user_id")
    public String userId;
    @SerializedName("is_fav")
    public int isFav;
    @SerializedName("ava")
    public String avaUrl;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.region);
        dest.writeString(this.userName);
        dest.writeString(this.abt);
        dest.writeInt(this.isPurchase);
        dest.writeString(this.lastLoginTime);
        dest.writeInt(this.age);
        dest.writeString(this.avaId);
        dest.writeInt(this.job);
        dest.writeInt(this.gender);
        dest.writeString(this.userId);
        dest.writeInt(this.isFav);
        dest.writeString(this.avaUrl);
    }

    public OnlineNotificationItem() {
    }

    protected OnlineNotificationItem(Parcel in) {
        this.region = in.readInt();
        this.userName = in.readString();
        this.abt = in.readString();
        this.isPurchase = in.readInt();
        this.lastLoginTime = in.readString();
        this.age = in.readInt();
        this.avaId = in.readString();
        this.job = in.readInt();
        this.gender = in.readInt();
        this.userId = in.readString();
        this.isFav = in.readInt();
        this.avaUrl = in.readString();
    }

    public static final Parcelable.Creator<OnlineNotificationItem> CREATOR = new Parcelable.Creator<OnlineNotificationItem>() {
        @Override
        public OnlineNotificationItem createFromParcel(Parcel source) {
            return new OnlineNotificationItem(source);
        }

        @Override
        public OnlineNotificationItem[] newArray(int size) {
            return new OnlineNotificationItem[size];
        }
    };
}
