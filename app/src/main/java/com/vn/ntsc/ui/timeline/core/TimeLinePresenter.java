package com.vn.ntsc.ui.timeline.core;

import android.view.View;

import com.vn.ntsc.core.BasePresenter;
import com.vn.ntsc.core.callback.SubscriberCallback;
import com.vn.ntsc.core.model.ServerResponse;
import com.vn.ntsc.repository.TypeView;
import com.vn.ntsc.repository.model.favorite.AddFavoriteRequest;
import com.vn.ntsc.repository.model.favorite.AddFavoriteResponse;
import com.vn.ntsc.repository.model.favorite.RemoveFavoriteRequest;
import com.vn.ntsc.repository.model.favorite.RemoveFavoriteResponse;
import com.vn.ntsc.repository.model.profile.EvaluateUserProfileRequest;
import com.vn.ntsc.repository.model.profile.EvaluateUserProfileResponse;
import com.vn.ntsc.repository.model.share.AddNumberShareRequest;
import com.vn.ntsc.repository.model.share.AddNumberShareResponse;
import com.vn.ntsc.repository.model.timeline.BuzzDetailRequest;
import com.vn.ntsc.repository.model.timeline.BuzzDetailResponse;
import com.vn.ntsc.repository.model.timeline.BuzzListRequest;
import com.vn.ntsc.repository.model.timeline.BuzzListResponse;
import com.vn.ntsc.repository.model.timeline.DeleteBuzzRequest;
import com.vn.ntsc.repository.model.timeline.DeleteBuzzResponse;
import com.vn.ntsc.repository.model.timeline.LikeBuzzRequest;
import com.vn.ntsc.repository.model.timeline.LikeBuzzResponse;
import com.vn.ntsc.repository.model.timeline.datas.BuzzBean;
import com.vn.ntsc.repository.model.timeline.datas.sub.ListBuzzChild;
import com.vn.ntsc.repository.model.user.UserInfoResponse;
import com.vn.ntsc.repository.publicfile.PublicFileRequest;
import com.vn.ntsc.repository.publicfile.PublicFileResponse;
import com.vn.ntsc.repository.remote.ApiService;
import com.vn.ntsc.utils.LogUtils;
import com.vn.ntsc.utils.SystemUtils;
import com.vn.ntsc.utils.cache.CacheJson;
import com.vn.ntsc.utils.cache.CacheType;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by nankai on 8/8/2017.
 */

public class TimeLinePresenter extends BasePresenter<TimeLineContract.View> implements TimeLineContract.Presenter {

    @Inject
    public TimeLinePresenter(ApiService apiService) {
        this.apiService = apiService;
    }

