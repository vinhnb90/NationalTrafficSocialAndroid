package com.vn.ntsc.repository.model.notification;

import com.vn.ntsc.core.model.ServerRequest;

/**
 * Created by ThoNh on 11/9/2017.
 */

public class ReadAllNotificationRequest extends ServerRequest {

    public ReadAllNotificationRequest(String token) {
        super("total_noti_seen");
        this.token = token;
    }
}
