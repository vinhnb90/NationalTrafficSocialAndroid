package com.vn.ntsc.repository.model.chat;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by ThoNh on 9/27/2017.
 */

public class ChatHistoryResponse {

    @SerializedName("data")
    public List<ChatMessage> mData;
}
