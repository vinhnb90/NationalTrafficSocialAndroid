package com.vn.ntsc.ui.mediadetail.base;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.vn.ntsc.R;
import com.vn.ntsc.ui.mediadetail.util.StateMediaPlayer;
import com.vn.ntsc.ui.mediadetail.util.Utils;
import com.vn.ntsc.utils.ImagesUtils;
import com.vn.ntsc.utils.LogUtils;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.vn.ntsc.ui.mediadetail.util.StateMediaPlayer.*;
import static com.vn.ntsc.ui.mediadetail.util.StateMediaPlayer.ERROR;
import static com.vn.ntsc.ui.mediadetail.util.StateMediaPlayer.IDLE;
import static com.vn.ntsc.ui.mediadetail.util.StateMediaPlayer.PREPARED;
import static com.vn.ntsc.ui.mediadetail.util.StateMediaPlayer.PREPARING;
import static com.vn.ntsc.ui.mediadetail.util.StateMediaPlayer.STARTED;

public class AudioViewHolder extends BaseMediaHolder implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {
    private static final String TAG = AudioViewHolder.class.getName();
    /*----------------------------------------var----------------------------------------*/

    private final Context mContext;
    private final Unbinder mUnbinder;

    //media and state lifecycle media
    private MediaPlayer mMediaPlayer;

    private int mAudioSession;

    private String mUri;

    //iteraction with audio fragment
    private AudioViewHolder.IteractionAudio mIteractionAudio;

    /**
     * mCurrentState :is a Audio object's current state.
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

    private int mCurrentBufferPercentage;

    //update time track audio
    private Handler mHandler = new Handler();

    /**
     * use for streaming video
     * presenter code block get Metadata check mode streaming video not working
     * then default true all flag below
     */
    private boolean mCanPause;
    private boolean mCanSeekBack;
    private boolean mCanSeekForward;

    //use to log
    private int mPos;

    //position seek when prepared
    private int mSeekWhenPrepared;

    //if load data from resource exist then mCurrentBufferPercentage default = 100
    private boolean isLoadNetwork;

    private AlertDialog errorDialog;

    private Runnable mTimeTrackMedia = new Runnable() {
        public void run() {
            if (mMediaPlayer == null)
                return;

            long totalDuration = mMediaPlayer.getDuration();
            long currentDuration = mMediaPlayer.getCurrentPosition();

            setTimeBegin(Utils.milliSecondsToTimer(currentDuration) + "");
            setTimeEnd(Utils.milliSecondsToTimer(totalDuration) + "");

            // Updating progress bar
            int progress = (Utils.getProgressPercentage(currentDuration, totalDuration));
            //if (DEBUG) Log.d("Progress", ""+progress);
            setMediaProgress(progress);

            // Running this thread after 100 milliseconds
            mHandler.postDelayed(this, 100);
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

            //show dialog this error
            if (mIteractionAudio != null) {
                if (mIteractionAudio.isAreadyVisibleToUser())
                    //show dialog this error
                    showDialogError(frameworkError);
                else {
                    //reload
                    prepareDataAudio(mUri);
                }
            }

            return true;
        }
    };

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

            int seekToPosition = mSeekWhenPrepared;
            if (seekToPosition != 0) {
                seekTo(seekToPosition);
            }

