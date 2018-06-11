package com.vn.ntsc.repository.json.boby;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.vn.ntsc.core.model.BaseBean;

import java.util.List;

/**
 * Created by ThoNh on 9/21/2017.
 */

public class BodyType  extends BaseBean{

    @SerializedName("male")
    public List<BodyTypeItem> bodyTypeMale;

    @SerializedName("female")
    public List<BodyTypeItem> bodyTypeFeMale;



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
        dest.writeTypedList(this.bodyTypeMale);
        dest.writeTypedList(this.bodyTypeFeMale);
    }

    public BodyType() {
    }

    protected BodyType(Parcel in) {
        this.bodyTypeMale = in.createTypedArrayList(BodyTypeItem.CREATOR);
        this.bodyTypeFeMale = in.createTypedArrayList(BodyTypeItem.CREATOR);
    }

    public static final Parcelable.Creator<BodyType> CREATOR = new Parcelable.Creator<BodyType>() {
        @Override
        public BodyType createFromParcel(Parcel source) {
            return new BodyType(source);
        }

        @Override
        public BodyType[] newArray(int size) {
            return new BodyType[size];
        }
    };
}
