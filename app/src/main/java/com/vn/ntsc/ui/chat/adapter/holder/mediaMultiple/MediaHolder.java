package com.vn.ntsc.ui.chat.adapter.holder.mediaMultiple;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.vn.ntsc.R;
import com.vn.ntsc.repository.model.chat.ChatMessage;
import com.vn.ntsc.ui.chat.adapter.holder.BaseChatHolder;
import com.vn.ntsc.utils.ImagesUtils;
import com.vn.ntsc.utils.LogUtils;
import com.vn.ntsc.utils.Utils;
import com.vn.ntsc.widget.views.images.RecyclingImageView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;

/**
 * Created by dev22 on 2/27/18.
 * base class for media
 */
public class MediaHolder extends BaseChatHolder<ChatMessage> {
    public static final String TAG = MediaHolder.class.getSimpleName();
    private final SimpleDateFormat sdfDay;
    private String avaterUrl;
    @BindView(R.id.tv_time)
    TextView tvTime;

    @BindView(R.id.tv_status)
    TextView tvStatus;

    @BindView(R.id.image_chat_error)
    ImageView errorIcon;

    @BindView(R.id.avatar_receive)
    ImageView mAvatar;

    private int mGender;

    MediaHolder(int gender, String avatarUrl, View itemView, MessageOnEventListener mMessageOnEventListener) {
        super(itemView, mMessageOnEventListener);
        sdfDay = new SimpleDateFormat("hh:mm", Locale.getDefault());
        this.avaterUrl = avatarUrl;
        this.mGender = gender;


    }

    @Override
    public void bindView(final ChatMessage message, final int position) {
        // TODO: 2/27/18 still wrong but i don't have time now

        if (message.mTimeStamp != null) {
            String currentTime = sdfDay.format(new Date(Utils.convertTimeToMilisecond(Utils.convertGMTtoLocale(message.mTimeStamp))));
            tvTime.setText(currentTime);
        }

        LogUtils.e(TAG, "mTimeStamp : " + message.mTimeStamp);

        if (message.isOwn) {
            mAvatar.setVisibility(View.GONE);
            setMessageStatus(tvStatus, message);
        } else {
            mAvatar.setVisibility(View.VISIBLE);
            ImagesUtils.loadRoundedAvatar(avaterUrl, mGender, mAvatar);
        }

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
//            case ChatMessage.STATUS_UNSET:
//                break;
//        }


        errorIcon.setVisibility(message.sendMesasgeStatus == ChatMessage.STATUS_ERROR ? View.VISIBLE : View.GONE);
        errorIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMessageOnEventListener.onItemClickSenMessageError(message.messageId, getAdapterPosition());
            }
        });
    }

    void setOnClickMedia(final RecyclingImageView image, final ChatMessage chatMessage, final int indexInListMedia) {
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMessageOnEventListener.onClickMedia(chatMessage, indexInListMedia);
            }
        });
    }
}
