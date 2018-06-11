package com.tux.socket.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by nankai on 12/29/2017.
 */

public class Ping {
    public static final String TYPE_PONG = "PONG";
    public static final String TYPE_PING = "PING";

    @SerializedName("msg_type")
    private String msgType;
    @SerializedName("from")
    private String from;
    @SerializedName("token")
    public String token;

    public Ping(String from, String token) {
        this.msgType = TYPE_PONG;
        this.from = from;
        this.token = token;
    }

    public String toJson() {
        return "{\"msg_type\":\"" + msgType + "\",\"from\":\"" + from + "\",\"token\":\"" + token + "\"}";
    }
}
