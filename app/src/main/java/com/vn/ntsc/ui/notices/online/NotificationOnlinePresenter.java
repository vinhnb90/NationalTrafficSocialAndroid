package com.vn.ntsc.ui.notices.online;

import com.vn.ntsc.core.BasePresenter;
import com.vn.ntsc.core.callback.SubscriberCallback;
import com.vn.ntsc.core.model.ServerResponse;
import com.vn.ntsc.repository.model.notification.OnlineNotificationRequest;
import com.vn.ntsc.repository.model.notification.OnlineNotificationResponse;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by ThoNh on 9/1/2017.
 */

public class NotificationOnlinePresenter extends BasePresenter<NotificationOnlineContract.View> implements NotificationOnlineContract.Presenter {

    @Inject
    public NotificationOnlinePresenter() {

    }

    @Override
    public void getOnlineNotifications(final OnlineNotificationRequest request) {
        addSubscriber(apiService.getOnlineNotifications(request)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new SubscriberCallback<OnlineNotificationResponse>(view) {
                    @Override
                    public void onSuccess(OnlineNotificationResponse response) {
                        if (response.code == ServerResponse.DefinitionCode.SERVER_SUCCESS) {
                            view.getOnlineNotificationSuccess(response);
                        } else {
                            view.getOnlineNotificationFailure();
                        }
                    }

                    @Override
                    public void onCompleted() {
                        view.loadComplete();
                    }
                }));
    }
}
