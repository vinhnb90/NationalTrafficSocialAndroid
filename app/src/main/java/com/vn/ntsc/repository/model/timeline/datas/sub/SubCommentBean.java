package com.vn.ntsc.repository.model.timeline.datas.sub;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.vn.ntsc.core.model.BaseBean;

/**
 * Created by nankai on 8/10/2017.
 */

public class SubCommentBean extends BaseBean {

    private static final long serialVersionUID = 2736094459882496035L;

    /**
     * Sub ic_comment Id
     */
    @SerializedName("sub_comment_id")
    public String sub_comment_id;

    /**
     * Id of user who sent sub ic_comment
     */
    @SerializedName("user_id")
    public String user_id;

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
    public String ava_id;

    /**
     * User Name
     */
    @SerializedName("user_name")
    public String user_name;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.sub_comment_id);
        dest.writeString(this.user_id);
        dest.writeString(this.value);
        dest.writeString(this.time);
        dest.writeString(this.ava_id);
        dest.writeString(this.user_name);
    }

    public SubCommentBean() {
    }

    protected SubCommentBean(Parcel in) {
        this.sub_comment_id = in.readString();
        this.user_id = in.readString();
        this.value = in.readString();
        this.time = in.readString();
        this.ava_id = in.readString();
        this.user_name = in.readString();
    }

    public static final Parcelable.Creator<SubCommentBean> CREATOR = new Parcelable.Creator<SubCommentBean>() {
        @Override
        public SubCommentBean createFromParcel(Parcel source) {
            return new SubCommentBean(source);
        }

        @Override
        public SubCommentBean[] newArray(int size) {
            return new SubCommentBean[size];
        }
    };
}
