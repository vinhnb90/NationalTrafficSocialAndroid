package com.vn.ntsc.repository.model.comment;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.vn.ntsc.core.model.BaseBean;

/**
 * Created by nankai on 4/23/2018.
 */

public class CommentDetailBean extends BaseBean{

    @SerializedName("cmt_time")
    public String cmtTime;
    @SerializedName("sub_comment_number")
    public String subCommentNumber;
    @SerializedName("is_app")
    public int isApp;
    @SerializedName("user_id")
    public String userId;
    @SerializedName("can_delete")
    public int canDelete;
    @SerializedName("cmt_id")
    public String cmtId;
    @SerializedName("cmt_val")
    public String cmtVal;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.cmtTime);
        dest.writeString(this.subCommentNumber);
        dest.writeInt(this.isApp);
        dest.writeString(this.userId);
        dest.writeInt(this.canDelete);
        dest.writeString(this.cmtId);
        dest.writeString(this.cmtVal);
    }

    public CommentDetailBean() {
    }

    protected CommentDetailBean(Parcel in) {
        this.cmtTime = in.readString();
        this.subCommentNumber = in.readString();
        this.isApp = in.readInt();
        this.userId = in.readString();
        this.canDelete = in.readInt();
        this.cmtId = in.readString();
        this.cmtVal = in.readString();
    }

    public static final Parcelable.Creator<CommentDetailBean> CREATOR = new Parcelable.Creator<CommentDetailBean>() {
        @Override
        public CommentDetailBean createFromParcel(Parcel source) {
            return new CommentDetailBean(source);
        }

        @Override
        public CommentDetailBean[] newArray(int size) {
            return new CommentDetailBean[size];
        }
    };
}
