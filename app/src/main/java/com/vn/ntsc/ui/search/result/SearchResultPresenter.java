package com.vn.ntsc.ui.search.result;

import com.vn.ntsc.core.BasePresenter;
import com.vn.ntsc.core.callback.SubscriberCallback;
import com.vn.ntsc.core.model.ServerResponse;
import com.vn.ntsc.repository.model.favorite.AddFavoriteRequest;
import com.vn.ntsc.repository.model.favorite.AddFavoriteResponse;
import com.vn.ntsc.repository.model.favorite.RemoveFavoriteRequest;
import com.vn.ntsc.repository.model.favorite.RemoveFavoriteResponse;
import com.vn.ntsc.repository.model.search.MeetPeopleRequest;
import com.vn.ntsc.repository.model.search.MeetPeopleResponse;
import com.vn.ntsc.repository.model.search.byname.SearchByNameRequest;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by hnc on 22/08/2017.
 */

public class SearchResultPresenter extends BasePresenter<SearchResultContract.View> implements SearchResultContract.Presenter {

    @Inject
    public SearchResultPresenter() {

    }

    @Override
    public void addFavorite(final AddFavoriteRequest request) {
        addSubscriber(apiService.onAddFavorite(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .filter(new Predicate<AddFavoriteResponse>() {
                    @Override
                    public boolean test(@NonNull AddFavoriteResponse addFavoriteResponse) throws Exception {
                        switch (addFavoriteResponse.code) {
                            case ServerResponse.DefinitionCode.SERVER_BLOCKED_USER:
                                view.handleErrorUserIsBlocked();
                                return false;
                            case ServerResponse.DefinitionCode.SERVER_USER_NOT_EXIST:
                                view.handleErrorUserNotFound();
                                return false;
                        }
                        return true;
                    }
                })
                .subscribeWith(new SubscriberCallback<AddFavoriteResponse>(view) {
                    @Override
                    public void onSuccess(AddFavoriteResponse response) {
                        view.addFavoriteSuccess();
                    }

                    @Override
                    public void onCompleted() {

                    }
                }));
    }

    @Override
    public void removeFavorite(final RemoveFavoriteRequest request) {
        addSubscriber(apiService.removeFavorite(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .filter(new Predicate<RemoveFavoriteResponse>() {
                    @Override
                    public boolean test(@NonNull RemoveFavoriteResponse response) throws Exception {
                        switch (response.code) {
                            case ServerResponse.DefinitionCode.SERVER_USER_NOT_EXIST:
                                view.handleErrorUserNotFound();
                                return false;
                        }
                        return true;
                    }
                })
                .subscribeWith(new SubscriberCallback<RemoveFavoriteResponse>(view) {
                    @Override
                    public void onSuccess(RemoveFavoriteResponse response) {
                        view.removeFavoriteSuccess();
                    }

                    @Override
                    public void onCompleted() {

                    }
                }));
    }

    @Override
    public void searchBySetting(MeetPeopleRequest request) {
        addSubscriber(apiService.getResultSearchSetting(request)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new SubscriberCallback<MeetPeopleResponse>(view) {
                    @Override
                    public void onSuccess(MeetPeopleResponse response) {
                        view.onSearchSuccess(response);
                    }

                    @Override
                    public void onCompleted() {
                        view.onFinish();
                    }
                }));
    }

    @Override
    public void searchByName(SearchByNameRequest request) {
        addSubscriber(apiService.searchByName(request)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new SubscriberCallback<MeetPeopleResponse>(view) {
                    @Override
                    public void onSuccess(MeetPeopleResponse response) {
                        view.onSearchSuccess(response);
                    }

                    @Override
                    public void onCompleted() {
                        view.onFinish();
                    }
                }));
    }
}
