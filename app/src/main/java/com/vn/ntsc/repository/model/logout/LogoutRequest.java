package com.vn.ntsc.repository.model.logout;

import com.google.gson.annotations.SerializedName;
import com.vn.ntsc.core.model.ServerRequest;

/**
 * Created by nankai on 9/1/2017.
 */

public class LogoutRequest extends ServerRequest{

    @SerializedName("notify_token")
    private String notify_token;
    @SerializedName("device_type")
    private int device_type;


    public LogoutRequest(String token, String notify_token) {
        super("logout");
        this.notify_token = notify_token;
        this.device_type = 1;
        this.token = token;
    }
}
