package com.vn.ntsc.repository.preferece;

import android.text.TextUtils;

import com.vn.ntsc.repository.model.login.AuthenticationBean;
import com.vn.ntsc.utils.Constants;
import com.vn.ntsc.utils.LogUtils;

/**
 * Created by nankai on 2/9/2017.
 */

public class UserPreferences extends BasePrefers {

    private static UserPreferences instance;

    public static final String FILE_PREFERENCES = "user_preference";

    //=======================================================//
    //================= variable ============================//
    //=======================================================//

    public static final String KEY_AGE_VERIFICATION = "key.age.verification";

    public static final String KEY_FACEBOOK_LOGIN = "key.fb.login";
    public static final String KEY_FACEBOOK_ID = "key.facebookid";

    public static final String KEY_LOGOUT = "log_out";
    public static final String KEY_IS_LOGIN = "key.is.login";

    public static final String KEY_FRIEND_ID = "key.friendid";
    public static final String KEY_CURRENT_CHAT_FRIEND_ID = "key.current.chat.friend.id";

    public static final String KEY_FINISH_REGISTER_FLAG = "key.finish.register.flag";

    public static final String KEY_UNREAD_MESSAGE = "key.number.unread.message";
    public static final String KEY_NUMBER_NOTIFY = "key.number.notify";
    public static final String KEY_NUMBER_NOTIFY_ONLINE_ALARM = "key.number.notify.online.alarm";
    public static final String KEY_NUMBER_POINT = "key.number.point";

    public static final String KEY_TOKEN = "key.token";
    public static final String KEY_GENDER = "key.gender";
    public static final String KEY_BIRTHDAY = "key.birthday";
    public static final String KEY_UPDATE_EMAIL = "key.update.email";
    public static final String KEY_USER_ID = "key.user.id";
    public static final String KEY_USER_NAME = "key.user.name";
    public static final String KEY_EMAIL = "key.email";
    public static final String KEY_PASSWORD = "key.password";
    public static final String KEY_AVA = "key.ava";
    public static final String KEY_IDENTIFY_NO = "key.identify.no";
    public static final String KEY_JOB = "key.job";
    public static final String KEY_REGION = "key.region";
    public static final String KEY_BODY_TYPE = "key.body.type";
    public static final String KEY_HOBBY = "key.hobby";
    public static final String KEY_ABOUT = "key.about";
    public static final String KEY_PHONE_NO = "key.phone";

    public static String TAG = UserPreferences.class.getSimpleName();

    public UserPreferences() {
        super();
    }

    //=======================================================//
    //================= Constructor =========================//
    //=======================================================//
    public static UserPreferences getInstance() {
        if (null == instance)
            instance = new UserPreferences();
        return instance;
    }

    //=======================================================//
    //================= File name ============================//
    //=======================================================//
    @Override
    protected String getFileNamePrefers() {
        return FILE_PREFERENCES;
    }

    public void saveSuccessLoginData(AuthenticationBean authenData, boolean isFirstLogin) {
        if (authenData == null) {
            return;
        }
        // Toogle logout flag
        setIsLogout(false);
        setIsLogin(true);

        // Basic data
        String token = authenData.token;
        String userName = authenData.userName;
        String userId = authenData.userId;
        String avatar = authenData.avartar;

        int gender = authenData.gender;
        int numMyChat = authenData.numMyChat;
        int numNotification = authenData.numNotification;

        saveBasicData(token, userName, userId, avatar, gender, numMyChat, numNotification);

        // save email, birthday, identify number if they not null or empty
        saveEmail(authenData.email);
        saveBirthday(authenData.birthDay);
        saveIdentifyNo(authenData.identifyNumber);

        // job, region, body type, hobby, message
        saveJob(authenData.job);
        saveBody(authenData.bodyType);
        savePhoneNumber(authenData.phoneNumber);
        saveHobby(authenData.hobby);
        saveRegion(authenData.region);
        saveAbout(authenData.message);

        int finishRegister = authenData.finishRegister;
        int verificationFlag = authenData.verificationFlag;
        int isUpdateEmail = authenData.isUpdateEmail;
        saveUpdateEmailFlag(isUpdateEmail);
        saveFinishRegister(finishRegister);
        saveAgeVerification(verificationFlag);
    }

