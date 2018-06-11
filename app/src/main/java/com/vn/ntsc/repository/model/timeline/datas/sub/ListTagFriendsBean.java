package com.vn.ntsc.repository.model.timeline.datas.sub;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.vn.ntsc.core.model.BaseBean;

/**
 * Created by nankai on 10/16/2017.
 */

public class ListTagFriendsBean extends BaseBean implements Cloneable {

    @SerializedName("user_id")
    public String userId;
    @SerializedName("user_name")
    public String userName;
    @SerializedName("ava")
    public String avatar;
    @SerializedName("age")
    public int age;
    @SerializedName("gender")
    public int gender;
    @SerializedName("region")
    public int region;

    @SerializedName("tag_flag")
    public int tagFlag;

    public boolean isTagged = false;//is tagged
    public boolean matchedFilterResult = true;//Matched with filtered results

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ListTagFriendsBean that = (ListTagFriendsBean) o;

        return userId != null ? userId.equals(that.userId) : that.userId == null;
    }

    @Override
    public int hashCode() {
        return userId != null ? userId.hashCode() : 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.userId == null ? "" : this.userId);
        dest.writeString(this.userName == null ? "" : this.userName);
        dest.writeString(this.avatar == null ? "" : this.avatar);
        dest.writeInt(this.age);
        dest.writeInt(this.gender);
        dest.writeInt(this.region);
        dest.writeInt(this.tagFlag);
        dest.writeByte(this.isTagged ? (byte) 1 : (byte) 0);
        dest.writeByte(this.matchedFilterResult ? (byte) 1 : (byte) 0);
    }

    public ListTagFriendsBean() {
    }

    protected ListTagFriendsBean(Parcel in) {
        this.userId = in.readString();
        this.userName = in.readString();
        this.avatar = in.readString();
        this.age = in.readInt();
        this.gender = in.readInt();
        this.region = in.readInt();
        this.tagFlag = in.readInt();
        this.isTagged = in.readByte() != 0;
        this.matchedFilterResult = in.readByte() != 0;
    }

    public static final Parcelable.Creator<ListTagFriendsBean> CREATOR = new Parcelable.Creator<ListTagFriendsBean>() {
        @Override
        public ListTagFriendsBean createFromParcel(Parcel source) {
            return new ListTagFriendsBean(source);
        }

        @Override
        public ListTagFriendsBean[] newArray(int size) {
            return new ListTagFriendsBean[size];
        }
    };

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
