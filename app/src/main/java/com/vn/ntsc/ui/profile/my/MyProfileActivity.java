package com.vn.ntsc.ui.profile.my;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Handler;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.nankai.designlayout.dialog.DialogMaterial;
import com.nankai.designlayout.dialog.enums.Style;
import com.vn.ntsc.R;
import com.vn.ntsc.core.views.BaseActivity;
import com.vn.ntsc.repository.ActivityResultRequestCode;
import com.vn.ntsc.repository.TypeView;
import com.vn.ntsc.repository.model.block.addblock.AddBlockUserRequest;
import com.vn.ntsc.repository.model.favorite.AddFavoriteRequest;
import com.vn.ntsc.repository.model.favorite.AddFavoriteResponse;
import com.vn.ntsc.repository.model.favorite.RemoveFavoriteRequest;
import com.vn.ntsc.repository.model.favorite.RemoveFavoriteResponse;
import com.vn.ntsc.repository.model.report.ReportRequest;
import com.vn.ntsc.repository.model.timeline.datas.BuzzBean;
import com.vn.ntsc.repository.model.user.UserInfoRequest;
import com.vn.ntsc.repository.model.user.UserInfoResponse;
import com.vn.ntsc.repository.preferece.UserPreferences;
import com.vn.ntsc.ui.gift.GiftActivity;
import com.vn.ntsc.ui.main.MainActivity;
import com.vn.ntsc.ui.onlinealert.ManageOnlineAlertActivity;
import com.vn.ntsc.ui.timeline.core.TimelineFragment;
import com.vn.ntsc.ui.timeline.user.TimelineProfileFragment;
import com.vn.ntsc.utils.Constants;
import com.vn.ntsc.utils.LogUtils;
import com.vn.ntsc.widget.eventbus.RxEventBus;
import com.vn.ntsc.widget.eventbus.SubjectCode;
import com.vn.ntsc.widget.toolbar.ToolbarButtonRightClickListener;
import com.vn.ntsc.widget.toolbar.ToolbarTitleCenter;
import com.vn.ntsc.widget.views.popup.mymore.MyMoreLayout;
import com.vn.ntsc.widget.views.popup.mymore.OnMoreListener;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnTouch;
import io.reactivex.functions.Consumer;

/**
 * Created by nankai on 8/29/2017.
 */

public class MyProfileActivity extends BaseActivity<MyProfilePresenter> implements MyProfileContract.View, ToolbarButtonRightClickListener {

    private static final String TAG = MyProfileActivity.class.getSimpleName();

    //----------------------------------------------------------------
    //------------------------ Variable ------------------------------
    //----------------------------------------------------------------
    private static final String EXTRAS_PROFILE_COME_FROM = "EXTRAS_PROFILE_COME_FROM";
    private static final String ELEMENT_PROFILE = "profile_element";
    private static final String ELEMENT_TIMELINE = "extra.timeline";
    private static final String EXTRA_USER = "extra.user";
    private static final String EXTRA_USER_ID = "extra.user.id";

    private BuzzBean buzzDataBean;

    /*Check this ProfileDetailActivity is started from any activity*/
    public int mComeFromType;

    private PopupWindow mPopupWindow;
    private MyMoreLayout mMyMoreLayout;

    @BindView(R.id.toolbar)
    ToolbarTitleCenter mToolbar;
    @BindView(R.id.activity_my_profile_freezed)
    View mViewFreezeLayer;
    @BindView(R.id.activity_my_profile_content)
    FrameLayout content;

    @Inject
    UserInfoResponse userInfo;

