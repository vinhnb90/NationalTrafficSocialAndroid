package com.vn.ntsc.repository.model.onlinealert;

import com.google.gson.annotations.SerializedName;
import com.vn.ntsc.core.model.ServerRequest;

/**
 * Created by nankai on 9/19/2017.
 */

public class AddOnlineAlertRequest extends ServerRequest {

    @SerializedName("req_user_id")
    private String reqUserId;
    @SerializedName("is_alt")
    private int isAlert;
    @SerializedName("alt_fre")
    private int num;


    public AddOnlineAlertRequest(String token, String reqUserId, int isAlert, int num) {
        super("add_onl_alt");
        this.token = token;
        this.reqUserId = reqUserId;
        this.isAlert = isAlert;
        this.num = num;
    }
}
