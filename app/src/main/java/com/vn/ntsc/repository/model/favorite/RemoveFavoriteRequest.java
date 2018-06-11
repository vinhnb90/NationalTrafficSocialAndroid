package com.vn.ntsc.repository.model.favorite;

import com.google.gson.annotations.SerializedName;
import com.vn.ntsc.core.model.ServerRequest;

/**
 * Created by hnc on 11/08/2017.
 */

public class RemoveFavoriteRequest extends ServerRequest {

    @SerializedName("fav_id")
    private String userId;

    public RemoveFavoriteRequest(String token, String userId) {
        super("rmv_fav");
        this.token = token;
        this.userId = userId;
    }
}
