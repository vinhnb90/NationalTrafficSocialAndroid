package com.vn.ntsc.repository.model.forgotpassword;

import com.vn.ntsc.core.model.ServerRequest;

/**
 * Created by dev22 on 8/22/17.
 */
public class ForgotPasswordRequest extends ServerRequest {
    /**
     * email for sent request forgot password
     */
    String email;

    /**
     * currently it is empty
     */
    String ip;

    public ForgotPasswordRequest(String email) {
        super("fgt_pwd");
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
}
