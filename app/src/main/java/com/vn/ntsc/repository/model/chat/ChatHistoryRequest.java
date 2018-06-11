package com.vn.ntsc.repository.model.chat;

import com.google.gson.annotations.SerializedName;
import com.vn.ntsc.core.model.ServerRequest;

/**
 * Created by ThoNh on 9/27/2017.
 */

public class ChatHistoryRequest extends ServerRequest {

    @SerializedName("frd_id")
    public String mFriendId;

    @SerializedName("take")
    public int mTake;

    @SerializedName("time_stamp")
    public String mTimeStamp;

    public ChatHistoryRequest(String friendId, int take , String timeStamp, String token) {
        super("get_chat_history");
        this.mFriendId = friendId;
        this.mTimeStamp = timeStamp;
        this.mTake = take;
        this.token = token;
    }
}
