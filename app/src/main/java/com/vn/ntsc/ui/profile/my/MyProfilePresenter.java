package com.vn.ntsc.ui.profile.my;

import com.vn.ntsc.core.BasePresenter;
import com.vn.ntsc.core.callback.SubscriberCallback;
import com.vn.ntsc.repository.model.block.addblock.AddBlockResponse;
import com.vn.ntsc.repository.model.block.addblock.AddBlockUserRequest;
import com.vn.ntsc.repository.model.favorite.AddFavoriteRequest;
import com.vn.ntsc.repository.model.favorite.AddFavoriteResponse;
import com.vn.ntsc.repository.model.favorite.RemoveFavoriteRequest;
import com.vn.ntsc.repository.model.favorite.RemoveFavoriteResponse;
import com.vn.ntsc.repository.model.report.ReportRequest;
import com.vn.ntsc.repository.model.report.ReportResponse;
import com.vn.ntsc.repository.model.user.GetUserInfoResponse;
import com.vn.ntsc.repository.model.user.UserInfoRequest;
import com.vn.ntsc.utils.SystemUtils;
import com.vn.ntsc.utils.cache.CacheJson;
import com.vn.ntsc.utils.cache.CacheType;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by nankai on 9/12/2017.
 */

public class MyProfilePresenter extends BasePresenter<MyProfileContract.View> implements MyProfileContract.Presenter {

    private CacheJson cacheJson;

    @Inject
    MyProfilePresenter() {
    }

    @Override
    public void setFavorite(AddFavoriteRequest addFavoriteRequest, final String userId) {
        addSubscriber(apiService.onAddFavorite(addFavoriteRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new SubscriberCallback<AddFavoriteResponse>(view) {
                    @Override
                    public void onSuccess(AddFavoriteResponse response) {
                        view.onFavoriteResponse(response, userId);
                    }

                    @Override
                    public void onCompleted() {
                    }
                }));
    }

    @Override
    public void setUnFavorite(RemoveFavoriteRequest removeFavoriteRequest, final String userId) {
        addSubscriber(apiService.removeFavorite(removeFavoriteRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new SubscriberCallback<RemoveFavoriteResponse>(view) {
                    @Override
                    public void onSuccess(RemoveFavoriteResponse response) {
                        view.onUnFavoriteResponse(response, userId);
                    }

                    @Override
                    public void onCompleted() {
                    }
                }));
    }

    @Override
    public void getUserInfo(final UserInfoRequest request, int delay) {
        Observable<GetUserInfoResponse> userInfoRequestObservable;
        if (!SystemUtils.isNetworkConnected()) {
            userInfoRequestObservable = new Observable<GetUserInfoResponse>() {
                @Override
                protected void subscribeActual(Observer<? super GetUserInfoResponse> observer) {
                    try {
                        GetUserInfoResponse userInfoResponse = cacheJson.retrieveObject(String.format(CacheType.CACHE_TIMELINE_USER_INFO_ID, request.userid), GetUserInfoResponse.class);
                        observer.onNext(userInfoResponse);
                    } catch (Exception e) {
                        e.printStackTrace();
                        observer.onError(e);
                    }
                    observer.onComplete();
                }
            };
        } else {
            userInfoRequestObservable = apiService.getUserInfo(request);
        }

        Disposable disposable = userInfoRequestObservable
                .subscribeOn(Schedulers.io())
                .delay(delay, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
                .subscribeWith(new SubscriberCallback<GetUserInfoResponse>(view) {
                    @Override
                    public void onSuccess(GetUserInfoResponse response) {
                        if (response != null) {
                            view.onUserInfo(response.data);
                        }
                    }

                    @Override
                    public void onCompleted() {
                    }
                });

        addSubscriber(disposable);
    }

    @Override
    public void reportUser(ReportRequest reportRequest) {
        addSubscriber(apiService.reportUser(reportRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new SubscriberCallback<ReportResponse>(view) {
                    @Override
                    public void onSuccess(ReportResponse response) {
                        view.onReportUser();
                    }

                    @Override
                    public void onCompleted() {

                    }
                }));
    }

    @Override
    public void blockUser(AddBlockUserRequest request) {
        addSubscriber(apiService.addBlockUser(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new SubscriberCallback<AddBlockResponse>(view) {
                    @Override
                    public void onSuccess(AddBlockResponse response) {
                        view.onAddBlockUser();
                    }

                    @Override
                    public void onCompleted() {

                    }
                }));
    }
}