    private void saveBasicData(String token, String name, String id,
                               String avatar, int gender, int unreadMessage, int numberNotify) {
        if (!TextUtils.isEmpty(token)) {
            saveToken(token);
        }
        if (!TextUtils.isEmpty(id)) {
            saveUserId(id);
        }
        if (!TextUtils.isEmpty(name)) {
            saveUserName(name);
        }
        if (!TextUtils.isEmpty(avatar)) {
            saveAva(avatar);
        }
        saveGender(gender);
        saveNumberUnreadMessage(unreadMessage);
        saveNotifyNum(numberNotify);
    }

    /**
     * @return AuthenticationBean for set edit profile
     */
    public AuthenticationBean getAuthenticationBean() {

        AuthenticationBean authenticationBean = new AuthenticationBean();
        authenticationBean.userId = getUserId();
        authenticationBean.facebookId = getFacebookId();
        authenticationBean.email = getRegEmail();
        authenticationBean.phoneNumber = getPhoneNumber();
        authenticationBean.message = getAbout();
        authenticationBean.identifyNumber = getIdentifyNo();
        authenticationBean.avartar = getAva();
        authenticationBean.email = getRegEmail();
        authenticationBean.userName = getUserName();
        authenticationBean.birthDay = getBirthday();
        authenticationBean.gender = getGender();
        authenticationBean.job = getJob();
        authenticationBean.region = getRegion();
        authenticationBean.bodyType = getBody();
        authenticationBean.hobby = getHobby();
        authenticationBean.numPoint = getNumberPoint();
        authenticationBean.numNotification = getNotifyNum();

        return authenticationBean;
    }

    // ============ Register status flag =========== //
    public boolean saveFinishRegister(int finish) {
        LogUtils.d(TAG, "Fisnish register flag: " + finish);
        return getEditor().putInt(KEY_FINISH_REGISTER_FLAG, finish).commit();
    }

    public int getFinishRegister() {
        return getPreferences().getInt(KEY_FINISH_REGISTER_FLAG,
                Constants.FINISH_REGISTER_NO);
    }

    // ============ email update flag =========== //
    public boolean saveUpdateEmailFlag(int update) {
        return getEditor().putInt(KEY_UPDATE_EMAIL, update).commit();
    }

    public int geUpdateEmailFlag() {
        return getPreferences().getInt(KEY_UPDATE_EMAIL, 0);
    }

    // ============ age verification =========== //
    public boolean saveAgeVerification(int code) {
        LogUtils.d(TAG, "Age veification status: " + code);
        return getEditor().putInt(KEY_AGE_VERIFICATION, code).commit();
    }

    public int getAgeVerification() {
        return getPreferences().getInt(KEY_AGE_VERIFICATION, -3);
    }

    // ============ number point =========== //
    public boolean saveNumberPoint(int gender) {
        LogUtils.d(TAG, "point: " + gender);
        return getEditor().putInt(KEY_NUMBER_POINT, gender).commit();
    }

    public int getNumberPoint() {
        return getPreferences().getInt(KEY_NUMBER_POINT, -1);
    }

    // ============ User gender =========== //
    public boolean saveGender(int gender) {
        LogUtils.d(TAG, "Gender: " + gender);
        return getEditor().putInt(KEY_GENDER, gender).commit();
    }

    public int getGender() {
        return getPreferences().getInt(KEY_GENDER, -1);
    }

    // ============ User ID =========== //
    public boolean saveUserId(String userId) {
        LogUtils.d(TAG, "User id: " + userId);
        return getEditor().putString(KEY_USER_ID, userId).commit();
    }

    public String getUserId() {
        return getPreferences().getString(KEY_USER_ID, "");
    }

    // ============ User name =========== //
    public void saveUserName(String userName) {
        LogUtils.d(TAG, "User name: " + userName);
        getEditor().putString(KEY_USER_NAME, userName).commit();
    }

    public String getUserName() {
        return getPreferences().getString(KEY_USER_NAME, "");
    }

