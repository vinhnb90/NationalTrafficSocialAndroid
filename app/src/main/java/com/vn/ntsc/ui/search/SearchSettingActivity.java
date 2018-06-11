package com.vn.ntsc.ui.search;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nankai.designlayout.dialog.numberpicker.AgeBetweenPickerDialog;
import com.vn.ntsc.R;
import com.vn.ntsc.core.model.ServerResponse;
import com.vn.ntsc.core.views.BaseActivity;
import com.vn.ntsc.repository.ActivityResultRequestCode;
import com.vn.ntsc.repository.json.boby.BodyTypeItem;
import com.vn.ntsc.repository.json.gender.GenderType;
import com.vn.ntsc.repository.json.regions.RegionItem;
import com.vn.ntsc.repository.json.searchavatar.Avatar;
import com.vn.ntsc.repository.json.sortorder.Order;
import com.vn.ntsc.repository.model.search.MeetPeopleBean;
import com.vn.ntsc.repository.preferece.SearchSettingPreference;
import com.vn.ntsc.repository.preferece.UserPreferences;
import com.vn.ntsc.ui.main.MainActivity;
import com.vn.ntsc.ui.search.area.ChooseAreaActivity;
import com.vn.ntsc.ui.search.byname.SearchByNameActivity;
import com.vn.ntsc.ui.search.result.SearchResultFragment;
import com.vn.ntsc.utils.Constants;
import com.vn.ntsc.utils.LogUtils;
import com.vn.ntsc.utils.UserSetting;
import com.vn.ntsc.widget.toolbar.ToolbarTitleCenter;
import com.vn.ntsc.widget.views.dialog.ErrorApiDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static com.vn.ntsc.repository.ActivityResultRequestCode.REQUEST_CODE_SEARCH_BY_NAME;


/**
 * Created by KimTho on 14/08/2017.
 * Description
 * Xem giao diện màn hình trước khi đọc
 * <p>
 * - Khi bấm nút search: Add SearchResultFragment vào chính activity này, thay đổi title của toolbar, khi bấm Favorite hoặc unFavorite call đến addDataCallback() hoặc removeDataCallback
 * để lấy data nhét vào 1 mảng tý trả lại cho MainActivity
 * <p>
 * - Khi bấm searchByName: startActivityForResult sang SearchByNameActivity, SearchByNameActivity cũng add fragment SearchResultFragment,
 * khi bấm Favorite hoặc unFavorite call đến addDataCallback() hoặc removeDataCallback để lấy data nhét vào 1 mảng tý trả lại cho SearchSettingActivity này
 */

public class SearchSettingActivity extends BaseActivity {
    public static final String ACTIVITY_RESULT_LIST_MEET_PEOPLE = "RESULT_LIST_MEET_PEOPLE";
    public static final String NONE = "";
    public static final String NORMAL = "Bình thường";
    public static final String SLIGHTYFLAT = "Hơi béo";
    public static final String FAT = "Béo";


    private List<String> valueBody = new ArrayList<>();
    private SearchSetting mSearchSetting;

    @BindView(R.id.toolbar)
    ToolbarTitleCenter mToolbar;

    @BindView(R.id.activity_search_setting_layout_age)
    RelativeLayout mLayoutAge;
    @BindView(R.id.activity_search_setting_layout_regions)
    RelativeLayout mLayoutArea;
    @BindView(R.id.activity_search_setting_layout_sort_order)
    RelativeLayout mLayoutSortOrder;
    @BindView(R.id.activity_search_setting_layout_body)
    RelativeLayout mLayoutFigure;
    @BindView(R.id.activity_search_setting_layout_avatar)
    RelativeLayout mLayoutAvatar;
    @BindView(R.id.activity_search_setting_layout_search_by_name)
    RelativeLayout mLayoutSearchByNam;
    @BindView(R.id.activity_search_setting_layout_gender)
    RelativeLayout mLayoutGender;

