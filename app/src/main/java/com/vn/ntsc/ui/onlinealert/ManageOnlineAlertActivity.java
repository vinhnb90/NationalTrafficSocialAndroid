package com.vn.ntsc.ui.onlinealert;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.nankai.designlayout.dialog.DialogMaterial;
import com.nankai.designlayout.dialog.enums.Style;
import com.vn.ntsc.R;
import com.vn.ntsc.core.views.BaseActivity;
import com.vn.ntsc.repository.model.onlinealert.AddOnlineAlertRequest;
import com.vn.ntsc.repository.model.onlinealert.AddOnlineAlertResponse;
import com.vn.ntsc.repository.model.onlinealert.GetOnlineAlertRequest;
import com.vn.ntsc.repository.model.onlinealert.GetOnlineAlertResponse;
import com.vn.ntsc.repository.model.user.UserInfoResponse;
import com.vn.ntsc.repository.preferece.UserPreferences;
import com.vn.ntsc.utils.Constants;
import com.vn.ntsc.utils.ImagesUtils;
import com.vn.ntsc.widget.toolbar.ToolbarButtonRightClickListener;
import com.vn.ntsc.widget.toolbar.ToolbarTitleCenter;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by nankai on 9/19/2017.
 */

public class ManageOnlineAlertActivity extends BaseActivity<ManageOnlineAlertPresenter> implements ManageOnlineAlertContract.View, ToolbarButtonRightClickListener {

    private static final String ELEMENT_ONLINE_ALERT = "element.online.alert";
    private static final String EXTRA_USER_PROFILE = "extra.user.profile";

    public static final int ALERT_NO = 0;
    public static final int ALERT_YES = 1;

    private UserInfoResponse userProfileBean;
    private int valueWhen = Constants.MANAGE_ONLINE_NEVER;
    private String[] mValueWhenStrArray;
    private String strWhen;

    @BindView(R.id.activity_manage_online_alert_txtUserName)
    TextView txtUserName;
    @BindView(R.id.activity_manage_online_alert_iv_avatar)
    ImageView imgAvatar;
    @BindView(R.id.activity_manage_online_alert_cbx_is_alert)
    CheckBox cbxAlert;
    @BindView(R.id.activity_manage_online_alert_txt_when)
    TextView txtWhen;
    @BindView(R.id.activity_manage_online_alert_txt_when_title)
    TextView txtWhenTitle;
    @BindView(R.id.activity_manage_online_alert_rlt_when)
    View rltLayout;
    @BindView(R.id.activity_manage_online_alert_cbx_is_alert_layout)
    View cbxLayout;
    @BindView(R.id.toolbar)
    ToolbarTitleCenter mToolbar;

    private AlertDialog dialogWhen;

