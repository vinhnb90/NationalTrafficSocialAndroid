package com.vn.ntsc.repository.model.search.byname;

import com.google.gson.annotations.SerializedName;
import com.vn.ntsc.core.model.ServerRequest;

/**
 * Created by hnc on 23/08/2017.
 */

public class SearchByNameRequest extends ServerRequest {

    @SerializedName("user_name")
    public String mUserName;
    @SerializedName("skip")
    public int mSkip = 0;
    @SerializedName("take")
    public int mTake = 20;

    public SearchByNameRequest(String userName, String token, int skip) {
        super("search_by_name");
        this.mUserName = userName;
        this.token = token;
        this.mSkip = skip;
    }
}
