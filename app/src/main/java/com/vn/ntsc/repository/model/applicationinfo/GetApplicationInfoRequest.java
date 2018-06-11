package com.vn.ntsc.repository.model.applicationinfo;

import com.vn.ntsc.core.model.ServerRequest;


/**
 * Created by nankai on 11/28/2016.
 */

public class GetApplicationInfoRequest extends ServerRequest {

    public GetApplicationInfoRequest() {
        super("get_inf_for_application");
    }
}
