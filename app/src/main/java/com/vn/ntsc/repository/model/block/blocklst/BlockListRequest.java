package com.vn.ntsc.repository.model.block.blocklst;

import com.google.gson.annotations.SerializedName;
import com.vn.ntsc.core.model.ServerRequest;

/**
 * Created by ThoNh on 9/22/2017.
 */

public class BlockListRequest extends ServerRequest {

    public static final int TAKE = 24;

    @SerializedName("skip")
    public int skip;

    @SerializedName("take")
    public int take = TAKE;

    public BlockListRequest(int skip, String token) {
        super("lst_blk");
        this.skip = skip;
        this.token = token;
    }
}
