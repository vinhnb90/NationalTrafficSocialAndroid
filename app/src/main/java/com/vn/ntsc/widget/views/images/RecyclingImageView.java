package com.vn.ntsc.widget.views.images;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.util.AttributeSet;

/**
 * Created by nankai on 5/22/2017.
 */

public class RecyclingImageView extends android.support.v7.widget.AppCompatImageView {
    private boolean invalidated = false;
    private OnInvalidated onInvalidated;

    public RecyclingImageView(Context context) {
        super(context);
    }

    public RecyclingImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RecyclingImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onDraw(Canvas canvas) {
        Drawable drawable = this.getDrawable();
        if (drawable != null) {
            if (drawable instanceof BitmapDrawable) {
                if (isBitmapRecycled(drawable)) {
                    this.setImageDrawable(null);
                    setInvalidated(true);
                }
            } else if (drawable instanceof TransitionDrawable) {
                TransitionDrawable transitionDrawable = (TransitionDrawable) drawable;

                // If last bitmap in chain is recycled, just blank this out since it would be invalid anyways
                Drawable lastDrawable = transitionDrawable.getDrawable(transitionDrawable.getNumberOfLayers() - 1);
                if (isBitmapRecycled(lastDrawable)) {
                    this.setImageDrawable(null);
                    setInvalidated(true);
                } else {
                    // Go through earlier bitmaps and make sure that they are not recycled
                    for (int i = 0; i < transitionDrawable.getNumberOfLayers(); i++) {
                        Drawable layerDrawable = transitionDrawable.getDrawable(i);
                        if (isBitmapRecycled(layerDrawable)) {
                            // If anything in the chain is broken, just get rid of transition and go to last drawable
                            this.setImageDrawable(lastDrawable);
                            break;
                        }
                    }
                }
            }
        }

        super.onDraw(canvas);
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        setInvalidated(false);
    }

    private boolean isBitmapRecycled(Drawable drawable) {
        if (!(drawable instanceof BitmapDrawable)) {
            return false;
        }

        BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
        return bitmapDrawable.getBitmap() != null && bitmapDrawable.getBitmap().isRecycled();
    }

    public boolean isInvalidated() {
        return invalidated;
    }

    public void setInvalidated(boolean invalidated) {
        this.invalidated = invalidated;

        if (invalidated && onInvalidated != null) {
            onInvalidated.onInvalidated(this);
        }
    }

    public void setOnInvalidated(OnInvalidated onInvalidated) {
        this.onInvalidated = onInvalidated;
    }

    public interface OnInvalidated {
        void onInvalidated(RecyclingImageView imageView);
    }
}