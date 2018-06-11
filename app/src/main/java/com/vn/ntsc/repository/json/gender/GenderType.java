package com.vn.ntsc.repository.json.gender;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.vn.ntsc.core.model.BaseBean;

/**
 * Created by ducnv on 10/24/2017.
 */

public class GenderType extends BaseBean  {
    @SerializedName("name")
    public String name;

    @SerializedName("value")
    public int value;

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        GenderType item = (GenderType) obj;
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

    public GenderType() {
    }

    protected GenderType(Parcel in) {
        this.name = in.readString();
        this.value = in.readInt();
    }

    public static final Parcelable.Creator<GenderType> CREATOR = new Parcelable.Creator<GenderType>() {
        @Override
        public GenderType createFromParcel(Parcel source) {
            return new GenderType(source);
        }

        @Override
        public GenderType[] newArray(int size) {
            return new GenderType[size];
        }
    };
}
