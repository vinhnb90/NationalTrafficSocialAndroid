package com.vn.ntsc.ui.timeline;

import android.content.Intent;

/**
 * Created by nankai on 11/28/2017.
 */

public interface ActivityResultDispatcher {
    // Pass anything you want, Bundle, Intent, ecc...
    // For the sake of simplicity I'm using Bundle...
    public void dispatchResultData(int requestCode, int resultCode, Intent data);
}
