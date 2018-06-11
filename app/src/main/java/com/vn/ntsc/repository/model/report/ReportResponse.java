package com.vn.ntsc.repository.model.report;

import android.os.Parcel;
import android.os.Parcelable;

import com.vn.ntsc.core.model.ServerResponse;

/**
 * Created by nankai on 9/18/2017.
 */

public class ReportResponse extends ServerResponse {

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
    }

    public ReportResponse() {
    }

    protected ReportResponse(Parcel in) {
        super(in);
    }

    public static final Parcelable.Creator<ReportResponse> CREATOR = new Parcelable.Creator<ReportResponse>() {
        @Override
        public ReportResponse createFromParcel(Parcel source) {
            return new ReportResponse(source);
        }

        @Override
        public ReportResponse[] newArray(int size) {
            return new ReportResponse[size];
        }
    };
}
