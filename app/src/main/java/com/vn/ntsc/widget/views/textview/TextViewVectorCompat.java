package com.vn.ntsc.widget.views.textview;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.view.Gravity;

import com.vn.ntsc.R;

/**
 * Created by dev22 on 8/18/17.
 * TextView support drawable vector
 */
public class TextViewVectorCompat extends AppCompatTextView {
    private VectorDrawableCompat drawableRight;
    private VectorDrawableCompat drawableTop;
    private VectorDrawableCompat drawableBottom;
    private VectorDrawableCompat drawableLeft;

    public TextViewVectorCompat(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.TextViewVectorCompat,
                0,
                R.style.profile);
        int dr;
        int dt;
        int db;
        int dl;
        try {
            dl = a.getResourceId(R.styleable.TextViewVectorCompat_drawableCompatLeft, -1);
            dr = a.getResourceId(R.styleable.TextViewVectorCompat_drawableCompatRight, -1);
            dt = a.getResourceId(R.styleable.TextViewVectorCompat_drawableCompatTop, -1);
            db = a.getResourceId(R.styleable.TextViewVectorCompat_drawableCompatBottom, -1);
        } finally {
            a.recycle();
        }

        if (dl > 0) {
            drawableLeft = VectorDrawableCompat.create(getResources(), dl, context.getTheme());
        }
        if (dr > 0) {
            drawableRight = VectorDrawableCompat.create(getResources(), dr, context.getTheme());
        }
        if (dt > 0) {
            drawableTop = VectorDrawableCompat.create(getResources(), dt, context.getTheme());
        }
        if (db > 0) {
            drawableBottom = VectorDrawableCompat.create(getResources(), db, context.getTheme());
        }
        // set drawable
        setCompoundDrawablesWithIntrinsicBounds(drawableLeft, drawableTop, drawableRight, drawableBottom);
        // make content align center
        setGravity(Gravity.CENTER);
    }

    /**
     * set vector drawable left
     *
     * @param resVectorDrawable resource vector, if smaller than 0 -> set null
     */
    public void setVectorDrawableLeft(int resVectorDrawable) {
        drawableLeft = resVectorDrawable > 0 ? VectorDrawableCompat.create(getResources(), resVectorDrawable, getContext().getTheme()) : null;
        setCompoundDrawablesWithIntrinsicBounds(drawableLeft, drawableTop, drawableRight, drawableBottom);
    }

    /**
     * set vector drawable right
     *
     * @param resVectorDrawable resource vector, if smaller than 0 -> set null
     */
    public void setVectorDrawableRight(int resVectorDrawable) {
        drawableRight = resVectorDrawable > 0 ? VectorDrawableCompat.create(getResources(), resVectorDrawable, getContext().getTheme()) : null;
        setCompoundDrawablesWithIntrinsicBounds(drawableLeft, drawableTop, drawableRight, drawableBottom);
    }

    /**
     * set vector drawable top
     *
     * @param resVectorDrawable resource vector, if smaller than 0 -> set null
     */
    public void setVectorDrawableTop(int resVectorDrawable) {
        drawableTop = resVectorDrawable > 0 ? VectorDrawableCompat.create(getResources(), resVectorDrawable, getContext().getTheme()) : null;
        setCompoundDrawablesWithIntrinsicBounds(drawableLeft, drawableTop, drawableRight, drawableBottom);
    }

    /**
     * set vector drawable bottom
     *
     * @param resVectorDrawable resource vector, if smaller than 0 -> set null
     */
    public void setVectorDrawableBottom(int resVectorDrawable) {
        drawableBottom = resVectorDrawable > 0 ? VectorDrawableCompat.create(getResources(), resVectorDrawable, getContext().getTheme()) : null;
        setCompoundDrawablesWithIntrinsicBounds(drawableLeft, drawableTop, drawableRight, drawableBottom);
    }
}
