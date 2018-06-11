package com.vn.ntsc.widget.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.vn.ntsc.R;
import com.vn.ntsc.widget.views.textview.TextViewVectorCompat;

/**
 * Created by dev22 on 8/30/17.
 * container layout for toggle edit mode on edit profile
 */
public class ToggleEditModeRelativeLayout extends RelativeLayout {
    /**
     * true: edit mode, false: disable all edit text
     */
    private boolean isEnableEditMode = true;

    public ToggleEditModeRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.ToggleEditModeRelativeLayout,
                0,
                0);
        try {
            isEnableEditMode = a.getBoolean(R.styleable.ToggleEditModeRelativeLayout_isEnableEditMode, true);
        } finally {
            a.recycle();
        }
    }

    /**
     * toggle all edit text and text view compat in container
     *
     * @param enableEditMode true: edit mode, false: disable all edit text
     * @see #isEnableEditMode
     * @see #toggleEditMode(ViewGroup)
     */
    public void setEnableEditMode(boolean enableEditMode) {
        isEnableEditMode = enableEditMode;
        toggleEditMode(this);
        invalidate();
    }

    /**
     * @param viewGroup parent to find all editText and TextViewVectorCompat
     * @see #setEnableEditMode(boolean)
     */
    private void toggleEditMode(ViewGroup viewGroup) {
        int count = viewGroup.getChildCount();
        for (int i = 0; i < count; i++) {
            View view = viewGroup.getChildAt(i);
            if (view instanceof ViewGroup)
                toggleEditMode((ViewGroup) view);
            else if (view instanceof EditText) {
                EditText edittext = (EditText) view;
                edittext.setEnabled(isEnableEditMode);
            } else if (view instanceof TextViewVectorCompat) {
                TextViewVectorCompat textViewVectorCompat = (TextViewVectorCompat) view;
                textViewVectorCompat.setVectorDrawableRight(isEnableEditMode ? R.drawable.ic_arrow_drop_down_black_48dp : 0);
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        toggleEditMode(this);
    }
}
