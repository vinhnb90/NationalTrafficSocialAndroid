package com.vn.ntsc.ui.chat.adapter.holder.mediaMultiple;

import android.view.View;
import android.widget.TextView;

import com.vn.ntsc.R;
import com.vn.ntsc.repository.model.chat.ChatMessage;

import butterknife.BindView;

/**
 * Created by dev22 on 2/28/18.
 * just same as media5, i don't know why we split into 2 layout
 */
public class MediaMoreHolder extends Media5Holder {
    @BindView(R.id.item_timeline_text_view_more)
    TextView more;

    public MediaMoreHolder(int gender, String avatarUrl, View itemView, MessageOnEventListener mMessageOnEventListener) {
        super(gender, avatarUrl, itemView, mMessageOnEventListener);
    }

    @Override
    public void bindView(ChatMessage chatMessage, int position) {
        super.bindView(chatMessage, position);

        videoIcon5.setVisibility(View.GONE);
        more.setVisibility(View.VISIBLE);

        String textMore = (chatMessage.listFile.size() - 5) + "+";
        more.setText(textMore);
    }
}
