package com.vn.ntsc.ui.accountsetting;

import android.app.ProgressDialog;
import android.content.Intent;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.vn.ntsc.R;
import com.vn.ntsc.core.views.BaseActivity;
import com.vn.ntsc.repository.model.accountsetting.ChangeAccFacebookRequest;
import com.vn.ntsc.repository.model.accountsetting.ChangeEmailRequest;
import com.vn.ntsc.repository.model.accountsetting.ChangePasswordRequest;
import com.vn.ntsc.repository.preferece.UserPreferences;
import com.vn.ntsc.ui.main.MainActivity;
import com.vn.ntsc.utils.Utils;
import com.vn.ntsc.utils.keyboard.KeyboardUtils;
import com.vn.ntsc.widget.toolbar.ToolbarButtonRightClickListener;
import com.vn.ntsc.widget.toolbar.ToolbarTitleCenter;

import butterknife.BindView;

/**
 * Created by ThoNh on 10/6/2017.
 */

public class AccountSettingActivity extends BaseActivity<AccountSettingPresenter> implements AccountSettingContracts.View, ToolbarButtonRightClickListener {
    public static final String TAG = AccountSettingActivity.class.getSimpleName();

    @BindView(R.id.toolbar)
    ToolbarTitleCenter mToolbar;
    @BindView(R.id.activity_account_edt_email)
    EditText mTxtEmail;
    @BindView(R.id.activity_account_edt_curent_password)
    EditText mTxtCurentPassword;
    @BindView(R.id.activity_account_edt_new_email)
    EditText mTxtNewEmail;
    @BindView(R.id.activity_account_edt_new_password)
    EditText mTxtNewPassword;
    @BindView(R.id.activity_account_edt_confirm_password)
    EditText mTxtConfirmPassword;
    @BindView(R.id.activity_account_view1)
    View mViewLine;
    private ProgressDialog mLoadingProgress;
    private int updateEmail = 0;
    private boolean isAccLogin = false;


    private String newEmail, newPass;

    public static void newInstance(MainActivity mainActivity) {
        Intent intent = new Intent(mainActivity, AccountSettingActivity.class);
        mainActivity.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_account_setting;
    }

    @Override
    public void onCreateView(View rootView) {
        getModulesCommonComponent().inject(this);

    }

    @Override
    public void onViewReady() {
        setSupportActionBar(mToolbar);
        mToolbar.setActionbar(getSupportActionBar())
                .setDisplayHomeAsUpEnabled(true)
                .setButtonRightListener(this);

        updateEmail = UserPreferences.getInstance().geUpdateEmailFlag();
        isAccLogin = UserPreferences.getInstance().isFacebookLogin();
        checkForUpdateEmail(updateEmail, isAccLogin);
        // set current email
        String email = UserPreferences.getInstance().getAuthenticationBean().email;
        mTxtEmail.setText(email);

        // request forcus and show keybroad
        KeyboardUtils.showDelayKeyboard(mTxtCurentPassword, 0);
        mTxtNewPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
        mTxtConfirmPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());

