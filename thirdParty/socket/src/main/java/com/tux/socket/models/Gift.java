package com.tux.socket.models;

import android.support.annotation.Keep;
import android.support.annotation.NonNull;

import java.util.Locale;

/**
 * Created by dev22 on 11/25/17.
 * send gift
 */
@Keep
public class Gift extends Message {
    public static final String TYPE_GIFT = "GIFT";

    /**
     * create gift message
     *
     * @param senderId   user id of sender
     * @param receiverId receiver id
     */
    public Gift(@NonNull String giftId, String senderId, @NonNull String receiverId) {
        super(String.format(Locale.US, "%1$s", giftId), senderId, receiverId, TYPE_GIFT);
    }
}
