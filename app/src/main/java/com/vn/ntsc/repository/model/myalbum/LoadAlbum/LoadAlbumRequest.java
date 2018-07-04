package com.vn.ntsc.repository.model.myalbum.LoadAlbum;

import com.google.gson.annotations.SerializedName;
import com.vn.ntsc.core.model.ServerRequest;

/**
 * Created by ThoNh on 11/14/2017.
 */

public class LoadAlbumRequest extends ServerRequest {


    /**
     * token : 2661b09e-4ed6-435e-bdea-baf929051727
     * req_user_id : 57721e930cf2eb6d9632e6cf
     * skip : 0
     * take : 9
     */
    @SerializedName("req_user_id")
    public String reqUserId;
    @SerializedName("skip")
    public int skip = 0;
    @SerializedName("take")
    public int take;

    public LoadAlbumRequest(String token, String reqUserId, int skip, int take) {
        super("load_album");
        this.token = token;
        this.reqUserId = reqUserId;
        this.skip = skip;
        this.take = take;
    }


}
