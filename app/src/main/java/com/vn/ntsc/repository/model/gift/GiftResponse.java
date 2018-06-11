package com.vn.ntsc.repository.model.gift;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.vn.ntsc.core.model.ServerResponse;

import java.util.List;

import static android.R.attr.data;

/**
 * Created by TuanPC on 10/26/2017.
 */
public class GiftResponse extends ServerResponse {

    @SerializedName("data")
    public List<Gift> mDataGift;

    public GiftResponse() {

    }

    @Override
    public String toString() {
        return "GiftResponse{" +
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
        dest.writeTypedList(this.mDataGift);
    }

    protected GiftResponse(Parcel in) {
        super(in);
        this.mDataGift = in.createTypedArrayList(Gift.CREATOR);
    }

    public static final Parcelable.Creator<GiftResponse> CREATOR = new Parcelable.Creator<GiftResponse>() {
        @Override
        public GiftResponse createFromParcel(Parcel source) {
            return new GiftResponse(source);
        }

        @Override
        public GiftResponse[] newArray(int size) {
            return new GiftResponse[size];
        }
    };
}
