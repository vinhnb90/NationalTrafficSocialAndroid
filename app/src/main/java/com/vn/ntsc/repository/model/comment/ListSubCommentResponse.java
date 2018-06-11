package com.vn.ntsc.repository.model.comment;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.vn.ntsc.core.model.ServerResponse;
import com.vn.ntsc.repository.model.timeline.datas.sub.BuzzSubCommentBean;

import java.util.List;

/**
 * Created by nankai on 8/10/2017.
 */

public class ListSubCommentResponse extends ServerResponse {

    @SerializedName("data")
    public List<BuzzSubCommentBean> data;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeTypedList(this.data);
    }

    public ListSubCommentResponse() {
    }

    protected ListSubCommentResponse(Parcel in) {
        super(in);
        this.data = in.createTypedArrayList(BuzzSubCommentBean.CREATOR);
    }

    public static final Parcelable.Creator<ListSubCommentResponse> CREATOR = new Parcelable.Creator<ListSubCommentResponse>() {
        @Override
        public ListSubCommentResponse createFromParcel(Parcel source) {
            return new ListSubCommentResponse(source);
        }

        @Override
        public ListSubCommentResponse[] newArray(int size) {
            return new ListSubCommentResponse[size];
        }
    };
}
