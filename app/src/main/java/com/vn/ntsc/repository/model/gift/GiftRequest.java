package com.vn.ntsc.repository.model.gift;

import com.google.gson.annotations.SerializedName;
import com.vn.ntsc.core.model.ServerRequest;

/**
 * Created by TuanPC on 10/26/2017.
 */
public class GiftRequest extends ServerRequest {

    @SerializedName("language")
    public String language;
    @SerializedName("skip")
    public int skip ;
    @SerializedName("take")
    public int take ;

    public GiftRequest(String language,int skip,int take) {
        super("get_all_gift");
         this.token = "";
        this.language=language;
        this.skip=skip;
        this.take=take;
    }
}