    public static void launch(AppCompatActivity activity, View view, UserInfoResponse userProfileBean) {
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, view, ELEMENT_ONLINE_ALERT);
        Intent intent = new Intent();
        intent.putExtra(EXTRA_USER_PROFILE, userProfileBean);
        intent.setClass(activity, ManageOnlineAlertActivity.class);
        activity.startActivity(intent, options.toBundle());
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_manage_online_alert;
    }

    @Override
    public void onCreateView(View rootView) {
        getModulesCommonComponent().inject(this);

        this.userProfileBean = getIntent().getExtras().getParcelable(EXTRA_USER_PROFILE);
        mValueWhenStrArray = getResources().getStringArray(R.array.online_alert_array);

        cbxAlert.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                updateLayout(isChecked);
            }
        });

        initDialogs();
        ViewCompat.setTransitionName(cbxAlert, ELEMENT_ONLINE_ALERT);
    }

    /**
     * helper function to toogle checkbox
     */
    public void toogleCheckbox(View view) {
        cbxAlert.toggle();
    }

    @Override
    public void onViewReady() {

        setSupportActionBar(mToolbar);
        mToolbar.setActionbar(getSupportActionBar())
                .setDisplayHomeAsUpEnabled(true)
                .setButtonRightListener(this);

        String userName = userProfileBean.userName;
        String formatUsername = getString(R.string.fragment_manage_online_alert_text_user);
        txtUserName.setText(String.format(formatUsername, userName));

        cbxAlert.setChecked(userProfileBean.isAlert != ALERT_NO);

        updateLayout(cbxAlert.isChecked());

        ImagesUtils.loadRoundedAvatar(userProfileBean.avatar, userProfileBean.gender, imgAvatar);
        requestGetOnlineAlert();
    }

    @OnClick(R.id.activity_manage_online_alert_rlt_when)
    void showDialogWhen() {
        if (dialogWhen != null)
            dialogWhen.show();
    }

    private void initDialogs() {
        AlertDialog.Builder dialogMoreBuilder = new AlertDialog.Builder(
                ManageOnlineAlertActivity.this);
        dialogMoreBuilder.setItems(R.array.online_alert_array,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                            default:
                                valueWhen = Constants.MANAGE_ONLINE_EVERY_TIME;
                                break;
                            case 1:
                                valueWhen = Constants.MANAGE_ONLINE_MAX_TEN;
                                break;
                            case 2:
                                valueWhen = Constants.MANAGE_ONLINE_MAX_FIVE;
                                break;
                            case 3:
                                valueWhen = Constants.MANAGE_ONLINE_ONCE_PER_DAY;
                                break;
                        }
                        strWhen = mValueWhenStrArray[which];
                        txtWhen.setText(strWhen);
                        dialogWhen.dismiss();
                    }
                });
        dialogWhen = dialogMoreBuilder.create();
    }

    private void requestGetOnlineAlert() {
        String token = UserPreferences.getInstance().getToken();
        GetOnlineAlertRequest request = new GetOnlineAlertRequest(token,
                userProfileBean.userId);
        getPresenter().getOnlineAlert(request);
    }

    @Override
    public void onGetOnlineAlert(GetOnlineAlertResponse response) {
        boolean isChecked = response.data.isAlt != ALERT_NO;

        valueWhen = response.data.altNumber;
        cbxAlert.setChecked(isChecked);
        updateLayout(isChecked);

        switch (response.data.altNumber) {
            case Constants.MANAGE_ONLINE_EVERY_TIME:
                strWhen = mValueWhenStrArray[0];
                break;
            case Constants.MANAGE_ONLINE_MAX_TEN:
                strWhen = mValueWhenStrArray[1];
                break;
            case Constants.MANAGE_ONLINE_MAX_FIVE:
                strWhen = mValueWhenStrArray[2];
                break;
            case Constants.MANAGE_ONLINE_ONCE_PER_DAY:
                strWhen = mValueWhenStrArray[3];
                break;
            default:
                strWhen = "";
                break;
        }
        txtWhen.setText(strWhen);
    }

    void updateLayout(boolean isChecked) {
        rltLayout.setEnabled(isChecked);
        if (isChecked) {
            txtWhen.setTextColor(getResources().getColor(
                    R.color.black));
            txtWhenTitle.setTextColor(getResources().getColor(
                    R.color.black));
        } else {
            txtWhen.setTextColor(getResources().getColor(
                    R.color.hint_text_color));
            txtWhenTitle.setTextColor(getResources().getColor(
                    R.color.hint_text_color));
        }
    }

    @Override
    public void onAddOnlineAlert(AddOnlineAlertResponse response) {
        String title = getResources().getString(R.string.fragment_manage_online_alert_confirm_title);
        String strFormat = "";
        String message = "";
        if (!cbxAlert.isChecked()) {
            strFormat = getResources()
                    .getString(
                            R.string.fragment_manage_online_alert_confirm_content_never);
            message = String.format(strFormat, userProfileBean.userName);
        } else {
            String strResource = getResources()
                    .getString(
                            R.string.fragment_manage_online_alert_confirm_content_other);
            message = String.format(strResource, strWhen, userProfileBean.userName);
        }

        DialogMaterial.Builder builder = new DialogMaterial.Builder(context);
        builder.setStyle(Style.HEADER_WITH_TITLE)
                .setTitle(title)
                .setContent(message)
                .onNegative(R.string.common_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        finish();
                    }
                });
        builder.show();
    }

    @Override
    public void onToolbarButtonRightClick(View view) {
        String token = UserPreferences.getInstance().getToken();
        if (cbxAlert.isChecked()) {
            userProfileBean.isAlert = ALERT_YES;
        } else {
            userProfileBean.isAlert = ALERT_NO;
        }
        int num = valueWhen;
        AddOnlineAlertRequest onlineAlertRequest = new AddOnlineAlertRequest(
                token, userProfileBean.userId, userProfileBean.isAlert, num);
        getPresenter().addOnlineAlert(onlineAlertRequest);
    }
}
