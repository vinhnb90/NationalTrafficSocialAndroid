package com.vn.ntsc.repository.model.onlinealert;

import com.google.gson.annotations.SerializedName;
import com.vn.ntsc.core.model.ServerRequest;

/**
 * Created by nankai on 9/19/2017.
 */

public class GetOnlineAlertRequest extends ServerRequest {

    @SerializedName("req_user_id")
    private String userId;


    public GetOnlineAlertRequest(String token) {
        super("get_onl_alt");
        this.token = token;
    }

    public GetOnlineAlertRequest(String token, String userId) {
        super("get_onl_alt");
        this.token = token;
        this.userId = userId;
    }
}
