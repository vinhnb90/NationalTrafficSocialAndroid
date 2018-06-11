package com.vn.ntsc.ui.tagfriends;

import com.vn.ntsc.core.callback.CallbackListener;
import com.vn.ntsc.core.callback.PresenterListener;
import com.vn.ntsc.repository.model.poststatus.tagfriend.TagFriendsFavoriteRequest;
import com.vn.ntsc.repository.model.poststatus.tagfriend.TagFriendsFavoriteResponse;

/**
 * Created by Robert on 2017 Aug 28
 * This specifies the Post status contract between the view and the presenter.
 */
public interface TagFriendContract {

    interface View extends CallbackListener {

        /**
         * Call when successful request get list friend were favorites of yourself
         * @param response
         */
        void onFriendsFavMeResponse(TagFriendsFavoriteResponse response);

        /**
         * Call when getLstBlockComplete request get list friend were favorites of yourself
         */
        void onGetFavYourSelfComplete();
    }

    interface Presenter extends PresenterListener<TagFriendContract.View> {

        void getListFriendsMeFavorite(TagFriendsFavoriteRequest request);
    }
}
