package com.vn.ntsc.repository.json.sortorder;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.vn.ntsc.core.model.BaseBean;

import java.util.List;

/**
 * Created by ThoNh on 9/25/2017.
 */

public class SearchSortOrder extends BaseBean {

    @SerializedName("data")
    public List<Order> mData;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.mData);
    }

    public SearchSortOrder() {
    }

    protected SearchSortOrder(Parcel in) {
        this.mData = in.createTypedArrayList(Order.CREATOR);
    }

    public static final Parcelable.Creator<SearchSortOrder> CREATOR = new Parcelable.Creator<SearchSortOrder>() {
        @Override
        public SearchSortOrder createFromParcel(Parcel source) {
            return new SearchSortOrder(source);
        }

        @Override
        public SearchSortOrder[] newArray(int size) {
            return new SearchSortOrder[size];
        }
    };
}

