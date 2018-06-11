package com.vn.ntsc.repository.model.myalbum.DeleteAlbum;

import com.google.gson.annotations.SerializedName;
import com.vn.ntsc.core.model.ServerRequest;

/**
 * Created by ThoNh on 11/14/2017.
 */

public class DelAlbumRequest extends ServerRequest {

    @SerializedName("album_id")         String albumId;             // id of album to del

    public DelAlbumRequest( String token, String albumId) {
        super("del_album");
        this.token = token;
        this.albumId = albumId;
    }
}
