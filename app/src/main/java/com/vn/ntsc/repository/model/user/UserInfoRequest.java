package com.vn.ntsc.repository.model.user;

import com.google.gson.annotations.SerializedName;
import com.vn.ntsc.core.model.ServerRequest;

/**
 * Created by nankai on 8/29/2017.
 */

public class UserInfoRequest extends ServerRequest {

    @SerializedName("req_user_id")
    public String userid;


    /**
     * Get friend info
     * @param userid
     */
    public UserInfoRequest(String userid) {
        super("get_user_inf");
        this.userid = userid;
    }

    /**
     * Get yourself info
     * @param token
     * @param userId
     */
    public UserInfoRequest(String token, String userId) {
        super("get_user_inf");
        this.token = token;
        this.userid = userId;
    }
}
