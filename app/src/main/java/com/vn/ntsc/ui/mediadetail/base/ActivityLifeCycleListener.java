package com.vn.ntsc.ui.mediadetail.base;

/**
 * Created by ThoNh on 11/21/2017.
 */

public interface ActivityLifeCycleListener {

    void onActivityResume();

    void onActivityPause();

    void onActivityDestroy();

    void onActivityStop();

    void onActivityConfigChange(int mOrientation, boolean isOrientationSettingOn);

}
