package com.vn.ntsc.repository.json.regions;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.vn.ntsc.core.model.BaseBean;

/**
 * Created by ThoNh on 9/21/2017.
 */

public class RegionItem extends BaseBean {

    /**
     * Type of item
     *
     * @see ChooseAreaAdapter#TYPE_HEADER
     * @see ChooseAreaAdapter#TYPE_ROUND_BOTTOM
     * @see ChooseAreaAdapter#TYPE_ROUND_NORMAL
     * @see ChooseAreaAdapter#TYPE_ROUND_TOP
     */
    public int mAdapterType;

    @SerializedName("value")
    public int value;

    @SerializedName("name")
    public String name;

    @SerializedName("lat")
    public double lat;

    @SerializedName("lng")
    public double lng;


    private boolean isChecked;

    public RegionItem(int mAdapterType ,String name, int code) {
        this.mAdapterType = mAdapterType;
        this.name = name;
        this.value = code;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }


    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        RegionItem regionItem = (RegionItem) obj;
        return regionItem.name.equals(name) && regionItem.value == this.value || super.equals(obj);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.value);
        dest.writeString(this.name);
        dest.writeDouble(this.lat);
        dest.writeDouble(this.lng);
        dest.writeByte(this.isChecked ? (byte) 1 : (byte) 0);
    }

    protected RegionItem(Parcel in) {
        this.value = in.readInt();
        this.name = in.readString();
        this.lat = in.readDouble();
        this.lng = in.readDouble();
        this.isChecked = in.readByte() != 0;
    }

    public static final Parcelable.Creator<RegionItem> CREATOR = new Parcelable.Creator<RegionItem>() {
        @Override
        public RegionItem createFromParcel(Parcel source) {
            return new RegionItem(source);
        }

        @Override
        public RegionItem[] newArray(int size) {
            return new RegionItem[size];
        }
    };
}
