package com.vn.ntsc.repository.json.hobby;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.vn.ntsc.core.model.BaseBean;

/**
 * Created by ThoNh on 9/21/2017.
 */

public class HobbyItem extends BaseBean{
    @SerializedName("name")
    public String name;

    @SerializedName("value")
    public String value;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.value);
    }

    public HobbyItem() {
    }

    protected HobbyItem(Parcel in) {
        this.name = in.readString();
        this.value = in.readString();
    }

    public static final Parcelable.Creator<HobbyItem> CREATOR = new Parcelable.Creator<HobbyItem>() {
        @Override
        public HobbyItem createFromParcel(Parcel source) {
            return new HobbyItem(source);
        }

        @Override
        public HobbyItem[] newArray(int size) {
            return new HobbyItem[size];
        }
    };
}
