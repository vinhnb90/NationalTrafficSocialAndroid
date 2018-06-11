package com.vn.ntsc.repository.model.timeline.datas;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.vn.ntsc.core.model.BaseBean;
import com.vn.ntsc.repository.model.timeline.BuzzDetailRequest;
import com.vn.ntsc.repository.model.timeline.datas.sub.LikeBean;
import com.vn.ntsc.repository.model.timeline.datas.sub.ListBuzzChild;
import com.vn.ntsc.repository.model.timeline.datas.sub.ListCommentBean;
import com.vn.ntsc.repository.model.timeline.datas.sub.ListTagFriendsBean;
import com.vn.ntsc.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nankai on 12/13/2017.
 */

public class BuzzBean extends BaseBean {
    /**
     * User Id
     */
    @SerializedName("user_id")
    public String userId;
    /**
     * User Name
     */
    @SerializedName("user_name")
    public String userName;
    /**
     * Avatar Id
     */
    @SerializedName("ava")
    public String avatar;
    /**
     * Gender
     */
    @SerializedName("gender")
    public int gender;
    /**
     * privacy
     */
    @SerializedName("privacy")
    public int privacy;
    /**
     * Longitude
     */
    @SerializedName("long")
    public double longitude;
    /**
     * Latitude
     */
    @SerializedName("lat")
    public double latitude;
    /**
     * Distance
     */
    @SerializedName("dist")
    public double distance;

    /**
     * Is favorite
     */
    @SerializedName("is_fav")
    public int isFavorite;
    /**
     * Buzz Time
     */
    @SerializedName("buzz_time")
    public String buzzTime;

    /**
     * Like Number
     */
    @SerializedName("like")
    public LikeBean like;

    /**
     * Buzz Id
     */
    @SerializedName("buzz_id")
    public String buzzId;

    /**
     * Select To Delete flag
     */
    @SerializedName("select_to_delete")
    public boolean selectToDelete;

    @SerializedName("is_app")
    public int isApproved;

    @SerializedName("is_online")
    public boolean isOnline;

    @SerializedName("region")
    public int region;

    @SerializedName("comment_buzz_point")
    public int commentBuzzPoint;

    @SerializedName("buzz_val")
    public String buzzValue;


    @SerializedName("child_num")
    public int childNumber;

    @SerializedName("tag_num")
    public int tagNumber;

    @SerializedName("cmt_num")
    public long commentNumber;

    @SerializedName("sub_cmt_num")
    public long subCommentNumber;

    @SerializedName("share_number")
    public long shareNumber;

    @SerializedName("list_child")
    public List<ListBuzzChild> listChildBuzzes;

    @SerializedName("ic_comment")
    public List<ListCommentBean> commentsList;

    @SerializedName("tag_list")
    public ArrayList<ListTagFriendsBean> tagList;

    @SerializedName("share_detail")
    public ShareDetailBean shareDetailBean;

    @SerializedName("share_id")
    public String shareId;

    @SerializedName("is_share")
    public int isShare;

    //------------- Template
    public boolean isTemplate = false;

    public boolean isError;

    public BuzzDetailRequest buzzDetailRequest;
    //------------- End Template

    private long totalCommentNumber;

    public long getTotalCommentNumber() {
        return this.totalCommentNumber = commentNumber + subCommentNumber;
    }

    public void setDataBean(BuzzBean buzzBean) {
        if (buzzBean == null) {
            LogUtils.w("BuzzBean", "BuzzBean is null!");
            return;
        }
        this.userId = buzzBean.userId;
        this.userName = buzzBean.userName;
        this.avatar = buzzBean.avatar;
        this.gender = buzzBean.gender;
        this.privacy = buzzBean.privacy;
        this.longitude = buzzBean.longitude;
        this.latitude = buzzBean.latitude;
        this.distance = buzzBean.distance;
        this.isFavorite = buzzBean.isFavorite;
        this.buzzTime = buzzBean.buzzTime;
        this.like = buzzBean.like;
        this.buzzId = buzzBean.buzzId;
        this.selectToDelete = buzzBean.selectToDelete;
        this.isApproved = buzzBean.isApproved;
        this.isOnline = buzzBean.isOnline;
        this.region = buzzBean.region;
        this.commentBuzzPoint = buzzBean.commentBuzzPoint;
        this.buzzValue = buzzBean.buzzValue;
        this.childNumber = buzzBean.childNumber;
        this.tagNumber = buzzBean.tagNumber;
        this.commentNumber = buzzBean.commentNumber;
        this.subCommentNumber = buzzBean.subCommentNumber;
        this.shareNumber = buzzBean.shareNumber;
        this.listChildBuzzes = buzzBean.listChildBuzzes;
        this.commentsList = buzzBean.commentsList;
        this.tagList = buzzBean.tagList;
        this.isTemplate = buzzBean.isTemplate;
        this.getTotalCommentNumber();
    }

