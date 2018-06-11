package com.vn.ntsc.repository.model.comment;

import com.google.gson.annotations.SerializedName;
import com.vn.ntsc.core.model.ServerRequest;

/**
 * Created by nankai on 8/21/2017.
 */

public class ListSubCommentRequest extends ServerRequest {


    @SerializedName("buzz_id")
    public String buzzId;
    @SerializedName("cmt_id")
    public String commentId;
    @SerializedName("skip")
    public int skip;
    @SerializedName("take")
    public int take;
    @SerializedName("sort_by")
    public int sortBy;

    public ListSubCommentRequest(String token, String buzzId, String commentId, int skip, int take) {
        super("list_sub_comment");
        if (token != null && !token.equals(""))
            this.token = token;
        this.commentId = commentId;
        this.buzzId = buzzId;
        this.skip = skip;
        this.take = take;
        this.sortBy = -1;
    }
}
