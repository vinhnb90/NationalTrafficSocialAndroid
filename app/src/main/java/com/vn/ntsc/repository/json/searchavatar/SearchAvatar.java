package com.vn.ntsc.repository.json.searchavatar;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.vn.ntsc.core.model.BaseBean;

import java.util.List;

/**
 * Created by ThoNh on 9/25/2017.
 */

public class SearchAvatar extends BaseBean{

    @SerializedName("data")
    public List<Avatar> mData ;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.mData);
    }

    public SearchAvatar() {
    }

    protected SearchAvatar(Parcel in) {
        this.mData = in.createTypedArrayList(Avatar.CREATOR);
    }

    public static final Parcelable.Creator<SearchAvatar> CREATOR = new Parcelable.Creator<SearchAvatar>() {
        @Override
        public SearchAvatar createFromParcel(Parcel source) {
            return new SearchAvatar(source);
        }

        @Override
        public SearchAvatar[] newArray(int size) {
            return new SearchAvatar[size];
        }
    };
}
