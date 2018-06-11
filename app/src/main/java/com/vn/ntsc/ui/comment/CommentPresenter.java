package com.vn.ntsc.ui.comment;

import android.annotation.SuppressLint;
import com.google.gson.Gson;
import com.vn.ntsc.core.BasePresenter;
import com.vn.ntsc.core.callback.SubscriberCallback;
import com.vn.ntsc.core.model.ServerResponse;
import com.vn.ntsc.repository.model.comment.AddCommentRequest;
import com.vn.ntsc.repository.model.comment.DeleteCommentRequest;
import com.vn.ntsc.repository.model.comment.DeleteCommentResponse;
import com.vn.ntsc.repository.model.comment.DeleteSubCommentRequest;
import com.vn.ntsc.repository.model.comment.DeleteSubCommentResponse;
import com.vn.ntsc.repository.model.comment.ListCommentRequest;
import com.vn.ntsc.repository.model.comment.ListCommentResponse;
import com.vn.ntsc.repository.model.comment.ListSubCommentRequest;
import com.vn.ntsc.repository.model.comment.ListSubCommentResponse;
import com.vn.ntsc.repository.model.favorite.AddFavoriteRequest;
import com.vn.ntsc.repository.model.favorite.AddFavoriteResponse;
import com.vn.ntsc.repository.model.favorite.RemoveFavoriteRequest;
import com.vn.ntsc.repository.model.favorite.RemoveFavoriteResponse;
import com.vn.ntsc.repository.model.timeline.BuzzDetailRequest;
import com.vn.ntsc.repository.model.timeline.BuzzDetailResponse;
import com.vn.ntsc.repository.model.timeline.DeleteBuzzRequest;
import com.vn.ntsc.repository.model.timeline.DeleteBuzzResponse;
import com.vn.ntsc.repository.model.timeline.JoinBuzzRequest;
import com.vn.ntsc.repository.model.timeline.LikeBuzzRequest;
import com.vn.ntsc.repository.model.timeline.LikeBuzzResponse;
import com.vn.ntsc.repository.model.timeline.datas.sub.ListCommentBean;
import com.vn.ntsc.utils.LogUtils;
import com.vn.ntsc.utils.SystemUtils;
import com.vn.ntsc.utils.cache.CacheJson;
import com.vn.ntsc.utils.cache.CacheType;
import com.vn.ntsc.widget.socket.RxSocket;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by nankai on 8/10/2017.
 */

public class CommentPresenter extends BasePresenter<CommentContract.View> implements CommentContract.Presenter {

    @Inject
    RxSocket rxSocket;

    @Inject
    public CommentPresenter(RxSocket rxSocket) {
        this.rxSocket = rxSocket;
    }

