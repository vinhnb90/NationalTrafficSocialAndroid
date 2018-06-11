package com.vn.ntsc.ui.profile.media.albumDetail;

import com.vn.ntsc.core.BasePresenter;
import com.vn.ntsc.core.callback.SubscriberCallback;
import com.vn.ntsc.repository.model.myalbum.DeleteAlbum.DelAlbumRequest;
import com.vn.ntsc.repository.model.myalbum.DeleteAlbum.DelAlbumResponse;
import com.vn.ntsc.repository.model.myalbum.DeleteImageInAlbum.DelAlbumImageRequest;
import com.vn.ntsc.repository.model.myalbum.DeleteImageInAlbum.DelAlbumImageResponse;
import com.vn.ntsc.repository.model.myalbum.ItemImageInAlbum;
import com.vn.ntsc.repository.model.myalbum.LoadImageInAlbum.LoadAlbumImageRequest;
import com.vn.ntsc.repository.model.myalbum.LoadImageInAlbum.LoadAlbumImageResponse;
import com.vn.ntsc.repository.model.myalbum.UpdateAlbum.UpdateAlbumRequest;
import com.vn.ntsc.repository.model.myalbum.UpdateAlbum.UpdateAlbumResponse;
import com.vn.ntsc.utils.LogUtils;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by ThoNh on 11/15/2017.
 */

public class MyAlbumDetailPresenter extends BasePresenter<MyAlbumDetailContract.View> implements MyAlbumDetailContract.Presenter {
    @Inject
    public MyAlbumDetailPresenter() {

    }

    @Override
    public void getImageAlbum(LoadAlbumImageRequest request) {
        addSubscriber(apiService.getImageAlbum(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(
                        new SubscriberCallback<LoadAlbumImageResponse>(view) {
                            @Override
                            public void onSuccess(LoadAlbumImageResponse response) {
                                view.getImageAlbumSuccess(response);
                            }

                            @Override
                            public void onError(Throwable e) {
                                super.onError(e);
                                view.getImageAlbumFailure();
                            }

                            @Override
                            public void onCompleted() {
                                view.getImageAlbumComplete();
                            }
                        }));
    }


    @Override
    public void deteleAlbum(DelAlbumRequest request) {

        addSubscriber(apiService.deleteAlbum(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new SubscriberCallback<DelAlbumResponse>(view) {
                    @Override
                    public void onSuccess(DelAlbumResponse response) {
                        LogUtils.e("ThoNH","deteleAlBum --> onSuccess :" + response.toString());
                        view.deleteAlbumSuccess();
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                    }

                    @Override
                    public void onCompleted() {
                        view.deleteAlbumComplete();
                    }
                }));

    }

    @Override
    public void deleteImagesInAlbum(DelAlbumImageRequest request, final List<ItemImageInAlbum> imagesSelected) {
        addSubscriber(apiService.deleteImagesInAlbum(request)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeWith(new SubscriberCallback<DelAlbumImageResponse>(view) {
            @Override
            public void onSuccess(DelAlbumImageResponse response) {
                view.deleteImagesInAlbumSuccess(response,imagesSelected);
            }

            @Override
            public void onCompleted() {
                view.deleteImagesInAlbumComplete();
            }
        }));
    }



    @Override
    public void updateAlbum(UpdateAlbumRequest request) {
        addSubscriber(apiService.updateMyAlbum(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new SubscriberCallback<UpdateAlbumResponse>(view) {
                    @Override
                    public void onSuccess(UpdateAlbumResponse response) {
                        view.updateAlbumSuccess(response);
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        view.updateAlbumFailure();
                    }

                    @Override
                    public void onCompleted() {
                        view.updateComplete();
                    }
                }));
    }
}
