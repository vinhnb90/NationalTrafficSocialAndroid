package com.vn.ntsc.ui.setting.notification;

import android.content.Intent;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.vn.ntsc.R;
import com.vn.ntsc.core.views.BaseActivity;
import com.vn.ntsc.repository.preferece.NotificationSettingPreference;
import com.vn.ntsc.ui.main.MainActivity;
import com.vn.ntsc.widget.toolbar.ToolbarTitleCenter;

import butterknife.BindView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

import static com.vn.ntsc.ui.setting.notification.NotificationReceiveMessageActivity.RESULT;

/**
 * Created by ThoNh on 10/4/2017.
 */

public class NotificationSettingActivity extends BaseActivity {
    private static final String TAG = NotificationSettingActivity.class.getSimpleName();
    private static final int NOTIFICATION_RECEIVE_MESSAGE_SETTING = 22;

    @BindView(R.id.toolbar)
    ToolbarTitleCenter mToolbar;

    @BindView(R.id.activity_notification_setting_swt_vibrate)
    SwitchCompat mSwtVibrate;

    @BindView(R.id.activity_notification_setting_swt_sound)
    SwitchCompat mSwtSound;

    @BindView(R.id.activity_notification_setting_swt_app_notify)
    SwitchCompat mSwtAppNotify;

    @BindView(R.id.activity_notification_setting_swt_fav_notify)
    SwitchCompat mSwtFavNotify;

    @BindView(R.id.activity_notification_setting_tv_msg_notify)
    TextView mTvMsgNotify;

    private int selectedResult = NotificationReceiveMessageActivity.ALL;

    public static void newInstance(MainActivity mainActivity) {
        Intent intent = new Intent(mainActivity, NotificationSettingActivity.class);
        mainActivity.startActivity(intent);
    }

    private int[] notificationMessageResult = new int[]{
            R.string.notification_setting_all,
            R.string.notification_setting_no_one,
            R.string.notification_setting_only_favorite
    };

    @Override
    public int getLayoutId() {
        return R.layout.activity_notification_setting;
    }

    @Override
    public void onCreateView(View rootView) {
    }

    @Override
    public void onViewReady() {
        setSupportActionBar(mToolbar);
        mToolbar.setActionbar(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        getNotificationCurrentState();
    }

    private void getNotificationCurrentState() {
        mSwtVibrate.setChecked(NotificationSettingPreference.getInstance().isVibrate());
        mSwtSound.setChecked(NotificationSettingPreference.getInstance().isSound());
        mSwtAppNotify.setChecked(NotificationSettingPreference.getInstance().isNotificationApp());
        mSwtFavNotify.setChecked(NotificationSettingPreference.getInstance().isNotificationFav());

        try {
            selectedResult = NotificationSettingPreference.getInstance().getNotificationSelection();
        } catch (ClassCastException e) {
            e.printStackTrace();
            NotificationSettingPreference.getInstance().saveNotificationSelection(selectedResult);
            selectedResult = NotificationSettingPreference.getInstance().getNotificationSelection();
        }

        mTvMsgNotify.setText(notificationMessageResult[selectedResult]);
    }

    @OnClick(R.id.activity_notification_setting_tv_msg_notify)
    public void notificationMessageClick() {
        NotificationReceiveMessageActivity.start(this, NOTIFICATION_RECEIVE_MESSAGE_SETTING, selectedResult);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == NOTIFICATION_RECEIVE_MESSAGE_SETTING) {
            selectedResult = data.getIntExtra(RESULT, NotificationReceiveMessageActivity.ALL);
            String textNotify = getString(notificationMessageResult[selectedResult]);
            mTvMsgNotify.setText(textNotify);
            NotificationSettingPreference.getInstance().saveNotificationSelection(selectedResult);
        }
    }

    @OnCheckedChanged(R.id.activity_notification_setting_swt_vibrate)
    void changeSettingVibrate(CompoundButton compoundButton, boolean isChecked) {
        NotificationSettingPreference.getInstance().saveVibrate(isChecked);
    }

    @OnCheckedChanged(R.id.activity_notification_setting_swt_sound)
    void changeSettingSound(CompoundButton compoundButton, boolean isChecked) {
        NotificationSettingPreference.getInstance().saveSound(isChecked);
    }

    @OnCheckedChanged(R.id.activity_notification_setting_swt_app_notify)
    void changeSettingAppNotify(CompoundButton compoundButton, boolean isChecked) {
        NotificationSettingPreference.getInstance().saveNotificationApp(isChecked);
    }

    @OnCheckedChanged(R.id.activity_notification_setting_swt_fav_notify)
    void changeSettingFavNotify(CompoundButton compoundButton, boolean isChecked) {
        NotificationSettingPreference.getInstance().saveNotificationFav(isChecked);
    }
}