    // ============ Avatar Id =========== //
    public boolean saveAva(String avaUrl) {
        LogUtils.d(TAG, "Avata url: " + avaUrl);
        return getEditor().putString(KEY_AVA, avaUrl).commit();
    }

    public String getAva() {
        return getPreferences().getString(KEY_AVA, "");
    }

    // ============ Current friend chat with =========== //
    public boolean saveCurrentFriendChat(String userId) {
        LogUtils.i(TAG, "Current friend chat Id: " + userId);
        return getEditor().putString(KEY_CURRENT_CHAT_FRIEND_ID, userId).commit();
    }

    public String getCurrentFriendChat() {
        return getPreferences().getString(KEY_CURRENT_CHAT_FRIEND_ID, "");
    }

    /**
     * Must pass currentUserIdToSend. Duoc su dung khi xay ra su kien onDetach()
     * o man hinh chat. (tranh truong hop: dang chat vs A, chuyen sang chat vs B
     * thi onDetach() o A duoc goi sau khi onStart() cua B -> dan toi truong hop
     * userIdToSend bi remove
     */
    public void removeCurrentFriendChat() {
        getEditor().remove(KEY_CURRENT_CHAT_FRIEND_ID).commit();
    }

    // ============ Token =========== //
    public boolean saveToken(String token) {
        LogUtils.d(TAG, "Token: " + token);
        return getEditor().putString(KEY_TOKEN, token).commit();
    }

    public String getToken() {
        return getPreferences().getString(KEY_TOKEN, "");
    }

    // Remove login status of user
    public void removeToken() {
        getEditor().remove(KEY_TOKEN).commit();
    }

    // =========== Region =========== //
    public boolean saveRegion(int region) {
        return getEditor().putInt(KEY_REGION, region).commit();
    }

    public int getRegion() {
        return getPreferences().getInt(KEY_REGION, -1);
    }

    // ============ about =========== //
    public boolean saveAbout(String about) {
        LogUtils.i(TAG, "Save about " + about);
        return getEditor().putString(KEY_ABOUT, about).commit();
    }

    public String getAbout() {
        return getPreferences().getString(KEY_ABOUT, "");
    }

    // ============ Hobby =========== //
    public boolean saveHobby(String hobby) {
        LogUtils.i(TAG, "Save hobby " + hobby);
        return getEditor().putString(KEY_HOBBY, hobby).commit();
    }

    public String getHobby() {
        return getPreferences().getString(KEY_HOBBY, "");
    }

    // ============ Phone number =========== //
    public boolean savePhoneNumber(String phoneNumber) {
        LogUtils.i(TAG, "Save phone number " + phoneNumber);
        return getEditor().putString(KEY_PHONE_NO, phoneNumber).commit();
    }

    public String getPhoneNumber() {
        return getPreferences().getString(KEY_PHONE_NO, "");
    }

    // ============ BODY =========== //
    public boolean saveBody(int body) {
        LogUtils.i(TAG, "Save body " + body);
        return getEditor().putInt(KEY_BODY_TYPE, body).commit();
    }

    public int getBody() {
        return getPreferences().getInt(KEY_BODY_TYPE, -1);
    }

    // ============ Jobs =========== //
    public boolean saveJob(int job) {
        LogUtils.i(TAG, "Save job " + job);
        return getEditor().putInt(KEY_JOB, job).commit();
    }

    public int getJob() {
        return getPreferences().getInt(KEY_JOB, -1);
    }

    // ============ Email =========== //
    public boolean saveEmail(String email) {
        LogUtils.i(TAG, "Save email " + email);
        removeFacebookId();
        return getEditor().putString(KEY_EMAIL, email).commit();
    }

    public String getRegEmail() {
        String email = getPreferences().getString(KEY_EMAIL, "");
        LogUtils.i(TAG, "Get email: " + email);
        return email;
    }

    public void removeEmail() {
        getEditor().remove(KEY_EMAIL).commit();
    }

    // ====== ====== friend id ===== ======

    public boolean saveFriendId(String id) {
        return getEditor().putString(KEY_FRIEND_ID, id).commit();
    }

