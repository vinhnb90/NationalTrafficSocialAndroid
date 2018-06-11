package com.vn.ntsc.ui.forgotpassword;

import com.vn.ntsc.core.BasePresenter;
import com.vn.ntsc.core.callback.SubscriberCallback;
import com.vn.ntsc.core.model.ServerResponse;
import com.vn.ntsc.repository.model.forgotpassword.ForgotPasswordRequest;
import com.vn.ntsc.repository.remote.ApiService;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by dev22 on 8/21/17.
 */
class ForgotPasswordPresenter extends BasePresenter<ForgotPasswordContract.View> implements ForgotPasswordContract.Presenter {
    private final ApiService apiService;

    @Inject
    ForgotPasswordPresenter(ApiService apiService) {
        this.apiService = apiService;
    }

    @Override
    public void onForgotPassword(ForgotPasswordRequest forgotPasswordRequest) {
        view.showLoadingDialog();
        addSubscriber(
                apiService.forgotPassword(forgotPasswordRequest)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .filter(new Predicate<ServerResponse>() {
                            @Override
                            public boolean test(@NonNull ServerResponse response) throws Exception {
                                boolean isEmailNotFound = response.code == ServerResponse.DefinitionCode.SERVER_EMAIL_NOT_FOUND;
                                if (isEmailNotFound)
                                    view.showEmailNotFound();
                                return !isEmailNotFound;
                            }
                        })
                        .subscribeWith(new SubscriberCallback<ServerResponse>(view) {
                            @Override
                            public void onSuccess(ServerResponse response) {
                                if (response.code == ServerResponse.DefinitionCode.SERVER_SUCCESS)
                                    view.showSuccessAlert();
                            }

                            @Override
                            public void onCompleted() {
                                view.hideLoadingDialog();
                            }
                        })
        );
    }
}