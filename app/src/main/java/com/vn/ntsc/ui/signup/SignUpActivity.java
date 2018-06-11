package com.vn.ntsc.ui.signup;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.google.firebase.iid.FirebaseInstanceId;
import com.vn.ntsc.R;
import com.vn.ntsc.core.views.LoginFacebookActivity;
import com.vn.ntsc.repository.model.login.AuthenticationBean;
import com.vn.ntsc.repository.model.login.LoginByFacebookRequest;
import com.vn.ntsc.repository.model.signup.SignUpRequest;
import com.vn.ntsc.repository.model.signup.SignUpResponse;
import com.vn.ntsc.repository.model.user.User;
import com.vn.ntsc.repository.preferece.GPSADIDPrefernces;
import com.vn.ntsc.repository.preferece.Preferences;
import com.vn.ntsc.repository.preferece.UserPreferences;
import com.vn.ntsc.ui.main.MainActivity;
import com.vn.ntsc.ui.profile.edit.EditProfileActivity;
import com.vn.ntsc.utils.Constants;
import com.vn.ntsc.utils.SystemUtils;
import com.vn.ntsc.utils.UserSetting;
import com.vn.ntsc.utils.Utils;
import com.vn.ntsc.utils.keyboard.KeyboardUtils;
import com.vn.ntsc.utils.time.TimeUtils;
import com.vn.ntsc.widget.toolbar.ToolbarTitleCenter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;

import static com.vn.ntsc.utils.SystemUtils.getAndroidOSVersion;
import static com.vn.ntsc.utils.SystemUtils.getDeviceId;
import static com.vn.ntsc.utils.SystemUtils.getDeviceName;

/**
 * Created by nankai on 8/3/2017.
 */
public class SignUpActivity extends LoginFacebookActivity<SignUpPresenter> implements DatePickerDialog.OnDateSetListener, SignUpContract.View, TextView.OnEditorActionListener {

    private static final String ELEMENT_LOGO = "activity_signup_logo";
    public static final String EXTRA_USER_DATA = "extra_user_data";

    @BindView(R.id.toolbar)
    ToolbarTitleCenter mToolbar;

    @BindView(R.id.activity_signup_container)
    LinearLayout container;
    @BindView(R.id.activity_signup_gender)
    RadioGroup gender;
    @BindView(R.id.activity_signup_date_of_birthday)
    TextView txtDateOfBirthday;
    @BindView(R.id.activity_signup_password)
    EditText etxPassword;
    @BindView(R.id.activity_signup_confirm_password)
    EditText etxConfirmPassword;
    @BindView(R.id.activity_signup_age_confirm)
    CheckBox ageConfirm;
    @BindView(R.id.activity_signup_term_confirm)
    CheckBox termConfirm;
    @BindView(R.id.activity_signup_sign_up)
    Button btnSignUp;

    private User mUser;
    private ProgressDialog mLoadingProgress;

    /**
     * will set if login with facebook success.
     *
     * @see #onSuccess(LoginResult)
     */
    private String facebookId = null;

    public static void launch(AppCompatActivity activity, View logo) {
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, logo, ELEMENT_LOGO);
        Intent intent = new Intent(activity, SignUpActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ActivityCompat.startActivity(activity, intent, options.toBundle());
    }

    public static void launch(AppCompatActivity activity) {
        Intent intent = new Intent(activity, SignUpActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_signup;
    }

    @Override
    public void onCreateView(View rootView) {
        mLoadingProgress = new ProgressDialog(this);
        mLoadingProgress.setCanceledOnTouchOutside(false);
        mLoadingProgress.setCancelable(false);
        getModulesCommonComponent().inject(this);

        ViewCompat.setTransitionName(mToolbar, ELEMENT_LOGO);
    }

    @Override
    public void onViewReady() {
        setSupportActionBar(mToolbar);
        mToolbar.setActionbar(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        etxConfirmPassword.setOnEditorActionListener(this);

        loadCachedBackgroundContainer(R.drawable.bg_signup);

        initUserVariable();

        // fix bug: font hint not same as birthday
        etxPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
        etxConfirmPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void initUserVariable() {
        Intent intent = getIntent();
        if (intent != null) {
            mUser = intent.getParcelableExtra(EXTRA_USER_DATA);
        }
        if (mUser == null) {
            mUser = new User();
        }
    }

    @OnClick({R.id.activity_signup_male, R.id.activity_signup_female})
    void onChangeGender(CompoundButton button) {
        switch (button.getId()) {
            case R.id.activity_signup_male:
                mUser.gender = UserSetting.GENDER_MALE;
                break;
            case R.id.activity_signup_female:
                mUser.gender = UserSetting.GENDER_FEMALE;
                break;
            default:
                break;
        }
    }

    @OnClick(R.id.activity_signup_date_of_birthday)
    void onPickDateOfBirthday() {
        Calendar cal = Calendar.getInstance();


        // limit age = 14
        String[] dmy = txtDateOfBirthday.getText().toString().split("/");
        if (!txtDateOfBirthday.getText().toString().isEmpty() && dmy.length == 3) {
            cal.set(Calendar.YEAR, Integer.parseInt(dmy[2]));
            cal.set(Calendar.MONTH, Integer.parseInt(dmy[1]) - 1); // month start 0->11
            cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dmy[0]));

        }

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                this,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH));

