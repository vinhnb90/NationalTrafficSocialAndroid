package com.vn.ntsc.repository.preferece;


import com.vn.ntsc.R;
import com.vn.ntsc.repository.model.login.AuthenticationBean;
import com.vn.ntsc.utils.Constants;
import com.vn.ntsc.utils.LogUtils;

/**
 * Created by nankai on 2/10/2017.
 */

public class Preferences extends BasePrefers {

    private static Preferences instance;

    //ADID
    public static final String KEY_ADID = "key.adid";
    public static final String KEY_GPS_ADID = "key.gps.adid";
    //=======================================================//
    //================= variable ============================//
    //=======================================================//
    private static final String KEY_HOME_PAGE_URL = "home_page_url";
    private static final String KEY_GCM_PER_APP_VERSION = "gcm_per_app_version";
    private static final String KEY_FCM_PER_APP_VERSION = "fcm_per_app_version";
    private static final String KEY_GCM_REGISTRATION_ID = "gcm_registrationId";
    private static final String KEY_FIRE_BASE_REGISTRATION_ID = "firebase_registrationId";
    private static final String KEY_DIRTY_WORD_VERSION = "dirty_word_version";
    private static final String KEY_SEND_CM_CODE = "send_cm_code";
    private static final String KEY_MEET_PEOPLE_LIST_TYPE = "meet_people_list";
    private static final String KEY_DISTANCE_UNIT = "distance_unit";
    private static final String KEY_BUZZ_TAB = "buzz_tab";
    private static final String KEY_UNLOCK_WHO_CHECK_ME_OUT_POINTS = "unlock_who_check_me_out_points";
    private static final String KEY_RATE_DISTRIBUTION_POINTS = "rate_distribution_points";
    private static final String KEY_UNLOCK_FAVORITE_POINTS = "unlock_favorite_points";
    private static final String KEY_INVITE_FRIEND_POINTS = "invite_friend_points";
    private static final String KEY_ONLINE_ALERT_POINTS = "online_alert_points";
    private static final String KEY_DAILY_BONUS_POINTS = "daily_bonus_points";
    private static final String KEY_SAVE_IMAGE_POINTS = "save_image_points";
    private static final String KEY_WINK_BOMB_POINTS = "wink_bomb_points";
    private static final String KEY_BACKSTAGE_PRICE = "backstage_price";
    private static final String KEY_BACKSTAGE_BONUS = "backstage_bonus";
    private static final String KEY_COMMENT_POINT = "comment_ponit";
    private static final String KEY_CHAT_POINT = "chat_ponit";
    private static final String KEY_VIEW_IMAGE_POINT = "view_image_point";
    private static final String KEY_WATCH_VIDEO_POINT = "watch_video_point";
    private static final String KEY_LISTEN_AUDIO_POINT = "listen_audio";
    private static final String KEY_TIME_LOOK_AT_ME = "time_look_at_me";
    private static final String KEY_TIME_CHECK_OUT = "time_check_out";
    private static final String KEY_TIME_FAVOURITE = "time_favourite";
    private static final String KEY_TIME_BACKSTAGE = "time_backstage";
    private static final String KEY_LOGIN_TIME = "login_time";
    private static final String KEY_IS_INSTALLED = "is_installed";
    private static final String KEY_IS_ATTACH_CMCODE = "is_attach_cmcode";
    private static final String KEY_IS_TRACKED_APPTIZER = "is_tracked_apptizer";
    private static final String KEY_KEYBOARD_HEIGHT = "key_keyboard_height";
    public static String TAG = Preferences.class.getSimpleName();

    public Preferences() {
        super();
    }

    //=======================================================//
    //================= Constructor ========================//
    //=======================================================//
    public static Preferences getInstance() {
        if (instance == null)
            instance = new Preferences();
        return instance;
    }

    //=======================================================//
    //================= File name ============================//
    //=======================================================//
    @Override
    protected String getFileNamePrefers() {
        return "andG_preferences";
    }

    //======================================================//
    //=================== Function =========================//
    //======================================================//
    public void saveTimeSetting(AuthenticationBean authenData) {
        int checkoutTime = authenData.checkoutTime;
        int favouriteTime = authenData.favouriteTime;
        int backstageTime = authenData.backstageTime;
        String lookAtMeTime = authenData.lookAtMeTime;

        saveTimeCheckout(checkoutTime);
        saveTimeFavourite(favouriteTime);
        saveTimeBackstage(backstageTime);
        saveTimeLookAtMe(lookAtMeTime);
    }

