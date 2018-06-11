package com.vn.ntsc.repository.model.timeline.datas.sub;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.vn.ntsc.core.model.BaseBean;

/**
 * Created by nankai on 8/8/2017.
 */

public class BuzzSubCommentBean extends BaseBean {

    /**
     * Sub ic_comment Id
     */
    @SerializedName("sub_comment_id")
    public String subCommentId;

    /**
     * Id of user who sent sub ic_comment
     */
    @SerializedName("user_id")
    public String userId;
    /**
     * Content of sub ic_comment
     */
    @SerializedName("value")
    public String value;
    /**
     * The time when user sent sub ic_comment
     */
    @SerializedName("time")
    public String time;

    /**
     * Gender: 0 female, 1 male
     */
    @SerializedName("gender")
    public int gender;

    @SerializedName("is_online")
    public boolean isOnline;

    @SerializedName("ava")
    public String avatar;

    /**
     * User Name
     */
    @SerializedName("user_name")
    public String userName;

    @SerializedName("is_app")
    public int isApprove;

    @SerializedName("msg_type")
    public String msgType;

    @SerializedName("cmt_id")
    public String cmtId;

    @SerializedName("server_time")
    public String serverTime;

    @SerializedName("from")
    public String from;

    @SerializedName("buzz_id")
    public String buzzId;

    /**
     * Number of ic_comment
     */
    @SerializedName("comment_number")
    public int commentNumber;

    /**
     * all sub ic_comment number of the buzz
     */
    @SerializedName("all_sub_comment_number")
    public int allSubCommentNumber;

    public boolean isViewMore = false;
    public String textViewMore;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.subCommentId);
        dest.writeString(this.userId);
        dest.writeString(this.value);
        dest.writeString(this.time);
        dest.writeInt(this.gender);
        dest.writeByte(this.isOnline ? (byte) 1 : (byte) 0);
        dest.writeString(this.avatar);
        dest.writeString(this.userName);
        dest.writeInt(this.isApprove);
        dest.writeString(this.msgType);
        dest.writeString(this.cmtId);
        dest.writeString(this.serverTime);
        dest.writeString(this.from);
        dest.writeString(this.buzzId);
        dest.writeInt(this.commentNumber);
        dest.writeInt(this.allSubCommentNumber);
        dest.writeByte(this.isViewMore ? (byte) 1 : (byte) 0);
        dest.writeString(this.textViewMore);
    }

    public BuzzSubCommentBean() {
    }

    protected BuzzSubCommentBean(Parcel in) {
        this.subCommentId = in.readString();
        this.userId = in.readString();
        this.value = in.readString();
        this.time = in.readString();
        this.gender = in.readInt();
        this.isOnline = in.readByte() != 0;
        this.avatar = in.readString();
        this.userName = in.readString();
        this.isApprove = in.readInt();
        this.msgType = in.readString();
        this.cmtId = in.readString();
        this.serverTime = in.readString();
        this.from = in.readString();
        this.buzzId = in.readString();
        this.commentNumber = in.readInt();
        this.allSubCommentNumber = in.readInt();
        this.isViewMore = in.readByte() != 0;
        this.textViewMore = in.readString();
    }

    public static final Parcelable.Creator<BuzzSubCommentBean> CREATOR = new Parcelable.Creator<BuzzSubCommentBean>() {
        @Override
        public BuzzSubCommentBean createFromParcel(Parcel source) {
            return new BuzzSubCommentBean(source);
        }

        @Override
        public BuzzSubCommentBean[] newArray(int size) {
            return new BuzzSubCommentBean[size];
        }
    };
}
