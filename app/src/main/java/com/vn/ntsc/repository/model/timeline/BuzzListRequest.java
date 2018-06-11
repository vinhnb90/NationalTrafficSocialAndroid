package com.vn.ntsc.repository.model.timeline;

import com.google.gson.annotations.SerializedName;
import com.vn.ntsc.core.model.ServerRequest;
import com.vn.ntsc.repository.TypeView;

/**
 * Created by nankai on 8/8/2017.
 */

public class BuzzListRequest extends ServerRequest {

    @SerializedName("req_user_id")
    private String reqUserId;
    @SerializedName("buzz_kind")
    public int buzzKind;
    @SerializedName("long")
    private double lon;
    @SerializedName("lat")
    private double lat;
    @SerializedName("skip")
    public int skip;
    @SerializedName("take")
    public int take;
    @SerializedName("is_fav")
    private boolean isFav;


    public BuzzListRequest(String token, String reqUserId, @TypeView.TypeViewTimeline int buzzKind, double lon, double lat, int skip, int take) {
        super("get_buzz");
        if (token != null && !token.equals(""))
            this.token = token;
        if (reqUserId == null || reqUserId.length() == 0) {
            this.reqUserId = null;
        } else {
            this.reqUserId = reqUserId;
        }
        this.buzzKind = buzzKind;
        this.lon = lon;
        this.lat = lat;
        this.skip = skip;
        this.take = take;
    }

    public BuzzListRequest(String token, String reqUserId, @TypeView.TypeViewTimeline int buzzKind, boolean isLiveStreamFavorite, double lon, double lat, int skip, int take) {
        super("get_buzz");
        if (token != null && !token.equals(""))
            this.token = token;
        if (reqUserId == null || reqUserId.length() == 0) {
            this.reqUserId = null;
        } else {
            this.reqUserId = reqUserId;
        }
        this.buzzKind = buzzKind;
        this.lon = lon;
        this.lat = lat;
        this.skip = skip;
        this.take = take;
        this.isFav = isLiveStreamFavorite;
    }
}
