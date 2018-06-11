package com.vn.ntsc.ui.livestream;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.nankai.designlayout.dialog.DialogMaterial;
import com.nankai.designlayout.dialog.enums.Style;
import com.tux.socket.models.SocketEvent;
import com.vn.ntsc.R;
import com.vn.ntsc.core.views.BaseActivity;
import com.vn.ntsc.repository.model.comment.ListCommentResponse;
import com.vn.ntsc.repository.model.notification.push.NotificationAps;
import com.vn.ntsc.repository.model.notification.push.NotificationType;
import com.vn.ntsc.repository.model.poststatus.PostStatusResponse;
import com.vn.ntsc.repository.model.timeline.BuzzDetailRequest;
import com.vn.ntsc.repository.model.timeline.BuzzDetailResponse;
import com.vn.ntsc.repository.model.timeline.JoinBuzzRequest;
import com.vn.ntsc.repository.model.timeline.JoinBuzzResponse;
import com.vn.ntsc.repository.model.timeline.datas.sub.ListCommentBean;
import com.vn.ntsc.repository.model.token.CheckTokenRequest;
import com.vn.ntsc.repository.model.token.CheckTokenResponse;
import com.vn.ntsc.repository.preferece.UserPreferences;
import com.vn.ntsc.services.UserLiveStreamService;
import com.vn.ntsc.utils.Constants;
import com.vn.ntsc.utils.ImagesUtils;
import com.vn.ntsc.utils.LogUtils;
import com.vn.ntsc.utils.Utils;
import com.vn.ntsc.widget.eventbus.RxEventBus;
import com.vn.ntsc.widget.eventbus.SubjectCode;
import com.vn.ntsc.widget.livestream.AppRTCAudioManager;
import com.vn.ntsc.widget.livestream.MediaConnection;
import com.vn.ntsc.widget.livestream.MediaConnectionEvents;
import com.vn.ntsc.widget.livestream.WebSocketRTCEvents;
import com.vn.ntsc.widget.livestream.WebSocketRTCState;
import com.vn.ntsc.widget.views.images.ScalingImageView;
import com.vn.ntsc.widget.views.livestream.MediaStream;
import com.vn.ntsc.widget.views.livestream.PercentFrameLayout;

import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.EglBase;
import org.webrtc.RendererCommon;
import org.webrtc.StatsReport;
import org.webrtc.SurfaceTextureHelper;
import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoFileRenderer;
import org.webrtc.VideoRenderer;
import org.webrtc.VideoTrack;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

import static com.tux.socket.models.SocketEvent.EVENT_AUTH_SUCCESS;

/**
 * Created by nankai on 2017 Nov 10
 */
public class LiveStreamActivity extends BaseActivity<LiveStreamPresenter> implements LiveStreamActivityListener, WebSocketRTCEvents, LiveStreamContract.View {

    private final String TAG = "LiveStreamActivity";
    private static final String BUNDLE_MODE = "bundle.mode";
    private static final String BUNDLE_TYPE_VIEW = "bundle.type.view";
    private static final String BUNDLE_BUZZ_ID = "bundle.buzz.id";
    private static final String BUNDLE_STREAM_ID = "bundle.stream.id";
    private static final String BUNDLE_THUMBNAIL = "bundle.thumbnail";
    private static final String BUNDLE_FILE_PATH = "bundle.file.path";
    // Peer connection statistics callback period in ms.
    private static final int STAT_CALLBACK_PERIOD = 1000;

    enum Size {
        QVGA, VGA, HD
    }

    private static final int VIDEO_FPS = 30;
    private boolean isShowLog = false;

    private static Map<Size, MediaConnection.VideoSize> videoSizeMap = new HashMap<>();

    static {
        videoSizeMap.put(Size.QVGA, new MediaConnection.VideoSize("QVGA", 320, 240));
        videoSizeMap.put(Size.VGA, new MediaConnection.VideoSize("VGA", 640, 480));
        videoSizeMap.put(Size.HD, new MediaConnection.VideoSize("HD", 1290, 960));
    }

    private Size currentVideoSize = Size.VGA;

    @BindView(R.id.activity_live_stream_remote_video_layout)
    PercentFrameLayout remoteVideoContainer;
    @BindView(R.id.activity_live_stream_remote_video_view)
    SurfaceViewRenderer remoteVideoView;
    @BindView(R.id.activity_live_stream_local_video_layout)
    PercentFrameLayout localVideoContainer;
    @BindView(R.id.activity_live_stream_local_video_view)
    SurfaceViewRenderer localVideoView;

    @BindView(R.id.activity_live_stream_live_stream_status_thumbnail)
    ScalingImageView mThumbnail;
    @BindView(R.id.activity_live_stream_layout_live_stream_close)
    RelativeLayout layoutLiveStreamClose;
    @BindView(R.id.activity_live_stream_delete_buzz)
    LinearLayout removeTimeline;
    @BindView(R.id.activity_live_stream_post_status)
    LinearLayout postTimeline;
    @BindView(R.id.activity_live_stream_live_stream_status_pause)
    TextView statusPause;

    private Toast logToast;

    String buzzId;
    String streamID;
    String thumbnail;
//    private CpuMonitor cpuMonitor;

    private Gson gson;

    private String filePath;
    private int typeView = UserLiveStreamService.TypeView.STREAM_CAMERA;

