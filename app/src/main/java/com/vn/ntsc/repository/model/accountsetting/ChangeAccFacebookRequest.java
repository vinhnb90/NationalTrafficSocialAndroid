package com.vn.ntsc.repository.model.accountsetting;

import com.google.gson.annotations.SerializedName;
import com.vn.ntsc.core.model.ServerRequest;

/**
 * Created by Doremon on 2/22/2018.
 */

public class ChangeAccFacebookRequest extends ServerRequest {

    /**
     * token : a55d9391-d0cd-4439-a539-ef206437f7f5
     * api : create_account_from_fbid
     * fb_id : 267676613592498
     * email : phadfn5dsff4@gmail.com
     * pwd : 7c4a8d09ca3762af61e59520943dc26494f8941b
     * original_pwd : 123456
     */
    @SerializedName("fb_id")
    private String fbId;
    @SerializedName("email")
    private String email;
    @SerializedName("pwd")
    private String pwd;
    @SerializedName("original_pwd")
    private String originalPwd;

    public ChangeAccFacebookRequest(String token, String fbId, String email, String pwd, String originalPwd) {
        super("create_account_from_fbid");
        this.token = token;
        this.fbId = fbId;
        this.email = email;
        this.pwd = pwd;
        this.originalPwd = originalPwd;
    }
}
