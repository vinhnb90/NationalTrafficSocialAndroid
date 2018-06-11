package com.tux.socket.models;

import android.support.annotation.Keep;

/**
 * send text
 */
@Keep
public class Text extends Message {
    public static final String TYPE_TEXT = "PP";
    public static final String TYPE_TEXT_EMOJI = "PE";

    /**
     * create message object to send text
     *
     * @param senderId   sender
     * @param receiverId receiver
     * @param text       content
     */
    public Text(String senderId, String receiverId, String text, String type) {
        super(text, senderId, receiverId, type);
    }
}
