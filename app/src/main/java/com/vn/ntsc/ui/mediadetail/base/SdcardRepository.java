package com.vn.ntsc.ui.mediadetail.base;

import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.tux.mylab.gallery.data.MediaFile;
import com.vn.ntsc.repository.model.media.MediaEntity;
import com.vn.ntsc.utils.LogUtils;
import com.vn.ntsc.utils.Utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


public class SdcardRepository implements IDataModel {
    private static final String TAG = SdcardRepository.class.getName();

    private static SdcardRepository sInstance;
    private int mPosItemPlaying;
    private List<MediaEntity> mMediaEntities = new ArrayList<>();

    private SdcardRepository() {
    }

    public static synchronized SdcardRepository getsInstance() {
        if (sInstance == null)
            sInstance = new SdcardRepository();
        return sInstance;
    }


    @Override
    public void savePositionMedia(@IntRange(from = 0) int position) {
        mPosItemPlaying = position;
    }

    @Override
    public int getPositionMediaPlayingNow() {
        return mPosItemPlaying;
    }

    @Override
    public List<MediaEntity> getListMediaPlaying() {
        return mMediaEntities;
    }

    @Override
    public void saveListMedia(@NonNull List<MediaEntity> mediaEntities) {
        mMediaEntities.clear();
        mMediaEntities.addAll(mediaEntities);
        mPosItemPlaying = 0;
    }

    @Override
    public MediaEntity getItemMediaPlaying() {
        if (mPosItemPlaying > mMediaEntities.size() - 1)
            return null;

        return mMediaEntities.get(mPosItemPlaying);
    }

    @Override
    public void saveCurrentPauseMedia(int pos, long currentDuration) {
        if (pos > mMediaEntities.size() - 1) {
            LogUtils.e(TAG, "saveCurrentPauseMedia at pos " + pos + " index out of range");
            return;
        }

        mMediaEntities.get(pos).setCurrentPauseTime((int) currentDuration);
    }

    @Override
    public void clearAllCurrentTimePause() {
        for (MediaEntity entity :
                mMediaEntities) {
            entity.setCurrentPauseTime(0);
        }
    }

//
//    @Override
//    public void saveCurrentPauseMedia(int pos, long currentDuration) {
//        FileEntity fileEntity = mMediaEntities.get(mPosItemPlaying);
//        if (fileEntity instanceof SoundFile)
//            ((SoundFile) mMediaEntities.get(pos)).setCurrentPauseTime(currentDuration);
//    }
//
//    @Override
//    public FileEntity getItemMediaPlaying() {
//        return mMediaEntities.get(mPosItemPlaying);
//    }
//
//    @Override
//    public void saveImageAfterLoad(int pos, Bitmap bitmap) {
//        if (mMediaEntities.get(pos) instanceof ImageEntity)
//            ((ImageEntity) mMediaEntities.get(pos)).setImage(bitmap);
//    }
//
//    @Override
//    public void saveThumbAudioAfterLoad(int pos, Bitmap bitmap) {
//        mMediaEntities.get(pos).setThumb(bitmap);
//    }
//
//    @Override
//    public void clearAllCurrentTimePause() {
//        for (FileEntity entity :
//                mMediaEntities) {
//            if (entity instanceof SoundFile)
//                ((SoundFile) entity).setCurrentPauseTime(0);
//        }
//    }
}
