package com.vn.ntsc.repository.model.block.blocklst;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.vn.ntsc.core.model.BaseBean;

/**
 * Created by ThoNh on 9/22/2017.
 */

public class BlockLstItem extends BaseBean {

    @SerializedName("user_name")
    public String userName;

    @SerializedName("region")
    public int regions;

    @SerializedName("abt")
    public String about;

    @SerializedName("last_login_time")
    public long lastLoginTime;

    @SerializedName("age")
    public int age;

    @SerializedName("job")
    public int job;

    @SerializedName("gender")
    public int gender;

    @SerializedName("user_id")
    public String userId;

    @SerializedName("ava")
    public String avatarUrl;

    @SerializedName("ava_id")
    public String avatarId;

    @SerializedName("is_fav")
    public int isFav;

    /**
     * Server trả về thì đương nhiên user này đang bị block
     */
    public boolean isBlocked = true;

    public BlockLstItem() {
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.userName);
        dest.writeInt(this.regions);
        dest.writeString(this.about);
        dest.writeLong(this.lastLoginTime);
        dest.writeInt(this.age);
        dest.writeInt(this.job);
        dest.writeInt(this.gender);
        dest.writeString(this.userId);
        dest.writeString(this.avatarUrl);
        dest.writeString(this.avatarId);
        dest.writeInt(this.isFav);
        dest.writeByte(this.isBlocked ? (byte) 1 : (byte) 0);
    }

    protected BlockLstItem(Parcel in) {
        this.userName = in.readString();
        this.regions = in.readInt();
        this.about = in.readString();
        this.lastLoginTime = in.readLong();
        this.age = in.readInt();
        this.job = in.readInt();
        this.gender = in.readInt();
        this.userId = in.readString();
        this.avatarUrl = in.readString();
        this.avatarId = in.readString();
        this.isFav = in.readInt();
        this.isBlocked = in.readByte() != 0;
    }

    public static final Parcelable.Creator<BlockLstItem> CREATOR = new Parcelable.Creator<BlockLstItem>() {
        @Override
        public BlockLstItem createFromParcel(Parcel source) {
            return new BlockLstItem(source);
        }

        @Override
        public BlockLstItem[] newArray(int size) {
            return new BlockLstItem[size];
        }
    };
}
