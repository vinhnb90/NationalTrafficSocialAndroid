package com.vn.ntsc.repository.model.timeline;

import com.google.gson.annotations.SerializedName;

/**
 * Created by nankai on 12/28/2017.
 */

public class JoinBuzzResponse {
//    {"from":"5a094d3541afe76378298ad8","msg_type":"BUZZJOIN","buzz_id":"5a1d476141afe72454dc0cd5","code":0}
    @SerializedName("from")
    public String from;
    @SerializedName("msg_type")
    public String msgType;
    @SerializedName("buzz_id")
    public String buzzId;
    @SerializedName("code")
    public int code;
}
