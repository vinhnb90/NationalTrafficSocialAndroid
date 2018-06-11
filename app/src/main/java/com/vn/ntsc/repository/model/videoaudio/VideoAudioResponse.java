package com.vn.ntsc.repository.model.videoaudio;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.vn.ntsc.core.model.ServerResponse;
import com.vn.ntsc.repository.model.timeline.datas.sub.ListBuzzChild;

import java.util.List;

/**
 * Created by ThoNh on 11/20/2017.
 */

public class VideoAudioResponse extends ServerResponse {

    @SerializedName("data")
    public List<ListBuzzChild> data;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeTypedList(this.data);
    }

    public VideoAudioResponse() {
    }

    protected VideoAudioResponse(Parcel in) {
        super(in);
        this.data = in.createTypedArrayList(ListBuzzChild.CREATOR);
    }

    public static final Parcelable.Creator<VideoAudioResponse> CREATOR = new Parcelable.Creator<VideoAudioResponse>() {
        @Override
        public VideoAudioResponse createFromParcel(Parcel source) {
            return new VideoAudioResponse(source);
        }

        @Override
        public VideoAudioResponse[] newArray(int size) {
            return new VideoAudioResponse[size];
        }
    };
}