    //----------------------------------------------------------------
    //-------------------------- launch ------------------------------
    //----------------------------------------------------------------
    public static void launch(AppCompatActivity activity, View view, BuzzBean buzzDataBean, @ActivityResultRequestCode int requestCode,
                              @TypeView.ProfileType int fromActivity) {
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, view, ELEMENT_PROFILE);
        Intent intent = new Intent();
        intent.setClass(activity, MyProfileActivity.class);
        intent.putExtra(ELEMENT_TIMELINE, buzzDataBean);
        intent.putExtra(EXTRAS_PROFILE_COME_FROM, fromActivity);
        activity.startActivityForResult(intent, requestCode, options.toBundle());
    }

    public static void launch(AppCompatActivity activity, View view, UserInfoResponse userProfileBean, @TypeView.ProfileType int fromActivity) {
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, view, ELEMENT_PROFILE);
        Intent intent = new Intent();
        intent.setClass(activity, MyProfileActivity.class);
        intent.putExtra(EXTRA_USER, userProfileBean);
        intent.putExtra(EXTRAS_PROFILE_COME_FROM, fromActivity);
        activity.startActivity(intent, options.toBundle());
    }

    public static void launch(AppCompatActivity activity, UserInfoResponse userProfileBean,
                              @TypeView.ProfileType int fromActivity) {
        Intent intent = new Intent();
        intent.setClass(activity, MyProfileActivity.class);
        intent.putExtra(EXTRA_USER, userProfileBean);
        intent.putExtra(EXTRAS_PROFILE_COME_FROM, fromActivity);
        activity.startActivity(intent);
    }

    public static void launch(AppCompatActivity activity, View view, String userId, @TypeView.ProfileType int fromActivity) {
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, view, ELEMENT_PROFILE);
        Intent intent = new Intent();
        intent.setClass(activity, MyProfileActivity.class);
        intent.putExtra(EXTRA_USER_ID, userId);
        intent.putExtra(EXTRAS_PROFILE_COME_FROM, fromActivity);
        activity.startActivity(intent, options.toBundle());
    }

    //----------------------------------------------------------------
    //------------------------ View  ---------------------------------
    //----------------------------------------------------------------
    @Override
    public int getLayoutId() {
        return R.layout.activity_my_profile;
    }

    //----------------------------------------------------------------
    //------------------------ life cycle ----------------------------
    //----------------------------------------------------------------
    @Override
    public void onCreateView(View rootView) {
        getTimelineComponent().inject(this);

        if (getIntent().hasExtra(EXTRAS_PROFILE_COME_FROM)) {
            mComeFromType = getIntent().getIntExtra(EXTRAS_PROFILE_COME_FROM, -1);

            if (mComeFromType == TypeView.ProfileType.COME_FROM_TIMELINE) {
                buzzDataBean = getIntent().getParcelableExtra(ELEMENT_TIMELINE);
                if (null == buzzDataBean)
                    finish();

                String userId = buzzDataBean.userId;
                int gender = buzzDataBean.gender;
                String userName = buzzDataBean.userName;
                String avatar = buzzDataBean.avatar;
                int isFavorite = buzzDataBean.isFavorite;

                userInfo.setData(userId, gender, userName, avatar, isFavorite);
                mToolbar.setTitleCenter(userInfo.userName);

            } else if (mComeFromType == TypeView.ProfileType.COME_FROM_NOTI) {
                userInfo.setData((UserInfoResponse) getIntent().getParcelableExtra(EXTRA_USER));
            } else if (mComeFromType == TypeView.ProfileType.COME_FROM_OTHER) {
                userInfo.setData((UserInfoResponse) getIntent().getParcelableExtra(EXTRA_USER));
            } else {
                userInfo.userId = getIntent().getStringExtra(EXTRA_USER_ID);
            }
        } else {
            finish();
        }

        if (userInfo.userId.equals(UserPreferences.getInstance().getUserId())) {
            mToolbar.setVisibilityButtonRight(false);
        } else {
            iniMore();
        }
    }

    @Override
    public void onViewReady() {
        setSupportActionBar(mToolbar);
        mToolbar.setActionbar(getSupportActionBar())
                .setDisplayHomeAsUpEnabled(true)
                .setButtonRightListener(this);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.activity_my_profile_content, TimelineProfileFragment.newInstance(TypeView.TypeViewTimeline.TIMELINE_USER)).addToBackStack(null).commit();

        setObserverEventBus();
        //get yourself info
        if (!UserPreferences.getInstance().getToken().equals(""))
            getPresenter().getUserInfo(new UserInfoRequest(UserPreferences.getInstance().getToken(), userInfo.userId), 1);
        else //Get friend info
            getPresenter().getUserInfo(new UserInfoRequest(userInfo.userId), 1);

        ViewCompat.setTransitionName(content, ELEMENT_PROFILE);
    }

    //----------------------------------------------------------------
    //-------------------------- Listener ----------------------------
    //----------------------------------------------------------------
    @OnTouch(R.id.activity_my_profile_freezed)
    public boolean invisibleMoreLayout() {
        hideChatMoreOptions();
        return true;
    }


    @Override
    public void onBackPressed() {
        if (mComeFromType == TypeView.ProfileType.COME_FROM_TIMELINE) {
            buzzDataBean.isFavorite = userInfo.isFavorite;
            returnBackDataForTimeline(buzzDataBean, TimelineFragment.RETURN_UPDATE);
        } else {
            finish();
        }
    }

    //----------------------------------------------------------------
    //----------------------- onMoreListener -------------------------
    //----------------------------------------------------------------
    private OnMoreListener onMoreListener = new OnMoreListener() {
        @Override
        public void onSendGift(UserInfoResponse userProfileBean) {
            LogUtils.d(TAG, "onSendGift -> " + userProfileBean.userId);
            GiftActivity.launch(MyProfileActivity.this, userProfileBean, mToolbar, ActivityResultRequestCode.REQUEST_START_SEND_GIFT_FROM_PROFILE);
            hideChatMoreOptions();
        }

        @Override
        public void onFavorite(UserInfoResponse userProfileBean) {
            LogUtils.d(TAG, "onFavorite -> " + userProfileBean.userId);
            hideChatMoreOptions();
            //Send request favorite for TimeLineFragment
            if (userProfileBean.isFavorite == Constants.BUZZ_TYPE_IS_NOT_FAVORITE)
                getPresenter().setFavorite(new AddFavoriteRequest(UserPreferences.getInstance().getToken(), userProfileBean.userId), userProfileBean.userId);
            else
                getPresenter().setUnFavorite(new RemoveFavoriteRequest(UserPreferences.getInstance().getToken(), userProfileBean.userId), userProfileBean.userId);
        }

        @Override
        public void onBlock(UserInfoResponse userProfileBean) {
            LogUtils.d(TAG, "onBlock -> " + userProfileBean.userId);
            executeBlockUser();
            hideChatMoreOptions();
        }

        @Override
        public void onReport(UserInfoResponse userProfileBean) {
            LogUtils.d(TAG, "onReport -> " + userProfileBean.userId);
            hideChatMoreOptions();
            executeReportUser();
        }

        @Override
        public void onAlertOnline(UserInfoResponse userProfileBean) {
            LogUtils.d(TAG, "onAlertOnline -> " + userProfileBean.userId);
            hideChatMoreOptions();
            ManageOnlineAlertActivity.launch(MyProfileActivity.this,
                    ((TimelineProfileFragment) getSupportFragmentManager().findFragmentById(R.id.activity_my_profile_content))
                            .getHeaderProfile()
                            .getAvatarView(), userProfileBean
            );
        }
    };

    //----------------------------------------------------------------
    //------------------------ Function ------------------------------
    //----------------------------------------------------------------

    private void executeReportUser() {
        Resources resource = getResources();
        final int[] position = new int[1];

        String[] items = null;
        items = resource.getStringArray(R.array.report_user_type);
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.layout_item_single_choice, items);
        View reportView = LayoutInflater.from(context).inflate(R.layout.layout_report_user, null);
        ListView listView = reportView.findViewById(R.id.listView);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.setAdapter(adapter);

        int defaultSelected = 0, noreId = 0;
        listView.setItemChecked(defaultSelected, true);
        listView.performItemClick(listView.getSelectedView(), defaultSelected, noreId);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                position[0] = i;
            }
        });

        DialogMaterial.Builder builder = new DialogMaterial.Builder(context);
        final String[] finalItems = items;

        builder.setStyle(Style.HEADER_WITH_TITLE)
                .setTitle(R.string.dialog_confirm_report_user_title)
                .setCustomView(reportView)
                .onNegative(R.string.common_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        //TODO #12257 - Not reporting user when choose 'Không báo cáo user này' option
                        if (position[0] == 0) return;//Not reporting user option

                        LogUtils.e(TAG, finalItems[position[0]]);

                        String token = UserPreferences.getInstance().getToken();
                        ReportRequest reportRequest = new ReportRequest(token, userInfo.userId, position[0], Constants.REPORT_TYPE_USER);
                        getPresenter().reportUser(reportRequest);
                    }
                });
        builder.setButtonTextColor(getResources().getColor(R.color.colorPrimary));
        builder.show();
    }

    private void executeBlockUser() {
        DialogMaterial.Builder builder = new DialogMaterial.Builder(context);
        builder.setStyle(Style.HEADER_WITH_TITLE)
                .setTitle(R.string.dialog_confirm_block_user_title)
                .setContent(R.string.dialog_confirm_block_user_content)
                .onPositive(R.string.common_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        AddBlockUserRequest addBlockUserRequest = new AddBlockUserRequest(UserPreferences.getInstance().getToken(), userInfo.userId);
                        getPresenter().blockUser(addBlockUserRequest);
                        dialogInterface.dismiss();
                    }
                })
                .onNegative(R.string.common_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
        builder.show();
    }

    private void showChatMoreOptions() {
        iniMore();
        mPopupWindow.showAsDropDown(mToolbar);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                mViewFreezeLayer.setVisibility(View.VISIBLE);
            }
        };
        Handler handler = new Handler();
        handler.post(runnable);
    }

    private void iniMore() {
        mMyMoreLayout = new MyMoreLayout(getApplicationContext(), onMoreListener, userInfo, TypeView.MyMoreLayout.TYPE_INFO);
        mMyMoreLayout.setFavorite(userInfo.isFavorite);
        mPopupWindow = new PopupWindow(mMyMoreLayout,
                android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT, false);
    }


    public void hideChatMoreOptions() {
        if (mPopupWindow != null && mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
            mViewFreezeLayer.setVisibility(View.GONE);
        }
    }

    private void returnBackDataForTimeline(BuzzBean buzzDataBean, int eventReturn) {
        Intent data = new Intent();
        data.putExtra(TimelineFragment.KEY_BUZZ_RETURN, buzzDataBean);
        data.putExtra(TimelineFragment.KEY_EVENT_RETURN, eventReturn);
        setResult(Activity.RESULT_OK, data);
        finish();
    }

    //----------------------------------------------------------
    //----------------------Server event -----------------------
    //----------------------------------------------------------

    @Override
    public void onFavoriteResponse(AddFavoriteResponse response, String userId) {
        RxEventBus.publish(SubjectCode.SUBJECT_UPDATE_TIMELINE_FAVORITE, userId);
    }

    @Override
    public void onUnFavoriteResponse(RemoveFavoriteResponse response, String userId) {
        RxEventBus.publish(SubjectCode.SUBJECT_UPDATE_TIMELINE_UNFAVORITE, userId);
    }

    @Override
    public void onUserInfo(UserInfoResponse userInfoResponse) {

        this.userInfo.setData(userInfoResponse);
        if (mMyMoreLayout != null)
            mMyMoreLayout.setFavorite(userInfo.isFavorite);

        TimelineProfileFragment fragment = (TimelineProfileFragment) getSupportFragmentManager().findFragmentById(R.id.activity_my_profile_content);
        if (null != fragment) {
            fragment.initHelperTimelineUser()
                    .updateUserInfo()
                    .requestListPublicImage();
        }
        mToolbar.setTitleCenter(userInfo.userName);
    }

    @Override
    public void onReportUser() {
        DialogMaterial.Builder builder = new DialogMaterial.Builder(context);
        builder.setStyle(Style.HEADER_WITH_TITLE)
                .setTitle(R.string.dialog_confirm_report_user_title)
                .setContent(R.string.dialog_confirm_report_user_content)
                .onNegative(R.string.common_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
        builder.setButtonTextColor(getResources().getColor(R.color.colorPrimary));
        builder.show();
    }

    @Override
    public void onAddBlockUser() {
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();
    }

    //----------------------------------------------------------------
    //------------------- subject event bus --------------------------
    //----------------------------------------------------------------
    private void setObserverEventBus() {
        RxEventBus.subscribe(SubjectCode.SUBJECT_UPDATE_TIMELINE_FAVORITE, this, new Consumer<Object>() {

            @Override
            public void accept(Object userId) throws Exception {
                if (userId != null) {
                    if (userInfo != null)
                        if (userInfo.userId.equals(userId.toString())) {
                            userInfo.isFavorite = Constants.BUZZ_TYPE_IS_FAVORITE;
                            userInfo.isFavorite = Constants.BUZZ_TYPE_IS_FAVORITE;
                            mMyMoreLayout.setFavorite(userInfo.isFavorite);
                        }
                }
            }
        });

        RxEventBus.subscribe(SubjectCode.SUBJECT_UPDATE_TIMELINE_UNFAVORITE, this, new Consumer<Object>() {

            @Override
            public void accept(Object userId) throws Exception {
                if (userId != null) {
                    if (userInfo != null)
                        if (userInfo.userId.equals(userId.toString())) {
                            userInfo.isFavorite = Constants.BUZZ_TYPE_IS_NOT_FAVORITE;
                            userInfo.isFavorite = Constants.BUZZ_TYPE_IS_NOT_FAVORITE;
                            mMyMoreLayout.setFavorite(userInfo.isFavorite);
                        }
                }
            }
        });

    }

    @Override
    public void onToolbarButtonRightClick(View view) {
        if (mPopupWindow != null && mPopupWindow.isShowing()) {
            hideChatMoreOptions();
        } else {
            showChatMoreOptions();
        }
    }
}
