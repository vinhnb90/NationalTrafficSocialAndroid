package com.vn.ntsc.ui.mediadetail.base;

import android.content.Context;

import com.vn.ntsc.repository.TypeView;
import com.vn.ntsc.repository.model.media.MediaEntity;

/**
 * Created by ThoNh on 11/21/2017.
 */

public abstract class BaseMediaHolder {

//    public MediaEntity mMediaEntity;

    public Context mActivity;

    public BaseMediaHolder(Context activity
//            , MediaEntity mediaEntity
    ) {
//        this.mMediaEntity = mediaEntity;
        this.mActivity = activity;
//        this.mMediaEntity.setOnPageChangeListener(this);
//        this.mMediaEntity.setActivityListener(this);
    }


//    /**
//     * @return Position of item In Adapter
//     */
//    public int getPositionMediaInAdapter() {
//        return mMediaEntity.mPosition;
//    }
//
//    /**
//     * @return Type of media
//     */
//    public @TypeView.MediaDetailType
//    String getMediaType() {
//        return mMediaEntity.mType;
//    }

}
