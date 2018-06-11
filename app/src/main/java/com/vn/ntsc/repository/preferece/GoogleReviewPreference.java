package com.vn.ntsc.repository.preferece;

import android.text.TextUtils;

import com.vn.ntsc.app.AppController;
import com.vn.ntsc.utils.SystemUtils;


/**
 * Created by nankai on 2/9/2017.
 */

public class GoogleReviewPreference extends BasePrefers {

    public static String TAG = GoogleReviewPreference.class.getSimpleName();

    //=======================================================//
    //================= variable ============================//
    //=======================================================//
    private static GoogleReviewPreference instance;

    private final String TURN_OFF_VERSION = "turn.off.version";
    private final String IS_ENABLE_GET_FREE_POINT = "is.enable.get.free.point";
    private final String IS_TURN_OFF_USER_INFO = "is.turn.off.user.info";
    private final String IS_ENABLE_BROWSER = "is.enable.browser";
    private final String IS_ENABLE_LOGIN_BY_MOCOM = "is.enable.login.by.another.system";

    public GoogleReviewPreference() {
        super();
    }

    //=======================================================//
    //================= Constructor ============================//
    //=======================================================//
    public static GoogleReviewPreference getInstance() {
       if (null==instance)
           instance = new GoogleReviewPreference();
        return instance;
    }

    //=======================================================//
    //================= File name ============================//
    //=======================================================//
    @Override
    protected String getFileNamePrefers() {
        return "google_review";
    }

    //======================================================//
    //=================== Function =========================//
    //======================================================//

    public void saveTurnOffVersion(String version) {
        getEditor().putString(TURN_OFF_VERSION, String.valueOf(version))
                .commit();
    }

    /**
     * @return Return true if current version greater than or same server
     */
    public boolean isBelowTurnOffVersion() {
        String turnOffVersion = getPreferences()
                .getString(TURN_OFF_VERSION, "");
        if (TextUtils.isEmpty(turnOffVersion)) {
            return true;
        }

        String currentVersion = SystemUtils.getAppVersionName(AppController.getAppContext());
        if (TextUtils.isEmpty(currentVersion)) {
            return true;
        }

        String[] turnOffElements = turnOffVersion.split("\\.");
        String[] currentElements = currentVersion.split("\\.");

        int size = currentElements.length;
        boolean isSizeTurnOffMoreThanCurrent = true;
        if (size >= turnOffElements.length) {
            size = turnOffElements.length;
            isSizeTurnOffMoreThanCurrent = false;
        }

        for (int i = 0; i < size; i++) {
            int turnOffValue = 0;
            int currentValue = 0;

            try {
                turnOffValue = Integer.parseInt(turnOffElements[i]);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                return false;
            }

            try {
                currentValue = Integer.parseInt(currentElements[i]);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                return true;
            }

            if (currentValue < turnOffValue) {
                return true;
            } else if (currentValue > turnOffValue) {
                return false;
            }
        }

        return isSizeTurnOffMoreThanCurrent;
    }

    public void saveEnableGetFreePoint(boolean isTurnOn) {
        getEditor().putBoolean(IS_ENABLE_GET_FREE_POINT, isTurnOn).commit();
    }

    public boolean isEnableGetFreePoint() {
        return getPreferences().getBoolean(IS_ENABLE_GET_FREE_POINT, false)
                || isBelowTurnOffVersion();
    }

    public void saveIsTurnOffUserInfo(boolean isTurnOn) {
        getEditor().putBoolean(IS_TURN_OFF_USER_INFO, isTurnOn).commit();
    }

    public boolean isTurnOffUserInfo() {
        return getPreferences().getBoolean(IS_TURN_OFF_USER_INFO, false)
                || isBelowTurnOffVersion();
    }

    public void saveEnableLoginByAnotherSystem(boolean isTurnOn) {
        getEditor().putBoolean(IS_ENABLE_LOGIN_BY_MOCOM, isTurnOn).commit();
    }

    public boolean isEnableLoginByAnotherSystem() {
        return getPreferences().getBoolean(IS_ENABLE_LOGIN_BY_MOCOM, false)
                || isBelowTurnOffVersion();
    }

    public void saveEnableBrowser(boolean isTurnOn) {
        getEditor().putBoolean(IS_ENABLE_BROWSER, isTurnOn).commit();
    }

    public boolean isEnableBrowser() {
        return getPreferences().getBoolean(IS_ENABLE_BROWSER, false)
                || isBelowTurnOffVersion();
    }

    public void clearAll() {
        getEditor().clear().commit();
    }
}