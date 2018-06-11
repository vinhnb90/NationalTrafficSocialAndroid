package com.vn.ntsc.repository.json.boby;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.vn.ntsc.core.model.BaseBean;

/**
 * Created by ThoNh on 9/21/2017.
 */

public class BodyTypeItem extends BaseBean {

    @SerializedName("name")
    public String name;

    @SerializedName("value")
    public int value;

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        BodyTypeItem item = (BodyTypeItem) obj;
        return item.name.equals(this.name) && item.value == this.value || super.equals(obj);
    }

    @Override
    public int hashCode() {
        return name.hashCode() + value;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
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

    public BodyTypeItem() {
    }

    protected BodyTypeItem(Parcel in) {
        this.name = in.readString();
        this.value = in.readInt();
    }

    public static final Parcelable.Creator<BodyTypeItem> CREATOR = new Parcelable.Creator<BodyTypeItem>() {
        @Override
        public BodyTypeItem createFromParcel(Parcel source) {
            return new BodyTypeItem(source);
        }

        @Override
        public BodyTypeItem[] newArray(int size) {
            return new BodyTypeItem[size];
        }
    };
}