            if (mTargetState == STARTED) {

                //if wish is started then play
                //update progress time, change button
                start();

            } else if (isPausingAt(seekToPosition)) {

                //if wish is not started
                //but is pausing at a position then play
                start();
            } else {
                LogUtils.d(TAG, "onPrepared(MediaPlayer mp)  ---> NOT START NOT START NOT START NOT START---> mPos = " + mPos);
            }
        }
    };

    private MediaPlayer.OnBufferingUpdateListener mBufferingUpdateListener = new MediaPlayer.OnBufferingUpdateListener() {
        @Override
        public void onBufferingUpdate(MediaPlayer mp, int percent) {
            LogUtils.d(TAG, "onBufferingUpdate ---> mCurrentBufferPercentage = " + mCurrentBufferPercentage + " mTargetState = " + mTargetState + "---> mPos = " + mPos);

            //detect load from network
            isLoadNetwork = true;
            mCurrentBufferPercentage = percent;
            if (mTargetState == STARTED) {
                requireTryStartAgain();
                LogUtils.d(TAG, "onBufferingUpdate ---> requireTryStartAgain(); ---> mPos = " + mPos);
            }
        }
    };

    private MediaPlayer.OnCompletionListener mCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            //loop
            mMediaPlayer.seekTo(1);
            start();
        }
    };

    //bind view
    @BindView(R.id.fragment_detail_media_audio_tv_timebegin)
    TextView tvTimeBegin;

    @BindView(R.id.fragment_detail_media_audio_tv_timeend)
    TextView tvTimeEnd;

    @BindView(R.id.fragment_detail_media_audio_sbar_process)
    SeekBar sbarProcess;

    @BindView(R.id.fragment_detail_media_audio_ib_playing)
    ImageButton ibtnPlaying;

    @BindView(R.id.fragment_detail_media_audio_iv_thumb)
    ImageView ivThumb;

    @BindView(R.id.fragment_detail_media_audio_tv_audio_name)
    TextView tvAudioName;

    @BindView(R.id.fragment_detail_media_audio_tv_author_name)
    TextView tvAuthorName;

    @BindView(R.id.fragment_detail_media_audio_rl_progressbar_time)
    RelativeLayout rlSeekBar;

    /**
     * Called to indicate an info or a warning.
     *
     * @param mp      the MediaPlayer the info pertains to.
     * @param what    the type of info or warning.
     * @param extra an extra code, specific to the info. Typically
     * implementation dependent.
     * @return True if the method handled the info, false if it didn't.
     * Returning false, or not having an OnInfoListener at all, will
     * cause the info to be discarded.
     */
    private MediaPlayer.OnInfoListener mInfoListener = new MediaPlayer.OnInfoListener() {
        @Override
        public boolean onInfo(MediaPlayer mp, int what, int extra) {

            LogUtils.d(TAG, "onInfo -------> what = " + what + " mPos = " + mPos);
            if (MediaPlayer.MEDIA_INFO_UNKNOWN == what) {
                LogUtils.d(TAG, mContext.getResources().getString(R.string.audio_media_info_unknow));
            }

            if (MediaPlayer.MEDIA_INFO_VIDEO_TRACK_LAGGING == what) {
                LogUtils.d(TAG, mContext.getResources().getString(R.string.video_media_info_video_track_lagging));
            }

            if (MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START == what) {
                Toast.makeText(mContext, R.string.video_media_info_video_track_lagging, Toast.LENGTH_SHORT).show();
            }

            if (MediaPlayer.MEDIA_INFO_BUFFERING_START == what) {
                LogUtils.d(TAG, mContext.getResources().getString(R.string.media_info_buffering_start));
            }

            if (MediaPlayer.MEDIA_INFO_BUFFERING_END == what) {
                LogUtils.d(TAG, mContext.getResources().getString(R.string.media_info_buffering_end));
            }

            if (MediaPlayer.MEDIA_INFO_BAD_INTERLEAVING == what) {
                LogUtils.d(TAG, mContext.getResources().getString(R.string.media_info_bad_interleaving));
            }

            if (MediaPlayer.MEDIA_INFO_NOT_SEEKABLE == what) {
                LogUtils.d(TAG, mContext.getResources().getString(R.string.media_info_not_seekable));
            }

            if (MediaPlayer.MEDIA_INFO_METADATA_UPDATE == what) {
                LogUtils.d(TAG, mContext.getResources().getString(R.string.media_info_meta_data_update));
            }

            if (MediaPlayer.MEDIA_INFO_UNSUPPORTED_SUBTITLE == what) {
                LogUtils.d(TAG, mContext.getResources().getString(R.string.media_info_unsupported_title));
            }

            if (MediaPlayer.MEDIA_INFO_SUBTITLE_TIMED_OUT == what) {
                LogUtils.d(TAG, mContext.getResources().getString(R.string.media_info_subtitle_time_out));
            }

            return true;
        }
    };


    /*----------------------------------------instance----------------------------------------*/
    public AudioViewHolder(Context activity, AudioViewHolder.IteractionAudio iteractionAudio, View layout) {
        super(activity);

        mContext = activity;
        mIteractionAudio = iteractionAudio;
        mUnbinder = ButterKnife.bind(this, layout);

        // Changing Button Image to pause image
        ibtnPlaying.setImageResource(R.drawable.btn_pause);

        sbarProcess.setOnSeekBarChangeListener(this);

        ibtnPlaying.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchPlayPauseAudio();
            }
        });

        // set Progress bar values
        sbarProcess.setProgress(0);
        sbarProcess.setMax(100);

        //state life cycle media player
        mCurrentState = IDLE;
        mTargetState = IDLE;
    }

    /*----------------------------------------lifecycle----------------------------------------*/
    /*----------------------------------------override----------------------------------------*/
    @Override
    public void onClick(View v) {

    }

    @OnClick(R.id.fragment_detail_media_audio_ib_playing)
    public void clickPlayPause(View view) {
        switchPlayPauseAudio();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    /**
     * stop handler update time text
     * seek to new progress
     * update time text again
     *
     * @param seekBar: track
     */
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        //remove handler update time
        stopUpdateTimeCurrentPlay();

        int totalDuration = mMediaPlayer.getDuration();
        int currentPosition = Utils.progressToTimer(seekBar.getProgress(), totalDuration);

        // forward or backward to certain seconds
        mMediaPlayer.seekTo(currentPosition);

        // update timer progress again
        updateProgressBarAudio();
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        //remove handler update time
        stopUpdateTimeCurrentPlay();

        //seekTo
        int totalDuration = mMediaPlayer.getDuration();
        int currentPosition = Utils.progressToTimer(seekBar.getProgress(), totalDuration);

        // forward or backward to certain seconds
        mMediaPlayer.seekTo(currentPosition);

        // update timer progress again
        updateProgressBarAudio();

    }

    /*----------------------------------------func----------------------------------------*/
    public void unbind() {
        mUnbinder.unbind();
    }

    public void loadThumb(@NonNull String thumbUrl) {
        ImagesUtils.loadRounded(mContext, thumbUrl, ivThumb);
    }

    /**
     * after ok uri, has posPauseTimePlayOld
     * setup media player
     *
     * @param uri
     */
    public void prepareDataAudio(String uri) {
        if (uri == null) {
            // not ready for playback just yet, will try again later
            return;
        }

        mUri = uri;

        try {
            // shouldn't clear the target state, because somebody might have
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
            mMediaPlayer.setOnCompletionListener(mCompletionListener);
            mMediaPlayer.setOnErrorListener(mErrorListener);
            mMediaPlayer.setOnInfoListener(mInfoListener);
            mMediaPlayer.setOnBufferingUpdateListener(mBufferingUpdateListener);
            mCurrentBufferPercentage = 0;
            mMediaPlayer.setDataSource(mContext, Uri.parse(uri));
            mMediaPlayer.prepareAsync();

            mCurrentState = PREPARING;
            mTargetState = PREPARING;
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

    public int pauseAudio() {
        if (mMediaPlayer == null)
            return 0;

        mTargetState = PAUSED;
        int currentDurationPause = 0;

        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
            setIconButtonPlaying(R.drawable.btn_play);

            currentDurationPause = mMediaPlayer.getCurrentPosition();
            mCurrentState = PAUSED;

            LogUtils.d(TAG, "pause: OK OK OK mPos " + mPos);
        }

        return currentDurationPause;
    }

    public int stopAudio() {
        if (mMediaPlayer == null)
            return 0;

        int currentTimeTrack = mMediaPlayer.getCurrentPosition();

        //stop and release media player
        mMediaPlayer.stop();

        //status
        mCurrentState = STOPED;

        //stop track media
        stopUpdateTimeCurrentPlay();

        return currentTimeTrack;
    }

    /**
     * clear, reset mCurrentState
     * option reset mTargetState
     *
     * @param clearTargetState
     */
    public void release(boolean clearTargetState) {
        if (mMediaPlayer != null) {
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
            mCurrentState = IDLE;
            if (clearTargetState) {
                mTargetState = IDLE;
            }
        }
    }

    public void seekTo(int millis) {
        if (isInPlaybackState()) {
            mMediaPlayer.seekTo(millis);
            mSeekWhenPrepared = 0;
        } else {
            mSeekWhenPrepared = millis;
        }
    }

    private void switchPlayPauseAudio() {
        if (mMediaPlayer == null)
            return;

        if (mCurrentState == STARTED) {
            pauseAudio();

            //status
            mCurrentState = PAUSED;
            return;
        }

        if (mCurrentState == PAUSED) {
            //TODO
            playAudio();

            //status
            mCurrentState = STARTED;
            return;
        }
    }

    /**
     * check can start audio
     * start audio
     */
    public void start() {
        LogUtils.d(TAG, "start() ---> mPos = " + mPos);

        if (isInPlaybackState()) {
            LogUtils.d(TAG, "start() ---> isInPlaybackState() ---> mPos = " + mPos);

            mMediaPlayer.start();

            mCurrentState = STARTED;

            //update progress time, change button
            updateProgressBarAudio();
            ibtnPlaying.setImageResource(R.drawable.btn_pause);
        }

        //if it can not play, then wish is started
        mTargetState = STARTED;
    }

    public boolean isPlaying() {
        if (mMediaPlayer != null)
            return mMediaPlayer.isPlaying();
        return false;
    }

    public int getCurrentPosition() {
        int millisecond = 0;
        if (mMediaPlayer != null)
            millisecond = mMediaPlayer.getCurrentPosition();
        return millisecond;
    }

    private void showDialogError(int frameworkError) {
        //show dialog
        //if this view not attach any parent window (getWindowToken == null)
        if (errorDialog != null && errorDialog.isShowing()) {
            errorDialog.dismiss();
        }
        errorDialog = createErrorDialog(mContext, getErrorMessage(frameworkError));
        errorDialog.show();
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

    private void requireTryStartAgain() {
        //TODO
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
            mMediaPlayer.start();
        }
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

    /**
     * isInPlaybackState = is state not idle, error, preparing
     * media player can play
     *
     * @return
     */
    private boolean isInPlaybackState() {
        return mMediaPlayer != null && mCurrentState != IDLE && mCurrentState != ERROR && mCurrentState != PREPARING;
    }

    public void setIconButtonPlaying(int idIcon) {
        ibtnPlaying.setImageResource(idIcon);
    }

    public void setMediaProgress(@IntRange(from = 0, to = 100) int progress) {
        sbarProcess.setProgress(progress);
    }

    public void setPos(int pos) {
        mPos = pos;
    }

    public void setTimeBegin(String time) {
        tvTimeBegin.setText(time);
    }

    public void setTimeEnd(String time) {
        tvTimeEnd.setText(time);
    }

    private int getErrorMessage(int frameworkError) {
        int messageId = R.string.play_audio_error;

        if (frameworkError == MediaPlayer.MEDIA_ERROR_IO) {
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

    private void updateProgressBarAudio() {
        mHandler.removeCallbacks(mTimeTrackMedia);
        mHandler.postDelayed(mTimeTrackMedia, 100);
    }

    private void stopUpdateTimeCurrentPlay() {
        //remove call back update time tracking
        mHandler.removeCallbacks(mTimeTrackMedia);
    }

    /**
     * seek to
     * start
     *
     * @param posPauseTimePlay
     */
    public void playAudio(int posPauseTimePlay) {
        mSeekWhenPrepared = posPauseTimePlay;
        start();
    }

    /**
     * start
     */
    private void playAudio() {
        start();
    }
    /*----------------------------------------inner----------------------------------------*/

    /**
     * this interface callback to interaction data save
     */
    public interface IteractionAudio {
        boolean isAreadyVisibleToUser();
    }
}
