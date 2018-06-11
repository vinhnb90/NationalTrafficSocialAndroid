package com.vn.ntsc.repository.model.chat.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Doremon on 12/27/2017.
 */

public class EmojiModel implements Parcelable {
    String code;
    String name;
    String url;
    String catId;

    public EmojiModel() {

    }

    public EmojiModel(String code, String name, String url, String catId) {
        this.code = code;
        this.name = name;
        this.url = url;
        this.catId = catId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EmojiModel)) return false;

        EmojiModel model = (EmojiModel) o;

        return url.equals(model.url);
    }

    @Override
    public int hashCode() {
        return url.hashCode();
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCatId() {
        return catId;
    }

    public void setCatId(String catId) {
        this.catId = catId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.code);
        dest.writeString(this.name);
        dest.writeString(this.url);
        dest.writeString(this.catId);
    }

    protected EmojiModel(Parcel in) {
        this.code = in.readString();
        this.name = in.readString();
        this.url = in.readString();
        this.catId = in.readString();
    }

    public static final Creator<EmojiModel> CREATOR = new Creator<EmojiModel>() {
        @Override
        public EmojiModel createFromParcel(Parcel source) {
            return new EmojiModel(source);
        }

        @Override
        public EmojiModel[] newArray(int size) {
            return new EmojiModel[size];
        }
    };
}
