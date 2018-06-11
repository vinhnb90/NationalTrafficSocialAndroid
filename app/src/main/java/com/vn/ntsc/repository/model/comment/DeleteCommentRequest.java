package com.vn.ntsc.repository.model.comment;

import com.google.gson.annotations.SerializedName;
import com.vn.ntsc.core.model.ServerRequest;

/**
 * Created by nankai on 8/23/2017.
 */

public class DeleteCommentRequest extends ServerRequest {

    @SerializedName("buzz_id")
    private String buzzId;

    @SerializedName("cmt_id")
    private String commentId;



    public DeleteCommentRequest(String token, String buzzId, String commentId) {
        super("del_cmt");
        this.token = token;
        this.buzzId = buzzId;
        this.commentId = commentId;
    }
}
