package com.vn.ntsc.ui.profile.edit;

import com.vn.ntsc.core.callback.CallbackListener;
import com.vn.ntsc.core.callback.PresenterListener;
import com.vn.ntsc.repository.model.editprofile.EditProfileRequest;
import com.vn.ntsc.repository.model.login.AuthenticationBean;
import com.vn.ntsc.repository.model.signup.SignUpByFacebookRequest;
import com.vn.ntsc.repository.model.user.UserInfoRequest;
import com.vn.ntsc.repository.model.user.UserInfoResponse;

interface EditProfileContract {
    interface View extends CallbackListener {
        /**
         * update profile success, save auth data to share preference then go to main
         *
         * @param authData to save into share preference
         * @see Presenter#editProfile(EditProfileRequest, String, String, String)
         */
        void gotoMain(AuthenticationBean authData);

        /**
         * invalid token when call {@link Presenter#editProfile(EditProfileRequest, String, String, String)} update profile}
         */
        void onInvalidToken();

        /**
         * upload avatar fail
         */
        void onUploadAvatarFail();


        void onLoadUserInfoSuccess(UserInfoResponse response);

        void showLoadingDialog();

        void hideLoadingDialog();

        /**
         * show birthday error
         */
        void onInvalidBirthday();
    }

    interface Presenter extends PresenterListener<View> {
        /**
         * update profile info
         *
         * @param editProfileRequest input
         */
        void editProfile(EditProfileRequest editProfileRequest, String token, String sum, String path);

        /**
         * request user info
         *
         * @param request input
         */
        void requestUserInfo(UserInfoRequest request);

        /**
         * signup with fb
         *
         * @param signUpByFacebookRequest input
         */
        void signUpFabook(SignUpByFacebookRequest signUpByFacebookRequest);
    }
}