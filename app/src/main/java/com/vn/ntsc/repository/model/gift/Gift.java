package com.vn.ntsc.repository.model.gift;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.vn.ntsc.core.model.BaseBean;

/**
 * Created by TuanPC on 10/26/2017.
 */
public class Gift extends BaseBean {
    @SerializedName("cat_id")
    public String cat_id;
    @SerializedName("gift_id")
    public String gift_id;
    @SerializedName("gift_url")
    public String gift_url;
    @SerializedName("gift_pri")
    public int gift_pri;
    @SerializedName("gift_inf")
    public String gift_inf;
    @SerializedName("gift_name")
    public String gift_name;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.cat_id);
        dest.writeString(this.gift_id);
        dest.writeString(this.gift_url);
        dest.writeInt(this.gift_pri);
        dest.writeString(this.gift_inf);
        dest.writeString(this.gift_name);
    }

    public Gift() {
    }

    protected Gift(Parcel in) {
        this.cat_id = in.readString();
        this.gift_id = in.readString();
        this.gift_url = in.readString();
        this.gift_pri = in.readInt();
        this.gift_inf = in.readString();
        this.gift_name = in.readString();
    }

    public static final Parcelable.Creator<Gift> CREATOR = new Parcelable.Creator<Gift>() {
        @Override
        public Gift createFromParcel(Parcel source) {
            return new Gift(source);
        }

        @Override
        public Gift[] newArray(int size) {
            return new Gift[size];
        }
    };
}