    @Override
    public void sendBuzzJoin(JoinBuzzRequest joinBuzzRequest) {
        LogUtils.i("CommentPresenter", "JoinBuzzRequest ---> " + joinBuzzRequest.toString());
        try {
            rxSocket.getWebSocketService().sendMessage(joinBuzzRequest.toString());
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void getTimelineDetail(final BuzzDetailRequest listDetailRequest, int millisecond) {
        Observable<BuzzDetailResponse> buzzDetailResponseObservable;
        // Load cache
        if (!SystemUtils.isNetworkConnected()) {
            buzzDetailResponseObservable = Observable.create(new ObservableOnSubscribe<BuzzDetailResponse>() {
                @Override
                public void subscribe(ObservableEmitter<BuzzDetailResponse> observableEmitter) throws Exception {
                    try {
                        BuzzDetailResponse buzzDetailResponse = CacheJson
                                .retrieveObject(String.format(CacheType.CACHE_TIMELINE_DETAIL_BY_ID, listDetailRequest.buzzId), BuzzDetailResponse.class);
                        if (buzzDetailResponse == null)
                            observableEmitter.onError(new ConnectException());
                        else {
                            if (buzzDetailResponse.data == null)
                                observableEmitter.onError(new ConnectException());
                            else
                                observableEmitter.onNext(buzzDetailResponse);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        observableEmitter.onError(e);
                    }

                    observableEmitter.onComplete();
                }
            });
        } else {
            buzzDetailResponseObservable = apiService.getBuzzListDetail(listDetailRequest);
        }

        Disposable disposable = buzzDetailResponseObservable
                .subscribeOn(Schedulers.io())
                .delay(millisecond, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
                .filter(new Predicate<BuzzDetailResponse>() {
                    @Override
                    public boolean test(@NonNull BuzzDetailResponse response) throws Exception {
                        if (response.code == ServerResponse.DefinitionCode.SERVER_BUZZ_NOT_FOUND) {
                            view.onBuzzNotFoundFromNotificationId();
                            return false;
                        }
                        return true;
                    }
                })
                .subscribeWith(new SubscriberCallback<BuzzDetailResponse>(view) {
                    @Override
                    public void onSuccess(BuzzDetailResponse response) {
                        view.onTimelineDetail(response);
                    }

                    @Override
                    public void onCompleted() {
                        view.onComplete();
                    }
                });

        addSubscriber(disposable);
    }

    @Override
    public void getBuzzListComment(final ListCommentRequest listCommentRequest, int millisecond) {
        Observable<ListCommentResponse> listCommentResponseObservable;
        // Load cache
        if (!SystemUtils.isNetworkConnected()) {
            listCommentResponseObservable = Observable.create(new ObservableOnSubscribe<ListCommentResponse>() {
                @SuppressLint("CheckResult")
                @Override
                public void subscribe(ObservableEmitter<ListCommentResponse> observableEmitter) throws Exception {
                    try {
                        ListCommentResponse listCommentResponse = CacheJson
                                .retrieveObject(String.format(CacheType.CACHE_TIMELINE_DETAIL_LIST_COMMENT_BY_ID, listCommentRequest.buzz_id), ListCommentResponse.class);

                        if (listCommentResponse == null)
                            observableEmitter.onError(new ConnectException());
                        else {
                            if (listCommentResponse.data == null)
                                observableEmitter.onError(new ConnectException());
                            else {
                                final List<ListCommentBean> finalListComment = new ArrayList<>();

                                Observable.fromIterable(listCommentResponse.data)
                                        .skip(listCommentRequest.skip)
                                        .take(listCommentRequest.take)
                                        .toList()
                                        .subscribe(new Consumer<List<ListCommentBean>>() {
                                            @Override
                                            public void accept(List<ListCommentBean> beanList){
                                                finalListComment.addAll(beanList);
                                            }
                                        });
                                listCommentResponse.data = finalListComment;
                                observableEmitter.onNext(listCommentResponse);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        observableEmitter.onError(e);
                    }

                    observableEmitter.onComplete();
                }
            });
        } else {
            listCommentResponseObservable = apiService.getBuzzListComment(listCommentRequest);
        }

        Disposable disposable = listCommentResponseObservable
                .subscribeOn(Schedulers.io())
                .delay(millisecond, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
                .subscribeWith(new SubscriberCallback<ListCommentResponse>(view) {
                    @Override
                    public void onSuccess(ListCommentResponse response) {
                        view.onBuzzListComment(response);
                    }

                    @Override
                    public void onCompleted() {
                        view.onComplete();
                    }
                });

        addSubscriber(disposable);
    }

    @Override
    public void getMoreBuzzListComment(final ListCommentRequest listCommentRequest, final boolean hasScrollLastPosition) {
        Observable<ListCommentResponse> listCommentResponseObservable;
        // Load cache
        if (!SystemUtils.isNetworkConnected()) {
            listCommentResponseObservable = Observable.create(new ObservableOnSubscribe<ListCommentResponse>() {
                @SuppressLint("CheckResult")
                @Override
                public void subscribe(ObservableEmitter<ListCommentResponse> observableEmitter) throws Exception {
                    try {
                        ListCommentResponse listCommentResponse = CacheJson
                                .retrieveObject(String.format(CacheType.CACHE_TIMELINE_DETAIL_LIST_COMMENT_BY_ID, listCommentRequest.buzz_id), ListCommentResponse.class);

                        if (listCommentResponse == null)
                            observableEmitter.onError(new ConnectException());
                        else {
                            if (listCommentResponse.data == null)
                                observableEmitter.onError(new ConnectException());
                            else {
                                final List<ListCommentBean> finalListComment = new ArrayList<>();
                                Observable.fromIterable(listCommentResponse.data)
                                        .skip(listCommentRequest.skip)
                                        .take(listCommentRequest.take)
                                        .toList()
                                        .subscribe(new Consumer<List<ListCommentBean>>() {
                                            @Override
                                            public void accept(List<ListCommentBean> beanList){
                                                finalListComment.addAll(beanList);
                                            }
                                        });

                                LogUtils.i("cacheJson", "--> skip: " + listCommentRequest.skip + "| take: " + listCommentRequest.take
                                        + " | old datas: " + listCommentResponse.data.size() + "| final datas: " + finalListComment.size());

                                listCommentResponse.data = finalListComment;
                                observableEmitter.onNext(listCommentResponse);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        observableEmitter.onError(e);
                    }

                    observableEmitter.onComplete();
                }
            });
        } else {
            listCommentResponseObservable = apiService.getBuzzListComment(listCommentRequest);
        }

        Disposable disposable = listCommentResponseObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new SubscriberCallback<ListCommentResponse>(view) {
                    @Override
                    public void onSuccess(ListCommentResponse response) {
                        view.onLoadMoreBuzzListComment(response, hasScrollLastPosition);
                    }

                    @Override
                    public void onCompleted() {
                        view.onComplete();
                    }
                });

        addSubscriber(disposable);
    }

    @Override
    public void onFavorite(AddFavoriteRequest addFavoriteRequest) {
        addSubscriber(apiService.onAddFavorite(addFavoriteRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new SubscriberCallback<AddFavoriteResponse>(view) {
                    @Override
                    public void onSuccess(AddFavoriteResponse response) {
                        view.onFavorite(response);
                    }

                    @Override
                    public void onCompleted() {
                        view.onComplete();
                    }
                }));
    }

    @Override
    public void onUnFavorite(RemoveFavoriteRequest removeFavoriteRequest) {
        addSubscriber(apiService.removeFavorite(removeFavoriteRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new SubscriberCallback<RemoveFavoriteResponse>(view) {
                    @Override
                    public void onSuccess(RemoveFavoriteResponse response) {
                        view.onUnFavorite(response);
                    }

                    @Override
                    public void onCompleted() {
                        view.onComplete();
                    }
                }));
    }

    @Override
    public void onLike(final LikeBuzzRequest likeBuzzRequest) {
        addSubscriber(apiService.onLike(likeBuzzRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .filter(new Predicate<LikeBuzzResponse>() {
                    @Override
                    public boolean test(LikeBuzzResponse likeBuzzResponse) throws Exception {
                        if (likeBuzzResponse.code != ServerResponse.DefinitionCode.SERVER_SUCCESS) {
                            view.onLikeFail();
                            view.onServerResponseInvalid(likeBuzzResponse.code, likeBuzzResponse);
                            return false;
                        }
                        return true;
                    }
                })
                .subscribeWith(new SubscriberCallback<LikeBuzzResponse>(view) {
                    @Override
                    public void onSuccess(LikeBuzzResponse response) {
                        view.onLike(response);
                    }

                    @Override
                    public void onCompleted() {
                        view.onComplete();
                    }
                }));
    }

    @Override
    public void onDelete(DeleteBuzzRequest deleteBuzzRequest) {
        addSubscriber(apiService.onDeleteBuzz(deleteBuzzRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new SubscriberCallback<DeleteBuzzResponse>(view) {
                    @Override
                    public void onSuccess(DeleteBuzzResponse response) {
                        view.onDelete(response);
                    }

                    @Override
                    public void onCompleted() {
                        view.onComplete();
                    }
                }));
    }

    @Override
    public void sendComment(AddCommentRequest addCommentRequest) {
        Gson gson = new Gson();
        String addComment = gson.toJson(addCommentRequest);
        LogUtils.i("addComment", "AddCommentRequest ---> " + addComment);
        try {
            rxSocket.getWebSocketService().sendMessage(addComment);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteComment(DeleteCommentRequest deleteCommentRequest, final int position) {
        addSubscriber(apiService.deleteComment(deleteCommentRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new SubscriberCallback<DeleteCommentResponse>(view) {
                    @Override
                    public void onSuccess(DeleteCommentResponse response) {
                        view.onDeleteComment(response, position);
                    }

                    @Override
                    public void onCompleted() {
                        view.onComplete();
                    }
                }));
    }

    @Override
    public void deleteSubComment(final ListCommentBean bean, DeleteSubCommentRequest deleteSubCommentRequest, final int position) {
        addSubscriber(apiService.deleteSubComment(deleteSubCommentRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new SubscriberCallback<DeleteSubCommentResponse>(view) {
                    @Override
                    public void onSuccess(DeleteSubCommentResponse response) {
                        view.onDeleteSubComment(response, bean, position);
                    }

                    @Override
                    public void onCompleted() {
                        view.onComplete();
                    }
                }));
    }
}
