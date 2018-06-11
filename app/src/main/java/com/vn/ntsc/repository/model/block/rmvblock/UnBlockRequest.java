package com.vn.ntsc.repository.model.block.rmvblock;

import com.google.gson.annotations.SerializedName;
import com.vn.ntsc.core.model.ServerRequest;

/**
 * Created by ThoNh on 9/22/2017.
 */

public class UnBlockRequest extends ServerRequest{

    @SerializedName("blk_user_id")
    public String userIdBlocked;

    public UnBlockRequest(String blockId, String token) {
        super("rmv_blk");
        this.userIdBlocked = blockId;
        this.token = token;
    }
}