    @BindView(R.id.activity_search_setting_txt_name_gender)
    TextView mTxtGender;
    @BindView(R.id.activity_search_setting_txt_regions)
    TextView mTxtAreas;
    @BindView(R.id.activity_search_setting_txt_order)
    TextView mTxtOrder;
    @BindView(R.id.activity_search_setting_txt_body)
    TextView mTxtBody;
    @BindView(R.id.activity_search_setting_txt_avatar)
    TextView mTxtAvatar;
    @BindView(R.id.activity_search_setting_txt_age)
    TextView mTxtRangeAge;
    @BindView(R.id.activity_search_setting_chkb_login_within_24h)
    AppCompatCheckBox mChkbLoginWithin24h;
    @BindView(R.id.activity_search_setting_chkb_not_interacted)
    AppCompatCheckBox mChkbNotInteracted;

    @BindView(R.id.activity_search_setting_btn_search)
    LinearLayout mBtnSearch;

    private List<MeetPeopleBean> mDataCallback = new ArrayList<>();

    private AgeBetweenPickerDialog mDialogAgePicker;
    private int mLowerAge = Constants.SEARCH_SETTING_AGE_MIN_LIMIT;
    private int mUpperAge = Constants.SEARCH_SETTING_AGE_MAX_LIMIT;

    private boolean isInteracted;
    private boolean isLogin24h;
    private List<BodyTypeItem> mBodies;
    private List<Order> mOrderSorts;
    private List<Avatar> mAvatars;
    private List<GenderType> mGender;

    private BodyTypeItem mCurrentBodyType;
    private Order mCurrentSort;
    private Avatar mCurrentAvatar;
    private List<RegionItem> mSelectedRegions;
    private GenderType mCurrentGender;
    private int genderType = -1;
    private String botyType;

    public static void launch(AppCompatActivity activity, @ActivityResultRequestCode int requestCode) {
        Intent intent = new Intent(activity, SearchSettingActivity.class);
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_search_setting;
    }

    @Override
    public void onCreateView(View rootView) {
    }

    @Override
    public void onViewReady() {
        setSupportActionBar(mToolbar);
        mToolbar.setActionbar(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        // get from sharePreference
        mSearchSetting = SearchSettingPreference.getInstance().getSearchSetting();
        if (mSearchSetting == null) {
            mSearchSetting = new SearchSetting(this);
        }

        mOrderSorts = mSearchSetting.getListOrder();
        mAvatars = mSearchSetting.getListAvatar();
        mGender = mSearchSetting.getListGender();

        mSelectedRegions = mSearchSetting.getSelectedRegions();

        mLowerAge = mSearchSetting.getMinAge();
        mUpperAge = mSearchSetting.getMaxAge();

        mCurrentBodyType = mSearchSetting.getCurrentBodyType();
        mCurrentSort = mSearchSetting.getCurrentOrderSort();
        mCurrentAvatar = mSearchSetting.getCurrentAvatar();
        mCurrentGender = mSearchSetting.getCurrentGender();

        if (UserPreferences.getInstance().getToken().equals("")) {
            mChkbNotInteracted.setChecked(false);
        } else {
            mChkbNotInteracted.setChecked(isInteracted);
        }
        isInteracted = mSearchSetting.isNoInteracted();
        isLogin24h = mSearchSetting.isLoginWithin24h();

        // set data for view


        mChkbLoginWithin24h.setChecked(isLogin24h);
        if (mLowerAge == 0 && mUpperAge == 0) {
            mLowerAge = Constants.SEARCH_SETTING_AGE_MIN_LIMIT;
            mUpperAge = Constants.SEARCH_SETTING_AGE_MAX_LIMIT;
        }

        mTxtRangeAge.setText(getTextRangeAge(mLowerAge, mUpperAge));
        mTxtAreas.setText(getNameFromAreasChecked(mSelectedRegions));
        mTxtOrder.setText(mCurrentSort.name);
        mTxtBody.setText(mCurrentBodyType.name);
        mTxtAvatar.setText(mCurrentAvatar.name);
        mTxtGender.setText(mCurrentGender.name);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                FragmentManager fm = getSupportFragmentManager();
                int count = fm.getBackStackEntryCount();
                if (count > 0) {
                    Fragment fragment = fm.findFragmentById(R.id.activity_search_setting_root);
                    if (fragment instanceof SearchResultFragment) {
                        mToolbar.setTitleCenter(R.string.title_activity_search_setting);
                        onBackPressed();
                    }
                } else {
                    finish();
                }
                break;
        }
        return true;
    }

