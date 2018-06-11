package com.vn.ntsc.core.views;

import android.view.View;

import com.tux.socket.models.Message;
import com.tux.socket.models.SocketEvent;
import com.vn.ntsc.repository.model.notification.push.NotificationAps;

/**
 * Created by nankai on 4/18/2018.
 * <p>
 * <p>
 * When you extends it again you will have to override 3 methods
 * getLayoutId, initInject vs onViewReady
 * {@link BaseActivity#onCreateView(View)} You initialize the views here before proceeding to {@link BaseActivity#onViewReady()}
 * {@link BaseActivity#onViewReady()} Operations related to datalogic processing are implemented in this method instead of using it on {@link BaseActivity#onCreateView(View)}}
 * {@link BaseActivity#getLayoutId()} ()} return LayoutId of the activity
 * <p>
 * When you want to listen or do not want to listen to socket events or FCM you need to change the method {@link BaseActivity#hasRegisterSocket()} and {@link BaseActivity#hasShowNotificationView()}
 */

public interface IActivity {

    /**
     * @return return LayoutId of the activity
     */
    int getLayoutId();

    /**
     * You initialize the views here before proceeding to {@link BaseActivity#onViewReady()}
     */
    void onCreateView(View rootView);

    /**
     * Operations related to datalogic processing are implemented in this method instead of using it on {@link BaseActivity#onCreateView(View)}}
     */
    void onViewReady();

    /**
     * Need to wait for viewRoot to run successfully
     *
     * @param viewRoot {@link View} current layout view
     */
    void onStart(View viewRoot);

    /**
     * Need to wait for viewRoot to run successfully
     *
     * @param viewRoot {@link View} current layout view
     */
    void onResume(View viewRoot);

    /**
     * Need to wait for viewRoot to run successfully
     *
     * @param viewRoot {@link View} current layout view
     */
    void onPostResume(View viewRoot);

    /**
     * Listen to the signal from the socket
     * You want to receive signals from socket, then override this is function
     *
     * @param socketEvent {@link SocketEvent}
     */
    void onReceiveSocket(SocketEvent socketEvent);

    /**
     * The method determines whether you want to listen to the socket events
     *
     * @return true register to listen socket, false not register to listen socket
     */
    boolean hasRegisterSocket();

    /**
     * The method determines whether you want to listen to the notification FCM events
     *
     * @return true has show notification, false not show notification
     */
    boolean hasShowNotificationView();

    /**
     * This method will receive the signals from socket notify,
     * and the received signals will automatically convert to {@link com.vn.ntsc.repository.model.notification.push.NotificationMessage} or {@link NotificationAps}
     * and move to {@link com.vn.ntsc.services.fcm.MyFirebaseMessagingService.LocalBroadcast} to act as an FCM.
     * @param message {@link Message} message from socket
     */
    void onShowNotification(Message message);

    /**
     * The FCM Broadcast Data From The Broadcast was sent to display the notification
     * @param notifyMessage {@link NotificationAps}
     */
    void onShowNotification(NotificationAps notifyMessage);

    /**
     * Show notificaiton
     * @param mNotificationAps {@link NotificationAps}
     */
    void showNotificationView(final NotificationAps mNotificationAps) throws Exception;
}
