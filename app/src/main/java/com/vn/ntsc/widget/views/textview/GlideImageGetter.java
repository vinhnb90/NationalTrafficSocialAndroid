package com.vn.ntsc.widget.views.textview;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by dev22 on 3/21/18.
 * show emoji via img tag using Glide
 */
public class GlideImageGetter implements Html.ImageGetter, Drawable.Callback {
    private final Context context;
    private final boolean animated;
    private final RequestManager glide;
    private final int size;
    private TextView targetView;
    private final Collection<Target> imageTargets = new ArrayList<>();

    GlideImageGetter(Context context, RequestManager glide, TextView targetView, boolean animated, int size) {
        this.context = context.getApplicationContext();
        this.animated = animated;
        this.glide = createGlideRequest(glide, animated);
        this.targetView = targetView;
        this.size = size;
        targetView.setTag(this);
    }

    private RequestManager createGlideRequest(RequestManager glide, boolean animated) {
        RequestManager load;
        RequestOptions requestOption = new RequestOptions();
        if (animated) {
            load = glide.applyDefaultRequestOptions(requestOption
                    .diskCacheStrategy(DiskCacheStrategy.DATA) // animated GIFs need source cache
                    .fitCenter() // show full image when animating
            );
        } else {
            load = glide
                    // force still images
                    .applyDefaultRequestOptions(requestOption
                            // cache resized images (RESULT), and re-use SOURCE cached GIFs if any
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            // show part of the image when still
                            .centerCrop()
                    )
            ;
        }
        return load;
    }

    @Override
    public void invalidateDrawable(@NonNull Drawable who) {
        targetView.invalidate();
    }

    @Override
    public void scheduleDrawable(@NonNull Drawable who, @NonNull Runnable what, long when) {

    }

    @Override
    public void unscheduleDrawable(@NonNull Drawable who, @NonNull Runnable what) {

    }

    @Override
    public Drawable getDrawable(String source) {
        // set up target for this Image inside the TextView
        WrapperTarget imageTarget = new WrapperTarget(size, animated);
        Drawable asyncWrapper = imageTarget.getLazyDrawable();
        // listen for Drawable's request for invalidation
        asyncWrapper.setCallback(this);

        // check if not local then load remote url
        File file = new File(source);
        Uri uri = file.exists() ? Uri.fromFile(file) : Uri.parse(source);
        // start Glide's async load
        glide.load(uri)
                .into(imageTarget);
        // save target for clearing it later
        imageTargets.add(imageTarget);
        return asyncWrapper;
    }

    /**
     * clear target
     */
    private void clear() {
        for (Target target : imageTargets) {
            Glide.with(context).clear(target);
        }
    }

    /**
     * clear all target's tag, used for list view or recycler view
     *
     * @param view target view
     */
    public static void clear(@Nullable TextView view) {
        if (view == null) return;

        view.setText(null);
        Object tag = view.getTag();
        if (tag instanceof GlideImageGetter) {
            ((GlideImageGetter) tag).clear();
            view.setTag(null);
        }
    }
}
