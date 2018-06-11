package com.vn.ntsc.repository.model.comment;

import android.os.Parcel;
import android.os.Parcelable;

import com.vn.ntsc.core.model.ServerResponse;

/**
 * Created by nankai on 8/24/2017.
 */

public class DeleteSubCommentResponse extends ServerResponse {

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
    }

    public DeleteSubCommentResponse() {
    }

    protected DeleteSubCommentResponse(Parcel in) {
        super(in);
    }

    public static final Parcelable.Creator<DeleteSubCommentResponse> CREATOR = new Parcelable.Creator<DeleteSubCommentResponse>() {
        @Override
        public DeleteSubCommentResponse createFromParcel(Parcel source) {
            return new DeleteSubCommentResponse(source);
        }

        @Override
        public DeleteSubCommentResponse[] newArray(int size) {
            return new DeleteSubCommentResponse[size];
        }
    };
}
