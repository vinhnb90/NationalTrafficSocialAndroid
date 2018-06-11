package com.vn.ntsc.repository.model.comment;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.vn.ntsc.core.model.BaseBean;

/**
 * Created by nankai on 8/23/2017.
 */

public class AddCommentBean extends BaseBean {

    @SerializedName("cmt_id")
    public String cmtId;

    @SerializedName("point")
    public int point;

    public int commentBuzzPoint;

    @SerializedName("is_app")
    public int isApprove;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.cmtId);
        dest.writeInt(this.point);
        dest.writeInt(this.commentBuzzPoint);
        dest.writeInt(this.isApprove);
    }

    public AddCommentBean() {
    }

    protected AddCommentBean(Parcel in) {
        this.cmtId = in.readString();
        this.point = in.readInt();
        this.commentBuzzPoint = in.readInt();
        this.isApprove = in.readInt();
    }

    public static final Parcelable.Creator<AddCommentBean> CREATOR = new Parcelable.Creator<AddCommentBean>() {
        @Override
        public AddCommentBean createFromParcel(Parcel source) {
            return new AddCommentBean(source);
        }

        @Override
        public AddCommentBean[] newArray(int size) {
            return new AddCommentBean[size];
        }
    };
}

