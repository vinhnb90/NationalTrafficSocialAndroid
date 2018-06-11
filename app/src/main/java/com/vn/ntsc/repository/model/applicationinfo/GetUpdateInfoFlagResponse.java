package com.vn.ntsc.repository.model.applicationinfo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.vn.ntsc.core.model.ServerResponse;

/**
 * Created by nankai on 8/3/2017.
 */

public class GetUpdateInfoFlagResponse extends ServerResponse {

    public static final Parcelable.Creator<GetUpdateInfoFlagResponse> CREATOR = new Parcelable.Creator<GetUpdateInfoFlagResponse>() {
        @Override
        public GetUpdateInfoFlagResponse createFromParcel(Parcel source) {
            return new GetUpdateInfoFlagResponse(source);
        }

        @Override
        public GetUpdateInfoFlagResponse[] newArray(int size) {
            return new GetUpdateInfoFlagResponse[size];
        }
    };
    @SerializedName("data")
    public GetUpdateInfoFlagResponse data;
    
    @SerializedName("update_email_flag")
    public int updateEmailFlag;
    @SerializedName("verification_flag")
    public int verificationFlag;
    @SerializedName("finish_register_flag")
    public int finishRegisterFlag;

    public GetUpdateInfoFlagResponse() {
    }

    protected GetUpdateInfoFlagResponse(Parcel in) {
        super(in);
        this.updateEmailFlag = in.readInt();
        this.verificationFlag = in.readInt();
        this.finishRegisterFlag = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(this.updateEmailFlag);
        dest.writeInt(this.verificationFlag);
        dest.writeInt(this.finishRegisterFlag);
    }
}
