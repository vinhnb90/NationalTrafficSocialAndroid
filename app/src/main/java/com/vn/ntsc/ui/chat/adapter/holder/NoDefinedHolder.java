package com.vn.ntsc.ui.chat.adapter.holder;

import android.view.View;

import com.vn.ntsc.repository.model.chat.ChatMessage;

/**
 * Created by ThoNh on 9/26/2017.
 */

public class NoDefinedHolder extends BaseChatHolder<ChatMessage> {

    public NoDefinedHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void bindView(ChatMessage message, int position) {

    }
}
