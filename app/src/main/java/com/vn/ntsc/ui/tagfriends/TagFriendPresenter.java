package com.vn.ntsc.ui.tagfriends;

import com.vn.ntsc.core.BasePresenter;
import com.vn.ntsc.core.callback.SubscriberCallback;
import com.vn.ntsc.repository.model.poststatus.tagfriend.TagFriendsFavoriteRequest;
import com.vn.ntsc.repository.model.poststatus.tagfriend.TagFriendsFavoriteResponse;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Robert on 2017 Sep 13
 * This specifies the Tag friend the presenter implementation.
 */
public class TagFriendPresenter extends BasePresenter<TagFriendContract.View> implements TagFriendContract.Presenter {

    @Inject
    public TagFriendPresenter() {
    }

    @Override
    public void getListFriendsMeFavorite(TagFriendsFavoriteRequest request) {
        addSubscriber(apiService.getListMeFavorites(request)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(new SubscriberCallback<TagFriendsFavoriteResponse>(view) {
                @Override
                public void onSuccess(TagFriendsFavoriteResponse response) {
                    if (view != null) view.onFriendsFavMeResponse(response);
                }

                @Override
                public void onCompleted() {
                    if (view != null) view.onGetFavYourSelfComplete();
                }
            }));
    }
}
