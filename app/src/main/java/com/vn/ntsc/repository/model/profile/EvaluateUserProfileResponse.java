package com.vn.ntsc.repository.model.profile;

import android.os.Parcel;
import android.os.Parcelable;

import com.vn.ntsc.core.model.ServerResponse;

/**
 * Created by ducng on 11/8/2017.
 */

public class EvaluateUserProfileResponse extends ServerResponse {

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
    }

    public EvaluateUserProfileResponse() {
    }

    protected EvaluateUserProfileResponse(Parcel in) {
        super(in);
    }

    public static final Parcelable.Creator<EvaluateUserProfileResponse> CREATOR = new Parcelable.Creator<EvaluateUserProfileResponse>() {
        @Override
        public EvaluateUserProfileResponse createFromParcel(Parcel source) {
            return new EvaluateUserProfileResponse(source);
        }

        @Override
        public EvaluateUserProfileResponse[] newArray(int size) {
            return new EvaluateUserProfileResponse[size];
        }
    };
}
