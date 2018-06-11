package com.vn.ntsc.ui.splash;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.provider.Settings;
import android.support.constraint.ConstraintLayout;
import android.text.TextUtils;
import android.view.View;

import com.vn.ntsc.R;
import com.vn.ntsc.app.AppController;
import com.vn.ntsc.core.model.ServerResponse;
import com.vn.ntsc.core.views.BaseActivity;
import com.vn.ntsc.repository.model.applicationinfo.GetApplicationInfoResponse;
import com.vn.ntsc.repository.model.applicationinfo.GetUpdateInfoFlagRequest;
import com.vn.ntsc.repository.model.applicationinfo.GetUpdateInfoFlagResponse;
import com.vn.ntsc.repository.model.chat.sql.DatabaseHelper;
import com.vn.ntsc.repository.model.installcount.InstallCountRequest;
import com.vn.ntsc.repository.model.installcount.InstallCountResponse;
import com.vn.ntsc.repository.model.user.BannedWordResponse;
import com.vn.ntsc.repository.model.user.GetUserStatusRequest;
import com.vn.ntsc.repository.model.user.GetUserStatusResponse;
import com.vn.ntsc.repository.model.user.SetCareUserInfoRequest;
import com.vn.ntsc.repository.model.user.SetCareUserInfoResponse;
import com.vn.ntsc.repository.preferece.GoogleReviewPreference;
import com.vn.ntsc.repository.preferece.Preferences;
import com.vn.ntsc.repository.preferece.UserPreferences;
import com.vn.ntsc.ui.main.MainActivity;
import com.vn.ntsc.utils.DirtyWordUtils;
import com.vn.ntsc.utils.LogUtils;
import com.vn.ntsc.utils.Utils;
import com.vn.ntsc.utils.chats.ChatUtils;
import com.vn.ntsc.utils.keyboard.KeyboardUtils;
import com.vn.ntsc.utils.time.TimeUtils;

import java.util.List;
import java.util.TimeZone;

import butterknife.BindView;

public class SplashActivity extends BaseActivity<SplashPresenter> implements SplashContract.View {

    //================= variable ============================//
    private static final String SHORTCUT = "SHORTCUT";

    private static String TAG = SplashActivity.class.getSimpleName();
    @BindView(R.id.activity_splash_layout_splash)
    ConstraintLayout layout;

    //================= life cycle ==========================//
    @Override
    public int getLayoutId() {
        return R.layout.activity_splash;
    }

    @Override
    public void onCreateView(View rootView) {
        getModulesCommonComponent().inject(this);
        KeyboardUtils.hideSoftKeyboard(this);
        addShortcut();
    }

    @Override
    public void onViewReady() {
        //Default time zone
        TimeUtils.YYYYMMDDHHMMSS.setTimeZone(TimeZone.getTimeZone(TimeUtils.CURRENT_TIMELINE_ZONE_ID));

        // data sticker
        ChatUtils.setStickerData(ChatUtils.getEmojiInternal(AppController.getAppContext(), ChatUtils.STICKER));

        getPresenter().getApplicationInfo();
    }

    @Override
    public void onResume(View viewRoot) {
        super.onResume(viewRoot);
        // get app info will call after get banned word complete
        int bannedWordVersion = DatabaseHelper.getInstance(getApplicationContext()).getLastBannedWordVersion();
        getPresenter().getBannedWords(bannedWordVersion);

        Uri uri = getIntent().getData();
        if (uri != null && uri.getQueryParameter("id") != null && uri.getQueryParameter("pass") != null) {
            String token = UserPreferences.getInstance().getToken();
            String id = uri.getQueryParameter("id");
            String pass = uri.getQueryParameter("pass");
            SetCareUserInfoRequest careUserInfoRequest = new SetCareUserInfoRequest(token, id, pass);
            getPresenter().setCareUserInfo(careUserInfoRequest);
        }
    }

    //================= Function ============================//

    private void addShortcut() {
        SharedPreferences preferences = getSharedPreferences(SHORTCUT, MODE_PRIVATE);
        if (!preferences.getBoolean(SHORTCUT, false)) {
            //on Home screen
            Intent shortcutIntent = new Intent(getApplicationContext(),
                    SplashActivity.class);

            shortcutIntent.setAction(Intent.ACTION_MAIN);

            Intent addIntent = new Intent();
            addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
            addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, getResources().getString(R.string.app_name));
            addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
                    Intent.ShortcutIconResource.fromContext(getApplicationContext(),
                            R.mipmap.ic_launcher));
            addIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
            getApplicationContext().sendBroadcast(addIntent);

