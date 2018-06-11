package com.vn.ntsc.repository.model.block.addblock;

import com.google.gson.annotations.SerializedName;
import com.vn.ntsc.core.model.ServerRequest;

/**
 * Created by nankai on 9/18/2017.
 */

public class AddBlockUserRequest extends ServerRequest {

    @SerializedName("req_user_id")
    private String req_user_id;


    public AddBlockUserRequest(String token, String req_user_id) {
        super("add_blk");
        this.token = token;
        this.req_user_id = req_user_id;
    }
}
