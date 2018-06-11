package com.vn.ntsc.ui.chat.adapter;

import android.support.annotation.IntDef;

import static com.vn.ntsc.ui.chat.adapter.ChatMessageType.TYPE_AUDIO_RECEIVER;
import static com.vn.ntsc.ui.chat.adapter.ChatMessageType.TYPE_AUDIO_SEND;
import static com.vn.ntsc.ui.chat.adapter.ChatMessageType.TYPE_GIFT_RECEIVER;
import static com.vn.ntsc.ui.chat.adapter.ChatMessageType.TYPE_GIFT_SEND;
import static com.vn.ntsc.ui.chat.adapter.ChatMessageType.TYPE_HEADER;
import static com.vn.ntsc.ui.chat.adapter.ChatMessageType.TYPE_MEDIA1_RECEIVE;
import static com.vn.ntsc.ui.chat.adapter.ChatMessageType.TYPE_MEDIA1_SEND;
import static com.vn.ntsc.ui.chat.adapter.ChatMessageType.TYPE_MEDIA2_RECEIVE;
import static com.vn.ntsc.ui.chat.adapter.ChatMessageType.TYPE_MEDIA2_SEND;
import static com.vn.ntsc.ui.chat.adapter.ChatMessageType.TYPE_MEDIA3_RECEIVE;
import static com.vn.ntsc.ui.chat.adapter.ChatMessageType.TYPE_MEDIA3_SEND;
import static com.vn.ntsc.ui.chat.adapter.ChatMessageType.TYPE_MEDIA4_RECEIVE;
import static com.vn.ntsc.ui.chat.adapter.ChatMessageType.TYPE_MEDIA4_SEND;
import static com.vn.ntsc.ui.chat.adapter.ChatMessageType.TYPE_MEDIA5_RECEIVE;
import static com.vn.ntsc.ui.chat.adapter.ChatMessageType.TYPE_MEDIA5_SEND;
import static com.vn.ntsc.ui.chat.adapter.ChatMessageType.TYPE_MEDIA_MORE_RECEIVE;
import static com.vn.ntsc.ui.chat.adapter.ChatMessageType.TYPE_MEDIA_MORE_SEND;
import static com.vn.ntsc.ui.chat.adapter.ChatMessageType.TYPE_MESSAGE_RECEIVER;
import static com.vn.ntsc.ui.chat.adapter.ChatMessageType.TYPE_MESSAGE_SEND;
import static com.vn.ntsc.ui.chat.adapter.ChatMessageType.TYPE_NO_DEFINED;
import static com.vn.ntsc.ui.chat.adapter.ChatMessageType.TYPE_STICKER_RECEIVER;
import static com.vn.ntsc.ui.chat.adapter.ChatMessageType.TYPE_STICKER_SEND;
import static com.vn.ntsc.ui.chat.adapter.ChatMessageType.TYPE_TYPING;

/**
 * Created by ThoNh on 9/26/2017.
 */

@IntDef({TYPE_HEADER, TYPE_MEDIA1_SEND, TYPE_MEDIA2_SEND, TYPE_MEDIA3_SEND, TYPE_MEDIA4_SEND, TYPE_MEDIA5_SEND, TYPE_AUDIO_SEND, TYPE_AUDIO_RECEIVER,
        TYPE_GIFT_SEND, TYPE_GIFT_RECEIVER, TYPE_MESSAGE_SEND, TYPE_MESSAGE_RECEIVER, TYPE_STICKER_SEND, TYPE_MEDIA_MORE_RECEIVE, TYPE_MEDIA_MORE_SEND,
        TYPE_STICKER_RECEIVER, TYPE_MEDIA1_RECEIVE, TYPE_MEDIA2_RECEIVE, TYPE_MEDIA3_RECEIVE, TYPE_MEDIA4_RECEIVE, TYPE_MEDIA5_RECEIVE, TYPE_TYPING, TYPE_NO_DEFINED})
public @interface ChatMessageType {
    int TYPE_HEADER = 1;
    int TYPE_AUDIO_SEND = 4;
    int TYPE_AUDIO_RECEIVER = 5;
    int TYPE_GIFT_SEND = 6;
    int TYPE_GIFT_RECEIVER = 7;
    int TYPE_MESSAGE_SEND = 8;
    int TYPE_MESSAGE_RECEIVER = 9;
    int TYPE_STICKER_SEND = 10;
    int TYPE_STICKER_RECEIVER = 11;

    int TYPE_TYPING = 16;
    int TYPE_NO_DEFINED = 19;

    int TYPE_MEDIA1_SEND = 20;
    int TYPE_MEDIA2_SEND = 21;
    int TYPE_MEDIA3_SEND = 22;
    int TYPE_MEDIA4_SEND = 23;
    int TYPE_MEDIA5_SEND = 24;
    int TYPE_MEDIA_MORE_SEND = 30;

    int TYPE_MEDIA1_RECEIVE = 25;
    int TYPE_MEDIA2_RECEIVE = 26;
    int TYPE_MEDIA3_RECEIVE = 27;
    int TYPE_MEDIA4_RECEIVE = 28;
    int TYPE_MEDIA5_RECEIVE = 29;
    int TYPE_MEDIA_MORE_RECEIVE = 31;
}
