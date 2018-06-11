package com.vn.ntsc.ui.notices.online;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.vn.ntsc.R;
import com.vn.ntsc.core.views.BaseActivity;
import com.vn.ntsc.repository.TypeView;
import com.vn.ntsc.repository.model.notification.OnlineNotificationItem;
import com.vn.ntsc.repository.model.notification.OnlineNotificationRequest;
import com.vn.ntsc.repository.model.notification.OnlineNotificationResponse;
import com.vn.ntsc.repository.model.user.UserInfoResponse;
import com.vn.ntsc.repository.preferece.UserPreferences;
import com.vn.ntsc.ui.onlinealert.ManageOnlineAlertActivity;
import com.vn.ntsc.ui.profile.my.MyProfileActivity;
import com.vn.ntsc.widget.toolbar.ToolbarTitleCenter;

import butterknife.BindView;

/**
 * Created by ThoNh on 9/1/2017.
 */

public class NotificationOnlineActivity extends BaseActivity<NotificationOnlinePresenter> implements NotificationOnlineContract.View, NotificationOnlineAdapter.OnlineNotificationEvent, SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.activity_online_notification_swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @BindView(R.id.toolbar)
    ToolbarTitleCenter mToolbar;

    @BindView(R.id.activity_online_notification_list_online_notification)
    RecyclerView mListOnlineNotification;

    private NotificationOnlineAdapter mAdapter;


    public static void launch(AppCompatActivity appCompatActivity) {
        appCompatActivity.startActivity(new Intent(appCompatActivity, NotificationOnlineActivity.class));
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_online_notification;
    }

    @Override
    public void onCreateView(View rootView) {
        getModulesCommonComponent().inject(this);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mListOnlineNotification.setLayoutManager(mLayoutManager);
        mAdapter = new NotificationOnlineAdapter(this);
        mListOnlineNotification.setAdapter(mAdapter);

        mSwipeRefreshLayout.setOnRefreshListener(this);
    }

    @Override
    public void onViewReady() {
        setSupportActionBar(mToolbar);
        mToolbar.setActionbar(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        mSwipeRefreshLayout.setRefreshing(true);

        OnlineNotificationRequest request = new OnlineNotificationRequest(UserPreferences.getInstance().getToken());
        getPresenter().getOnlineNotifications(request);
    }

    @Override
    public void onRefresh() {
        mAdapter.getData().clear();
        OnlineNotificationRequest request = new OnlineNotificationRequest(UserPreferences.getInstance().getToken());
        getPresenter().getOnlineNotifications(request);
    }

    @Override
    public void onItemActionClick(View view, OnlineNotificationItem item, int position) {
        UserInfoResponse userProfileBean = new UserInfoResponse(item.userId, item.gender, item.userName, item.avaId, item.isFav);
        ManageOnlineAlertActivity.launch(this, view, userProfileBean);
    }

    @Override
    public void onItemClick(View view, OnlineNotificationItem item, int position) {
        UserInfoResponse userProfileBean = new UserInfoResponse(item.userId, item.gender, item.userName, item.avaId, item.isFav);
        MyProfileActivity.launch(this, view, userProfileBean, TypeView.ProfileType.COME_FROM_OTHER);
    }

    //========================================== Server response ===================================

    @Override
    public void getOnlineNotificationSuccess(OnlineNotificationResponse response) {
        mAdapter.setNewData(response.mData);
        UserPreferences.getInstance().saveNotifyOnlineAlarmNum(0);
    }

    @Override
    public void getOnlineNotificationFailure() {
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void loadComplete() {
        mSwipeRefreshLayout.setRefreshing(false);
        if (mAdapter.getData().isEmpty()) {
            mAdapter.setEmptyView(R.layout.layout_empty);
        }
    }

    //=============================================== End ==========================================

}
