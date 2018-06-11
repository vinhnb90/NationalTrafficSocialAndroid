package com.vn.ntsc.ui.main;

import com.vn.ntsc.core.callback.CallbackListener;
import com.vn.ntsc.core.callback.PresenterListener;
import com.vn.ntsc.repository.model.logout.LogoutRequest;
import com.vn.ntsc.repository.model.notification.ReadAllNotificationRequest;
import com.vn.ntsc.repository.model.user.UserInfoRequest;
import com.vn.ntsc.repository.model.user.UserInfoResponse;

/**
 * Created by nankai on 8/3/2017.
 */

public interface MainContract {

    interface View extends CallbackListener {
        void onLogout();

        void onUserInForwardProfile(UserInfoResponse userInfoResponse);

        void onReadAllNotification();
    }

    interface Presenter extends PresenterListener<MainContract.View> {

        void onLogout(LogoutRequest logoutRequest);

        void getUserInForwardProfile(UserInfoRequest userInfoRequest);

        void markReadAllNotification(ReadAllNotificationRequest request);
    }
}
