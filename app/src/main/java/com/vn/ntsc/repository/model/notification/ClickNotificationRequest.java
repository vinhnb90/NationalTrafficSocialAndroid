package com.vn.ntsc.repository.model.notification;

import com.google.gson.annotations.SerializedName;
import com.vn.ntsc.core.model.ServerRequest;

/**
 * Created by ThoNh on 8/30/2017.
 */

public class ClickNotificationRequest extends ServerRequest {

    @SerializedName("noti_id")
    public String pushId;
    @SerializedName("user_id")
    public String userId;

    public ClickNotificationRequest(String token, String pushId, String userId) {
        super("click_noti_notification");
        this.token = token;
        this.pushId = pushId;
        this.userId = userId;
    }
}
