package com.vn.ntsc.ui.chat.adapter.holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.vn.ntsc.R;
import com.vn.ntsc.repository.model.chat.ChatMessage;
import com.vn.ntsc.utils.ImagesUtils;
import com.vn.ntsc.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;

/**
 * Created by ThoNh on 9/26/2017.
 */

public class StickerReceiverHolder extends BaseChatHolder<ChatMessage> {
    private static final String TAG = StickerReceiverHolder.class.getSimpleName();

    @BindView(R.id.avatar)
    ImageView mAvatar;

    @BindView(R.id.view_sticker)
    ImageView mSticker;

    @BindView(R.id.tv_time_receiver_sticker)
    TextView tvTime;


    public StickerReceiverHolder(int gender, MessageOnEventListener mMessageOnEventListener, String avatarUrl, View itemView) {
        super(itemView, mMessageOnEventListener);
        ImagesUtils.loadRoundedAvatar(avatarUrl, gender, mAvatar);
    }

    @Override
    public void bindView(ChatMessage message, int position) {


        SimpleDateFormat sdfDay = new SimpleDateFormat("hh:mm", Locale.getDefault());
        String currenTime = sdfDay.format(new Date(Utils.convertTimeToMilisecond(Utils.convertGMTtoLocale(message.mTimeStamp))));
        tvTime.setText(currenTime);
        ImagesUtils.loadSticker(message.stickerUrl, mSticker);
    }
}
