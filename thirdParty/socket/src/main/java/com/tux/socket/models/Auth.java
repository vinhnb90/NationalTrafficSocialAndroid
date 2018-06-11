package com.tux.socket.models;

import android.support.annotation.Keep;

/**
 * Created by dev22 on 11/22/17.
 * message for auth user
 */
@Keep
public class Auth extends Message {
    public static final String TYPE_AUTH = "AUTH";
    public static final String VALUE_AUTH_SUCCESS = "s";
    public static final String VALUE_AUTH_FAIL = "f";

    /**
     * create message auth, account: {token , user_id}
     *
     * @param token  user token
     * @param userId user id
     */
    public Auth(String token, String userId) {
        super(token, userId, Message.TO_SERVER, TYPE_AUTH);
    }
}