    @OnClick(R.id.activity_search_setting_btn_search)
    public void search() {

        boolean isNewLogin = mChkbLoginWithin24h.isChecked();
        boolean isInteracter = mChkbNotInteracted.isChecked();
        mSearchSetting.setSelectedRegion(mSelectedRegions);
        mSearchSetting.setMinAge(mLowerAge);
        mSearchSetting.setMaxAge(mUpperAge);
        mSearchSetting.setLoginWithin24h(isNewLogin);
        mSearchSetting.setNoInteracted(isInteracter);
        mSearchSetting.setCurrentOrderSort(mCurrentSort);
        mSearchSetting.setCurrentGender(mCurrentGender);
        mSearchSetting.setCurrentBodyType(mCurrentBodyType);
        mSearchSetting.setCurrentAvatar(mCurrentAvatar);
        SearchSettingPreference.getInstance().saveSearchSetting(mSearchSetting);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.activity_search_setting_root, SearchResultFragment.newInstance(mSearchSetting, botyType));
        ft.addToBackStack(SearchResultFragment.class.getSimpleName());
        ft.commit();

        mToolbar.setTitleCenter(R.string.title_activity_search_result);
    }

    @OnClick(R.id.activity_search_setting_chkb_not_interacted)
    void setInteracted() {
        LogUtils.d("setInteracted", "11 " + UserPreferences.getInstance().getToken());
        if (UserPreferences.getInstance().getToken().equals("")) {
            LogUtils.d("setInteracted", "22");
            onShowDialogLogin(ServerResponse.DefinitionCode.SERVER_EXPIRED_TOKEN);
            mChkbNotInteracted.setChecked(false);
        }
    }

    @OnClick(R.id.activity_search_setting_layout_age)
    public void setAge() {
        showAgeDialogPicker();
    }

    // Nhẩy đến màn hinhd ChooseAreaActivity để c họn những địa điểm cần tìm kiếm
    @OnClick(R.id.activity_search_setting_layout_regions)
    public void setArea() {
        ChooseAreaActivity.launch(this, mSelectedRegions, ActivityResultRequestCode.REQUEST_CODE_CHOOSE_AREA);
    }

    @OnClick(R.id.activity_search_setting_layout_gender)
    public void setGender() {

        final GenderType temp = mCurrentGender;
        // Lấy tên của các item Gender
        final String[] gender = new String[mGender.size()];
        for (int i = 0; i < mGender.size(); i++) {
            gender[i] = mGender.get(i).name;
        }

        // Lấy vị trí của mCurrentGender để check lên list
        int position = mGender.indexOf(mCurrentGender);
        if (position == -1) mCurrentGender = mGender.get(0);

        View customTitleView = getLayoutInflater().inflate(R.layout.view_title_dialog_search_order, null);
        TextView customTitleDialog = customTitleView.findViewById(R.id.title_dialog);
        customTitleDialog.setText(R.string.act_search_setting_gender);
        new AlertDialog.Builder(this, R.style.Dialog_SingleChoice)
                .setCustomTitle(customTitleView)
                .setCancelable(false)
                .setSingleChoiceItems(gender, position, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mCurrentGender = mGender.get(i);
                    }
                })
                .setPositiveButton(R.string.common_ok_2, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mTxtGender.setText(mCurrentGender.name);
                        mTxtBody.setText(NONE);
                        if (mCurrentGender.name.equals(getString(R.string.common_man))) {
                            genderType = UserSetting.GENDER_MALE;
                        } else if (mCurrentGender.name.equals(getString(R.string.common_woman))) {
                            genderType = UserSetting.GENDER_FEMALE;
                        } else {
                            genderType = UserSetting.GENDER_ALL;
                        }
                    }
                })
                .setNegativeButton(R.string.common_cancel_2, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // nếu hủy bỏ trả lại index cũ cho index Gender
                        dialogInterface.dismiss();
                        mCurrentGender = temp;
                    }
                })
                .show();

    }

    @OnClick(R.id.activity_search_setting_layout_sort_order)
    public void setSortOrder() {
        // Tạo Order tạm thời, nếu người dùng ấn Cancel thì gán lại giá trị cũ
        final Order temp = mCurrentSort;

        // Lấy tên của các item sort
        final String[] sorts = new String[mOrderSorts.size()];
        for (int i = 0; i < mOrderSorts.size(); i++) {
            sorts[i] = mOrderSorts.get(i).name;
        }

        // Lấy vị trí của mCurrentBodyType để check lên list
        int position = mOrderSorts.indexOf(mCurrentSort);
        if (position == -1) mCurrentSort = mOrderSorts.get(0);

        View customTitleView = getLayoutInflater().inflate(R.layout.view_title_dialog_search_order, null);
        TextView customTitleDialog = customTitleView.findViewById(R.id.title_dialog);
        customTitleDialog.setText(R.string.title_dialog_search_sort_oder);
        new AlertDialog.Builder(this, R.style.Dialog_SingleChoice)
                .setCustomTitle(customTitleView)
                .setCancelable(false)
                .setSingleChoiceItems(sorts, position, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mCurrentSort = mOrderSorts.get(i);
                    }
                })
                .setPositiveButton(R.string.common_ok_2, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mTxtOrder.setText(mCurrentSort.name);
                    }
                })
                .setNegativeButton(R.string.common_cancel_2, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // nếu hủy bỏ trả lại index như cũ cho indexOrder
                        dialogInterface.dismiss();
                        mCurrentSort = temp;
                    }
                })
                .show();
    }

    @OnClick(R.id.activity_search_setting_layout_body)
    public void setBody() {
        // Tạo CurrentBodyType tạm thời, nếu người dùng ấn Cancel thì gán lại giá trị cũ
        loadDataBody(genderType);
        final BodyTypeItem temp = mCurrentBodyType;

        // Lấy tên của các item body
        final String[] bodyArr = new String[mBodies.size()];
        for (int i = 0; i < mBodies.size(); i++) {
            bodyArr[i] = mBodies.get(i).name;
        }
        // Lấy vị trí của mCurrentBodyType để check lên list
        int position = mBodies.indexOf(mCurrentBodyType);
        if (position == -1) mCurrentBodyType = mBodies.get(0);

        View customTitleView = getLayoutInflater().inflate(R.layout.view_title_dialog_search_order, null);
        TextView customTitleDialog = customTitleView.findViewById(R.id.title_dialog);
        customTitleDialog.setText(R.string.title_dialog_search_body);
        new AlertDialog.Builder(this, R.style.Dialog_SingleChoice)
                .setCustomTitle(customTitleView)
                .setCancelable(false)
                .setSingleChoiceItems(bodyArr, mBodies.indexOf(mCurrentBodyType), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int position) {
                        mCurrentBodyType = mBodies.get(position);
                    }
                })
                .setPositiveButton(R.string.common_ok_2, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        mTxtBody.setText(mCurrentBodyType.name);
                        checkValueBodyType(mCurrentBodyType.name);
                    }
                })
                .setNegativeButton(R.string.common_cancel_2, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // nếu hủy bỏ trả lại giá trị cũ cho mCurrentBodyType
                        dialogInterface.dismiss();
                        mCurrentBodyType = temp;
                    }
                })
                .show();
    }


    private void checkValueBodyType(String boty) {
        switch (boty) {
            case NORMAL:
                botyType = valueBody.get(0);
                break;
            case SLIGHTYFLAT:
                botyType = valueBody.get(1);
                break;
            case FAT:
                botyType = valueBody.get(2);
                break;
        }
    }

    @OnClick(R.id.activity_search_setting_layout_avatar)
    public void setAvatar() {
        // Tạo Avatar tạm thời, nếu người dùng ấn Cancel thì gán lại giá trị cũ
        final Avatar temp = mCurrentAvatar;

        // Lấy tên của các item avatar
        final String[] avatars = new String[mAvatars.size()];
        for (int i = 0; i < mAvatars.size(); i++) {
            avatars[i] = mAvatars.get(i).name;
            LogUtils.d("avatars  ", "111 " + avatars[i]);
        }

        // Lấy vị trí của mCurrentAvatar để check lên list
        int position = mAvatars.indexOf(mCurrentAvatar);
        if (position == -1) mCurrentAvatar = mAvatars.get(0);

        View customTitleView = getLayoutInflater().inflate(R.layout.view_title_dialog_search_order, null);
        TextView customTitleDialog = customTitleView.findViewById(R.id.title_dialog);
        customTitleDialog.setText(R.string.title_dialog_search_avatar);
        new AlertDialog.Builder(this, R.style.Dialog_SingleChoice)
                .setCustomTitle(customTitleView)
                .setCancelable(false)
                .setSingleChoiceItems(avatars, position, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mCurrentAvatar = mAvatars.get(i);
                    }
                })
                .setPositiveButton(R.string.common_ok_2, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mTxtAvatar.setText(mCurrentAvatar.name);
                    }
                })
                .setNegativeButton(R.string.common_cancel_2, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // nếu hủy bỏ trả lại index cũ cho indexAvatar
                        dialogInterface.dismiss();
                        mCurrentAvatar = temp;
                    }
                })
                .show();
    }

    @OnClick(R.id.activity_search_setting_layout_search_by_name)
    public void searchByName() {
        Intent intent = new Intent(this, SearchByNameActivity.class);
        startActivityForResult(intent, REQUEST_CODE_SEARCH_BY_NAME);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ActivityResultRequestCode.REQUEST_CODE_CHOOSE_AREA) {
            if (resultCode == Activity.RESULT_OK) {
                mSelectedRegions = data.getParcelableArrayListExtra(ChooseAreaActivity.EXTRA_RETURN_LIST_AREA);
                LogUtils.d("mSelectedRegions ", "22 " + mSelectedRegions);
                mTxtAreas.setText(getNameFromAreasChecked(mSelectedRegions));
            }
        }

        if (requestCode == REQUEST_CODE_SEARCH_BY_NAME) {
            if (resultCode == Activity.RESULT_OK) {
                mDataCallback = data.getParcelableArrayListExtra(ACTIVITY_RESULT_LIST_MEET_PEOPLE);
                Intent returnIntent = new Intent();
                returnIntent.putParcelableArrayListExtra(MainActivity.BUNDLE_MEET_PEOPLE_SEARCH, (ArrayList<? extends Parcelable>) mDataCallback);
                setResult(RESULT_OK, returnIntent);
            }
        }
    }

    private void showAgeDialogPicker() {
        if (mDialogAgePicker == null) {
            mDialogAgePicker = new AgeBetweenPickerDialog(this,
                    R.string.title_dialog_set_range_age,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            int min = mDialogAgePicker.getMinAge();
                            int max = mDialogAgePicker.getMaxAge();
                            if (min > max) {
                                ErrorApiDialog
                                        .showAlert(
                                                SearchSettingActivity.this,
                                                R.string.title_error_user_age,
                                                R.string.error_age_min_bigger_max);
                            } else {
                                mLowerAge = min;
                                mUpperAge = max;
                                mTxtRangeAge.setText(getTextRangeAge(min, max));
                            }
                        }
                    });

            mDialogAgePicker.setRangeOne(
                    Constants.SEARCH_SETTING_AGE_MIN_LIMIT,
                    Constants.SEARCH_SETTING_AGE_MAX_LIMIT);
            mDialogAgePicker.setRangeTwo(
                    Constants.SEARCH_SETTING_AGE_MIN_LIMIT,
                    Constants.SEARCH_SETTING_AGE_MAX_LIMIT);
            mDialogAgePicker.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT);
            mDialogAgePicker.setInverseBackgroundForced(true);
            int flag = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
            mDialogAgePicker.getWindow().setFlags(flag, flag);
        }

        if (!mDialogAgePicker.isShowing()) {
            mDialogAgePicker.setInitialValue(mLowerAge, mUpperAge);
            mDialogAgePicker.show();
        }
    }

    private String getTextRangeAge(int minAge, int maxAge) {
        return String.valueOf(minAge) + " ~ " + maxAge;
    }

    // Lấy Tên các địa điểm đã được checked
    // Định dạng: "Hà nội, Hải phòng, Nghệ an, ..."
    private String getNameFromAreasChecked(List<RegionItem> areas) {
        StringBuilder builder = new StringBuilder();
        for (RegionItem area : areas) {
            if (area.isChecked()) {
                builder.append(area.name);
                builder.append(", ");
            }
        }
        return builder.toString();
    }

    //============================= Return this datas to MainActivity ==============================
    // 2 method này được gọi khi Fragment: SearchResult được add trực tiếp trên SearchSetting Activity này
    // và khi "favorite" hoặc "unFavorite"

    /**
     * Được gọi khi thêm favorite ở fragment SearchResultFragment
     */
    public void addDataCallback(MeetPeopleBean meetPeopleBean) {
        mDataCallback.add(meetPeopleBean);
    }

    /**
     * Được gọi khi thêm favorite ở fragment SearchResultFragment
     */
    public void removeDataCallback(MeetPeopleBean meetPeopleBean) {
        mDataCallback.remove(meetPeopleBean);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent returnIntent = new Intent();
        returnIntent.putParcelableArrayListExtra(MainActivity.BUNDLE_MEET_PEOPLE_SEARCH, (ArrayList<? extends Parcelable>) mDataCallback);
        setResult(RESULT_OK, returnIntent);
    }

    private void loadDataBody(int genderType) {
        switch (genderType) {
            case UserSetting.GENDER_ALL:
                mBodies = new ArrayList<>();
                LogUtils.d("loadDataBody", "GENDER_ALL " + genderType);
                mBodies.addAll(mSearchSetting.getArrayBodyMale());
                for (int i = 0; i < mSearchSetting.getArrayBodyFemale().size(); i++) {
                    if (!mBodies.get(i).name.contains(mSearchSetting.getArrayBodyFemale().get(i).name)) {
                        mBodies.add(mSearchSetting.getArrayBodyFemale().get(i));
                    } else {
                        if (mSearchSetting.getArrayBodyMale().get(i).value != -1) {
                            valueBody.add(mSearchSetting.getArrayBodyMale().get(i).value + "," + mSearchSetting.getArrayBodyFemale().get(i).value);
                            LogUtils.d("mSearchSetting", "11 " + mSearchSetting.getArrayBodyFemale().get(i).value + "__" + mSearchSetting.getArrayBodyMale().get(i).value);
                        }
                    }
                }

                break;
            case UserSetting.GENDER_FEMALE:
                mBodies = new ArrayList<>();
                LogUtils.d("loadDataBody", "GENDER_FEMALE " + genderType);
                mBodies = mSearchSetting.getArrayBodyFemale();
                break;
            case UserSetting.GENDER_MALE:
                mBodies = new ArrayList<>();
                LogUtils.d("loadDataBody", "GENDER_MALE " + genderType);
                mBodies = mSearchSetting.getArrayBodyMale();
                break;
        }
    }
}
