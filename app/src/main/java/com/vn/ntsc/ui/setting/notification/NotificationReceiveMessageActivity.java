package com.vn.ntsc.ui.setting.notification;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.vn.ntsc.R;
import com.vn.ntsc.core.views.BaseActivity;
import com.vn.ntsc.widget.toolbar.ToolbarTitleCenter;
import com.vn.ntsc.widget.views.checkbox.MyCheckboxButton;

import butterknife.BindView;

/**
 * http://10.64.100.201/issues/12039
 */
public class NotificationReceiveMessageActivity extends BaseActivity implements MyCheckboxButton.CheckedChangeListener {

    public static final String RESULT = "select";

    public static final int ALL = 0;
    public static final int NO_ONE = 1;
    public static final int ONLY_FAVORITE = 2;

    @BindView(R.id.toolbar)
    ToolbarTitleCenter mToolbar;

    @BindView(R.id.activity_notification_receive_message_all)
    MyCheckboxButton cbxAll;
    @BindView(R.id.activity_notification_receive_message_no_one)
    MyCheckboxButton cbxNoOne;
    @BindView(R.id.activity_notification_receive_message_only_favorite)
    MyCheckboxButton cbxOnlyFavourite;


    @Override
    public int getLayoutId() {
        return R.layout.activity_notification_receive_message;
    }

    @Override
    public void onCreateView(View rootView) {
    }

    @Override
    public void onViewReady() {
        setSupportActionBar(mToolbar);
        mToolbar.setActionbar(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        int selectedValue = getIntent().getIntExtra(RESULT, -1);
        setSelected(selectedValue);

        cbxAll.setListener(this);
        cbxNoOne.setListener(this);
        cbxOnlyFavourite.setListener(this);
    }

    /**
     * restore state from bundle
     */
    private void setSelected(int selectedValue) {
        if (selectedValue == ALL) {
            cbxAll.setChecked(true);
        }
        if (selectedValue == NO_ONE) {
            cbxNoOne.setChecked(true);
        }
        if (selectedValue == ONLY_FAVORITE) {
            cbxOnlyFavourite.setChecked(true);
        }
    }

    @Override
    public void onChecked(int id) {
        clearCheckedExcept(id);

        setResult(RESULT_OK, new Intent().putExtra(RESULT, getResultValue(id)));
        finish();
    }

    /**
     * clear another checkbox with exception
     *
     * @param id skip button with id
     */
    private void clearCheckedExcept(int id) {
        if (id != R.id.activity_notification_receive_message_all) {
            cbxAll.setChecked(false);
        }

        if (id != R.id.activity_notification_receive_message_no_one) {
            cbxNoOne.setChecked(false);
        }

        if (id != R.id.activity_notification_receive_message_only_favorite) {
            cbxOnlyFavourite.setChecked(false);
        }
    }

    /**
     * @return result code base on id
     */
    private int getResultValue(int id) {
        switch (id) {
            case R.id.activity_notification_receive_message_all:
                return ALL;
            case R.id.activity_notification_receive_message_no_one:
                return NO_ONE;
            case R.id.activity_notification_receive_message_only_favorite:
                return ONLY_FAVORITE;
        }
        return -1;
    }

    public static void start(AppCompatActivity activity, int requestCode, int selectedResult) {
        Intent intent = new Intent(activity, NotificationReceiveMessageActivity.class);
        intent.putExtra(RESULT, selectedResult);
        activity.startActivityForResult(intent, requestCode);
    }
}
