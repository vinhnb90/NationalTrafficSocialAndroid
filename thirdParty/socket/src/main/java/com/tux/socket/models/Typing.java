package com.tux.socket.models;

import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by dev22 on 11/25/17.
 * send typing signal
 */
@Keep
public class Typing extends Message {
    public static final String TYPE_WRITING = "PRC";

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({START_TYPING, STOP_TYPING})
    @interface MessageValue {
    }

    /**
     * stop typing
     */
    public static final String STOP_TYPING = "sw";
    /**
     * start typing
     */
    public static final String START_TYPING = "wt";

    /**
     * create typing message
     *
     * @param senderId   user id of sender
     * @param receiverId user id of receiver
     */
    public Typing(@MessageValue String value, String senderId, @NonNull String receiverId) {
        super(value, senderId, receiverId, TYPE_WRITING);
    }
}
