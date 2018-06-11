package com.vn.ntsc.ui.notices.notification;

import android.view.View;

import com.vn.ntsc.core.BasePresenter;
import com.vn.ntsc.core.callback.SubscriberCallback;
import com.vn.ntsc.core.model.ServerResponse;
import com.vn.ntsc.repository.model.notification.ClickNotificationRequest;
import com.vn.ntsc.repository.model.notification.ClickNotificationResponse;
import com.vn.ntsc.repository.model.notification.DelNotificationRequest;
import com.vn.ntsc.repository.model.notification.DelNotificationResponse;
import com.vn.ntsc.repository.model.notification.NotificationRequest;
import com.vn.ntsc.repository.model.notification.NotificationResponse;
import com.vn.ntsc.repository.model.timeline.BuzzDetailRequest;
import com.vn.ntsc.repository.model.timeline.BuzzDetailResponse;
import com.vn.ntsc.repository.model.user.GetUserInfoResponse;
import com.vn.ntsc.repository.model.user.UserInfoRequest;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;


/**
 * Created by ThoNh on 8/30/2017.
 */

public class NotificationPresenter extends BasePresenter<NotificationContract.View> implements NotificationContract.Presenter {

    @Inject
    public NotificationPresenter() {

    }

    @Override
    public void getNotifications(final NotificationRequest request, final int requestType) {
        addSubscriber(apiService.getNotifications(request)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new SubscriberCallback<NotificationResponse>(view) {
                    @Override
                    public void onSuccess(NotificationResponse response) {
                        if (response.code == ServerResponse.DefinitionCode.SERVER_SUCCESS) {
                            view.getNotificationSuccess(response, requestType);
                        }
                    }

                    @Override
                    public void onCompleted() {
                        view.onCompleted();
                    }
                }));
    }

    @Override
    public void markReadNotification(ClickNotificationRequest request, final int position) {
        addSubscriber(apiService.markReadNotification(request)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new SubscriberCallback<ClickNotificationResponse>(view) {
                    @Override
                    public void onSuccess(ClickNotificationResponse response) {
                        if (response.code == ServerResponse.DefinitionCode.SERVER_SUCCESS) {
                            view.markReadNotificationSuccess(position);
                        }
                    }

                    @Override
                    public void onCompleted() {
                        view.onCompleted();
                    }
                }));
    }

    @Override
    public void delNotification(DelNotificationRequest request, final int position) {
        addSubscriber(apiService.delNotification(request)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new SubscriberCallback<DelNotificationResponse>(view) {
                    @Override
                    public void onSuccess(DelNotificationResponse response) {
                        if (response.code == ServerResponse.DefinitionCode.SERVER_SUCCESS) {
                            view.delNotificationSuccess(position);
                        } else {
                            view.delNotificationFailure();
                        }
                    }

                    @Override
                    public void onCompleted() {
                        view.onCompleted();
                    }
                }));
    }


    /*Check Buzz is deleted, if isDeleted --> response = null */
    @Override
    public void requestFriendsInfo(UserInfoRequest buzzRequest, final View view1, final int position) {
        addSubscriber(apiService.getUserInfo(buzzRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new SubscriberCallback<GetUserInfoResponse>(view) {
                    @Override
                    public void onSuccess(GetUserInfoResponse response) {
                        view.getFriendsInfoSuccess(response.data, view1, position);
                    }

                    @Override
                    public void onCompleted() {

                    }
                }));
    }


    /*Noti chat --> kiểm tra xe buzz này đã xóa chưa*/
    @Override
    public void getBuzzFromNotificationId(BuzzDetailRequest request, final int position) {
        addSubscriber(apiService.getBuzzListDetail(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .filter(new Predicate<BuzzDetailResponse>() {
                    @Override
                    public boolean test(@NonNull BuzzDetailResponse response) throws Exception {
                        if (response.code == ServerResponse.DefinitionCode.SERVER_BUZZ_NOT_FOUND) {
                            view.onBuzzNotFoundFromNotificationId(position);
                            return false;
                        }
                        return true;
                    }
                })
                .subscribeWith(new SubscriberCallback<BuzzDetailResponse>(view) {
                    @Override
                    public void onSuccess(BuzzDetailResponse response) {
                        view.onBuzzDetailFromNotificationId(response);
                    }

                    @Override
                    public void onCompleted() {
                        view.onCompleted();
                    }
                }));
    }

}
