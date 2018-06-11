package com.vn.ntsc.repository.model.block.blocklst;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.vn.ntsc.core.model.ServerResponse;

import java.util.List;

/**
 * Created by ThoNh on 9/22/2017.
 */

public class BlockListResponse extends ServerResponse {

    @SerializedName("data")
    public List<BlockLstItem> mData;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeTypedList(this.mData);
    }

    public BlockListResponse() {
    }

    protected BlockListResponse(Parcel in) {
        super(in);
        this.mData = in.createTypedArrayList(BlockLstItem.CREATOR);
    }

    public static final Parcelable.Creator<BlockListResponse> CREATOR = new Parcelable.Creator<BlockListResponse>() {
        @Override
        public BlockListResponse createFromParcel(Parcel source) {
            return new BlockListResponse(source);
        }

        @Override
        public BlockListResponse[] newArray(int size) {
            return new BlockListResponse[size];
        }
    };
}
