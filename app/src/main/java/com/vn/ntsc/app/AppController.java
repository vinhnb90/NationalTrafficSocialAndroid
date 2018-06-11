package com.vn.ntsc.app;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.support.multidex.MultiDex;
import android.support.v7.app.AppCompatDelegate;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import com.squareup.leakcanary.LeakCanary;
import com.vn.ntsc.BuildConfig;
import com.vn.ntsc.repository.preferece.UserPreferences;
import com.vn.ntsc.utils.LogUtils;

import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by ADMIN on 7/31/2017.
 */
public class AppController extends Application implements Application.ActivityLifecycleCallbacks {
    // for android 17: fix crash if use vector drawable (HTC 616)
    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    public static int SCREEN_WIDTH = -1;
    public static int SCREEN_HEIGHT = -1;

    public static float DIMEN_RATE = -1.0F;
    public static int DIMEN_DPI = -1;

    private static AppController instance;
    private static ComponentBuilder builder;
    private LinkedList<Activity> mActivityList = new LinkedList<>();

    public static AppController getAppContext() {
        return instance;
    }

    public static ComponentBuilder getComponent() {
        if (builder == null)
            builder = ComponentBuilder.install(instance);
        return builder;
    }

    /**
     * count number of activities
     */
    AtomicInteger activityCount = new AtomicInteger();
    /**
     * to detect when app run in background
     */
    AtomicInteger foregroundTaskCount = new AtomicInteger();

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        builder = ComponentBuilder.install(instance);
        registerActivityLifecycleCallbacks(this);

        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }

        LeakCanary.install(this);
        getScreenSize();
        // If you want to use AppEventsLogger to log events.
        // facebook log
//        AppEventsLogger.activateApp(this);
    }

    public void getScreenSize() {
        WindowManager windowManager = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        if (windowManager != null) {
            Display display = windowManager.getDefaultDisplay();
            display.getMetrics(dm);
            DIMEN_RATE = dm.density / 1.0F;
            DIMEN_DPI = dm.densityDpi;
            SCREEN_WIDTH = dm.widthPixels;
            SCREEN_HEIGHT = dm.heightPixels;
            if (SCREEN_WIDTH > SCREEN_HEIGHT) {
                int t = SCREEN_HEIGHT;
                SCREEN_HEIGHT = SCREEN_WIDTH;
                SCREEN_WIDTH = t;
            }
            LogUtils.d("App", "SCREEN_WIDTH = " + SCREEN_WIDTH + "\nSCREEN_HEIGHT=" + SCREEN_HEIGHT + "\nDIMEN_RATE=" + DIMEN_RATE + "\nDIMEN_DPI=" + DIMEN_DPI);
        }
    }

    public void finishAllActivities() {
        if (null != mActivityList) {
            unregisterActivityLifecycleCallbacks(this);
            for (Activity activity : mActivityList) {
                if (null == activity) {
                    continue;
                }
                try {
                    activity.finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            mActivityList.clear();
            registerActivityLifecycleCallbacks(this);
        }
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        if (activityCount.incrementAndGet() > 0) {
            // don't worry bind service will not execute if it bound
            builder.getSocketServiceConnection().bindService(this);
        }
        if (null != mActivityList && null != activity) {
            mActivityList.offerFirst(activity);
        }
    }

    @Override
    public void onActivityStarted(Activity activity) {
        onConnectSocket();
    }

    public void onConnectSocket() {
        foregroundTaskCount.incrementAndGet();
        UserPreferences userPreferences = UserPreferences.getInstance();
        String userId = userPreferences.getUserId();
        String token = userPreferences.getToken();
        if (builder.getSocketServiceConnection().isBound())
            // check is login
            if (!TextUtils.isEmpty(userId) && !TextUtils.isEmpty(token)) {
                // don't worry it will not execute again if state is connected or connecting
                builder.getWebSocketService().connect(BuildConfig.CHAT_SERVER_IP, BuildConfig.CHAT_SERVER_PORT, token, userId);
            } else {
                builder.getSocketServiceConnection().getSocketService().close();
            }
    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {
        foregroundTaskCount.decrementAndGet();
        if (isRunningInBackground() && builder.getSocketServiceConnection().isBound())
            builder.getWebSocketService().close();
    }

    private boolean isRunningInBackground() {
        return foregroundTaskCount.get() == 0;
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        if (activityCount.decrementAndGet() <= 0) {
            // it only unbind if isBound = true
            builder.getSocketServiceConnection().unbindService(this);
        }
        if (null != mActivityList && null != activity) {
            Activity aty = findActivity(activity);
            if (null != aty) {
                mActivityList.remove(aty);
            }
        }
    }

    private Activity findActivity(Activity activity) {
        if (null == activity || null == mActivityList) {
            return null;
        }
        for (Activity aty : mActivityList) {
            if (null == aty) {
                continue;
            }
            if (activity.equals(aty)) {
                return aty;
            }
        }
        return null;
    }
}
