package com.vn.ntsc.repository.model.image;

import com.google.gson.annotations.SerializedName;
import com.vn.ntsc.BuildConfig;
import com.vn.ntsc.core.model.ServerRequest;

/**
 * Created by nankai on 8/8/2017.
 */

public class CircleImageRequest extends ServerRequest {

    @SerializedName("img_kind")
    public int img_kind;
    @SerializedName("img_id")
    public String img_id;
    @SerializedName("isCircle")
    public boolean isCircle = true;
    private StringBuilder stringBuilder;

    public CircleImageRequest(String img_id) {
        super("load_img");
        this.img_id = img_id;
        this.img_kind = 1;    //Thumbnail
    }

    public String toURL() {
        if (stringBuilder == null) {
            stringBuilder = new StringBuilder();
        }
        stringBuilder.append(BuildConfig.MEDIA_SERVER);
        stringBuilder.append("api");
        stringBuilder.append("=");
        stringBuilder.append(this.api);
        stringBuilder.append("&");
        stringBuilder.append("img_id");
        stringBuilder.append("=");
        stringBuilder.append(this.img_id);
        stringBuilder.append("&");
        stringBuilder.append("img_kind");
        stringBuilder.append("=");
        stringBuilder.append(this.img_kind);
        String url = stringBuilder.toString();
        stringBuilder.delete(0, stringBuilder.length());
        return url;
    }

    public String getCircleImageId() {
        return this.img_id;
    }

    public String getCircleImageIdUnique() {
        return this.img_id + this.img_kind + "circle";
    }
}
