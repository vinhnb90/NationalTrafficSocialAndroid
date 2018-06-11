package com.vn.ntsc.repository.model.comment;

import com.google.gson.annotations.SerializedName;
import com.vn.ntsc.core.model.ServerRequest;

/**
 * Created by nankai on 8/23/2017.
 */

public class AddCommentRequest extends ServerRequest {



    @SerializedName("buzz_id")
    private String buzz_id;

    @SerializedName("cmt_val")
    private String cmt_val;

    @SerializedName("msg_type")
    private String msgType;

    public AddCommentRequest(String token, String buzz_id, String cmt_val) {
        super();
        this.msgType = "BUZZCMT";
        this.token = token;
        this.buzz_id = buzz_id;
        this.cmt_val = cmt_val;
    }
}
