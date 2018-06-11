package com.vn.ntsc.ui.mediadetail.base;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.vn.ntsc.R;
import com.vn.ntsc.app.AppController;
import com.vn.ntsc.repository.model.media.MediaEntity;
import com.vn.ntsc.utils.LogUtils;
import com.vn.ntsc.widget.views.images.mediadetail.video.VideoControllerView;

import java.io.IOException;

/**
 * Created by ThoNh on 11/21/2017.
 */

public class Video extends BaseMediaHolder implements View.OnClickListener {
    private static final String TAG = Video.class.getSimpleName();

    private SurfaceView mVideoSurface;
    private ProgressBar mLoading;
    private FrameLayout anchorView;

    private MediaPlayer mPlayer;
    private SurfaceHolder surfaceHolder;
    private VideoControllerView mViewController;
    private ImageView mImagePause;

    private int screenOrient;
    private boolean isPlayerPrepared;
    private boolean isPlayImmediately;
    private Context mContext;

    public Video(final Context activity, final MediaEntity mediaEntity, View layout) {
        super(activity, mediaEntity);

        mContext = activity;
        mVideoSurface = layout.findViewById(R.id.videoSurface);
        mImagePause = layout.findViewById(R.id.pause);
        mImagePause.setOnClickListener(this);

        mVideoSurface.setZOrderOnTop(false);
        mVideoSurface.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        mLoading = layout.findViewById(R.id.loading);
        anchorView = layout.findViewById(R.id.view_anchor);
        isPlayImmediately = mediaEntity.isPlayImmediately();
        screenOrient = activity.getResources().getConfiguration().orientation;

        try {
            surfaceHolder = mVideoSurface.getHolder();
            surfaceHolder.addCallback(mSurfaceHolderCallback);
            layout.findViewById(R.id.video_container).setOnTouchListener(mSurfaceVideoOnTouch);
            mViewController = new VideoControllerView(activity, true);
            mViewController.setEnabled(true);
            mViewController.show();
            mImagePause.setVisibility(View.GONE);
            mPlayer = new MediaPlayer();
            if (mediaEntity.mUrl != null) {
                mPlayer.setDataSource(activity, Uri.parse(mediaEntity.mUrl));
                mPlayer.setOnPreparedListener(mOnPreparedListener);
                mPlayer.setOnErrorListener(mOnErrorListener);
                mPlayer.setOnInfoListener(mOnInfoListener);
                mPlayer.setOnCompletionListener(mCompleteListener);
                mPlayer.prepareAsync();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        LogUtils.e(TAG, mediaEntity.toString());
    }

    private MediaPlayer.OnPreparedListener mOnPreparedListener =
            new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    mLoading.setVisibility(View.GONE);
                    LogUtils.e(TAG, "mOnPreparedListener ---> onPrepared");
                    mViewController.setMediaPlayer(mMediaPlayerControl);
                    mViewController.setAnchorViews(anchorView);
                    mViewController.setEnabled(true);

                    int orient = mActivity.getResources().getConfiguration().orientation;
                    orientationScreenChange(orient);

//                    setSurfaceViewSize(orient, mVideoSurface, mPlayer);

                    isPlayerPrepared = true;
                    if (isPlayImmediately) {
                        mPlayer.start();
                        mImagePause.setImageResource(android.R.drawable.ic_media_pause);
                        mImagePause.setVisibility(View.VISIBLE);
                        mViewController.show();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                //Do something after 100ms
                                mImagePause.setVisibility(View.GONE);
                            }
                        }, 3000);
                        updatePausePlay();

                    }
                }
            };


    // set size of surfaceView when change orientation phone
    private void setSurfaceViewSize(int screenOrient, SurfaceView surfaceView, MediaPlayer mediaPlayer) {

        int videoWidth = mediaPlayer.getVideoWidth();                       // get width video
        int videoHeight = mediaPlayer.getVideoHeight();                     // get height video
        float videoProportion = (float) videoWidth / videoHeight;           // ratio

        int screenWidth = AppController.SCREEN_WIDTH;
        int screenHeight = AppController.SCREEN_HEIGHT;
        float screenProportion = (float) screenWidth / screenHeight;


        LogUtils.e("ThoNH", "videoProportion:" + videoProportion + "            screenProportion:" + screenProportion);
        LogUtils.e("ThoNH", "\nvideoProportion/screenProportion:" + (videoProportion / screenProportion));


        final ViewGroup.LayoutParams lp = surfaceView.getLayoutParams();          // Get the SurfaceView layout param
        switch (screenOrient) {
            case 0:
            case 180:
            case Configuration.ORIENTATION_PORTRAIT:

                if (videoProportion > 1) {                      // Screen Vertical, Video Horizontal
                    lp.width = screenWidth;
                    lp.height = (int) (float) (screenWidth / videoProportion);
                    LogUtils.e(TAG, "Screen Vertical, Video Horizontal-------> setSurfaceViewSize:-->" + lp.width + "-" + lp.height);

                } else {                                        // Screen Vertical, Video Vertical
                    lp.height = screenHeight;
                    lp.width = (int) (float) (screenHeight * videoProportion);
                    LogUtils.e(TAG, "Screen Vertical, Video Vertical-------> setSurfaceViewSize:-->" + lp.width + "-" + lp.height);
                }

                surfaceView.setScaleX(1.0f);
                surfaceView.setScaleY(1.0f);
                surfaceView.getHolder().setFixedSize(lp.width, lp.height);
                surfaceView.setLayoutParams(lp);
                break;

            case 90:
            case 270:
            case Configuration.ORIENTATION_LANDSCAPE:

                if (videoProportion > 1) {                      // Screen Horizontal, Video Horizontal
                    lp.width = screenHeight;
                    lp.height = (int) (float) (screenHeight / videoProportion) - 300;
                    LogUtils.e(TAG, "Screen Horizontal, Video Horizontal-------> setSurfaceViewSize:-->" + lp.width + "-" + lp.height);

                } else {                                        // Screen Horizontal, Video Vertical
                    lp.height = screenWidth;
                    lp.width = (int) (float) (screenWidth * videoProportion) - 300;
                    LogUtils.e(TAG, "Screen Horizontal, Video Vertical-------> setSurfaceViewSize:-->" + lp.width + "-" + lp.height);
                }

                surfaceView.setScaleX(1.0f);
                surfaceView.setScaleY(1.0f);
                surfaceView.getHolder().setFixedSize(lp.width, lp.height);
                surfaceView.setLayoutParams(lp);
                break;
        }
    }


    private View.OnTouchListener mSurfaceVideoOnTouch =
            new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                        if (mViewController != null && mViewController.isShowing()) {
                            mImagePause.setVisibility(View.GONE);
                            mViewController.hide();
                        } else if (mViewController != null && !mViewController.isShowing()) {
                            LogUtils.e(TAG, "mLoading " + mLoading.isShown());
                            if (!mLoading.isShown()) {
                                mImagePause.setVisibility(View.VISIBLE);
                                mViewController.show();
                            }

                        }

                    }
                    return false;
                }
            };


    private MediaPlayer.OnErrorListener mOnErrorListener = new MediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {

            switch (what) {
                case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                    LogUtils.e("ThoNH", "MEDIA_ERROR_UNKNOWN");
                    break;
                case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                    LogUtils.e("ThoNH", "MEDIA_ERROR_SERVER_DIED");
                    break;
                case MediaPlayer.MEDIA_ERROR_IO:
                    LogUtils.e("ThoNH", "MEDIA_ERROR_IO");
                    Toast.makeText(mContext, R.string.msg_common_no_connection, Toast.LENGTH_LONG);
                    break;
                case MediaPlayer.MEDIA_ERROR_MALFORMED:
                    LogUtils.e("ThoNH", "MEDIA_ERROR_MALFORMED");
                    break;
                case MediaPlayer.MEDIA_ERROR_UNSUPPORTED:
                    LogUtils.e("ThoNH", "MEDIA_ERROR_UNSUPPORTED");
                    break;
                case MediaPlayer.MEDIA_ERROR_TIMED_OUT:
                    LogUtils.e("ThoNH", "MEDIA_ERROR_TIMED_OUT");
                    Toast.makeText(mContext, R.string.msg_common_no_connection, Toast.LENGTH_LONG);
                    break;
            }
            return false;
        }
    };


    private MediaPlayer.OnCompletionListener mCompleteListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            mImagePause.setImageResource(android.R.drawable.ic_media_play);
            mImagePause.setVisibility(View.GONE);
        }
    };


    private MediaPlayer.OnInfoListener mOnInfoListener = new MediaPlayer.OnInfoListener() {
        @Override
        public boolean onInfo(MediaPlayer mp, int what, int extra) {
            switch (what) {
                case MediaPlayer.MEDIA_INFO_UNKNOWN:
                    LogUtils.e("ThoNH", "MEDIA_INFO_UNKNOWN");
                    break;
                case MediaPlayer.MEDIA_INFO_VIDEO_TRACK_LAGGING:
                    LogUtils.e("ThoNH", "MEDIA_INFO_VIDEO_TRACK_LAGGING");
                    break;
                case MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                    LogUtils.e("ThoNH", "MEDIA_INFO_VIDEO_RENDERING_START");
                    break;
                case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                    LogUtils.e("ThoNH", "MEDIA_INFO_BUFFERING_START");
                    mLoading.setVisibility(View.VISIBLE);
                    break;
                case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                    LogUtils.e("ThoNH", "MEDIA_INFO_BUFFERING_END");
                    mLoading.setVisibility(View.GONE);
                    break;
                case MediaPlayer.MEDIA_INFO_BAD_INTERLEAVING:
                    LogUtils.e("ThoNH", "MEDIA_INFO_BAD_INTERLEAVING");
                    break;
                case MediaPlayer.MEDIA_INFO_NOT_SEEKABLE:
                    LogUtils.e("ThoNH", "MEDIA_INFO_NOT_SEEKABLE");
                    break;
                case MediaPlayer.MEDIA_INFO_METADATA_UPDATE:
                    LogUtils.e("ThoNH", "MEDIA_INFO_METADATA_UPDATE");
                    break;
                case MediaPlayer.MEDIA_INFO_UNSUPPORTED_SUBTITLE:
                    LogUtils.e("ThoNH", "MEDIA_INFO_UNSUPPORTED_SUBTITLE");
                    break;
                case MediaPlayer.MEDIA_INFO_SUBTITLE_TIMED_OUT:
                    LogUtils.e("ThoNH", "MEDIA_INFO_SUBTITLE_TIMED_OUT");
                    Toast.makeText(mContext, R.string.msg_common_no_connection, Toast.LENGTH_LONG);
                    break;
            }
            return false;
        }
    };


    private SurfaceHolder.Callback mSurfaceHolderCallback =
            new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder surfaceHolder) {
                    if (mPlayer != null)
                        mPlayer.setDisplay(surfaceHolder);

                    Video.this.surfaceHolder = surfaceHolder;
                }

                @Override
                public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
                    if (mPlayer != null)
                        mPlayer.setDisplay(surfaceHolder);
                }

                @Override
                public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

                }
            };


    private VideoControllerView.MediaPlayerControl mMediaPlayerControl =
            new VideoControllerView.MediaPlayerControl() {
                @Override
                public void start() {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (mPlayer != null && isPlayerPrepared && mViewController != null) {
                                mPlayer.start();
                                mImagePause.setVisibility(View.VISIBLE);
                                mViewController.show();
                                updatePausePlay();
                            }

                        }
                    }, 500);
                }

                @Override
                public void pause() {
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            if (isPlayerPrepared && mPlayer != null && mViewController != null) {
                                mPlayer.pause();
                                mImagePause.setVisibility(View.VISIBLE);
                                mViewController.show();
                                updatePausePlay();
                            }
                        }
                    });
                }

                @Override
                public int getDuration() {
                    if (mPlayer != null) {
                        return mPlayer.getDuration();
                    }
                    return 0;
                }

                @Override
                public int getCurrentPosition() {
                    try {
                        return mPlayer.getCurrentPosition();
                    } catch (Exception e) {
                        return 0;
                    }
                }

                @Override
                public void seekTo(int pos) {
                    if (mPlayer != null)
                        try {
                            mPlayer.seekTo(pos);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                }

                @Override
                public boolean isPlaying() {
                    if (mPlayer != null)
                        return mPlayer.isPlaying();
                    return false;
                }

                @Override
                public int getBufferPercentage() {
                    return 0;
                }

                @Override
                public boolean canPause() {
                    return true;
                }

                @Override
                public boolean canSeekBackward() {
                    return true;
                }

                @Override
                public boolean canSeekForward() {
                    return true;
                }

                @Override
                public boolean isFullScreen() {
                    return screenOrient == Configuration.ORIENTATION_LANDSCAPE;
                }

                @Override
                public void toggleFullScreen() {
                    screenOrient = mActivity.getResources().getConfiguration().orientation;
                    switch (screenOrient) {
                        case Configuration.ORIENTATION_PORTRAIT:
                            orientationScreenChange(Configuration.ORIENTATION_LANDSCAPE);
                            break;
                        case Configuration.ORIENTATION_LANDSCAPE:
                            orientationScreenChange(Configuration.ORIENTATION_PORTRAIT);
                            break;
                    }
                }
            };

    public void start() {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                if (isPlayerPrepared) {
                    mMediaPlayerControl.start();
                    mPlayer.start();
                } else {
                    isPlayImmediately = true;
                }
            }
        });
    }

    public void pause() {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                mMediaPlayerControl.pause();
            }
        });
    }

    public void updatePausePlay() {
        LogUtils.e(TAG, "updatePausePlay : " + mPlayer.isPlaying());
        if (mPlayer.isPlaying()) {
            mImagePause.setImageResource(android.R.drawable.ic_media_pause);
        } else {
            mImagePause.setImageResource(android.R.drawable.ic_media_play);
        }
    }


    @Override
    public void onActivityResume() {
        LogUtils.e(TAG, "onActivityResume ---> ");
        start();
    }

    @Override
    public void onActivityPause() {
        LogUtils.e(TAG, "onActivityPause ---> ");
        pause();
        isPlayImmediately = false;
    }

    @Override
    public void onActivityStop() {
        LogUtils.e(TAG, "onActivityStop ---> ");
    }

    @Override
    public void onActivityDestroy() {
        super.onActivityDestroy();
        LogUtils.e(TAG, "onActivityDestroy ---> ");

        isPlayImmediately = false;
        isPlayerPrepared = false;

        if (mViewController != null) {
            mViewController.hide();
            mImagePause.setVisibility(View.GONE);
            mViewController = null;
        }
        if (mPlayer != null) {
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
        }
        if (mActivity != null) {
            mActivity = null;
        }
    }

    @Override
    public void onActivityConfigChange(int mOrientation, boolean isOrientSettingOn) {
        if (isOrientSettingOn)
            orientationScreenChange(mOrientation);
    }

    @Override
    public void onPageComing() {
        LogUtils.e(TAG, "onPageComing ---> ");
        start();
    }

    @Override
    public void onPageLeaving() {
        LogUtils.e(TAG, "onPageLeaving ---> ");
        pause();
        isPlayImmediately = false;
    }

    private void orientationScreenChange(final int mOrientation) {
        LogUtils.e(TAG, "orientationScreenChange:" + mOrientation);
        switch (mOrientation) {
            case 90:
            case 270:
            case Configuration.ORIENTATION_LANDSCAPE:
                AppCompatActivity activity = (AppCompatActivity) mActivity;
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
                setSurfaceViewSize(mOrientation, mVideoSurface, mPlayer);
                try {
                    ((BaseMediaActivity) mActivity).orientationChange(Configuration.ORIENTATION_LANDSCAPE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case 0:
            case 180:
            case Configuration.ORIENTATION_PORTRAIT:
                AppCompatActivity activity2 = (AppCompatActivity) mActivity;
                activity2.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
                setSurfaceViewSize(mOrientation, mVideoSurface, mPlayer);
                try {
                    ((BaseMediaActivity) mActivity).orientationChange(Configuration.ORIENTATION_PORTRAIT);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    @Override
    public void onClick(View v) {
        if (!mLoading.isShown()) {
            mViewController.show();
            doPauseResume();
        }
    }

    private void doPauseResume() {
        if (mPlayer == null) {
            return;
        }

        if (mPlayer.isPlaying()) {
            mPlayer.pause();
        } else {
            mPlayer.start();
        }
        updatePausePlay();
    }


}
