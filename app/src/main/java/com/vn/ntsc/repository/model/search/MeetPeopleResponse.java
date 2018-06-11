package com.vn.ntsc.repository.model.search;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.vn.ntsc.core.model.ServerResponse;

import java.util.List;

import static android.R.attr.data;

/**
 * Created by hnc on 21/08/2017.
 */

public class MeetPeopleResponse extends ServerResponse {

    @SerializedName("data")
    public List<MeetPeopleBean> mData ;

    public MeetPeopleResponse() {

    }

    @Override
    public String toString() {
        return "MeetPeopleResponse{" +
                "data=" + data +
                '}';
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeTypedList(this.mData);
    }

    protected MeetPeopleResponse(Parcel in) {
        super(in);
        this.mData = in.createTypedArrayList(MeetPeopleBean.CREATOR);
    }

    public static final Parcelable.Creator<MeetPeopleResponse> CREATOR = new Parcelable.Creator<MeetPeopleResponse>() {
        @Override
        public MeetPeopleResponse createFromParcel(Parcel source) {
            return new MeetPeopleResponse(source);
        }

        @Override
        public MeetPeopleResponse[] newArray(int size) {
            return new MeetPeopleResponse[size];
        }
    };
}
