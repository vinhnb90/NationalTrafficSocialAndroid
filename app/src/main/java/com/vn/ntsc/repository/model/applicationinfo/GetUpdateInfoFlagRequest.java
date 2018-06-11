package com.vn.ntsc.repository.model.applicationinfo;

import com.vn.ntsc.core.model.ServerRequest;

/**
 * Created by nankai on 8/3/2017.
 */

public class GetUpdateInfoFlagRequest extends ServerRequest {

    public GetUpdateInfoFlagRequest(String token) {
        super("get_update_info_flag");
        this.token = token;
    }
}