package com.vn.ntsc.repository.model.timeline.datas.sub;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.vn.ntsc.core.model.BaseBean;

import java.util.List;

/**
 * Created by nankai on 8/8/2017.
 */

public class ListCommentBean extends BaseBean {

    @SerializedName("region")
    public int region;

    /**
     * Number of sub ic_comment
     */
    @SerializedName("sub_comment_number")
    public int subCommentNumber;

    /**
     * all number of ic_comment off buzz
     */
    @SerializedName("comment_number")
    public int commentNumber;

    /**
     * all sub ic_comment number of the buzz
     */
    @SerializedName("all_sub_comment_number")
    public int allSubCommentNumber;

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
    public String cmtId;
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

    @SerializedName("ava")
    public String avatar;
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
    public List<BuzzSubCommentBean> subComments;

    @SerializedName("msg_type")
    public String msgType;

    @SerializedName("server_time")
    public String serverTime;

    @SerializedName("from")
    public String from;

    @SerializedName("buzz_id")
    public String buzzId;

    public boolean isViewMore = false;
    public String textViewMore;

    public void updateData(ListCommentBean bean) {
        this.region = bean.region;
        this.subCommentNumber = bean.subCommentNumber;
        this.commentNumber = bean.commentNumber;
        this.allSubCommentNumber = bean.allSubCommentNumber;
        this.subCommentPoint = bean.subCommentPoint;
        this.canDelete = bean.canDelete;
        this.cmtId = bean.cmtId;
        this.userId = bean.userId;
        this.userName = bean.userName;
        this.avatar = bean.avatar;
        this.gender = bean.gender;
        this.commentValue = bean.commentValue;
        this.commentTime = bean.commentTime;
        this.isOnline = bean.isOnline;
        this.isApproved = bean.isApproved;
        this.subComments = bean.subComments;
        this.msgType = bean.msgType;
        this.serverTime = bean.serverTime;
        this.from = bean.from;
        this.buzzId = bean.buzzId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ListCommentBean that = (ListCommentBean) o;

        return cmtId.equals(that.cmtId) && buzzId.equals(that.buzzId);
    }

    @Override
    public int hashCode() {
        int result = cmtId.hashCode();
        result = 31 * result + buzzId.hashCode();
        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.region);
        dest.writeInt(this.subCommentNumber);
        dest.writeInt(this.commentNumber);
        dest.writeInt(this.allSubCommentNumber);
        dest.writeInt(this.subCommentPoint);
        dest.writeInt(this.canDelete);
        dest.writeString(this.cmtId);
        dest.writeString(this.userId);
        dest.writeString(this.userName);
        dest.writeString(this.avatar);
        dest.writeInt(this.gender);
        dest.writeString(this.commentValue);
        dest.writeString(this.commentTime);
        dest.writeByte(this.isOnline ? (byte) 1 : (byte) 0);
        dest.writeInt(this.isApproved);
        dest.writeTypedList(this.subComments);
        dest.writeString(this.msgType);
        dest.writeString(this.serverTime);
        dest.writeString(this.from);
        dest.writeString(this.buzzId);
        dest.writeByte(this.isViewMore ? (byte) 1 : (byte) 0);
        dest.writeString(this.textViewMore);
    }

    public ListCommentBean() {
    }

    protected ListCommentBean(Parcel in) {
        this.region = in.readInt();
        this.subCommentNumber = in.readInt();
        this.commentNumber = in.readInt();
        this.allSubCommentNumber = in.readInt();
        this.subCommentPoint = in.readInt();
        this.canDelete = in.readInt();
        this.cmtId = in.readString();
        this.userId = in.readString();
        this.userName = in.readString();
        this.avatar = in.readString();
        this.gender = in.readInt();
        this.commentValue = in.readString();
        this.commentTime = in.readString();
        this.isOnline = in.readByte() != 0;
        this.isApproved = in.readInt();
        this.subComments = in.createTypedArrayList(BuzzSubCommentBean.CREATOR);
        this.msgType = in.readString();
        this.serverTime = in.readString();
        this.from = in.readString();
        this.buzzId = in.readString();
        this.isViewMore = in.readByte() != 0;
        this.textViewMore = in.readString();
    }

    public static final Parcelable.Creator<ListCommentBean> CREATOR = new Parcelable.Creator<ListCommentBean>() {
        @Override
        public ListCommentBean createFromParcel(Parcel source) {
            return new ListCommentBean(source);
        }

        @Override
        public ListCommentBean[] newArray(int size) {
            return new ListCommentBean[size];
        }
    };
}
