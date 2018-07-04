package com.vn.ntsc.ui.mediadetail.util;

import android.content.Context;

import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

public class MediaPlayAppUtils extends android.app.Application {
    /*
     * var
     * */
    private static MediaPlayAppUtils sInstance;
    private RefWatcher refWatcher;

    /*
     * contructor
     * */
    public MediaPlayAppUtils() {
        sInstance = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        refWatcher = LeakCanary.install(this);
        // Normal app init code...

    }

    /*
     * func
     * */
    public static Context getContext() {
        return sInstance.getApplicationContext();
    }

    public static RefWatcher getRefWatcher(Context context) {
        MediaPlayAppUtils application = (MediaPlayAppUtils) context.getApplicationContext();
        return application.refWatcher;
    }

}