    private VideoFileRenderer videoFileRenderer;
    private RendererCommon.ScalingType scalingType;
    private AppRTCAudioManager audioManager = null;
    private EglBase rootEglBase;

    MediaConnectionEvents mediaConnectionEvents;

    @Inject
    MediaConnection mMediaConnection;
    @Inject
    UserLiveStreamService mUserLiveStreamService;

    LiveStreamState state = LiveStreamState.CLOSE;
    LiveStreamDialogPagerFragment dialogPagerFragment;

    CompositeDisposable disposables;

    Subject<LiveStreamState> liveStreamStateDisposable = PublishSubject.create();
    Subject<Integer> viewNumberDisposable = PublishSubject.create();
    Subject<ListCommentBean> socketDisposable = PublishSubject.create();

    public static void launch(AppCompatActivity activity, @UserLiveStreamService.Mode int mode, @UserLiveStreamService.TypeView int typeView) {
        Bundle bundle = new Bundle();
        bundle.putInt(BUNDLE_MODE, mode);
        bundle.putInt(BUNDLE_TYPE_VIEW, typeView);
        Intent intent = new Intent();
        intent.setClass(activity, LiveStreamActivity.class);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
    }

    public static void launch(AppCompatActivity activity, @UserLiveStreamService.Mode int mode, String filePath, @UserLiveStreamService.TypeView int typeView) {
        Bundle bundle = new Bundle();
        bundle.putInt(BUNDLE_MODE, mode);
        bundle.putString(BUNDLE_FILE_PATH, filePath);
        bundle.putInt(BUNDLE_TYPE_VIEW, typeView);
        Intent intent = new Intent();
        intent.setClass(activity, LiveStreamActivity.class);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
    }

    public static void launch(AppCompatActivity activity, @UserLiveStreamService.Mode int mode, @Nullable String buzzId, @Nullable String streamId, @Nullable String thumbnail) {
        Bundle bundle = new Bundle();
        bundle.putInt(BUNDLE_MODE, mode);
        bundle.putString(BUNDLE_BUZZ_ID, buzzId);
        bundle.putString(BUNDLE_STREAM_ID, streamId);
        bundle.putString(BUNDLE_THUMBNAIL, thumbnail);
        Intent intent = new Intent();
        intent.setClass(activity, LiveStreamActivity.class);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        // remove title
        // Set window styles for fullscreen-window size. Needs to be done before
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_live_stream;
    }

    @Override
    public void onCreateView(View rootView) {
        getLiveStreamComponent().inject(this);

        disposables = new CompositeDisposable();
        gson = new Gson();

        // Create video renderers.
        rootEglBase = EglBase.create();

        mediaConnectionEvents = mMediaConnection;
        mediaConnectionEvents.setEvents(this);

        //setMode
        mUserLiveStreamService.mode = getIntent().getIntExtra(BUNDLE_MODE, UserLiveStreamService.Mode.CHAT);

        //If mode is a view, then set the roomHash to streamId
        if (mUserLiveStreamService.mode == UserLiveStreamService.Mode.VIEW) {

            remoteVideoContainer.setVisibility(View.VISIBLE);
            buzzId = getIntent().getStringExtra(BUNDLE_BUZZ_ID);
            streamID = getIntent().getStringExtra(BUNDLE_STREAM_ID);
            thumbnail = getIntent().getStringExtra(BUNDLE_THUMBNAIL);

            mUserLiveStreamService.roomHash = streamID;
            mUserLiveStreamService.buzzId = buzzId;

            mThumbnail.setVisibility(View.VISIBLE);
            ImagesUtils.loadImageLiveStream(thumbnail, mThumbnail);

            remoteVideoView.init(rootEglBase.getEglBaseContext(), null);
            remoteVideoView.setEnableHardwareScaler(true /* enabled */);
            remoteVideoView.setZOrderMediaOverlay(true);
            remoteVideoView.setKeepScreenOn(true);
        } else {
            typeView = getIntent().getIntExtra(BUNDLE_TYPE_VIEW, UserLiveStreamService.TypeView.STREAM_CAMERA);
            if (typeView == UserLiveStreamService.TypeView.STREAM_FILE) {
                filePath = getIntent().getStringExtra(BUNDLE_FILE_PATH);
                localVideoView.setMirror(false);
            }
            mUserLiveStreamService.typeView = typeView;

            localVideoView.setVisibility(View.VISIBLE);
            localVideoView.init(rootEglBase.getEglBaseContext(), null);
            localVideoView.setMirror(mediaConnectionEvents.isFrontFacing());
            localVideoView.setFpsReduction(VIDEO_FPS);
            localVideoView.setEnableHardwareScaler(true /* enabled */);
            localVideoView.setZOrderMediaOverlay(true);
            localVideoView.setKeepScreenOn(true);
        }

        scalingType = mUserLiveStreamService.mode == UserLiveStreamService.Mode.VIEW ? RendererCommon.ScalingType.SCALE_ASPECT_FIT : RendererCommon.ScalingType.SCALE_ASPECT_BALANCED;
    }

