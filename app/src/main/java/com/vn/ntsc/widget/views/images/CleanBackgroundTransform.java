package com.vn.ntsc.widget.views.images;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.annotation.NonNull;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

import java.nio.charset.Charset;
import java.security.MessageDigest;

/**
 * transform clean black background on some kitkat device
 * http://10.64.100.201/issues/10030#note-73
 */
public class CleanBackgroundTransform extends BitmapTransformation {
    private static final String ID = "com.vn.ntsc.widget.views.images.CleanBackgroundTransform";
    private static final byte[] ID_BYTES = ID.getBytes(Charset.forName("UTF-8"));
    private static final int CLEAN_BLACK = -16777216;

    @Override
    protected Bitmap transform(@NonNull BitmapPool pool, @NonNull Bitmap toTransform, int outWidth, int outHeight) {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            return eraseBG(toTransform, CLEAN_BLACK);
        }
        return toTransform;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof CleanBackgroundTransform;
    }

    @Override
    public int hashCode() {
        return ID.hashCode();
    }

    @Override
    public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {
        messageDigest.update(ID_BYTES);
    }

    /**
     * @param src   bitmap source
     * @param color {@link #CLEAN_BLACK}
     * @return bitmap without background fix
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private Bitmap eraseBG(Bitmap src, int color) {
        int width = src.getWidth();
        int height = src.getHeight();
        Bitmap b = src.copy(Bitmap.Config.ARGB_8888, true);
        b.setHasAlpha(true);

        int[] pixels = new int[width * height];
        src.getPixels(pixels, 0, width, 0, 0, width, height);

        for (int i = 0; i < width * height; i++) {
            if (pixels[i] == color) {
                pixels[i] = 0;
            }
        }

        b.setPixels(pixels, 0, width, 0, 0, width, height);

        return b;
    }
}
