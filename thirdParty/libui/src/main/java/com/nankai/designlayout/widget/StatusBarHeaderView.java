package com.nankai.designlayout.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.nankai.designlayout.utils.Utils;

/**
 * Created by nankai on 11/3/2016.
 */

public class StatusBarHeaderView extends View {

    public StatusBarHeaderView(Context context) {
        super(context);
    }

    public StatusBarHeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public StatusBarHeaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        ViewGroup.LayoutParams params = getLayoutParams();
        params.height = Utils.getStatusBarHeight(getContext());
    }

}

