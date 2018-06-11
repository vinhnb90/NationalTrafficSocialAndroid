package com.vn.ntsc.ui.conversation;

import com.vn.ntsc.core.BasePresenter;
import com.vn.ntsc.core.callback.SubscriberCallback;
import com.vn.ntsc.core.model.ServerResponse;
import com.vn.ntsc.repository.model.conversation.ConversationRequest;
import com.vn.ntsc.repository.model.conversation.ConversationResponse;
import com.vn.ntsc.repository.model.conversation.DelConversationRequest;
import com.vn.ntsc.repository.model.conversation.DelConversationResponse;
import com.vn.ntsc.repository.model.conversation.MarkReadAllRequest;
import com.vn.ntsc.repository.model.conversation.MarkReadAllResponse;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by dev22 on 8/21/17.
 */
class ConversationPresenter extends BasePresenter<ConversationContract.View> implements ConversationContract.Presenter {

    @Inject
    ConversationPresenter() {
    }

    @Override
    public void requestListConversation(ConversationRequest request) {

        addSubscriber(apiService.getConversations(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new SubscriberCallback<ConversationResponse>(view) {
                    @Override
                    public void onSuccess(ConversationResponse response) {
                        view.onConversationsSuccess(response);
                    }

                    @Override
                    public void onCompleted() {
                        view.loadComplete();
                    }
                }));
    }

    @Override
    public void requestLoadMoreListConversation(ConversationRequest request) {
        addSubscriber(apiService.getConversations(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new SubscriberCallback<ConversationResponse>(view) {
                    @Override
                    public void onSuccess(ConversationResponse response) {
                        view.onLoadMoreConversationsSuccess(response);
                    }

                    @Override
                    public void onCompleted() {
                        view.loadComplete();
                    }
                }));
    }

    @Override
    public void requestDelConversation(final DelConversationRequest request, final int positionInAdapter) {
        addSubscriber(apiService.delConversation(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .filter(new Predicate<DelConversationResponse>() {
                    @Override
                    public boolean test(@NonNull DelConversationResponse response) throws Exception {
                        //Show dialog notice token expired  or invalid
                        if (response.code == ServerResponse.DefinitionCode.SERVER_EXPIRED_TOKEN || response.code == ServerResponse.DefinitionCode.SERVER_INVALID_TOKEN) {
                            view.onServerResponseInvalid(response.code,response);
                            return false;
                        }
                        //Handle other case of error in itself (intrinsic ConversationFragment)
                        if (response.code != ServerResponse.DefinitionCode.SERVER_SUCCESS) {
                            view.delConversationFailure();
                            return false;
                        }
                        return true;
                    }
                })
                .subscribeWith(new SubscriberCallback<DelConversationResponse>(view) {
                    @Override
                    public void onSuccess(DelConversationResponse response) {
                        view.delConversationSuccess(positionInAdapter);
                    }

                    @Override
                    public void onCompleted() {
                        view.loadComplete();
                    }
                }));
    }

    @Override
    public void requestDelAllConversation(DelConversationRequest request) {
        addSubscriber(apiService.delConversation(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .filter(new Predicate<DelConversationResponse>() {
                    @Override
                    public boolean test(@NonNull DelConversationResponse response) throws Exception {
                        //Show dialog notice token expired  or invalid
                        if (response.code == ServerResponse.DefinitionCode.SERVER_EXPIRED_TOKEN || response.code == ServerResponse.DefinitionCode.SERVER_INVALID_TOKEN) {
                            view.onServerResponseInvalid(response.code,response);
                            return false;
                        }
                        //Handle other case of error in itself (intrinsic ConversationFragment)
                        if (response.code != ServerResponse.DefinitionCode.SERVER_SUCCESS) {
                            view.delConversationFailure();
                            return false;
                        }
                        return true;
                    }
                })
                .subscribeWith(new SubscriberCallback<DelConversationResponse>(view) {
                    @Override
                    public void onSuccess(DelConversationResponse response) {
                        view.delAllConversationSuccess();
                    }

                    @Override
                    public void onCompleted() {
                        view.loadComplete();
                    }
                }));
    }

    @Override
    public void markAllReadConversations(final MarkReadAllRequest request) {
        addSubscriber(apiService.markAllReadConversations(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .filter(new Predicate<MarkReadAllResponse>() {
                    @Override
                    public boolean test(@NonNull MarkReadAllResponse response) throws Exception {
                        //Show dialog notice token expired  or invalid
                        if (response.code == ServerResponse.DefinitionCode.SERVER_EXPIRED_TOKEN || response.code == ServerResponse.DefinitionCode.SERVER_INVALID_TOKEN) {
                            view.onServerResponseInvalid(response.code,response);
                            return false;
                        }
                        //Handle other case of error in itself (intrinsic ConversationFragment)
                        if (response.code != ServerResponse.DefinitionCode.SERVER_SUCCESS) {
                            view.markAllReadFailure();
                            return false;
                        }
                        return true;
                    }
                })
                .subscribeWith(new SubscriberCallback<MarkReadAllResponse>(view) {
                    @Override
                    public void onSuccess(MarkReadAllResponse response) {
                        view.markAllReadSuccess();
                    }

                    @Override
                    public void onCompleted() {
                        view.loadComplete();
                    }
                }));
    }
}