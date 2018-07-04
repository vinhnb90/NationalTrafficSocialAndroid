package com.vn.ntsc.ui.mediadetail.util;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.vn.ntsc.ui.mediadetail.util.StateMediaPlayer.END;
import static com.vn.ntsc.ui.mediadetail.util.StateMediaPlayer.ERROR;
import static com.vn.ntsc.ui.mediadetail.util.StateMediaPlayer.IDLE;
import static com.vn.ntsc.ui.mediadetail.util.StateMediaPlayer.INIT;
import static com.vn.ntsc.ui.mediadetail.util.StateMediaPlayer.PAUSED;
import static com.vn.ntsc.ui.mediadetail.util.StateMediaPlayer.PLAY_COMPLETE;
import static com.vn.ntsc.ui.mediadetail.util.StateMediaPlayer.PREPARED;
import static com.vn.ntsc.ui.mediadetail.util.StateMediaPlayer.PREPARING;
import static com.vn.ntsc.ui.mediadetail.util.StateMediaPlayer.STARTED;
import static com.vn.ntsc.ui.mediadetail.util.StateMediaPlayer.STOPED;


@Retention(RetentionPolicy.SOURCE)
@IntDef({IDLE, INIT, PREPARING, PREPARED, STARTED, PAUSED, STOPED, PLAY_COMPLETE, ERROR, END})
public @interface StateMediaPlayer {
    int IDLE = 0;
    int INIT = 1;
    int PREPARING = 2;
    int PREPARED = 3;
    int STARTED = 4;
    int PAUSED = 5;
    int STOPED = 6;
    int PLAY_COMPLETE = 7;
    int ERROR = 8;
    int END = 9;
}