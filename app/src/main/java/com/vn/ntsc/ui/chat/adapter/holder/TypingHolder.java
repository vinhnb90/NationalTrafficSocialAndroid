package com.vn.ntsc.ui.chat.adapter.holder;

import android.view.View;

import com.nankai.designlayout.typing.LoadingDots;
import com.vn.ntsc.R;
import com.vn.ntsc.repository.model.chat.ChatMessage;

import butterknife.BindView;

/**
 * Created by ThoNh on 9/26/2017.
 */

public class TypingHolder extends BaseChatHolder<ChatMessage> {
    @BindView(R.id.tv_typing)
    LoadingDots mTvTyping;

    public TypingHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void bindView(ChatMessage message, int position) {
        if (message.isTyping) {
            mTvTyping.startAnimation();
            mTvTyping.setVisibility(View.VISIBLE);
        } else {
            mTvTyping.stopAnimation();
            mTvTyping.setVisibility(View.GONE);
        }

    }
}
