package com.vn.ntsc.ui.main;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tux.mylab.MediaPickerBaseActivity;
import com.example.tux.mylab.gallery.Gallery;
import com.example.tux.mylab.gallery.data.MediaFile;
import com.google.firebase.iid.FirebaseInstanceId;
import com.nankai.designlayout.bottomnavigation.BottomNavigation;
import com.nankai.designlayout.bottomnavigation.BottomNavigationAdapter;
import com.nankai.designlayout.bottomnavigation.BottomNavigationViewPager;
import com.nankai.designlayout.bottomnavigation.FragmentEmpty;
import com.vn.ntsc.BuildConfig;
import com.vn.ntsc.R;
import com.vn.ntsc.app.AppController;
import com.vn.ntsc.core.model.ServerResponse;
import com.vn.ntsc.core.views.BaseActivity;
import com.vn.ntsc.repository.ActivityResultRequestCode;
import com.vn.ntsc.repository.TypeView;
import com.vn.ntsc.repository.model.logout.LogoutRequest;
import com.vn.ntsc.repository.model.notification.ReadAllNotificationRequest;
import com.vn.ntsc.repository.model.notification.push.NotificationAps;
import com.vn.ntsc.repository.model.notification.push.NotificationType;
import com.vn.ntsc.repository.model.search.MeetPeopleBean;
import com.vn.ntsc.repository.model.timeline.BuzzListResponse;
import com.vn.ntsc.repository.model.user.GetUserInfoResponse;
import com.vn.ntsc.repository.model.user.UserInfoRequest;
import com.vn.ntsc.repository.model.user.UserInfoResponse;
import com.vn.ntsc.repository.model.webview.WebViewBean;
import com.vn.ntsc.repository.preferece.UserPreferences;
import com.vn.ntsc.services.StickerAndGiftDownloadService;
import com.vn.ntsc.services.UserLiveStreamService;
import com.vn.ntsc.services.fcm.MyFirebaseMessagingService;
import com.vn.ntsc.services.uploadFileChat.PostStatusService;
import com.vn.ntsc.ui.accountsetting.AccountSettingActivity;
import com.vn.ntsc.ui.blocklst.BlockListActivity;
import com.vn.ntsc.ui.chat.ChatActivity;
import com.vn.ntsc.ui.comments.CommentActivity;
import com.vn.ntsc.ui.conversation.ConversationFragment;
import com.vn.ntsc.ui.friends.favorite.FavoritePageFragment;
import com.vn.ntsc.ui.livestream.LiveStreamActivity;
import com.vn.ntsc.ui.login.LoginActivity;
import com.vn.ntsc.ui.notices.notification.NotificationFragment;
import com.vn.ntsc.ui.notices.online.NotificationOnlineActivity;
import com.vn.ntsc.ui.posts.PostStatusActivity;
import com.vn.ntsc.ui.profile.my.MyProfileActivity;
import com.vn.ntsc.ui.search.SearchSettingActivity;
import com.vn.ntsc.ui.setting.notification.NotificationSettingActivity;
import com.vn.ntsc.ui.signup.SignUpActivity;
import com.vn.ntsc.ui.splash.SplashActivity;
import com.vn.ntsc.ui.timeline.TimeLinePageFragment;
import com.vn.ntsc.ui.webview.WebViewActivity;
import com.vn.ntsc.utils.AuthenticationUtil;
import com.vn.ntsc.utils.ImagesUtils;
import com.vn.ntsc.utils.LogUtils;
import com.vn.ntsc.utils.SystemUtils;
import com.vn.ntsc.utils.Utils;
import com.vn.ntsc.utils.cache.CacheJson;
import com.vn.ntsc.utils.cache.CacheType;
import com.vn.ntsc.widget.permissions.Permission;
import com.vn.ntsc.widget.toolbar.ToolbarTitleCenter;
import com.vn.ntsc.widget.views.images.CircleImageView;
import com.vn.ntsc.widget.views.images.ScalingImageView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnPageChange;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

