package com.vn.ntsc.ui.forgotpassword;

import com.vn.ntsc.core.callback.CallbackListener;
import com.vn.ntsc.core.callback.PresenterListener;
import com.vn.ntsc.repository.model.forgotpassword.ForgotPasswordRequest;

/**
 * Created by dev22 on 8/21/17.
 */
interface ForgotPasswordContract {
    interface View extends CallbackListener {
        /**
         * when forgot password success
         */
        void showSuccessAlert();

        /**
         * when not found email on server
         */
        void showEmailNotFound();

        void showLoadingDialog();

        void hideLoadingDialog();
    }

    interface Presenter extends PresenterListener<View> {
        /**
         * call forgot password request
         *
         * @param passwordRequest param to request
         */
        void onForgotPassword(ForgotPasswordRequest passwordRequest);
    }
}