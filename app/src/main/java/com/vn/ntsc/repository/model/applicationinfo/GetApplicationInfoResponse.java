package com.vn.ntsc.repository.model.applicationinfo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.vn.ntsc.core.model.ServerResponse;

/**
 * Created by nankai on 11/28/2016.
 */

public class GetApplicationInfoResponse extends ServerResponse {

    public static final Parcelable.Creator<GetApplicationInfoResponse> CREATOR = new Parcelable.Creator<GetApplicationInfoResponse>() {
        @Override
        public GetApplicationInfoResponse createFromParcel(Parcel source) {
            return new GetApplicationInfoResponse(source);
        }

        @Override
        public GetApplicationInfoResponse[] newArray(int size) {
            return new GetApplicationInfoResponse[size];
        }
    };
    private static final long serialVersionUID = -9174703502721624612L;
    @SerializedName("data")
    public GetApplicationInfoResponse data;
    @SerializedName("switch_browser_android_version")
    public String switchBrowserVersion;
    @SerializedName("switch_browser_android")
    public boolean isSwitchBrowser;
    @SerializedName("login_by_mocom_android")
    public boolean isLoginByAnotherSystem;
    @SerializedName("get_free_point_android")
    public boolean isGetFreePoint;
    @SerializedName("turn_off_user_info_android")
    public boolean isTurnOffUserInfo;
    @SerializedName("turn_off_show_news_android")
    public boolean isShowNews;

    public GetApplicationInfoResponse() {
    }

    protected GetApplicationInfoResponse(Parcel in) {
        super(in);
        this.data = in.readParcelable(GetApplicationInfoResponse.class.getClassLoader());
        this.switchBrowserVersion = in.readString();
        this.isSwitchBrowser = in.readByte() != 0;
        this.isLoginByAnotherSystem = in.readByte() != 0;
        this.isGetFreePoint = in.readByte() != 0;
        this.isTurnOffUserInfo = in.readByte() != 0;
        this.isShowNews = in.readByte() != 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeParcelable(this.data, flags);
        dest.writeString(this.switchBrowserVersion);
        dest.writeByte(this.isSwitchBrowser ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isLoginByAnotherSystem ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isGetFreePoint ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isTurnOffUserInfo ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isShowNews ? (byte) 1 : (byte) 0);
    }
}
