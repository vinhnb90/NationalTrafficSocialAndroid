package com.vn.ntsc.repository.model.myalbum.LoadImageInAlbum;

import com.google.gson.annotations.SerializedName;
import com.vn.ntsc.core.model.ServerRequest;

/**
 * Created by ThoNh on 11/14/2017.
 */

public class LoadAlbumImageRequest extends ServerRequest {

    public static final int TAKE = 48;

    /**
     * token : 52fa3222-5dd2-4f0e-aa9a-52dc405c0009
     * album_id : 5a0cec3041afe7738bb0c3f3
     * skip : 0
     * take : 20
     */
    @SerializedName("album_id")
    public String albumId;
    @SerializedName("skip")
    public int skip;
    @SerializedName("take")
    public int take = TAKE;

    public LoadAlbumImageRequest(String token, String albumId, int skip) {
        super("load_album_image");
        this.token = token;
        this.albumId = albumId;
        this.skip = skip;
    }
}
