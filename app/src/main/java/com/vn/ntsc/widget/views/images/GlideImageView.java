package com.vn.ntsc.widget.views.images;

import android.app.Application;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.vn.ntsc.R;

import java.io.File;
import java.net.URL;

/**
 * Created by nankai on 11/1/2017.
 */

public class GlideImageView extends RecyclingImageView implements RequestListener<Drawable> {

    ProgressBar progressBar;
    private boolean showProgressBar;

    private int errorRes;
    private Application applicationContext;

    public void setApplicationContext(Application applicationContext) {
        this.applicationContext = applicationContext;
    }

    private int placeHolderRes;

    public GlideImageView(Context context) {
        super(context);
    }

    public GlideImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public GlideImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {

        final TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.GlideImageView);
        if (typedArray.hasValue(R.styleable.GlideImageView_show_progress)) {
            showProgressBar = typedArray.getBoolean(R.styleable.GlideImageView_show_progress, true);
//            int style = typedArray.getResourceId(R.styleable.GlideImageView_progress_bar_style, android.R.attr.progressBarStyleSmall);
            progressBar = new ProgressBar(getContext(), attrs, android.R.attr.progressBarStyleSmall);
//            progressBar.setVisibility(GONE);
        }

        if (typedArray.hasValue(R.styleable.GlideImageView_error_res)) {
            errorRes = typedArray.getResourceId(R.styleable.GlideImageView_error_res, 0);
        }
        if (typedArray.hasValue(R.styleable.GlideImageView_placeholder_res)) {
            placeHolderRes = typedArray.getResourceId(R.styleable.GlideImageView_placeholder_res, 0);
        }
    }

    public void setErrorRes(int errorRes) {
        this.errorRes = errorRes;
    }

    public void setPlaceHolderRes(int placeHolderRes) {
        this.placeHolderRes = placeHolderRes;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (showProgressBar && progressBar.getParent() == null) {
            ViewParent viewGroupParent = getParent();
            if (viewGroupParent != null && viewGroupParent instanceof ViewGroup) {

                ViewGroup parent = (ViewGroup) viewGroupParent;

                if (!(parent instanceof FrameLayout) || (getLayoutParams().width != FrameLayout.LayoutParams.MATCH_PARENT)
                        ||
                        (getLayoutParams().height != FrameLayout.LayoutParams.MATCH_PARENT)) {

                    FrameLayout frameLayout = new FrameLayout(getContext());
                    int position = parent.indexOfChild(this);

                    parent.removeView(this);
                    frameLayout.addView(this);

                    progressBar.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.CENTER));
                    frameLayout.addView(progressBar);
                    frameLayout.setLayoutParams(getLayoutParams());
                    parent.addView(frameLayout, position);

                } else {
                    int position = parent.indexOfChild(this);
                    parent.addView(progressBar, position + 1);
                    ViewGroup.LayoutParams layoutParams = progressBar.getLayoutParams();
                    layoutParams.width = FrameLayout.LayoutParams.WRAP_CONTENT;
                    layoutParams.height = FrameLayout.LayoutParams.WRAP_CONTENT;
                    if (layoutParams instanceof FrameLayout.LayoutParams)
                        ((FrameLayout.LayoutParams) layoutParams).gravity = Gravity.CENTER;
                }
//                }
            }
        }
    }

    static RequestManager glide;

    public static RequestManager getGlide(Context context) {
        if (glide == null) {
            glide = Glide.with(context);
        }
        return glide;
    }

    public void loadImageUrl(String stringUrl) {
        if (progressBar != null)
            progressBar.setVisibility(VISIBLE);
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(placeHolderRes).error(errorRes).dontAnimate();

        getGlide(getMyContext())
                .load(stringUrl)
                .apply(requestOptions)
                .listener(this)
                .into(this);
    }

    public void load(String string) {
        if (progressBar != null)
            progressBar.setVisibility(VISIBLE);
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(placeHolderRes).error(errorRes).dontAnimate();

        getGlide(getMyContext())
                .load(string)
                .apply(requestOptions)
                .listener(this)
                .into(this);
    }

    private Context getMyContext() {
        if (applicationContext != null) return applicationContext;
        return getContext();
    }

    public void load(Uri uri) {
        if (progressBar != null)
            progressBar.setVisibility(VISIBLE);
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(placeHolderRes).error(errorRes).dontAnimate();

        getGlide(getMyContext())
                .load(uri)
                .apply(requestOptions)
                .listener(this).into(this);
    }

    public void load(File file) {
        if (progressBar != null)
            progressBar.setVisibility(VISIBLE);
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(placeHolderRes).error(errorRes).dontAnimate();

        getGlide(getMyContext())
                .load(file)
                .apply(requestOptions)
                .listener(this).into(this);
    }

    public void load(Integer resourceId) {
        if (progressBar != null)
            progressBar.setVisibility(VISIBLE);
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(placeHolderRes).error(errorRes).dontAnimate();

        getGlide(getMyContext())
                .load(resourceId)
                .apply(requestOptions)
                .listener(this).into(this);
    }

    public void load(URL url) {
        if (progressBar != null)
            progressBar.setVisibility(VISIBLE);
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(placeHolderRes).error(errorRes).dontAnimate();
        getGlide(getMyContext())
                .load(url)
                .apply(requestOptions)
                .listener(this).into(this);
    }

    @Override
    public boolean onLoadFailed(@Nullable GlideException e, Object o, Target<Drawable> target, boolean b) {
        if (progressBar != null)
            progressBar.setVisibility(GONE);
        return false;
    }

    @Override
    public boolean onResourceReady(Drawable drawable, Object o, Target<Drawable> target, DataSource dataSource, boolean b) {
        if (progressBar != null)
            progressBar.setVisibility(GONE);
        return false;
    }
}