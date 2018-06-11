package com.vn.ntsc.repository.model.timeline;

import com.google.gson.annotations.SerializedName;
import com.vn.ntsc.core.model.ServerRequest;

/**
 * Created by nankai on 8/16/2017.
 */

public class DeleteBuzzRequest extends ServerRequest {

    /**
     * Buzz Id
     */
    @SerializedName("buzz_id")
    private String buzz_id;


    public DeleteBuzzRequest(String token, String buzz_id) {
        super("del_buzz");
        this.token = token;
        this.buzz_id = buzz_id;
    }
}
