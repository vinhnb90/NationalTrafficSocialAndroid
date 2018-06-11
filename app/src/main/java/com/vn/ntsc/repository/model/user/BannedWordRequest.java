package com.vn.ntsc.repository.model.user;

import com.google.gson.annotations.SerializedName;
import com.vn.ntsc.core.model.ServerRequest;

public class BannedWordRequest extends ServerRequest {
    @SerializedName("version")
    private final int version;

    public BannedWordRequest(int version) {
        super("get_banned_word");

        this.version = version;
    }
}
