package com.vn.ntsc.ui.mediadetail.base;

import android.graphics.Bitmap;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;

import com.example.tux.mylab.gallery.data.MediaFile;
import com.vn.ntsc.repository.model.media.MediaEntity;

import java.util.List;


public interface IDataModel {
    void savePositionMedia(@IntRange(from = 0) int position);

    int getPositionMediaPlayingNow();

    List<MediaEntity> getListMediaPlaying();

    void saveListMedia(@NonNull List<MediaEntity> mediaEntities);

    MediaEntity getItemMediaPlaying();

    void saveCurrentPauseMedia(int pos, long currentDuration);

//    void saveImageAfterLoad(int pos, Bitmap bitmap);
//
//    void saveThumbAudioAfterLoad(int pos, Bitmap bitmap);

    /**
     * clear all when destroy detail media screen
     */
    void clearAllCurrentTimePause();
}
