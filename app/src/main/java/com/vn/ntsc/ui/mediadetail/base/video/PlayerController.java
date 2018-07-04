package com.vn.ntsc.ui.mediadetail.base.video;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.vn.ntsc.R;

import java.util.Formatter;
import java.util.Locale;

/**
 * Controller to manage syncing the ui models with the UI Controls and MediaPlayer.
 * <p/>
 * Note that the ui models have a narrow scope (i.e. chapter list, piece navigation),
 * their interaction is orchestrated by this controller.ø
 * <p/>
 * It's actually a view currently, as is the android MediaController.
 * (which is a bit odd and should be subject to change.)
 */
public final class PlayerController extends FrameLayout implements VideoTouchRoot.OnTouchReceiver, TextureVideoView.VideoController, TextureVideoView.OnPlayStateListener {

    private static final int DEFAULT_TIMEOUT = 5000;
    private static final int DEFAULT_TIME_HIDE = 5000;
    private static final long DEFAULT_TIME_PLAY_BUTTON_HIDE = 1000;

    public static final int DEFAULT_VIDEO_START = 0;

    private static final int FADE_OUT = 1;
    private static final int SHOW_PROGRESS = 2;

    private Context mContext;
    private Player mPlayer;
    private boolean mShowing;
    private boolean mDragging;
    private boolean mLoading;
    private boolean mFirstTimeLoading = true;
    private StringBuilder mFormatBuilder;
    private Formatter mFormatter;

    private View mAnchor;
    private View mRoot;

    private ProgressBar mProgress;
    private TextView mEndTime;
    private TextView mCurrentTime;
    private ImageButton mPauseButton;
    private ImageButton mVolumeButton;
    private ImageButton mFullScreenButton;

    private ProgressBar loadingView;
    private boolean mIsFullScreen;
    private boolean mIsVolumeMute;

    private VisibilityListener mVisibilityListener;
    private VolumeListener mVolumeListener;


    public PlayerController(final Context context) {
        this(context, null);
        mContext = context;
        init();
    }

