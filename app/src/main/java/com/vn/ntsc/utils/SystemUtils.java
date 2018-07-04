package com.vn.ntsc.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.vn.ntsc.app.AppController;

import java.util.List;

/**
 * Created by nankai on 8/3/2017.
 */

public class SystemUtils {
    public static String TAG = SystemUtils.class.getSimpleName();

    public static String getDeviceId() {
        Context context = AppController.getAppContext();
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public static String getAppVersionName(Context context) {
        String packageName = context.getPackageName();
        PackageInfo packageInfo = null;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(
                    packageName, 0);
        } catch (Exception e) {
            LogUtils.e(TAG, String.valueOf(e.getMessage()));
        }
        String appVersion = "";
        if (packageInfo != null && packageInfo.versionName != null) {
            String version = packageInfo.versionName;
            if (version.contains(" ")) {
                appVersion = packageInfo.versionName.substring(0,
                        packageInfo.versionName.indexOf(" "));
            } else {
                appVersion = version;
            }
        }
        return appVersion;
    }

    public static String getDeviceName() {
        String name = Build.MODEL;
        LogUtils.d(TAG, "Device Name - " + name);
        return name;
    }

    public static String getAndroidOSVersion() {
        String release = Build.VERSION.RELEASE;
        int sdkVersion = Build.VERSION.SDK_INT;
        String name = "Android SDK: " + sdkVersion + " (" + release + ")";
        LogUtils.d(TAG, "Device Name - " + name);
        return name;
    }

    public static boolean showInputMethod(View view) {
        final InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(
                Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            return imm.showSoftInput(view, 0);
        }
        return false;
    }

    public static boolean hideInputMethod(View view) {
        final InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(
                Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            return imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        return false;
    }

    /**
     * @return true: connected or connecting status, false: otherwise
     */
    public static boolean isNetworkConnected() {
        Context context = AppController.getAppContext();
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = null;
        if (cm != null) {
            activeNetwork = cm.getActiveNetworkInfo();
        }

        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }

    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static int getAppVersion(Context applicationContext) {
        Context con = AppController.getAppContext();
        PackageInfo info;
        try {
            info = con.getPackageManager().getPackageInfo(
                    applicationContext.getPackageName(), 0);
            return info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // An ban phim khi goi method nay
    public static void hideSoftKeyboard(Context context) {
        if (context == null) {
            return;
        }
        InputMethodManager inputMethodManager = (InputMethodManager) context
                .getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = ((Activity) context).getCurrentFocus();
        if (view == null) {
            return;
        }
        IBinder iBinder = view.getWindowToken();
        if (iBinder != null)
            inputMethodManager.hideSoftInputFromWindow(iBinder, 0);
    }

    /**
     * Method checks if the app is in background or not
     */
    public static boolean isAppIsInForeground(Context context) {

        boolean isInForeground = false;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
            if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                for (String activeProcess : processInfo.pkgList) {
                    if (activeProcess.equals(context.getPackageName())) {
                        isInForeground = true;
                    }
                }
            }
        }

        LogUtils.i(TAG, "App with PackageName \"" + context.getPackageName() + "\" | isInForeground = " + isInForeground);

        return isInForeground;
    }

    private boolean isMyServiceRunning(Class<?> serviceClass, Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