    @Override
    public void getBuzzDetail(final BuzzDetailRequest listDetailRequest, final String templateId) {
        addSubscriber(apiService.getBuzzListDetail(listDetailRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .filter(new Predicate<BuzzDetailResponse>() {
                    @Override
                    public boolean test(@NonNull BuzzDetailResponse response) throws Exception {
                        if (response.code == ServerResponse.DefinitionCode.SERVER_BUZZ_NOT_FOUND) {
                            view.onBuzzDetailError(listDetailRequest, templateId);
                            return false;
                        }
                        return true;
                    }
                })
                .subscribeWith(new SubscriberCallback<BuzzDetailResponse>(view) {
                    @Override
                    public void onSuccess(BuzzDetailResponse response) {
                        view.onBuzzDetail(response, templateId);
                    }

                    @Override
                    public void onCompleted() {
                        view.onComplete();
                    }
                }));
    }

    @Override
    public void getBuzzList(final BuzzListRequest buzzListRequest, final UserInfoResponse profileBean, @TypeView.TypeViewTimeline final int typeView, int delay) {

        Observable<BuzzListResponse> buzzListResponseObservable;
        if (!SystemUtils.isNetworkConnected()) {// Load cache
            buzzListResponseObservable = new Observable<BuzzListResponse>() {
                @Override
                protected void subscribeActual(Observer<? super BuzzListResponse> observer) {
                    try {
                        BuzzListResponse buzzListResponse = new BuzzListResponse();

                        if (typeView == TypeView.TypeViewTimeline.TIMELINE_USER) {
                            buzzListResponse = CacheJson.retrieveObject(String.format(CacheType.CACHE_TIMELINE_USER_ID, profileBean.userId), BuzzListResponse.class);
                        } else if (typeView == TypeView.TypeViewTimeline.TIMELINE_FAVORITES) {
                            buzzListResponse = CacheJson.retrieveObject(CacheType.CACHE_TIMELINE_FAVORITE, BuzzListResponse.class);
                        } else if (typeView == TypeView.TypeViewTimeline.TIMELINE_ALL) {
                            buzzListResponse = CacheJson.retrieveObject(CacheType.CACHE_TIMELINE_ALL, BuzzListResponse.class);
                        }

                        if (buzzListResponse.data == null)
                            observer.onError(new ConnectException());
                        else {
                            final List<BuzzBean> finalBuzzBeanList = new ArrayList<>();
                            Observable.fromIterable(buzzListResponse.data)
                                    .skip(buzzListRequest.skip)
                                    .take(buzzListRequest.take)
                                    .subscribeOn(Schedulers.newThread())
                                    .observeOn(Schedulers.io())
                                    .toList()
                                    .subscribe(new Consumer<List<BuzzBean>>() {
                                        @Override
                                        public void accept(List<BuzzBean> beanList) throws Exception {
                                            finalBuzzBeanList.addAll(beanList);
                                        }
                                    });
                            buzzListResponse.data = finalBuzzBeanList;
                            observer.onNext(buzzListResponse);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        observer.onError(e);
                    }
                    observer.onComplete();
                }
            };
        } else {
            buzzListResponseObservable = apiService.getBuzzList(buzzListRequest);
        }

        Disposable disposable = buzzListResponseObservable
                .subscribeOn(Schedulers.io())
                .delay(delay, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
                .subscribeWith(new SubscriberCallback<BuzzListResponse>(view) {
                    @Override
                    public void onSuccess(BuzzListResponse response) {
                        view.onBuzzListResponse(response);
                    }

                    @Override
                    public void onCompleted() {
                        view.onComplete();
                    }
                });
        addSubscriber(disposable);
    }

    @Override
    public void getMoreBuzzList(final BuzzListRequest buzzListRequest, final UserInfoResponse profileBean, @TypeView.TypeViewTimeline final int typeView) {
        Observable<BuzzListResponse> buzzListResponseObservable;
        // Load cache
        if (!SystemUtils.isNetworkConnected())
            buzzListResponseObservable = new Observable<BuzzListResponse>() {
                @Override
                protected void subscribeActual(Observer<? super BuzzListResponse> observer) {
                    try {
                        BuzzListResponse buzzListResponse = new BuzzListResponse();

                        if (typeView == TypeView.TypeViewTimeline.TIMELINE_USER) {
                            buzzListResponse = CacheJson.retrieveObject(String.format(CacheType.CACHE_TIMELINE_USER_ID, profileBean.userId), BuzzListResponse.class);
                        } else if (typeView == TypeView.TypeViewTimeline.TIMELINE_FAVORITES) {
                            buzzListResponse = CacheJson.retrieveObject(CacheType.CACHE_TIMELINE_FAVORITE, BuzzListResponse.class);
                        } else if (typeView == TypeView.TypeViewTimeline.TIMELINE_ALL) {
                            buzzListResponse = CacheJson.retrieveObject(CacheType.CACHE_TIMELINE_ALL, BuzzListResponse.class);
                        }

                        if (buzzListResponse.data == null)
                            buzzListResponse.data = new ArrayList<>();

                        final List<BuzzBean> finalBuzzBeanList = new ArrayList<>();
                        Observable.fromIterable(buzzListResponse.data)
                                .skip(buzzListRequest.skip)
                                .take(buzzListRequest.take)
                                .toList()
                                .subscribe(new Consumer<List<BuzzBean>>() {
                                    @Override
                                    public void accept(List<BuzzBean> beanList) throws Exception {
                                        finalBuzzBeanList.addAll(beanList);
                                    }
                                });

                        buzzListResponse.data = finalBuzzBeanList;
                        observer.onNext(buzzListResponse);
                    } catch (Exception e) {
                        e.printStackTrace();
                        observer.onError(e);
                    }
                    observer.onComplete();
                }
            };
        else {
            buzzListResponseObservable = apiService.getBuzzList(buzzListRequest);
        }

        Disposable disposable = buzzListResponseObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .filter(new Predicate<BuzzListResponse>() {
                    @Override
                    public boolean test(BuzzListResponse response) throws Exception {
                        if (response.code != ServerResponse.DefinitionCode.SERVER_SUCCESS) {
                            view.onLoadMoreEmpty();
                            return false;
                        }
                        return true;
                    }
                })
                .subscribeWith(new SubscriberCallback<BuzzListResponse>(view) {
                    @Override
                    public void onSuccess(BuzzListResponse response) {
                        if (null == response || response.data.isEmpty())
                            view.onLoadMoreEmpty();
                        else
                            view.onLoadMoreBuzzListResponse(response);
                    }

                    @Override
                    public void onError(Throwable e) {
                        view.onLoadMoreEmpty();
                        super.onError(e);
                    }

                    @Override
                    public void onCompleted() {
                        view.onComplete();
                    }
                });

        addSubscriber(disposable);
    }

    @Override
    public void setLike(LikeBuzzRequest likeBuzzRequest, final String buzzId, final View view) {
        addSubscriber(apiService.onLike(likeBuzzRequest)
                .filter(new Predicate<LikeBuzzResponse>() {
                    @Override
                    public boolean test(LikeBuzzResponse likeBuzzResponse) throws Exception {
                        if (likeBuzzResponse.code == ServerResponse.DefinitionCode.SERVER_BUZZ_NOT_FOUND) {
                            TimeLinePresenter.this.view.handleBuzzNotFound(buzzId);
                            return false;
                        }
                        return true;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new SubscriberCallback<LikeBuzzResponse>(TimeLinePresenter.this.view) {
                    @Override
                    public void onSuccess(LikeBuzzResponse response) {
                        TimeLinePresenter.this.view.onLikeResponse(response, buzzId, view);
                    }

                    @Override
                    public void onCompleted() {
                        TimeLinePresenter.this.view.onComplete();
                    }
                }));
    }

    @Override
    public void requestDeleteBuzz(DeleteBuzzRequest deleteBuzzRequest, final String buzzId) {
        addSubscriber(apiService.onDeleteBuzz(deleteBuzzRequest)
                .filter(new Predicate<DeleteBuzzResponse>() {
                    @Override
                    public boolean test(DeleteBuzzResponse deleteBuzzResponse) throws Exception {
                        if (deleteBuzzResponse.code == ServerResponse.DefinitionCode.SERVER_BUZZ_NOT_FOUND) {
                            view.handleBuzzNotFound(buzzId);
                            return false;
                        }
                        return true;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new SubscriberCallback<DeleteBuzzResponse>(view) {
                    @Override
                    public void onSuccess(DeleteBuzzResponse response) {
                        view.onDeleteBuzzResponse(response, buzzId);
                    }

                    @Override
                    public void onCompleted() {
                        view.onComplete();
                    }
                }));
    }

    @Override
    public void requestAddNumberShare(AddNumberShareRequest addNumberShareRequest,
                                      final String buzzID) {
        addSubscriber(apiService.onAddNumberShareBuzz(addNumberShareRequest)
                .filter(new Predicate<AddNumberShareResponse>() {
                    @Override
                    public boolean test(AddNumberShareResponse addNumberShareResponse) throws Exception {
                        if (addNumberShareResponse.code == ServerResponse.DefinitionCode.SERVER_BUZZ_NOT_FOUND) {
                            view.handleBuzzNotFound(buzzID);
                            return false;
                        }
                        return true;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new SubscriberCallback<AddNumberShareResponse>(view) {
                    @Override
                    public void onSuccess(AddNumberShareResponse response) {
                        view.onAddNumberShare(response, buzzID);
                    }

                    @Override
                    public void onCompleted() {
                        view.onComplete();
                    }
                }));
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
                        view.onComplete();
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
                        view.onComplete();
                    }
                }));
    }

    @Override
    public void getListPublicFile(final PublicFileRequest listPublicFileRequest,
                                  final UserInfoResponse profileBean, @TypeView.TypeViewTimeline final int typeView, int delay) {
        Observable<PublicFileResponse> buzzListResponseObservable;
        // Load cache
        if (!SystemUtils.isNetworkConnected()) {
            buzzListResponseObservable = new Observable<PublicFileResponse>() {

                @Override
                protected void subscribeActual(Observer<? super PublicFileResponse> observer) {
                    try {
                        PublicFileResponse listPublicImageResponse = CacheJson
                                .retrieveObject(String.format(CacheType.CACHE_TIMELINE_PUBLIC_IMAGES_ID, profileBean.userId)
                                        , PublicFileResponse.class);

                        if (listPublicImageResponse.mData == null)
                            listPublicImageResponse.mData = new ArrayList<>();

                        final List<ListBuzzChild> finalListPublicImages = new ArrayList<>();
                        Observable.fromIterable(listPublicImageResponse.mData)
                                .skip(listPublicFileRequest.skip)
                                .take(listPublicFileRequest.take)
                                .subscribeOn(Schedulers.newThread())
                                .observeOn(Schedulers.io())
                                .toList()
                                .subscribe(new Consumer<List<ListBuzzChild>>() {
                                    @Override
                                    public void accept(List<ListBuzzChild> beanList) throws Exception {
                                        finalListPublicImages.addAll(beanList);
                                    }
                                });
                        listPublicImageResponse.mData = finalListPublicImages;
                        observer.onNext(listPublicImageResponse);
                    } catch (Exception e) {
                        e.printStackTrace();
                        observer.onError(e);
                    }
                    observer.onComplete();
                }
            };

        } else {
            buzzListResponseObservable = apiService.getPublicFiles(listPublicFileRequest);
        }

        Disposable disposable = buzzListResponseObservable
                .subscribeOn(Schedulers.io())
                .delay(delay, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
                .subscribeWith(new SubscriberCallback<PublicFileResponse>(view) {
                    @Override
                    public void onSuccess(PublicFileResponse response) {
                        view.onGetListPublicFile(response);
                    }

                    @Override
                    public void onCompleted() {
                        view.onComplete();
                    }
                });

        addSubscriber(disposable);
    }

    @Override
    public void getRoomLiveStream(final BuzzListRequest buzzListRequest,
                                  @TypeView.TypeViewTimeline final int typeView, int delay) {

        Observable<BuzzListResponse> buzzListResponseObservable;
        // Load cache
        if (!SystemUtils.isNetworkConnected()) {
            buzzListResponseObservable = new Observable<BuzzListResponse>() {

                @Override
                protected void subscribeActual(Observer<? super BuzzListResponse> observer) {
                    try {
                        BuzzListResponse buzzListResponse;
                        if (typeView == TypeView.TypeViewTimeline.TIMELINE_FAVORITES)
                            buzzListResponse = CacheJson.retrieveObject(CacheType.CACHE_TIMELINE_LIVE_STREAM_FAVORITE, BuzzListResponse.class);
                        else
                            buzzListResponse = CacheJson.retrieveObject(CacheType.CACHE_TIMELINE_LIVE_STREAM_ALL, BuzzListResponse.class);

                        if (buzzListResponse == null)
                            buzzListResponse = new BuzzListResponse();

                        if (buzzListResponse.data == null)
                            buzzListResponse.data = new ArrayList<>();

                        final List<BuzzBean> finalBuzzBean = new ArrayList<>();
                        Observable.fromIterable(buzzListResponse.data)
                                .skip(buzzListRequest.skip)
                                .take(buzzListRequest.take)
                                .subscribeOn(Schedulers.newThread())
                                .observeOn(Schedulers.io())
                                .toList()
                                .subscribe(new Consumer<List<BuzzBean>>() {
                                    @Override
                                    public void accept(List<BuzzBean> beanList) throws Exception {
                                        finalBuzzBean.addAll(beanList);
                                    }
                                });
                        buzzListResponse.data = finalBuzzBean;
                        observer.onNext(buzzListResponse);
                    } catch (Exception e) {
                        e.printStackTrace();
                        observer.onError(e);
                    }
                    observer.onComplete();
                }
            };

        } else {
            buzzListResponseObservable = apiService.getBuzzList(buzzListRequest);
        }

        Disposable disposable = buzzListResponseObservable
                .subscribeOn(Schedulers.io())
                .delay(delay, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
                .filter(new Predicate<BuzzListResponse>() {
                    @Override
                    public boolean test(BuzzListResponse buzzListResponse) throws Exception {
                        if (buzzListResponse.code != ServerResponse.DefinitionCode.SERVER_SUCCESS)
                            view.onTimelineLiveStreamEmptyView();
                        return true;
                    }
                })
                .subscribeWith(new SubscriberCallback<BuzzListResponse>(view) {
                    @Override
                    public void onSuccess(BuzzListResponse response) {
                        if (null == response || response.data == null || response.data.isEmpty())
                            view.onTimelineLiveStreamEmptyView();
                        else
                            view.onGetRoomLiveStream(response);
                    }

                    @Override
                    public void onCompleted() {
                        view.onComplete();
                    }
                });

        addSubscriber(disposable);
    }

    @Override
    public void getEvaluateUser(EvaluateUserProfileRequest evaluateUserProfileRequest) {
        addSubscriber(apiService.getEvaluateUserProfile(evaluateUserProfileRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new SubscriberCallback<EvaluateUserProfileResponse>(view) {
                    @Override
                    public void onSuccess(EvaluateUserProfileResponse response) {
                        view.onEvaluateUser(response);
                        LogUtils.d("getEvaluateUser", "11 " + response.code);
                    }

                    @Override
                    public void onCompleted() {

                    }
                }));
    }
}
