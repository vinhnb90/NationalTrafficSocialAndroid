package com.vn.ntsc.ui.conversation;

import com.vn.ntsc.core.callback.CallbackListener;
import com.vn.ntsc.core.callback.PresenterListener;
import com.vn.ntsc.repository.model.conversation.ConversationRequest;
import com.vn.ntsc.repository.model.conversation.ConversationResponse;
import com.vn.ntsc.repository.model.conversation.DelConversationRequest;
import com.vn.ntsc.repository.model.conversation.MarkReadAllRequest;

/**
 * Created by dev22 on 8/21/17.
 */
interface ConversationContract {
    interface View extends CallbackListener {

        void onConversationsSuccess(ConversationResponse response);

        void onLoadMoreConversationsSuccess(ConversationResponse response);

        void delConversationSuccess(int positionInAdapter);

        void delConversationFailure();

        void delAllConversationSuccess();

        void markAllReadSuccess();

        void markAllReadFailure();

        void loadComplete();
    }

    interface Presenter extends PresenterListener<View> {

        void requestListConversation(ConversationRequest request);

        void requestLoadMoreListConversation(ConversationRequest request);

        void requestDelConversation(DelConversationRequest request, int positionInAdapter);

        void requestDelAllConversation(DelConversationRequest request);

        void markAllReadConversations(MarkReadAllRequest request);
    }
}