    public String getFriendId() {
        return getPreferences().getString(KEY_FRIEND_ID, "");
    }

    // ====== ====== account accounts type ===== ======

    public void setIsFacebookLogin(boolean isAccLogin) {
        getEditor().putBoolean(KEY_FACEBOOK_LOGIN, isAccLogin).commit();
    }

    public boolean isFacebookLogin() {
        return getPreferences().getBoolean(KEY_FACEBOOK_LOGIN, false);
    }

    // ====== ====== Facebook ===== ======
    public boolean saveFacebookId(String facebookId) {
        removeEmail();
        return getEditor().putString(KEY_FACEBOOK_ID, facebookId).commit();
    }

    public String getFacebookId() {
        return getPreferences().getString(KEY_FACEBOOK_ID, "");
    }

    public void removeFacebookId() {
        getEditor().remove(KEY_FACEBOOK_ID).commit();
    }

    // ====== ====== password ===== ======
    public boolean savePassword(String password) {
        return getEditor().putString(KEY_PASSWORD, password).commit();
    }

    public String getPassword() {
        String currentPassword = getPreferences().getString(KEY_PASSWORD,
                "");
        if (!TextUtils.isEmpty(currentPassword)) {
            return currentPassword;
        }

        return getPreferences().getString(KEY_PASSWORD, "");
    }

    // ====== ====== Login & logout ===== ======
    public boolean isLogout() {
        return getPreferences().getBoolean(KEY_LOGOUT, true);
    }

    public void setIsLogout(boolean isLogout) {
        getEditor().putBoolean(KEY_LOGOUT, isLogout).commit();
    }

    public boolean isLogin() {
        return getPreferences().getBoolean(KEY_IS_LOGIN, false);
    }

    public void setIsLogin(boolean isLogin) {
        getEditor().putBoolean(KEY_IS_LOGIN, isLogin).commit();
    }

    public void clear() {
        getEditor().clear().commit();
    }

    // ============ User birthday ===== ======
    public void saveBirthday(String birthday) {
        getEditor().putString(KEY_BIRTHDAY, birthday).commit();
    }

    public String getBirthday() {
        return getPreferences().getString(KEY_BIRTHDAY, "");
    }

    // ============ User identify no ===== ======
    public void saveIdentifyNo(String identify) {
        getEditor().putString(KEY_IDENTIFY_NO, identify).commit();
    }

    public String getIdentifyNo() {
        return getPreferences().getString(KEY_IDENTIFY_NO, "");
    }

    // ====== ====== Number of unread message ===== ======
    public void saveNumberUnreadMessage(int unreadMessage) {
        LogUtils.d(TAG, "Number of unread message: " + unreadMessage);
        getEditor().putInt(KEY_UNREAD_MESSAGE, unreadMessage).commit();
    }

    public void increaseUnreadMessage(int unreadMessage) {
        int currenMsg = getNumberUnreadMessage();
        currenMsg += unreadMessage;
        saveNumberUnreadMessage(currenMsg);
    }

    public int getNumberUnreadMessage() {
        return getPreferences().getInt(KEY_UNREAD_MESSAGE, 0);
    }

    public int getUnreadMessage() {
        return getPreferences().getInt(KEY_UNREAD_MESSAGE, 0);
    }

    // ====== ====== notify ===== ======
    public void saveNotifyNum(int notify) {
        LogUtils.d(TAG, "Number of notify: " + notify);
        getEditor().putInt(KEY_NUMBER_NOTIFY, notify).commit();
    }

    public int getNotifyNum() {
        return getPreferences().getInt(KEY_NUMBER_NOTIFY, 0);
    }

    // ====== ====== notify online alarm ===== ======
    public void saveNotifyOnlineAlarmNum(int onlineAlarmNum) {
        getEditor().putInt(KEY_NUMBER_NOTIFY_ONLINE_ALARM, onlineAlarmNum).commit();
    }

    public int getNotifyOnlineAlarmNum() {
        return getPreferences().getInt(KEY_NUMBER_NOTIFY_ONLINE_ALARM, 0);
    }

}
