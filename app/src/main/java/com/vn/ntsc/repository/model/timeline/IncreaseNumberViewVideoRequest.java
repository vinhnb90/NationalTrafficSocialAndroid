package com.vn.ntsc.repository.model.timeline;

import com.google.gson.annotations.SerializedName;
import com.vn.ntsc.core.model.ServerRequest;

/**
 * Created by ThoNh on 3/7/2018.
 */

public class IncreaseNumberViewVideoRequest extends ServerRequest {

    @SerializedName("buzz_id")
    public String buzzId;

    public IncreaseNumberViewVideoRequest(String token, String buzzId) {
        super("add_number_of_view");
        this.token = token;
        this.buzzId = buzzId;
    }
}
