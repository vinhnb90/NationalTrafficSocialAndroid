package com.vn.ntsc.repository.model.token;

import com.vn.ntsc.core.model.ServerRequest;

/**
 * Created by ThoNh on 10/2/2017.
 */

public class CheckTokenRequest extends ServerRequest {

    public CheckTokenRequest(String token) {
        super("check_token");
        this.token = token;
    }
}
