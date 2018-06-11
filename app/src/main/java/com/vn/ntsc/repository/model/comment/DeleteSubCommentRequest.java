package com.vn.ntsc.repository.model.comment;

import com.google.gson.annotations.SerializedName;
import com.vn.ntsc.core.model.ServerRequest;

/**
 * Created by nankai on 8/24/2017.
 */

public class DeleteSubCommentRequest extends ServerRequest{

    @SerializedName("buzz_id")
    private String buzzId;
    @SerializedName("cmt_id")
    private String commentId;
    @SerializedName("sub_comment_id")
    private String subCommentId;


    public DeleteSubCommentRequest (String token, String buzzId, String cmtId, String subCommentId) {
        super("delete_sub_comment");
        this.token = token;
        this.buzzId = buzzId;
        this.commentId = cmtId;
        this.subCommentId = subCommentId;
    }
}
