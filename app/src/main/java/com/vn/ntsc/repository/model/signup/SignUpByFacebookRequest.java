package com.vn.ntsc.repository.model.signup;

import com.google.gson.annotations.SerializedName;
import com.vn.ntsc.core.model.ServerRequest;

/**
 * Created by dev22 on 9/5/17.
 */
public class SignUpByFacebookRequest extends ServerRequest {
    @SerializedName("fb_id")
    public String facebookId;
    @SerializedName("user_name")
    public String userName;
    @SerializedName("bir")
    public String birthday; // yyyyMMdd
    @SerializedName("gender")
    public int gender;
    @SerializedName("id_number")
    public String identifyNumber;
    @SerializedName("region")
    public int region;
    @SerializedName("gps_adid")
    public String gpsAdid;
    @SerializedName("login_time")
    public String loginTime; // current local time
    @SerializedName("notify_token")
    public String notifyToken;
    @SerializedName("application_version")
    public String appVersion;
    @SerializedName("device_id")
    public String deviceId;
    @SerializedName("device_name")
    public String deviceName;
    @SerializedName("device_type")
    public int deviceType = 1; // 0 : IOS | 1: Android
    @SerializedName("os_version")
    public String osVersion;
    @SerializedName("phone_number")
    public String phoneNo;
    @SerializedName("job")
    public int job;
    @SerializedName("hobby")
    public String hobby;
    @SerializedName("body_type")
    public int bodyType;
    @SerializedName("abt")
    public String abt = "";

    public SignUpByFacebookRequest(String facebookId, String userName, String birthday, int gender, String identifyNumber, int region, String phoneNumber, int job, String hobby, int bodyType, String message, String gpsAdid, String loginTime, String notifyToken, String appVersion, String deviceId, String deviceName, String osVersion) {
        super("reg_ver_2");
        this.facebookId = facebookId;
        this.userName = userName;
        this.birthday = birthday;
        this.gender = gender;
        this.identifyNumber = identifyNumber;
        this.region = region;
        this.gpsAdid = gpsAdid;
        this.loginTime = loginTime;
        this.notifyToken = notifyToken;
        this.appVersion = appVersion;
        this.deviceId = deviceId;
        this.deviceName = deviceName;
        this.osVersion = osVersion;
        this.phoneNo = phoneNumber;
        this.job = job;
        this.hobby = hobby;
        this.bodyType = bodyType;
        this.abt = message;
    }
}
