package com.vn.ntsc.repository.model.image;

import com.vn.ntsc.BuildConfig;
import com.vn.ntsc.core.model.ServerRequest;

/**
 * Created by nankai on 8/8/2017.
 */

public class ImageRequest extends ServerRequest {
    public static final int THUMBNAIL = 1;
    public static final int ORIGINAL = 2;
    public static final int STICKER = 3;
    public static final int GIFT = 4;
    public static final int STICKER_CATEGORY = 5;
    public static final int BANNER = 6;

    /**
     *
     */
    private static final long serialVersionUID = 4162076248105963965L;
    public int img_kind;
    public String img_id;

    public ImageRequest(String img_id, int img_kind) {
        super("load_img");
        this.img_id = img_id;
        this.img_kind = img_kind;
    }

    public String toURL() {
        StringBuilder stringBuilder = new StringBuilder();
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
        return stringBuilder.toString();
    }

    public String toURL(String token) {
        StringBuilder stringBuilder = new StringBuilder();
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
        stringBuilder.append("&token=");
        stringBuilder.append(token);
        return stringBuilder.toString();
    }

    public String toURLCache() {
        return this.img_id + this.img_kind;
    }
}