    @Override
    public void onViewReady() {
//        cpuMonitor = new CpuMonitor(this);
        dialogPagerFragment = LiveStreamDialogPagerFragment.newInstance(mUserLiveStreamService.mode);
        //LiveStreamDialogPagerFragment displayed at the top of the page has reached a variety of different interactive needs
        dialogPagerFragment.show(getSupportFragmentManager(), LiveStreamDialogPagerFragment.TAG);
//        dialogPagerFragment.setCpuMonitor(cpuMonitor);

        mediaConnectionEvents.init(this, rootEglBase.getEglBaseContext());

        onObservablePeer();

        onObservableWsSocketRTC();
    }

    @Override
    public void onStart(View viewRoot) {
        super.onStart(viewRoot);
        if (disposables == null)
            disposables = new CompositeDisposable();
    }

    @Override
    public void onResume(View viewRoot) {
        super.onResume(viewRoot);

        if (mUserLiveStreamService.buzzId != null && !mUserLiveStreamService.buzzId.isEmpty()) // Reconnect room
            getPresenter().sendBuzzJoin(new JoinBuzzRequest(UserPreferences.getInstance().getUserId(), mUserLiveStreamService.buzzId, UserPreferences.getInstance().getToken()));

        if (mediaConnectionEvents != null) {
            if (state == LiveStreamState.JOINED) { //If you are in the process of live stream, you will not start the view again
                onStartVideo();
            } else {
                // Create and audio manager that will take care of audio routing,
                // audio modes, audio device enumeration etc.
                audioManager = AppRTCAudioManager.create(this, new Runnable() {
                    // This method will be called each time the audio state (number and
                    // type of devices) has been changed.
                    @Override
                    public void run() {
                        onAudioManagerChangedState();
                    }
                });

                // Store existing audio settings and change audio mode to
                // MODE_IN_COMMUNICATION for best possible VoIP performance.
                Log.d(TAG, "Initializing the audio manager...");
                audioManager.init();

                onStartVideo();
            }
        }
//
//        if (cpuMonitor != null)
//            cpuMonitor.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
//        if (cpuMonitor != null)
//            cpuMonitor.pause();
        if (mediaConnectionEvents != null) {
            mediaConnectionEvents.stopVideo();
            mediaConnectionEvents.sendStatus(MediaConnection.StreamStatus.PAUSE);
        }
    }

    @Override
    protected void onDestroy() {
        disconnect();
        disconnectFromRoom();
        if (mUserLiveStreamService != null)
            mUserLiveStreamService = null;
//        if (cpuMonitor != null) {
//            cpuMonitor = null;
//        }
        super.onDestroy();
//        android.os.Process.killProcess(android.os.Process.myPid());
        // Restore Audio
    }

    // Disconnect from remote resources, dispose of local resources, and exit.
    private void disconnect() {

        if (disposables != null) {
            disposables.isDisposed();
            disposables = null;
        }

        if (logToast != null) {
            logToast.cancel();
            logToast = null;
        }

        if (localVideoView != null) {
            localVideoView.release();
            localVideoView = null;
        }
        if (videoFileRenderer != null) {
            videoFileRenderer.release();
            videoFileRenderer = null;
        }

        if (remoteVideoView != null) {
            remoteVideoView.release();
            remoteVideoView = null;
        }

        if (rootEglBase != null) {
            rootEglBase.release();
            rootEglBase = null;
        }

        if (audioManager != null) {
            audioManager.close();
            audioManager = null;
        }

        if (mediaConnectionEvents != null) {
            mediaConnectionEvents.disconnect();
            mediaConnectionEvents.release();
            mediaConnectionEvents.stopLiveStream();
        }

        state = LiveStreamState.CLOSE;
    }

    private void disconnectFromRoom() {
        if (mediaConnectionEvents != null) {
            mediaConnectionEvents.disconnectFromRoom();
            mMediaConnection = null;
        }
    }

    @Override
    public void onBackPressed() {
        if (this.state != LiveStreamState.CLOSE && this.mUserLiveStreamService.mode == UserLiveStreamService.Mode.CHAT) {
            leave();
        } else {
            finish();
        }
    }

    // Log |msg| and Toast about it.
    private void logAndToast(String msg) {
        Log.d(TAG, msg);
        if (logToast != null) {
            logToast.cancel();
        }
        logToast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
        logToast.show();
    }

    //----------------------------------------------------------------
    //------------------------- Click listener -----------------------
    //----------------------------------------------------------------
    @OnClick(R.id.activity_live_stream_delete_buzz)
    void onDeleteBuzz() {
        new DialogMaterial.Builder(this).setStyle(Style.HEADER_WITH_NOT_HEADER)
                .setContent(R.string.do_you_want_remove_video)
                .onPositive(R.string.common_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).onNegative(R.string.common_no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).show();
    }

    @OnClick(R.id.activity_live_stream_post_status)
    void onPostStatus() {
        mediaConnectionEvents.displaysOnTheTimeline();
        disconnectFromRoom();
        requestBuzzDetail(this.mUserLiveStreamService.buzzId);
    }

    //----------------------------------------------------------------
    //------------------------- Video --------------------------------
    //----------------------------------------------------------------

    private void requestBuzzDetail(String buzzId) {
        UserPreferences userPreference = new UserPreferences();
        BuzzDetailRequest buzzDetailRequest = new BuzzDetailRequest(userPreference.getToken(), buzzId);
        getPresenter().getTimelineDetail(buzzDetailRequest);
    }

