package com.vn.ntsc.repository.model.comment;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.vn.ntsc.core.model.BaseBean;

/**
 * Created by nankai on 8/24/2017.
 */

public class AddSubCommentBean extends BaseBean {

    @SerializedName("sub_comment_id")
    public String subCommentId;

    @SerializedName("point")
    public int point;

    @SerializedName("sub_comment_point")
    public int subCommentPoint;

    @SerializedName("is_app")
    public int isApprove;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.subCommentId);
        dest.writeInt(this.point);
        dest.writeInt(this.subCommentPoint);
        dest.writeInt(this.isApprove);
    }

    public AddSubCommentBean() {
    }

    protected AddSubCommentBean(Parcel in) {
        this.subCommentId = in.readString();
        this.point = in.readInt();
        this.subCommentPoint = in.readInt();
        this.isApprove = in.readInt();
    }

    public static final Parcelable.Creator<AddSubCommentBean> CREATOR = new Parcelable.Creator<AddSubCommentBean>() {
        @Override
        public AddSubCommentBean createFromParcel(Parcel source) {
            return new AddSubCommentBean(source);
        }

        @Override
        public AddSubCommentBean[] newArray(int size) {
            return new AddSubCommentBean[size];
        }
    };
}
