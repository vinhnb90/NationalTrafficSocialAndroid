package com.vn.ntsc.repository.model.online.notification;

import com.vn.ntsc.core.model.ServerRequest;

/**
 * Created by ThoNh on 3/29/2018.
 */

public class OnlineNotificationNumberRequest extends ServerRequest {

    public OnlineNotificationNumberRequest(String token) {
        super("list_user_online_alert");
        this.token = token;
    }
}