    public static final class MockTextureListener
            implements SurfaceTextureHelper.OnTextureFrameAvailableListener {
        int oesTextureId;
        float[] transformMatrix;
        private boolean hasNewFrame = false;
        // Thread where frames are expected to be received on.
        private final Thread expectedThread;

        MockTextureListener() {
            this.expectedThread = null;
        }

        MockTextureListener(Thread expectedThread) {
            this.expectedThread = expectedThread;
        }

        @Override
        public synchronized void onTextureFrameAvailable(
                int oesTextureId, float[] transformMatrix, long timestampNs) {
            if (expectedThread != null && Thread.currentThread() != expectedThread) {
                throw new IllegalStateException("onTextureFrameAvailable called on wrong thread.");
            }
            this.oesTextureId = oesTextureId;
            this.transformMatrix = transformMatrix;
            hasNewFrame = true;
            notifyAll();
        }

        /**
         * Wait indefinitely for a new frame.
         */
        public synchronized void waitForNewFrame() throws InterruptedException {
            while (!hasNewFrame) {
                wait();
            }
            hasNewFrame = false;
        }

        /**
         * Wait for a new frame, or until the specified timeout elapses. Returns true if a new frame was
         * received before the timeout.
         */
        public synchronized boolean waitForNewFrame(final long timeoutMs) throws InterruptedException {
            final long startTimeMs = SystemClock.elapsedRealtime();
            long timeRemainingMs = timeoutMs;
            while (!hasNewFrame && timeRemainingMs > 0) {
                wait(timeRemainingMs);
                final long elapsedTimeMs = SystemClock.elapsedRealtime() - startTimeMs;
                timeRemainingMs = timeoutMs - elapsedTimeMs;
            }
            final boolean didReceiveFrame = hasNewFrame;
            hasNewFrame = false;
            return didReceiveFrame;
        }
    }

