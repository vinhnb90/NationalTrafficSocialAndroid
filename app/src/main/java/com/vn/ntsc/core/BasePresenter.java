package com.vn.ntsc.core;

import android.content.Context;
import android.content.DialogInterface;

import com.nankai.designlayout.dialog.DialogMaterial;
import com.nankai.designlayout.dialog.enums.Style;
import com.vn.ntsc.R;
import com.vn.ntsc.core.callback.CallbackListener;
import com.vn.ntsc.core.callback.PresenterListener;
import com.vn.ntsc.core.callback.SubscriberCallback;
import com.vn.ntsc.core.model.ServerResponse;
import com.vn.ntsc.repository.model.login.LoginByEmailRequest;
import com.vn.ntsc.repository.model.login.LoginByFacebookRequest;
import com.vn.ntsc.repository.model.login.LoginResponse;
import com.vn.ntsc.repository.model.login.UserLogin;
import com.vn.ntsc.repository.preferece.UserPreferences;
import com.vn.ntsc.repository.remote.ApiService;
import com.vn.ntsc.utils.LogUtils;
import com.vn.ntsc.utils.Utils;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;


/**
 * Created by nankai on 11/22/2016.
 */

public abstract class BasePresenter<T extends CallbackListener> implements PresenterListener<T> {

    @Inject
    protected ApiService apiService;

    protected T view;
    private CompositeDisposable disposables = null;

    @Override
    final public void attachView(T view) {
        this.view = view;
        if (disposables == null
                || disposables.isDisposed())
            disposables = new CompositeDisposable();
    }

    final public boolean isAttachView() {
        return (view != null || disposables != null);
    }

    final public void addSubscriber(Disposable subscriber) {
        disposables.add(subscriber);
    }

    @Override
    final public void detachView() {
        if (disposables != null) {
            disposables.dispose();
            disposables = null;
        }
        this.view = null;
    }

    @Override
    final public void onAutoLogin(Context context) {
        LogUtils.i("BasePresenter", "----------------------------- AutoLogin ----------------------------- ");
        final UserLogin userLogin = UserLogin.onAutoLogin();
        if (!UserPreferences.getInstance().isLogout()) {
            new DialogMaterial.Builder(context)
                    .setStyle(Style.HEADER_WITH_TITLE)
                    .setTitle(R.string.auto_login)
                    .setContent(R.string.auto_login_content)
                    .setCancelable(false)
                    .onPositive(R.string.common_ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (!Utils.isEmptyOrNull(userLogin.facebookId)) {
                                LogUtils.i("BasePresenter", "---------------> Auto Login Facebook");
                                LoginByFacebookRequest loginRequest = new LoginByFacebookRequest(userLogin);
                                addSubscriber(apiService.loginFacebook(loginRequest)
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .filter(new Predicate<LoginResponse>() {
                                            @Override
                                            public boolean test(LoginResponse loginResponse) throws Exception {
                                                if (loginResponse.code == ServerResponse.DefinitionCode.SERVER_INCORRECT_PASSWORD
                                                        || loginResponse.code == ServerResponse.DefinitionCode.SERVER_INVALID_PASSWORD) {
                                                    LogUtils.i("BasePresenter", "---------------> Password Not Found");
                                                    view.onPasswordNotFound(loginResponse);
                                                    return false;
                                                } else if (loginResponse.code == ServerResponse.DefinitionCode.SERVER_EMAIL_NOT_FOUND
                                                        || loginResponse.code == ServerResponse.DefinitionCode.SERVER_INVALID_EMAIL) {
                                                    LogUtils.i("BasePresenter", "---------------> Email Not Found");
                                                    view.onEmailNotFound(loginResponse);
                                                    return false;
                                                }
                                                return true;
                                            }
                                        })
                                        .subscribeWith(new SubscriberCallback<LoginResponse>(view) {
                                            @Override
                                            public void onSuccess(LoginResponse loginResponse) {
                                                view.onAutoLogin(loginResponse);
                                            }

                                            @Override
                                            public void onCompleted() {

                                            }
                                        }));
                            } else if (!Utils.isEmptyOrNull(userLogin.email) && !Utils.isEmptyOrNull(userLogin.pass)) {
                                LogUtils.i("BasePresenter", "---------------> Auto Login Email");
                                LoginByEmailRequest loginRequest = new LoginByEmailRequest(userLogin);
                                addSubscriber(apiService.loginEmail(loginRequest)
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .filter(new Predicate<LoginResponse>() {
                                            @Override
                                            public boolean test(LoginResponse loginResponse) throws Exception {
                                                if (loginResponse.code == ServerResponse.DefinitionCode.SERVER_INCORRECT_PASSWORD
                                                        || loginResponse.code == ServerResponse.DefinitionCode.SERVER_INVALID_PASSWORD) {
                                                    LogUtils.i("BasePresenter", "---------------> Password Not Found");
                                                    view.onPasswordNotFound(loginResponse);
                                                    return false;
                                                } else if (loginResponse.code == ServerResponse.DefinitionCode.SERVER_EMAIL_NOT_FOUND
                                                        || loginResponse.code == ServerResponse.DefinitionCode.SERVER_INVALID_EMAIL) {
                                                    LogUtils.i("BasePresenter", "---------------> Email Not Found");
                                                    view.onEmailNotFound(loginResponse);
                                                    return false;
                                                }
                                                return true;
                                            }
                                        })
                                        .subscribeWith(new SubscriberCallback<LoginResponse>(view) {
                                            @Override
                                            public void onSuccess(LoginResponse loginResponse) {
                                                view.onAutoLogin(loginResponse);
                                            }

                                            @Override
                                            public void onCompleted() {

                                            }
                                        }));
                            } else {
                                LogUtils.i("BasePresenter", "---------------> Show Dialog Login");
                                view.onShowDialogLogin(ServerResponse.DefinitionCode.SERVER_EXPIRED_TOKEN);
                            }

                        }
                    })
                    .onNegative(R.string.common_cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();
        } else {
            LogUtils.i("BasePresenter", "---------------> Show Dialog Login");
            view.onShowDialogLogin(ServerResponse.DefinitionCode.SERVER_EXPIRED_TOKEN);
        }
    }
}