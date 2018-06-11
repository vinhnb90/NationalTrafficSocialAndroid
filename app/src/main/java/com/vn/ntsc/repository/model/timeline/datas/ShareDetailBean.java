package com.vn.ntsc.repository.model.timeline.datas;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.vn.ntsc.core.model.BaseBean;
import com.vn.ntsc.repository.model.timeline.datas.sub.ListBuzzChild;
import com.vn.ntsc.repository.model.timeline.datas.sub.ListTagFriendsBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nankai on 1/5/2018.
 */

public class ShareDetailBean extends BaseBean {
    
    @SerializedName("region")
    public int region;
    @SerializedName("ava")
    public String avatar;
    @SerializedName("child_num")
    public int childNum;
    @SerializedName("buzz_id")
    public String buzzId;
    @SerializedName("user_name")
    public String userName;
    @SerializedName("buzz_time")
    public String buzzTime;
    @SerializedName("flag")
    public int flag;
    @SerializedName("user_id")
    public String userId;
    @SerializedName("buzz_val")
    public String buzzValue;
    @SerializedName("tag_list")
    public ArrayList<ListTagFriendsBean> tagList;
    @SerializedName("tag_num")
    public int tagNumber;
    @SerializedName("list_child")
    public List<ListBuzzChild> listChildBuzzes;
    @SerializedName("gender")
    public int gender;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.region);
        dest.writeString(this.avatar);
        dest.writeInt(this.childNum);
        dest.writeString(this.buzzId);
        dest.writeString(this.userName);
        dest.writeString(this.buzzTime);
        dest.writeInt(this.flag);
        dest.writeString(this.userId);
        dest.writeString(this.buzzValue);
        dest.writeTypedList(this.tagList);
        dest.writeInt(this.tagNumber);
        dest.writeInt(this.gender);
        dest.writeTypedList(this.listChildBuzzes);
    }

    public ShareDetailBean() {
    }

    protected ShareDetailBean(Parcel in) {
        this.region = in.readInt();
        this.avatar = in.readString();
        this.childNum = in.readInt();
        this.buzzId = in.readString();
        this.userName = in.readString();
        this.buzzTime = in.readString();
        this.flag = in.readInt();
        this.userId = in.readString();
        this.buzzValue = in.readString();
        this.tagList = in.createTypedArrayList(ListTagFriendsBean.CREATOR);
        this.tagNumber = in.readInt();
        this.gender = in.readInt();
        this.listChildBuzzes = in.createTypedArrayList(ListBuzzChild.CREATOR);
    }

    public static final Parcelable.Creator<ShareDetailBean> CREATOR = new Parcelable.Creator<ShareDetailBean>() {
        @Override
        public ShareDetailBean createFromParcel(Parcel source) {
            return new ShareDetailBean(source);
        }

        @Override
        public ShareDetailBean[] newArray(int size) {
            return new ShareDetailBean[size];
        }
    };
}
