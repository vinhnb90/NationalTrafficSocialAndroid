package com.vn.ntsc.repository.model.listpublicimage;

import com.google.gson.annotations.SerializedName;
import com.vn.ntsc.core.model.ServerRequest;

/**
 * Created by nankai on 8/29/2017.
 */

public class ListPublicImageRequest extends ServerRequest {

    @SerializedName("req_user_id")
    private String req_user_id;
    @SerializedName("skip")
    public int skip;
    @SerializedName("take")
    public int take;


    public ListPublicImageRequest(String token, int skip, int take) {
        super("lst_pbimg");
        this.token = token;
        this.req_user_id = null;
        this.skip = skip;
        this.take = take;
    }

    public ListPublicImageRequest(String token, String req_user_id, int skip, int take) {
        super("lst_pbimg");
        this.token = "";
        this.req_user_id = req_user_id;
        this.skip = skip;
        this.take = take;
    }
}
