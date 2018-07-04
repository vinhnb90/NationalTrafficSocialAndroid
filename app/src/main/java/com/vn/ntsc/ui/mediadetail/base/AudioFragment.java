package com.vn.ntsc.ui.mediadetail.base;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.vn.ntsc.R;
import com.vn.ntsc.ui.mediadetail.util.Utils;

import static com.vn.ntsc.ui.mediadetail.util.Utils.ARG_POSITION_MEDIA;

public class AudioFragment extends Fragment implements IDetailMediaInteractor.AudioFragmentView, AudioViewHolder.IteractionAudio {

    /*----------------------------------------var----------------------------------------*/
    private IDetailMediaInteractor.Presenter mIPresenter;
    private int mPos;

    //when viewpager load first n pager,
    //need flag detect is prepare done data (isFirstInitialView = true) then play audio
    private boolean isFirstInitialView;

    //if has error when init view then show message
    private boolean isErrorView;

    //when already visible to user then play audio
    private boolean alreadyVisibleToUser;

    private AudioViewHolder mAudioViewHolder;

    /*----------------------------------------instance----------------------------------------*/
    public AudioFragment() {
        // Required empty public constructor
    }

    public static AudioFragment newInstance(int position) {
        AudioFragment fragment = new AudioFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(Utils.ARG_POSITION_MEDIA, position);
        fragment.setArguments(bundle);

        return fragment;
    }

    /*----------------------------------------lifecycle----------------------------------------*/

    /**
     * if isVisibleToUser and isFirstInitialView ok not error then play audio
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
                mIPresenter.playAudio(mPos);
            }

            if (isErrorView)
                Toast.makeText(getActivity(), getResources().getString(R.string.play_audio_error), Toast.LENGTH_SHORT).show();
        } else {
            if (alreadyVisibleToUser) {
                pauseAudio();
                alreadyVisibleToUser = false;
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mPos = getArguments().getInt(ARG_POSITION_MEDIA);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_media_detail_audio, container, false);

        try {
            //pass parent view and this view to Presenter
            new DetailMediaPresenter.Builder((IDetailMediaInteractor.ActivityView) getActivity(), this).build();

            mAudioViewHolder = new AudioViewHolder(getActivity(), this, view);
            mAudioViewHolder.setPos(mPos);

            //get time paused ,get URL, Get thumb, prepare sync buffer audio
            //requestInitDataAudio --> prepareDataAudio
            mIPresenter.requestInitDataAudio(mPos);

            //flag initial done, prepare to play if alreadyVisibleToUser
            isFirstInitialView = true;
        } catch (Exception e) {
            //error
            isErrorView = true;
        }

        if (!isErrorView && alreadyVisibleToUser) {
            //init alreadyVisibleToUser then play media
            mIPresenter.savePositionMediaPlayingNow(mPos);

            //wish is play audio
            mIPresenter.playAudio(mPos);
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        //play audio resume
        if (getUserVisibleHint()) {
            mIPresenter.playAudio(mPos);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        int currentDuration = mAudioViewHolder.pauseAudio();
        mIPresenter.saveCurrentTimePause(mPos, currentDuration);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isErrorView = false;
        isFirstInitialView = false;
        alreadyVisibleToUser = false;

        mAudioViewHolder.stopAudio();
        mAudioViewHolder.release(true);
    }

    /*----------------------------------------override----------------------------------------*/
    @Override
    public void prepareDataAudio(@NonNull String uri) {
        mAudioViewHolder.prepareDataAudio(uri);
    }

    @Override
    public void pauseAudio() {
        int currentDuration = mAudioViewHolder.pauseAudio();
        mIPresenter.saveCurrentTimePause(mPos, currentDuration);
    }

    @Override
    public void stopAudio() {
        int currentTimeTrack = mAudioViewHolder.stopAudio();
        mIPresenter.saveCurrentTimePause(mPos, currentTimeTrack);
    }

    @Override
    public void releaseAudio() {
        mAudioViewHolder.release(true);
    }

    @Override
    public void playAudio(int posPauseTimePlay) {
        mAudioViewHolder.playAudio(posPauseTimePlay);
    }

    @Override
    public void showThumb(@NonNull final String thumbUrl) {

        //check case load thread done bitmap but destroyed view
        mAudioViewHolder.loadThumb(thumbUrl);
    }

    @Override
    public void setPresenter(@NonNull IDetailMediaInteractor.Presenter presenter) {
        mIPresenter = presenter;
    }

    /*----------------------------------------func----------------------------------------*/

    public AudioViewHolder getAudioViewHolder() {
        return mAudioViewHolder;
    }

    @Override
    public boolean isAreadyVisibleToUser() {
        return alreadyVisibleToUser;
    }
    /*----------------------------------------inner----------------------------------------*/
}


