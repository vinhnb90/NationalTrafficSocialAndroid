package com.vn.ntsc.ui.chat.adapter.holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.vn.ntsc.R;
import com.vn.ntsc.repository.model.chat.ChatMessage;
import com.vn.ntsc.utils.Constants;
import com.vn.ntsc.utils.LogUtils;
import com.vn.ntsc.utils.Utils;
import com.vn.ntsc.widget.views.autolink.ClickPattern;
import com.vn.ntsc.widget.views.textview.EmoTextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;

/**
 * Created by ThoNh on 9/26/2017.
 */
public class MessageSendHolder extends BaseChatHolder<ChatMessage> {
    public static final String TAG = MessageSendHolder.class.getSimpleName();
    private final ClickPattern email;
    private final ClickPattern phone;
    private final ClickPattern weblink;


    @BindView(R.id.text_content)
    public
    EmoTextView mTvSend;

    @BindView(R.id.image_chat_error)
    ImageView imageChatError;

    @BindView(R.id.tv_send_massage_time)
    TextView tvTime;

    @BindView(R.id.tv_status_send_massage)
    TextView tvStatus;


    public MessageSendHolder(MessageOnEventListener mMessageOnEventListener, View itemView) {
        super(itemView, mMessageOnEventListener);

        email = new ClickPattern();
        phone = new ClickPattern();
        weblink = new ClickPattern();
    }

    @Override
    public void bindView(final ChatMessage message, final int position) {
        if (message.sendMesasgeStatus == ChatMessage.STATUS_ERROR) {
            imageChatError.setVisibility(View.VISIBLE);
        } else {
            imageChatError.setVisibility(View.GONE);
        }

        mTvSend.setText(message.mContent);

        SimpleDateFormat sdfDay = new SimpleDateFormat("hh:mm", Locale.getDefault());
        String currentTime = sdfDay.format(new Date(Utils.convertTimeToMilisecond(Utils.convertGMTtoLocale(message.mTimeStamp))));
        tvTime.setText(currentTime);

        LogUtils.e(TAG, "sendMesasgeStatus :" + message.sendMesasgeStatus);
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

        imageChatError.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMessageOnEventListener != null) {
                    mMessageOnEventListener.onItemClickSenMessageError(message.messageId, getAdapterPosition());
                }

            }
        });

        /**
         * set Functionality for what will happen on click of that pattern
         * In this example pattern is email
         */
        email.setOnClickListener(new ClickPattern.OnClickListener() {
            @Override
            public void onClickAutoLink(String s) {
                if (mMessageOnEventListener != null) {
                    mMessageOnEventListener.onItemClickEmailMessage(s);
                }
            }
        });

        /**
         * set Functionality for what will happen on click of that pattern
         * In this example pattern is phone
         */
        phone.setOnClickListener(new ClickPattern.OnClickListener() {
            @Override
            public void onClickAutoLink(String s) {
                if (mMessageOnEventListener != null) {
                    mMessageOnEventListener.onItemClickPhoneMessage(s);
                }
            }
        });

        /**
         * set Functionality for what will happen on click of that pattern
         * In this example pattern is weblink
         */
        weblink.setOnClickListener(new ClickPattern.OnClickListener() {
            @Override
            public void onClickAutoLink(String s) {
                if (mMessageOnEventListener != null) {
                    mMessageOnEventListener.onItemClickWebLinkMessage(s);
                }
            }
        });

        email.setRegex(Constants.EMAIL_PATTERN); // regex for email
        phone.setRegex(Constants.PHONE_PATTERN); // regex for phone number
        weblink.setRegex(Constants.WEB_URL);

        mTvSend.addClickPattern(Constants.EMAIL, email);
        mTvSend.addClickPattern(Constants.PHONE, phone);
        mTvSend.addClickPattern(Constants.WEBLINK, weblink);
    }

}
