package com.vn.ntsc.ui.notices.notification;

import com.vn.ntsc.core.callback.CallbackListener;
import com.vn.ntsc.core.callback.PresenterListener;
import com.vn.ntsc.repository.model.notification.ClickNotificationRequest;
import com.vn.ntsc.repository.model.notification.DelNotificationRequest;
import com.vn.ntsc.repository.model.notification.NotificationRequest;
import com.vn.ntsc.repository.model.notification.NotificationResponse;
import com.vn.ntsc.repository.model.timeline.BuzzDetailRequest;
import com.vn.ntsc.repository.model.timeline.BuzzDetailResponse;
import com.vn.ntsc.repository.model.user.UserInfoRequest;
import com.vn.ntsc.repository.model.user.UserInfoResponse;

/**
 * Created by ThoNh on 8/30/2017.
 */

public interface NotificationContract {

    interface View extends CallbackListener {

        void getNotificationSuccess(NotificationResponse response, int requestType);

        void getNotificationFailure();

        void markReadNotificationSuccess(int position);

        void delNotificationSuccess(int position);

        void delNotificationFailure();

        void getFriendsInfoSuccess(UserInfoResponse friendsInfo, android.view.View view, int position);

        void onBuzzDetailFromNotificationId(BuzzDetailResponse response);

        void onCompleted();

        void onBuzzNotFoundFromNotificationId(int position);

        void onNonReadNotification();
    }

    interface Presenter extends PresenterListener<View> {

        void getNotifications(NotificationRequest request, int requestType);

        void markReadNotification(ClickNotificationRequest request, int position);

        void delNotification(DelNotificationRequest request, int position);

        void requestFriendsInfo(UserInfoRequest buzzRequest, android.view.View view, int position);

        void getBuzzFromNotificationId(BuzzDetailRequest request, int position);

    }

}
