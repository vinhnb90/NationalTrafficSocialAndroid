package com.vn.ntsc.ui.tagfriends;

import android.graphics.drawable.Drawable;
import android.view.View;

/**
 * Created by Robert on 2017 Sep 22.
 */
public interface ChipListener {
    void onResourceReady(Drawable drawable, View view);
    void onLoadFailed(View view);
}
