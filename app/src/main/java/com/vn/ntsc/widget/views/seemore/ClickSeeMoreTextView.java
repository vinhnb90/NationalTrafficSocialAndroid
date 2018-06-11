package com.vn.ntsc.widget.views.seemore;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

/**
 * Created by nankai on 2/8/2018.
 */

public class ClickSeeMoreTextView extends AppCompatTextView {

    public boolean linkClicked;

    public ClickSeeMoreTextView(Context context) {
        super(context);
    }

    public ClickSeeMoreTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ClickSeeMoreTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean performClick() {
        return !linkClicked && super.performClick();
    }


}
