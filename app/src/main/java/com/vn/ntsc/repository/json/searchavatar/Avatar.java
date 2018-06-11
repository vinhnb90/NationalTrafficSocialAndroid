package com.vn.ntsc.repository.json.searchavatar;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.vn.ntsc.core.model.BaseBean;

/**
 * Created by ThoNh on 9/26/2017.
 */

public class Avatar extends BaseBean  {
    @SerializedName("name")
    public String name;

    @SerializedName("value")
    public int value;

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        Avatar item = (Avatar) obj;
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

    public Avatar() {
    }

    protected Avatar(Parcel in) {
        this.name = in.readString();
        this.value = in.readInt();
    }

    public static final Parcelable.Creator<Avatar> CREATOR = new Parcelable.Creator<Avatar>() {
        @Override
        public Avatar createFromParcel(Parcel source) {
            return new Avatar(source);
        }

        @Override
        public Avatar[] newArray(int size) {
            return new Avatar[size];
        }
    };
}
