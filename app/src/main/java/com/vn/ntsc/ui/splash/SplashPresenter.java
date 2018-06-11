package com.vn.ntsc.ui.splash;

import com.google.gson.Gson;
import com.vn.ntsc.core.BasePresenter;
import com.vn.ntsc.core.callback.SubscriberCallback;
import com.vn.ntsc.core.model.ServerResponse;
import com.vn.ntsc.repository.model.applicationinfo.GetApplicationInfoRequest;
import com.vn.ntsc.repository.model.applicationinfo.GetApplicationInfoResponse;
import com.vn.ntsc.repository.model.applicationinfo.GetUpdateInfoFlagRequest;
import com.vn.ntsc.repository.model.applicationinfo.GetUpdateInfoFlagResponse;
import com.vn.ntsc.repository.model.installcount.InstallCountRequest;
import com.vn.ntsc.repository.model.installcount.InstallCountResponse;
import com.vn.ntsc.repository.model.user.BannedWordRequest;
import com.vn.ntsc.repository.model.user.BannedWordResponse;
import com.vn.ntsc.repository.model.user.GetUserStatusRequest;
import com.vn.ntsc.repository.model.user.GetUserStatusResponse;
import com.vn.ntsc.repository.model.user.SetCareUserInfoRequest;
import com.vn.ntsc.repository.model.user.SetCareUserInfoResponse;
import com.vn.ntsc.repository.remote.ApiService;
import com.vn.ntsc.utils.LogUtils;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;


/**
 * Created by nankai on 8/3/2017.
 */

public class SplashPresenter extends BasePresenter<SplashContract.View> implements SplashContract.Presenter {

    @Inject
    public SplashPresenter(ApiService apiService) {
        this.apiService = apiService;
    }

    @Override
    public void getApplicationInfo() {
        GetApplicationInfoRequest infoRequest = new GetApplicationInfoRequest();
        addSubscriber(
                apiService.getInfoForApplication(infoRequest)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .filter(new Predicate<GetApplicationInfoResponse>() {
                            @Override
                            public boolean test(@NonNull GetApplicationInfoResponse response) throws Exception {
                                if (response.code != ServerResponse.DefinitionCode.SERVER_SUCCESS) {
                                    view.onInvalid(response);
                                    return false;
                                }
                                return true;
                            }
                        })
                        .subscribeWith(new SubscriberCallback<GetApplicationInfoResponse>(view) {
                            @Override
                            public void onSuccess(GetApplicationInfoResponse response) {
                                view.onApplicationInfo(response);
                            }

                            @Override
                            public void onCompleted() {

                            }
                        })
        );
    }

    @Override
    public void sendInstallCount(InstallCountRequest installCountRequest) {
        addSubscriber(apiService.installApplication(installCountRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .filter(new Predicate<InstallCountResponse>() {
                    @Override
                    public boolean test(@NonNull InstallCountResponse response) throws Exception {
                        if (response.code != ServerResponse.DefinitionCode.SERVER_SUCCESS) {
                            view.onInvalid(response);
                            return false;
                        }
                        return true;
                    }
                })
                .subscribeWith(new SubscriberCallback<InstallCountResponse>(view) {
                    @Override
                    public void onSuccess(InstallCountResponse response) {
                        view.onInstallCount(response);
                    }

                    @Override
                    public void onCompleted() {

                    }
                }));
    }

    @Override
    public void updateInfoFlagRequest(GetUpdateInfoFlagRequest updateInfoFlagRequest) {
        addSubscriber(apiService.updateInfoFlagRequest(updateInfoFlagRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .filter(new Predicate<GetUpdateInfoFlagResponse>() {
                    @Override
                    public boolean test(@NonNull GetUpdateInfoFlagResponse response) throws Exception {
                        if (response.code == ServerResponse.DefinitionCode.SERVER_ACCESS_DENIED
                                || response.code == ServerResponse.DefinitionCode.SERVER_EXPIRED_TOKEN
                                || response.code == ServerResponse.DefinitionCode.SERVER_INVALID_TOKEN) {
                            view.onInvalid(response);
                            return false;
                        }
                        return true;
                    }
                })
                .subscribeWith(new SubscriberCallback<GetUpdateInfoFlagResponse>(view) {
                    @Override
                    public void onSuccess(GetUpdateInfoFlagResponse response) {
                        LogUtils.e("UpdateInfoFlag", "respons:" + new Gson().toJson(response));
                        view.onUpdateInfoFlag(response);
                    }

                    @Override
                    public void onCompleted() {

                    }
                }));
    }

    @Override
    public void getUserStatusRequest(GetUserStatusRequest userStatusRequest) {
        addSubscriber(apiService.getUserStatus(userStatusRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .filter(new Predicate<GetUserStatusResponse>() {
                    @Override
                    public boolean test(@NonNull GetUserStatusResponse response) throws Exception {
                        if (response.code != ServerResponse.DefinitionCode.SERVER_SUCCESS) {
                            view.onInvalid(response);
                            return false;
                        }
                        return true;
                    }
                })
                .subscribeWith(new SubscriberCallback<GetUserStatusResponse>(view) {
                    @Override
                    public void onSuccess(GetUserStatusResponse response) {
                        view.onUserStatus(response);
                    }

                    @Override
                    public void onCompleted() {

                    }
                }));
    }

    @Override
    public void setCareUserInfo(SetCareUserInfoRequest setCareUserInfoRequest) {
        addSubscriber(apiService.setCeraUserInfo(setCareUserInfoRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .filter(new Predicate<SetCareUserInfoResponse>() {
                    @Override
                    public boolean test(@NonNull SetCareUserInfoResponse response) throws Exception {
                        if (response.code != ServerResponse.DefinitionCode.SERVER_SUCCESS) {
                            view.onInvalid(response);
                            return false;
                        }
                        return true;
                    }
                })
                .subscribeWith(new SubscriberCallback<SetCareUserInfoResponse>(view) {
                    @Override
                    public void onSuccess(SetCareUserInfoResponse response) {
                        view.onCareUserInfo(response);
                    }

                    @Override
                    public void onCompleted() {

                    }
                })
        );
    }

    @Override
    public void getBannedWords(int bannedWordVersion) {
        BannedWordRequest request = new BannedWordRequest(bannedWordVersion);
        addSubscriber(apiService.getBannedWords(request)
                .subscribeOn(Schedulers.io())
                .filter(new Predicate<BannedWordResponse>() {
                    @Override
                    public boolean test(BannedWordResponse bannedWordResponse) {
                        return bannedWordResponse.code == ServerResponse.DefinitionCode.SERVER_SUCCESS;
                    }
                })
                .subscribeWith(new SubscriberCallback<BannedWordResponse>(view) {
                    @Override
                    public void onSuccess(BannedWordResponse response) {
                        view.onUpdateBannedWords(response);
                    }

                    @Override
                    public void onCompleted() {
                        view.onCompleteGetBannedWord();
                    }
                }));
    }
}
