package com.vn.ntsc.ui.login;

import com.vn.ntsc.core.BasePresenter;
import com.vn.ntsc.core.callback.SubscriberCallback;
import com.vn.ntsc.core.model.ServerResponse;
import com.vn.ntsc.repository.model.login.LoginByEmailRequest;
import com.vn.ntsc.repository.model.login.LoginByFacebookRequest;
import com.vn.ntsc.repository.model.login.LoginResponse;
import com.vn.ntsc.repository.model.notification.UpdateNotificationRequest;
import com.vn.ntsc.repository.model.notification.UpdateNotificationResponse;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by dev22 on 8/21/17.
 */
class LoginPresenter extends BasePresenter<LoginContract.View> implements LoginContract.Presenter {
    @Inject
    LoginPresenter() {
    }

    @Override
    public void onLoginFbSuccess(LoginByFacebookRequest loginByFacebookRequest) {
        view.showLoadingDialog();
        addSubscriber(apiService.loginFacebook(loginByFacebookRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .filter(new Predicate<LoginResponse>() {
                    @Override
                    public boolean test(@NonNull LoginResponse loginResponse) throws Exception {
                        switch (loginResponse.code) {
                            case ServerResponse.DefinitionCode.SERVER_EMAIL_NOT_FOUND:
                                view.gotoEditProfile();
                                return false;
                            case ServerResponse.DefinitionCode.SERVER_INVALID_EMAIL:
                                view.onLoginEmailInvalid();
                                return false;
                            case ServerResponse.DefinitionCode.SERVER_INCORRECT_PASSWORD:
                                view.onLoginPasswordIncorrect();
                                return false;
                            case ServerResponse.DefinitionCode.SERVER_INVALID_PASSWORD:
                                view.onLoginPasswordInvalid();
                                return false;
                            case ServerResponse.DefinitionCode.SERVER_LOOKED_USER:
                                view.onLockedUser();
                                return false;
                        }
                        return true;
                    }
                })
                .subscribeWith(new SubscriberCallback<LoginResponse>(view) {
                                   @Override
                                   public void onCompleted() {
                                       view.hideLoadingDialog();
                                   }

                                   @Override
                                   public void onSuccess(LoginResponse response) {
                                       view.onLoginFBSuccess(response);
                                   }
                               }
                )
        );
    }

    @Override
    public void loginByEmail(LoginByEmailRequest loginRequest) {
        view.showLoadingDialog();
        addSubscriber(apiService.loginEmail(loginRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .filter(new Predicate<LoginResponse>() {
                    @Override
                    public boolean test(@NonNull LoginResponse loginResponse) throws Exception {
                        switch (loginResponse.code) {
                            case ServerResponse.DefinitionCode.SERVER_EMAIL_NOT_FOUND:
                                view.onLoginEmailNotFound();
                                return false;
                            case ServerResponse.DefinitionCode.SERVER_INVALID_EMAIL:
                                view.onLoginEmailInvalid();
                                return false;
                            case ServerResponse.DefinitionCode.SERVER_INCORRECT_PASSWORD:
                                view.onLoginPasswordIncorrect();
                                return false;
                            case ServerResponse.DefinitionCode.SERVER_INVALID_PASSWORD:
                                view.onLoginPasswordInvalid();
                                return false;
                            case ServerResponse.DefinitionCode.SERVER_LOOKED_USER:
                                view.onLockedUser();
                                return false;
                        }
                        return true;
                    }
                })
                .subscribeWith(new SubscriberCallback<LoginResponse>(view) {
                                   @Override
                                   public void onCompleted() {
                                       view.hideLoadingDialog();
                                   }

                                   @Override
                                   public void onSuccess(LoginResponse response) {
                                       if (response.code == ServerResponse.DefinitionCode.SERVER_SUCCESS)
                                           view.onLoginEmailSuccess(response);
                                   }
                               }
                )
        );
    }

    @Override
    public void updateNotification(UpdateNotificationRequest updateNotificationRequest) {
        view.showLoadingDialog();
        addSubscriber(apiService.getUpdateNotification(updateNotificationRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new SubscriberCallback<UpdateNotificationResponse>(view) {
                                   @Override
                                   public void onCompleted() {
                                       view.hideLoadingDialog();
                                   }

                                   @Override
                                   public void onSuccess(UpdateNotificationResponse response) {
                                       view.gotoMain();
                                   }
                               }
                )
        );
    }

}