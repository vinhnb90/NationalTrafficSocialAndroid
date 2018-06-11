package com.vn.ntsc.ui.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v4.util.PatternsCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.google.firebase.iid.FirebaseInstanceId;
import com.vn.ntsc.R;
import com.vn.ntsc.core.views.LoginFacebookActivity;
import com.vn.ntsc.repository.model.login.AuthenticationBean;
import com.vn.ntsc.repository.model.login.LoginByEmailRequest;
import com.vn.ntsc.repository.model.login.LoginByFacebookRequest;
import com.vn.ntsc.repository.model.login.LoginResponse;
import com.vn.ntsc.repository.preferece.GPSADIDPrefernces;
import com.vn.ntsc.repository.preferece.GoogleReviewPreference;
import com.vn.ntsc.repository.preferece.Preferences;
import com.vn.ntsc.repository.preferece.RememberLoginPreference;
import com.vn.ntsc.repository.preferece.UserPreferences;
import com.vn.ntsc.ui.forgotpassword.ForgotPasswordActivity;
import com.vn.ntsc.ui.main.MainActivity;
import com.vn.ntsc.ui.profile.edit.EditProfileActivity;
import com.vn.ntsc.utils.Constants;
import com.vn.ntsc.utils.LogUtils;
import com.vn.ntsc.utils.SystemUtils;
import com.vn.ntsc.utils.UserSetting;
import com.vn.ntsc.utils.Utils;
import com.vn.ntsc.utils.keyboard.KeyboardUtils;
import com.vn.ntsc.widget.toolbar.ToolbarTitleCenter;

import butterknife.BindView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

import static com.vn.ntsc.utils.SystemUtils.getDeviceId;
import static com.vn.ntsc.utils.Utils.encryptPassword;
import static com.vn.ntsc.utils.time.TimeUtils.getLoginTime;

/**
 * Created by nankai on 8/3/2017.
 */
public class LoginActivity extends LoginFacebookActivity<LoginPresenter> implements LoginContract.View, TextView.OnEditorActionListener {

    private static final String ELEMENT_LOGIN = "login_element";

    @BindView(R.id.toolbar)
    ToolbarTitleCenter mToolbar;
    @BindView(R.id.activity_login_email)
    EditText edtEmail;
    @BindView(R.id.activity_login_password)
    EditText edtPassword;
    @BindView(R.id.activity_login_remember)
    CheckBox rememberPwd;
    private boolean isRememberLogin;
    private String facebookId;
    private ProgressDialog mLoadingProgress;

