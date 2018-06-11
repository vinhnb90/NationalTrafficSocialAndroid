package com.vn.ntsc.repository.model.timeline.datas.sub;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.vn.ntsc.core.model.BaseBean;

import java.util.ArrayList;

/**
 * Created by nankai on 8/10/2017.
 */

public class BuzzListCommentItemBean extends BaseBean {

    private static final long serialVersionUID = 6785615448751034410L;
    /**
     * Number of sub ic_comment
     */
    @SerializedName("sub_comment_number")
    public int subCommentNumber;

    @SerializedName("sub_comment_point")
    public int subCommentPoint;

    /**
     * Flag used to check whether or not this ic_comment can be deleted
     */
    @SerializedName("can_delete")
    public int canDelete;

    /**
     * Comment Id
     */
    @SerializedName("cmt_id")
    public String commentId;

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
    @SerializedName("ava_id")
    public String avatarId;

    /**
     * Gender: 0 female, 1 male
     */
    @SerializedName("gender")
    public int gender;

    /**
     * Comment value
     */
    @SerializedName("cmt_val")
    public String commentValue;

    /**
     * Comment time: yyyyMMddHHmmss
     */
    @SerializedName("cmt_time")
    public String commentTime;

    @SerializedName("is_online")
    public boolean isOnline;

    @SerializedName("is_app")
    public int isApproved;

    @SerializedName("sub_comment")
    public ArrayList<BuzzListSubCommentBean> listSubComments;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.subCommentNumber);
        dest.writeInt(this.subCommentPoint);
        dest.writeInt(this.canDelete);
        dest.writeString(this.commentId);
        dest.writeString(this.userId);
        dest.writeString(this.userName);
        dest.writeString(this.avatarId);
        dest.writeInt(this.gender);
        dest.writeString(this.commentValue);
        dest.writeString(this.commentTime);
        dest.writeByte(this.isOnline ? (byte) 1 : (byte) 0);
        dest.writeInt(this.isApproved);
        dest.writeTypedList(this.listSubComments);
    }

    public BuzzListCommentItemBean() {
    }

    protected BuzzListCommentItemBean(Parcel in) {
        this.subCommentNumber = in.readInt();
        this.subCommentPoint = in.readInt();
        this.canDelete = in.readInt();
        this.commentId = in.readString();
        this.userId = in.readString();
        this.userName = in.readString();
        this.avatarId = in.readString();
        this.gender = in.readInt();
        this.commentValue = in.readString();
        this.commentTime = in.readString();
        this.isOnline = in.readByte() != 0;
        this.isApproved = in.readInt();
        this.listSubComments = in.createTypedArrayList(BuzzListSubCommentBean.CREATOR);
    }

    public static final Parcelable.Creator<BuzzListCommentItemBean> CREATOR = new Parcelable.Creator<BuzzListCommentItemBean>() {
        @Override
        public BuzzListCommentItemBean createFromParcel(Parcel source) {
            return new BuzzListCommentItemBean(source);
        }

        @Override
        public BuzzListCommentItemBean[] newArray(int size) {
            return new BuzzListCommentItemBean[size];
        }
    };
}
