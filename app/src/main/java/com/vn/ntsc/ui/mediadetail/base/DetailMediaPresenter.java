package com.vn.ntsc.ui.mediadetail.base;

import android.support.annotation.Nullable;

import com.vn.ntsc.repository.model.media.MediaEntity;
import com.vn.ntsc.utils.LogUtils;

import java.util.List;


public class DetailMediaPresenter extends BasePresenter implements IDetailMediaInteractor.Presenter {
    private static final String TAG = DetailMediaPresenter.class.getName();

    private final IDetailMediaInteractor.ActivityView mViewParent;
    private final IDetailMediaInteractor.AudioFragmentView mAudioFragmentView;
    private final IDetailMediaInteractor.VideoFragmentView mVideoFragmentView;
    private final IDetailMediaInteractor.ImageFragmentView mImageFragmentView;
    private final IDetailMediaInteractor.ViewPagerView mViewPagerView;
    private IDataModel mIDataModel;
    //save systemUiVisibility default
    private int mSystemUiVisibility;

    /**
     * contructor: builder pattern,
     * allow a view can create presenter can use method of itself and its parent view (activity)
     *
     * @param viewParent: pass viewParent = Activity
     * @param mView:      pass view = of Fragment
     */
    private DetailMediaPresenter(IDetailMediaInteractor.ActivityView viewParent, BaseView mView) {
        mIDataModel = SdcardRepository.getsInstance();

        //if at Activity
        //viewParent = null, view = IDetailMediaInteractor.ActivityView
        if (viewParent == null) {
            if (mView instanceof IDetailMediaInteractor.ActivityView) {
                this.mViewParent = (IDetailMediaInteractor.ActivityView) mView;
                mImageFragmentView = null;
                mVideoFragmentView = null;
                mAudioFragmentView = null;
                mViewPagerView = null;

                mViewParent.setPresenter(this);
                return;
            } else {
                throw new ClassCastException("class can not implement interface IDetailMediaInteractor.ActivityView!");
            }
        }

        //if at Fragment
        //viewParent = ActivityView, view = of Fragment
        this.mViewParent = viewParent;

        if (mView instanceof IDetailMediaInteractor.AudioFragmentView) {
            mImageFragmentView = null;
            mVideoFragmentView = null;
            mViewPagerView = null;

            mAudioFragmentView = (IDetailMediaInteractor.AudioFragmentView) mView;
            mAudioFragmentView.setPresenter(this);

        } else if (mView instanceof IDetailMediaInteractor.VideoFragmentView) {
            mViewPagerView = null;
            mImageFragmentView = null;
            mAudioFragmentView = null;

            mVideoFragmentView = (IDetailMediaInteractor.VideoFragmentView) mView;
            mVideoFragmentView.setPresenter(this);
        } else if (mView instanceof IDetailMediaInteractor.ImageFragmentView) {
            mViewPagerView = null;
            mVideoFragmentView = null;
            mAudioFragmentView = null;

            mImageFragmentView = (IDetailMediaInteractor.ImageFragmentView) mView;
            mImageFragmentView.setPresenter(this);
        } else if (mView instanceof IDetailMediaInteractor.ViewPagerView) {
            mVideoFragmentView = null;
            mAudioFragmentView = null;
            mImageFragmentView = null;

            mViewPagerView = (IDetailMediaInteractor.ViewPagerView) mView;
            mViewPagerView.setPresenter(this);
        } else {

            throw new ClassCastException("class can not implement interface correctly!");
        }
    }

    public static class Builder {
        private BaseView mView;
        private IDetailMediaInteractor.ActivityView mViewParent;

        /**
         * set view parent is Activity
         *
         * @param viewParent
         */
        public Builder(IDetailMediaInteractor.ActivityView viewParent) {
            this.mViewParent = viewParent;
        }

        /**
         * @param viewParent : nullable at DetailActivity, not need pass viewParent
         *                   ex: new DetailMediaPresenter.ViewPagerBuilder(null, this).build
         * @param mView
         */
        public Builder(@Nullable IDetailMediaInteractor.ActivityView viewParent, BaseView mView) {
            this.mViewParent = viewParent;
            this.mView = mView;
        }

        public Builder setView(@Nullable BaseView view) {
            this.mView = view;
            return this;
        }

        public DetailMediaPresenter build() {
            return new DetailMediaPresenter(this.mViewParent, this.mView);
        }
    }

    @Override
    public void loadImage(final int pos) {
        List<MediaEntity> mediaEntityList = mIDataModel.getListMediaPlaying();
        if (mediaEntityList.size() <= pos) {
            LogUtils.e(TAG, "pos out of range size list media !");
            return;
        }

        mImageFragmentView.loadImage(mediaEntityList.get(pos).mUrl);
    }

    @Override
    public void requestInitDataAudio(int pos) {
        //check file
        List<MediaEntity> mediaEntityList = mIDataModel.getListMediaPlaying();
        if (mediaEntityList == null || mediaEntityList.size() <= pos)
            return;

        MediaEntity mediaEntity = mediaEntityList.get(pos);

        //set name and author
//        mAudioFragmentView.setMediaName(mediaEntity..getNameFile());
//        mAudioFragmentView.setMediaAuthorName(fileEntity.getAuthor());

        //init audio
        mAudioFragmentView.showThumb(mediaEntity.mThumbnail);
        mAudioFragmentView.prepareDataAudio(mediaEntity.mUrl);
    }

    @Override
    public void requestInitDataVideo(int pos) throws Exception {
        //check file
        List<MediaEntity> mediaEntityList = mIDataModel.getListMediaPlaying();
        if (mediaEntityList.size() <= pos)
            return;

        //time seekTo
        int posPauseTimePlay = (int) mediaEntityList.get(pos).getCurrentPauseTime();
        mVideoFragmentView.prepareVideo(mediaEntityList.get(pos).mUrl, posPauseTimePlay);
    }

    @Override
    public void clearAllCurrentTimePause() {
        mIDataModel.clearAllCurrentTimePause();
    }

    @Override
    public void saveCurrentTimePause(int pos, int pauseTime) {
        mIDataModel.saveCurrentPauseMedia(pos, pauseTime);
    }

    @Override
    public void playVideo(int pos) {
        //check file
        List<MediaEntity> mediaEntityList = mIDataModel.getListMediaPlaying();
        if (mediaEntityList.size() <= pos)
            return;

        //time seekTo
        int posPauseTimePlay = (int) mediaEntityList.get(pos).getCurrentPauseTime();
        mVideoFragmentView.playVideo(posPauseTimePlay);

    }

    @Override
    public void saveDefaultArgSystemUiVisibility(int systemUiVisibility) {
        this.mSystemUiVisibility = systemUiVisibility;
    }

    @Override
    public int getSystemUiVisibilityDefaultSave() {
        return mSystemUiVisibility;
    }

    @Override
    public void playAudio(int pos) {
        //check file
        List<MediaEntity> mediaEntityList = mIDataModel.getListMediaPlaying();
        if (mediaEntityList.size() <= pos)
            return;

        //time seekTo
        int posPauseTimePlay = (int) mediaEntityList.get(pos).getCurrentPauseTime();
        mAudioFragmentView.playAudio(posPauseTimePlay);
    }
}