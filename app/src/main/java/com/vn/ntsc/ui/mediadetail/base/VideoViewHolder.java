package com.vn.ntsc.ui.mediadetail.base;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import com.vn.ntsc.R;
import com.vn.ntsc.ui.mediadetail.base.video.PlayerController;
import com.vn.ntsc.ui.mediadetail.base.video.TextureVideoView;
import com.vn.ntsc.ui.mediadetail.base.video.VideoTouchRoot;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class VideoViewHolder extends BaseMediaHolder implements
        PlayerController.VisibilityListener,
        PlayerController.VolumeListener
{
    /*----------------------------------------var----------------------------------------*/
    private IteractionVideo mIteractionVideo;
    private Unbinder mUnbinder;

    @BindView(R.id.fragment_video_texture_video)
    TextureVideoView textureVideoView;

    @BindView(R.id.fragment_video_controller)
    PlayerController playerController;

    @BindView(R.id.fragment_video_video_touch_root)
    VideoTouchRoot videoTouchRoot;
    /*----------------------------------------instance----------------------------------------*/

    public VideoViewHolder(Context activity, View view, IteractionVideo iteractionVideo, int pos) {
        super(activity);
        mIteractionVideo = iteractionVideo;
        mUnbinder = ButterKnife.bind(this, view);

        playerController.setVisibilityListener(this);
        playerController.setVolumeListener(this);
        textureVideoView.setMediaController(playerController);
        textureVideoView.setOnPlayStateListener(playerController);
        textureVideoView.setIteractionVideo(iteractionVideo);
        videoTouchRoot.setOnTouchReceiver(playerController);

        textureVideoView.setPos(pos);
        videoTouchRoot.setOnTouchReceiver(playerController);

    }

    /*----------------------------------------lifecycle----------------------------------------*/
    /*----------------------------------------override----------------------------------------*/

    @Override
    public void onFullScreen(boolean needFull) {
        if (mActivity instanceof PlayerController.VisibilityListener) {
            ((PlayerController.VisibilityListener) mActivity).onFullScreen(needFull);
        }

        if (needFull) {
            hideSystemUI();
            //config hide all view of activity
        } else showSystemUI();
    }

    @Override
    public void setVolumeMute(boolean isMute) {
        textureVideoView.setVolumeMute(isMute);
    }

    /*----------------------------------------func----------------------------------------*/
    public void prepareVideo(String url, int posPauseTimePlay) {
        textureVideoView.setVideo(url, posPauseTimePlay);
        textureVideoView.seekTo(posPauseTimePlay);
    }


    public void playVideo(int posPauseTimePlay) {
        textureVideoView.seekTo(posPauseTimePlay);
        textureVideoView.start();
    }

    public int pauseVideo() {
        textureVideoView.pause();
        return textureVideoView.getCurrentPosition();
    }

    public void resetFullScreen() {
        playerController.resetFullScreen();
    }

    public void stopVideo() {
        textureVideoView.stop();
    }

    public void unbind() {
        mUnbinder.unbind();
    }

    private void hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        View decorView = ((Activity) mActivity).getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    private void showSystemUI() {
        View decorView = ((Activity) mActivity).getWindow().getDecorView();
        decorView.setSystemUiVisibility(mIteractionVideo.getSystemUiVisibilityDefaultSave());
    }

    public void release(boolean clearTargetState) {
        textureVideoView.release(clearTargetState);
    }

    public void showLoadingVideo() {
        textureVideoView.showLoadingVideo();
    }

    /*----------------------------------------inner----------------------------------------*/

    /**
     * this interface callback to interaction data save
     */
    public interface IteractionVideo {
        int getSystemUiVisibilityDefaultSave();
        boolean isAreadyVisibleToUser();
    }
}
