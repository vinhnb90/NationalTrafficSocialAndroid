package com.vn.ntsc.repository.model.timeline;

import com.google.gson.annotations.SerializedName;
import com.vn.ntsc.core.model.ServerRequest;

/**
 * Created by nankai on 8/10/2017.
 */

public class BuzzDetailRequest extends ServerRequest {


    @SerializedName("buzz_id")
    public String buzzId;
    @SerializedName("take")
    private int take;
    @SerializedName("skip")
    private int skip;

    public BuzzDetailRequest(String token, String buzzID) {
        super("get_buzz_detail");
        this.take = 0;
        this.skip = 0;
        if (token != null && !token.equals(""))
            this.token = token;
        this.buzzId = buzzID;
    }

    public BuzzDetailRequest(String token, String buzzID, int take, int skip) {
        super("get_buzz_detail");
        this.take = take;
        this.skip = skip;
        if (token != null && !token.equals(""))
            this.token = token;
        this.buzzId = buzzID;
    }
}
