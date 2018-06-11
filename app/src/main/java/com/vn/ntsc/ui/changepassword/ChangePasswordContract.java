package com.vn.ntsc.ui.changepassword;

import com.vn.ntsc.core.callback.CallbackListener;
import com.vn.ntsc.core.callback.PresenterListener;
import com.vn.ntsc.repository.model.changepassword.ChangePasswordRequest;
import com.vn.ntsc.repository.model.login.AuthenticationBean;

/**
 * Created by dev22 on 8/21/17.
 */
interface ChangePasswordContract {
    interface View extends CallbackListener {
        /**
         * when change password success
         */
        void gotoMain();

        /**
         * email not exist to send recovery code
         */
        void onEmailNotFound();

        /**
         * incorrect verify code
         */
        void onCodeIncorrect();

        /**
         * user locked
         */
        void onLockedUser();

        /**
         * save preference when change password success
         * @param authenData input
         */
        void saveAuthData(AuthenticationBean authenData);

        void showLoadingDialog();

        void hideLoadingDialog();
    }

    interface Presenter extends PresenterListener<View> {
        /**
         * submit request change password
         *
         * @param changePasswordRequest input
         */
        void onChangePassword(ChangePasswordRequest changePasswordRequest);
    }
}