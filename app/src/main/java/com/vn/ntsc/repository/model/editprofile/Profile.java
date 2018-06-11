package com.vn.ntsc.repository.model.editprofile;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.vn.ntsc.ui.profile.detail.ProfileDetailActivity;

/**
 * Created by dev22 on 9/7/17.
 * pojo class for fill data on {@link ProfileDetailActivity}
 */
public class Profile implements Parcelable {
    public String name;
    public String avatar;
    public int age;
    public int gender;
    public int job;
    public int region;
    public int bodyType;
    public String hobby;
    @SerializedName("abt")
    public String message;

    public Profile(String name, String avatar, int age, int gender, int job, int region, int bodyType, String hobby, String message) {
        this.name = name;
        this.avatar = avatar;
        this.age = age;
        this.gender = gender;
        this.job = job;
        this.region = region;
        this.bodyType = bodyType;
        this.hobby = hobby;
        this.message = message;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.avatar);
        dest.writeInt(this.age);
        dest.writeInt(this.gender);
        dest.writeInt(this.job);
        dest.writeInt(this.region);
        dest.writeInt(this.bodyType);
        dest.writeString(this.hobby);
        dest.writeString(this.message);
    }

    protected Profile(Parcel in) {
        this.name = in.readString();
        this.avatar = in.readString();
        this.age = in.readInt();
        this.gender = in.readInt();
        this.job = in.readInt();
        this.region = in.readInt();
        this.bodyType = in.readInt();
        this.hobby = in.readString();
        this.message = in.readString();
    }

    public static final Creator<Profile> CREATOR = new Creator<Profile>() {
        @Override
        public Profile createFromParcel(Parcel source) {
            return new Profile(source);
        }

        @Override
        public Profile[] newArray(int size) {
            return new Profile[size];
        }
    };
}
