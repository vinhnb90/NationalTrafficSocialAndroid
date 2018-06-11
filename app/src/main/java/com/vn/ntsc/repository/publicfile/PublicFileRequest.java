package com.vn.ntsc.repository.publicfile;

import com.google.gson.annotations.SerializedName;
import com.vn.ntsc.core.model.ServerRequest;

/**
 * Created by ThoNh on 11/23/2017.
 */

public class PublicFileRequest  extends ServerRequest{

    public static final int TYPE_VIDEO_AUDIO = 2;
    public static final int TYPE_TIMELINE = 1;
    public static final int TYPE_ALL = 0;


    /**
     * skip : 0
     * take : 48
     * token : a9e486a6-a857-482e-a141-198d1738cdb4
     * req_user_id : 5a03e3ce41afe7451cbb8736
     */

    public static final int TAKE = 48 ;

    @SerializedName("skip")
    public int skip;
    @SerializedName("take")
    public int take;
    @SerializedName("req_user_id")
    public String reqUserId;
    @SerializedName("type")
    public int type;

    public PublicFileRequest( int skip, String token, String reqUserId, int type) {
        super("list_public_file");
        this.skip = skip;
        this.take = TAKE;
        this.token = token;
        this.reqUserId = reqUserId;
        this.type = type;
    }

}
