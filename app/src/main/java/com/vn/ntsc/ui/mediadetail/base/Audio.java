package com.vn.ntsc.ui.mediadetail.base;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.vn.ntsc.R;
import com.vn.ntsc.repository.model.media.MediaEntity;
import com.vn.ntsc.utils.ImagesUtils;
import com.vn.ntsc.utils.LogUtils;
import com.vn.ntsc.widget.views.images.mediadetail.video.VideoControllerView;

import java.io.IOException;

/**
 * Created by ThoNh on 11/21/2017.
 */

public class Audio extends BaseMediaHolder implements MediaPlayer.OnPreparedListener, View.OnTouchListener, View.OnClickListener {
    private static final String TAG = Audio.class.getSimpleName();

    private ProgressBar mLoading;
    private FrameLayout mAnchorLayout;
    private VideoControllerView mViewController;

    private boolean isPrepared = false;
    private MediaPlayer mPlayer;
    private boolean isPlayImmediately;

    private ImageView mImagePause;
    private Context mContext;


    public Audio(Context activity, MediaEntity mediaEntity, View layout) {
        super(activity, mediaEntity);

        try {
            mContext = activity;
            ImageView mImagebBackground = layout.findViewById(R.id.image_background);
            mAnchorLayout = layout.findViewById(R.id.view_anchor);
            mLoading = layout.findViewById(R.id.loading);

            mImagePause = layout.findViewById(R.id.pause);
            mImagePause.setOnClickListener(this);

            isPlayImmediately = mediaEntity.isPlayImmediately();
            ImagesUtils.loadImage(mediaEntity.mThumbnail, mImagebBackground);

            layout.setOnTouchListener(this);
            mViewController = new VideoControllerView(activity, true);
            mViewController.show();
            mImagePause.setVisibility(View.GONE);

            mPlayer = new MediaPlayer();
            mPlayer.setOnPreparedListener(this);
            mPlayer.setDataSource(mediaEntity.mUrl);
            mPlayer.setOnErrorListener(mOnErrorListener);
            mPlayer.setOnInfoListener(mOnInfoListener);
            mPlayer.setOnCompletionListener(mCompleteListener);
            mPlayer.prepareAsync();

        } catch (IOException e) {
            Log.e(TAG, "Could not open file " + mediaEntity.mUrl + " for playback.", e);
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        Log.d(TAG, "onPrepared");
        mLoading.setVisibility(View.GONE);

        mViewController.setEnabled(true);
        mViewController.setMediaPlayer(mediaController);
        mViewController.setAnchorViews(mAnchorLayout);
        mViewController.findViewById(R.id.fullscreen).setVisibility(View.GONE);
        mViewController.show();

        isPrepared = true;
        if (isPlayImmediately) {
            mImagePause.setImageResource(android.R.drawable.ic_media_pause);
            mImagePause.setVisibility(View.VISIBLE);
            mViewController.show();
            mediaController.start();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    //Do something after 100ms
                    mImagePause.setVisibility(View.GONE);
                }
            }, 3000);


        }
    }

    private VideoControllerView.MediaPlayerControl mediaController = new VideoControllerView.MediaPlayerControl() {
        @Override
        public void start() {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (mPlayer != null)
                        mPlayer.start();
                    if (mViewController != null) {
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
                    mPlayer.pause();
                    mViewController.setEnabled(true);
                    updatePausePlay();
                }
            });
        }

        @Override
        public int getDuration() {
            return mPlayer.getDuration();
        }

        @Override
        public int getCurrentPosition() {
            return mPlayer.getCurrentPosition();
        }

        @Override
        public void seekTo(int pos) {
            mPlayer.seekTo(pos);
        }

        @Override
        public boolean isPlaying() {
            return mPlayer.isPlaying();
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
            return false;
        }

        @Override
        public void toggleFullScreen() {

        }
    };

    public void start() {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                if (isPrepared) {
                    mediaController.start();
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
                mediaController.pause();
            }
        });
    }


    @Override
    public void onActivityResume() {
        LogUtils.e(TAG, "onActivityResume");
        start();
    }

    @Override
    public void onActivityPause() {
        LogUtils.e(TAG, "onActivityPause");
        pause();
    }

    @Override
    public void onActivityStop() {
        LogUtils.e(TAG, "onActivityStop");
    }

    @Override
    public void onActivityDestroy() {
        super.onActivityDestroy();
        LogUtils.e(TAG, "onActivityDestroy");
        isPlayImmediately = false;
        isPrepared = false;

        if (mViewController != null) {
            mImagePause.setVisibility(View.GONE);
            mViewController.hide();
            mViewController = null;
        }

        if (mPlayer != null) {
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
        }
        mActivity = null;
    }

    @Override
    public void onActivityConfigChange(int mOrientation, boolean isOrientationSettingOn) {
        LogUtils.e(TAG, "onActivityConfigChange ---> ");
    }


    @Override
    public void onPageComing() {
        LogUtils.e(TAG, "onPageComing ---> ");
        start();
        isPlayImmediately = true;
    }

    @Override
    public void onPageLeaving() {
        LogUtils.e(TAG, "onPageLeaving ---> ");
        pause();
        isPlayImmediately = false;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (mViewController != null && mViewController.isShowing()) {
                mImagePause.setVisibility(View.GONE);
                mViewController.hide();
            } else if (mViewController != null && !mViewController.isShowing()) {
                if (!mLoading.isShown()) {
                    mImagePause.setVisibility(View.VISIBLE);
                    mViewController.show();
                }
            }

        }
        return true;
    }

    private MediaPlayer.OnCompletionListener mCompleteListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            mImagePause.setImageResource(android.R.drawable.ic_media_play);
            mImagePause.setVisibility(View.GONE);
        }
    };

    private MediaPlayer.OnErrorListener mOnErrorListener = new MediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {

            switch (what) {
                case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                    LogUtils.e("ThoNH", "MEDIA_ERROR_UNKNOWN");
                    Toast.makeText(mActivity, R.string.common_error, Toast.LENGTH_LONG).show();
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

            return true;
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
            return true;
        }
    };

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

    public void updatePausePlay() {
        LogUtils.e(TAG, "updatePausePlay : " + mPlayer.isPlaying());
        if (mPlayer.isPlaying()) {
            mImagePause.setImageResource(android.R.drawable.ic_media_pause);
        } else {
            mImagePause.setImageResource(android.R.drawable.ic_media_play);
        }
    }

}
