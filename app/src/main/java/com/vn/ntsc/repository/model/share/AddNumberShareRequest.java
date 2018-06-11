package com.vn.ntsc.repository.model.share;

import com.google.gson.annotations.SerializedName;
import com.vn.ntsc.core.model.ServerRequest;

/**
 * Created by nankai on 11/24/2017.
 */

public class AddNumberShareRequest extends ServerRequest {

    @SerializedName("buzz_id")
    String buzzId;

    public AddNumberShareRequest(String buzz_id, String token) {
        super("add_number_of_share");
        this.buzzId = buzz_id;
        this.token = token;
    }
}
