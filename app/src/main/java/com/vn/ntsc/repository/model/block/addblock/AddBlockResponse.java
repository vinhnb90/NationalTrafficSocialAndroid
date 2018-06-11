package com.vn.ntsc.repository.model.block.addblock;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.vn.ntsc.core.model.ServerResponse;

/**
 * Created by ThoNh on 9/22/2017.
 */

public class AddBlockResponse extends ServerResponse {

    @SerializedName("fav_num")
    public int favoriteNumber;

    @SerializedName("my_footprint_num")
    public int footPrintNumber;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.favoriteNumber);
        dest.writeInt(this.footPrintNumber);
    }

    public AddBlockResponse() {
    }

    protected AddBlockResponse(Parcel in) {
        this.favoriteNumber = in.readInt();
        this.footPrintNumber = in.readInt();
    }

    public static final Parcelable.Creator<AddBlockResponse> CREATOR = new Parcelable.Creator<AddBlockResponse>() {
        @Override
        public AddBlockResponse createFromParcel(Parcel source) {
            return new AddBlockResponse(source);
        }

        @Override
        public AddBlockResponse[] newArray(int size) {
            return new AddBlockResponse[size];
        }
    };
}
