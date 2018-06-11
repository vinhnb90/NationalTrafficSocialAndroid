package com.vn.ntsc.repository.model.login;

import com.google.gson.annotations.SerializedName;
import com.vn.ntsc.core.model.ServerRequest;

/**
 * Created by nankai on 11/29/2016.
 */

public class LoginRequest extends ServerRequest {

    @SerializedName("device_id")
    private String device_id;
    @SerializedName("notify_token")
    private String notify_token;
    // Integer : 0 : IOS | 1: Android
    @SerializedName("device_type")
    private int device_type;
    @SerializedName("device_name")
    private String device_name;
    @SerializedName("os_version")
    private String os_version;
    @SerializedName("login_time")
    private String login_time;
    @SerializedName("application_version")
    private String application_version;
    @SerializedName("gps_adid")
    private String gps_adid;

    public LoginRequest(String device_id, String notifyToken, String logTime,
                        String appVer, String device_name, String os_version, String gps_adid) {
        super("login");
        this.device_id = device_id;
        this.notify_token = notifyToken;
        this.device_type = 1;
        this.login_time = logTime;
        this.application_version = appVer;
        this.device_name = device_name;
        this.os_version = os_version;
        this.gps_adid = gps_adid;
    }


    public LoginRequest(UserLogin userLogin) {
        super("login");
        this.device_id = userLogin.deviceId;
        this.notify_token = userLogin.notifyToken;
        this.device_type = 1;
        this.login_time = userLogin.loginTime;
        this.application_version = userLogin.appVersion;
        this.device_name = userLogin.deviceName;
        this.os_version = userLogin.osVersion;
        this.gps_adid = userLogin.gpsAdid;
    }
}
