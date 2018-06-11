package com.vn.ntsc.repository.model.poststatus;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.vn.ntsc.core.model.BaseBean;

import java.util.List;

/**
 * Created by nankai on 11/27/2017.
 */

public class PostStatusBean extends BaseBean {

    @SerializedName("img_list")
    public List<String> imgList;
    @SerializedName("buzz_id")
    public String buzzId;
    @SerializedName("is_app")
    public int isApp;
    @SerializedName("comment_app")
    public int commentApp;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringList(this.imgList);
        dest.writeString(this.buzzId);
        dest.writeInt(this.isApp);
        dest.writeInt(this.commentApp);
    }

    public PostStatusBean() {
    }

    protected PostStatusBean(Parcel in) {
        this.imgList = in.createStringArrayList();
        this.buzzId = in.readString();
        this.isApp = in.readInt();
        this.commentApp = in.readInt();
    }

    public static final Parcelable.Creator<PostStatusBean> CREATOR = new Parcelable.Creator<PostStatusBean>() {
        @Override
        public PostStatusBean createFromParcel(Parcel source) {
            return new PostStatusBean(source);
        }

        @Override
        public PostStatusBean[] newArray(int size) {
            return new PostStatusBean[size];
        }
    };
}
