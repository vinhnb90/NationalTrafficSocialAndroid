package com.vn.ntsc.repository.model.changepassword;

import com.google.gson.annotations.SerializedName;
import com.vn.ntsc.core.model.ServerRequest;

/**
 * Created by dev22 on 8/22/17.
 */
public class ChangePasswordRequest extends ServerRequest {
    /**
     * current client time
     */
    @SerializedName("login_time")
    public String loginTime;

    @SerializedName("email")
    public String email;

    /**
     * you can get verify code on email
     */
    @SerializedName("vrf_code")
    public String verifyCode;

    /**
     * 0-ios, 1-android
     */
    @SerializedName("device_type")
    public int deviceType = 1;

    @SerializedName("application_version")
    public String appVersion;

    @SerializedName("original_pwd")
    public String originPwd;

    /**
     * encrypted pass
     */
    @SerializedName("new_pwd")
    public String encryptedPwd;

    /**
     * 1: product, 2: enterprise
     */
    @SerializedName("applicaton_type")
    public int applicationType = 1;

    @SerializedName("device_id")
    public String devideId;

    @SerializedName("notify_token")
    public String notifyToken;

    @SerializedName("ip")
    public String ip = "";

    public ChangePasswordRequest(String loginTime, String email, String verifyCode, String appVersion, String originPwd, String encryptedPwd, String devideId, String notifyToken) {
        super("chg_pwd_fgt");
        this.loginTime = loginTime;
        this.email = email;
        this.verifyCode = verifyCode;
        this.appVersion = appVersion;
        this.originPwd = originPwd;
        this.encryptedPwd = encryptedPwd;
        this.devideId = devideId;
        this.notifyToken = notifyToken;
    }
}
