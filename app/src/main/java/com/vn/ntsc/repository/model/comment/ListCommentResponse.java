package com.vn.ntsc.repository.model.comment;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.vn.ntsc.core.model.ServerResponse;
import com.vn.ntsc.repository.model.timeline.datas.sub.ListCommentBean;

import java.util.List;

/**
 * Created by nankai on 8/23/2017.
 */

public class ListCommentResponse extends ServerResponse {

    @SerializedName("data")
    public List<ListCommentBean> data;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeTypedList(this.data);
    }

    public ListCommentResponse() {
    }

    protected ListCommentResponse(Parcel in) {
        super(in);
        this.data = in.createTypedArrayList(ListCommentBean.CREATOR);
    }

    public static final Parcelable.Creator<ListCommentResponse> CREATOR = new Parcelable.Creator<ListCommentResponse>() {
        @Override
        public ListCommentResponse createFromParcel(Parcel source) {
            return new ListCommentResponse(source);
        }

        @Override
        public ListCommentResponse[] newArray(int size) {
            return new ListCommentResponse[size];
        }
    };
}
