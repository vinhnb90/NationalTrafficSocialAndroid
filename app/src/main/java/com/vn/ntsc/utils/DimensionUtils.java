package com.vn.ntsc.utils;

import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;

import com.vn.ntsc.app.AppController;

/**
 * Created by hnc on 15/08/2017.
 */

public class DimensionUtils {

    private static boolean isInitialised = false;
    private static float pixelsPerOneDp;


    private DimensionUtils() {
        throw new AssertionError();
    }

    private static void initialise(View view) {
        pixelsPerOneDp = view.getResources().getDisplayMetrics().densityDpi / 160f;
        isInitialised = true;
    }

    public static int pxToDp(View view, float px) {
        if (!isInitialised) {
            initialise(view);
        }

        return (int) (px / pixelsPerOneDp);
    }

    public static int dpToPx(View view, float dp) {
        if (!isInitialised) {
            initialise(view);
        }

        return (int) (dp * pixelsPerOneDp);
    }


    public static int convertDpToPx(int dp){
        return Math.round(dp*(AppController.getAppContext().getResources().getDisplayMetrics().xdpi/ DisplayMetrics.DENSITY_DEFAULT));

    }

    public static int convertPxToDp(int px){
        return Math.round(px/(AppController.getAppContext().getResources().getDisplayMetrics().xdpi/DisplayMetrics.DENSITY_DEFAULT));
    }

    public static int convertSpToPixels(float sp) {
        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, AppController.getAppContext().getResources().getDisplayMetrics());
        return px;
    }

}
