package com.vn.ntsc.repository.model.comment;

import com.google.gson.annotations.SerializedName;
import com.vn.ntsc.core.model.ServerRequest;

/**
 * Created by nankai on 8/24/2017.
 */

public class AddSubCommentRequest extends ServerRequest {

    @SerializedName("cmt_id")
    private String commentId;
    @SerializedName("cmt_val")
    private String value;
    @SerializedName("buzz_id")
    private String buzzId;

    @SerializedName("msg_type")
    private String msgType;

    public AddSubCommentRequest(String token, String commentId, String value, String buzzId) {
        super("add_sub_comment");
        this.token = token;
        this.commentId = commentId;
        this.value = value;
        this.buzzId = buzzId;
        this.msgType = "BUZZSUBCMT";
    }
}