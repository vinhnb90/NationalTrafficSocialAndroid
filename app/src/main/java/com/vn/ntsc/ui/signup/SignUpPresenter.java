package com.vn.ntsc.ui.signup;

import com.vn.ntsc.core.BasePresenter;
import com.vn.ntsc.core.callback.SubscriberCallback;
import com.vn.ntsc.core.model.ServerResponse;
import com.vn.ntsc.repository.model.login.LoginByFacebookRequest;
import com.vn.ntsc.repository.model.login.LoginResponse;
import com.vn.ntsc.repository.model.signup.SignUpRequest;
import com.vn.ntsc.repository.model.signup.SignUpResponse;
import com.vn.ntsc.repository.remote.ApiService;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by nankai on 8/3/2017.
 */

public class SignUpPresenter extends BasePresenter<SignUpContract.View> implements SignUpContract.Presenter {

    @Inject
    SignUpPresenter(ApiService apiService) {
        this.apiService = apiService;
    }

    @Override
    public void loginByFacebook(final LoginByFacebookRequest loginByFacebookRequest) {
        view.showLoadingDialog();
        addSubscriber(apiService.loginFacebook(loginByFacebookRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .filter(new Predicate<LoginResponse>() {
                    @Override
                    public boolean test(@NonNull LoginResponse loginResponse) throws Exception {
                        if (loginResponse.code == ServerResponse.DefinitionCode.SERVER_EMAIL_NOT_FOUND) {
                            view.gotoEditProfile(loginByFacebookRequest.fb_id);
                            return false;
                        }
                        return true;
                    }
                })
                .subscribeWith(
                        new SubscriberCallback<LoginResponse>(view) {
                            @Override
                            public void onCompleted() {
                                view.hideLoadingDialog();
                            }

                            @Override
                            public void onSuccess(LoginResponse response) {
                                if (response.code == ServerResponse.DefinitionCode.SERVER_SUCCESS) {
                                    view.saveAuthData(response.authenData);
                                    view.gotoMain();
                                }
                            }
                        }
                ));
    }

    @Override
    public void signUp(SignUpRequest signupRequest) {
        view.showLoadingDialog();
        addSubscriber(apiService.signUp(signupRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .filter(new Predicate<SignUpResponse>() {
                    @Override
                    public boolean test(@NonNull SignUpResponse signUpResponse) throws Exception {
                        switch (signUpResponse.code) {
                            case ServerResponse.DefinitionCode.SERVER_EMAIL_REGISTERED:
                                view.onSignUpEmailRegistered();
                                return false;
                            case ServerResponse.DefinitionCode.SERVER_INVALID_EMAIL:
                                view.onSignUpEmailInvalid();
                                return false;
                            case ServerResponse.DefinitionCode.SERVER_INVALID_PASSWORD:
                                view.onSignUpPasswordInvalid();
                                return false;
                        }
                        return true;
                    }
                })
                .subscribeWith(
                        new SubscriberCallback<SignUpResponse>(view) {
                            @Override
                            public void onCompleted() {
                                view.hideLoadingDialog();
                            }

                            @Override
                            public void onSuccess(SignUpResponse response) {
                                view.savePreference(response);
                                view.gotoEditProfile();
                            }
                        }
                ));
    }
}
