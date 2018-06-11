package com.vn.ntsc.ui.accountsetting;

import com.vn.ntsc.core.callback.CallbackListener;
import com.vn.ntsc.core.callback.PresenterListener;
import com.vn.ntsc.repository.model.accountsetting.ChangeAccFacebookRequest;
import com.vn.ntsc.repository.model.accountsetting.ChangeEmailRequest;
import com.vn.ntsc.repository.model.accountsetting.ChangePasswordRequest;

/**
 * Created by ThoNh on 10/6/2017.
 */

public interface AccountSettingContracts {

    interface View extends CallbackListener {

        void emailEmpty();

        void currentPasswordIncorrect();

        void currentPasswordEmpty();

        void newEmailInvalid();

        void newPasswordNotMatch();

        void newPasswordNotValid();

        void changeEmailSuccess();

        void changePasswordSuccess();

        void changeFailure();

        void complete();

        void emailAlreadyExist();

        void oldPasswordIncorrect();

        void showLoadingDialog();

        void hideLoadingDialog();

        void changeEmailOrChangePassword();

        void requestChangeEmail();

        void requestChangePassword();

        void changeAccFacebookSuccess();

    }


    interface Presenter extends PresenterListener<View> {

        void changeEmail(ChangeEmailRequest request);

        void changePassword(ChangePasswordRequest request);

        void changeAccFacebook(ChangeAccFacebookRequest request);
    }
}
