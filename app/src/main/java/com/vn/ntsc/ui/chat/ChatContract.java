package com.vn.ntsc.ui.chat;

import com.vn.ntsc.core.callback.CallbackListener;
import com.vn.ntsc.core.callback.PresenterListener;
import com.vn.ntsc.repository.model.block.addblock.AddBlockUserRequest;
import com.vn.ntsc.repository.model.chat.ChatHistoryRequest;
import com.vn.ntsc.repository.model.chat.ChatMessage;
import com.vn.ntsc.repository.model.emoji.EmojiReponse;
import com.vn.ntsc.repository.model.emoji.EmojiRequest;
import com.vn.ntsc.repository.model.favorite.AddFavoriteRequest;
import com.vn.ntsc.repository.model.favorite.RemoveFavoriteRequest;
import com.vn.ntsc.repository.model.report.ReportRequest;
import com.vn.ntsc.repository.model.user.UserInfoRequest;
import com.vn.ntsc.repository.model.user.UserInfoResponse;

import java.util.List;

interface ChatContract {
    interface View extends CallbackListener {
        void getChatHistorySuccess(List<ChatMessage> messages);

        void getChatHistoryError();

        void onUserInfo(UserInfoResponse userInfoResponse);

        void onReportUser();

        void onAddBlockUser();

        void loadComplete();

        void getDataEmojiSucess(EmojiReponse emojiReponse);

        /**
         * call favourite user success
         *
         * @see Presenter#setFavorite(AddFavoriteRequest, String)
         */
        void onFavouriteUser(String userId);

        /**
         * call unFavourite user success
         *
         * @see Presenter#setUnFavorite(RemoveFavoriteRequest, String)
         */
        void onUnFavouriteUser(String userId);
    }

    interface Presenter extends PresenterListener<View> {

        void getChatHistory(ChatHistoryRequest request);

        void getUserInfo(UserInfoRequest userInfoRequest);

        void reportUser(ReportRequest reportRequest);

        void blockUser(AddBlockUserRequest request);

        void getDataEmoji(EmojiRequest request);

        /**
         * favourite user
         *
         * @param addFavoriteRequest request favourite
         * @param userId             user id to notify when request success
         * @see #setUnFavorite(RemoveFavoriteRequest, String)
         */
        void setFavorite(AddFavoriteRequest addFavoriteRequest, String userId);

        /**
         * unFavourite user
         *
         * @param removeFavoriteRequest request favourite
         * @param userId                user id to notify when request success
         * @see #setFavorite(AddFavoriteRequest, String)
         */
        void setUnFavorite(RemoveFavoriteRequest removeFavoriteRequest, String userId);
    }
}
