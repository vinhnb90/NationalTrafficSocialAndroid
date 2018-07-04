package com.vn.ntsc.ui.mediadetail.base;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.RelativeLayout;

import com.vn.ntsc.R;
import com.vn.ntsc.utils.ImagesUtils;
import com.vn.ntsc.widget.views.images.mediadetail.image.ImageViewTouch;
import com.vn.ntsc.widget.views.images.mediadetail.image.ImageViewTouchBase;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ImageViewHolder extends BaseMediaHolder implements View.OnClickListener {
    /*-----------------------------------var-----------------------------------*/
    private static final String TAG = ImageViewHolder.class.getSimpleName();
    private IteractionImage mIteractionImage;
    private Context mContext;
    private Unbinder mUnbinder;
    private View mView;
    private ProgressDialog mDialog;

    @BindView(R.id.item_media_detail_image_image_view)
    ImageViewTouch imageViewTouch;

    @BindView(R.id.item_media_detail_image_rl_progressbar)
    RelativeLayout mRelativeLayout;

    /*-----------------------------------instance-----------------------------------*/
    public ImageViewHolder(final Context activity, View layout, @NonNull final IteractionImage iteractionImage) {
        super(activity);

        mContext = activity;
        this.mIteractionImage = iteractionImage;
        mView = layout;
        mUnbinder = ButterKnife.bind(this, layout);

        imageViewTouch.setOnDrawableChangedListener(new ImageViewTouchBase.OnDrawableChangeListener() {
            @Override
            public void onDrawableChanged(Drawable drawable) {
                if (drawable != null) {
                    mView.post(new Runnable() {
                        @Override
                        public void run() {
                            showLoadingView(false);
                        }
                    });
                } else {
                    iteractionImage.loadImageAgain();
                }
            }
        });
    }
    /*-----------------------------------lifecycle-----------------------------------*/


    /*-----------------------------------override-----------------------------------*/
    @Override
    public void onClick(View v) {

    }

    /*-----------------------------------func-----------------------------------*/
    public void loadImage(final String bitmapImageUrl) {
        ImagesUtils.loadImage(bitmapImageUrl, imageViewTouch);
    }


    public void unbind() {
        mUnbinder.unbind();
    }

    public void showLoadingView(boolean isShow) {
        mRelativeLayout.setVisibility(isShow ? View.VISIBLE : View.INVISIBLE);
    }

    /*-----------------------------------interface-----------------------------------*/


}