    public void savePointSetting(AuthenticationBean authenData) {
        int backstagePrice = authenData.backstagePrice;
        int backstageBonus = authenData.backstageBonus;
        int commentPoint = authenData.commentPoint;
        int chatPoint = authenData.chatPoint;
        int dailyBonusPoints = authenData.dailyBonusPoints;
        int saveImagePoints = authenData.saveImagePoints;
        int unlockWhoCheckMeOutPoints = authenData
                .unlockWhoCheckMeOutPoints;
        int unlockFavoritePoints = authenData.unlockFavoritePoints;
        int onlineAlertPoints = authenData.onlineAlertPoints;
        int winkBombPoints = authenData.winkBombPoints;
        int inviteFriendPoints = authenData.inviteFriendPoints;
        double rateDistributionPoints = authenData.rateDistributionPoints;

        saveBackstagePrice(backstagePrice);
        saveBackstageBonus(backstageBonus);
        saveCommentPoint(commentPoint);
        saveChatPoint(chatPoint);
        saveDailyBonusPoints(dailyBonusPoints);
        saveSaveImagePoints(saveImagePoints);
        saveUnlockWhoCheckMeOutPoints(unlockWhoCheckMeOutPoints);
        saveUnlockFavoritePoints(unlockFavoritePoints);
        saveOnlineAlertPoints(onlineAlertPoints);
        saveWinkBombPoints(winkBombPoints);
        saveInviteFriendPoints(inviteFriendPoints);
        saveRateDistributionPoints(rateDistributionPoints);
    }

    public int getDistanceUnit() {
        return getPreferences().getInt(KEY_DISTANCE_UNIT,
                Constants.DISTANCE_UNIT_KILOMETER);
    }

    public String getDistanceUnitInString() {
        int unit = getDistanceUnit();
        String unitInString = "";

        switch (unit) {
            case Constants.DISTANCE_UNIT_KILOMETER:
                unitInString = mContext
                        .getString(R.string.common_kilometer_abbreviation);
                break;
            default:
                unitInString = mContext
                        .getString(R.string.common_mile_abbreviation);
                break;
        }

        return unitInString;
    }

    public String getFirebaseRegistrationId() {
        return getPreferences().getString(KEY_FIRE_BASE_REGISTRATION_ID, "");
    }

    public void setFirebaseRegistrationId(String id) {
        getEditor().putString(KEY_FIRE_BASE_REGISTRATION_ID, id).commit();
    }

    public void saveFcmPerAppVersion(int version) {
        getEditor().putInt(KEY_FCM_PER_APP_VERSION, version).commit();
    }

    public int getFcmPerAppVersion() {
        return getPreferences().getInt(KEY_FCM_PER_APP_VERSION, 0);
    }

    // ====== ====== gps ad id ===== ======
    public void saveGPSADID(String adid) {
        getEditor().putString(KEY_GPS_ADID, adid).commit();
    }

    public String getGPSADID() {
        return getPreferences().getString(KEY_GPS_ADID, "");
    }

    public void saveAdid(String adid) {
        getEditor().putString(KEY_ADID, adid).commit();
    }

    public String getAdid() {
        return getPreferences().getString(KEY_ADID, "");
    }


    public void saveDistanceUnit(int distanceUnit) {
        LogUtils.d(TAG, "DistanceUnit: " + distanceUnit);
        getEditor().putInt(KEY_DISTANCE_UNIT, distanceUnit).commit();
    }

    public boolean getMeetPeopleListType() {
        return getPreferences().getBoolean(KEY_MEET_PEOPLE_LIST_TYPE,
                false);
    }

    public boolean saveMeetPeopleListType(boolean value) {
        LogUtils.d(TAG, "MeetPeopleListType:" + value);
        return getEditor().putBoolean(KEY_MEET_PEOPLE_LIST_TYPE, value)
                .commit();
    }

    public boolean saveCommentPoint(int point) {
        return getEditor().putInt(KEY_COMMENT_POINT, point).commit();
    }

    public int getCommentPoint() {
        return getPreferences().getInt(KEY_COMMENT_POINT, 0);
    }

    public boolean saveChatPoint(int point) {
        return getEditor().putInt(KEY_CHAT_POINT, point).commit();
    }

    public int getChatPoint() {
        return getPreferences().getInt(KEY_CHAT_POINT, 0);
    }

    public boolean saveViewImageChatPoint(int point) {
        return getEditor().putInt(KEY_VIEW_IMAGE_POINT, point).commit();
    }

    public int getViewImageChatPoint() {
        return getPreferences().getInt(KEY_VIEW_IMAGE_POINT, 0);
    }

    public boolean saveWatchVideoChatPoint(int point) {
        return getEditor().putInt(KEY_WATCH_VIDEO_POINT, point).commit();
    }

