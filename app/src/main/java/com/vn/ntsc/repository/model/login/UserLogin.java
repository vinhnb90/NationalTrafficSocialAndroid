package com.vn.ntsc.repository.model.login;


import com.vn.ntsc.app.AppController;
import com.vn.ntsc.repository.preferece.Preferences;
import com.vn.ntsc.repository.preferece.UserPreferences;
import com.vn.ntsc.utils.SystemUtils;
import com.vn.ntsc.utils.time.TimeUtils;

/**
 * Created by nankai on 12/2/2016.
 */

public class UserLogin {
    public String email;
    public String facebookId;
    public String pass;
    public String deviceId;
    public String notifyToken;
    public String loginTime;
    public String appVersion;
    public String deviceName;
    public String osVersion;
    public String gpsAdid;

    public UserLogin() {
    }

    public UserLogin(String email, String facebookId, String pass, String deviceId, String notifyToken, String loginTime, String appVersion, String deviceName, String osVersion, String gpsAdid) {
        this.email = email;
        this.facebookId = facebookId;
        this.pass = pass;
        this.deviceId = deviceId;
        this.notifyToken = notifyToken;
        this.loginTime = loginTime;
        this.appVersion = appVersion;
        this.deviceName = deviceName;
        this.osVersion = osVersion;
        this.gpsAdid = gpsAdid;
    }

    public static synchronized UserLogin onAutoLogin() {
        UserPreferences userPreferences = UserPreferences.getInstance();
        // Authentication data
        String email = userPreferences.getRegEmail();
        String facebookId = userPreferences.getFacebookId();
        String pass = userPreferences.getPassword();
        // Get basic login data
        String device_id = SystemUtils.getDeviceId();
        String notify_token = Preferences.getInstance().getFirebaseRegistrationId();
        String login_time = TimeUtils.getLoginTime();
        String appVersion = SystemUtils.getAppVersionName(AppController.getAppContext());
        String device_name = SystemUtils.getDeviceName();
        String os_version = SystemUtils.getAndroidOSVersion();
        String gps_adid = Preferences.getInstance().getGPSADID();
        return new UserLogin(email, facebookId, pass, device_id, notify_token, login_time, appVersion, device_name, os_version, gps_adid);
    }
}