        Calendar calendar = Calendar.getInstance();
        // limit age = 14
        calendar.add(Calendar.YEAR, -14);
        datePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());
        datePickerDialog.show();
    }

    @OnClick(R.id.activity_signup_login_with_fb)
    void onClickLoginWithFacebook() {
        loginWithFacebook();
    }

    @OnClick(R.id.activity_signup_sign_up)
    void onClickSignUpButton() {
        // check valid input before request to server
        String dateOfBirthday = txtDateOfBirthday.getText().toString();
        if (TextUtils.isEmpty(dateOfBirthday)) {
            showConfirmDialog(R.string.invalid_birthday, null, false);
            return;
        }

        int checkedId = gender.getCheckedRadioButtonId();
        if (checkedId == -1) {
            showConfirmDialog(R.string.gender_invalid, null, false);
            return;
        }

        String pwd = etxPassword.getText().toString();
        if (pwd.length() < 6) {
            showConfirmDialog(R.string.password_invalid, null, false);
            return;
        }

        if (!pwd.equals(etxConfirmPassword.getText().toString())) {
            showConfirmDialog(R.string.confirm_password_invalid, null, false);
            return;
        }
        if (!ageConfirm.isChecked() || !termConfirm.isChecked()) {
            showConfirmDialog(R.string.check_confirm_age_term, null, false);
            return;
        }
        // ========================= //
        KeyboardUtils.hideSoftKeyboard(this);
        mUser.password = etxPassword.getText().toString();
        SimpleDateFormat format = new SimpleDateFormat(Constants.DATE_FORMAT_SEND_TO_SERVER, Locale.getDefault());
        String birthday = format.format(mUser.birthday);
        String deviceId = getDeviceId();
        int gender = mUser.gender;
        String loginTime = Utils.getLoginTime();
        String notify_token = FirebaseInstanceId.getInstance().getToken();
        String appVersion = Utils.getAppVersionName(this);
        String gps_adid = Preferences.getInstance().getGPSADID();
        String device_name = getDeviceName();
        String os_version = getAndroidOSVersion();
        String adid = Preferences.getInstance().getAdid();
        SignUpRequest signUpRequest = new SignUpRequest(
                facebookId,
                birthday,
                gender,
                deviceId,
                loginTime,
                notify_token,
                appVersion,
                gps_adid,
                device_name,
                os_version,
                adid,
                Utils.encryptPassword(mUser.password),
                mUser.password
        );
        getPresenter().signUp(signUpRequest);
    }

    @Override
    public void gotoEditProfile() {
        EditProfileActivity.launch(this, true, mUser.password, btnSignUp);
    }

    /**
     * go to main then finish
     */
    @Override
    public void gotoMain() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    @Override
    public void gotoEditProfile(String fbId) {
        EditProfileActivity.launchEditProfileWithFb(this, fbId);
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


    // ============== handle server response ============ //
    @Override
    public void savePreference(SignUpResponse response) {
        UserPreferences userPref = UserPreferences.getInstance();
        userPref.saveSuccessLoginData(response.authenData, true);
    }
    // ============== end handle server response ============ //

    @Override
    public void onSignUpEmailRegistered() {
        showConfirmDialog(R.string.msg_common_email_has_already, null, false);
    }

    @Override
    public void onSignUpEmailInvalid() {
        showConfirmDialog(R.string.email_invalid, null, false);
    }

    @Override
    public void onSignUpPasswordInvalid() {
        showConfirmDialog(R.string.msg_common_invalid_password, null, false);
    }

    @Override
    public void onSuccess(LoginResult loginResult) {
        facebookId = loginResult.getAccessToken().getUserId();
        getPresenter().loginByFacebook(
                new LoginByFacebookRequest(
                        facebookId,
                        getDeviceId(),
                        FirebaseInstanceId.getInstance().getToken(),
                        TimeUtils.getLoginTime(),
                        SystemUtils.getAppVersionName(getApplicationContext()),
                        getDeviceName(),
                        getAndroidOSVersion(),
                        GPSADIDPrefernces.getInstance().getGPSADID()
                )
        );
    }

    @Override
    public void onCancel() {
        showConfirmDialog(R.string.signup_cancel_facebook, null, false);
    }

    @Override
    public void onError(FacebookException error) {
        showConfirmDialog(R.string.signup_error_facebook, null, false);
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        mUser.birthday = cal.getTime();

        txtDateOfBirthday.setText(String.format(Locale.US, "%02d/%02d/%04d", dayOfMonth, ++month, year));
    }

    @Override
    public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            onClickSignUpButton();
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
