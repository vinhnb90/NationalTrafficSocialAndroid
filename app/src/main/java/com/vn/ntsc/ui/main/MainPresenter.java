package com.vn.ntsc.ui.main;

import com.facebook.login.LoginManager;
import com.vn.ntsc.core.BasePresenter;
import com.vn.ntsc.core.callback.SubscriberCallback;
import com.vn.ntsc.core.model.ServerResponse;
import com.vn.ntsc.repository.model.logout.LogoutRequest;
import com.vn.ntsc.repository.model.logout.LogoutResponse;
import com.vn.ntsc.repository.model.notification.ReadAllNotificationRequest;
import com.vn.ntsc.repository.model.notification.ReadAllNotificationResponse;
import com.vn.ntsc.repository.model.user.GetUserInfoResponse;
import com.vn.ntsc.repository.model.user.UserInfoRequest;
import com.vn.ntsc.repository.remote.ApiService;
import com.vn.ntsc.utils.SystemUtils;
import com.vn.ntsc.utils.cache.CacheJson;
import com.vn.ntsc.utils.cache.CacheType;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by nankai on 8/8/2017.
 */

public class MainPresenter extends BasePresenter<MainContract.View> implements MainContract.Presenter {

    @Inject
    public MainPresenter(ApiService apiService) {
        this.apiService = apiService;
    }

    @Override
    public void onLogout(final LogoutRequest logoutRequest) {
        // always logout fb no mater success or not
        LoginManager.getInstance().logOut();

        addSubscriber(apiService.logout(logoutRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(
                        new SubscriberCallback<LogoutResponse>(view) {

                            @Override
                            public void onSuccess(LogoutResponse response) {
                                view.onLogout();
                            }

                            @Override
                            public void onCompleted() {

                            }
                        }
                ));
    }

//    @Override
//    public void getUserInfo(final UserInfoRequest userInfoRequest) {
//        Observable<GetUserInfoResponse> getUserInfoResponseObservable;
//        if (!SystemUtils.isNetworkConnected()) {
//            getUserInfoResponseObservable = Observable.create(new ObservableOnSubscribe<GetUserInfoResponse>() {
//                @Override
//                public void subscribe(ObservableEmitter<GetUserInfoResponse> observableEmitter) throws Exception {
//                    try {
//                        GetUserInfoResponse userInfoResponse =
//                                cacheJson.retrieveObject(String.format(CacheType.CACHE_TIMELINE_USER_INFO_ID, userInfoRequest.userid),
//                                        GetUserInfoResponse.class);
//                        observableEmitter.onNext(userInfoResponse);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        observableEmitter.onError(e);
//                    }
//                    observableEmitter.onComplete();
//                }
//            });
//        } else {
//            getUserInfoResponseObservable = apiService.getUserInfo(userInfoRequest);
//        }
//
//        Disposable disposable = getUserInfoResponseObservable
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .filter(new Predicate<GetUserInfoResponse>() {
//                    @Override
//                    public boolean test(GetUserInfoResponse userInfoResponse) throws Exception {
//                        if (userInfoResponse.code != ServerResponse.DefinitionCode.SERVER_SUCCESS)
//                            return false;
//                        return true;
//                    }
//                })
//                .subscribeWith(new SubscriberCallback<GetUserInfoResponse>(view) {
//                    @Override
//                    public void onSuccess(GetUserInfoResponse response) {
//                        if (response != null) {
//                            view.onUserInfo(response.data);
//                        }
//                    }
//
//                    @Override
//                    public void onCompleted() {
//
//                    }
//                });
//
//        addSubscriber(disposable);
//    }

    @Override
    public void getUserInForwardProfile(final UserInfoRequest userInfoRequest) {
        Observable<GetUserInfoResponse> getUserInfoResponseObservable;
        if (!SystemUtils.isNetworkConnected()) {
            getUserInfoResponseObservable = Observable.create(new ObservableOnSubscribe<GetUserInfoResponse>() {
                @Override
                public void subscribe(ObservableEmitter<GetUserInfoResponse> observableEmitter) throws Exception {
                    try {
                        GetUserInfoResponse userInfoResponse = CacheJson.retrieveObject(String.format(CacheType.CACHE_TIMELINE_USER_INFO_ID, userInfoRequest.userid),
                                GetUserInfoResponse.class);
                        observableEmitter.onNext(userInfoResponse);
                    } catch (Exception e) {
                        e.printStackTrace();
                        observableEmitter.onError(e);
                    }
                    observableEmitter.onComplete();
                }
            });
        } else {
            getUserInfoResponseObservable = apiService.getUserInfo(userInfoRequest);
        }

        Disposable disposable = getUserInfoResponseObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .filter(new Predicate<GetUserInfoResponse>() {
                    @Override
                    public boolean test(GetUserInfoResponse userInfoResponse) throws Exception {
                        if (userInfoResponse.code != ServerResponse.DefinitionCode.SERVER_SUCCESS)
                            return false;
                        return true;
                    }
                })
                .subscribeWith(new SubscriberCallback<GetUserInfoResponse>(view) {
                    @Override
                    public void onSuccess(GetUserInfoResponse response) {
                        if (response != null) {
                            view.onUserInForwardProfile(response.data);
                        }
                    }

                    @Override
                    public void onCompleted() {

                    }
                });
        addSubscriber(disposable);
    }

    @Override
    public void markReadAllNotification(ReadAllNotificationRequest request) {
        addSubscriber(apiService.markReadAllNotification(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribeWith(new SubscriberCallback<ReadAllNotificationResponse>(view) {
                    @Override
                    public void onSuccess(ReadAllNotificationResponse response) {
                        view.onReadAllNotification();
                    }

                    @Override
                    public void onCompleted() {

                    }
                }));
    }

}
