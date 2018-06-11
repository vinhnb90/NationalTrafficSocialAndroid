package com.vn.ntsc.repository.model.myalbum.AddAlbum;

import com.google.gson.annotations.SerializedName;
import com.vn.ntsc.core.model.ServerResponse;

/**
 * Created by ThoNh on 11/14/2017.
 */

public class AddAlbumResponse extends ServerResponse {

    /**
     * data : {"album_id":"5a0c0bda41afe766730e3997"}
     */

    @SerializedName("data")
    public DataBean data;

    public static class DataBean {
        /**
         * album_id : 5a0c0bda41afe766730e3997
         */

        @SerializedName("album_id")
        public String albumId;
    }
}
