package com.vn.ntsc.ui.mediadetail.base.video;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.MediaController;

import com.vn.ntsc.R;
import com.vn.ntsc.ui.mediadetail.base.VideoViewHolder;
import com.vn.ntsc.ui.mediadetail.util.StateMediaPlayer;
import com.vn.ntsc.utils.LogUtils;

import java.io.IOException;

import static com.vn.ntsc.ui.mediadetail.util.StateMediaPlayer.ERROR;
import static com.vn.ntsc.ui.mediadetail.util.StateMediaPlayer.IDLE;
import static com.vn.ntsc.ui.mediadetail.util.StateMediaPlayer.PAUSED;
import static com.vn.ntsc.ui.mediadetail.util.StateMediaPlayer.PLAY_COMPLETE;
import static com.vn.ntsc.ui.mediadetail.util.StateMediaPlayer.PREPARED;
import static com.vn.ntsc.ui.mediadetail.util.StateMediaPlayer.PREPARING;
import static com.vn.ntsc.ui.mediadetail.util.StateMediaPlayer.STARTED;
import static com.vn.ntsc.ui.mediadetail.util.StateMediaPlayer.STOPED;


/**
 * TextureView need @android:hardwareAccelerated="true"
 */
public class TextureVideoView extends TextureView implements MediaController.MediaPlayerControl, Player {
    public static final String TAG = TextureVideoView.class.getName();
    private final Context mContext;
    private SurfaceTexture mSurfaceTexture;

    //a media player has a only a audio session to interact with system audio
    private MediaPlayer mMediaPlayer;
    private int mAudioSession;

    //iteraction with video fragment
    private VideoViewHolder.IteractionVideo mIteractionVideo;

    /**
     * mCurrentState :is a VideoView object's current state.
     */
    @StateMediaPlayer
    private int mCurrentState = IDLE;

    /**
     * mTargetState: is the state that a method caller intends to reach.
     * For instance, regardless the VideoView object's current state,
     * calling pause() intends to bring the object to a target state PAUSE {@link StateMediaPlayer}
     */
    @StateMediaPlayer
    private int mTargetState = IDLE;

    //use to log
    private int mPos;

    //uri video
    private Uri mUri;

    //Holder video size
    private VideoSizeCalculator mVideoSizeCalculator;

    //custom player controller
    private PlayerController mPlayerController;

    //position seek when prepared
    private int mSeekWhenPrepared;

    /**
     * use for streaming video
     * presenter code block get Metadata check mode streaming video not working
     * then default true all flag below
     */
    private boolean mCanPause;
    private boolean mCanSeekBack;
    private boolean mCanSeekForward;

    //percentage loaded buffer
    private int mCurrentBufferPercentage;

    //if load data from resource exist then mCurrentBufferPercentage default = 100
    private boolean isLoadNetwork;

    private AlertDialog errorDialog;

    //state play
    private OnPlayStateListener onPlayStateListener;

    private MediaPlayer.OnPreparedListener mPreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
            mCurrentState = PREPARED;
            //with streaming video, need check
            // Get the capabilities of the player for this stream
            // Metadata data = mp.getMetadata(MediaPlayer.METADATA_ALL,MediaPlayer.BYPASS_METADATA_FILTER);
            //can not get Metadata

            //flag = true because do not unknow get mp.getMetadata
            mCanPause = true;
            mCanSeekBack = true;
            mCanSeekForward = true;

            //enable
            if (mPlayerController != null) {
                mPlayerController.setEnabled(true);
            }

            //set video size real when load done video
            mVideoSizeCalculator.setVideoSize(mp.getVideoWidth(), mp.getVideoHeight());

            LogUtils.d(TAG, "onPrepared(MediaPlayer mp)  ---> mTargetState = " + mTargetState + " ---> mSeekWhenPrepared = " + mSeekWhenPrepared + " -----> mPos = " + mPos);

