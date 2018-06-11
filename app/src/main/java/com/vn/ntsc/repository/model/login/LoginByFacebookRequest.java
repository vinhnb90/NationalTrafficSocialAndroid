package com.vn.ntsc.repository.model.login;

import com.google.gson.annotations.SerializedName;


/**
 * Created by nankai on 11/29/2016.
 */

public class LoginByFacebookRequest extends LoginRequest {

    private static final long serialVersionUID = 3291744951370758177L;
    @SerializedName("fb_id")
    public String fb_id;

    public LoginByFacebookRequest(String fb_id, String device_id,
                                  String notify_token, String login_time, String appVersion, String device_name, String os_version, String gps_adid) {
        super(device_id, notify_token, login_time, appVersion, device_name, os_version, gps_adid);
        this.fb_id = fb_id;
    }

    public LoginByFacebookRequest(UserLogin userLogin) {
        super(userLogin.deviceId, userLogin.notifyToken, userLogin.loginTime, userLogin.appVersion, userLogin.deviceName, userLogin.osVersion, userLogin.gpsAdid);
        this.fb_id = userLogin.facebookId;
    }
}
