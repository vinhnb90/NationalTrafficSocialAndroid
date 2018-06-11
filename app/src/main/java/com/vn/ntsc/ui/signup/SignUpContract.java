package com.vn.ntsc.ui.signup;

import com.vn.ntsc.core.callback.CallbackListener;
import com.vn.ntsc.core.callback.PresenterListener;
import com.vn.ntsc.repository.model.applicationinfo.GetUpdateInfoFlagResponse;
import com.vn.ntsc.repository.model.login.AuthenticationBean;
import com.vn.ntsc.repository.model.login.LoginByFacebookRequest;
import com.vn.ntsc.repository.model.signup.SignUpRequest;
import com.vn.ntsc.repository.model.signup.SignUpResponse;

/**
 * Created by nankai on 8/3/2017.
 */

interface SignUpContract {

    interface View extends CallbackListener {
        /**
         * after sign up success
         *
         * @see Presenter#signUp(SignUpRequest)
         */
        void gotoEditProfile();

        /**
         * save preference for use later<br/>
         * NOTE: finishRegisterFlag & userId for direction later
         *
         * @param response success
         * @see com.vn.ntsc.ui.splash.SplashActivity#onUpdateInfoFlag(GetUpdateInfoFlagResponse) save user info on bg_splash
         */
        void savePreference(SignUpResponse response);

        /**
         * email already exist, please select other
         *
         * @see Presenter#signUp(SignUpRequest)
         */
        void onSignUpEmailRegistered();

        /**
         * invalid email format
         *
         * @see Presenter#signUp(SignUpRequest)
         */
        void onSignUpEmailInvalid();

        // ??
        void onSignUpPasswordInvalid();

        /**
         * login with fb success
         */
        void gotoMain();

        /**
         * goto edit profile with fb id
         *
         * @param fbId send to edit profile
         */
        void gotoEditProfile(String fbId);

        /**
         * save auth data when login with fb success
         * @param authenData input
         */
        void saveAuthData(AuthenticationBean authenData);

        void showLoadingDialog();

        void hideLoadingDialog();
    }

    interface Presenter extends PresenterListener<View> {
        /**
         * send fb id to server
         *
         * @param loginByFacebookRequest input
         */
        void loginByFacebook(LoginByFacebookRequest loginByFacebookRequest);

        /**
         * sign up with user input
         *
         * @param signupRequest input
         */
        void signUp(SignUpRequest signupRequest);
    }
}
