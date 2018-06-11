package com.vn.ntsc.repository.model.timeline;

import com.google.gson.annotations.SerializedName;
import com.vn.ntsc.core.model.ServerRequest;

/**
 * Created by nankai on 8/9/2017.
 */

public class LikeBuzzRequest extends ServerRequest {

    @SerializedName("buzz_id")
    private String buzz_id;
    @SerializedName("like_type")
    private int like_type;


    public LikeBuzzRequest(String token, String buzz_id, int like_type) {
        super("like_buzz");
        this.token = token;
        this.buzz_id = buzz_id;
        this.like_type = like_type;
    }
}
