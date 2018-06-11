package com.tux.socket.models;

import android.support.annotation.Keep;
import android.support.annotation.NonNull;

/**
 * Created by dev22 on 11/25/17.
 * send sticker
 */
@Keep
public class Sticker extends Message {
    public static final String TYPE_STICKER = "STK";

    /**
     * create sticker message
     *
     * @param senderId   user id of sender
     * @param receiverId receiver id
     */
    public Sticker(@NonNull String stickerCategory, @NonNull String stickerName, String senderId, @NonNull String receiverId, @NonNull String stickerUrl) {
        super(stickerUrl, stickerCategory, senderId, receiverId, TYPE_STICKER, stickerName);
    }
}
