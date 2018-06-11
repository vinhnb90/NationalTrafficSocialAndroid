package com.vn.ntsc.repository.model.myalbum.DeleteImageInAlbum;

import com.google.gson.annotations.SerializedName;
import com.vn.ntsc.core.model.ServerRequest;

import java.util.List;

/**
 * Created by ThoNh on 11/14/2017.
 */

public class DelAlbumImageRequest extends ServerRequest {

    /**
     * token : 103df308-7709-4d1a-9fcf-4aee02eba127
     * album_id : 5a0557d841afe761eae4fad9
     * list_image : ["5a055df341afe7608b73b08f"]
     */
    @SerializedName("album_id")
    public String albumId;
    @SerializedName("list_image")
    public List<String> listImage;

    public DelAlbumImageRequest(String token, String albumId, List<String> listImage) {
        super("del_album_image");
        this.token = token;
        this.albumId = albumId;
        this.listImage = listImage;
    }
}
