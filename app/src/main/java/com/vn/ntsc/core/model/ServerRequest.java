package com.vn.ntsc.core.model;

import com.google.gson.annotations.SerializedName;

public class ServerRequest {

    @SerializedName("api")
    public String api;
    @SerializedName("token")
    public String token;

    public ServerRequest(String api) {
        this.api = api;
    }

    public ServerRequest() {
    }
}
