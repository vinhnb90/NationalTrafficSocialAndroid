package com.vn.ntsc.ui.friends.favorite;

import com.vn.ntsc.core.callback.CallbackListener;
import com.vn.ntsc.core.callback.PresenterListener;
import com.vn.ntsc.repository.model.favorite.FriendsFavoriteRequest;
import com.vn.ntsc.repository.model.favorite.FriendsFavoriteResponse;
import com.vn.ntsc.repository.model.favorite.RemoveFavoriteRequest;
import com.vn.ntsc.repository.model.favorite.RemoveFavoriteResponse;

/**
 * Created by hnc on 21/08/2017.
 */

public interface FriendsFavoriteContract {
    interface View extends CallbackListener {
        void onFriendsMeFavResponse(FriendsFavoriteResponse response);

        void onFriendsFavMeResponse(FriendsFavoriteResponse response);

        void onRemoveFriendsMeFavResponse(RemoveFavoriteResponse response);

        void onCompleted();
    }

    interface Presenter extends PresenterListener<View> {

        void requestListFriendsMeFavorite(FriendsFavoriteRequest request);

        void requestListFriendsFavoriteMe(FriendsFavoriteRequest request);

        void removeFriendsMeFavorite(RemoveFavoriteRequest request);
    }

}
