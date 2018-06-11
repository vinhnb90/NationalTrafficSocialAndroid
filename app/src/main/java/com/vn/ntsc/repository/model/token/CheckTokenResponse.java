package com.vn.ntsc.repository.model.token;

import com.google.gson.annotations.SerializedName;
import com.vn.ntsc.core.model.ServerResponse;

/**
 * Created by ThoNh on 10/2/2017.
 */

public class CheckTokenResponse extends ServerResponse {

    @SerializedName("data")
    public NewToken data;


    public static class NewToken {
        @SerializedName("token")
        public String token;

        @SerializedName("code")
        public int code;
    }
}