            // enable and start, show Media
            if (mPlayerController != null) {
                mPlayerController.setEnabled(true);
            }

            //TODO
            int seekToPosition = mSeekWhenPrepared;
            if (seekToPosition != 0) {
                seekTo(seekToPosition);
            }

            if (mTargetState == STARTED) {
                LogUtils.d(TAG, "onPrepared(MediaPlayer mp)  ---> mTargetState == STARTED ---> mPos = " + mPos);
                start();
                //show controller after time PlayerController.DEFAULT_TIMEOUT
                showMediaController();

            } else if (isPausingAt(seekToPosition)) {
                LogUtils.d(TAG, "onPrepared(MediaPlayer mp)  ---> isPausingAt(seekToPosition) ---> mPos = " + mPos);
                start();

                //show controller after time PlayerController.DEFAULT_TIMEOUT
                showMediaController();

            } else {
                LogUtils.d(TAG, "onPrepared(MediaPlayer mp)  ---> NOT START NOT START NOT START NOT START---> mPos = " + mPos);
            }

//            showMediaController();
        }
    };

    private MediaPlayer.OnVideoSizeChangedListener mSizeChangedListener = new MediaPlayer.OnVideoSizeChangedListener() {
        @Override
        public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
            //refresh layout if change size video
            mVideoSizeCalculator.setVideoSize(mp.getVideoWidth(), mp.getVideoHeight());
            if (mVideoSizeCalculator.hasASizeYet()) {
                requestLayout();
            }
        }
    };

    /**
     * when OnBufferingUpdateListener
     */
    private MediaPlayer.OnBufferingUpdateListener mBufferingUpdateListener = new MediaPlayer.OnBufferingUpdateListener() {
        @Override
        public void onBufferingUpdate(MediaPlayer mp, int percent) {
            isLoadNetwork = true;
            mCurrentBufferPercentage = percent;

            LogUtils.d(TAG, "onBufferingUpdate ---> mCurrentBufferPercentage = " + mCurrentBufferPercentage + " mTargetState = " + mTargetState + "---> mPos = " + mPos);
        }
    };

    private MediaPlayer.OnCompletionListener mCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {

            //turn off mode always light screen
            setKeepScreenOn(false);

            mCurrentState = PLAY_COMPLETE;
            mTargetState = PLAY_COMPLETE;

            //hide media, notify completion
            hideMediaController();

            seekTo(0);
            start();
        }
    };

    private MediaPlayer.OnErrorListener mErrorListener = new MediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(MediaPlayer mp, int frameworkError, int implError) {
            LogUtils.d(TAG, "onError -------> frameworkError = " + frameworkError + " mPos = " + mPos);

            if (mCurrentState == ERROR) {
                return true;
            }

            mCurrentState = ERROR;
            mTargetState = ERROR;
            hideMediaController();

            //show dialog this error
            if (mIteractionVideo != null) {
                if (mIteractionVideo.isAreadyVisibleToUser())
                    showDialogError(frameworkError);
                else {
                    //reload
                    prepareVideo();
                }
            }
            return true;
        }
    };

    private MediaPlayer.OnInfoListener mInfoListener = new MediaPlayer.OnInfoListener() {
        @Override
        public boolean onInfo(MediaPlayer mp, int what, int extra) {
            LogUtils.d(TAG, "onInfo -------> what = " + what + " mPos = " + mPos);

            if (onPlayStateListener == null) {
                return false;
            }

            if (MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START == what) {
                //first video frame for rendering
                LogUtils.i(TAG, getResources().getString(R.string.media_info_video_rending_start));
                onPlayStateListener.onFirstVideoFrameRendered();
                onPlayStateListener.onPlay();
            }

            if (MediaPlayer.MEDIA_INFO_BUFFERING_START == what) {
                //when is buffering video
                onPlayStateListener.onBuffer();
                LogUtils.i(TAG, getResources().getString(R.string.media_info_buffering_start));
            }

            if (MediaPlayer.MEDIA_INFO_BUFFERING_END == what) {
                //when ok buffered video
                onPlayStateListener.onPlay();
                LogUtils.i(TAG, getResources().getString(R.string.media_info_buffering_end));
            }

            if (MediaPlayer.MEDIA_INFO_UNKNOWN == what) {
                LogUtils.i(TAG, getResources().getString(R.string.video_media_info_unknow));
            }

            if (MediaPlayer.MEDIA_INFO_VIDEO_TRACK_LAGGING == what) {
                LogUtils.i(TAG, getResources().getString(R.string.video_media_info_video_track_lagging));
            }

            if (MediaPlayer.MEDIA_INFO_BAD_INTERLEAVING == what) {
                LogUtils.i(TAG, getResources().getString(R.string.media_info_bad_interleaving));
            }

            if (MediaPlayer.MEDIA_INFO_NOT_SEEKABLE == what) {
                LogUtils.i(TAG, getResources().getString(R.string.media_info_not_seekable));
            }

            if (MediaPlayer.MEDIA_INFO_METADATA_UPDATE == what) {
                LogUtils.i(TAG, getResources().getString(R.string.media_info_meta_data_update));
            }

            if (MediaPlayer.MEDIA_INFO_UNSUPPORTED_SUBTITLE == what) {
                LogUtils.i(TAG, getResources().getString(R.string.media_info_unsupported_title));
            }

            if (MediaPlayer.MEDIA_INFO_SUBTITLE_TIMED_OUT == what) {
                LogUtils.i(TAG, getResources().getString(R.string.media_info_subtitle_time_out));
            }

            return true;
        }
    };

    /**
     * instance
     *
     * @param context
     */
    public TextureVideoView(Context context) {
        super(context);
        mContext = context;
        initVideoView();
    }

    public TextureVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initVideoView();
    }

    public TextureVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initVideoView();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public TextureVideoView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mContext = context;
        initVideoView();
    }

    /**
     * life cycle
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //measure calculate real size video
        VideoSizeCalculator.Dimens dimens = mVideoSizeCalculator.measure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(dimens.getWidth(), dimens.getHeight());
    }

    /**
     * set init access with device, ex trackball
     *
     * @param event
     */
    @Override
    public void onInitializeAccessibilityEvent(final AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
        event.setClassName(TextureVideoView.class.getName());
    }

    @Override
    public void onInitializeAccessibilityNodeInfo(final AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClassName(TextureVideoView.class.getName());
    }

    /**
     * func
     */

    public void setVideo(final Uri uri, final int seekInSeconds) {
        setVideoURI(uri, seekInSeconds);
    }

    public void setVideo(final String url, final int seekInSeconds) {
        setVideoURI(Uri.parse(url), seekInSeconds);
    }

    private void setVideoURI(final Uri uri, final int seekInSeconds) {
        mUri = uri;
//        mUri = Uri.parse("http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4");
        mSeekWhenPrepared = seekInSeconds * 1000;
        LogUtils.d(TAG, "setVideoURI ---------> prepareVideo() -------> mPos = " + mPos);
        prepareVideo();
        requestLayout();
        invalidate();
    }

    private void initVideoView() {
        //calculate size real video
        //init 0 , 0
        mVideoSizeCalculator = new VideoSizeCalculator();
        mVideoSizeCalculator.setVideoSize(0, 0);

        //setup a setSurfaceTextureListener to detect surface activity
        setSurfaceTextureListener(mSurfaceTextureListener);

        //set focus and focus in several mode, ex trackball,....
        setFocusable(true);
        setFocusableInTouchMode(true);
        requestFocus();

        //state life cycle media player
        mCurrentState = IDLE;
        mTargetState = IDLE;
    }

    private void prepareVideo() {
        //TODO
        if (mUri == null || mSurfaceTexture == null) {
            // not ready for playback just yet, will try again later
            return;
        }

        try {

            LogUtils.d(TAG, "onSurfaceTextureAvailable ---------> prepareVideo() ------> DONE -------> mPos = " + mPos);

            //release video
            // we shouldn't clear the target state, because somebody might have
            // called start() previously
            release(false);
            mMediaPlayer = new MediaPlayer();

            //set again audio session
            if (mAudioSession != 0) {
                mMediaPlayer.setAudioSessionId(mAudioSession);
            } else {
                mAudioSession = mMediaPlayer.getAudioSessionId();
            }

            //set listener
            mMediaPlayer.setOnPreparedListener(mPreparedListener);
            mMediaPlayer.setOnVideoSizeChangedListener(mSizeChangedListener);
            mMediaPlayer.setOnCompletionListener(mCompletionListener);
            mMediaPlayer.setOnErrorListener(mErrorListener);
            mMediaPlayer.setOnInfoListener(mInfoListener);
            mMediaPlayer.setOnBufferingUpdateListener(mBufferingUpdateListener);
            mCurrentBufferPercentage = 0;
            mMediaPlayer.setDataSource(mContext, mUri);
            mMediaPlayer.setSurface(new Surface(mSurfaceTexture));
            mMediaPlayer.prepareAsync();

            mCurrentState = PREPARING;
        } catch (IOException e) {
            mCurrentState = ERROR;
            mTargetState = ERROR;
            mErrorListener.onError(mMediaPlayer, MediaPlayer.MEDIA_ERROR_UNKNOWN, 0);
        } catch (IllegalStateException ex) {
            mCurrentState = ERROR;
            mTargetState = ERROR;
            mErrorListener.onError(mMediaPlayer, MediaPlayer.MEDIA_ERROR_UNKNOWN, 0);
        }
    }

    public void release(boolean clearTargetState) {
        if (mMediaPlayer != null) {
            LogUtils.d(TAG, "release(boolean clearTargetState) -------> mPos = " + mPos);
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
            mCurrentState = IDLE;
            if (clearTargetState) {
                mTargetState = IDLE;
            }
        }
    }

    private SurfaceTextureListener mSurfaceTextureListener = new SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            //get SurfaceTexture, it can null if not acttack ok to parent
            mSurfaceTexture = surface;

            LogUtils.d(TAG, "onSurfaceTextureAvailable ---------> prepareVideo() -------> mPos = " + mPos);

            //prepareVideo
            prepareVideo();

            //when ok surface then attack media controller
            attachMediaController();

        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
            //if started
            boolean isValidState = (mTargetState == STARTED);
            //if match with size video
            boolean hasValidSize = mVideoSizeCalculator.currentSizeIs(width, height);

            LogUtils.d(TAG, "onSurfaceTextureAvailable ---------> onSurfaceTextureSizeChanged() -------> mPos = " + mPos);
            if (mMediaPlayer != null && isValidState && hasValidSize) {
                //start at mSeekWhenPrepared
                if (mSeekWhenPrepared != 0) {
                    seekTo(mSeekWhenPrepared);
                }
                LogUtils.d(TAG, "onSurfaceTextureAvailable ---------> onSurfaceTextureSizeChanged() -------> start()---------> mPos = " + mPos);
                start();
            }
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            //release
            mSurfaceTexture = null;
            hideMediaController();
            release(true);
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {
            mSurfaceTexture = surface;
        }
    };

    private void showDialogError(int frameworkError) {
        //show dialog
        if (getWindowToken() != null) {
            //if this view not attach any parent window (getWindowToken == null)
            if (errorDialog != null && errorDialog.isShowing()) {
                errorDialog.dismiss();
            }
            errorDialog = createErrorDialog(this.getContext(), getErrorMessage(frameworkError));
            errorDialog.show();
        }
    }

    private AlertDialog createErrorDialog(Context context, final int errorMessage) {
        return new AlertDialog.Builder(context)
                .setMessage(errorMessage)
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(final DialogInterface dialog, final int whichButton) {

                            }
                        }
                )
                .setCancelable(false)
                .create();
    }

    private int getErrorMessage(int frameworkError) {
        int messageId = R.string.play_video_error;

        if (frameworkError == MediaPlayer.MEDIA_ERROR_UNKNOWN) {
            messageId = R.string.play_media_error_media_error_unknown;
        } else if (frameworkError == MediaPlayer.MEDIA_ERROR_IO) {
            messageId = R.string.play_media_error_media_error_io;
        } else if (frameworkError == MediaPlayer.MEDIA_ERROR_MALFORMED) {
            messageId = R.string.play_media_error_media_error_malformed;
        } else if (frameworkError == MediaPlayer.MEDIA_ERROR_SERVER_DIED) {
            messageId = R.string.play_media_error_media_error_server_died;
        } else if (frameworkError == MediaPlayer.MEDIA_ERROR_TIMED_OUT) {
            messageId = R.string.play_media_error_media_error_time_out;
        } else if (frameworkError == MediaPlayer.MEDIA_ERROR_UNKNOWN) {
            messageId = R.string.play_media_error_media_error_unknown;
        } else if (frameworkError == MediaPlayer.MEDIA_ERROR_UNSUPPORTED) {
            messageId = R.string.play_media_error_media_error_unsupported;
        } else if (frameworkError == MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK) {
            messageId = R.string.play_media_progressive_error_message;
        }
        return messageId;
    }

    private void hideMediaController() {
        if (mPlayerController != null)
            mPlayerController.hide();
    }

    private void showMediaController() {
        if (mPlayerController != null)
            mPlayerController.show();
    }

    public void showLoadingVideo() {
        if (mPlayerController != null)
            mPlayerController.showBegin();
    }

    public void setPos(int pos) {
        mPos = pos;
    }

    /**
     * isInPlaybackState = is state not idle, error, preparing
     * media player can play
     *
     * @return
     */
    private boolean isInPlaybackState() {
        return mMediaPlayer != null && mCurrentState != IDLE && mCurrentState != ERROR && mCurrentState != PREPARING;
    }

    /**
     * check already pause at time
     *
     * @param seekToPosition
     * @return
     */
    private boolean isPausingAt(int seekToPosition) {
        return !isPlaying() && (seekToPosition != 0 || getCurrentPosition() > 0);
    }

    @Override
    public void start() {
        LogUtils.d(TAG, "start() ---> mPos = " + mPos);
        if (isInPlaybackState()) {
            LogUtils.d(TAG, "start() ---> isInPlaybackState() ---> mPos = " + mPos);

            mMediaPlayer.start();
            mCurrentState = STARTED;

            //show media controller
            showMediaController();

        }

        //if it can not play, then wish is started
        mTargetState = STARTED;
    }

    @Override
    public void pause() {
        LogUtils.d(TAG, "pause: mPos " + mPos);

        if (isInPlaybackState()) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.pause();
                mCurrentState = PAUSED;
                setKeepScreenOn(false);
                mPlayerController.pause();

                LogUtils.d(TAG, "pause: OK OK OK mPos " + mPos);
            }
        }

        mTargetState = PAUSED;
    }

    @Override
    public int getDuration() {
        if (mMediaPlayer != null)
            return mMediaPlayer.getDuration();
        return 0;
    }

    @Override
    public int getCurrentPosition() {
        LogUtils.d(TAG, "getCurrentPosition() -------> mPos = " + mPos);
        int millisecond = 0;
        if (mMediaPlayer != null)
            millisecond = mMediaPlayer.getCurrentPosition();
        return millisecond;
    }

    @Override
    public void seekTo(int millis) {
        if (isInPlaybackState()) {
            mMediaPlayer.seekTo(millis);
            mSeekWhenPrepared = 0;
        } else {
            mSeekWhenPrepared = millis;
        }
    }

    @Override
    public boolean isPlaying() {
        if (mMediaPlayer != null)
            return mMediaPlayer.isPlaying();
        return false;
    }

    @Override
    public int getBufferPercentage() {
        if (isLoadNetwork)
            return mCurrentBufferPercentage;
        else return 100;
    }

    @Override
    public boolean canPause() {
        return mCanPause;
    }

    @Override
    public boolean canSeekBackward() {
        return mCanSeekBack;
    }

    @Override
    public boolean canSeekForward() {
        return mCanSeekForward;
    }

    @Override
    public int getAudioSessionId() {
        if (mAudioSession == 0) {
            MediaPlayer foo = new MediaPlayer();
            mAudioSession = foo.getAudioSessionId();
            foo.release();
        }
        return mAudioSession;
    }


    public void setOnPlayStateListener(final OnPlayStateListener onPlayStateListener) {
        this.onPlayStateListener = onPlayStateListener;
    }


    public void resume(int posPauseTimePlay) {
        //when resume if load from network must to load again video
        //need load in service
        if (isLoadNetwork) {
            prepareVideo();
            requestLayout();
            invalidate();
        } else {
            seekTo(posPauseTimePlay);
            start();
        }
    }

    /**
     * hide old media controller
     * attach new media controller
     * if onSurface Texture available
     * else will attack when listener SurfaceTextureListener
     *
     * @param controller
     */
    public void setMediaController(final PlayerController controller) {
//        hideMediaController();
        mPlayerController = controller;
        attachMediaController();
        showLoadingVideo();
    }

    /**
     * {@link MediaController.MediaPlayerControl
     */
    private void attachMediaController() {
        if (
//                mMediaPlayer != null &&
                mPlayerController != null) {
            //pass interface media player
            //update icon pause button
            mPlayerController.setMediaPlayer(this);

            //anchor to parent if it is view
            View anchorView = this.getParent() instanceof View ? (View) this.getParent() : this;
            mPlayerController.setAnchorView(anchorView);

            //set enable button
            //TODO
            mPlayerController.setEnabled(isInPlaybackState());
        }
    }

    public void setVolumeMute(boolean isMute) {
        if (mMediaPlayer == null)
            return;

        if (isMute)
            mMediaPlayer.setVolume(0, 0);
        else
            mMediaPlayer.setVolume(1, 1);
    }

    public void setSeekWhenPrepared(int seekWhenPrepared) {
        mSeekWhenPrepared = seekWhenPrepared;
    }

    public void stop() {
        mTargetState = STOPED;
        if (mMediaPlayer != null) {
            LogUtils.d(TAG, "stop() -------> mPos = " + mPos);
            mMediaPlayer.stop();
            setKeepScreenOn(false);
            showMediaControllerButStopUpdateTrack();
            mCurrentState = STOPED;
        }
    }

    private void showMediaControllerButStopUpdateTrack() {
        showMediaController();
        mPlayerController.stopUpdateTrack();

    }

    public void setIteractionVideo(VideoViewHolder.IteractionVideo iteractionVideo) {
        this.mIteractionVideo = iteractionVideo;
    }

    public interface OnPlayStateListener {
        /**
         * notify when video begin prepared ok buffer first block
         */
        void onFirstVideoFrameRendered();

        /**
         * notify when video is playing
         */
        void onPlay();

        /**
         * notify when video is buffer
         */
        void onBuffer();

    }

    public interface OnCompletionListener {
        /**
         * Called when the end of a media source is reached during playback.
         */
        void onCompletion();
    }

    /**
     * controller video with a several action normal
     */
    public interface VideoController {
        void setMediaPlayer(Player player);

        void setEnabled(boolean value);

        void setAnchorView(View view);

        void show(int timeInMilliSeconds);

        void show();

        void hide();
    }
}
