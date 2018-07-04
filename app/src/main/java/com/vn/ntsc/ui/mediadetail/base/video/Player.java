package com.vn.ntsc.ui.mediadetail.base.video;

import android.widget.MediaController;

/**
 * {@link MediaController.MediaPlayerControl}
 */

public interface Player {
    void start();

    void pause();

    int getDuration();

    /**
     * @return current playback position in milliseconds.
     */
    int getCurrentPosition();

    void seekTo(int pos);

    boolean isPlaying();

    /**
     * get buffer to load in setSecondaryProgress track
     * if load data from resource exist then mCurrentBufferPercentage default = 100
     *
     * @return
     */
    int getBufferPercentage();

    boolean canPause();

    boolean canSeekBackward();

    boolean canSeekForward();

    /**
     * Get the audio session id for the player used by this VideoView. This can be used to
     * apply audio effects to the audio track of a video.
     *
     * @return The audio session, or 0 if there was an error.
     */
    int getAudioSessionId();
}