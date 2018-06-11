package com.vn.ntsc.ui.profile.edit;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.PatternsCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.tux.mylab.MediaPickerBaseActivity;
import com.example.tux.mylab.camera.Camera;
import com.example.tux.mylab.gallery.Gallery;
import com.example.tux.mylab.gallery.data.MediaFile;
import com.google.firebase.iid.FirebaseInstanceId;
import com.vn.ntsc.R;
import com.vn.ntsc.app.AppController;
import com.vn.ntsc.core.views.BaseActivity;
import com.vn.ntsc.repository.ActivityResultRequestCode;
import com.vn.ntsc.repository.json.boby.BodyType;
import com.vn.ntsc.repository.json.boby.BodyTypeItem;
import com.vn.ntsc.repository.json.regions.RegionItem;
import com.vn.ntsc.repository.model.editprofile.EditProfileRequest;
import com.vn.ntsc.repository.model.login.AuthenticationBean;
import com.vn.ntsc.repository.model.notification.NotificationItem;
import com.vn.ntsc.repository.model.signup.SignUpByFacebookRequest;
import com.vn.ntsc.repository.model.user.UserInfoRequest;
import com.vn.ntsc.repository.model.user.UserInfoResponse;
import com.vn.ntsc.repository.preferece.Preferences;
import com.vn.ntsc.repository.preferece.UserPreferences;
import com.vn.ntsc.ui.main.MainActivity;
import com.vn.ntsc.ui.profile.edit.hobby.HobbyActivity;
import com.vn.ntsc.ui.profile.edit.job.JobActivity;
import com.vn.ntsc.ui.search.area.ChooseEachAreaActivity;
import com.vn.ntsc.utils.AssetsUtils;
import com.vn.ntsc.utils.Constants;
import com.vn.ntsc.utils.DirtyWordUtils;
import com.vn.ntsc.utils.FileUtils;
import com.vn.ntsc.utils.ImagesUtils;
import com.vn.ntsc.utils.SystemUtils;
import com.vn.ntsc.utils.Utils;
import com.vn.ntsc.utils.keyboard.KeyboardUtils;
import com.vn.ntsc.widget.toolbar.ToolbarButtonLeftClickListener;
import com.vn.ntsc.widget.toolbar.ToolbarButtonRightClickListener;
import com.vn.ntsc.widget.toolbar.ToolbarTitleCenter;
import com.vn.ntsc.widget.views.ToggleEditModeRelativeLayout;
import com.vn.ntsc.widget.views.textview.TextViewVectorCompat;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindArray;
import butterknife.BindView;
import butterknife.OnClick;

import static com.vn.ntsc.utils.SystemUtils.getDeviceId;
import static com.vn.ntsc.utils.time.TimeUtils.getLoginTime;

/**
 * Created by dev22 on 8/16/17.
 * when user sign up or click edit profile
 */
