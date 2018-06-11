package com.vn.ntsc.repository.model.comment;

import com.google.gson.annotations.SerializedName;
import com.vn.ntsc.core.model.ServerRequest;

/**
 * Created by nankai on 4/23/2018.
 */

public class CommentDetailRequest extends ServerRequest {

    @SerializedName("cmt_id")
    private String commentId;
    @SerializedName("buzz_id")
    private String buzzId;

    public CommentDetailRequest(String token, String buzzId, String commentId) {
        super("comment_detail");
        this.token = token;
        this.commentId = commentId;
        this.buzzId = buzzId;
    }
}
