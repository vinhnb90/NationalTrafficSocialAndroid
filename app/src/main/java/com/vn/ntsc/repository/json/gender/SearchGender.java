package com.vn.ntsc.repository.json.gender;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.vn.ntsc.core.model.BaseBean;

import java.util.List;

/**
 * Created by ducng on 10/24/2017.
 */

public class SearchGender extends BaseBean {

    @SerializedName("data")
    public List<GenderType> mDataGender;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(mDataGender);
    }


    protected SearchGender(Parcel in) {
        mDataGender = in.createTypedArrayList(GenderType.CREATOR);
    }

    public static final Parcelable.Creator<SearchGender> CREATOR = new Parcelable.Creator<SearchGender>() {
        @Override
        public SearchGender createFromParcel(Parcel source) {
            return new SearchGender(source);
        }

        @Override
        public SearchGender[] newArray(int size) {
            return new SearchGender[size];
        }
    };

}
