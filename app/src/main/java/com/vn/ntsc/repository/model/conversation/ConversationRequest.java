package com.vn.ntsc.repository.model.conversation;

import com.google.gson.annotations.SerializedName;
import com.vn.ntsc.core.model.ServerRequest;

/**
 * Created by ThoNH on 25/08/2017.
 */

public class ConversationRequest extends ServerRequest {

    @SerializedName("time_stamp")
    public String timeStamp;
    @SerializedName("take")
    public int take;

    public ConversationRequest(String timeStamp, String token,int take) {
        super("list_conversation");
        this.timeStamp = timeStamp;
        this.token = token;
        this.take = take;
    }
}