    public static void launch(AppCompatActivity activity, View view) {
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, view, ELEMENT_LOGIN);
        Intent intent = new Intent();
        intent.setClass(activity, LoginActivity.class);
        activity.startActivity(intent, options.toBundle());
    }

    public static void launch(AppCompatActivity activity) {
        Intent intent = new Intent(activity, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
    }

    public static void launchClearTask(AppCompatActivity activity) {
        Intent intent = new Intent(activity, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    public void onCreateView(View rootView) {
        mLoadingProgress = new ProgressDialog(this);
        mLoadingProgress.setMessage(getResources().getString(R.string.loading));
        mLoadingProgress.setCanceledOnTouchOutside(false);
        mLoadingProgress.setCancelable(false);
        getModulesCommonComponent().inject(this);
        ViewCompat.setTransitionName(mToolbar, ELEMENT_LOGIN);
    }

    @Override
    public void onViewReady() {

        setSupportActionBar(mToolbar);
        mToolbar.setActionbar(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        edtPassword.setOnEditorActionListener(this);
        loadCachedBackgroundContainer(R.drawable.bg_login);
        restoreRememberInfo();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * when tick on remember login with email and password
     */
    @OnCheckedChanged(R.id.activity_login_remember)
    void onRememberStatusChanged(boolean isChecked) {
        this.isRememberLogin = isChecked;
    }

    /**
     * when click login with email and password
     */
    @OnClick(R.id.activity_login_login)
    void onClickLogin() {
        if (isValidParams()) {
            KeyboardUtils.hideSoftKeyboard(this);
            String email = edtEmail.getText().toString().trim();
            String pwd = edtPassword.getText().toString();
            pwd = encryptPassword(pwd);
            String notify_token = FirebaseInstanceId.getInstance().getToken();
            String appVersion = SystemUtils.getAppVersionName(this);
            String device_name = SystemUtils.getDeviceName();
            String os_version = SystemUtils.getAndroidOSVersion();
            String gps_adid = Preferences.getInstance().getGPSADID();
            LoginByEmailRequest loginRequest = new LoginByEmailRequest(email,
                    pwd, getDeviceId(), notify_token,
                    getLoginTime(), appVersion, device_name, os_version, gps_adid);
            getPresenter().loginByEmail(loginRequest);
        }
    }

    private boolean isValidParams() {
        String email = edtEmail.getText().toString();

        if (email.replace("\u3000", " ").trim().length() <= 0) {
            showConfirmDialog(R.string.email_invalid, null, false);
            return false;
        }

        if (!PatternsCompat.EMAIL_ADDRESS.matcher(email).matches()) {
            showConfirmDialog(R.string.email_invalid, null, false);
            return false;
        }

        int length = edtPassword.getText().toString().length();
        if (length < UserSetting.MIN_PASSWORD_LENGTH
                || length > UserSetting.MAX_PASSWORD_LENGTH) {
            showConfirmDialog(R.string.msg_common_invalid_password, null, false);
            return false;
        }

        return true;
    }

    /**
     * when click login with fb button
     */
    @OnClick(R.id.activity_login_login_with_fb)
    void onClickLoginWithFb() {
        loginWithFacebook();
    }

    @OnClick(R.id.activity_login_forgot_password)
    void onClickForgotPassword() {
        startActivity(new Intent(this, ForgotPasswordActivity.class));
    }

    /**
     * restore remember info from share preference
     */
    private void restoreRememberInfo() {
        Pair<String, String> emailPwdPair = RememberLoginPreference.getLoginInfo(getApplicationContext());
        edtEmail.setText(emailPwdPair.first);
        edtPassword.setText(emailPwdPair.second);

        // check email != null => restore info => set checkbox true
        rememberPwd.setChecked(!TextUtils.isEmpty(emailPwdPair.first));
    }

    @Override
    public void onSuccess(LoginResult loginResult) {
        facebookId = loginResult.getAccessToken().getUserId();
        saveIdLoginFace();

        LogUtils.e("loginFace", "faceid:" + facebookId);
        // get user info
        LoginByFacebookRequest loginRequest = new LoginByFacebookRequest(
                facebookId,
                getDeviceId(),
                FirebaseInstanceId.getInstance().getToken(),
                getLoginTime(),
                SystemUtils.getAppVersionName(getApplicationContext()),
                SystemUtils.getDeviceName(),
                SystemUtils.getAndroidOSVersion(),
                GPSADIDPrefernces.getInstance().getGPSADID()
        );

        getPresenter().onLoginFbSuccess(loginRequest);
    }

    @Override
    public void onCancel() {
        showLoginWithFbCanceled();
    }

    void showLoginWithFbCanceled() {
        Toast.makeText(this, R.string.login_facebook_cancel, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onError(FacebookException error) {
        showLoginFbError();
    }

    @Override
    public void showLoginFbError() {
        Toast.makeText(this, R.string.login_facebook_fail, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onLockedUser() {
        showConfirmDialog(R.string.login_locked_user, null, false);
    }

    @Override
    public void showLoadingDialog() {
        if (!mLoadingProgress.isShowing())
            mLoadingProgress.show();
    }

    @Override
    public void hideLoadingDialog() {
        mLoadingProgress.dismiss();
    }

    @Override
    public void gotoEditProfile() {
        EditProfileActivity.launchEditProfileWithFb(this, facebookId);
    }


    @Override
    public void onLoginEmailSuccess(LoginResponse response) {
        handleLoginSuccess(response);
    }

    @Override
    public void onLoginFBSuccess(LoginResponse response) {
        handleLoginSuccess(response);
        saveIdLoginFace();
    }

    @Override
    public void onLoginEmailNotFound() {
        showConfirmDialog(R.string.email_not_found, null, false);
    }

    @Override
    public void onLoginEmailInvalid() {
        showConfirmDialog(R.string.email_invalid, null, false);
    }

    @Override
    public void onLoginPasswordIncorrect() {
        showConfirmDialog(R.string.msg_common_password_is_incorrect, null, false);
    }

    @Override
    public void onLoginPasswordInvalid() {
        showConfirmDialog(R.string.password_invalid, null, false);
    }

    @Override
    public void gotoMain() {
        MainActivity.launch(LoginActivity.this);
    }

    // ========================== //

    //Request data
    private void handleLoginSuccess(LoginResponse loginBean) {
        // Save token
        UserPreferences userPreferences = UserPreferences.getInstance();

        // Save info when login success
        AuthenticationBean authenData = loginBean.authenData;
        if (isRememberLogin) {
            RememberLoginPreference.saveLoginInfo(getApplicationContext(), authenData.email, edtPassword.getText().toString());
        } else {
            RememberLoginPreference.clear(getApplicationContext());
        }

        userPreferences.saveSuccessLoginData(authenData, true);

        Preferences preferences = Preferences.getInstance();
        // Login time
        preferences.saveTimeRelogin(System.currentTimeMillis());
        // Get banned word
        GoogleReviewPreference googleReviewPreference = new GoogleReviewPreference();
        googleReviewPreference.saveTurnOffVersion(authenData
                .switchBrowserVersion);
        googleReviewPreference.saveEnableGetFreePoint(authenData
                .isEnableGetFreePoint);
        googleReviewPreference.saveIsTurnOffUserInfo(authenData.isTurnOffUserInfo);

        // if not getLstBlockComplete edit profile
        if (loginBean.authenData.finishRegister == Constants.FINISH_REGISTER_NO) {
            EditProfileActivity.launchEditProfileWithFb(this, facebookId);
        } else {
            gotoMain();
        }
    }

    private void saveIdLoginFace() {
        UserPreferences pre = UserPreferences.getInstance();
        if (Utils.isEmptyOrNull(facebookId)) {
            // email : true,facebook : false
            pre.setIsFacebookLogin(true);
        } else {
            pre.setIsFacebookLogin(false);
        }
        pre.saveFacebookId(facebookId);
    }

    @Override
    public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            onClickLogin();
            return true;
        }
        return false;
    }

    @Override
    public boolean hasRegisterSocket() {
        return false;
    }

    @Override
    public boolean hasShowNotificationView() {
        return false;
    }
}
