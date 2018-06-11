package com.vn.ntsc.repository.model.user;

import com.vn.ntsc.core.model.ServerRequest;

/**
 * Created by nankai on 8/3/2017.
 */

public class SetCareUserInfoRequest extends ServerRequest {

    private String crea_id;
    private String crea_pass;

    public SetCareUserInfoRequest(String token, String crea_id, String crea_pass) {
        super("set_crea_info");
        this.token = token;
        this.crea_id = crea_id;
        this.crea_pass = crea_pass;
    }
}
