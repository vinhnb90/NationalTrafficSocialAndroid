package com.vn.ntsc.ui.search.result;

import com.vn.ntsc.core.callback.CallbackListener;
import com.vn.ntsc.core.callback.PresenterListener;
import com.vn.ntsc.repository.model.favorite.AddFavoriteRequest;
import com.vn.ntsc.repository.model.favorite.RemoveFavoriteRequest;
import com.vn.ntsc.repository.model.search.MeetPeopleRequest;
import com.vn.ntsc.repository.model.search.MeetPeopleResponse;
import com.vn.ntsc.repository.model.search.byname.SearchByNameRequest;

/**
 * Created by hnc on 22/08/2017.
 */

public interface SearchResultContract {
    interface View extends CallbackListener {

        void addFavoriteSuccess();

        void handleErrorUserNotFound();

        void handleErrorUserIsBlocked();

        void removeFavoriteSuccess();

        void onSearchSuccess(MeetPeopleResponse response);

        void onFinish();
    }

    interface Presenter extends PresenterListener<View> {

        void addFavorite(AddFavoriteRequest request);

        void removeFavorite(RemoveFavoriteRequest request);

        void searchBySetting(MeetPeopleRequest request);

        void searchByName(SearchByNameRequest request);
    }

}