            SharedPreferences.Editor edit = preferences.edit();
            edit.putBoolean(SHORTCUT, true);
            edit.apply();
        }
    }

    public void startMainScreen() {
        //start MainActivity
        Intent intent = getIntent();
        String buzzId = null;
        if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            Uri uri = intent.getData();
            assert uri != null;
            List<String> listPath = uri.getPathSegments();
            if (listPath != null && listPath.size() == 2)
                buzzId = listPath.get(1);
        }

        MainActivity.launch(SplashActivity.this, buzzId);
    }

    private void checkInfoUpdated() {
        UserPreferences prefers = UserPreferences.getInstance();
        String token = prefers.getToken();
        int type = Utils.isNeededGetUserStatus();
        if (type != GetUserStatusRequest.TYPE_NONE) {
            requestGetUserStatus(type);
        } else if (TextUtils.isEmpty(token) && !TextUtils.isEmpty(prefers.getUserId())) {
            startMainScreen();
        } else {
            // Request user information updated
            GetUpdateInfoFlagRequest request = new GetUpdateInfoFlagRequest(
                    token);
            getPresenter().updateInfoFlagRequest(request);
        }
    }

    public void requestGetUserStatus(int type) {
        LogUtils.d(TAG, "requestGetUserStatus - " + type);
        UserPreferences prefers = UserPreferences.getInstance();
        String data = null;
        switch (type) {
            case GetUserStatusRequest.TYPE_EMAIL:
                data = prefers.getRegEmail();
                break;
            case GetUserStatusRequest.TYPE_FACEBOOK:
                data = prefers.getFacebookId();
                break;
            default:
                break;
        }

        if (data == null)
            return;

        GetUserStatusRequest request = new GetUserStatusRequest(type, data);
        getPresenter().getUserStatusRequest(request);
    }


    //================= server callback =====================//
    private void onResponseApplicationInfoError(GetApplicationInfoResponse body) {
        GoogleReviewPreference preference = GoogleReviewPreference.getInstance();
        preference.saveTurnOffVersion("");
        preference.saveEnableGetFreePoint(false);
        preference.saveEnableBrowser(false);
        preference.saveIsTurnOffUserInfo(false);
    }

    //----------------------- Server response ---------------------------------
    @Override
    public void onApplicationInfo(GetApplicationInfoResponse response) {
        GoogleReviewPreference preference = new GoogleReviewPreference();
        preference.saveTurnOffVersion(response.switchBrowserVersion);
        preference.saveEnableGetFreePoint(response.isGetFreePoint);
        preference.saveEnableBrowser(response.isSwitchBrowser);
        preference.saveIsTurnOffUserInfo(response.isTurnOffUserInfo);

        // Check application first install
        Preferences preferences = Preferences.getInstance();
        if (!preferences.isTrackedApptizer()) {
            preferences.saveIsTrackedApptizer();
        }

        if (preferences.isInstall()) {
            checkInfoUpdated();
        } else {
            // Request to count and check user info after count
            @SuppressLint("HardwareIds") String android_id = Settings.Secure.getString(getContentResolver(),
                    Settings.Secure.ANDROID_ID);
            InstallCountRequest installCountRequest = new InstallCountRequest(1, android_id);
            getPresenter().sendInstallCount(installCountRequest);
        }
    }

    @Override
    public void onInstallCount(InstallCountResponse response) {
        Preferences preferences = Preferences.getInstance();
        preferences.saveIsInstalled();
        checkInfoUpdated();
    }

    @Override
    public void onUpdateInfoFlag(GetUpdateInfoFlagResponse response) {
        UserPreferences pre = UserPreferences.getInstance();
        // save finish register flag for direction
        // finishRegisterFlag 0: no, 1: yes

        pre.saveFinishRegister(response.finishRegisterFlag);
        pre.saveUpdateEmailFlag(response.data.updateEmailFlag);
        LogUtils.e(TAG, "finishRegisterFlag:" + response.finishRegisterFlag + "| updateEmailFlag:" + response.data.updateEmailFlag);
        pre.saveAgeVerification(response.verificationFlag);
        startMainScreen();
    }

    @Override
    public void onUserStatus(GetUserStatusResponse response) {
        try {
            int status = response.userStatus;
            LogUtils.d(TAG, "onResponseGetUserStatus -- status : "
                    + status);
            if (status == GetUserStatusResponse.ACTIVE) {
                getPresenter().onAutoLogin(getApplicationContext());
            } else {
                startMainScreen();
            }
        } catch (Exception e) {
            e.printStackTrace();
            startMainScreen();
        }
    }

    @Override
    public void onCareUserInfo(SetCareUserInfoResponse response) {
        LogUtils.i(TAG, "Update location to server Success");
    }

    @Override
    public void onInvalid(ServerResponse response) {
        if (response instanceof InstallCountResponse) {
            checkInfoUpdated();
        } else if (response instanceof GetApplicationInfoResponse) {
            onResponseApplicationInfoError((GetApplicationInfoResponse) response);
        } else {
            startMainScreen();
        }
    }

    @Override
    public void onUpdateBannedWords(BannedWordResponse response) {
        if (response == null) return;
        BannedWordResponse.BannedObject bannedData = response.data;
        if (bannedData == null) return;

        DatabaseHelper.getInstance(getApplicationContext()).updateBannedWords(bannedData.getList(), bannedData.getVersion());
    }

    @Override
    public void onCompleteGetBannedWord() {
        DirtyWordUtils.init(this);
    }

    //-----------------------End Server response ---------------------------------

    @Override
    public boolean hasRegisterSocket() {
        return false;
    }

    @Override
    public boolean hasShowNotificationView() {
        return false;
    }
}
