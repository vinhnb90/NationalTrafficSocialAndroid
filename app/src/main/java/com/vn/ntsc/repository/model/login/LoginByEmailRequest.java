package com.vn.ntsc.repository.model.login;

import com.google.gson.annotations.SerializedName;

/**
 * Created by nankai on 11/29/2016.
 */

public class LoginByEmailRequest extends LoginRequest {

    @SerializedName("email")
    protected String email;
    @SerializedName("pwd")
    protected String pwd;

    public LoginByEmailRequest(String email, String password, String device_id,
                               String notify_token, String login_time, String appVersion, String device_name, String os_version, String gps_adid) {
        super(device_id, notify_token, login_time, appVersion, device_name, os_version, gps_adid);
        this.email = email;
        this.pwd = password;
    }

    public LoginByEmailRequest(UserLogin userLogin) {
        super(userLogin.deviceId, userLogin.notifyToken, userLogin.loginTime, userLogin.appVersion, userLogin.deviceName, userLogin.osVersion, userLogin.gpsAdid);
        this.email = userLogin.email;
        this.pwd = userLogin.pass;
    }
}
