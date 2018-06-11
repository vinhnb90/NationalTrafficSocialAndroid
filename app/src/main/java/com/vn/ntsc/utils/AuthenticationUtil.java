package com.vn.ntsc.utils;

import android.app.NotificationManager;
import android.content.Context;

import com.facebook.login.LoginManager;
import com.vn.ntsc.app.AppController;
import com.vn.ntsc.repository.preferece.ChatMessagePreference;
import com.vn.ntsc.repository.preferece.MeetPeopleSetting;
import com.vn.ntsc.repository.preferece.UserPreferences;

/**
 * Created by nankai on 8/7/2017.
 */

public class AuthenticationUtil {
    /**
     * Logout
     */
    public static void onLogout() {
        clearAuthenticationData();

        UserPreferences.getInstance().setIsLogout(true);
        UserPreferences.getInstance().setIsLogin(false);
    }

    public static void clearAuthenticationData() {
        // Clear user data
        UserPreferences userPreferences = UserPreferences.getInstance();
        userPreferences.clear();

        // Clear Facebook session data
        LoginManager.getInstance().logOut();

        // Clear notification
        Context context = AppController.getAppContext();

        // Clear search setting
        MeetPeopleSetting.getInstance().clear();

        NotificationManager notifyManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notifyManager != null) {
            notifyManager.cancelAll();
        }

        // Clear all temp chat message
        ChatMessagePreference chatMessagePreference = ChatMessagePreference.getInstance();
        chatMessagePreference.cleverAll();
    }
}
