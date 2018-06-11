package com.vn.ntsc.ui.chat.generalibrary;

import com.vn.ntsc.core.BasePresenter;
import com.vn.ntsc.core.callback.SubscriberCallback;
import com.vn.ntsc.repository.model.chat.GeneraLibraryRequest;
import com.vn.ntsc.repository.model.chat.GeneraLibraryResponse;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Doremon on 2/28/2018.
 */

public class GeneraLibraryPresenter extends BasePresenter<GeneraLibraryContract.View> implements GeneraLibraryContract.Presenter {

    @Inject
    public GeneraLibraryPresenter() {

    }

    @Override
    public void getAllFileChat(GeneraLibraryRequest request) {
        addSubscriber(apiService.getAllFile(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new SubscriberCallback<GeneraLibraryResponse>(view) {
                    @Override
                    public void onSuccess(GeneraLibraryResponse response) {
                        view.onAllFileChat(response);
                    }

                    @Override
                    public void onCompleted() {
                        view.onComplete();
                    }
                }));
    }
}
