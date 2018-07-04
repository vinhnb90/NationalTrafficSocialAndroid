package com.vn.ntsc.ui.mediadetail.base;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.vn.ntsc.R;

import static com.vn.ntsc.ui.mediadetail.util.Utils.ARG_POSITION_MEDIA;

public class VideoFragment extends Fragment implements IDetailMediaInteractor.VideoFragmentView, VideoViewHolder.IteractionVideo {
    /*----------------------------------------var----------------------------------------*/

    private IDetailMediaInteractor.Presenter mIPresenter;
    private int mPos;

    //when viewpager load first n pager,
    //need flag detect is prepare done data (isFirstInitialView = true) then play video
    private boolean isFirstInitialView;

    //if has error when init view then show message
    private boolean isErrorView;

    //when already visible to user then play video
    private boolean alreadyVisibleToUser;

    private VideoViewHolder mVideoViewHolder;

    /*----------------------------------------instance----------------------------------------*/
    public VideoFragment() {
        // Required empty public constructor
    }

    public static VideoFragment newInstance(int position) {
        VideoFragment fragment = new VideoFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_POSITION_MEDIA, position);
        fragment.setArguments(args);
        return fragment;
    }

    /*----------------------------------------lifecycle----------------------------------------*/

    /**
     * if isVisibleToUser and isFirstInitialView ok not error then play video
     * else pause
     *
     * @param isVisibleToUser
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            alreadyVisibleToUser = true;
            if (isFirstInitialView) {
                mIPresenter.savePositionMediaPlayingNow(mPos);
                mIPresenter.playVideo(mPos);
            }

            if (isErrorView)
                Toast.makeText(getActivity(), getResources().getString(R.string.play_video_error), Toast.LENGTH_SHORT).show();
        } else {
            if (alreadyVisibleToUser) {
                alreadyVisibleToUser = false;
                pauseVideo();

                //reset mode fullscreen
                mVideoViewHolder.resetFullScreen();
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            mPos = getArguments().getInt(ARG_POSITION_MEDIA);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            mPos = savedInstanceState.getInt(ARG_POSITION_MEDIA);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_video, container, false);
        try {
            //init
            new DetailMediaPresenter.Builder((IDetailMediaInteractor.ActivityView) getActivity(), this).build();

            mVideoViewHolder = new VideoViewHolder(getActivity(), view, this, mPos);

            //in first create view
            //begin prepare Video
            mIPresenter.requestInitDataVideo(mPos);

            //save arg UI first time and use for restore state exits full screen
            mIPresenter.saveDefaultArgSystemUiVisibility(getActivity().getWindow().getDecorView().getSystemUiVisibility());

            isFirstInitialView = true;
        } catch (Exception e) {
            //if error
            isErrorView = true;
        }

        //if alreadyVisibleToUser
        //play video
        if (!isErrorView && alreadyVisibleToUser) {
            //save pos playing
            mIPresenter.savePositionMediaPlayingNow(mPos);

            playVideo(0);
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        //TODO
        //play audio resume
        if (getUserVisibleHint()) {
            mIPresenter.playVideo(mPos);
            mVideoViewHolder.showLoadingVideo();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        int currentDuration = mVideoViewHolder.pauseVideo();
        mIPresenter.saveCurrentTimePause(mPos, currentDuration);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isErrorView = false;
        isFirstInitialView = false;
        alreadyVisibleToUser = false;

        mVideoViewHolder.stopVideo();
        mVideoViewHolder.release(true);
        //reset mode fullscreen
        mVideoViewHolder.resetFullScreen();
    }

    /*----------------------------------------override----------------------------------------*/

    @Override
    public void prepareVideo(String url, int posPauseTimePlay) {
        mVideoViewHolder.prepareVideo(url, posPauseTimePlay);
    }

    @Override
    public void playVideo(int posPauseTimePlay) {
        mVideoViewHolder.playVideo(posPauseTimePlay);
    }

    @Override
    public void pauseVideo() {
        int currentDuration = mVideoViewHolder.pauseVideo();
        mIPresenter.saveCurrentTimePause(mPos, currentDuration);
    }

    @Override
    public void setPresenter(@NonNull IDetailMediaInteractor.Presenter presenter) {
        mIPresenter = presenter;
    }

    @Override
    public int getSystemUiVisibilityDefaultSave() {
        return mIPresenter.getSystemUiVisibilityDefaultSave();
    }

    @Override
    public boolean isAreadyVisibleToUser() {
        return alreadyVisibleToUser;
    }


    /*----------------------------------------func----------------------------------------*/

    public VideoViewHolder getVideoViewHolder() {
        return mVideoViewHolder;
    }

    /*----------------------------------------inner----------------------------------------*/
}


