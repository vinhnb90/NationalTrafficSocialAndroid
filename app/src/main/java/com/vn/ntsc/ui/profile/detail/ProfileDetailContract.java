package com.vn.ntsc.ui.profile.detail;

import com.vn.ntsc.core.callback.CallbackListener;
import com.vn.ntsc.core.callback.PresenterListener;
import com.vn.ntsc.repository.model.user.GetUserInfoResponse;

interface ProfileDetailContract {
    interface View extends CallbackListener {
        /**
         * when receive user info {@link Presenter#getUserInfo(String, String)}
         *
         * @param response object to set user info
         */
        void update(GetUserInfoResponse response);

        public void showLoadingDialog();

        public void hideLoadingDialog();
    }

    interface Presenter extends PresenterListener<View> {
        void getUserInfo(String useserId, String token);
    }
}
