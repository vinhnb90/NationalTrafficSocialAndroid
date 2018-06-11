package com.vn.ntsc.ui.changepassword;

import android.app.ProgressDialog;
import android.content.Intent;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.iid.FirebaseInstanceId;
import com.vn.ntsc.R;
import com.vn.ntsc.core.views.BaseActivity;
import com.vn.ntsc.repository.model.changepassword.ChangePasswordRequest;
import com.vn.ntsc.repository.model.login.AuthenticationBean;
import com.vn.ntsc.repository.preferece.UserPreferences;
import com.vn.ntsc.ui.main.MainActivity;
import com.vn.ntsc.utils.SystemUtils;
import com.vn.ntsc.utils.Utils;
import com.vn.ntsc.widget.toolbar.ToolbarTitleCenter;

import butterknife.BindView;
import butterknife.OnClick;

import static com.vn.ntsc.utils.SystemUtils.getDeviceId;
import static com.vn.ntsc.utils.time.TimeUtils.getLoginTime;

public class ChangePasswordActivity extends BaseActivity<ChangePasswordPresenter> implements ChangePasswordContract.View {
    /**
     * key for receive email from bundle
     */
    public static final String KEY_EMAIL = "email";

    @BindView(R.id.toolbar)
    ToolbarTitleCenter mToolbar;
    @BindView(R.id.activity_change_passsword_password)
    EditText edPassword;
    @BindView(R.id.activity_change_passsword_confirm_password)
    EditText edConfirmPassword;
    @BindView(R.id.activity_change_passsword_auth_code)
    EditText edAuthCode;

    private String email;
    private ProgressDialog mLoadingProgress;

    @Override
    public int getLayoutId() {
        return R.layout.activity_change_password;
    }

    @Override
    public void onCreateView(View rootView) {
        mLoadingProgress = new ProgressDialog(this);
        mLoadingProgress.setCanceledOnTouchOutside(false);
        mLoadingProgress.setCancelable(false);
        getModulesCommonComponent().inject(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onViewReady() {
        setSupportActionBar(mToolbar);
        mToolbar.setActionbar(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        checkRequireInput();
    }

    /**
     * require email
     */
    private void checkRequireInput() {
        email = getIntent().getStringExtra(KEY_EMAIL);
        if (TextUtils.isEmpty(email)) finish();
    }

    @OnClick(R.id.activity_change_passsword_submit)
    void onSubmitClick() {
        String authCode = edAuthCode.getText().toString();
        String password = edPassword.getText().toString();
        String confPassword = edConfirmPassword.getText().toString();

        //  check valid
        if (TextUtils.isEmpty(authCode)) {
            showConfirmDialog(R.string.auth_code_empty, null, false);
            return;
        }
        if (TextUtils.isEmpty(password) || password.length() < 6) {
            showConfirmDialog(R.string.password_invalid, null, false);
            return;
        }
        if (!password.equals(confPassword)) {
            showConfirmDialog(R.string.confirm_password_invalid, null, false);
            return;
        }

        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest(
                getLoginTime(),
                email,
                authCode,
                SystemUtils.getAppVersionName(this),
                password,
                Utils.encryptPassword(password),
                getDeviceId(),
                FirebaseInstanceId.getInstance().getToken()
        );

        getPresenter().onChangePassword(changePasswordRequest);
    }

    @Override
    public void gotoMain() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    @Override
    public void onEmailNotFound() {
        showConfirmDialog(R.string.email_not_found, null, false);
    }

    @Override
    public void onCodeIncorrect() {
        showConfirmDialog(R.string.msg_common_incorrect_code, null, false);
    }

    @Override
    public void onLockedUser() {
        showConfirmDialog(R.string.login_locked_user, null, false);
    }

    @Override
    public void saveAuthData(AuthenticationBean authenData) {
        UserPreferences.getInstance().saveSuccessLoginData(authenData, true);
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
    public boolean hasRegisterSocket() {
        return false;
    }

    @Override
    public boolean hasShowNotificationView() {
        return false;
    }
}
