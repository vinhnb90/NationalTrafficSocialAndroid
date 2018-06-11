package com.vn.ntsc.repository.model.myalbum.UpdateAlbum;

import com.google.gson.annotations.SerializedName;
import com.vn.ntsc.core.model.ServerRequest;

/**
 * Created by ThoNh on 11/14/2017.
 */

public class UpdateAlbumRequest extends ServerRequest {


    /**
     * api : update_album
     * token : 0c58567a-69e9-4038-a6bb-039eee547cdf
     * album_id : 5a0557d841afe761eae4fad9
     * album_name : xxx
     * album_des : ss
     * privacy : 0
     */

    @SerializedName("album_id")      public String albumId;
    @SerializedName("album_name")    public String albumName;
    @SerializedName("album_des")     public String albumDes;
    @SerializedName("privacy")       public int privacy;

    public UpdateAlbumRequest(String token, String albumId, String albumName, String albumDes, int privacy) {
        super("update_album");
        this.token = token;
        this.albumId = albumId;
        this.albumName = albumName;
        this.albumDes = albumDes;
        this.privacy = privacy;
    }
}
