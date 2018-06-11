package com.vn.ntsc.repository.model.conversation;

import com.google.gson.annotations.SerializedName;
import com.vn.ntsc.core.model.ServerRequest;

import java.util.List;

/**
 * Created by hnc on 25/08/2017.
 */

public class DelConversationRequest extends ServerRequest {

    @SerializedName("frd_id")
    public List<String> friendIds;

    public DelConversationRequest(List<String> friendsId, String token) {
        super("del_conversations");
        this.friendIds = friendsId;
        this.token = token;
    }
}