import static com.vn.ntsc.services.fcm.MyFirebaseMessagingService.EXTRA_NOTIFICATION_MESSAGE;

/**
 * Created by nankai on 8/3/2017.
 */

public class MainActivity extends BaseActivity<MainPresenter> implements MainContract.View, NavigationView.OnNavigationItemSelectedListener {

    private static final String BUZZ_ID = "buzz_id";
    private final String TAG = MainActivity.class.getSimpleName();

    public static final String BUNDLE_MEET_PEOPLE_SEARCH = "BUNDLE_MEET_PEOPLE_SEARCH";
    public static final String EXTRA_SCHEME = "extra.scheme";

    private final int MENU_TOP = 0;
    private final int MENU_FRIENDS = 1;
    private final int MENU_STREAM = 2;
    private final int MENU_MESSAGE = 3;
    private final int MENU_PROFILE = 4;
    private static final int REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE = 0x00;

    @BindView(R.id.toolbar)
    ToolbarTitleCenter mToolbar;

    @BindView(R.id.activity_main_bottom_navigation)
    BottomNavigation bottomNavigationView;
    @BindView(R.id.activity_main_content)
    BottomNavigationViewPager viewPager;

    @BindView(R.id.activity_main_action_fab)
    FloatingActionButton fab;

    @BindView(R.id.activity_main_drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.activity_main_nav_view)
    NavigationView navigationView;

    private CircleImageView nvAvatar;
    private ScalingImageView navBanner;
    private TextView nvUserName;
    private TextView nvEmail;
    private TextView nvLogin;
    private TextView nvRegister;
    private TextView mBadgeOnlineNotification;
    private View headerLayout;

    CompositeDisposable disposables;

    protected Subject<UserInfoResponse> userInfoSubject = PublishSubject.create();

    public static void launch(AppCompatActivity activity, String buzzId) {
        Intent intent = new Intent(activity, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(EXTRA_SCHEME, buzzId);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        activity.finish();
    }

    public static void launch(AppCompatActivity activity) {
        Intent intent = new Intent(activity, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        activity.finish();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void onCreateView(View rootView) {

        //register
        getModulesCommonComponent().inject(this);

        disposables = new CompositeDisposable();

        CacheJson.deleteFileOlderWeek();

        //DrawerLayout
        setSupportActionBar(mToolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, mToolbar, R.string.open_drawer, R.string.close_drawer);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //Navigation left
        headerLayout = navigationView.getHeaderView(0);
        nvAvatar = headerLayout.findViewById(R.id.nv_header_main_avatar);
        navBanner = headerLayout.findViewById(R.id.nv_header_main_banner);
        nvUserName = headerLayout.findViewById(R.id.nv_header_main_user_name);
        nvEmail = headerLayout.findViewById(R.id.nv_header_main_email);
        nvLogin = headerLayout.findViewById(R.id.nv_header_main_login);
        nvRegister = headerLayout.findViewById(R.id.nv_header_main_register);

        nvAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Utils.isEmptyOrNull(UserPreferences.getInstance().getToken())) {
                    onShowDialogLogin(ServerResponse.DefinitionCode.SERVER_EXPIRED_TOKEN);
                } else {
                    UserPreferences userPreferences = UserPreferences.getInstance();
                    String userID = userPreferences.getUserId();
                    int gender = userPreferences.getGender();
                    String userName = userPreferences.getUserName();
                    String avatar = userPreferences.getAva();
                    UserInfoResponse userProfileBean = new UserInfoResponse(userID, gender, userName, avatar);
                    MyProfileActivity.launch(MainActivity.this, view, userProfileBean, TypeView.ProfileType.COME_FROM_OTHER);
                }
            }
        });
        navigationView.setNavigationItemSelectedListener(this);

