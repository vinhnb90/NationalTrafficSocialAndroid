package com.vn.ntsc.repository.model.notification;

import com.google.gson.annotations.SerializedName;
import com.vn.ntsc.core.model.ServerRequest;

/**
 * Created by nankai on 8/22/2017.
 */

public class UpdateNotificationRequest extends ServerRequest {



    @SerializedName("notify_token")
    private String notifyToken;

    @SerializedName("device_type")
    private int deviceType;

    @SerializedName("device_id")
    private String deviceId;

    @SerializedName("deviceToken")
    private String deviceToken;

    public UpdateNotificationRequest(String token, String notify_token, String deviceId) {
        super("upd_noti_token");
        this.token = token;

        this.deviceToken = notify_token;

        this.notifyToken = notify_token;

        this.deviceId = deviceId;

        this.deviceType = 1;
    }
}
