package com.vn.ntsc.ui.comment.subcomment;

import com.google.gson.Gson;
import com.vn.ntsc.core.BasePresenter;
import com.vn.ntsc.core.callback.SubscriberCallback;
import com.vn.ntsc.repository.model.comment.AddSubCommentRequest;
import com.vn.ntsc.repository.model.comment.CommentDetailRequest;
import com.vn.ntsc.repository.model.comment.CommentDetailResponse;
import com.vn.ntsc.repository.model.comment.DeleteSubCommentRequest;
import com.vn.ntsc.repository.model.comment.DeleteSubCommentResponse;
import com.vn.ntsc.repository.model.comment.ListSubCommentRequest;
import com.vn.ntsc.repository.model.comment.ListSubCommentResponse;
import com.vn.ntsc.repository.model.timeline.JoinBuzzRequest;
import com.vn.ntsc.repository.model.timeline.datas.sub.BuzzSubCommentBean;
import com.vn.ntsc.utils.LogUtils;
import com.vn.ntsc.utils.SystemUtils;
import com.vn.ntsc.utils.cache.CacheJson;
import com.vn.ntsc.utils.cache.CacheType;
import com.vn.ntsc.widget.socket.RxSocket;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by nankai on 8/10/2017.
 */

public class SubCommentPresenter extends BasePresenter<SubCommentContract.View> implements SubCommentContract.Presenter {

    @Inject
    RxSocket rxSocket;

    @Inject
    public SubCommentPresenter(RxSocket rxSocket) {
        this.rxSocket = rxSocket;
    }

    @Override
    public void sendBuzzJoin(JoinBuzzRequest joinBuzzRequest) {
        LogUtils.i("SubCommentPresenter", "JoinBuzzRequest ---> " + joinBuzzRequest.toString());
        try {
            rxSocket.getWebSocketService().sendMessage(joinBuzzRequest.toString());
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void getCommentDetail(CommentDetailRequest request) {
        addSubscriber(apiService.getBuzzListComment(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new SubscriberCallback<CommentDetailResponse>(view) {

                    @Override
                    public void onSuccess(CommentDetailResponse response) {
                        view.onGetCommentDetail(response);
                    }

                    @Override
                    public void onCompleted() {
                        view.onCompleted();
                    }
                }));
    }

    @Override
    public void getSubListComment(final ListSubCommentRequest listSubCommentRequest) {
        Observable<ListSubCommentResponse> subCommentResponseObservable;
        // Load cache
        if (!SystemUtils.isNetworkConnected()) {//offline
            subCommentResponseObservable = Observable.create(new ObservableOnSubscribe<ListSubCommentResponse>() {
                @Override
                public void subscribe(ObservableEmitter<ListSubCommentResponse> observableEmitter) throws Exception {
                    try {
                        ListSubCommentResponse listSubCommentResponse = CacheJson
                                .retrieveObject(String.format(CacheType.CACHE_TIMELINE_DETAIL_LIST_SUB_COMMENT_BY_ID, listSubCommentRequest.buzzId, listSubCommentRequest.commentId)
                                        , ListSubCommentResponse.class);

                        if (listSubCommentResponse == null)
                            listSubCommentResponse = new ListSubCommentResponse();

                        if (listSubCommentResponse.data == null)
                            listSubCommentResponse.data = new ArrayList<>();

                        final List<BuzzSubCommentBean> finalListSubComment = new ArrayList<>();
                        Observable.fromIterable(listSubCommentResponse.data)
                                .skip(listSubCommentRequest.skip)
                                .take(listSubCommentRequest.take)
                                .toList()
                                .subscribe(new Consumer<List<BuzzSubCommentBean>>() {
                                    @Override
                                    public void accept(List<BuzzSubCommentBean> beanList) throws Exception {
                                        finalListSubComment.addAll(beanList);
                                    }
                                });

                        LogUtils.i("cacheJson", "--> skip: " + listSubCommentRequest.skip + "| take: " + listSubCommentRequest.take
                                + " | old datas: " + listSubCommentResponse.data.size() + "| final datas: " + finalListSubComment.size());

                        listSubCommentResponse.data = finalListSubComment;
                        observableEmitter.onNext(listSubCommentResponse);
                    } catch (Exception e) {
                        e.printStackTrace();
                        observableEmitter.onError(e);
                    }
                    observableEmitter.onComplete();
                }
            });
        } else {
            subCommentResponseObservable = apiService.getSubListComment(listSubCommentRequest);
        }

        Disposable disposable = subCommentResponseObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new SubscriberCallback<ListSubCommentResponse>(view) {
                    @Override
                    public void onSuccess(ListSubCommentResponse response) {
                        view.onSubListComment(response);
                    }

                    @Override
                    public void onCompleted() {
                        view.onCompleted();
                    }
                });

        addSubscriber(disposable);
    }

    @Override
    public void sendSubComment(AddSubCommentRequest addSubCommentRequest) {
        Gson gson = new Gson();
        String addComment = gson.toJson(addSubCommentRequest);
        LogUtils.i("addComment", "AddSubCommentRequest ---> " + addComment);
        try {
            rxSocket.getWebSocketService().sendMessage(addComment);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteComment(DeleteSubCommentRequest deleteSubCommentRequest, final int position) {
        addSubscriber(apiService.deleteSubComment(deleteSubCommentRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new SubscriberCallback<DeleteSubCommentResponse>(view) {
                    @Override
                    public void onSuccess(DeleteSubCommentResponse response) {
                        view.onDeleteComment(response, position);
                    }

                    @Override
                    public void onCompleted() {
                        view.onCompleted();
                    }
                }));
    }
}
