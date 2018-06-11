package com.vn.ntsc.repository.model.gift;

import okhttp3.ResponseBody;

/**
 * Created by TuanPC on 10/31/2017.
 */
public class PairGiftResponseBody {
    ResponseBody responseBody;
    Gift gift;

    public PairGiftResponseBody(ResponseBody responseBody, Gift gift) {
        this.responseBody = responseBody;
        this.gift = gift;
    }

    public ResponseBody getResponseBody() {
        return responseBody;
    }

    public Gift getGift() {
        return gift;
    }
}
