package com.vn.ntsc.ui.chat.adapter.holder;

import com.vn.ntsc.repository.model.chat.ChatMessage;

import java.util.List;

/**
 * Created by ThoNh on 9/26/2017.
 */

public interface IDataChat {
    void setData(List<ChatMessage> data);

    void setData(int index, List<ChatMessage> data);

    void insertData(ChatMessage chatMessage);

    ChatMessage getData(int position);

    List<ChatMessage> getData();
}
