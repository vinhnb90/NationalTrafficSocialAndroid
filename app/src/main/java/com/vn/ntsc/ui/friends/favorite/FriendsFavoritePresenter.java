package com.vn.ntsc.ui.friends.favorite;

import com.vn.ntsc.core.BasePresenter;
import com.vn.ntsc.core.callback.SubscriberCallback;
import com.vn.ntsc.core.model.ServerResponse;
import com.vn.ntsc.repository.model.favorite.FriendsFavoriteRequest;
import com.vn.ntsc.repository.model.favorite.FriendsFavoriteResponse;
import com.vn.ntsc.repository.model.favorite.RemoveFavoriteRequest;
import com.vn.ntsc.repository.model.favorite.RemoveFavoriteResponse;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by hnc on 08/08/2017.
 */

public class FriendsFavoritePresenter extends BasePresenter<FriendsFavoriteContract.View> implements FriendsFavoriteContract.Presenter {

    @Inject
    public FriendsFavoritePresenter() {

    }

    @Override
    public void requestListFriendsMeFavorite(FriendsFavoriteRequest request) {
        addSubscriber(apiService.getListMeFavorites(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .filter(new Predicate<FriendsFavoriteResponse>() {
                    @Override
                    public boolean test(FriendsFavoriteResponse response) throws Exception {
                        //Show dialog notice token expired  or invalid
                        if (response.code == ServerResponse.DefinitionCode.SERVER_EXPIRED_TOKEN || response.code == ServerResponse.DefinitionCode.SERVER_INVALID_TOKEN) {
                            view.onServerResponseInvalid(response.code, response);
                            return false;
                        }
                        //Handle other case of error in itself (intrinsic FriendFavoriteFragment)
                        if (response.code != ServerResponse.DefinitionCode.SERVER_SUCCESS) {
                            view.onFailure(response.code, null);
                        }
                        return true;
                    }
                })
                .subscribeWith(new SubscriberCallback<FriendsFavoriteResponse>(view) {
                    @Override
                    public void onSuccess(FriendsFavoriteResponse response) {
                        view.onFriendsFavMeResponse(response);
                    }

                    @Override
                    public void onCompleted() {
                        view.onCompleted();
                    }
                }));
    }

    @Override
    public void requestListFriendsFavoriteMe(FriendsFavoriteRequest request) {
        addSubscriber(apiService.getListFavoriteMe(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .filter(new Predicate<FriendsFavoriteResponse>() {
                    @Override
                    public boolean test(FriendsFavoriteResponse response) throws Exception {
                        //Show dialog notice token expired  or invalid
                        if (response.code == ServerResponse.DefinitionCode.SERVER_EXPIRED_TOKEN || response.code == ServerResponse.DefinitionCode.SERVER_INVALID_TOKEN) {
                            view.onServerResponseInvalid(response.code, response);
                            return false;
                        }
                        //Handle other case of error in itself (intrinsic FriendFavoriteFragment)
                        if (response.code != ServerResponse.DefinitionCode.SERVER_SUCCESS) {
                            view.onFailure(response.code, null);
                        }
                        return true;
                    }
                })
                .subscribeWith(new SubscriberCallback<FriendsFavoriteResponse>(view) {
                    @Override
                    public void onSuccess(FriendsFavoriteResponse response) {
                        view.onFriendsMeFavResponse(response);
                    }

                    @Override
                    public void onCompleted() {
                        view.onCompleted();
                    }
                }));
    }

    @Override
    public void removeFriendsMeFavorite(RemoveFavoriteRequest request) {
        addSubscriber(apiService.removeFavorite(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .filter(new Predicate<RemoveFavoriteResponse>() {
                    @Override
                    public boolean test(RemoveFavoriteResponse response) throws Exception {
                        //Show dialog notice token expired  or invalid
                        if (response.code == ServerResponse.DefinitionCode.SERVER_EXPIRED_TOKEN || response.code == ServerResponse.DefinitionCode.SERVER_INVALID_TOKEN) {
                            view.onServerResponseInvalid(response.code, response);
                            return false;
                        }
                        //Handle other case of error in itself (intrinsic FriendFavoriteFragment)
                        if (response.code != ServerResponse.DefinitionCode.SERVER_SUCCESS) {
                            view.onFailure(response.code, null);
                        }
                        return true;
                    }
                })
                .subscribeWith(new SubscriberCallback<RemoveFavoriteResponse>(view) {
                    @Override
                    public void onSuccess(RemoveFavoriteResponse response) {
                        view.onRemoveFriendsMeFavResponse(response);
                    }

                    @Override
                    public void onCompleted() {
                        view.onCompleted();
                    }
                }));

    }
}