    public int getWatchVideoChatPoint() {
        return getPreferences().getInt(KEY_WATCH_VIDEO_POINT, 0);
    }

    public boolean saveListenAudioChatPoint(int point) {
        return getEditor().putInt(KEY_LISTEN_AUDIO_POINT, point).commit();
    }

    public int getListenAudioChatPoint() {
        return getPreferences().getInt(KEY_LISTEN_AUDIO_POINT, 0);
    }

    public void saveBuzzTab(String tab) {
        getEditor().putString(KEY_BUZZ_TAB, tab).commit();
    }

    public String getBuzzTab() {
        //TODO
//        return getPreferences().getString(KEY_BUZZ_TAB,
//                BuzzFragment.TAB_LOCAL);
        return null;
    }

    public boolean saveOnlineAlertPoints(int onlineAlertPoints) {
        return getEditor().putInt(KEY_ONLINE_ALERT_POINTS, onlineAlertPoints)
                .commit();
    }

    public int getOnlineAlertPoints() {
        return getPreferences().getInt(KEY_ONLINE_ALERT_POINTS, 0);
    }

    public boolean saveDailyBonusPoints(int dailyBonusPoints) {
        return getEditor().putInt(KEY_DAILY_BONUS_POINTS, dailyBonusPoints)
                .commit();
    }

    public int getDailyBonusPoints() {
        return getPreferences().getInt(KEY_DAILY_BONUS_POINTS, 0);
    }

    public boolean saveSaveImagePoints(int saveImagePoints) {
        return getEditor().putInt(KEY_SAVE_IMAGE_POINTS, saveImagePoints)
                .commit();
    }

    public int getSaveImagePoints() {
        return getPreferences().getInt(KEY_SAVE_IMAGE_POINTS, 0);
    }

    public boolean saveUnlockWhoCheckMeOutPoints(int unlockWhoCheckMeOutPoints) {
        return getEditor().putInt(KEY_UNLOCK_WHO_CHECK_ME_OUT_POINTS,
                unlockWhoCheckMeOutPoints).commit();
    }

    public int getUnlockWhoCheckMeOutPoints() {
        return getPreferences().getInt(
                KEY_UNLOCK_WHO_CHECK_ME_OUT_POINTS, 0);
    }

    public boolean saveUnlockFavoritePoints(int unlockFavoritePoints) {
        return getEditor().putInt(KEY_UNLOCK_FAVORITE_POINTS,
                unlockFavoritePoints).commit();
    }

    public int getUnlockFavoritePoints() {
        return getPreferences().getInt(KEY_UNLOCK_FAVORITE_POINTS, 0);
    }

    public boolean saveWinkBombPoints(int winkBombPoints) {
        return getEditor().putInt(KEY_WINK_BOMB_POINTS, winkBombPoints)
                .commit();
    }

    public int getWinkBombPoints() {
        return getPreferences().getInt(KEY_WINK_BOMB_POINTS, 0);
    }

    public boolean saveInviteFriendPoints(int inviteFriendPoints) {
        return getEditor().putInt(KEY_INVITE_FRIEND_POINTS, inviteFriendPoints)
                .commit();
    }

    public int getInviteFriendPoints() {
        return getPreferences().getInt(KEY_INVITE_FRIEND_POINTS, 0);
    }

    public boolean saveRateDistributionPoints(double rateDistributionPoints) {
        LogUtils.i(TAG, "save rate=" + rateDistributionPoints);
        return getEditor().putString(KEY_RATE_DISTRIBUTION_POINTS,
                "" + rateDistributionPoints).commit();
    }

    public double getRateDistributionPoints() {
        String result = getPreferences().getString(
                KEY_RATE_DISTRIBUTION_POINTS, "0.0");
        LogUtils.i(TAG, "get rate=" + result);
        double rate = 0.0;
        try {
            rate = Double.parseDouble(result);
        } catch (NumberFormatException nfe) {
            rate = 0.0;
            nfe.printStackTrace();
        }
        return rate;
    }

    public void setGCMRegistrationId(String id) {
        getEditor().putString(KEY_GCM_REGISTRATION_ID, id).commit();
    }

    public String getGCMResitrationId() {
        return getPreferences().getString(KEY_GCM_REGISTRATION_ID, "");
    }

    public void saveSendCMCode() {
        getEditor().putBoolean(KEY_SEND_CM_CODE, true).commit();
    }

    public boolean isSendCMCode() {
        return getPreferences().contains(KEY_SEND_CM_CODE);
    }

