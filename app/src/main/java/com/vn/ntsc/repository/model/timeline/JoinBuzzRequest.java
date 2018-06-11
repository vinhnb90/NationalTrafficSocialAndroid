package com.vn.ntsc.repository.model.timeline;

import com.google.gson.annotations.SerializedName;
import com.vn.ntsc.core.model.ServerRequest;

/**
 * Created by nankai on 12/28/2017.
 */

public class JoinBuzzRequest extends ServerRequest {

    //    {"from":"5a094d3541afe76378298ad8","msg_type":"BUZZJOIN","buzz_id":"5a1d476141afe72454dc0cd5","token":"23d42451-c553-4540-9976-39f096a01ef5"}
    @SerializedName("from")
    private String from;
    @SerializedName("msg_type")
    private String msgType;
    @SerializedName("buzz_id")
    private String buzzId;


    public JoinBuzzRequest(String from, String buzzId, String token) {
        this.from = from;
        this.msgType = "BUZZJOIN";
        this.buzzId = buzzId;
        this.token = token;
    }

    @Override
    public String toString() {
        return "{\"from\":\"" + from + "\",\"msg_type\":\"" + msgType + "\",\"buzz_id\":\"" + buzzId + "\",\"token\":\"" + token + "\"}";
    }
}
