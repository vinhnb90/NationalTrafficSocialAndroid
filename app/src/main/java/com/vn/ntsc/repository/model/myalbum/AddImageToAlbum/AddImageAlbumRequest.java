package com.vn.ntsc.repository.model.myalbum.AddImageToAlbum;

import com.vn.ntsc.repository.model.mediafile.MediaFileBean;

import java.util.List;

/**
 * Created by ThoNh on 11/14/2017.
 */

public class AddImageAlbumRequest {

    //      @SerializedName("files")
    public List<MediaFileBean> imagePaths;

    //    @SerializedName("token")
    public String token;

    //    @SerializedName("album_id")
    public String albumId;

    //    @SerializedName("api") ->add_album_image
    public String api = "add_album_image";

    public AddImageAlbumRequest(List<MediaFileBean> imagePaths, String token, String albumId) {
        this.imagePaths = imagePaths;
        this.token = token;
        this.albumId = albumId;
    }
}
