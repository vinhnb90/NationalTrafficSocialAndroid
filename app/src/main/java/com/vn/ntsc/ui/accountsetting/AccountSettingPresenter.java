package com.vn.ntsc.ui.accountsetting;

import com.vn.ntsc.core.BasePresenter;
import com.vn.ntsc.core.callback.SubscriberCallback;
import com.vn.ntsc.core.model.ServerResponse;
import com.vn.ntsc.repository.model.accountsetting.ChangeAccFacebookRequest;
import com.vn.ntsc.repository.model.accountsetting.ChangeAccFacebookResponse;
import com.vn.ntsc.repository.model.accountsetting.ChangeEmailRequest;
import com.vn.ntsc.repository.model.accountsetting.ChangeEmailResponse;
import com.vn.ntsc.repository.model.accountsetting.ChangePasswordRequest;
import com.vn.ntsc.repository.model.accountsetting.ChangePasswordResponse;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by ThoNh on 10/6/2017.
 */

public class AccountSettingPresenter extends BasePresenter<AccountSettingContracts.View> implements AccountSettingContracts.Presenter {

    @Inject
    public AccountSettingPresenter() {
    }

    @Override
    public void changeEmail(final ChangeEmailRequest request) {
        addSubscriber(apiService.changeEmail(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .filter(new Predicate<ChangeEmailResponse>() {
                    @Override
                    public boolean test(@NonNull ChangeEmailResponse changeEmailResponse) throws Exception {
                        switch (changeEmailResponse.code) {
                            case ServerResponse.DefinitionCode.SERVER_EMAIL_REGISTERED:
                                view.emailAlreadyExist();
                                return false;

                            case ServerResponse.DefinitionCode.SERVER_INCORRECT_PASSWORD:
                                view.oldPasswordIncorrect();
                                return false;

                            case ServerResponse.DefinitionCode.SERVER_INVALID_PASSWORD:
                                view.newPasswordNotValid();
                                return false;

                            case ServerResponse.DefinitionCode.SERVER_INVALID_EMAIL:
                                view.newEmailInvalid();
                                return false;
                        }
                        return true;
                    }
                })
                .subscribeWith(new SubscriberCallback<ChangeEmailResponse>(view) {

                    @Override
                    public void onSuccess(ChangeEmailResponse response) {
                        view.changeEmailSuccess();
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        view.changeFailure();
                    }

                    @Override
                    public void onCompleted() {
                        view.complete();
                    }

                }));
    }

    @Override
    public void changePassword(final ChangePasswordRequest request) {
        addSubscriber(apiService.changePassword(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .filter(new Predicate<ChangePasswordResponse>() {
                    @Override
                    public boolean test(@NonNull ChangePasswordResponse response) throws Exception {

                        switch (response.code) {
                            case ServerResponse.DefinitionCode.SERVER_INCORRECT_PASSWORD:
                                view.oldPasswordIncorrect();
                                return false;

                            case ServerResponse.DefinitionCode.SERVER_INVALID_PASSWORD:
                                view.newPasswordNotValid();
                                return false;
                        }

                        return true;
                    }
                }).subscribeWith(new SubscriberCallback<ChangePasswordResponse>(view) {

                    @Override
                    public void onSuccess(ChangePasswordResponse response) {
                        view.changePasswordSuccess();
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        view.changeFailure();
                    }

                    @Override
                    public void onCompleted() {
                        view.complete();
                    }
                }));
    }

    @Override
    public void changeAccFacebook(ChangeAccFacebookRequest request) {
        addSubscriber(apiService.changeAccFacebook(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .filter(new Predicate<ChangeAccFacebookResponse>() {
                    @Override
                    public boolean test(@NonNull ChangeAccFacebookResponse changeEmailResponse) throws Exception {
                        switch (changeEmailResponse.code) {
                            case ServerResponse.DefinitionCode.SERVER_EMAIL_REGISTERED:
                                view.emailAlreadyExist();
                                return false;

                            case ServerResponse.DefinitionCode.SERVER_INVALID_PASSWORD:
                                view.newPasswordNotValid();
                                return false;

                            case ServerResponse.DefinitionCode.SERVER_INVALID_EMAIL:
                                view.newEmailInvalid();
                                return false;
                        }
                        return true;
                    }
                })

                .subscribeWith(new SubscriberCallback<ChangeAccFacebookResponse>(view) {
                    @Override
                    public void onSuccess(ChangeAccFacebookResponse response) {
                        if (response.code == 0) {
                            view.changeAccFacebookSuccess();
                        }
                    }

                    @Override
                    public void onCompleted() {

                    }
                }));
    }
}