    public void saveHomePageUrl(String url) {
        getEditor().putString(KEY_HOME_PAGE_URL, url).commit();
    }

    public String getHomePageUrl() {
        return getPreferences().getString(KEY_HOME_PAGE_URL, null);
    }

    public void removeHomePageUrl() {
        getEditor().remove(KEY_HOME_PAGE_URL).commit();
    }

    public int getVersionDirtyWord() {
        return getPreferences().getInt(KEY_DIRTY_WORD_VERSION, 0);
    }

    public void setVersionDirtyWord(int version) {
        getEditor().putInt(KEY_DIRTY_WORD_VERSION, version).commit();
    }

    public void saveGcmPerAppVersion(int version) {
        getEditor().putInt(KEY_GCM_PER_APP_VERSION, version).commit();
    }

    public int getGcmPerAppVersion() {
        return getPreferences().getInt(KEY_GCM_PER_APP_VERSION, 0);
    }

    public void saveTimeLookAtMe(String time) {
        getEditor().putString(KEY_TIME_LOOK_AT_ME, time).commit();
    }

    public String getTimeLookAtMe() {
        return getPreferences().getString(KEY_TIME_LOOK_AT_ME, "");
    }

    public void saveTimeCheckout(int time) {
        getEditor().putInt(KEY_TIME_CHECK_OUT, time).commit();
    }

    public int getTimeCheckout() {
        return getPreferences().getInt(KEY_TIME_CHECK_OUT, 0);
    }

    public void saveTimeBackstage(int time) {
        getEditor().putInt(KEY_TIME_BACKSTAGE, time).commit();
    }

    public int getTimeBackstage() {
        return getPreferences().getInt(KEY_TIME_BACKSTAGE, 0);
    }

    public void saveBackstagePrice(int backstagePrice) {
        getEditor().putInt(KEY_BACKSTAGE_PRICE, backstagePrice).commit();
    }

    public int getBackstagePrice() {
        return getPreferences().getInt(KEY_BACKSTAGE_PRICE, 0);
    }

    public void saveBackstageBonus(int backstageBonus) {
        getEditor().putInt(KEY_BACKSTAGE_BONUS, backstageBonus).commit();

    }

    public int getBackstageBonus() {
        return getPreferences().getInt(KEY_BACKSTAGE_BONUS, 0);
    }

    public void saveTimeFavourite(int time) {
        getEditor().putInt(KEY_TIME_FAVOURITE, time).commit();
    }

    public int getTimeFavourite() {
        return getPreferences().getInt(KEY_TIME_FAVOURITE, 0);
    }

    public void saveTimeRelogin(long time) {
        getEditor().putLong(KEY_LOGIN_TIME, time).commit();
    }

    public long getTimeRelogin() {
        return getPreferences().getLong(KEY_LOGIN_TIME, 0);
    }

    public boolean haslogginedEarly() {
        long currentTime = System.currentTimeMillis();
        long reloginTime = getTimeRelogin();
        long threslod = 20 * 1000;
        return (currentTime - reloginTime) < threslod;
    }

    public boolean saveIsInstalled() {
        return getEditor().putBoolean(KEY_IS_INSTALLED, true).commit();
    }

    public boolean isInstall() {
        return getPreferences().getBoolean(KEY_IS_INSTALLED, false);
    }

    public void removeIsInstall() {
        getEditor().remove(KEY_IS_INSTALLED).commit();
    }

    public boolean saveIsAttachCmcode() {
        return getEditor().putBoolean(KEY_IS_ATTACH_CMCODE, true).commit();
    }

    public boolean isAttachCmcode() {
        return getPreferences().getBoolean(KEY_IS_ATTACH_CMCODE, false);
    }

    public void removeIsAttachCmcode() {
        getEditor().remove(KEY_IS_ATTACH_CMCODE).commit();
    }

    public boolean saveIsTrackedApptizer() {
        return getEditor().putBoolean(KEY_IS_TRACKED_APPTIZER, true).commit();
    }

    public boolean isTrackedApptizer() {
        return getPreferences().getBoolean(KEY_IS_TRACKED_APPTIZER, false);
    }

    public void removeIsTrackedApptizer() {
        getEditor().remove(KEY_IS_TRACKED_APPTIZER).commit();
    }

    public void saveKeyboardHeight(int keyboardHeight) {
        getEditor().putInt(KEY_KEYBOARD_HEIGHT, keyboardHeight);
    }

    /**
     * @param defaultKeyboardHeight
     * @return
     */
    public int getKeyboardHeight(int defaultKeyboardHeight) {
        return getPreferences().getInt(KEY_KEYBOARD_HEIGHT, defaultKeyboardHeight);
    }

}