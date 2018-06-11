package com.vn.ntsc.ui.forgotpassword;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.util.PatternsCompat;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.vn.ntsc.R;
import com.vn.ntsc.core.views.BaseActivity;
import com.vn.ntsc.repository.model.forgotpassword.ForgotPasswordRequest;
import com.vn.ntsc.ui.changepassword.ChangePasswordActivity;
import com.vn.ntsc.widget.toolbar.ToolbarTitleCenter;

import butterknife.BindView;
import butterknife.OnClick;

public class ForgotPasswordActivity extends BaseActivity<ForgotPasswordPresenter> implements ForgotPasswordContract.View {

    @BindView(R.id.toolbar)
    ToolbarTitleCenter mToolbar;
    @BindView(R.id.activity_forgot_password_email)
    EditText email;
    private ProgressDialog mLoadingProgress;

    @Override
    public int getLayoutId() {
        return R.layout.activity_forgot_password;
    }

    @Override
    public void onCreateView(View rootView) {
        mLoadingProgress = new ProgressDialog(this);
        mLoadingProgress.setCanceledOnTouchOutside(false);
        mLoadingProgress.setCancelable(false);
        getModulesCommonComponent().inject(this);
    }

    @Override
    public void onViewReady() {
        setSupportActionBar(mToolbar);
        mToolbar.setActionbar(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.activity_forgot_password_submit)
    void onSubmitRecoveryPassword() {
        String em = email.getText().toString();

        // check valid email format
        if (!PatternsCompat.EMAIL_ADDRESS.matcher(em).matches()) {
            showDialogInvalidEmail();
            return;
        }

        ForgotPasswordRequest forgotPasswordRequest = new ForgotPasswordRequest(em);
        getPresenter().onForgotPassword(forgotPasswordRequest);
    }

    @Override
    public void showSuccessAlert() {
        showConfirmDialog(
                R.string.msg_recovery_password,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        gotoChangePassword(email.getText().toString());
                    }
                },
                false
        );
    }

    /**
     * when send request forgot password success
     *
     * @param email send change password setting
     */
    private void gotoChangePassword(String email) {
        Intent intent = new Intent(this, ChangePasswordActivity.class);
        intent.putExtra(ChangePasswordActivity.KEY_EMAIL, email);
        startActivity(intent);
        finish();
    }

    @Override
    public void showEmailNotFound() {
        showConfirmDialog(R.string.email_not_found, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                email.setText("");
                email.requestFocus();
            }
        }, true);
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

    private void showDialogInvalidEmail() {
        showConfirmDialog(R.string.email_invalid, null, false);
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
