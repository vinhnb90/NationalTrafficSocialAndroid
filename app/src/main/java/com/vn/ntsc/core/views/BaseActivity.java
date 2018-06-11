package com.vn.ntsc.core.views;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.tux.mylab.gallery.Gallery;
import com.google.gson.Gson;
import com.nankai.designlayout.dialog.DialogMaterial;
import com.nankai.designlayout.dialog.enums.Style;
import com.tux.socket.models.Gift;
import com.tux.socket.models.Media;
import com.tux.socket.models.Message;
import com.tux.socket.models.SocketEvent;
import com.tux.socket.models.Sticker;
import com.tux.socket.models.Text;
import com.vn.ntsc.BuildConfig;
import com.vn.ntsc.R;
import com.vn.ntsc.app.AppController;
import com.vn.ntsc.core.BasePresenter;
import com.vn.ntsc.repository.ActivityResultRequestCode;
import com.vn.ntsc.repository.TypeView;
import com.vn.ntsc.repository.model.notification.push.NotificationAps;
import com.vn.ntsc.repository.model.notification.push.NotificationMessage;
import com.vn.ntsc.repository.model.notification.push.NotificationType;
import com.vn.ntsc.repository.model.user.UserInfoResponse;
import com.vn.ntsc.repository.model.webview.WebViewBean;
import com.vn.ntsc.repository.preferece.Preferences;
import com.vn.ntsc.repository.preferece.UserPreferences;
import com.vn.ntsc.services.UserLiveStreamService;
import com.vn.ntsc.services.fcm.MyFirebaseMessagingService;
import com.vn.ntsc.ui.chat.ChatActivity;
import com.vn.ntsc.ui.comment.CommentActivity;
import com.vn.ntsc.ui.livestream.LiveStreamActivity;
import com.vn.ntsc.ui.main.MainActivity;
import com.vn.ntsc.ui.profile.my.MyProfileActivity;
import com.vn.ntsc.ui.webview.WebViewActivity;
import com.vn.ntsc.utils.ApplicationNotificationManager;
import com.vn.ntsc.utils.Constants;
import com.vn.ntsc.utils.LogUtils;
import com.vn.ntsc.utils.SystemUtils;
import com.vn.ntsc.utils.Utils;
import com.vn.ntsc.utils.notify.NotificationUtils;
import com.vn.ntsc.widget.eventbus.RxEventBus;
import com.vn.ntsc.widget.permissions.Permission;
import com.vn.ntsc.widget.permissions.RxPermissions;
import com.vn.ntsc.widget.socket.RxSocket;

import java.util.List;

import butterknife.BindColor;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import me.leolin.shortcutbadger.ShortcutBadger;

/**
 * Created by nankai on 8/3/2017.
 */
public abstract class BaseActivity<T extends BasePresenter> extends BaseActivityDefaultCallBack<T> implements IActivity {

    protected final int MEDIA_LOADER_ID_MODE = 106;
    protected final int REQUEST_PERMISSION_LIVESTEAM = 107;
    protected final int REQUEST_READ_EXTERNAL_FROM_DISPLAY_MEDIA_MODE = 102;
    protected final int REQUEST_READ_EXTERNAL_WHEN_CLICK_SUBMIT_POST_MODE = 103;

    private String TAG;
    protected AppCompatActivity context;
    protected View viewRoot;
    private RxPermissions rxPermissions;
    private Handler mHandler;
    private BroadcastReceiver mBroadcastReceiver;
    private LocalBroadcastManager mLocalBroadcastManager;
    public static final int SNACKBAR_DURATION = 2000;

    @BindColor(R.color.green)
    int actionSuccessCustomColor;
    @BindColor(R.color.white)
    int textColor;
    @BindColor(R.color.default_app)
    int successCustomColor;

    private CompositeDisposable disposables;

    final public RxPermissions getRxPermissions() {
        if (null == rxPermissions) {
            rxPermissions = new RxPermissions(this);
            rxPermissions.setLogging(true);
        }
        return rxPermissions;
    }

