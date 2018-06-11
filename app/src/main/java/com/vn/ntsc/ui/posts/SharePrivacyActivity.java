package com.vn.ntsc.ui.posts;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.vn.ntsc.R;
import com.vn.ntsc.core.views.BaseActivity;
import com.vn.ntsc.repository.ActivityResultRequestCode;
import com.vn.ntsc.utils.Constants;
import com.vn.ntsc.utils.keyboard.KeyboardUtils;
import com.vn.ntsc.widget.toolbar.ToolbarButtonLeftClickListener;
import com.vn.ntsc.widget.toolbar.ToolbarButtonRightClickListener;
import com.vn.ntsc.widget.toolbar.ToolbarTitleCenter;

import butterknife.BindView;
import butterknife.OnClick;


/**
 * Created by Robert on 2017 Sep 25.
 */
public class SharePrivacyActivity extends BaseActivity implements ToolbarButtonLeftClickListener, ToolbarButtonRightClickListener {

    private final String TAG = SharePrivacyActivity.class.getSimpleName();
    private static final String RESULT_KEY = "selected_privacy_result";

    private int privacy = -1;//-1 is default not check; 0 <=> public; 1 <=> friend

    @BindView(R.id.toolbar)
    ToolbarTitleCenter mToolbar;
    @BindView(R.id.activity_share_privacy_group)
    RadioGroup mPrivacyGroup;
    @BindView(R.id.activity_share_privacy_public_option)
    RadioButton mPublicPrivacyOption;
    @BindView(R.id.activity_share_privacy_friend_option)
    RadioButton mFriendPrivacyOption;
    @BindView(R.id.activity_share_privacy_only_me_option)
    RadioButton mOnlyMePrivacyOption;

    public static void startActivityForResult(Activity mBaseActivity, String resultKey, int privacy, @ActivityResultRequestCode int requestCode) {

        Intent intent = new Intent(mBaseActivity, SharePrivacyActivity.class);
        intent.putExtra(RESULT_KEY, resultKey);
        intent.putExtra(resultKey, privacy);
        mBaseActivity.startActivityForResult(intent, requestCode);

    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_share_privacy;
    }

    @Override
    public void onCreateView(View rootView) {

        setSupportActionBar(mToolbar);
        ActionBar actionbar = getSupportActionBar();
        assert actionbar != null;
        actionbar.setDisplayShowTitleEnabled(false);
        actionbar.setDisplayShowHomeEnabled(false);
        //Hide soft keyboard if need
        KeyboardUtils.hideSoftKeyboard(this);
    }


    @Override
    public void onViewReady() {

        setSupportActionBar(mToolbar);
        mToolbar.setActionbar(getSupportActionBar())
                .setButtonLeftListener(this)
                .setButtonRightListener(this);

        if (getIntent() != null) {
            privacy = getIntent().getIntExtra(getIntent().getStringExtra(RESULT_KEY), -1);
        }
        mPrivacyGroup.check(privacy);

        if (privacy == Constants.PRIVACY_PUBLIC) {//Public privacy option Selected
            mPublicPrivacyOption.setChecked(true);
            mFriendPrivacyOption.setChecked(false);
            mOnlyMePrivacyOption.setChecked(false);
        } else if (privacy == Constants.PRIVACY_FRIENDS) {//Friend privacy option Selected
            mPublicPrivacyOption.setChecked(false);
            mOnlyMePrivacyOption.setChecked(false);
            mFriendPrivacyOption.setChecked(true);
        } else if (privacy == Constants.PRIVACY_PRIVATE) {//Only me privacy option Selected
            mPublicPrivacyOption.setChecked(false);
            mFriendPrivacyOption.setChecked(false);
            mOnlyMePrivacyOption.setChecked(true);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 1. When user click to ic_share privacy public option to ic_share all everyone
     * 2. When user click to ic_share privacy friend option to ic_share all everyone
     * 3. When user click to ic_share privacy only me option to ic_share all everyone
     */
    @OnClick({R.id.activity_share_privacy_public_option, R.id.activity_share_privacy_friend_option, R.id.activity_share_privacy_only_me_option})
    void onChangePrivacy(CompoundButton button) {
        switch (button.getId()) {
            case R.id.activity_share_privacy_public_option:
                privacy = Constants.PRIVACY_PUBLIC;
                break;
            case R.id.activity_share_privacy_friend_option:
                privacy = Constants.PRIVACY_FRIENDS;
                break;
            case R.id.activity_share_privacy_only_me_option:
                privacy = Constants.PRIVACY_PRIVATE;
                break;
            default:
                break;
        }
    }

    @Override
    public void onToolbarButtonLeftClick(View view) {
        finish();
    }

    @Override
    public void onToolbarButtonRightClick(View view) {
        //Set privacy option result to PostStatusActivity
        Intent returnIntent = getIntent();
        returnIntent.putExtra(returnIntent.getStringExtra(RESULT_KEY), privacy);
        setResult(RESULT_OK, returnIntent);
        finish();
    }
}
