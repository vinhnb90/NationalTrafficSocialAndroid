package com.vn.ntsc.repository.model.block.rmvblock;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.vn.ntsc.core.model.ServerResponse;

/**
 * Created by ThoNh on 9/22/2017.
 */

public class UnblockResponse extends ServerResponse{

    @SerializedName("fav_num")
    public int favoriteNumber;

    @SerializedName("my_footprint_num")
    public int footprintNumber;


    public UnblockResponse() {
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(this.favoriteNumber);
        dest.writeInt(this.footprintNumber);
    }

    protected UnblockResponse(Parcel in) {
        super(in);
        this.favoriteNumber = in.readInt();
        this.footprintNumber = in.readInt();
    }

    public static final Parcelable.Creator<UnblockResponse> CREATOR = new Parcelable.Creator<UnblockResponse>() {
        @Override
        public UnblockResponse createFromParcel(Parcel source) {
            return new UnblockResponse(source);
        }

        @Override
        public UnblockResponse[] newArray(int size) {
            return new UnblockResponse[size];
        }
    };
}
