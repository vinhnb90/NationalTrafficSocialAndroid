package com.vn.ntsc.ui.login;

import com.vn.ntsc.core.callback.CallbackListener;
import com.vn.ntsc.core.callback.PresenterListener;
import com.vn.ntsc.repository.model.login.LoginByEmailRequest;
import com.vn.ntsc.repository.model.login.LoginByFacebookRequest;
import com.vn.ntsc.repository.model.login.LoginResponse;
import com.vn.ntsc.repository.model.notification.UpdateNotificationRequest;

/**
 * Created by dev22 on 8/21/17.
 */
interface LoginContract {
    interface View extends CallbackListener {

        /**
         * when login success, go to time line
         *
         * @param response input
         */
        void onLoginEmailSuccess(LoginResponse response);

        /**
         * when login success, go to time line
         *
         * @param response input
         */
        void onLoginFBSuccess(LoginResponse response);

        /**
         * show email not found
         */
        void onLoginEmailNotFound();

        /**
         * when email not valid format
         */
        void onLoginEmailInvalid();

        /**
         * show password incorrect
         */
        void onLoginPasswordIncorrect();

        void onLoginPasswordInvalid();

        void gotoMain();

        /**
         * when login with fb fail
         */
        void showLoginFbError();

        /**
         * show user is locked
         */
        void onLockedUser();

        void showLoadingDialog();

        void hideLoadingDialog();

        /**
         * account not found if login with fb -> goto edit profile
         */
        void gotoEditProfile();


    }

    interface Presenter extends PresenterListener<View> {
        /**
         * after login with fb success, send fb id to server
         *
         * @param loginByFacebookRequest input
         */
        void onLoginFbSuccess(LoginByFacebookRequest loginByFacebookRequest);

        /**
         * login by email and password
         *
         * @param loginRequest input
         */
        void loginByEmail(LoginByEmailRequest loginRequest);

        void updateNotification(UpdateNotificationRequest updateNotificationRequest);

    }
}