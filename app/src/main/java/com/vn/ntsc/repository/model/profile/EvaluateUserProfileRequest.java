package com.vn.ntsc.repository.model.profile;

import com.google.gson.annotations.SerializedName;
import com.vn.ntsc.core.model.ServerRequest;

/**
 * Created by ducng on 11/8/2017.
 */

public class EvaluateUserProfileRequest extends ServerRequest {
    /**
     * api : rate_user_voice
     * token : 6b48c315-7779-4522-960e-8dc9e1e6833e
     * req_user_id : 57721e930cf2eb6d9632e6cf
     * rate_value : 2
     */
    @SerializedName("req_user_id")
    private String reqUserId;
    @SerializedName("rate_value")
    private int rateValue;

    public EvaluateUserProfileRequest(String token, String reqUserId, int rateValue) {
        this.api = "rate_user_voice";
        this.token = token;
        this.reqUserId = reqUserId;
        this.rateValue = rateValue;
    }
}