    public boolean isBuzzShare() {
        if (shareDetailBean != null && shareId != null && !shareId.equals(""))
            return true;
        return false;
    }

    public boolean isTemplate() {
        return isTemplate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BuzzBean)) return false;

        BuzzBean buzzBean = (BuzzBean) o;

        return userId.equals(buzzBean.userId) && buzzId.equals(buzzBean.buzzId);
    }

    @Override
    public int hashCode() {
        int result = userId.hashCode();
        result = 31 * result + buzzId.hashCode();
        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.userId);
        dest.writeString(this.userName);
        dest.writeString(this.avatar);
        dest.writeInt(this.gender);
        dest.writeInt(this.privacy);
        dest.writeDouble(this.longitude);
        dest.writeDouble(this.latitude);
        dest.writeDouble(this.distance);
        dest.writeInt(this.isFavorite);
        dest.writeString(this.buzzTime);
        dest.writeParcelable(this.like, flags);
        dest.writeString(this.buzzId);
        dest.writeByte(this.selectToDelete ? (byte) 1 : (byte) 0);
        dest.writeInt(this.isApproved);
        dest.writeByte(this.isOnline ? (byte) 1 : (byte) 0);
        dest.writeInt(this.region);
        dest.writeInt(this.commentBuzzPoint);
        dest.writeString(this.buzzValue);
        dest.writeInt(this.childNumber);
        dest.writeInt(this.tagNumber);
        dest.writeLong(this.commentNumber);
        dest.writeLong(this.subCommentNumber);
        dest.writeLong(this.shareNumber);
        dest.writeTypedList(this.listChildBuzzes);
        dest.writeTypedList(this.commentsList);
        dest.writeTypedList(this.tagList);
        dest.writeParcelable(this.shareDetailBean, flags);
        dest.writeString(this.shareId);
        dest.writeInt(this.isShare);
        dest.writeByte(this.isTemplate ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isError ? (byte) 1 : (byte) 0);
        dest.writeLong(this.totalCommentNumber);
    }

    public BuzzBean() {
    }

    protected BuzzBean(Parcel in) {
        this.userId = in.readString();
        this.userName = in.readString();
        this.avatar = in.readString();
        this.gender = in.readInt();
        this.privacy = in.readInt();
        this.longitude = in.readDouble();
        this.latitude = in.readDouble();
        this.distance = in.readDouble();
        this.isFavorite = in.readInt();
        this.buzzTime = in.readString();
        this.like = in.readParcelable(LikeBean.class.getClassLoader());
        this.buzzId = in.readString();
        this.selectToDelete = in.readByte() != 0;
        this.isApproved = in.readInt();
        this.isOnline = in.readByte() != 0;
        this.region = in.readInt();
        this.commentBuzzPoint = in.readInt();
        this.buzzValue = in.readString();
        this.childNumber = in.readInt();
        this.tagNumber = in.readInt();
        this.commentNumber = in.readLong();
        this.subCommentNumber = in.readLong();
        this.shareNumber = in.readLong();
        this.listChildBuzzes = in.createTypedArrayList(ListBuzzChild.CREATOR);
        this.commentsList = in.createTypedArrayList(ListCommentBean.CREATOR);
        this.tagList = in.createTypedArrayList(ListTagFriendsBean.CREATOR);
        this.shareDetailBean = in.readParcelable(ShareDetailBean.class.getClassLoader());
        this.shareId = in.readString();
        this.isShare = in.readInt();
        this.isTemplate = in.readByte() != 0;
        this.isError = in.readByte() != 0;
        this.totalCommentNumber = in.readLong();
    }

    public static final Parcelable.Creator<BuzzBean> CREATOR = new Parcelable.Creator<BuzzBean>() {
        @Override
        public BuzzBean createFromParcel(Parcel source) {
            return new BuzzBean(source);
        }

        @Override
        public BuzzBean[] newArray(int size) {
            return new BuzzBean[size];
        }
    };
}
