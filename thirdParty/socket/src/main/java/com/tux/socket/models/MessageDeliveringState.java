package com.tux.socket.models;

import android.support.annotation.Keep;
import android.support.annotation.NonNull;

/**
 * Created by dev22 on 11/27/17.
 * mark as read message
 */
@Keep
public class MessageDeliveringState extends Message {
    public static final String TYPE_DELIVERING_STATE = "MDS";
    /**
     * to send read all message
     */
    public static final String VALUE_MARK_MESSAGE_AS_READ = "rd_all";
    /**
     * when send message server will return a message {msg_type: MDS, value=st...} to notify message delivered
     */
    public static final String VALUE_SENT_MESSAGE_SUCCESS = "st";
    /**
     * when have more than one user login into same account, if 1 of them send message
     * <p>-> server will send 1 message value {@link #VALUE_SENT_MESSAGE_SUCCESS st} and many to other user value {@link #VALUE_SENT_MESSAGE_SUCCESS_SAME_ACCOUNT ost}</p>
     * <pre>
     *     example: user1, user2 -> login account aaaa@aaa.com
     *     user1 -> send "hello guy"
     *     server return:
     *      - to user1: {msg_type: MDS, value=st...}
     *      - to user2: {msg_type: MDS, value=ost, message_content: 'hello guy'}
     * </pre>
     *
     * @see #VALUE_SENT_MESSAGE_SUCCESS
     */
    public static final String VALUE_SENT_MESSAGE_SUCCESS_SAME_ACCOUNT = "ost";

    public MessageDeliveringState(String senderId, @NonNull String receiverId) {
        super(VALUE_MARK_MESSAGE_AS_READ, senderId, receiverId, TYPE_DELIVERING_STATE);
    }
}
