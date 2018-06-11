package com.vn.ntsc.widget.views.images.mediadetail.video;

import android.content.res.Configuration;

/**
 * Created by ThoNh on 10/16/2017.
 */

public interface MediaStateListener {

    void onResume();

    void onPause();

    void onStop();

    void onDestroy();

    void onConfigChange(Configuration configuration);

    void resetSensorOrient();

    void start();

    void pause();
}
