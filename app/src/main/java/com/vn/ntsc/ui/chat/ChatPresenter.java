package com.vn.ntsc.ui.chat;

/**
 * Created by dev22 on 8/21/17.
 */

import com.vn.ntsc.core.BasePresenter;
import com.vn.ntsc.core.callback.SubscriberCallback;
import com.vn.ntsc.repository.model.block.addblock.AddBlockResponse;
import com.vn.ntsc.repository.model.block.addblock.AddBlockUserRequest;
import com.vn.ntsc.repository.model.chat.ChatHistoryRequest;
import com.vn.ntsc.repository.model.chat.ChatHistoryResponse;
import com.vn.ntsc.repository.model.chat.ChatMessage;
import com.vn.ntsc.repository.model.emoji.EmojiReponse;
import com.vn.ntsc.repository.model.emoji.EmojiRequest;
import com.vn.ntsc.repository.model.favorite.AddFavoriteRequest;
import com.vn.ntsc.repository.model.favorite.AddFavoriteResponse;
import com.vn.ntsc.repository.model.favorite.RemoveFavoriteRequest;
import com.vn.ntsc.repository.model.favorite.RemoveFavoriteResponse;
import com.vn.ntsc.repository.model.report.ReportRequest;
import com.vn.ntsc.repository.model.report.ReportResponse;
import com.vn.ntsc.repository.model.user.GetUserInfoResponse;
import com.vn.ntsc.repository.model.user.UserInfoRequest;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

class ChatPresenter extends BasePresenter<ChatContract.View> implements ChatContract.Presenter {

    @Inject
    ChatPresenter() {
    }

    @Override
    public void getChatHistory(final ChatHistoryRequest request) {
        apiService.getChatHistory(request)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .map(new Function<ChatHistoryResponse, List<ChatMessage>>() {
                    @Override
                    public List<ChatMessage> apply(@NonNull ChatHistoryResponse chatHistoryResponse) throws Exception {
                        return chatHistoryResponse.mData;
                    }
                })
                .subscribe(new Observer<List<ChatMessage>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull List<ChatMessage> messages) {
                        if (messages == null) return;
                        view.getChatHistorySuccess(messages);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
//                        view.getChatHistoryError();
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        view.loadComplete();
                    }
                });
    }

    @Override
    public void getUserInfo(UserInfoRequest userInfoRequest) {
        addSubscriber(apiService.getUserInfo(userInfoRequest)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
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
                }));
    }

    @Override
    public void reportUser(ReportRequest reportRequest) {
        addSubscriber(apiService.reportUser(reportRequest)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
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
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
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

    @Override
    public void getDataEmoji(EmojiRequest emojiRequest) {
        addSubscriber(apiService.getCatgoryEmoji(emojiRequest)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new SubscriberCallback<EmojiReponse>(view) {
                    @Override
                    public void onSuccess(EmojiReponse response) {
                        view.getDataEmojiSucess(response);
                    }

                    @Override
                    public void onCompleted() {

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
                        view.onFavouriteUser(userId);
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
                        view.onUnFavouriteUser(userId);
                    }

                    @Override
                    public void onCompleted() {
                    }
                }));
    }
}
