package com.vn.ntsc.services.fcm;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.vn.ntsc.repository.model.notification.push.NotificationAps;
import com.vn.ntsc.repository.model.notification.push.NotificationMessage;
import com.vn.ntsc.repository.model.notification.push.NotificationType;
import com.vn.ntsc.repository.preferece.NotificationSettingPreference;
import com.vn.ntsc.repository.preferece.Preferences;
import com.vn.ntsc.repository.preferece.UserPreferences;
import com.vn.ntsc.utils.ApplicationNotificationManager;
import com.vn.ntsc.utils.LogUtils;
import com.vn.ntsc.utils.SystemUtils;
import com.vn.ntsc.utils.Utils;

import java.util.Map;

/**
 * Created by nankai on 12/26/2016.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();

    public static final String ACTION_FCM_RECEIVE_MESSAGE = "com.vn.ntsc.services.fcm.receive";
    public static final String EXTRA_NOTIFICATION_MESSAGE = "com.vn.ntsc.services.fcm.message";
    public static final String EXTRA_NOTIFICATION_TYPE_NOTY_FCM = "com.vn.ntsc.services.fcm.receive.type";

    Handler handler;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        handler = new Handler(getMainLooper());

        Map<String, String> mMapData = remoteMessage.getData();
        String notifyJson = Utils.dumpMapData(mMapData);
        if (!notifyJson.equals("")) {
            LogUtils.i(TAG, "Notify json: " + notifyJson);
            Gson gson = new Gson();
            NotificationMessage notification = gson.fromJson(notifyJson, NotificationMessage.class);
            //Show notification
            showNotification(notification);
        } else {
            LogUtils.w(TAG, "Notify json is empty!");
        }
    }

    /**
     * extract data from GCM and determine show notification in app or status
     * bar
     *
     * @param notifyMessage {@link NotificationMessage}
     */
    private void showNotification(NotificationMessage notifyMessage) {
        if (notifyMessage == null) {
            LogUtils.w(TAG, "Warning notificationMessage is null");
            return;
        }
        Gson gson = new Gson();
        LogUtils.i(TAG, gson.toJson(notifyMessage));

        if (notifyMessage.value == null) {
            LogUtils.w(TAG, "Warning notificationMessage value is null");
            return;
        }

        final NotificationAps aps = notifyMessage.value.aps;

        if (aps == null) {
            LogUtils.w(TAG, "Warning notificationMessage aps is null");
            return;
        }

        UserPreferences userPreferences = UserPreferences.getInstance();
        userPreferences.saveNumberUnreadMessage(aps.totalNotifyChat);
        userPreferences.saveNotifyNum(aps.totalNotify);
        userPreferences.saveNotifyOnlineAlarmNum(aps.totalNotifyOnline);

        String userIdLogin = userPreferences.getUserId();
        //Don't show notification if user not login
        if (TextUtils.isEmpty(userIdLogin)) return;
        if (!userIdLogin.equalsIgnoreCase(aps.data.userid) && userIdLogin.equals(aps.data.ownerId)) {

            // Check image approve
            int type = aps.data.notiType;
            // Only increase notification when message not chat text.
            if (ApplicationNotificationManager.isOnlineAlertNotification(aps)) {
                // update point
                int totalPoint = userPreferences.getNumberPoint();
                int priceAlert = Preferences.getInstance().getOnlineAlertPoints();
                int remainPoint = totalPoint - priceAlert;
                userPreferences.saveNumberPoint(remainPoint);
            }

            if (type == NotificationType.NOTI_FAVORITED_UNLOCK)
                if (!NotificationSettingPreference.getInstance().isNotificationFav())
                    return;

            String lockKey = aps.alert.lockey;
            // Because the notification from free page does not return lockKey.
            if (TextUtils.isEmpty(lockKey) && type != NotificationType.NOTI_FROM_FREE_PAGE && type != NotificationType.NOTI_NEWS_BUZZ && type != NotificationType.NOTI_QA_BUZZ) {
                return;
            }

            if (SystemUtils.isAppIsInForeground(getApplicationContext())) {
                LocalBroadcast.sendBroadcast(getApplicationContext(), aps);
            } else { // Only push notification when app invisible
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        new ApplicationNotificationManager(getApplicationContext()).showNotification(aps);
                    }
                });
            }
        }
    }

    /**
     * Helper to register for and send broadcasts of Intents to local objects within your process.
     * This has a number of advantages over sending local broadcasts with sendBroadcast(Intent, NotificationAps)
     */
    public static class LocalBroadcast {
        public static void sendBroadcast(Context context, NotificationAps aps) {
            if (aps == null) {
                LogUtils.w(TAG, "Can't show notification because NotificationAps is null");
                return;
            }

            UserPreferences.getInstance().saveNumberUnreadMessage(aps.totalNotifyChat);
            UserPreferences.getInstance().saveNotifyNum(aps.totalNotify);

            Intent intent = new Intent(ACTION_FCM_RECEIVE_MESSAGE);
            intent.putExtra(EXTRA_NOTIFICATION_MESSAGE, aps);
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            Utils.clearHandler(handler);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}