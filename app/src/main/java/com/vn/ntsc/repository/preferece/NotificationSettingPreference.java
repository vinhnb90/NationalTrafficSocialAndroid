package com.vn.ntsc.repository.preferece;

import com.vn.ntsc.ui.setting.notification.NotificationReceiveMessageActivity;

/**
 * Created by ThoNh on 10/5/2017.
 */

public class NotificationSettingPreference extends BasePrefers {


    private static final String KEY_VIBRATE = "KEY_VIBRATE";
    private static final String KEY_SOUND = "KEY_SOUND";
    private static final String KEY_NOTIFICATION_FROM_APP = "KEY_NOTIFICATION_FROM_APP";
    private static final String KEY_NOTIFICATION_FAVORITE = "KEY_NOTIFICATION_FAVORITE";
    private static final String KEY_NOTIFICATION_MESSAGE = "KEY_NOTIFICATION_MESSAGE";


    private static NotificationSettingPreference instance;

    public static NotificationSettingPreference getInstance() {
        if (instance == null) {
            instance = new NotificationSettingPreference();
        }
        return instance;
    }

    @Override
    protected String getFileNamePrefers() {
        return null;
    }

    private NotificationSettingPreference() {

    }

    public void saveVibrate(boolean isVibrate) {
        getEditor().putBoolean(KEY_VIBRATE, isVibrate).commit();
    }

    public boolean isVibrate() {
        return getPreferences().getBoolean(KEY_VIBRATE, false);
    }


    public void saveSound(boolean isSound) {
        getEditor().putBoolean(KEY_SOUND, isSound).commit();
    }

    public boolean isSound() {
        return getPreferences().getBoolean(KEY_SOUND, false);
    }


    public void saveNotificationApp(boolean isNotifyApp) {
        getEditor().putBoolean(KEY_NOTIFICATION_FROM_APP, isNotifyApp).commit();
    }

    public boolean isNotificationApp() {
        return getPreferences().getBoolean(KEY_NOTIFICATION_FROM_APP, false);
    }

    public void saveNotificationFav(boolean isNotifyFav) {
        getEditor().putBoolean(KEY_NOTIFICATION_FAVORITE, isNotifyFav).commit();
    }

    public boolean isNotificationFav() {
        return getPreferences().getBoolean(KEY_NOTIFICATION_FAVORITE, false);
    }

    public void saveNotificationSelection(int selectedValue) {
        getEditor().putInt(KEY_NOTIFICATION_MESSAGE, selectedValue).apply();
    }

    public int getNotificationSelection() {
        return getPreferences().getInt(KEY_NOTIFICATION_MESSAGE, NotificationReceiveMessageActivity.ALL);
    }
}
