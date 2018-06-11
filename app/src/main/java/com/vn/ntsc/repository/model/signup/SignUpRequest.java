package com.vn.ntsc.repository.model.signup;

import com.google.gson.annotations.SerializedName;
import com.vn.ntsc.core.model.ServerRequest;

/**
 * Created by dev22 on 8/15/17.
 */
public class SignUpRequest extends ServerRequest {
    @SerializedName("fb_id")
    private String facebookId;
    @SerializedName("bir")
    private String birthday;
    @SerializedName("gender")
    private int gender;
    @SerializedName("device_id")
    private String device_id;
    @SerializedName("device_type")
    // Integer : 0 : IOS | 1: Android
    private int device_type;
    @SerializedName("login_time")
    private String login_time;
    @SerializedName("notify_token")
    private String notify_token;
    @SerializedName("application_version")
    private String application_version;
    @SerializedName("gps_adid")
    private String gps_adid;
    @SerializedName("os_version")
    private String os_version;
    @SerializedName("device_name")
    private String device_name;
    @SerializedName("adid")
    private String adid;
    // encrypted password
    @SerializedName("pwd")
    private String encryptedPassword;
    // origin password (without encrypt)
    @SerializedName("original_pwd")
    private String originPassword;
    @SerializedName("application")
    private int application;

    public SignUpRequest(String facebookId, String birthday, int gender, String device_id, String login_time, String notify_token, String application_version, String gps_adid, String os_version, String device_name, String adid, String encryptedPassword, String originPassword) {
        super("reg");
        this.facebookId = facebookId;
        this.birthday = birthday;
        this.gender = gender;
        this.device_id = device_id;
        this.device_type = 1;
        this.login_time = login_time;
        this.notify_token = notify_token;
        this.application_version = application_version;
        this.gps_adid = gps_adid;
        this.os_version = os_version;
        this.device_name = device_name;
        this.adid = adid;
        this.encryptedPassword = encryptedPassword;
        this.originPassword = originPassword;
//        this.application = application;
    }
}