package com.vn.ntsc.repository.model.comment;

import com.google.gson.annotations.SerializedName;
import com.vn.ntsc.core.model.ServerRequest;

/**
 * Created by nankai on 8/23/2017.
 */

public class ListCommentRequest extends ServerRequest {

    @SerializedName("buzz_id")
    public String buzz_id;
    @SerializedName("skip")
    public int skip;
    @SerializedName("take")
    public int take;

    @SerializedName("sort_by")
    public int sortBy; //Invert the list -1 vs 1

    public ListCommentRequest(String token, String buzz_id, int skip, int take,int sortBy) {
        super("lst_cmt");
        this.token = token;
        this.buzz_id = buzz_id;
        this.skip = skip;
        this.take = take;
        this.sortBy = sortBy;
    }

    public ListCommentRequest(String token, String buzz_id, int skip, int take) {
        super("lst_cmt");
        this.token = token;
        this.buzz_id = buzz_id;
        this.skip = skip;
        this.take = take;
        this.sortBy = -1;
    }
}
