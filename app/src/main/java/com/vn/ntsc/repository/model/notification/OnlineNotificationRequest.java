package com.vn.ntsc.repository.model.notification;

import com.vn.ntsc.core.model.ServerRequest;

/**
 * Created by ThoNh on 9/1/2017.
 */

public class OnlineNotificationRequest extends ServerRequest {

    public OnlineNotificationRequest(String token) {
        super("list_online_alert");
        this.token = token;
    }
}
