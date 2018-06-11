package com.vn.ntsc.repository.model.image;

import com.google.gson.annotations.SerializedName;
import com.vn.ntsc.BuildConfig;
import com.vn.ntsc.core.model.ServerRequest;


public class ImageRequestWithSize extends ServerRequest {

    @SerializedName("img_id")
    public String imgId;

    @SerializedName("size")
    public int size;

    public ImageRequestWithSize(String token, String imageId, int size) {
        super("load_img_with_size");
        this.size = size;
        this.imgId = imageId;
        this.token = token;
    }

    public String toURL() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(BuildConfig.MEDIA_SERVER);
        stringBuilder.append("api");
        stringBuilder.append("=");
        stringBuilder.append(this.api);
        stringBuilder.append("&");
        stringBuilder.append("token");
        stringBuilder.append("=");
        stringBuilder.append(this.token);
        stringBuilder.append("&");
        stringBuilder.append("img_id");
        stringBuilder.append("=");
        stringBuilder.append(this.imgId);
        stringBuilder.append("&");
        stringBuilder.append("width_size");
        stringBuilder.append("=");
        stringBuilder.append(this.size);
        return stringBuilder.toString();
    }

}
