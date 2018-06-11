package com.vn.ntsc.ui.chat.audio;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.UiThread;

import java.io.IOException;

/**
 * Created by dev22 on 1/17/18.
 * class for play audio on chat
 */
public class SequenceAudioPlayer implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {
    /**
     * @return current position if media prepared, else return 0
     */
    public int getCurrentPosition() {
        if (isPrepared()) {
            return mediaPlayer.getCurrentPosition() / 1000;
        }
        return 0;
    }

    public interface MediaEventListener {
        /**
         * state prepare error
         *
         * @param id position to notify
         */
        void onError(int id);

        /**
         * state playing audio (prepared)
         *
         * @param id position to notify
         */
        void onPlay(int id);

        /**
         * state pause audio (prepared)
         *
         * @param id position to notify
         */
        void onPause(int id);

        /**
         * prepare audio to play
         *
         * @param id position to notify
         */
        void onPrepare(int id);

        /**
         * when audio prepared and complete (prepared)
         *
         * @param id position to notify
         */
        void onComplete(int id);
    }

    private MediaPlayer mediaPlayer;
    private MediaEventListener listener;
    /**
     * store current audio url to decide init media player again or not
     */
    private String url;
    /**
     * true: ready to play
     */
    private boolean isPrepared = false;
    /**
     * chat items may have same link to play audio, id make function {@link (int)} work correct
     */
    private int id;


    public SequenceAudioPlayer(MediaEventListener mediaEventListener) {
        listener = mediaEventListener;
    }

    /**
     * release and re-create media player instance then play
     */
    private void initMediaPlayer(int id, String url) {
        this.id = id;
        isPrepared = false;
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);

        playAudio(url);
    }

    /**
     * play|stop audio
     *
     * @param id  position of item
     * @param url audio source url to play
     */
    @UiThread
    public void toggle(@IntRange(from = 0) int id, @NonNull String url) {
        if (!url.equals(this.url)) {
            // case change url audio
            initMediaPlayer(id, url);
        } else {
            // case have same url
            if (isPrepared()) {
                // case prepared audio
                toggle(id);
            } else {
                // case audio is not prepared -> init again
                initMediaPlayer(id, url);
            }
        }
    }

    /**
     * toggle media player start|stop
     */
    private void toggle(int id) {
        if (mediaPlayer.isPlaying()) {
            if (id == this.id) {
                // case just click on same id (position)
                listener.onPause(id);
                mediaPlayer.pause();
            } else {
                // case have same link but in difference position -> just begin from start
                listener.onComplete(this.id);

                this.id = id;
                mediaPlayer.seekTo(0);
                listener.onPlay(id);
                mediaPlayer.start();
            }
        } else {
            listener.onPlay(id);
            mediaPlayer.start();
        }
    }


    /**
     * @return true: if media player is ready to play
     */
    private boolean isPrepared() {
        return mediaPlayer != null && isPrepared;
    }

    /**
     * prepare and play audio
     *
     * @param url audio source url to play
     */
    private void playAudio(String url) {
        this.url = url;
        try {
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepareAsync();
            listener.onPrepare(id);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        listener.onPlay(id);
        mp.start();
        isPrepared = true;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        listener.onComplete(id);
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        listener.onError(id);
        return true;
    }
}
