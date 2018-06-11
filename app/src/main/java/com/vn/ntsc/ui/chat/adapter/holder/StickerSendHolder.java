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

public class StickerSendHolder extends BaseChatHolder<ChatMessage> {
    private static final String TAG = StickerSendHolder.class.getSimpleName();
    @BindView(R.id.view_send_sticker)
    ImageView mSticker;

    @BindView(R.id.tv_time_send_sticker)
    TextView tvTime;

    @BindView(R.id.tv_status_send_sticker)
    TextView tvStatus;

    @BindView(R.id.image_chat_error)
    ImageView imageChatError;

    public StickerSendHolder(MessageOnEventListener mMessageOnEventListener, View itemView) {
        super(itemView, mMessageOnEventListener);
    }

    @Override
    public void bindView(final ChatMessage message, int position) {
        if (message.sendMesasgeStatus == ChatMessage.STATUS_ERROR) {
            imageChatError.setVisibility(View.VISIBLE);
        } else {
            imageChatError.setVisibility(View.GONE);
        }
        imageChatError.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMessageOnEventListener != null) {
                    mMessageOnEventListener.onItemClickSenMessageError(message.messageId, getAdapterPosition());
                }

            }
        });

        SimpleDateFormat sdfDay = new SimpleDateFormat("hh:mm", Locale.getDefault());
        String currenTime = sdfDay.format(new Date(Utils.convertTimeToMilisecond(Utils.convertGMTtoLocale(message.mTimeStamp))));
        tvTime.setText(currenTime);

        ImagesUtils.loadSticker(message.stickerUrl, mSticker);

        setMessageStatus(tvStatus, message);

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
    }
}
