package com.vn.ntsc.ui.profile.media.videoaudio;

import com.vn.ntsc.core.BasePresenter;
import com.vn.ntsc.core.callback.SubscriberCallback;
import com.vn.ntsc.repository.model.videoaudio.VideoAudioResponse;
import com.vn.ntsc.repository.publicfile.PublicFileRequest;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by ThoNh on 11/20/2017.
 */

class VideoAudioPresenter extends BasePresenter<VideoAudioContract.View> implements VideoAudioContract.Presenter {
    @Inject
    public VideoAudioPresenter() {

    }

    @Override
    public void requestLstPublicVideoAudio(PublicFileRequest request) {
        addSubscriber(apiService.getVideoAudio(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new SubscriberCallback<VideoAudioResponse>(view) {
                    @Override
                    public void onSuccess(VideoAudioResponse response) {
                        view.getLstPublicVideoAudioSuccess(response);
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        view.getLstPublicVideoAudioFailure();
                    }

                    @Override
                    public void onCompleted() {
                        view.onComplete();
                    }
                }));
    }
}
