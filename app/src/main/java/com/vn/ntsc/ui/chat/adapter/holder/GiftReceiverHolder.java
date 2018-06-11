package com.vn.ntsc.ui.chat.adapter.holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.vn.ntsc.R;
import com.vn.ntsc.repository.model.chat.ChatMessage;
import com.vn.ntsc.repository.model.image.ImageRequest;
import com.vn.ntsc.ui.chat.adapter.ChatAdapter;
import com.vn.ntsc.utils.ImagesUtils;
import com.vn.ntsc.utils.LogUtils;
import com.vn.ntsc.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;

/**
 * Created by ThoNh on 9/26/2017.
 */

public class GiftReceiverHolder extends BaseChatHolder<ChatMessage> {
    public static final String TAG = GiftReceiverHolder.class.getSimpleName();
    @BindView(R.id.avatar)
    ImageView mAvatar;

    @BindView(R.id.img_receiver_gift)
    ImageView mImageGift;

    @BindView(R.id.text_receiver_gift)
    TextView mTvGift;

    @BindView(R.id.tv_time_gift_receiver)
    TextView tvTime;

    private ChatAdapter chatAdapter;

    public GiftReceiverHolder(int gender, MessageOnEventListener mMessageOnEventListener, String avatarUrl, View itemView, ChatAdapter chatAdapter) {
        super(itemView, mMessageOnEventListener);
        this.chatAdapter = chatAdapter;
        ImagesUtils.loadRoundedAvatar(avatarUrl, gender, mAvatar);
    }

    ///59f96bca41afe72fc05e7e02|Đào duy đức|cr7|0
    @Override
    public void bindView(ChatMessage message, int position) {
        LogUtils.d(TAG, "receivergift " + message.mContent);
        ImageRequest giftRequest = new ImageRequest(message.mContent, ImageRequest.GIFT);
        String url = giftRequest.toURL();
        ImagesUtils.loadImage(url, mImageGift);
        String s = String.format(itemView.getContext().getString(R.string.chat_massage_receiver_gift), this.chatAdapter.getUserName());
        mTvGift.setText(s);

        // set current time
        SimpleDateFormat sdfDay = new SimpleDateFormat("hh:mm", Locale.getDefault());
        String currenTime = sdfDay.format(new Date(Utils.convertTimeToMilisecond(Utils.convertGMTtoLocale(message.mTimeStamp))));
        tvTime.setText(currenTime);

    }
}
