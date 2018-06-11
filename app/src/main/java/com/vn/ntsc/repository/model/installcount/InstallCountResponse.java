package com.vn.ntsc.repository.model.installcount;

import android.os.Parcel;
import android.os.Parcelable;

import com.vn.ntsc.core.model.ServerResponse;

/**
 * Created by nankai on 8/3/2017.
 */

public class InstallCountResponse extends ServerResponse {
    public static final Parcelable.Creator<InstallCountResponse> CREATOR = new Parcelable.Creator<InstallCountResponse>() {
        @Override
        public InstallCountResponse createFromParcel(Parcel source) {
            return new InstallCountResponse(source);
        }

        @Override
        public InstallCountResponse[] newArray(int size) {
            return new InstallCountResponse[size];
        }
    };

    public InstallCountResponse() {
        super();
    }

    protected InstallCountResponse(Parcel in) {
        super(in);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
    }
}