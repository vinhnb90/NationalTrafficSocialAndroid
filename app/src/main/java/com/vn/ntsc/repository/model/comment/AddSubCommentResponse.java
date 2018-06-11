package com.vn.ntsc.repository.model.comment;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.vn.ntsc.core.model.ServerResponse;

/**
 * Created by nankai on 8/24/2017.
 */

public class AddSubCommentResponse extends ServerResponse {

    @SerializedName("data")
    public AddSubCommentBean data;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeParcelable(this.data, flags);
    }

    public AddSubCommentResponse() {
    }

    protected AddSubCommentResponse(Parcel in) {
        super(in);
        this.data = in.readParcelable(AddSubCommentBean.class.getClassLoader());
    }

    public static final Parcelable.Creator<AddSubCommentResponse> CREATOR = new Parcelable.Creator<AddSubCommentResponse>() {
        @Override
        public AddSubCommentResponse createFromParcel(Parcel source) {
            return new AddSubCommentResponse(source);
        }

        @Override
        public AddSubCommentResponse[] newArray(int size) {
            return new AddSubCommentResponse[size];
        }
    };
}