    private void init() {
        mRoot = makeControllerView();
        LayoutParams frameParams = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );
        addView(mRoot, frameParams);
        mRoot.setVisibility(mShowing ? VISIBLE : INVISIBLE);
        requestLayout();
    }

    public PlayerController(final Context context, final AttributeSet attrs) {
        this(context, attrs, 0);
        mContext = context;
        init();
    }

    public PlayerController(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        init();
    }

    @Override
    public void setMediaPlayer(final Player player) {
        mPlayer = player;
        updatePausePlay();
    }

    @Override
    public void setAnchorView(final View view) {
        mAnchor = view;
    }

    public void pause() {
        if (mPlayer != null) {
            updatePausePlay();
        }
    }



    @SuppressLint("InflateParams")
    private View makeControllerView() {
        LayoutInflater inflate = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflate.inflate(R.layout.view_media_controller, null);
        initControllerView(view);
        return view;
    }

    private void initControllerView(final View v) {
        mPauseButton = (ImageButton) v.findViewById(R.id.view_media_controller_pause);
        mPauseButton.requestFocus();
        mPauseButton.setOnClickListener(mPauseListener);

        mVolumeButton = (ImageButton) v.findViewById(R.id.view_media_controller_time_vollumn);
        mVolumeButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mVolumeListener == null)
                    return;

                mIsVolumeMute = !mIsVolumeMute;
                mVolumeListener.setVolumeMute(mIsVolumeMute);
                mVolumeButton.setImageResource(mIsVolumeMute ? R.drawable.ic_mute : R.drawable.ic_unmute);
            }
        });

        mFullScreenButton = (ImageButton) v.findViewById(R.id.view_media_controller_mode_screen);
        mFullScreenButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mVisibilityListener == null)
                    return;

                mIsFullScreen = !mIsFullScreen;
                mVisibilityListener.onFullScreen(mIsFullScreen);
                mFullScreenButton.setImageResource(mIsFullScreen ? R.drawable.ic_exist_fullscreen : R.drawable.ic_fullscreen);
            }
        });

        mProgress = (SeekBar) v.findViewById(R.id.view_media_controller_progress);
        SeekBar seeker = (SeekBar) mProgress;
        seeker.setOnSeekBarChangeListener(mSeekListener);
        mProgress.setMax(1000);

        mEndTime = (TextView) v.findViewById(R.id.view_media_controller_time);
        mCurrentTime = (TextView) v.findViewById(R.id.view_media_controller_time_current);
        mFormatBuilder = new StringBuilder();
        mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());

        //progressbar
        loadingView = (ProgressBar) v.findViewById(R.id.view_media_controller_loading_view);
    }

    /**
     * Called by ViewTouchRoot on user touches,
     * so we can avoid hiding the ui while the user is interacting.
     */
    @Override
    public void onControllerUiTouched() {
        if (!mShowing) {
            mPauseButton.setVisibility(VISIBLE);
            show();
        }
    }

    /**
     * Show the controller on screen. It will go away
     * automatically after 'timeout' milliseconds of inactivity.
     *
     * @param timeInMilliSeconds The timeout in milliseconds. Use 0 to show
     *                           the controller until hide() is called.
     */
    @Override
    public void show(final int timeInMilliSeconds) {
        if (!mShowing && mAnchor != null) {
            setProgress();

            if (mPauseButton != null) {
                mPauseButton.requestFocus();
            }

            //show state
            mShowing = true;
            mRoot.setVisibility(View.VISIBLE);
        }

        //set icon pause
        updatePausePlay();

        // cause the progress bar to be updated even if mShowing
        // was already true.  This happens, for example, if we're
        // paused with the progress bar showing the user hits play.
        mHandler.sendEmptyMessage(SHOW_PROGRESS);

        Message msg = mHandler.obtainMessage(FADE_OUT);
        if (timeInMilliSeconds != 0) {
            mHandler.removeMessages(FADE_OUT);
            mHandler.sendMessageDelayed(msg, timeInMilliSeconds);
        }
    }

    /**
     * Show the controller on screen. It will go away
     * automatically after the default time of inactivity.
     */
    @Override
    public void show() {
        show(DEFAULT_TIMEOUT);
    }



    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(final Message msg) {
            int pos;
            switch (msg.what) {
                case FADE_OUT:
                    //hide after DEFAULT_TIMEOUT
                    if(mPlayer == null)
                        return;

                    if (mPlayer.isPlaying()) {
                        hide();
                    } else {
                        // re-schedule to check again
                        Message fadeMessage = obtainMessage(FADE_OUT);
                        removeMessages(FADE_OUT);
                        sendMessageDelayed(fadeMessage, DEFAULT_TIMEOUT);
                    }
                    break;
                case SHOW_PROGRESS:
                    pos = setProgress();
                    if (!mDragging && mShowing && mPlayer.isPlaying()) {
                        final Message message = obtainMessage(SHOW_PROGRESS);
                        int timeDelay = 1000 - (pos % 1000);
                        sendMessageDelayed(message, timeDelay);
                    }
                    break;
            }
        }
    };

    /**
     * detach the controller from the screen.
     */
    @Override
    public void hide() {
        if (mAnchor == null) {
            return;
        }

        if (mShowing) {
            try {
                //stop show progress
                mHandler.removeMessages(SHOW_PROGRESS);

                mRoot.setVisibility(View.INVISIBLE);
            } catch (final IllegalArgumentException ex) {
                ex.printStackTrace();
            }
            mShowing = false;
        }
    }

    private String stringForTime(final int timeMs) {
        int totalSeconds = timeMs / 1000;

        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;

        mFormatBuilder.setLength(0);
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }

    //buffer variable to throttle event propagation
    private int lastPlayedSeconds = -1;

    private int setProgress() {
        if (mPlayer == null || mDragging) {
            return 0;
        }
        int position = mPlayer.getCurrentPosition();
        int duration = mPlayer.getDuration();
        //set progerss
        if (mProgress != null) {
            if (duration > 0) {
                // use long to avoid overflow
                long pos = 1000L * position / duration;
                mProgress.setProgress((int) pos);
            }
            int percent = mPlayer.getBufferPercentage();
            mProgress.setSecondaryProgress(percent * 10);
        }

        if (mEndTime != null) {
            mEndTime.setText(stringForTime(duration));
        }
        if (mCurrentTime != null) {
            mCurrentTime.setText(stringForTime(position));
        }

        //save pos play progress
        final int playedSeconds = position / 1000;
        if (lastPlayedSeconds != playedSeconds) {
            lastPlayedSeconds = playedSeconds;
        }
        return position;
    }

    private void updatePausePlay() {
        //update icon nút pause hiện tại
        if (mRoot == null || mPauseButton == null)
            return;

        if(mPlayer == null)
            return;

        if (mPlayer.isPlaying()) {
            mPauseButton.setImageResource(R.drawable.bg_video_pause);
        } else {
            mPauseButton.setImageResource(R.drawable.bg_video_play);
        }
    }

    public void switchPlayPauseVideo() {
        //pause + update icon
        if (mPlayer.isPlaying()) {
            mPlayer.pause();
        } else {
            mPlayer.start();
        }
        updatePausePlay();

        show(DEFAULT_TIMEOUT);
    }

    // There are two scenarios that can trigger the seekbar listener to trigger:
    //
    // The first is the user using the touchpad to adjust the posititon of the
    // seekbar's thumb. In this case onStartTrackingTouch is called followed by
    // a number of onProgressChanged notifications, concluded by onStopTrackingTouch.
    // We're setting the field "mDragging" to true for the duration of the dragging
    // session to avoid jumps in the position in case of ongoing playback.
    //
    // The second scenario involves the user operating the scroll ball, in this
    // case there WON'T BE onStartTrackingTouch/onStopTrackingTouch notifications,
    // we will simply apply the updated position without suspending regular updates.
    private final SeekBar.OnSeekBarChangeListener mSeekListener = new SeekBar.OnSeekBarChangeListener() {
        public void onStartTrackingTouch(final SeekBar bar) {
            show(3600000);


            mDragging = true;

            // By removing these pending progress messages we make sure
            // that a) we won't update the progress while the user adjusts
            // the seekbar and b) once the user is done dragging the thumb
            // we will post one of these messages to the queue again and
            // this ensures that there will be exactly one message queued up.
            mHandler.removeMessages(SHOW_PROGRESS);
        }

        public void onProgressChanged(final SeekBar bar, final int progress, final boolean fromuser) {
            if (!fromuser) {
                // We're not interested in programmatically generated changes to
                // the progress bar's position.
                return;
            }

            long duration = mPlayer.getDuration();
            long newposition = (duration * progress) / 1000L;
            //TODO
            mPlayer.seekTo((int) newposition);
            if (mCurrentTime != null) {
                mCurrentTime.setText(stringForTime((int) newposition));
            }
        }

        public void onStopTrackingTouch(final SeekBar bar) {
            mDragging = false;
            setProgress();
            updatePausePlay();
            show(DEFAULT_TIMEOUT);

            // Ensure that progress is properly updated in the future,
            // the call to show() does not guarantee this because it is a
            // no-op if we are already showing.
            mHandler.sendEmptyMessage(SHOW_PROGRESS);
        }
    };

    @Override
    public void setEnabled(final boolean enabled) {

        if (mPauseButton != null) {
            mPauseButton.setEnabled(enabled);
        }

        if (mProgress != null) {
            mProgress.setEnabled(enabled);
        }

        //enable or disable layout
        super.setEnabled(enabled);
    }

    @Override
    public void onInitializeAccessibilityEvent(final AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
        event.setClassName(PlayerController.class.getName());
    }

    @Override
    public void onInitializeAccessibilityNodeInfo(final AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClassName(PlayerController.class.getName());
    }

    private final OnClickListener mPauseListener = new OnClickListener() {
        public void onClick(final View v) {
            switchPlayPauseVideo();
        }
    };

    @Override
    public void onFirstVideoFrameRendered() {
        mFirstTimeLoading = false;
    }

    @Override
    public void onPlay() {
        // when play
        // hide progress bar,
        hideLoadingView();

        //detach the controller from the screen.
        //hide root after time DEFAULT_TIME_HIDE
        mRoot.postDelayed(new Runnable() {
            @Override
            public void run() {
                hide();
            }
        }, DEFAULT_TIME_HIDE);

    }

    @Override
    public void onBuffer() {
        //if has new buffter
        showLoadingView();
    }


    private void hideLoadingView() {
        loadingView.setVisibility(View.GONE);
        mLoading = false;
    }

    private void showLoadingView() {
        mLoading = true;
        loadingView.setVisibility(View.VISIBLE);
    }

    public void setVisibilityListener(VisibilityListener visibilityListener) {
        mVisibilityListener = visibilityListener;
    }

    public void setVolumeListener(VolumeListener volumeListener) {
        mVolumeListener = volumeListener;
    }

    public void resetFullScreen() {
        mIsFullScreen = false;
        mVisibilityListener.onFullScreen(mIsFullScreen);
        mFullScreenButton.setImageResource(R.drawable.ic_fullscreen);
    }

    public void stopUpdateTrack() {
        //stop show progress
        mHandler.removeMessages(SHOW_PROGRESS);
    }

    public void showBegin() {
        if (!mShowing && mAnchor != null) {

            //request nút pause
            if (mPauseButton != null) {
                mPauseButton.requestFocus();
            }

            //show state
            mShowing = true;
            mRoot.setVisibility(View.VISIBLE);
        }

        //set icon pause
        updatePausePlay();

        // cause the progress bar to be updated even if mShowing
        // was already true.  This happens, for example, if we're
        // paused with the progress bar showing the user hits play.
//        mHandler.sendEmptyMessage(SHOW_PROGRESS);

        mHandler.removeMessages(FADE_OUT);

        showLoadingView();
    }

    /**
     * Called to notify that the control have been made visible or hidden.
     * Implementation might want to show/hide actionbar or do other ui adjustments.
     * <p/>
     * Implementation must be provided via the corresponding setter.
     */
    public interface VisibilityListener {
        void onFullScreen(boolean needFull);
    }

    public interface VolumeListener {
        void setVolumeMute(boolean isMute);
    }

}