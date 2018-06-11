package com.vn.ntsc.ui.notices.online;

import com.vn.ntsc.core.callback.CallbackListener;
import com.vn.ntsc.core.callback.PresenterListener;
import com.vn.ntsc.repository.model.notification.OnlineNotificationRequest;
import com.vn.ntsc.repository.model.notification.OnlineNotificationResponse;

/**
 * Created by ThoNh on 9/1/2017.
 */

public interface NotificationOnlineContract {

    interface View extends CallbackListener {
        void getOnlineNotificationSuccess(OnlineNotificationResponse response);

        void getOnlineNotificationFailure();

        void loadComplete();
    }

    interface Presenter extends PresenterListener<View> {
        void getOnlineNotifications(OnlineNotificationRequest request);
    }
}