    private void onStartVideo() {
        if (mUserLiveStreamService.mode == UserLiveStreamService.Mode.VIEW) {
            mediaConnectionEvents.stopVideo();
        } else {
            LogUtils.i(TAG, "type view: ----> " + typeView);
            if (typeView == UserLiveStreamService.TypeView.STREAM_FILE) {
                // Create SurfaceTextureHelper and listener.
                final SurfaceTextureHelper surfaceTextureHelper =
                        SurfaceTextureHelper.create(TAG, rootEglBase.getEglBaseContext());

                final MockTextureListener listener = new MockTextureListener();
                surfaceTextureHelper.startListening(listener);

                mediaConnectionEvents.startVideo(videoSizeMap.get(currentVideoSize), filePath, surfaceTextureHelper, VIDEO_FPS)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<MediaStream>() {
                            @Override
                            public void accept(MediaStream stream) throws Exception {
                                final org.webrtc.MediaStream rtcStream = stream.rtcMediaStream();

                                if (!rtcStream.videoTracks.isEmpty()) {
                                    VideoTrack videoTrack = rtcStream.videoTracks.getLast();
                                    videoTrack.setEnabled(true);
                                    if (mUserLiveStreamService.mode == UserLiveStreamService.Mode.VIEW) {
                                        videoTrack.addRenderer(new VideoRenderer(remoteVideoView));
                                    } else {
                                        videoTrack.addRenderer(new VideoRenderer(localVideoView));
                                    }
                                }

                                updateVideoView(stream);
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                throwable.printStackTrace();
                                Toast toast = Toast.makeText(LiveStreamActivity.this, "Phát video trực tuyến có vấn đề vui lòng thử lại", Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.TOP, 0, 200);
                                toast.show();
                                displayLeaveLiveStream();
                            }
                        });
            } else {
                mediaConnectionEvents.startVideo(videoSizeMap.get(currentVideoSize), VIDEO_FPS)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<MediaStream>() {
                            @Override
                            public void accept(MediaStream stream) throws Exception {
                                final org.webrtc.MediaStream rtcStream = stream.rtcMediaStream();

                                if (!rtcStream.videoTracks.isEmpty()) {
                                    VideoTrack videoTrack = rtcStream.videoTracks.getLast();
                                    videoTrack.setEnabled(true);
                                    if (mUserLiveStreamService.mode == UserLiveStreamService.Mode.VIEW) {
                                        videoTrack.addRenderer(new VideoRenderer(remoteVideoView));
                                    } else {
                                        videoTrack.addRenderer(new VideoRenderer(localVideoView));
                                    }
                                }

                                updateVideoView(stream);

                                if (isShowLog)
                                    mediaConnectionEvents.enableStatsEvents(STAT_CALLBACK_PERIOD)
                                            .subscribeOn(Schedulers.io())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe(new Consumer<StatsReport[]>() {
                                                @Override
                                                public void accept(StatsReport[] statsReports) throws Exception {
                                                    onPeerConnectionStatsReady(statsReports);
                                                }
                                            });
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                throwable.printStackTrace();
                                Toast toast = Toast.makeText(LiveStreamActivity.this, "Phát video trực tuyến có vấn đề vui lòng thử lại", Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.TOP, 0, 200);
                                toast.show();
                                displayLeaveLiveStream();
                            }
                        });
            }
        }
    }

    private void onAudioManagerChangedState() {
        // TODO(nankai): disable video if AppRTCAudioManager.AudioDevice.EARPIECE
        // is active.
    }

    @UiThread
    private void updateVideoView(MediaStream stream) {
        if (mUserLiveStreamService.mode == UserLiveStreamService.Mode.VIEW) {
            remoteVideoContainer.setVisibility(View.VISIBLE);
            remoteVideoView.setScalingType(scalingType);
            remoteVideoView.setMirror(false);
            remoteVideoView.requestLayout();
        } else {
            localVideoContainer.setVisibility(View.VISIBLE);
            localVideoView.setScalingType(scalingType);
            if (typeView == UserLiveStreamService.TypeView.STREAM_FILE)
                localVideoView.setMirror(false);
            else
                localVideoView.setMirror(stream.isFrontFacing());
            localVideoView.requestLayout();
        }
    }

    //----------------------------------------------------------------
    //------------------ Receiver Message room -----------------------
    //----------------------------------------------------------------

    private void onConnect(MediaConnection.ConnectState state) {
        Log.i(TAG, "connect observable. state:" + state);

        if (state == MediaConnection.ConnectState.CONNECTED) {
            //TODO update camera
        } else if (state == MediaConnection.ConnectState.CLOSE) {
            liveStreamStateDisposable.onNext(LiveStreamState.CLOSE);
        } else {
            String message = null;
            if (state == MediaConnection.ConnectState.DENIED) {
                message = "The connection to the media server was refused.";
            }
            if (state == MediaConnection.ConnectState.ERROR) {
                message = "An error occurred while connecting to the media server";
            }
            if (message != null) {
                Toast toast = Toast.makeText(LiveStreamActivity.this, message, Toast.LENGTH_LONG);
                toast.setGravity(Gravity.TOP, 0, 200);
                toast.show();
            }
            //TODO update camera
            liveStreamStateDisposable.onNext(LiveStreamState.ERROR);
        }
    }

    /*-----------------------------------------------------------------------------------------------------*/
    /*----------------------------------- Socket ----------------------------------------------------------*/
    /*-----------------------------------------------------------------------------------------------------*/
    @Override
    public void onReceiveSocket(SocketEvent socketEvent) {
        super.onReceiveSocket(socketEvent);

        if (state == LiveStreamState.JOINED)
            if (socketEvent.getEventType() == EVENT_AUTH_SUCCESS) {
                if (mUserLiveStreamService != null && mUserLiveStreamService.buzzId != null && !mUserLiveStreamService.buzzId.isEmpty())
                    getPresenter().sendBuzzJoin(new JoinBuzzRequest(UserPreferences.getInstance().getUserId(), mUserLiveStreamService.buzzId, UserPreferences.getInstance().getToken()));
            } else if (socketEvent.getEventType() == SocketEvent.EVENT_RECEIVE) {

                if (getState() == LiveStreamActivityListener.LiveStreamState.JOINED) {
                    if (socketEvent.getMessage() == null) {
                        LogUtils.w(TAG, "Socket message is null!");
                        return;
                    }

                    if (socketEvent.getMessage().getRawText() == null) {
                        LogUtils.w(TAG, "Socket message raw is null!");
                        return;
                    }

                    switch (socketEvent.getMessage().getMessageType()) {
                        case Constants.SOCKET_BUZZ_JOIN:
                            JoinBuzzResponse joinBuzzResponse = gson.fromJson(socketEvent.getMessage().getRawText(), JoinBuzzResponse.class);
                            if (joinBuzzResponse.buzzId.equals(mUserLiveStreamService.buzzId)) {
                                //TODO
                                LogUtils.i(TAG, "Join buzz response code: " + joinBuzzResponse.code);
                            }
                            break;
                        case Constants.SOCKET_BUZZ_CMT:
                        case Constants.SOCKET_BUZZ_SUB_CMT:
                            if (!Utils.isEmptyOrNull(socketEvent.getMessage().getRawText())) {
                                ListCommentBean bean = gson.fromJson(socketEvent.getMessage().getRawText(), ListCommentBean.class);
                                if (bean != null
                                        && mUserLiveStreamService != null
                                        && mUserLiveStreamService.buzzId != null
                                        && bean.buzzId.equals(mUserLiveStreamService.buzzId)) {

                                    socketDisposable.onNext(bean);
                                }
                            }
                            break;
                    }
                }
            }
    }

    @Override
    public void onShowNotification(NotificationAps notifyMessage) {
        if (notifyMessage == null) {
            LogUtils.w(TAG, "Can't show notification using socket because NotificationAps is null");
            return;
        }

        int type = notifyMessage.data.notiType;

        switch (type) {
            case NotificationType.NOTI_LIKE_BUZZ:
            case NotificationType.NOTI_LIKE_OTHER_BUZZ:
            case NotificationType.NOTI_COMMENT_BUZZ:
            case NotificationType.NOTI_COMMENT_OTHER_BUZZ:
            case NotificationType.NOTI_APPROVED_BUZZ:
            case NotificationType.NOTI_FAVORITED_CREATE_BUZZ:
            case NotificationType.NOTI_REPLY_YOUR_COMMENT:
                if (mUserLiveStreamService == null || mUserLiveStreamService.buzzId == null || !notifyMessage.data.buzzid.equals(mUserLiveStreamService.buzzId)) {
                    super.onShowNotification(notifyMessage);
                } else {
                    LogUtils.w(TAG, "Do not display the notification because buzz_id is the same as the buzz_id of the current ic_comment screen.!");
                }
                break;
            default:
                super.onShowNotification(notifyMessage);
        }
    }

    //-------------------------- ICE & Peer Subject -----------------------------//
    private void onObservablePeer() {

        Disposable iceConnection = mediaConnectionEvents.getAddStreamSubject()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<MediaStream>() {
                    @Override
                    public void accept(MediaStream stream) throws Exception {
                        onConnect(MediaConnection.ConnectState.CONNECTED);
                    }
                });

        disposables.add(iceConnection);

        Disposable addStream = mediaConnectionEvents.getAddStreamSubject()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<MediaStream>() {
                    @Override
                    public void accept(MediaStream stream) throws Exception {
                        final org.webrtc.MediaStream rtcStream = stream.rtcMediaStream();
                        if (!rtcStream.videoTracks.isEmpty()) {
                            VideoTrack localVideoTrack = rtcStream.videoTracks.getLast();
                            localVideoTrack.setEnabled(true);
                            if (mUserLiveStreamService.mode == UserLiveStreamService.Mode.VIEW) {
                                localVideoTrack.addRenderer(new VideoRenderer(remoteVideoView));
                            } else {
                                localVideoTrack.addRenderer(new VideoRenderer(localVideoView));
                            }
                        }
                        updateVideoView(stream);

                        if (isShowLog)
                            mediaConnectionEvents.enableStatsEvents(STAT_CALLBACK_PERIOD)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Consumer<StatsReport[]>() {
                                        @Override
                                        public void accept(StatsReport[] statsReports) throws Exception {
                                            onPeerConnectionStatsReady(statsReports);
                                        }
                                    });
                    }
                });

        disposables.add(addStream);

        Disposable removeStream = mediaConnectionEvents.getRemoveStreamSubject()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<MediaStream>() {
                    @Override
                    public void accept(MediaStream stream) throws Exception {
                        final org.webrtc.MediaStream rtcStream = stream.rtcMediaStream();
                        if (rtcStream != null && rtcStream.videoTracks != null)
                            if (!rtcStream.videoTracks.isEmpty()) {
                                final VideoTrack videoTrack = rtcStream.videoTracks.getLast();
                                videoTrack.setEnabled(false);
                                if (mUserLiveStreamService.mode == UserLiveStreamService.Mode.VIEW) {
                                    videoTrack.removeRenderer(new VideoRenderer(remoteVideoView));
                                } else {
                                    videoTrack.removeRenderer(new VideoRenderer(localVideoView));
                                }
                            }
                    }
                });

        disposables.add(removeStream);


        Disposable peerSignalingError = mediaConnectionEvents.getPeerErrorSubject()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String ms) throws Exception {
                        LogUtils.e(TAG, "onSignalingError onError. " + ms);
                        onConnect(MediaConnection.ConnectState.ERROR);
                    }
                });

        disposables.add(peerSignalingError);

        Disposable refreshToken = getModulesCommonComponent().onRefreshTokenSubject().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        if (LiveStreamActivity.this.mUserLiveStreamService.buzzId != null && !LiveStreamActivity.this.mUserLiveStreamService.buzzId.isEmpty())
                            getPresenter().sendBuzzJoin(new JoinBuzzRequest(UserPreferences.getInstance().getUserId(), LiveStreamActivity.this.mUserLiveStreamService.buzzId, UserPreferences.getInstance().getToken()));
                    }
                });
        disposables.add(refreshToken);
    }

    // --------------------------------------------------------------------
    // WebSocketRTCEvents interface implementation.
    // parameters, retrieves room parameters and connect to WebSocket server.
    // return data message webSocket
    //-------------------------- receive message socket live stream -----------------------
    private void onObservableWsSocketRTC() {

        Disposable wsClientDisposable = mediaConnectionEvents.getWebSocketRTCSubject()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<JSONObject>() {
                    @Override
                    public void accept(JSONObject json) throws Exception {

                        @WebSocketRTCState String type = json.getString("type");

                        if (type == null) {
                            return;
                        }
                        LogUtils.i(TAG, "|---------------- " + type.toUpperCase() + " -----------------");
                        LogUtils.i(TAG, "| Message: " + json.toString());

                        switch (type) {
                            case WebSocketRTCState.LIVE_STREAM_OVER:
                                onReceiveMessageOver();
                                break;
                            case WebSocketRTCState.LIVE_STREAM_JOIN_ME:
                                onReceiveMessageJoinMe(json);
                                break;
                            case WebSocketRTCState.LIVE_STREAM_OFFER:
                                onReceiveMessageOffer(json);
                                break;
                            case WebSocketRTCState.LIVE_STREAM_J0INED:
                                onReceiveMessageJoined(json);
                                break;
                            case WebSocketRTCState.LIVE_STREAM_VIEW:
                                onReceiveMessageViews(json);
                                break;
                            case WebSocketRTCState.LIVE_STREAM_STATUS:
                                onReceiveMessageStatus(json);
                                break;
                            case WebSocketRTCState.LIVE_STREAM_CLOSE:
                                int code = 0;
                                if (json.has("code"))
                                    code = json.getInt("code");
                                if (code == 3) {// invalid token
                                    onReceiveMessageTokenInvalid();
                                } else {
                                    onReceiveMessageClose();
                                }
                                break;
                        }
                    }
                });

        disposables.add(wsClientDisposable);
    }

    /**
     * server send over
     */
    private void onReceiveMessageOver() throws JSONException {
        onMessageRoom(WebSocketRTCState.LIVE_STREAM_OVER);
        onConnect(MediaConnection.ConnectState.DENIED);
        mediaConnectionEvents.disconnect();
    }

    /**
     * server send joinme
     */
    private void onReceiveMessageJoinMe(JSONObject json) throws JSONException {
        if (mUserLiveStreamService.mode == UserLiveStreamService.Mode.CHAT) {
            onMessageRoom(WebSocketRTCState.LIVE_STREAM_JOIN_ME);
            mUserLiveStreamService.buzzId = json.getJSONObject("data").getString("buzz_id");
        }
        mediaConnectionEvents.sendJoin();
    }

    /**
     * server send offer
     */
    private void onReceiveMessageOffer(JSONObject json) throws JSONException {
        if (mUserLiveStreamService.mode == UserLiveStreamService.Mode.CHAT)
            onMessageRoom(WebSocketRTCState.LIVE_STREAM_OFFER);
        String sdp = json.getString("sdp");
        if (sdp != null) {
            mediaConnectionEvents.receiveOffer(sdp);
        }
    }

    /**
     * server send joined
     */
    private void onReceiveMessageJoined(JSONObject json) throws JSONException {
        if (mUserLiveStreamService.mode == UserLiveStreamService.Mode.CHAT) {
            mUserLiveStreamService.roomHash = json.getString("room");
        }
        int seconds = json.getInt("duration");
        mUserLiveStreamService.startTime = seconds;
        onMessageRoom(WebSocketRTCState.LIVE_STREAM_J0INED);
    }

    /**
     * server send views
     */
    private void onReceiveMessageViews(JSONObject json) throws JSONException {
        mUserLiveStreamService.viewNumber = json.getInt("currentViews");
        onMessageRoom(WebSocketRTCState.LIVE_STREAM_VIEW);
    }

    private void onReceiveMessageStatus(JSONObject json) throws JSONException {
        String status = json.getString("status");
        if (status.trim().toLowerCase().equals("pause")) {
            statusPause.setVisibility(View.VISIBLE);
        }
    }

    /**
     * server send close
     */
    private void onReceiveMessageClose() throws JSONException {
        if (mUserLiveStreamService.mode == UserLiveStreamService.Mode.VIEW)
            onMessageRoom(WebSocketRTCState.LIVE_STREAM_CLOSE);
    }

    /**
     * server send close
     */
    private void onReceiveMessageTokenInvalid() throws JSONException {
        if (UserPreferences.getInstance().isLogin()) {
            final CheckTokenRequest checkTokenRequest = new CheckTokenRequest(UserPreferences.getInstance().getToken());
            getPresenter().checkToken(checkTokenRequest);
        }
    }

    public int getTypeView() {
        return typeView;
    }

    @UiThread
    private void onMessageRoom(@WebSocketRTCState String type) {
        switch (type) {
            case WebSocketRTCState.LIVE_STREAM_OPEN_SOCKET_LIVE_STREAM:
                state = LiveStreamState.OPEN;
                liveStreamStateDisposable.onNext(LiveStreamState.OPEN);
                break;
            case WebSocketRTCState.LIVE_STREAM_JOIN_ME:
                mediaConnectionEvents.checkOrientation();
                break;
            case WebSocketRTCState.LIVE_STREAM_OFFER:
                //TODO
                break;
            case WebSocketRTCState.LIVE_STREAM_J0INED:
                //Connect room chat
                getPresenter().sendBuzzJoin(new JoinBuzzRequest(UserPreferences.getInstance().getUserId(),
                        mUserLiveStreamService.buzzId, UserPreferences.getInstance().getToken()));

                statusPause.setVisibility(View.GONE);
                if (mUserLiveStreamService.mode == UserLiveStreamService.Mode.VIEW)
                    mThumbnail.setVisibility(View.INVISIBLE);

                state = LiveStreamState.JOINED;
                liveStreamStateDisposable.onNext(LiveStreamState.JOINED);
                break;
            case WebSocketRTCState.LIVE_STREAM_VIEW:
                viewNumberDisposable.onNext(mUserLiveStreamService.viewNumber);
                break;
            case WebSocketRTCState.LIVE_STREAM_CLOSE:
                new DialogMaterial.Builder(this)
                        .setStyle(Style.HEADER_WITH_NOT_HEADER)
                        .setContent(R.string.live_stream_close)
                        .onPositive(R.string.common_ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        }).show();
                break;
        }
    }

    //-------------------------- end receive message socket live stream -----------------------

    @Override
    public void onWebSocketClose() {
        onConnect(MediaConnection.ConnectState.CLOSE);
    }

    @Override
    public void onWebSocketDenied() {
        onConnect(MediaConnection.ConnectState.DENIED);
        mediaConnectionEvents.disconnect();
    }

    @Override
    public void onWebSocketError() {
        onConnect(MediaConnection.ConnectState.ERROR);
    }

    @UiThread
    @Override
    public void onSignalingError(final String error) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LogUtils.e(TAG, "onSignalingError onError. " + error);
                onConnect(MediaConnection.ConnectState.ERROR);
            }
        });
    }

    public void onPeerConnectionStatsReady(StatsReport[] reports) {
        LogUtils.i(TAG, "onPeerConnectionStatsReady: " + state);
        if (state == LiveStreamState.JOINED)
            dialogPagerFragment.updateEncoderStatistics(reports);
    }

    /*-----------------------------------------------------------------------------------------------------*/
    /*-----------------------------------Live stream listener----------------------------------------------*/
    /*-----------------------------------------------------------------------------------------------------*/

    @Override
    public Subject<LiveStreamState> getLiveStreamStateDisposable() {
        return liveStreamStateDisposable;
    }

    @Override
    public Subject<Integer> getViewNumberDisposable() {
        return viewNumberDisposable;
    }

    @Override
    public Subject<ListCommentBean> getSocketDisposable() {
        return socketDisposable;
    }

    @Override
    public LiveStreamState getState() {
        return state;
    }

    @Override
    public LiveStreamState setState(LiveStreamState state) {
        if (this.state != state)
            return this.state = state;
        return this.state;
    }

    @Override
    public void connect() {
        LogUtils.i(TAG, "CONNECT live stream ---> Activity\n"
                + "room Hash  : " + mUserLiveStreamService.roomHash + "\n"
                + "user Hash  : " + mUserLiveStreamService.userHash + "\n"
                + "buzzVal    : " + mUserLiveStreamService.buzzVal + "\n"
                + "buzzId     : " + mUserLiveStreamService.buzzId + "\n"
                + "privacy    : " + mUserLiveStreamService.privacy + "\n"
                + "tagList    : " + mUserLiveStreamService.tagList + "\n"
                + "Action     : " + mUserLiveStreamService.mode);
        mediaConnectionEvents.connectToRoom();
    }

    @Override
    public void leave() {
        new DialogMaterial.Builder(this).setStyle(Style.HEADER_WITH_NOT_HEADER)
                .setContent(R.string.do_you_want_close_live_stream)
                .onPositive(R.string.common_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        displayLeaveLiveStream();
                    }
                })
                .onNegative(R.string.common_no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).show();
    }

    @Override
    public void changeCamera() {
        mediaConnectionEvents.changeCamera(videoSizeMap.get(currentVideoSize), VIDEO_FPS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<MediaStream>() {
                    @Override
                    public void accept(MediaStream stream) throws Exception {
                        final org.webrtc.MediaStream rtcStream = stream.rtcMediaStream();
                        if (!rtcStream.videoTracks.isEmpty()) {
                            VideoTrack videoTrack = rtcStream.videoTracks.getLast();
                            videoTrack.setEnabled(true);
                            if (mUserLiveStreamService.mode == UserLiveStreamService.Mode.VIEW) {
                                videoTrack.addRenderer(new VideoRenderer(remoteVideoView));
                            } else {
                                videoTrack.addRenderer(new VideoRenderer(localVideoView));
                            }
                        }

                        updateVideoView(stream);

                        if (isShowLog)
                            mediaConnectionEvents.enableStatsEvents(STAT_CALLBACK_PERIOD)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Consumer<StatsReport[]>() {
                                        @Override
                                        public void accept(StatsReport[] statsReports) throws Exception {
                                            onPeerConnectionStatsReady(statsReports);
                                        }
                                    });
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast toast = Toast.makeText(LiveStreamActivity.this, "Lỗi thay đổi camera", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.TOP, 0, 200);
                        toast.show();
                    }
                });
    }

    private void displayLeaveLiveStream() {
        dialogPagerFragment.dismiss();
        if (state == LiveStreamState.JOINED) {
            disconnect();
            if (this.mUserLiveStreamService.mode == UserLiveStreamService.Mode.CHAT) {
                layoutLiveStreamClose.setVisibility(View.VISIBLE);
            }
        } else {
            disconnect();
            finish();
        }
    }

    /*-----------------------------------------------------------------------------------------------------*/
    /*----------------------------------- Server response -------------------------------------------------*/
    /*-----------------------------------------------------------------------------------------------------*/
    @Override
    public void onTimelineDetail(BuzzDetailResponse response) {
        if (mUserLiveStreamService.mode == UserLiveStreamService.Mode.CHAT) {
            RxEventBus.publish(SubjectCode.SUBJECT_UPDATE_TIMELINE_FROM_LIVE_STREAM, response.data);
            finish();
        }
    }

    @Override
    public void onBuzzListComment(ListCommentResponse response) {
    }

    @Override
    public void onBuzzNotFoundFromNotificationId() {
    }

    @Override
    public void onComplete() {
    }

    @Override
    public void onRefreshToken(CheckTokenResponse checkTokenResponse) {
        if (!Utils.isEmptyOrNull(checkTokenResponse.data.token))
            UserPreferences.getInstance().saveToken(checkTokenResponse.data.token);
    }

    @Override
    public void handleBuzzNotFound(String buzzId) {
        Toast.makeText(getApplicationContext(), getResources().getString(R.string.buzz_item_not_found), Toast.LENGTH_LONG).show();
        finish();
    }

    @Override
    public void shareMediaFailure() {

    }

    @Override
    public void shareMediaSuccess(PostStatusResponse response) {

    }
}