        mLoadingProgress = new ProgressDialog(this);
        mLoadingProgress.setCanceledOnTouchOutside(false);
        mLoadingProgress.setCancelable(false);
    }

    @Override
    public void onToolbarButtonRightClick(View view) {
        String currentPassword = mTxtCurentPassword.getText().toString().trim();
        String newEmail = mTxtNewEmail.getText().toString().trim();
        String newPassword = mTxtNewPassword.getText().toString();
        String newPasswordConfirm = mTxtConfirmPassword.getText().toString().trim();

        if (currentPassword.equals("") && isAccLogin) {
            Toast.makeText(this, R.string.account_setting_enter_password, Toast.LENGTH_SHORT).show();
            return;
        }

        if (newEmail.isEmpty()) {
            changeEmailOrChangePassword();
            return;
        }

        if (newPassword.equals("")) {
            Toast.makeText(this, R.string.account_setting_pass, Toast.LENGTH_SHORT).show();
            return;
        }

        if (newPasswordConfirm.equals("")) {
            Toast.makeText(this, R.string.account_setting_confirm_pass, Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPassword.equals(newPasswordConfirm)) {
            Toast.makeText(this, R.string.account_setting_password_or_newpass_not_valid, Toast.LENGTH_SHORT).show();
            return;
        }

        if (updateEmail == 0 && !isAccLogin) {
            ChangeAccFacebookRequest facebookRequest = new ChangeAccFacebookRequest(UserPreferences.getInstance().getToken(), UserPreferences.getInstance().getFacebookId(), newEmail, Utils.encryptPassword(newPasswordConfirm), newPassword);
            getPresenter().changeAccFacebook(facebookRequest);
            return;
        }

        if (!UserPreferences.getInstance().getPassword().equals(Utils.encryptPassword(currentPassword))) {
            currentPasswordIncorrect();
            return;
        }

        if (currentPassword.isEmpty()) {
            currentPasswordEmpty();
            return;
        }

        if ((newEmail.isEmpty() && !newPassword.isEmpty()) || (newEmail.isEmpty() && !newPasswordConfirm.isEmpty())) { // change password
            requestChangePassword();

        } else if (!newEmail.isEmpty()) { // change email
            requestChangeEmail();
        }
    }

    @Override
    public void emailEmpty() {
        Toast.makeText(this, R.string.account_setting_enter_password, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void currentPasswordIncorrect() {
        Toast.makeText(this, R.string.account_setting_password_incorrect, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void currentPasswordEmpty() {
        Toast.makeText(this, R.string.account_setting_enter_password, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void newEmailInvalid() {
        Toast.makeText(this, R.string.account_setting_new_email_invalid, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void newPasswordNotMatch() {
        Toast.makeText(this, R.string.account_setting_new_password, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void newPasswordNotValid() {
        Toast.makeText(this, R.string.account_setting_password_or_newpass_not_valid, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void changeEmailSuccess() {
        Toast.makeText(this, R.string.account_setting_confirm_success, Toast.LENGTH_SHORT).show();
        UserPreferences.getInstance().savePassword(newPass);
        UserPreferences.getInstance().saveEmail(newEmail);
        finish();
    }

    @Override
    public void changePasswordSuccess() {
        Toast.makeText(this, R.string.account_setting_change_password_success, Toast.LENGTH_SHORT).show();
        UserPreferences.getInstance().savePassword(newPass);
        finish();
    }

    @Override
    public void changeFailure() {
        Toast.makeText(this, R.string.account_setting_change_email_failure, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void emailAlreadyExist() {
        Toast.makeText(this, R.string.account_setting_change_email_exist, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void oldPasswordIncorrect() {
        Toast.makeText(this, R.string.account_setting_old_pass_wrong, Toast.LENGTH_SHORT).show();
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
    public void changeEmailOrChangePassword() {
        Toast.makeText(this, R.string.account_setting_email, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void requestChangeEmail() {

        String currentPassword = mTxtCurentPassword.getText().toString().trim();
        String currentEmail = mTxtEmail.getText().toString().trim();
        String newEmail = mTxtNewEmail.getText().toString().trim();
        String newPassword = mTxtNewPassword.getText().toString();
        String newPasswordConfirm = mTxtConfirmPassword.getText().toString().trim();
        ChangeEmailRequest request;

        if (newEmail.equals(currentEmail)) {
            emailAlreadyExist();
            return;
        }

        if (newPassword.isEmpty() && newPasswordConfirm.isEmpty()) {

            String currentPasswordEncrypt = Utils.encryptPassword(currentPassword);

            request = new ChangeEmailRequest(newEmail, currentPassword, currentPasswordEncrypt,

                    currentPasswordEncrypt, UserPreferences.getInstance().getToken());
            getPresenter().changeEmail(request);
            this.newEmail = newEmail;
            this.newPass = UserPreferences.getInstance().getPassword();
            showLoadingDialog();
        } else {

            if (newPassword.isEmpty() || newPasswordConfirm.isEmpty() || newPassword.length() < 6 || newPasswordConfirm.length() < 6) {
                newPasswordNotValid();

            } else if (!newPassword.equals(newPasswordConfirm)) {
                newPasswordNotMatch();

            } else {
                // encrypt for request
                String newPasswordEncrypt = Utils.encryptPassword(newPassword);
                String currentPasswordEncrypt = Utils.encryptPassword(currentPassword);

                request = new ChangeEmailRequest(
                        newEmail, currentPassword, newPasswordEncrypt,
                        currentPasswordEncrypt, UserPreferences.getInstance().getToken());
                getPresenter().changeEmail(request);
                this.newEmail = newEmail;
                this.newPass = Utils.encryptPassword(newPassword);
                showLoadingDialog();
            }
        }
    }

    @Override
    public void requestChangePassword() {
        String currentPassword = mTxtCurentPassword.getText().toString().trim();
        String newPassword = mTxtNewPassword.getText().toString();
        String newPasswordConfirm = mTxtConfirmPassword.getText().toString();

        if (newPassword.isEmpty() || newPasswordConfirm.isEmpty() ||
                newPassword.length() < 6 || newPasswordConfirm.length() < 6) {
            newPasswordNotValid();

        } else if (!newPassword.equals(newPasswordConfirm)) {
            newPasswordNotMatch();

        } else {
            String newPasswordEncrypt = Utils.encryptPassword(newPassword);
            String currentPasswordEncrypt = Utils.encryptPassword(currentPassword);
            ChangePasswordRequest request = new ChangePasswordRequest(newPasswordEncrypt,
                    currentPasswordEncrypt, newPassword, UserPreferences.getInstance().getToken());

            getPresenter().changePassword(request);
            this.newPass = Utils.encryptPassword(newPassword);
            showLoadingDialog();
        }
    }

    @Override
    public void changeAccFacebookSuccess() {
        UserPreferences.getInstance().setIsFacebookLogin(true);
        UserPreferences.getInstance().savePassword(Utils.encryptPassword(mTxtConfirmPassword.getText().toString().trim()));
        Toast.makeText(this, getResources().getString(R.string.account_setting_confirm_success), Toast.LENGTH_SHORT).show();
        MainActivity.launch(AccountSettingActivity.this);
    }

    @Override
    public void complete() {
        hideLoadingDialog();
    }

    private void checkForUpdateEmail(int updateEmail, boolean isAccLogin) {
        if (updateEmail == 0 && !isAccLogin) {
            mTxtCurentPassword.setVisibility(View.GONE);
            mViewLine.setVisibility(View.GONE);
        } else {
            mTxtCurentPassword.setVisibility(View.VISIBLE);
            mViewLine.setVisibility(View.VISIBLE);
            mTxtCurentPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }
    }
}
