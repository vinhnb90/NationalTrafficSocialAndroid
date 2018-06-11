package com.vn.ntsc.repository.model.sticker;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ThoNh on 9/29/2017.
 */

public class StickerItem {
    /**
     * stk_id : 574eb475e4b0ccb75f393e2e
     * code : 100000
     * stk_url : http://10.64.100.22:81/sticker/sticker/100000.png
     */

    @SerializedName("stk_id")
    private String stkId;
    @SerializedName("code")
    private int code;
    @SerializedName("stk_url")
    private String stkUrl;

    public String getStkId() {
        return stkId;
    }

    public void setStkId(String stkId) {
        this.stkId = stkId;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getStkUrl() {
        return stkUrl;
    }

    public void setStkUrl(String stkUrl) {
        this.stkUrl = stkUrl;
    }
}
