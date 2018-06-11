package com.vn.ntsc.ui.chat.adapter.holder;

import android.view.View;
import android.widget.TextView;

import com.vn.ntsc.R;
import com.vn.ntsc.repository.model.chat.ChatMessage;
import com.vn.ntsc.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;

/**
 * Created by ThoNh on 9/26/2017.
 */

public class HeaderHolder extends BaseChatHolder<ChatMessage> {

    @BindView(R.id.header)
    TextView mTvDate;


    public HeaderHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void bindView(ChatMessage message, int position) {
        SimpleDateFormat sdfDay = new SimpleDateFormat("EEE, MMM d, yyyy", Locale.getDefault());
        String a = sdfDay.format(new Date(Utils.convertTimeToMilisecond(Utils.convertGMTtoLocale(message.mTimeStamp))));
        mTvDate.setText(a);
    }


}
