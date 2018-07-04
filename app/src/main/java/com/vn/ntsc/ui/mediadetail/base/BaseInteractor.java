package com.vn.ntsc.ui.mediadetail.base;

import com.vn.ntsc.repository.model.media.MediaEntity;

import java.util.List;


public interface BaseInteractor {
    interface Presenter {
        int getPositionMediaPlayingNow();

        void savePositionMediaPlayingNow(int position);

        List<MediaEntity> getListMediaPlaying();

        void saveListMediaPlaying(List<MediaEntity> mediaEntityLíst);
    }
}
