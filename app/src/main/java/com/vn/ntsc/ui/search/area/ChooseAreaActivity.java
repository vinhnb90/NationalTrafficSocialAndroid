package com.vn.ntsc.ui.search.area;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RelativeLayout;

import com.vn.ntsc.R;
import com.vn.ntsc.core.views.BaseActivity;
import com.vn.ntsc.repository.ActivityResultRequestCode;
import com.vn.ntsc.repository.json.regions.RegionItem;
import com.vn.ntsc.utils.LogUtils;
import com.vn.ntsc.widget.toolbar.ToolbarButtonRightClickListener;
import com.vn.ntsc.widget.toolbar.ToolbarTitleCenter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static com.vn.ntsc.ui.search.area.ChooseEachAreaActivity.EXTRA_RETURN_AFTER_SELECTED;


public class ChooseAreaActivity extends BaseActivity {

    public static final String EXTRA_CHOOSE_AREA_TYPE = "EXTRA_CHOOSE_AREA_TYPE";

    public static final String EXTRA_LIST_AREA = "EXTRA_LIST_AREA";

    public static final String EXTRA_RETURN_LIST_AREA = "EXTRA_RETURN_LIST_AREA";

    public static final String MY_PREFS_NAME = "MY_PREFS_NAME";
    public static final String KEY_AREAS = "KEY_AREAS";


    @BindView(R.id.toolbar)
    ToolbarTitleCenter mToolbar;

    @BindView(R.id.activity_choose_area_btn_choose_each_area)
    RelativeLayout mLayoutChooseArea;

    @BindView(R.id.activity_choose_area_btn_choose_all)
    RelativeLayout mLayoutChooseAll;

    @BindView(R.id.activity_choose_area_radio_choose_all)
    RadioButton mRadioChooseAll;

    @BindView(R.id.activity_choose_area_radio_choose_each_area)
    RadioButton mRadioChooseArea;

    private ArrayList<RegionItem> mSelectedRegions;
    private int AREASTYPE = 0;

    public static void launch(AppCompatActivity activity, List<RegionItem> areas, @ActivityResultRequestCode int requestCode) {
        Intent intent = new Intent(activity, ChooseAreaActivity.class);
        intent.putParcelableArrayListExtra(EXTRA_LIST_AREA, (ArrayList<? extends Parcelable>) areas);
        activity.startActivityForResult(intent, requestCode);

    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_choose_area;
    }

    @Override
    public void onCreateView(View rootView) {
        mSelectedRegions = getIntent().getParcelableArrayListExtra(EXTRA_LIST_AREA);
    }

    @Override
    public void onViewReady() {
        setSupportActionBar(mToolbar);
        mToolbar.setActionbar(getSupportActionBar()).setDisplayHomeAsUpEnabled(true).setButtonRightListener(new ToolbarButtonRightClickListener() {
            @Override
            public void onToolbarButtonRightClick(View view) {
                SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                editor.putInt(KEY_AREAS, AREASTYPE);
                editor.apply();
                editor.commit();

                Intent returnIntent = new Intent();
                LogUtils.d("mSelectedRegions", " " + mSelectedRegions);
                returnIntent.putExtra(EXTRA_RETURN_LIST_AREA, mSelectedRegions);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });

        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        AREASTYPE = prefs.getInt(KEY_AREAS, 0); //0 is the default value.

        switch (AREASTYPE) {
            case 1:
                mRadioChooseArea.setChecked(true);
                mRadioChooseAll.setChecked(false);
                break;
            case 2:
                mRadioChooseArea.setChecked(false);
                mRadioChooseAll.setChecked(true);
                break;
        }

    }

    @OnClick({R.id.activity_choose_area_btn_choose_each_area, R.id.activity_choose_area_btn_choose_all, R.id.activity_choose_area_radio_choose_all, R.id.activity_choose_area_radio_choose_each_area})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.activity_choose_area_btn_choose_each_area:
                AREASTYPE = 1;
                ChooseEachAreaActivity.launchRegions(this, mSelectedRegions, ActivityResultRequestCode.REQUEST_CHOOSE_AREAS);
                mRadioChooseArea.setChecked(true);
                mRadioChooseAll.setChecked(false);
                break;
            case R.id.activity_choose_area_btn_choose_all:
                AREASTYPE = 2;
                for (int i = 0; i < mSelectedRegions.size(); i++) {
                    if (!(mSelectedRegions.get(i).value == -1)) {
                        mSelectedRegions.get(i).setChecked(true);
                    }
                    LogUtils.d("mSelectedRegions", "mSelectedRegions " + mSelectedRegions.get(i).name);
                }
                mRadioChooseArea.setChecked(false);
                mRadioChooseAll.setChecked(true);
                break;
            case R.id.activity_choose_area_radio_choose_all:
                AREASTYPE = 2;
                for (int i = 0; i < mSelectedRegions.size(); i++) {
                    if (!(mSelectedRegions.get(i).value == -1)) {
                        mSelectedRegions.get(i).setChecked(true);
                    }
                    LogUtils.d("mSelectedRegions", "mSelectedRegions " + mSelectedRegions.get(i).name);
                }
                mRadioChooseArea.setChecked(false);
                mRadioChooseAll.setChecked(true);
                break;
            case R.id.activity_choose_area_radio_choose_each_area:
                AREASTYPE = 1;
                ChooseEachAreaActivity.launchRegions(this, mSelectedRegions, ActivityResultRequestCode.REQUEST_CHOOSE_AREAS);
                mRadioChooseArea.setChecked(true);
                mRadioChooseAll.setChecked(false);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ActivityResultRequestCode.REQUEST_CHOOSE_AREAS) {
            if (resultCode == Activity.RESULT_OK) {
                mSelectedRegions = data.getParcelableArrayListExtra(EXTRA_RETURN_AFTER_SELECTED);
                LogUtils.d("mSelectedRegions", "11 " + mSelectedRegions);
            }
        }


    }
}
