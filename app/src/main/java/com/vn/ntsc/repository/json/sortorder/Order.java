package com.vn.ntsc.repository.json.sortorder;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.vn.ntsc.core.model.BaseBean;

/**
 * Created by ThoNh on 9/26/2017.
 */

public class Order extends BaseBean{
    @SerializedName("name")
    public String name;

    @SerializedName("value")
    public int value;

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        Order item = (Order) obj;
        return item.name.equals(this.name) && item.value == this.value || super.equals(obj);
    }

    @Override
    public int hashCode() {
        return name.hashCode() + value;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeInt(this.value);
    }

    public Order() {
    }

    protected Order(Parcel in) {
        this.name = in.readString();
        this.value = in.readInt();
    }

    public static final Parcelable.Creator<Order> CREATOR = new Parcelable.Creator<Order>() {
        @Override
        public Order createFromParcel(Parcel source) {
            return new Order(source);
        }

        @Override
        public Order[] newArray(int size) {
            return new Order[size];
        }
    };
}
