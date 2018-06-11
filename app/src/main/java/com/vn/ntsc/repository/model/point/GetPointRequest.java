package com.vn.ntsc.repository.model.point;

import com.google.gson.annotations.SerializedName;
import com.vn.ntsc.core.model.ServerRequest;

/**
 * Created by dev22 on 11/21/17.
 * request: {"api": "send_gift", "gift_id": "", "token": "", "rcv_id": "user id of receiver"}
 */
public class GetPointRequest extends ServerRequest {
    @SerializedName("gift_id")
    public final String giftId;
    @SerializedName("rcv_id")
    public final String receiveId;

    public GetPointRequest(String giftId, String token, String receiveId) {
        super("send_gift");
        this.giftId = giftId;
        this.token = token;
        this.receiveId = receiveId;
    }
}