    final public Handler getHandler() {
        if (mHandler == null) {
            mHandler = new Handler();
        }
        return mHandler;
    }

    @Deprecated
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TAG = BaseActivity.this.getClass().getSimpleName();
        LogUtils.i(TAG, "onCreate");
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        viewRoot = getLayoutInflater().inflate(getLayoutId(), null, false);
        setContentView(viewRoot);
        try {
            context = this;
            ButterKnife.bind(this);
            rxPermissions = new RxPermissions(this);
            rxPermissions.setLogging(true);

            onCreateView(viewRoot);

            viewRoot.post(new Runnable() {
                @Override
                public void run() {
                    onViewReady();
                }
            });
        } catch (final Exception e) {
            e.printStackTrace();
            if (viewRoot != null)
                viewRoot.post(new Runnable() {
                    @Override
                    public void run() {
                        Spanned description = Html.fromHtml(getResources().getString(R.string.view_error_2));

                        if (BuildConfig.DEBUG)
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                                description = Html.fromHtml(getResources().getString(R.string.view_error, e.getMessage() + " in class " + BaseActivity.this.getClass().getSimpleName()), Html.FROM_HTML_MODE_LEGACY);
                            } else {
                                description = Html.fromHtml(getResources().getString(R.string.view_error, e.getMessage() + " in class " + BaseActivity.this.getClass().getSimpleName()));
                            }

                        DialogMaterial.Builder dialog = new DialogMaterial.Builder(BaseActivity.this)
                                .removeHeader()
                                .setTitle(R.string.common_error)
                                .setContent(description)
                                .onPositive(R.string.common_ok, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        BaseActivity.this.finish();
                                        dialog.dismiss();
                                    }
                                });
                        dialog.show();
                    }
                });
        }
    }

    @Override
    public void onStart(View viewRoot) {
        LogUtils.i(TAG, "onStart");
        if (mLocalBroadcastManager == null)
            mLocalBroadcastManager = LocalBroadcastManager
                    .getInstance(getApplicationContext());
        if (hasShowNotificationView())
            registerBroadcastFCM();

        if (hasRegisterSocket()) {
            registerEvents();
        }
    }

    @Override
    public void onResume(View viewRoot) {
        LogUtils.i(TAG, "onResume");
    }

    @Override
    public void onPostResume(View viewRoot) {
        LogUtils.i(TAG, "onPostResume");
    }

    // <editor-fold defaultstate="collapsed" desc="Do not use this method">
    @Override
    protected final void onStart() {
        super.onStart();
        if (viewRoot != null)
            viewRoot.post(new Runnable() {
                @Override
                public void run() {
                    onStart(viewRoot);
                }
            });
    }

    @Override
    protected final void onResume() {
        super.onResume();
        if (viewRoot != null)
            viewRoot.post(new Runnable() {
                @Override
                public void run() {
                    onResume(viewRoot);
                }
            });
    }

    @Override
    protected final void onPostResume() {
        super.onPostResume();
        if (viewRoot != null)
            viewRoot.post(new Runnable() {
                @Override
                public void run() {
                    onPostResume(viewRoot);
                }
            });
    }

    // </editor-fold>

    @Override
    protected void onPause() {
        super.onPause();
        LogUtils.i(TAG, "onPause");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        LogUtils.i(TAG, "onRestart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        LogUtils.i(TAG, "onStop");

        if (hasShowNotificationView())
            unregisterBroadcastFCM();
        if (hasRegisterSocket())
            unRegisterEvents();
    }

    @Override
    protected void onDestroy() {
        LogUtils.i(TAG, "onDestroy");

        if (hasShowNotificationView())
            unregisterBroadcastFCM();
        if (hasRegisterSocket())
            unRegisterEvents();

        // Set number notification for App Shortcut before app has been close
        LogUtils.e(TAG, "Quit app set number notification for shortcut:" + UserPreferences.getInstance().getNotifyNum());
        ShortcutBadger.applyCount(this.getApplicationContext(),
                UserPreferences.getInstance().getNotifyNum());

        if (null != mHandler)
            try {
                Utils.clearHandler(mHandler);
            } catch (Exception e) {
                e.printStackTrace();
            }
        RxEventBus.unregister(this);
        mLocalBroadcastManager = null;
        viewRoot = null;
        rxPermissions = null;
        context = null;
        super.onDestroy();
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
        LogUtils.i(TAG, "onAttachFragment: " + fragment.getClass().getSimpleName());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtils.i(TAG, "--------------- onActivityResult ---------------"
                + "\n| requestCode:" + requestCode
                + "\n| resultCode: " + resultCode);
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        if (fragments != null) {
            for (Fragment fragment : fragments) {
                // i have no idea why AppCompatActivity#getSupportFragmentManager().getFragments return wrong fragments size, so check null before do something
                if (fragment != null) {
                    LogUtils.i(fragment.getClass().getSimpleName(), "resultCode: " + resultCode + "\n" + "requestCode: " + requestCode);
                    fragment.onActivityResult(requestCode, resultCode, data);
                }
            }
        }

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == ActivityResultRequestCode.REQUEST_CODE_FINISH) {
                finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        LogUtils.i(TAG, "onBackPressed");
        if (BaseActivity.this instanceof MainActivity) {
            new DialogMaterial.Builder(this).setStyle(Style.HEADER_WITH_NOT_HEADER)
                    .setContent(R.string.do_you_want_close_app)
                    .onPositive(R.string.common_yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            AppController.getAppContext().finishAllActivities();
                        }
                    }).onNegative(R.string.common_no, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            }).show();
        } else {
            int count = getFragmentManager().getBackStackEntryCount();
            if (count <= 1) {
                super.onBackPressed();
            } else {
                getFragmentManager().popBackStack();
            }
        }
    }

    /**
     * track all socket events
     * Listen all message to show notification on the SnackBar
     */
    final protected void registerEvents() {
        LogUtils.i(TAG, "Register socket event");
        if (disposables == null || disposables.isDisposed())
            disposables = new CompositeDisposable();
        Disposable socketEvent = RxSocket.getSocketEvent()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<SocketEvent>() {
                    @Override
                    public void accept(SocketEvent socketEvent) throws Exception {

                        if (socketEvent == null || socketEvent.getEventType() != SocketEvent.EVENT_RECEIVE) {
                            LogUtils.w(TAG, "socket Event -------> null!");
                            return;
                        }

                        onReceiveSocket(socketEvent);

                        UserPreferences userPreferences = UserPreferences.getInstance();
                        if (userPreferences.getCurrentFriendChat().equals(socketEvent.getMessage().getSenderId())) {
                            //Yourself online in chat room
                            LogUtils.w(TAG, "socket Event ------->Yourself online in chat room");
                            return;
                        }

                        //Send broad cast to show notification panel if has new message from all friend if need
                        if (socketEvent.getMessage() != null && socketEvent.getMessage().getMessageType() != null) {
                            LogUtils.i(TAG, "socket Event ------->Show notification on the SnackBar: " + socketEvent.getMessage().getMessageType().intern());
                            onShowNotification(socketEvent.getMessage());
                        }

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        LogUtils.e(TAG, "throwable socket " + throwable.getMessage());
                        onReceiveSocket(new SocketEvent.Builder()
                                .eventType(SocketEvent.EVENT_ERROR)
                                .build());
                        throwable.printStackTrace();
                    }
                });

        disposables.add(socketEvent);
    }

    /**
     * dispose event tracker to avoid leak
     */
    final protected void unRegisterEvents() {
        if (disposables != null)
            disposables.dispose();
    }

    final protected void unregisterBroadcastFCM() {
        if (mLocalBroadcastManager != null)
            mLocalBroadcastManager.unregisterReceiver(mBroadcastReceiver);
    }

    final protected void registerBroadcastFCM() {
        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                assert presenter != null;
                if (action != null && action.equals(MyFirebaseMessagingService.ACTION_FCM_RECEIVE_MESSAGE)) {
                    NotificationAps notifyMessage = intent.getParcelableExtra(MyFirebaseMessagingService.EXTRA_NOTIFICATION_MESSAGE);
                    onShowNotification(notifyMessage);
                }
            }
        };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MyFirebaseMessagingService.ACTION_FCM_RECEIVE_MESSAGE);
        intentFilter.addAction(MyFirebaseMessagingService.ACTION_FCM_RECEIVE_MESSAGE);
        mLocalBroadcastManager.registerReceiver(mBroadcastReceiver,
                intentFilter);
    }

    @Override
    public void onShowNotification(Message message) {
        if (message == null) {
            LogUtils.w(TAG, "Can't show notification using socket because Message is null");
            return;
        }

        if (message.getMessageType() == null) {
            LogUtils.w(TAG, "Can't show notification using socket because Message Type is null");
            return;
        }

        LogUtils.i(TAG, "Show notification using socket");

        UserPreferences userPreferences = UserPreferences.getInstance();
        if (//Must be a message sent to yourself
                Utils.nullToEmpty(userPreferences.getUserId()).equals(message.getReceiverId())
                        //Not friend's message on in chat room
                        && !userPreferences.getCurrentFriendChat().equals(message.getSenderId())
                        //Only accept TYPE_TEXT|TYPE_GIFT|TYPE_FILE|TYPE_STICKER
                        && ((Text.TYPE_TEXT.equals(message.getMessageType()))
                        || (Text.TYPE_TEXT_EMOJI.equals(message.getMessageType()))
                        || (Gift.TYPE_GIFT.equals(message.getMessageType()))
                        || (Media.TYPE_FILE.equals(message.getMessageType()))
                        || (Sticker.TYPE_STICKER.equals(message.getMessageType())))) {
            NotificationAps notifyMessage = NotificationAps.newInstance(message);
            MyFirebaseMessagingService.LocalBroadcast.sendBroadcast(getApplicationContext(), notifyMessage);
        } else if (Message.MessageType.NOTI_BUZZ.equals(message.getMessageType())) {
            Gson gson = new Gson();
            NotificationMessage notificationMessage = gson.fromJson(message.getRawText(), NotificationMessage.class);
            LogUtils.i(TAG, message.getRawText());
            MyFirebaseMessagingService.LocalBroadcast.sendBroadcast(getApplicationContext(), notificationMessage.value.aps);
        }
    }

    @Override
    public void onShowNotification(NotificationAps notifyMessage) {
        if (notifyMessage == null) {
            LogUtils.w(TAG, "Can't show notification using socket because NotificationAps is null");
            return;
        }

        LogUtils.i(TAG, "Show notification using FCM");
        // if type is'nt text notification then always notify
        UserPreferences userPreferences = UserPreferences.getInstance();

        if (notifyMessage.data.notiType != NotificationType.NOTI_CHAT) {

            try {
                showNotificationView(notifyMessage);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (notifyMessage.data.notiType == NotificationType.NOTI_ONLINE_ALERT) {
                Preferences preferences = Preferences.getInstance();
                int alertPoint = preferences.getOnlineAlertPoints();
                int currentPoint = userPreferences.getNumberPoint();
                userPreferences.saveNumberPoint(currentPoint - alertPoint);
            }

        } else /*else if only not in Chat screen then notify*/ {
            try {
                showNotificationView(notifyMessage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void showNotificationView(final NotificationAps mNotificationAps) throws Exception {
        NotificationUtils.playNotificationSound(getApplicationContext());
        NotificationUtils.vibarateNotification(getApplicationContext());

        String message = ApplicationNotificationManager.getMessageNotification(
                getApplicationContext(), mNotificationAps);
        int iconNotification = ApplicationNotificationManager
                .getIconNotification(mNotificationAps);

        final Snackbar customSnackBar = Snackbar.make(viewRoot, "", SNACKBAR_DURATION);
        Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) customSnackBar.getView();
        @SuppressLint("InflateParams") View customSnackView = getLayoutInflater().inflate(R.layout.layout_snack_bar_notifi, null);
        TextView tvContent = customSnackView.findViewById(R.id.tv_content);
        ImageView ivIcon = customSnackView.findViewById(R.id.iv_notifi);
        tvContent.setText(message);
        ivIcon.setImageResource(iconNotification);
        customSnackView.findViewById(R.id.ic_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customSnackBar.dismiss();
            }
        });
        customSnackView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performClickNotificationView(v, mNotificationAps);
            }
        });
        // We can also customize the above controls
        layout.setPadding(0, 0, 0, 0);
        layout.addView(customSnackView, 0);

        customSnackBar.show();
    }

    final protected void performClickNotificationView(View view, NotificationAps mNotificationAps) {
        // Hide keyboard
        SystemUtils.hideSoftKeyboard(this);

        int type = mNotificationAps.data.notiType;

        switch (type) {
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
                CommentActivity.launch(BaseActivity.this, view, mNotificationAps.data.buzzid, mNotificationAps.data.userid);
                break;

            case NotificationType.NOTI_CHECK_OUT_UNLOCK:
            case NotificationType.NOTI_FAVORITED_UNLOCK:
            case NotificationType.NOTI_UNLOCK_BACKSTAGE:
            case NotificationType.NOTI_FRIEND:
            case NotificationType.NOTI_ONLINE_ALERT:
                MyProfileActivity
                        .launch(BaseActivity.this, view, new UserInfoResponse(mNotificationAps.data.userid, Constants.GENDER_TYPE_MAN
                                , null, null, Constants.BUZZ_TYPE_IS_NOT_FAVORITE), TypeView.ProfileType.COME_FROM_NOTI);
                break;
            case NotificationType.NOTI_APPROVE_USERINFO:
            case NotificationType.NOTI_DENIED_USERINFO:
            case NotificationType.NOTI_APART_OF_USERINFO:
                MyProfileActivity
                        .launch(BaseActivity.this, view, new UserInfoResponse(mNotificationAps.data.ownerId, Constants.GENDER_TYPE_MAN
                                , null, null, Constants.BUZZ_TYPE_IS_NOT_FAVORITE), TypeView.ProfileType.COME_FROM_NOTI);
                break;
            case NotificationType.NOTI_CHAT:
                /*
                  Start ChatActivity when user click on the notification has new from Server via FCM
                 */
                ChatActivity.newInstance(this, mNotificationAps);
                break;
            case NotificationType.NOTI_DAYLY_BONUS:
                WebViewActivity.launch(BaseActivity.this, TypeView.PageTypeWebView.PAGE_TYPE_BUY_POINT);
                break;
            case NotificationType.NOTI_BACKSTAGE_APPROVED:
                //TODO
                break;
            case NotificationType.NOTI_DENIED_BACKSTAGE:
                //TODO
                break;
            case NotificationType.NOTI_FROM_FREE_PAGE:
                String url = mNotificationAps.data.avatarUrl;
                WebViewBean bean = new WebViewBean(TypeView.PageTypeWebView.PAGE_TYPE_WEB_VIEW, url, mNotificationAps.data.buzz, "");
                WebViewActivity.launch(BaseActivity.this, bean);
                break;
            case NotificationType.NOTI_LIVESTREAM_FROM_FAVOURIST:
            case NotificationType.NOTI_TAG_LIVESTREAM_FROM_FAVOURIST:
                onLiveStreamOption(UserLiveStreamService.Mode.VIEW, mNotificationAps.data.buzzid, mNotificationAps.data.streamId, "");
            default:
                break;
        }
    }


    /**
     * create cached background for reuse latter
     *
     * @param bgResId background resource id
     */
    final protected void loadCachedBackgroundContainer(@DrawableRes int bgResId) {
        Glide.with(getApplicationContext())
                .asBitmap()
                .load(bgResId)
                .into(new SimpleTarget<Bitmap>(AppController.SCREEN_WIDTH, AppController.SCREEN_HEIGHT) {

                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, Transition<? super Bitmap> transition) {
                        BitmapDrawable drawable = new BitmapDrawable(resource);
                        getWindow().setBackgroundDrawable(drawable);
                    }
                });
    }

    /**
     * confirm dialog
     *
     * @param dialogMessageResId   resource id for show content message
     * @param onOK                 when click ok
     * @param isShowNegativeButton true: show negative button
     */
    final protected void showConfirmDialog(int dialogMessageResId, DialogInterface.OnClickListener onOK, boolean isShowNegativeButton) {
        DialogMaterial.Builder builder = new DialogMaterial.Builder(context)
                .setStyle(Style.HEADER_WITH_NOT_HEADER)
                .setContent(dialogMessageResId);
        if (onOK != null) {
            builder.onPositive(R.string.common_ok_2, onOK);
        } else {
            builder.onPositive(R.string.common_ok_2, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        }

        if (isShowNegativeButton)
            builder.onNegative(R.string.common_cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        builder.show();
    }

    /**
     * confirm dialog
     *
     * @param dialogMessage        string content message
     * @param onOK                 when click ok
     * @param isShowNegativeButton true: show negative button
     */
    final protected void showConfirmDialog(String dialogMessage, DialogInterface.OnClickListener onOK, boolean isShowNegativeButton) {
        DialogMaterial.Builder builder = new DialogMaterial.Builder(context)
                .setStyle(Style.HEADER_WITH_TITLE)
                .setTitle(R.string.common_notify)
                .setContent(dialogMessage);
        if (onOK != null) {
            builder.onPositive(R.string.common_ok_2, onOK);
        } else {
            builder.onPositive(R.string.common_ok_2, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        }

        if (isShowNegativeButton)
            builder.onNegative(R.string.common_cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        builder.show();
    }


    /**
     * Show live stream option:
     * - From video exists
     * - With direct record video via camera
     * @param mode {@link com.vn.ntsc.services.UserLiveStreamService.Mode}
     */
    final public void onLiveStreamOption(@UserLiveStreamService.Mode final int mode) {
        onLiveStreamOption(mode, null, null, null);
    }

    /**
     * Show live stream option:
     * - From video exists
     * - With direct record video via camera
     * @param mode {@link com.vn.ntsc.services.UserLiveStreamService.Mode}
     * @param buzzId {@link com.vn.ntsc.repository.model.timeline.datas.BuzzBean}
     * @param streamId Stream id
     * @param thumbnailUrl using first load
     */
    final public void onLiveStreamOption(@UserLiveStreamService.Mode final int mode, @Nullable String buzzId, @Nullable String streamId, @Nullable String thumbnailUrl) {

        LogUtils.i(TAG, "mode: " + mode + "|buzzID: " + buzzId + "|streamID: " + streamId + "|thumbnailUrl: " + thumbnailUrl);

        // remember call after inject to avoid NPE
        UserPreferences userPref = UserPreferences.getInstance();
        String userId = userPref.getUserId();
        String token = userPref.getToken();

        if (!TextUtils.isEmpty(userId) && !TextUtils.isEmpty(token)) {

            if (mode == UserLiveStreamService.Mode.CHAT) {
                if (!checkPermission(Manifest.permission.CAMERA)) {
                    //Request accept camera, record video  and write external storage permission
                    requestAccessPermission(REQUEST_PERMISSION_LIVESTEAM, mode,
                            Manifest.permission.CAMERA,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.RECORD_AUDIO);
                }else {
                    LiveStreamActivity.launch(BaseActivity.this, mode, null, UserLiveStreamService.TypeView.STREAM_CAMERA);
                }

                // <editor-fold defaultstate="collapsed" desc="Temporarily not showing v1 to v2 will show up">
//                CharSequence[] liveStreamOption = getResources().getTextArray(R.array.live_stream_option);
//                final int[] checked = {0};

//                new AlertDialog.Builder(this, R.style.LiveStreamDialogAlert)
//                        .setTitle(null)
//                        .setCancelable(false)
//                        .setSingleChoiceItems(liveStreamOption, 0, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int checkedPosition) {
//                                checked[0] = checkedPosition;
//                            }
//                        })
//                        .setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int checkedPosition) {
//                                if (checked[0] == 0) {
//                                    if (!checkPermission(Manifest.permission.CAMERA)) {
//                                        //Request accept camera, record video  and write external storage permission
//                                        requestAccessPermission(REQUEST_PERMISSION_LIVESTEAM, mode,
//                                                Manifest.permission.CAMERA,
//                                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                                                Manifest.permission.RECORD_AUDIO);
//
//                                        dialogInterface.dismiss();
//                                        return;
//                                    }
//
//                                    dialogInterface.dismiss();
//                                    LiveStreamActivity.launch(BaseActivity.this, mode, UserLiveStreamService.TypeView.STREAM_CAMERA);
//                                } else {
//                                    if (!checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
//                                        //Request accept camera, record video  and write external storage permission
//                                        requestAccessPermission(REQUEST_PERMISSION_LIVESTEAM, mode,
//                                                Manifest.permission.CAMERA,
//                                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                                                Manifest.permission.RECORD_AUDIO);
//                                        dialogInterface.dismiss();
//                                        return;
//                                    }
//
//                                    dialogInterface.dismiss();
//                                    new Gallery.Builder()
//                                            .sortType(Gallery.SORT_BY_VIDEOS)
//                                            .isMultichoice(false)
//                                            .build()
//                                            .start(BaseActivity.this);
//                                }
//
//
//                            }
//                        })
//                        .setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//                                dialogInterface.dismiss();
//                            }
//                        }).show();
                // </editor-fold>//GEN-END:initComponents

            } else {
                LiveStreamActivity.launch(BaseActivity.this, mode, buzzId, streamId, thumbnailUrl);
            }
        } else {
            onShowDialogLogin(mode);
        }
    }

    /**
     * Determine whether <em>you</em> have been granted a particular permission.
     *
     * @param permission The name of the permission being checked.
     * @return {@link PackageManager#PERMISSION_GRANTED} if you have the
     * permission, or {@link PackageManager#PERMISSION_DENIED} if not.
     * @see PackageManager#checkPermission(String, String)
     */
    final protected boolean checkPermission(String permission) {
        int res = checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    /**
     * Request permission access to read external storage use Observer
     * Created on 2017 Aug 28 by Robert
     */
    final protected void requestAccessPermission(final int requestCode, @UserLiveStreamService.Mode final int mode,@NonNull final String... permissions) {
        getRxPermissions().requestEach(permissions)
                .subscribe(new Consumer<Permission>() {
                    @Override
                    public void accept(Permission permission) {
                        Log.i(TAG, "Permission result " + permission);
                        if (permission != null && permission.granted) {
                            if (requestCode == REQUEST_PERMISSION_LIVESTEAM) {
                                switch (permission.name) {
                                    case Manifest.permission.CAMERA:
                                        LiveStreamActivity.launch(BaseActivity.this, mode, UserLiveStreamService.TypeView.STREAM_CAMERA);
                                        break;
                                    //TODO User has been accepted for READ_EXTERNAL_STORAGE permission
                                    case Manifest.permission.READ_EXTERNAL_STORAGE:
                                        new Gallery.Builder()
                                                .sortType(Gallery.SORT_BY_VIDEOS)
                                                .isMultichoice(false)
                                                .build()
                                                .start(BaseActivity.this);
                                        break;
                                    default:
                                        break;
                                }
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

    @Override
    public void onViewReady() {
        //TODO
    }

    public void onReceiveSocket(SocketEvent socketEvent) {
        //TODO
    }

    @Override
    public boolean hasRegisterSocket() {
        return true;
    }

    @Override
    public boolean hasShowNotificationView() {
        return true;
    }
}
