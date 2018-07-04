package com.vn.ntsc.utils;

import android.content.res.Resources;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.view.View;

public class ViewUtils {

    public static int getShortAnimTime(View view) {
        return getShortAnimTime(view.getResources());
    }

    public static int getShortAnimTime(Resources resources) {
        return resources.getInteger(android.R.integer.config_shortAnimTime);
    }

    public static void fadeIn(View view) {
        fadeIn(view, getShortAnimTime(view));
    }

    public static void fadeIn(View view, int duration) {
        if (view.getVisibility() == View.VISIBLE && view.getAlpha() == 1) {
            // Cancel any starting animation.
            view.animate()
                    .alpha(1)
                    .setDuration(0)
                    .start();
            return;
        }
        view.setAlpha(isVisible(view) ? view.getAlpha() : 0);
        view.setVisibility(View.VISIBLE);
        view.animate()
                .alpha(1)
                .setDuration(duration)
                .setInterpolator(new FastOutSlowInInterpolator())
                // NOTE: We need to remove any previously set listener or Android will reuse it.
                .setListener(null)
                .start();
    }

    public static boolean isVisible(View view) {
        return view.getVisibility() == View.VISIBLE;
    }
}
