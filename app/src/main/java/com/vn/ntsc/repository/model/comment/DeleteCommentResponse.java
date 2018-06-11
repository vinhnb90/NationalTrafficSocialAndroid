package com.vn.ntsc.repository.model.comment;

import android.os.Parcel;
import android.os.Parcelable;

import com.vn.ntsc.core.model.ServerResponse;

/**
 * Created by nankai on 8/23/2017.
 */

public class DeleteCommentResponse extends ServerResponse {

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
    }

    public DeleteCommentResponse() {
    }

    protected DeleteCommentResponse(Parcel in) {
        super(in);
    }

    public static final Parcelable.Creator<DeleteCommentResponse> CREATOR = new Parcelable.Creator<DeleteCommentResponse>() {
        @Override
        public DeleteCommentResponse createFromParcel(Parcel source) {
            return new DeleteCommentResponse(source);
        }

        @Override
        public DeleteCommentResponse[] newArray(int size) {
            return new DeleteCommentResponse[size];
        }
    };
}
