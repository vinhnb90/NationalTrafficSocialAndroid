package com.vn.ntsc.ui.changepassword;

import com.vn.ntsc.core.BasePresenter;
import com.vn.ntsc.core.callback.SubscriberCallback;
import com.vn.ntsc.core.model.ServerResponse;
import com.vn.ntsc.repository.model.changepassword.ChangePasswordRequest;
import com.vn.ntsc.repository.model.changepassword.ChangePasswordResponse;
import com.vn.ntsc.repository.remote.ApiService;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;


/**
 * Created by dev22 on 8/21/17.
 */
class ChangePasswordPresenter extends BasePresenter<ChangePasswordContract.View> implements ChangePasswordContract.Presenter {
    private final ApiService apiService;

    @Inject
    ChangePasswordPresenter(ApiService apiService) {
        this.apiService = apiService;
    }

    @Override
    public void onChangePassword(ChangePasswordRequest changePasswordRequest) {
        view.showLoadingDialog();
        addSubscriber(apiService.changePassword(changePasswordRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .filter(new Predicate<ChangePasswordResponse>() {
                            @Override
                            public boolean test(@NonNull ChangePasswordResponse changePasswordResponse) throws Exception {
                                switch (changePasswordResponse.code) {
                                    case ServerResponse.DefinitionCode.SERVER_EMAIL_NOT_FOUND:
                                        view.onEmailNotFound();
                                        return false;
                                    case ServerResponse.DefinitionCode.SERVER_INCORRECT_CODE:
                                        view.onCodeIncorrect();
                                        return false;
                                    case ServerResponse.DefinitionCode.SERVER_LOOKED_USER:
                                        view.onLockedUser();
                                        return false;
                                }
                                return true;
                            }
                        }
                ).subscribeWith(new SubscriberCallback<ChangePasswordResponse>(view) {
                    @Override
                    public void onSuccess(ChangePasswordResponse response) {
                        if (response.code == ServerResponse.DefinitionCode.SERVER_SUCCESS){
                            view.saveAuthData(response.authenData);
                            view.gotoMain();
                        }
                    }

                    @Override
                    public void onCompleted() {
                        view.hideLoadingDialog();
                    }
                })
        );
    }
}
