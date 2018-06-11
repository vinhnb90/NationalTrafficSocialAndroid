package com.vn.ntsc.repository.model.timeline.datas.sub;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.vn.ntsc.core.model.BaseBean;

/**
 * Created by nankai on 8/10/2017.
 */

public class BuzzListSubCommentBean extends BaseBean {

    private static final long serialVersionUID = -3570060107760458182L;

    /**
     * Sub ic_comment Id
     */
    @SerializedName("sub_comment_id")
    public String subCommentID;

    /**
     * Flag used to check whether or not this ic_comment can be deleted
     */
    @SerializedName("can_delete")
    public boolean canDelete;

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
     * Avatar Id
     */
    @SerializedName("ava_id")
    public String avatarId;

    /**
     * Gender: 0 female, 1 male
     */
    @SerializedName("gender")
    public int gender;

    @SerializedName("is_online")
    public boolean isOnline;

    /**
     * User Name
     */
    @SerializedName("user_name")
    public String userName;

    @SerializedName("is_app")
    public int isApprove;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.subCommentID);
        dest.writeByte(this.canDelete ? (byte) 1 : (byte) 0);
        dest.writeString(this.userId);
        dest.writeString(this.value);
        dest.writeString(this.time);
        dest.writeString(this.avatarId);
        dest.writeInt(this.gender);
        dest.writeByte(this.isOnline ? (byte) 1 : (byte) 0);
        dest.writeString(this.userName);
        dest.writeInt(this.isApprove);
    }

    public BuzzListSubCommentBean() {
    }

    protected BuzzListSubCommentBean(Parcel in) {
        this.subCommentID = in.readString();
        this.canDelete = in.readByte() != 0;
        this.userId = in.readString();
        this.value = in.readString();
        this.time = in.readString();
        this.avatarId = in.readString();
        this.gender = in.readInt();
        this.isOnline = in.readByte() != 0;
        this.userName = in.readString();
        this.isApprove = in.readInt();
    }

    public static final Parcelable.Creator<BuzzListSubCommentBean> CREATOR = new Parcelable.Creator<BuzzListSubCommentBean>() {
        @Override
        public BuzzListSubCommentBean createFromParcel(Parcel source) {
            return new BuzzListSubCommentBean(source);
        }

        @Override
        public BuzzListSubCommentBean[] newArray(int size) {
            return new BuzzListSubCommentBean[size];
        }
    };
}
