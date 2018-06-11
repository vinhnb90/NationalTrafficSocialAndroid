package com.vn.ntsc.repository.model.image;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.vn.ntsc.core.model.BaseBean;

/**
 * Created by nankai on 8/29/2017.
 */

public class Image extends BaseBean {

    @SerializedName("original_url")
    public String originalUrl;

    @SerializedName("thumbnail_url")
    public String thumbnailUrl;

    @SerializedName("buzz_id")
    public String buzz_id;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.originalUrl);
        dest.writeString(this.thumbnailUrl);
        dest.writeString(this.buzz_id);
    }

    public Image() {
    }

    protected Image(Parcel in) {
        this.originalUrl = in.readString();
        this.thumbnailUrl = in.readString();
        this.buzz_id = in.readString();
    }

    public static final Parcelable.Creator<Image> CREATOR = new Parcelable.Creator<Image>() {
        @Override
        public Image createFromParcel(Parcel source) {
            return new Image(source);
        }

        @Override
        public Image[] newArray(int size) {
            return new Image[size];
        }
    };
}
