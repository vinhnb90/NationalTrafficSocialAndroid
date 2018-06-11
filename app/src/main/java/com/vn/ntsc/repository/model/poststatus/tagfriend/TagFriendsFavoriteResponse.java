package com.vn.ntsc.repository.model.poststatus.tagfriend;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.vn.ntsc.core.model.ServerResponse;

import java.util.List;

/**
 * The TagFriendFavorites respond model, use the parser respond from server
 */
public class TagFriendsFavoriteResponse extends ServerResponse {

    @SerializedName("data")
    public List<TagFriendsFavoriteBean> listFavorites;

    public TagFriendsFavoriteResponse() {

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

    protected TagFriendsFavoriteResponse(Parcel in) {
        super(in);
        this.listFavorites = in.createTypedArrayList(TagFriendsFavoriteBean.CREATOR);
    }

    public static final Parcelable.Creator<TagFriendsFavoriteResponse> CREATOR = new Parcelable.Creator<TagFriendsFavoriteResponse>() {
        @Override
        public TagFriendsFavoriteResponse createFromParcel(Parcel source) {
            return new TagFriendsFavoriteResponse(source);
        }

        @Override
        public TagFriendsFavoriteResponse[] newArray(int size) {
            return new TagFriendsFavoriteResponse[size];
        }
    };
}