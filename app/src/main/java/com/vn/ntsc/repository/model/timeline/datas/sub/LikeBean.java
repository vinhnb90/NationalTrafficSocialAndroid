package com.vn.ntsc.repository.model.timeline.datas.sub;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.vn.ntsc.core.model.BaseBean;

/**
 * Created by nankai on 9/28/2017.
 */

public class LikeBean extends BaseBean {

    @SerializedName("is_like")
    public int isLike;
    @SerializedName("like_num")
    public long likeNumber;
    @SerializedName("like_list")
    public String[] likeList;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.isLike);
        dest.writeLong(this.likeNumber);
        dest.writeStringArray(this.likeList);
    }

    public LikeBean() {
    }

    protected LikeBean(Parcel in) {
        this.isLike = in.readInt();
        this.likeNumber = in.readLong();
        this.likeList = in.createStringArray();
    }

    public static final Parcelable.Creator<LikeBean> CREATOR = new Parcelable.Creator<LikeBean>() {
        @Override
        public LikeBean createFromParcel(Parcel source) {
            return new LikeBean(source);
        }

        @Override
        public LikeBean[] newArray(int size) {
            return new LikeBean[size];
        }
    };
}
