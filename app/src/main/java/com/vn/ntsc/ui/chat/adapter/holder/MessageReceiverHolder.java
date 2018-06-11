package com.vn.ntsc.ui.chat.adapter.holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.vn.ntsc.R;
import com.vn.ntsc.repository.model.chat.ChatMessage;
import com.vn.ntsc.utils.Constants;
import com.vn.ntsc.utils.ImagesUtils;
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

public class MessageReceiverHolder extends BaseChatHolder<ChatMessage> {
    public static final String TAG = MessageReceiverHolder.class.getSimpleName();
    @BindView(R.id.avatar)
    ImageView mAvatar;

    @BindView(R.id.text_content)
    public
    EmoTextView mTvReceiver;

    @BindView(R.id.tv_receiver_massage_time)
    TextView tvTime;

    private final ClickPattern email;
    private final ClickPattern phone;
    private final ClickPattern webLink;


    public MessageReceiverHolder(int gender, MessageOnEventListener mMessageOnEventListener, String avatarUrl, View itemView) {
        super(itemView, mMessageOnEventListener);
        ImagesUtils.loadRoundedAvatar(avatarUrl, gender, mAvatar);

        email = new ClickPattern();
        phone = new ClickPattern();
        webLink = new ClickPattern();
    }

    @Override
    public void bindView(ChatMessage message, int position) {
        mTvReceiver.setText(message.mContent);


        SimpleDateFormat sdfDay = new SimpleDateFormat("hh:mm", Locale.getDefault());
        String currentTime = sdfDay.format(new Date(Utils.convertTimeToMilisecond(Utils.convertGMTtoLocale(message.mTimeStamp))));
        tvTime.setText(currentTime);

        /*
          set Functionality for what will happen on click of that pattern
          In this example pattern is email
         */
        email.setOnClickListener(new ClickPattern.OnClickListener() {
            @Override
            public void onClickAutoLink(String s) {
                if (mMessageOnEventListener != null) {
                    mMessageOnEventListener.onItemClickEmailMessage(s);
                }
            }
        });

        /*
          set Functionality for what will happen on click of that pattern
          In this example pattern is phone
         */
        phone.setOnClickListener(new ClickPattern.OnClickListener() {
            @Override
            public void onClickAutoLink(String s) {
                if (mMessageOnEventListener != null) {
                    mMessageOnEventListener.onItemClickPhoneMessage(s);
                }
            }
        });

        /*
          set Functionality for what will happen on click of that pattern
          In this example pattern is webLink
         */
        webLink.setOnClickListener(new ClickPattern.OnClickListener() {
            @Override
            public void onClickAutoLink(String s) {
                if (mMessageOnEventListener != null) {
                    mMessageOnEventListener.onItemClickWebLinkMessage(s);
                }
            }
        });

        email.setRegex(Constants.EMAIL_PATTERN); // regex for email
        phone.setRegex(Constants.PHONE_PATTERN); // regex for phone number
        webLink.setRegex(Constants.WEB_URL);

        mTvReceiver.addClickPattern(Constants.EMAIL, email);
        mTvReceiver.addClickPattern(Constants.PHONE, phone);
        mTvReceiver.addClickPattern(Constants.WEBLINK, webLink);
    }
}
