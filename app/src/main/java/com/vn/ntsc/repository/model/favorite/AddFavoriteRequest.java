package com.vn.ntsc.repository.model.favorite;

import com.google.gson.annotations.SerializedName;
import com.vn.ntsc.core.model.ServerRequest;

/**
 * Created by nankai on 8/11/2017.
 */

public class AddFavoriteRequest extends ServerRequest {

    @SerializedName("req_user_id")
    private String userId;


    public AddFavoriteRequest(String token, String userId) {
        super("add_fav");
        this.token = token;
        this.userId = userId;
    }
}

