package com.vn.ntsc.repository.model.onlinealert;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.vn.ntsc.core.model.BaseBean;

/**
 * Created by nankai on 9/19/2017.
 */

public class GetOnlineAlertBean extends BaseBean {

    @SerializedName("is_alt")
    public int isAlt;
    @SerializedName("alt_fre")
    public int altNumber;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.isAlt);
        dest.writeInt(this.altNumber);
    }

    public GetOnlineAlertBean() {
    }

    protected GetOnlineAlertBean(Parcel in) {
        this.isAlt = in.readInt();
        this.altNumber = in.readInt();
    }

    public static final Parcelable.Creator<GetOnlineAlertBean> CREATOR = new Parcelable.Creator<GetOnlineAlertBean>() {
        @Override
        public GetOnlineAlertBean createFromParcel(Parcel source) {
            return new GetOnlineAlertBean(source);
        }

        @Override
        public GetOnlineAlertBean[] newArray(int size) {
            return new GetOnlineAlertBean[size];
        }
    };
}