public class EditProfileActivity extends BaseActivity<EditProfilePresenter> implements
        EditProfileContract.View,
        CompoundButton.OnCheckedChangeListener,
        ToolbarButtonLeftClickListener, ToolbarButtonRightClickListener {

    private static final String TAG = EditProfileActivity.class.getSimpleName();

    private static final int CHOOSE_AREA = 1;
    private static final int CHOOSE_JOB = 2;
    private static final int CHOOSE_HOBBY = 3;

    /**
     * key for set bundle to profile edit|view only mode
     */
    public static final String ENABLE_EDIT = "enable_edit";

    /**
     * key for set bundle to profile from notification
     */
    public static final String FROM_NOTIFICAION = "from_notification";

    /**
     * if true: hide email field
     */
    public static final String WITH_FB = "with_fb";
    /**
     * key for send facebook id via bundle
     */
    private static final String FB_ID = "fb_id";
    /**
     * key for assign flag {@link #isClearWHenBack}
     */
    private static final String CLEAR_WHEN_BACK = "clear_whn_back";
    /**
     * key for set animation transform when change activity
     */
    private static final String ELEMENT_TRANSFORM = "elm";

    /**
     * for receive user info when edit profile success
     *
     * @see android.app.Activity#onActivityResult(int, int, Intent)
     * @see com.vn.ntsc.repository.ActivityResultRequestCode#REQUEST_CODE_EDIT_PROFILE
     */
    private static final String KEY_USER_INFO = "key_user";
    /**
     * login|signup with fb => server will generate email like this: xxxxxxxxx@facebook.com
     */
    private static final String FB_EMAIL_GENERATE_BY_SERVER = "@facebook.com";
    /**
     * sign up with email, need send password to edit profile to save
     */
    private static final String PASSWORD_TO_SAVE = "password_to_save";
    /**
     * when registered user cannot edit gender and birthday
     */
    private static final String REGISTERED = "registered";

    @BindView(R.id.toolbar)
    ToolbarTitleCenter mToolbar;
    @BindView(R.id.activity_edit_profile_email_label)
    LinearLayout emailContainer;
    @BindView(R.id.activity_edit_profile_ed_email)
    EditText edEmail;
    @BindView(R.id.activity_edit_profile_ed_name)
    EditText edName;
    @BindView(R.id.activity_edit_profile_identify_no)
    EditText edIdentifyNo;
    @BindView(R.id.activity_edit_profile_zone)
    TextViewVectorCompat tvZone;
    @BindView(R.id.activity_edit_profile_body_type)
    TextViewVectorCompat tvBodyType;
    @BindView(R.id.activity_edit_profile_hobby)
    TextViewVectorCompat tvHobby;
    @BindView(R.id.activity_edit_profile_date_of_birthday)
    TextViewVectorCompat tvBirdDay;
    @BindView(R.id.activity_edit_profile_job)
    TextViewVectorCompat tvJob;
    @BindView(R.id.activity_edit_profile_gender)
    RadioGroup gender;
    @BindView(R.id.activity_edit_profile_phone_no)
    EditText edPhone;
    @BindView(R.id.activity_edit_profile_avatar)
    ImageView avatar;
    @BindView(R.id.activity_edit_profile_message)
    EditText edMessage;
    @BindView(R.id.activity_edit_profile_container)
    ToggleEditModeRelativeLayout container;
    @BindView(R.id.activity_edit_profile_cover_photo)
    ImageView coverPhoto;

    @BindArray(R.array.hobby)
    String[] hobbies;
    @BindArray(R.array.job_male)
    String[] jobs_male;
    @BindArray(R.array.job_female)
    String[] jobs_female;


    /**
     * save selected area
     */
    private List<RegionItem> selectedArea = null;
    private int selectedJob = -1;
    private int selectedRegion = -1;
    private int[] selectedHobby;
    private MediaFile selectedAvatar;
    /**
     * if true => request reg_ver_2 instead of upd_user_inf
     *
     * @see com.vn.ntsc.repository.model.signup.SignUpByFacebookRequest
     */
    private boolean isSignUpWithFb;
    private String facebookId;
    private ProgressDialog loadingProgress;
    /**
     * clear preference when back
     *
     * @see #launch(AppCompatActivity, boolean, String, View)
     */
    private boolean isClearWHenBack = false;
    /**
     * password come from signup with email
     *
     * @see #PASSWORD_TO_SAVE
     */
    private String password = null;

    /**
     * store index of body type
     *
     * @see #chooseBodyType()
     */
    private int indexBody = -1;
    /**
     * store list data of body type from json
     *
     * @see #bodyAsset
     */
    private List<BodyTypeItem> bodyTypes;
    /**
     * object to parse json from asset
     */
    private BodyType bodyAsset;

    /**
     * start edit profile activity from sign up, need password to save when edit profile success
     *
     * @param activity        need for start intent
     * @param isClearWHenBack true: clear when click back
     * @param password        to save when edit profile success
     * @param view            for animation
     */
    public static void launch(AppCompatActivity activity, boolean isClearWHenBack, String password, View view) {
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, view, ELEMENT_TRANSFORM);
        Intent intent = new Intent(activity, EditProfileActivity.class);
        intent.putExtra(CLEAR_WHEN_BACK, isClearWHenBack);
        intent.putExtra(PASSWORD_TO_SAVE, password);
        activity.startActivity(intent, options.toBundle());
    }

    /**
     * start edit profile activity from timeline
     *
     * @param activity    need for start intent
     * @param view        for animation
     * @param requestCode for receive result
     */
    public static void launch(AppCompatActivity activity, View view, @ActivityResultRequestCode int requestCode) {
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, view, ELEMENT_TRANSFORM);
        Intent intent = new Intent(activity, EditProfileActivity.class);
        intent.putExtra(REGISTERED, true);
        activity.startActivityForResult(intent, requestCode, options.toBundle());
    }

    /**
     * start edit profile with fb, will hide email field
     *
     * @param activity   need for start intent
     * @param facebookId send to server
     */
    public static void launchEditProfileWithFb(AppCompatActivity activity, String facebookId) {
        Intent intent = new Intent(activity, EditProfileActivity.class);
        intent.putExtra(ENABLE_EDIT, true);
        intent.putExtra(WITH_FB, true);
        intent.putExtra(FB_ID, facebookId);
        intent.putExtra(CLEAR_WHEN_BACK, true);
        activity.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_edit_profile;
    }

    @Override
    public void onCreateView(View rootView) {
        loadingProgress = new ProgressDialog(this);
        loadingProgress.setMessage(getResources().getString(R.string.loading));
        loadingProgress.setCanceledOnTouchOutside(false);
        loadingProgress.setCancelable(false);

        getMediaComponent().inject(this);

        ViewCompat.setTransitionName(avatar, ELEMENT_TRANSFORM);
    }

    @Override
    public void onViewReady() {
        setSupportActionBar(mToolbar);

        mToolbar.setActionbar(getSupportActionBar())
                .setDisplayHomeAsUpEnabled(true)
                .setButtonLeftListener(this)
                .setButtonRightListener(this);

        NotificationItem mNotificationItem = getIntent().getParcelableExtra(FROM_NOTIFICAION);
        if (mNotificationItem != null) {
            //get yourself info
            UserInfoRequest request = new UserInfoRequest(UserPreferences.getInstance().getToken(), UserPreferences.getInstance().getUserId());
            getPresenter().requestUserInfo(request);
        }

        Intent intent = getIntent();

        facebookId = intent.getStringExtra(FB_ID);
        shouldHideInputEmail(intent.getBooleanExtra(WITH_FB, false));
        setEditMode(intent.getBooleanExtra(ENABLE_EDIT, true));
        setClearWHenBack(intent.getBooleanExtra(CLEAR_WHEN_BACK, false));
        storePasswordToSaveLater(intent.getStringExtra(PASSWORD_TO_SAVE));

        // always load gender and birthday, no meter from activity
        loadBirthday();
        loadGender();

        bodyAsset = AssetsUtils.getDataAssets(context, Constants.PATH_BODY_TYPE, BodyType.class);

        // start from activity except sign up, login
        if (!isClearWHenBack)
            loadUserInfoFromCache();
    }

    /**
     * get password from bundle to save later
     *
     * @param password string password in bundle
     * @see #gotoMain(AuthenticationBean)
     */
    private void storePasswordToSaveLater(String password) {
        this.password = password;
    }

    /**
     * load gender from share pref
     */
    private void loadGender() {
        int genderVal = UserPreferences.getInstance().getGender();
        if (genderVal != -1)
            gender.check(genderVal == 0 ? R.id.activity_edit_profile_male : R.id.activity_edit_profile_female);
    }

    /**
     * load birthday from share pref
     */
    private void loadBirthday() {
        String ymd = UserPreferences.getInstance().getBirthday();
        tvBirdDay.setText(String.format(Locale.US, "%s/%s/%s", ymd.substring(6), ymd.substring(4, 6), ymd.substring(0, 4)));
    }

    /**
     * check radio group of gender
     *
     * @return true: male, false: female
     */
    private boolean isMale() {
        return gender.getCheckedRadioButtonId() == R.id.activity_edit_profile_male;
    }

    /**
     * true: clear when click back
     *
     * @param clearWHenBack to assign
     */
    public void setClearWHenBack(boolean clearWHenBack) {
        isClearWHenBack = clearWHenBack;
    }

    /**
     * load user data from cache no matter exist or not
     */
    private void loadUserInfoFromCache() {
        UserPreferences user = UserPreferences.getInstance();
        AuthenticationBean bean = user.getAuthenticationBean();

        if (!TextUtils.isEmpty(user.getAva())) {
            loadAvatarThumbnail(user.getAva(), user.getGender());
        }

        // case: edit profile when login with fb, it is not case edit profile to complete sign up process
        if (bean.email.endsWith(FB_EMAIL_GENERATE_BY_SERVER))
            emailContainer.setVisibility(View.GONE);

        edEmail.setText(bean.email);
        edName.setText(bean.userName);

        // job from array
        selectedJob = bean.job;
        if (selectedJob != -1) {
            String[] arrayJob = isMale() ? jobs_male : jobs_female;
            if (selectedJob < arrayJob.length - 1) tvJob.setText(arrayJob[selectedJob]);
        }
        // region
        if (bean.region != -1) {
            selectedArea = ChooseEachAreaActivity.getRegionsJsonAndConvertMatchAdapter();
            selectedArea.get(bean.region).setChecked(true);
            selectedRegion = selectedArea.get(bean.region).value;
            tvZone.setText(selectedArea.get(bean.region).name);
        }
        // bodyAsset type
        if (bean.bodyType > -1) {
            // load array from json asset depend on gender of user
            bodyTypes = isMale() ? bodyAsset.bodyTypeMale : bodyAsset.bodyTypeFeMale;
            //  find index of value (server response)
            indexBody = findBodyIndex(bodyTypes, bean.bodyType);
            // get array to display
            final CharSequence[] arrayToDisplay = getBodyTypeDisplay(bodyTypes);
            tvBodyType.setText(arrayToDisplay[indexBody]);
        }

        tvHobby.setText(bean.hobby);
        getSelectedHobby(bean);
        edMessage.setText(bean.message);
        edIdentifyNo.setText(bean.identifyNumber);
        edPhone.setText(bean.phoneNumber);
    }

    /**
     * @param bodyTypes list data from json asset
     * @param bodyValue to find index
     * @return index of item in list <b>bodyTypes</b> have value = <b>bodyValue</b>
     */
    private int findBodyIndex(List<BodyTypeItem> bodyTypes, int bodyValue) {
        for (int i = 0; i < bodyTypes.size(); i++) {
            int value = bodyTypes.get(i).value;
            if (value == bodyValue) return i;
        }
        return -1;
    }

    /**
     * @param bodyTypes list origin data
     * @return list name value of item except item0
     */
    private CharSequence[] getBodyTypeDisplay(List<BodyTypeItem> bodyTypes) {
        // data is fixed json,so i don't want to check null because it always work
        CharSequence[] data = new CharSequence[bodyTypes.size() - 1];
        for (int i = 1; i < bodyTypes.size(); i++) {
            // cause start from 1
            data[i - 1] = bodyTypes.get(i).name;
        }
        return data;
    }

    /**
     * clear selected body type
     */
    private void clearBodyType() {
        indexBody = -1;
        tvBodyType.setText("");
    }

    /**
     * clear selected job
     */
    private void clearJob() {
        selectedJob = -1;
        tvJob.setText("");
    }

    private void getSelectedHobby(AuthenticationBean bean) {
        String hobby = bean.hobby;
        if (!hobby.isEmpty()) {
            String[] hobbyArray = hobby.split(",");
            selectedHobby = new int[hobbyArray.length];


            for (int i = 0; i < hobbyArray.length; i++) {
                for (int j = 0; j < hobbies.length; j++) {
                    if (hobbyArray[i].trim().equals(hobbies[j].trim())) {
                        selectedHobby[i] = j;
                    }
                }
            }
        }
    }

    /**
     * hide email if edit profile on user login by fb
     *
     * @param isSignUpWithFb true: sign up with fb
     */
    private void shouldHideInputEmail(boolean isSignUpWithFb) {
        this.isSignUpWithFb = isSignUpWithFb;
        if (isSignUpWithFb) {
            emailContainer.setVisibility(View.GONE);
        }
    }

    /**
     * toggle edit | view only mode
     *
     * @param isEnableEditMode true: edit mode, false: view only mode
     */
    private void setEditMode(boolean isEnableEditMode) {
        container.setEnableEditMode(isEnableEditMode);
    }

    /**
     * @param indexBody index of display list
     * @return body type value from index of display list {@link #getBodyTypeDisplay(List)}
     * @see #getBodyTypeDisplay(List)
     */
    private int getBodyTypeValue(int indexBody) {
        if (indexBody < 0) return -1;
        bodyTypes = isMale() ? bodyAsset.bodyTypeMale : bodyAsset.bodyTypeFeMale;
        // need index + 1 because indexBody is index of display array, see document
        return bodyTypes.get(indexBody + 1).value;
    }

    @OnClick(R.id.activity_edit_profile_avatar)
    void pickAvatar() {
        // start to gallery to select image
        new Gallery.Builder()
                .sortType(Gallery.SORT_BY_PHOTOS)
                .isMultichoice(false)
                .build()
                .start(this);
    }

    @OnClick(R.id.activity_edit_profile_zone)
    void chooseZone() {
        KeyboardUtils.hideSoftKeyboard(this);
        Intent intent = new Intent(this, ChooseEachAreaActivity.class);
        intent.putExtra(ChooseEachAreaActivity.SINGLE_CHOICE, true);
        intent.putExtra(ChooseEachAreaActivity.CHOOSE_AREAS_TYPE, EditProfileActivity.class.getSimpleName());
        intent.putParcelableArrayListExtra(ChooseEachAreaActivity.EXTRA_LIST_AREA, (ArrayList<? extends Parcelable>) selectedArea);
        startActivityForResult(intent, CHOOSE_AREA);
    }

    @OnClick(R.id.activity_edit_profile_job)
    void chooseJob() {
        KeyboardUtils.hideSoftKeyboard(this);
        JobActivity.start(this, selectedJob, isMale(), CHOOSE_JOB);
    }

    @OnClick(R.id.activity_edit_profile_hobby)
    void chooseHobby() {
        KeyboardUtils.hideSoftKeyboard(this);
        Intent intent = new Intent(this, HobbyActivity.class);
        intent.putExtra(HobbyActivity.SELECTED_RESULT, selectedHobby);
        startActivityForResult(intent, CHOOSE_HOBBY);
    }

    @OnClick(R.id.activity_edit_profile_body_type)
    void chooseBodyType() {
        KeyboardUtils.hideSoftKeyboard(this);
        final int saveIndexBody = indexBody;
        View customTitleView = getLayoutInflater().inflate(R.layout.view_title_dialog_search_order, null);
        TextView customTitleDialog = customTitleView.findViewById(R.id.title_dialog);
        customTitleDialog.setText(R.string.title_dialog_search_body);

        bodyTypes = isMale() ? bodyAsset.bodyTypeMale : bodyAsset.bodyTypeFeMale;
        // array of body type should not have [all] option -> ignore first element
        final CharSequence[] arrayToDisplay = getBodyTypeDisplay(bodyTypes);

        new AlertDialog.Builder(this, R.style.Dialog_SingleChoice)
                .setCustomTitle(customTitleView)
                .setSingleChoiceItems(arrayToDisplay, saveIndexBody, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        indexBody = i;
                    }
                })
                .setPositiveButton(R.string.common_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        tvBodyType.setText(arrayToDisplay[indexBody]);
                    }
                })
                .setNegativeButton(R.string.common_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // nếu hủy bỏ trả lại index như cũ cho indexBody
                        indexBody = saveIndexBody;
                        dialogInterface.dismiss();
                    }
                })
                .show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CHOOSE_AREA:
                    List<RegionItem> areaList = data.getParcelableArrayListExtra(ChooseEachAreaActivity.EXTRA_RETURN_AFTER_SELECTED);
                    selectedArea = areaList;
                    for (RegionItem area : areaList) {
                        if (area.isChecked()) {
                            selectedRegion = area.value;
                            tvZone.setText(area.name);
                            break;
                        }
                    }
                    break;

                case CHOOSE_JOB:
                    selectedJob = data.getIntExtra(JobActivity.SELECTED_RESULT, -1);
                    if (selectedJob != -1)
                        tvJob.setText(isMale() ? jobs_male[selectedJob] : jobs_female[selectedJob]);
                    else {
                        tvJob.setText("");
                    }
                    break;

                case CHOOSE_HOBBY:
                    selectedHobby = data.getIntArrayExtra(HobbyActivity.SELECTED_RESULT);
                    if (selectedHobby != null) {
                        StringBuilder textHobby = new StringBuilder();
                        for (int pos : selectedHobby) {
                            textHobby.append(hobbies[pos]);
                            if (pos != selectedHobby[selectedHobby.length - 1])
                                textHobby.append(", ");
                        }
                        tvHobby.setText(textHobby.toString());
                    }
                    break;

                case Camera.REQUEST_CODE_CAMERA:
                case Gallery.REQUEST_CODE_GALLERY:
                    Parcelable[] output = data.getParcelableArrayExtra(MediaPickerBaseActivity.RESULT_KEY);
                    // only 1 image need to set avatar
                    selectedAvatar = (MediaFile) output[0];
                    loadAvatarThumbnail(selectedAvatar.getPath(), UserPreferences.getInstance().getGender());
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * load avatar and thumbnail
     *
     * @param path to load
     */
    private void loadAvatarThumbnail(String path, int gender) {
        ImagesUtils.loadRoundedAvatar(path, gender, avatar);
        ImagesUtils.loadBlurImageBanner(context, AppController.SCREEN_WIDTH, getResources().getDimensionPixelSize(R.dimen.edit_profile_cover_photo), path, coverPhoto);
    }

    @Override
    public void gotoMain(AuthenticationBean authData) {
        UserPreferences userPref = UserPreferences.getInstance();

        if (isClearWHenBack) {
            userPref.saveSuccessLoginData(authData, true);
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra(KEY_USER_INFO, authData);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else {
            userPref.saveSuccessLoginData(authData, false);
            Intent intent = new Intent();
            intent.putExtra(KEY_USER_INFO, authData);
            setResult(RESULT_OK, intent);
        }

        if (!TextUtils.isEmpty(password)) {
            userPref.savePassword(Utils.encryptPassword(password));
        }
        finish();
    }

    @Override
    public void onInvalidToken() {
        showConfirmDialog(R.string.msg_common_invalid_token, null, false);
    }

    @Override
    public void onUploadAvatarFail() {
        showConfirmDialog(R.string.upload_avatar_fail, null, false);
    }

    @Override
    public void onLoadUserInfoSuccess(UserInfoResponse response) {

    }

    @Override
    public void showLoadingDialog() {
        if (!loadingProgress.isShowing())
            loadingProgress.show();
    }

    @Override
    public void hideLoadingDialog() {
        loadingProgress.dismiss();
    }

    @Override
    public void onInvalidBirthday() {
        showConfirmDialog(R.string.invalid_birthday, null, false);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        for (int i = 0; i < gender.getChildCount(); i++) {
            gender.getChildAt(i).setEnabled(isChecked);
        }
    }

    @Override
    public void onToolbarButtonLeftClick(View view) {
        // clear data
        if (isClearWHenBack) {
            UserPreferences.getInstance().clear();
            password = null;
        }
        finish();
    }

    @Override
    public void onToolbarButtonRightClick(View view) {
        String email = edEmail.getText().toString().trim();
        String name = edName.getText().toString();
        String identifyNo = edIdentifyNo.getText().toString();
        String zone = tvZone.getText().toString();
        String ms = edMessage.getText().toString();

        ArrayList<String> listDetectsBannedWord;

        listDetectsBannedWord = DirtyWordUtils.listDetectsBannedWord(getBaseContext(), ms);
        if (listDetectsBannedWord.size() > 0) {
            showConfirmDialog(getString(R.string.banned_word_error, DirtyWordUtils.convertArraySetToString(listDetectsBannedWord)), null, false);
            return;
        }

        listDetectsBannedWord = DirtyWordUtils.listDetectsBannedWord(getBaseContext(), name);
        if (listDetectsBannedWord.size() > 0) {
            showConfirmDialog(getString(R.string.banned_word_error, DirtyWordUtils.convertArraySetToString(listDetectsBannedWord)), null, false);
            return;
        }

        if (TextUtils.isEmpty(name)) {
            showConfirmDialog(R.string.edit_profile_empty_name, null, false);
            return;
        }
        if (!isSignUpWithFb) {
            if (TextUtils.isEmpty(email)) {
                showConfirmDialog(R.string.edit_profile_empty_email, null, false);
                return;
            }
            if (!PatternsCompat.EMAIL_ADDRESS.matcher(email).matches()) {
                showConfirmDialog(R.string.email_invalid, null, false);
                return;
            }
        }

        if (TextUtils.isEmpty(identifyNo)) {
            showConfirmDialog(R.string.edit_profile_empty_identify_no, null, false);
            return;
        }
        if (TextUtils.isEmpty(zone)) {
            showConfirmDialog(R.string.edit_profile_empty_zone, null, false);
            return;
        }

        int bodyTypeValue = getBodyTypeValue(indexBody);
        if (isSignUpWithFb) {
            SignUpByFacebookRequest signUpByFacebookRequest = new SignUpByFacebookRequest(
                    facebookId,
                    name,
                    UserPreferences.getInstance().getBirthday(),
                    UserPreferences.getInstance().getGender(),
                    edIdentifyNo.getText().toString(),
                    selectedRegion,
                    edPhone.getText().toString(),
                    selectedJob,
                    tvHobby.getText().toString(),
                    bodyTypeValue,
                    ms,
                    Preferences.getInstance().getGPSADID(),
                    getLoginTime(),
                    FirebaseInstanceId.getInstance().getToken(),
                    SystemUtils.getAndroidOSVersion(),
                    getDeviceId(),
                    SystemUtils.getDeviceName(),
                    SystemUtils.getAndroidOSVersion()
            );

            getPresenter().signUpFabook(signUpByFacebookRequest);
        } else {
            EditProfileRequest editProfileRequest = new EditProfileRequest(
                    edName.getText().toString(),
                    bodyTypeValue,
                    UserPreferences.getInstance().getGender(),
                    tvHobby.getText().toString(),
                    selectedJob,
                    selectedRegion, // region
                    UserPreferences.getInstance().getToken(),
                    edEmail.getText().toString(),
                    UserPreferences.getInstance().getBirthday(),
                    edIdentifyNo.getText().toString(),
                    edPhone.getText().toString(),
                    ms
            );

            String path = "";
            if (selectedAvatar != null) {
                path = selectedAvatar.getPath();
            }
            getPresenter().editProfile(editProfileRequest, UserPreferences.getInstance().getToken(), FileUtils.dataFileMD5EncryptedToString(new File(path)), path);
        }
    }
}