package com.vn.ntsc.repository.model.user;

import android.os.Parcel;
import android.os.Parcelable;

import com.vn.ntsc.utils.UserSetting;

import java.util.Date;

/**
 * Created by Administrator on 04/21/2017.
 */

public class User implements Parcelable {
    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
    public String name;
    public String email;
    public String password;
    public Date birthday;
    public int gender = UserSetting.GENDER_MALE;
    public int interested = UserSetting.INTERESTED_IN_WOMEN;
    public String avatar;
    public String anotherSystemId;
    public boolean isReceiveEmailNotification = false;

    public User(String name, String email, String password, Date birthday,
                int gender, int interested) {
        super();
        this.name = name;
        this.email = email;
        this.password = password;
        this.birthday = birthday;
        this.gender = gender;
        this.interested = interested;
    }

    public User(String name, Date birthday, int gender, int interested,
                String avatar, String anotherSystemId) {
        super();
        this.name = name;
        this.birthday = birthday;
        this.gender = gender;
        this.interested = interested;
        this.avatar = avatar;
        this.anotherSystemId = anotherSystemId;
    }

    public User(String name, String email, Date birthday, int gender,
                int interested, String avatar, String anotherSystemId) {
        super();
        this.name = name;
        this.email = email;
        this.birthday = birthday;
        this.gender = gender;
        this.interested = interested;
        this.avatar = avatar;
        this.anotherSystemId = anotherSystemId;
    }

    public User() {
    }

    protected User(Parcel in) {
        this.name = in.readString();
        this.email = in.readString();
        this.password = in.readString();
        long tmpBirthday = in.readLong();
        this.birthday = tmpBirthday == -1 ? null : new Date(tmpBirthday);
        this.gender = in.readInt();
        this.interested = in.readInt();
        this.avatar = in.readString();
        this.anotherSystemId = in.readString();
        this.isReceiveEmailNotification = in.readByte() != 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.email);
        dest.writeString(this.password);
        dest.writeLong(this.birthday != null ? this.birthday.getTime() : -1);
        dest.writeInt(this.gender);
        dest.writeInt(this.interested);
        dest.writeString(this.avatar);
        dest.writeString(this.anotherSystemId);
        dest.writeByte(this.isReceiveEmailNotification ? (byte) 1 : (byte) 0);
    }
}
