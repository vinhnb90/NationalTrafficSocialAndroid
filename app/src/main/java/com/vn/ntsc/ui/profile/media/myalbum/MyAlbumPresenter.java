package com.vn.ntsc.ui.profile.media.myalbum;

import android.util.Log;

import com.vn.ntsc.core.BasePresenter;
import com.vn.ntsc.core.callback.SubscriberCallback;
import com.vn.ntsc.core.model.NetworkError;
import com.vn.ntsc.repository.model.myalbum.LoadAlbum.LoadAlbumRequest;
import com.vn.ntsc.repository.model.myalbum.LoadAlbum.LoadAlbumResponse;
import com.vn.ntsc.repository.model.myalbum.UpdateAlbum.UpdateAlbumRequest;
import com.vn.ntsc.repository.model.myalbum.UpdateAlbum.UpdateAlbumResponse;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by ThoNh on 11/14/2017.
 */

public class MyAlbumPresenter extends BasePresenter<MyAlbumContract.View> implements MyAlbumContract.Presenter {

    @Inject
    public MyAlbumPresenter() {

    }

    @Override
    public void getMyAlbum(LoadAlbumRequest request) {
        addSubscriber(apiService.getMyAlbum(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new SubscriberCallback<LoadAlbumResponse>(view) {
                    @Override
                    public void onSuccess(LoadAlbumResponse response) {
                        view.getAlbumSuccess(response);
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        view.getAlbumFailure(e);
                    }

                    @Override
                    public void onCompleted() {
                        view.getAlbumComplete();
                    }
                }));
    }

    @Override
    public void updateMyAlbum(UpdateAlbumRequest request) {
        addSubscriber(apiService.updateMyAlbum(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new SubscriberCallback<UpdateAlbumResponse>(view) {
                    @Override
                    public void onSuccess(UpdateAlbumResponse response) {
                        view.updateAlbumSuccess();
                    }

                    @Override
                    public void onCompleted() {
                        view.updateComplete();
                    }
                }));
    }


    public void getMoreMyAlbum(LoadAlbumRequest request) {
        addSubscriber(apiService.getMyAlbum(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new SubscriberCallback<LoadAlbumResponse>(view) {
                    @Override
                    public void onSuccess(LoadAlbumResponse response) {
                        view.getMoreAlbumSuccess(response);
                    }

                    @Override
                    public void onCompleted() {
                        view.updateComplete();
                    }
                }));
    }
}
