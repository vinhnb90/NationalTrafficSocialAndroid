package com.vn.ntsc.repository.model.myalbum.AddAlbum;

import com.google.gson.annotations.SerializedName;
import com.vn.ntsc.core.model.ServerRequest;

/**
 * Created by ThoNh on 11/14/2017.
 */

public class AddAlbumRequest extends ServerRequest {

    @SerializedName("album_name")       String albumName;           // name of album
    @SerializedName("album_des")        String albumDescription;    // description for album
    @SerializedName("privacy")          int privacy;                // privacy of album [0: public] [1: private]


    public AddAlbumRequest(String token, String albumName, String albumDescription, int privacy) {
        super("add_album");
        this.token = token;
        this.albumName = albumName;
        this.albumDescription = albumDescription;
        this.privacy = privacy;
    }
}
