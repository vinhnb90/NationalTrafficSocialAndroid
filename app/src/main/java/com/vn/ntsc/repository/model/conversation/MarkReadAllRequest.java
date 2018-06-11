package com.vn.ntsc.repository.model.conversation;

import com.google.gson.annotations.SerializedName;
import com.vn.ntsc.core.model.ServerRequest;

import java.util.List;

/**
 * Created by ThoNh on 8/30/2017.
 */

/*
     sample request
{"frd_id":["5913e8b0e4b026875db0f29a","58ddc955e4b03e06859c2452","577232350cf25a8a3c869f91"],"api":"mark_reads","token":"f28132d6-b10d-4509-9e67-d894cad317fe"}
*/
public class MarkReadAllRequest extends ServerRequest {

    @SerializedName("frd_id")
    List<String> friendsId;

    public MarkReadAllRequest(List<String> friendsId, String token) {
        super("mark_reads");
        this.friendsId = friendsId;
        this.token = token;
    }
}
