package com.vn.ntsc.repository.model.comment;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.vn.ntsc.core.model.ServerResponse;
import com.vn.ntsc.repository.model.timeline.datas.sub.ListCommentBean;

/**
 * Created by nankai on 4/23/2018.
 */

public class CommentDetailResponse extends ServerResponse {

    @SerializedName("data")
    public ListCommentBean data;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeParcelable(this.data, flags);
    }

    public CommentDetailResponse() {
    }

    protected CommentDetailResponse(Parcel in) {
        super(in);
        this.data = in.readParcelable(ListCommentBean.class.getClassLoader());
    }

    public static final Parcelable.Creator<CommentDetailResponse> CREATOR = new Parcelable.Creator<CommentDetailResponse>() {
        @Override
        public CommentDetailResponse createFromParcel(Parcel source) {
            return new CommentDetailResponse(source);
        }

        @Override
        public CommentDetailResponse[] newArray(int size) {
            return new CommentDetailResponse[size];
        }
    };
}
