package com.vn.ntsc.repository.model.favorite;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.vn.ntsc.core.model.ServerResponse;

import java.util.ArrayList;

/**
 * Created by ThoNH on 08/08/2017.
 */

public class FriendsFavoriteResponse extends ServerResponse {

    @SerializedName("data")
    public ArrayList<FriendsFavoriteBean> listFavorites;

    public FriendsFavoriteResponse() {

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeTypedList(this.listFavorites);
    }

    protected FriendsFavoriteResponse(Parcel in) {
        super(in);
        this.listFavorites = in.createTypedArrayList(FriendsFavoriteBean.CREATOR);
    }

    public static final Parcelable.Creator<FriendsFavoriteResponse> CREATOR = new Parcelable.Creator<FriendsFavoriteResponse>() {
        @Override
        public FriendsFavoriteResponse createFromParcel(Parcel source) {
            return new FriendsFavoriteResponse(source);
        }

        @Override
        public FriendsFavoriteResponse[] newArray(int size) {
            return new FriendsFavoriteResponse[size];
        }
    };
}