package com.vn.ntsc.repository.model.comment;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.vn.ntsc.core.model.ServerResponse;

/**
 * Created by nankai on 8/23/2017.
 */

public class AddCommentResponse extends ServerResponse implements Parcelable {

    @SerializedName("data")
    public AddCommentBean data;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeParcelable(this.data, flags);
    }

    public AddCommentResponse() {
    }

    protected AddCommentResponse(Parcel in) {
        super(in);
        this.data = in.readParcelable(AddCommentBean.class.getClassLoader());
    }

    public static final Creator<AddCommentResponse> CREATOR = new Creator<AddCommentResponse>() {
        @Override
        public AddCommentResponse createFromParcel(Parcel source) {
            return new AddCommentResponse(source);
        }

        @Override
        public AddCommentResponse[] newArray(int size) {
            return new AddCommentResponse[size];
        }
    };
}
