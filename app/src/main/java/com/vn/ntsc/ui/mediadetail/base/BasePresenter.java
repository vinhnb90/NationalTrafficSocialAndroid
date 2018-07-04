package com.vn.ntsc.ui.mediadetail.base;

import com.vn.ntsc.repository.model.media.MediaEntity;

import java.util.List;


public class BasePresenter implements BaseInteractor.Presenter {
    private IDataModel mISdcardModel;

    public BasePresenter() {
        this.mISdcardModel = SdcardRepository.getsInstance();

    }
    @Override
    public int getPositionMediaPlayingNow() {
        return mISdcardModel.getPositionMediaPlayingNow();
    }

    @Override
    public void savePositionMediaPlayingNow(int position) {
        mISdcardModel.savePositionMedia(position);
    }

    @Override
    public List<MediaEntity> getListMediaPlaying() {
        return mISdcardModel.getListMediaPlaying();
    }

    @Override
    public void saveListMediaPlaying(List<MediaEntity> mediaEntityLíst) {
        mISdcardModel.saveListMedia(mediaEntityLíst);
    }

    public MediaEntity getItemMediaPlaying() {
        return mISdcardModel.getItemMediaPlaying();
    }
}