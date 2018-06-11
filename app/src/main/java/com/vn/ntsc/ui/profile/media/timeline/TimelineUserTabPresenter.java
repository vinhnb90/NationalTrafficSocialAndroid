package com.vn.ntsc.ui.profile.media.timeline;

import com.vn.ntsc.core.BasePresenter;
import com.vn.ntsc.core.callback.SubscriberCallback;
import com.vn.ntsc.repository.publicfile.PublicFileRequest;
import com.vn.ntsc.repository.publicfile.PublicFileResponse;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by ThoNh on 11/21/2017.
 */

class TimelineUserTabPresenter extends BasePresenter<TimelineUserTabContract.View> implements TimelineUserTabContract.Presenter{
    @Inject
    public TimelineUserTabPresenter(){

    }

    @Override
    public void getPublicMedia(PublicFileRequest request){
        addSubscriber(apiService.getPublicFiles(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new SubscriberCallback<PublicFileResponse>(view) {
                    @Override
                    public void onSuccess(PublicFileResponse response) {
                        view.onGetListPublicImage(response);
                    }

                    @Override
                    public void onCompleted() {
                        view.onComplete();
                    }
                }));
    }
}
