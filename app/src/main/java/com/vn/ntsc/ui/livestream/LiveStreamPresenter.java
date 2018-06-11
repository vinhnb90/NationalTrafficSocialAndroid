package com.vn.ntsc.ui.livestream;

import com.google.gson.Gson;
import com.vn.ntsc.core.BasePresenter;
import com.vn.ntsc.core.callback.SubscriberCallback;
import com.vn.ntsc.core.model.ServerResponse;
import com.vn.ntsc.repository.model.comment.AddCommentRequest;
import com.vn.ntsc.repository.model.comment.ListCommentRequest;
import com.vn.ntsc.repository.model.comment.ListCommentResponse;
import com.vn.ntsc.repository.model.poststatus.PostStatusResponse;
import com.vn.ntsc.repository.model.timeline.BuzzDetailRequest;
import com.vn.ntsc.repository.model.timeline.BuzzDetailResponse;
import com.vn.ntsc.repository.model.timeline.JoinBuzzRequest;
import com.vn.ntsc.repository.model.token.CheckTokenRequest;
import com.vn.ntsc.repository.model.token.CheckTokenResponse;
import com.vn.ntsc.repository.remote.ApiMediaService;
import com.vn.ntsc.utils.LogUtils;
import com.vn.ntsc.widget.socket.RxSocket;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * Created by Robert on 2017 Nov 25
 * This specifies the Live Stream the presenter implementation.
 */
public class LiveStreamPresenter extends BasePresenter<LiveStreamContract.View> implements LiveStreamContract.Presenter {

    @Inject
    RxSocket rxSocket;
    @Inject
    ApiMediaService apiMediaService;

    @Inject
    public LiveStreamPresenter(RxSocket rxSocket) {
        this.rxSocket = rxSocket;
    }

    @Override
    public void sendBuzzJoin(JoinBuzzRequest joinBuzzRequest) {
        LogUtils.i("LiveStreamPresenter", "JoinBuzzRequest ---> " + joinBuzzRequest.toString());
        try {
            rxSocket.getWebSocketService().sendMessage(joinBuzzRequest.toString());
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void getTimelineDetail(BuzzDetailRequest listDetailRequest) {
        addSubscriber(apiService.getBuzzListDetail(listDetailRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
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
                }));
    }

    @Override
    public void getBuzzListComment(ListCommentRequest response) {
        addSubscriber(apiService.getBuzzListComment(response)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new SubscriberCallback<ListCommentResponse>(view) {
                    @Override
                    public void onSuccess(ListCommentResponse response) {
                        view.onBuzzListComment(response);
                    }

                    @Override
                    public void onCompleted() {
                        view.onComplete();
                    }
                }));
    }

    @Override
    public void sendComment(AddCommentRequest addCommentRequest) {
        if (addCommentRequest == null)
            return;
        Gson gson = new Gson();
        String addComment = gson.toJson(addCommentRequest);
        LogUtils.i("addComment", "AddCommentRequest ---> " + addComment.toString());
        try {
            rxSocket.getWebSocketService().sendMessage(addComment);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void shareMedia(String token, String buzzValue, List<String> listUserId, String buzzId) {
        addSubscriber(apiMediaService.shareMediaPost(RequestBody.create(MediaType.parse("text/plain"), "add_status"),
                RequestBody.create(MediaType.parse("text/plain"), token),
                RequestBody.create(MediaType.parse("text/plain"), buzzValue),
                RequestBody.create(MediaType.parse("text/plain"), "0"),
                RequestBody.create(MediaType.parse("application/json"), new Gson().toJson(listUserId)),
                RequestBody.create(MediaType.parse("text/plain"), buzzId)
        )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                // show error
                .filter(new Predicate<PostStatusResponse>() {
                    @Override
                    public boolean test(PostStatusResponse response) throws Exception {
                        if (response.code != ServerResponse.DefinitionCode.SERVER_SUCCESS) {
                            view.shareMediaFailure();
                            return false;
                        }
                        return true;
                    }
                })
                .subscribeWith(new SubscriberCallback<PostStatusResponse>(view) {
                    @Override
                    public void onSuccess(PostStatusResponse response) {
                        view.shareMediaSuccess(response);
                    }

                    @Override
                    public void onCompleted() {
                        view.onComplete();
                    }
                }));
    }

    @Override
    public void checkToken(CheckTokenRequest checkTokenRequest) {
        addSubscriber(apiService.checkToken(checkTokenRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new SubscriberCallback<CheckTokenResponse>(view) {
                    @Override
                    public void onSuccess(CheckTokenResponse response) {
                        view.onRefreshToken(response);
                    }

                    @Override
                    public void onCompleted() {
                        view.onComplete();
                    }
                }));
    }
}
