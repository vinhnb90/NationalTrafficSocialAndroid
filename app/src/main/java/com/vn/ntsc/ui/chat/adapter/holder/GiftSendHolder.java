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

public class GiftSendHolder extends BaseChatHolder<ChatMessage> {
    public static final String TAG = GiftSendHolder.class.getSimpleName();

    @BindView(R.id.view_send_gift)
    ImageView mImageGitf;

    @BindView(R.id.text_send_gift)
    TextView mTvGift;

    @BindView(R.id.tv_time_gift_send)
    TextView tvTime;

    @BindView(R.id.tv_status_send_gift)
    TextView tvStatus;

    private String giftFomat;

    private ChatAdapter chatAdapter;

    public GiftSendHolder(MessageOnEventListener mMessageOnEventListener, View itemView, ChatAdapter chatAdapter) {
        super(itemView, mMessageOnEventListener);
        this.chatAdapter = chatAdapter;
    }

    // 59f96bf241afe72fc05e7e04|cr7|Đào duy đức|0
    @Override
    public void bindView(ChatMessage message, int position) {
        LogUtils.d(TAG, "GiftSendHolder " + message.mContent);
        giftFomat = String.format(itemView.getContext().getString(R.string.chat_massage_send_gift), this.chatAdapter.getUserName());
        imageRequest(message.mContent);
        mTvGift.setText(giftFomat);

        // set time send
        SimpleDateFormat sdfDay = new SimpleDateFormat("hh:mm", Locale.getDefault());
        String currenTime = sdfDay.format(new Date(Utils.convertTimeToMilisecond(Utils.convertGMTtoLocale(message.mTimeStamp))));
        tvTime.setText(currenTime);

//        switch (message.sendMesasgeStatus) {
//            case ChatMessage.STATUS_ERROR:
//                tvStatus.setText(R.string.common_error);
//                break;
//            case ChatMessage.STATUS_SEEN:
//                tvStatus.setText(R.string.seen);
//                break;
//            case ChatMessage.STATUS_SENDING:
//                tvStatus.setText(R.string.sending);
//                break;
//            case ChatMessage.STATUS_SUCCESS:
//                tvStatus.setText(R.string.sent);
//                break;
//            default:
//                break;
//        }
        setMessageStatus(tvStatus, message);
    }

    /**
     * load image url
     *
     * @param content
     */
    private void imageRequest(String content) {
        ImageRequest giftRequest = new ImageRequest(content, ImageRequest.GIFT);
        String url = giftRequest.toURL();
        ImagesUtils.loadImage(url, mImageGitf);

    }
}
