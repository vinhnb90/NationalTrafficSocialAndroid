package com.vn.ntsc.repository.model.notification;

import com.google.gson.annotations.SerializedName;
import com.vn.ntsc.core.model.ServerRequest;

/**
 * Created by ThoNh on 8/31/2017.
 */

public class DelNotificationRequest extends ServerRequest {

    @SerializedName("noti_id")
    public String notiId;

    public DelNotificationRequest(String notiId, String token) {
        super("delete_notification");
        this.notiId = notiId;
        this.token = token;
    }
}
