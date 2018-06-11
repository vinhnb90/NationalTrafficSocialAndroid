package com.vn.ntsc.repository.model.videoaudio;

import com.google.gson.annotations.SerializedName;
import com.vn.ntsc.core.model.ServerRequest;

/**
 * Created by ThoNh on 11/20/2017.
 */

public class VideoAudioRequest extends ServerRequest {

    /**
     * take : 20
     * skip : 0
     * req_user_id : 59db3896e4b0d7e209387052
     * ip : 10.64.1.55
     */

    public static final int TAKE = 48;

    @SerializedName("take")
    public int take = TAKE;
    @SerializedName("skip")
    public int skip;
    @SerializedName("req_user_id")
    public String reqUserId;


    public VideoAudioRequest(int skip, String reqUserId) {
        super("lst_pbvideo");
        this.skip = skip;
        this.reqUserId = reqUserId;
    }
}