        //subscribe
        setObservables();
    }

    @Override
    public void onViewReady() {

        initBottomNavigation();
        initBadgeOnlineNotification();

        if (BuildConfig.FLAVOR.equals("dev") || BuildConfig.FLAVOR.equals("preview")) {
            navigationView.getMenu().findItem(R.id.menu_main_drawer_version_app).setVisible(true);
            navigationView.getMenu().findItem(R.id.menu_main_drawer_version_app).setTitle("Version : " + BuildConfig.VERSION_NAME + "-" + BuildConfig.VERSION_CODE);
        } else {
            navigationView.getMenu().findItem(R.id.menu_main_drawer_version_app).setVisible(false);
        }

        requestAccessPermission(REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        //Notification
        int notifyType = getIntent().getIntExtra(MyFirebaseMessagingService.EXTRA_NOTIFICATION_TYPE_NOTY_FCM, -1); //action  search
        if (notifyType != -1) {
            initDirectionActivity(notifyType);
        }
    }

    /**
     * Init view for Badge of Notification Online in menu left
     */
    private void initBadgeOnlineNotification() {
        View viewGroupBadge = MenuItemCompat.getActionView(navigationView.getMenu().findItem(R.id.menu_main_drawer_online_alarm));
        if (viewGroupBadge != null) {
            mBadgeOnlineNotification = viewGroupBadge.findViewById(R.id.tv_badge_online_notification);
        }
    }

    @Override
    public void onResume(View viewRoot) {
        super.onResume(viewRoot);
        updateUserName();
        updateBadge();
    }

    private void updateUserName() {
        if (UserPreferences.getInstance().getToken().equals("")) {
            headerLayout.findViewById(R.id.nv_header_main_layout_view_detail).setVisibility(View.GONE);
            headerLayout.findViewById(R.id.nv_header_main_layout_action_signup).setVisibility(View.VISIBLE);
            navigationView.getMenu().findItem(R.id.menu_main_drawer_logout).setVisible(false);

            nvLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LoginActivity.launch(MainActivity.this, view);
                }
            });
            nvRegister.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SignUpActivity.launch(MainActivity.this, view);
                }
            });
        } else {
            initUserView();

            headerLayout.findViewById(R.id.nv_header_main_layout_view_detail).setVisibility(View.VISIBLE);
            headerLayout.findViewById(R.id.nv_header_main_layout_action_signup).setVisibility(View.GONE);
            navigationView.getMenu().findItem(R.id.menu_main_drawer_logout).setVisible(true);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        disposables.dispose();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_activity, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_main_activity_action_search:
                SearchSettingActivity.launch(this, ActivityResultRequestCode.REQUEST_CODE_SEARCH);
                break;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == ActivityResultRequestCode.REQUEST_POST_STATUS) { // start service upload status
                Intent intents = new Intent(MainActivity.this, PostStatusService.class);
                intents.putExtras(data);
                MainActivity.this.startService(intents);
            } else if (requestCode == ActivityResultRequestCode.REQUEST_CODE_SEARCH) {
                List<MeetPeopleBean> dataFromSearch = data.getParcelableArrayListExtra(BUNDLE_MEET_PEOPLE_SEARCH);
                Log.e(TAG, "size: " + dataFromSearch.size());
            } else if (requestCode == Gallery.REQUEST_CODE_GALLERY) {
                Parcelable[] output = data.getParcelableArrayExtra(MediaPickerBaseActivity.RESULT_KEY);
                // only 1 image need to set avatar
                MediaFile selectedAvatar = (MediaFile) output[0];
                String path = selectedAvatar.getPath();
                LogUtils.i(TAG, "Live Stream file path: " + path);
                LiveStreamActivity.launch(MainActivity.this, UserLiveStreamService.Mode.CHAT, path, UserLiveStreamService.TypeView.STREAM_FILE);
            }
        }
    }

    void initUserView() {
        UserPreferences userPreferences = UserPreferences.getInstance();
        String userName = userPreferences.getUserName();
        String email = userPreferences.getRegEmail();
        String avaUrl = userPreferences.getAva();
        LogUtils.i(TAG, "init menu left -> \nUserName: " + userName + "\nEmail: " + email + "\nAvatar Url: " + avaUrl);

        ImagesUtils.loadRoundedAvatar(avaUrl, UserPreferences.getInstance().getGender(), nvAvatar);
        ImagesUtils.loadBlurImageBanner(getBaseContext(), AppController.SCREEN_WIDTH, getResources().getDimensionPixelSize(R.dimen.nav_header_height), userPreferences.getAva(), navBanner);

        nvUserName.setText(userName);
        nvEmail.setText(email);
    }

    private void initBottomNavigation() {
        int[] tabColors = getApplicationContext().getResources().getIntArray(R.array.tab_colors);
        BottomNavigationAdapter navigationAdapter = new BottomNavigationAdapter(this, R.menu.menu_bottom_navigation_main);
        navigationAdapter.setupWithBottomNavigation(bottomNavigationView, tabColors);
        bottomNavigationView.setDefaultBackgroundColor(getResources().getColor(R.color.tab_layout_bot));
        bottomNavigationView.setForceTint(true);
        bottomNavigationView.setTitleState(BottomNavigation.TitleState.ALWAYS_SHOW);
//        bottomNavigationView.setBehaviorTranslationEnabled(true);
        bottomNavigationView.setSelectedBackgroundVisible(true);

        bottomNavigationView.setOnTabSelectedListener(new BottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {
                switch (position) {
                    case MENU_TOP:
                        viewPager.setCurrentItem(position, wasSelected);
                        mToolbar.setTitleCenter(getResources().getString(R.string.menu_top));
                        fab.show();
                        break;
                    case MENU_FRIENDS:
                        viewPager.setCurrentItem(position, wasSelected);
                        mToolbar.setTitleCenter(getResources().getString(R.string.menu_friends));
                        fab.hide();
                        break;
                    case MENU_STREAM:
                        //Show live stream option
                        onLiveStreamOption(UserLiveStreamService.Mode.CHAT);
                        return false;
                    case MENU_MESSAGE:
                        viewPager.setCurrentItem(position, wasSelected);
                        mToolbar.setTitleCenter(getResources().getString(R.string.menu_message));
                        fab.hide();
                        break;
                    case MENU_PROFILE:
                        viewPager.setCurrentItem(position, wasSelected);
                        mToolbar.setTitleCenter(getResources().getString(R.string.menu_notify));
                        fab.hide();
                        ReadAllNotificationRequest request =
                                new ReadAllNotificationRequest(UserPreferences.getInstance().getToken());
                        getPresenter().markReadAllNotification(request);
                        break;
                }

                return true;
            }
        });
        bottomNavigationView.setNotificationMarginLeft(getResources().getDimensionPixelSize(R.dimen.notification_padding_active), getResources().getDimensionPixelSize(R.dimen.notification_padding_inactive));

        viewPager.setOffscreenPageLimit(5);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        adapter.addFragment(TimeLinePageFragment.newInstance(), FavoritePageFragment.newInstance(), FragmentEmpty.newInstance(), ConversationFragment.newInstance(), NotificationFragment.newInstance());
    }

    @Override
    public void onShowNotification(NotificationAps notifyMessage) {
        super.onShowNotification(notifyMessage);
        updateBadge();
    }

    // Directs the screen when a user clicks a notification
    private void initDirectionActivity(int actionId) {
        NotificationAps notificationAps = getIntent().getExtras().getParcelable(EXTRA_NOTIFICATION_MESSAGE);
        String content = null;
        if (notificationAps != null) {
            content = notificationAps.data.buzz;
        }
        String buzzId = null;
        if (notificationAps != null) {
            buzzId = notificationAps.data.buzzid;
        }
        String userId = null;
        if (notificationAps != null) {
            userId = notificationAps.data.userid;
        }
        String avatarUrl = null;
        if (notificationAps != null) {
            avatarUrl = notificationAps.data.avatarUrl;
        }
        if (notificationAps != null) {
            //TODO
        }
        switch (actionId) {
            case NotificationType.NOTI_CHAT:
                //Start ChatActivity when user click on the notification has new from Server via FCM
                ChatActivity.newInstance(this, notificationAps);
                break;
            case NotificationType.NOTI_LIKE_BUZZ:
            case NotificationType.NOTI_NEWS_BUZZ:
            case NotificationType.NOTI_QA_BUZZ:
            case NotificationType.NOTI_LIKE_OTHER_BUZZ:
            case NotificationType.NOTI_COMMENT_BUZZ:
            case NotificationType.NOTI_COMMENT_OTHER_BUZZ:
            case NotificationType.NOTI_APPROVED_BUZZ:
            case NotificationType.NOTI_FAVORITED_CREATE_BUZZ:
            case NotificationType.NOTI_APPROVE_BUZZ_TEXT:
            case NotificationType.NOTI_REPLY_YOUR_COMMENT:
            case NotificationType.NOTI_APPROVE_COMMENT:
            case NotificationType.NOTI_DENIED_COMMENT:
            case NotificationType.NOTI_APPROVE_SUB_COMMENT:
            case NotificationType.NOTI_DENI_SUB_COMMENT:
            case NotificationType.NOTI_DENIED_BUZZ_IMAGE:
            case NotificationType.NOTI_DENIED_BUZZ_TEXT:
            case NotificationType.NOTI_SHARE_LIVE_STREAM:
            case NotificationType.NOTI_TAG_BUZZ:
            case NotificationType.NOTI_AUDIO_SHARE_BUZZ:
                CommentActivity.launch(MainActivity.this, buzzId, "");
                break;
            case NotificationType.NOTI_DAYLY_BONUS:
                WebViewActivity.launch(MainActivity.this, TypeView.PageTypeWebView.PAGE_TYPE_BUY_POINT);
                break;
            case NotificationType.NOTI_BACKSTAGE_APPROVED:
                //TODO
                break;
            case NotificationType.NOTI_DENIED_BACKSTAGE:
                //TODO
                break;
            case NotificationType.NOTI_FROM_FREE_PAGE:
                WebViewBean bean = new WebViewBean(TypeView.PageTypeWebView.PAGE_TYPE_WEB_VIEW, avatarUrl, content, "");
                WebViewActivity.launch(MainActivity.this, bean);
                break;
            case NotificationType.NOTI_ONLINE_ALERT:
            case NotificationType.NOTI_FRIEND:
            case NotificationType.NOTI_CHECK_OUT_UNLOCK:
            case NotificationType.NOTI_FAVORITED_UNLOCK:
            case NotificationType.NOTI_UNLOCK_BACKSTAGE:
            case NotificationType.NOTI_APPROVE_USERINFO:
            case NotificationType.NOTI_DENIED_USERINFO:
            case NotificationType.NOTI_APART_OF_USERINFO:
                if (userId == null) {
                    Toast.makeText(MainActivity.this, R.string.user_not_found, Toast.LENGTH_LONG).show();
                    //  delNotifyRequest(position);
                } else {
                    UserInfoRequest userInfoRequest = new UserInfoRequest(userId);
                    getPresenter().getUserInForwardProfile(userInfoRequest);
                }
                break;
            case NotificationType.NOTI_LIVESTREAM_FROM_FAVOURIST:
            case NotificationType.NOTI_TAG_LIVESTREAM_FROM_FAVOURIST:
                if (notificationAps != null) {
                    onLiveStreamOption(UserLiveStreamService.Mode.VIEW, notificationAps.data.buzzid, notificationAps.data.streamId, "");
                }
            default:
                break;
        }
    }

    /**
     * Dùng để chuyển tab MenuBottom
     *
     * @param index
     */
    public void forwardPage(@TypeView.MenuBottom int index) {
        bottomNavigationView.setCurrentItem(index);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @OnClick({R.id.activity_main_action_fab})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.activity_main_action_fab:
                //If Login status in cache equals deviceID then show dialog required login to use features
                if (Utils.isEmptyOrNull(UserPreferences.getInstance().getToken())) {
                    onShowDialogLogin(ServerResponse.DefinitionCode.SERVER_EXPIRED_TOKEN);
                } else {
                    PostStatusActivity.launch(MainActivity.this, view, ActivityResultRequestCode.REQUEST_POST_STATUS);
                }

                break;
        }
    }

    @OnPageChange(R.id.activity_main_content)
    public void onPageChangeBottomNavigation(int position) {
        LogUtils.d(TAG, "onChangeMenuBottom----------> " + position);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull final MenuItem item) {
        drawer.closeDrawer(GravityCompat.START);
        drawer.postDelayed(new Runnable() {
            @Override
            public void run() {
                int id = item.getItemId();
                if (id == R.id.menu_main_drawer_home) {
                    forwardPage(TypeView.MenuBottom.MENU_BOTTOM_HOME);
                } else {
                    if (Utils.isEmptyOrNull(UserPreferences.getInstance().getToken())) {
                        // Handle navigation view item clicks here.
                        onShowDialogLogin(ServerResponse.DefinitionCode.SERVER_EXPIRED_TOKEN);
                    } else {
                        if (id == R.id.menu_main_drawer_online_alarm) {
                            NotificationOnlineActivity.launch(MainActivity.this);
                        } else if (id == R.id.menu_main_drawer_list_block) {
                            BlockListActivity.newInstance(MainActivity.this);
                        } else if (id == R.id.menu_main_drawer_account_setting) {
                            AccountSettingActivity.newInstance(MainActivity.this);
                        } else if (id == R.id.menu_main_drawer_notify_setting) {
                            NotificationSettingActivity.newInstance(MainActivity.this);
                        } else if (id == R.id.menu_main_drawer_logout) {
                            if (UserPreferences.getInstance().isLogin()) {
                                UserPreferences userPreferences = UserPreferences.getInstance();
                                String token = userPreferences.getToken();
                                String notify_token = FirebaseInstanceId.getInstance().getToken();
                                LogoutRequest logoutRequest = new LogoutRequest(token, notify_token);
                                getPresenter().onLogout(logoutRequest);
                            }
                        }
                    }
                }
            }
        }, 250);
        return true;
    }

    @Override
    public void onLogout() {

        AuthenticationUtil.onLogout();

        CacheJson.saveObject(CacheType.CACHE_TIMELINE_FAVORITE, new BuzzListResponse());
        CacheJson.saveObject(CacheType.CACHE_TIMELINE_LIVE_STREAM_FAVORITE, new BuzzListResponse());

        // Finish current activity and back to login screen.
        Toast.makeText(this, R.string.logout_success, Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this, SplashActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        MainActivity.this.finish();

        unRegisterEvents();
    }

    @Override
    public void onUserInForwardProfile(UserInfoResponse userInfoResponse) {
        userInfoSubject.onNext(userInfoResponse);

        UserInfoResponse userProfileBean = new UserInfoResponse(userInfoResponse.userId, userInfoResponse.gender, userInfoResponse.userName, userInfoResponse.avatar);
        MyProfileActivity.launch(MainActivity.this, userProfileBean, TypeView.ProfileType.COME_FROM_OTHER);
    }

    @Override
    public void onReadAllNotification() {
        UserPreferences.getInstance().saveNotifyNum(0);
        updateBadge();
    }

    private void setObservables() {
        Disposable disposable = userInfoSubject
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.io())
                .subscribe(new Consumer<UserInfoResponse>() {
                    @Override
                    public void accept(UserInfoResponse userInfoResponse) throws Exception {
                        if (SystemUtils.isNetworkConnected()) {
                            GetUserInfoResponse getUserInfoResponse = new GetUserInfoResponse();
                            getUserInfoResponse.data = userInfoResponse;
                            CacheJson.saveObject(String.format(CacheType.CACHE_TIMELINE_USER_INFO_ID, userInfoResponse.userId), getUserInfoResponse);
                        }
                    }
                });
        disposables.add(disposable);
    }

    /**
     * Update badge number for all badge type
     */
    public void updateBadge() {
        int numberUnreadMs = UserPreferences.getInstance().getNumberUnreadMessage();
        if (numberUnreadMs > 0 && numberUnreadMs <= 20) {
            bottomNavigationView.setNotification(String.valueOf(numberUnreadMs), MENU_MESSAGE);
        } else if (numberUnreadMs > 20) {
            bottomNavigationView.setNotification("20+", MENU_MESSAGE);
        } else if (numberUnreadMs == 0) {
            bottomNavigationView.setNotification("", MENU_MESSAGE);
        }

        int numberNotify = UserPreferences.getInstance().getNotifyNum();
        if (numberNotify > 0 && numberNotify <= 20) {
            bottomNavigationView.setNotification(String.valueOf(numberNotify), MENU_PROFILE);
        } else if (numberNotify > 20) {
            bottomNavigationView.setNotification("20+", MENU_PROFILE);
        } else if (numberNotify == 0) {
            bottomNavigationView.setNotification("", MENU_PROFILE);
        }

        int numberNotifyOnlineAlarm = UserPreferences.getInstance().getNotifyOnlineAlarmNum();
        if (numberNotifyOnlineAlarm > 0 && numberNotifyOnlineAlarm <= 20) {
            if (mBadgeOnlineNotification.getVisibility() == View.INVISIBLE)
                mBadgeOnlineNotification.setVisibility(View.VISIBLE);
            mBadgeOnlineNotification.setText(String.valueOf(numberNotifyOnlineAlarm));
        } else if (numberNotifyOnlineAlarm > 20) {
            if (mBadgeOnlineNotification.getVisibility() == View.INVISIBLE)
                mBadgeOnlineNotification.setVisibility(View.VISIBLE);
            mBadgeOnlineNotification.setText("20+");
        } else if (numberNotifyOnlineAlarm == 0) {
            if (mBadgeOnlineNotification.getVisibility() == View.VISIBLE)
                mBadgeOnlineNotification.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Request permission access to write external storage
     */

    @SuppressLint("CheckResult")
    public void requestAccessPermission(final int requestCode, final String... permissions) {
        getRxPermissions().requestEach(permissions)
                .subscribe(new Consumer<Permission>() {
                    @Override
                    public void accept(Permission permission) {
                        Log.i(TAG, "Permission result " + permission);
                        if (permission != null && permission.granted) {

                            switch (permission.name) {
                                case Manifest.permission.WRITE_EXTERNAL_STORAGE:
                                    StickerAndGiftDownloadService.startService(MainActivity.this); // request download gift and sticker
                                    break;
                            }

                        } else if (permission != null && permission.shouldShowRequestPermissionRationale) {
                            // Denied permission without ask never again
                            Toast.makeText(context, getString(R.string.request_access_permission_denied), Toast.LENGTH_SHORT).show();
                        } else {
                            // Denied permission with ask never again Need to go to the settings
                            Toast.makeText(context, getString(R.string.request_access_permission_denied), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable t) {
                        Log.e(TAG, "onError", t);
                    }
                }, new Action() {
                    @Override
                    public void run() {
                        Log.i(TAG, "OnComplete");
                    }
                });
    }

    /*-----------------------------------Inner class----------------------------------------------*/
    private static class ViewPagerAdapter extends FragmentPagerAdapter {
        private List<Fragment> fragments;

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
            fragments = new ArrayList<>();
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        public void onClear() {
            fragments.clear();
        }

        public void addFragment(Fragment... fragments) {
            if (fragments == null)
                return;
            if (fragments.length > 0) {
                if (this.fragments == null)
                    this.fragments = new ArrayList<>();
                else
                    this.fragments.clear();

                List<Fragment> fragmentsNews = new ArrayList<>();
                fragmentsNews.addAll(Arrays.asList(fragments));
                this.fragments.addAll(fragmentsNews);
                notifyDataSetChanged();
            }
        }
    }

}
