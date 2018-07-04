package com.vn.ntsc.ui.mediadetail.base;

import android.support.annotation.NonNull;


public interface IDetailMediaInteractor {
    interface ActivityView extends BaseView<Presenter> {
    }

    interface ImageFragmentView extends BaseView<Presenter> {
        /**
         * get max size system openGL can load image
         * get image origin size with glide
         * resize image
         * send to view a uri
         * view load image from uri
         * fill image to image view
         *
         * @param urlFile
         */
        void loadImage(String urlFile);

    }

    interface AudioFragmentView extends BaseView<Presenter> {

        void showThumb(@NonNull String thumbUrl);


        /**
         * after prepare audio data
         * view init media player and set source
         *
         * @param uri
         */
        void prepareDataAudio(@NonNull String uri);

        /**
         * check file audio
         * pause audio
         * and save current time pause
         */
        void pauseAudio();

        /**
         * stop audio
         */
        void stopAudio();


        /**
         * check state
         * release
         * clear pos current pause time
         */
        void releaseAudio();

        void playAudio(int posPauseTimePlay);
    }

    interface VideoFragmentView extends BaseView<Presenter> {

        /**
         * set uri
         * set media controller
         * set seek to
         *
         * @param url
         * @param posPauseTimePlay
         */
        void prepareVideo(String url, int posPauseTimePlay);

        /**
         * set data source to video view widget UI
         * show media controller
         *
         * @param posPauseTimePlay
         */
        void playVideo(int posPauseTimePlay);

        /**
         * pause video view
         * hide media controller
         */
        void pauseVideo();

    }

    interface VideoFragmentCustomView extends BaseView<Presenter> {

    }

    public interface ViewPagerView extends BaseView<Presenter> {
    }

    interface Presenter extends BaseInteractor.Presenter {
        //------------------------------------------------------------Image Fragment

        /**
         * load Image with Glide
         *
         * @param pos: position media
         */
        void loadImage(int pos);

        //Audio Fragment

        /**
         * set data source, reset media (if exist)
         * load thumb audio
         * set text audio (author, name)
         *
         * @param pos: position media
         */
        void requestInitDataAudio(int pos);


        //------------------------------------------------------------Video Fragment

        /**
         * prepare video in first onCreateView to do:
         * check file
         * create media controller
         *
         * @param pos
         * @throws Exception: pos out of range list entity
         */
        void requestInitDataVideo(int pos) throws Exception;

        /**
         * clear all CurrentTimePause when finish detail activity
         */
        void clearAllCurrentTimePause();

        //------------------------------------------------------------ViewPager Fragment

        /**
         * save currentPauseTime
         *
         * @param pos
         * @param currentTimeTrack
         */
        void saveCurrentTimePause(int pos, int currentTimeTrack);

        /**
         * get currentPauseTime old and
         * play video
         *
         * @param pos
         */
        void playVideo(int pos);

        /**
         * save SystemUiVisibility to restore mode visible when swipe view page from video to other pager
         * reset if video is mode fullscreen
         *
         * @param systemUiVisibility
         */
        void saveDefaultArgSystemUiVisibility(int systemUiVisibility);

        /**
         * get default flag SystemUiVisibility
         *
         * @return
         */
        int getSystemUiVisibilityDefaultSave();

        /**
         * get time pause old
         * play Audio
         *
         * @param pos
         */
        void playAudio(int pos);

    }
}


