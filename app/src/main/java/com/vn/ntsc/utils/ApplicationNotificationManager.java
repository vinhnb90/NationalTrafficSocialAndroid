package com.vn.ntsc.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import com.bumptech.glide.request.target.NotificationTarget;
import com.vn.ntsc.R;
import com.vn.ntsc.repository.ActivityResultRequestCode;
import com.vn.ntsc.repository.model.notification.push.NotificationAps;
import com.vn.ntsc.repository.model.notification.push.NotificationType;
import com.vn.ntsc.repository.preferece.NotificationSettingPreference;
import com.vn.ntsc.services.fcm.MyFirebaseMessagingService;
import com.vn.ntsc.ui.main.MainActivity;
import com.vn.ntsc.utils.notify.NotificationUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import me.leolin.shortcutbadger.ShortcutBadger;

/**
 * Created by nankai on 9/11/2017.
 */

public class ApplicationNotificationManager {

    private static final String TAG = ApplicationNotificationManager.class.getSimpleName();

    private Context context;
    private NotificationManager mNotificationManager;

    public ApplicationNotificationManager(Context context) {
        this.context = context;
        mNotificationManager = (NotificationManager) this.context
                .getSystemService(Context.NOTIFICATION_SERVICE);
    }

    /**
     * Issues a notification to inform the user that server has sent a message.
     */
    public void showNotification(final NotificationAps notifyMessage) {
        String message = getMessageNotification(context, notifyMessage);
        String title;

        if (notifyMessage.data.notiType == NotificationType.NOTI_SHARE_LIVE_STREAM) {
            title = context.getString(R.string.live_stream);
        } else {
            title = context.getString(R.string.new_notification) + " " + notifyMessage.alert.logArgs[0];
        }
        onShowNotification(notifyMessage, message, title);
    }

    public void onShowNotification(final NotificationAps notifyMessage, String message, String title) {

        final int notificationId = getPushId();
        final String channelId = "notify_" + notificationId;

        // Intent for start activity and send data to activity
        final Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(MyFirebaseMessagingService.EXTRA_NOTIFICATION_MESSAGE, notifyMessage);
        intent.putExtra(MyFirebaseMessagingService.EXTRA_NOTIFICATION_TYPE_NOTY_FCM, notifyMessage.data.notiType);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK); // set intent so it does not start a new activity
        PendingIntent pendingIntent = PendingIntent.getActivity(context, ActivityResultRequestCode.NOTIFICATION, intent, PendingIntent.FLAG_UPDATE_CURRENT);


        // remote view (layout custom for notification)
        final RemoteViews remoteViews = new RemoteViews(context.getApplicationContext().getPackageName(), R.layout.layout_push_notification);
        remoteViews.setTextViewText(R.id.notification_title, title);
        remoteViews.setTextViewText(R.id.notification_content, message);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        String currentDateAndTime = sdf.format(new Date());
        remoteViews.setTextViewText(R.id.notification_time, currentDateAndTime);

        // Notification Builder
        final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context.getApplicationContext(), channelId);

        mBuilder.setSmallIcon(R.mipmap.ic_launcher_round)
                .setOngoing(false)
                .setUsesChronometer(false)
                .setAutoCancel(true)
                .setContentTitle(title)
                .setContentText(message)
                .setContent(remoteViews)
                .setPriority(Notification.PRIORITY_MAX)
                .setContentIntent(pendingIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBuilder.setColor(Color.TRANSPARENT);
        }

        // config sound for notification
        if (NotificationSettingPreference.getInstance().isSound())
            mBuilder.setSound(Uri.parse("android.resource://" + context.getPackageName() + File.separator + R.raw.notice));
        if (NotificationSettingPreference.getInstance().isVibrate()) {
            // config vibrate for notification
            NotificationUtils.vibarateNotification(context);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    context.getResources().getString(R.string.app_name),
                    NotificationManager.IMPORTANCE_DEFAULT);
            mNotificationManager.createNotificationChannel(channel);
        }

        final Notification notification = mBuilder.build();
        notification.bigContentView = remoteViews;
        notification.flags |= Notification.FLAG_AUTO_CANCEL;


        final NotificationTarget target = new NotificationTarget(context, R.id.notification_image, remoteViews, notification, notificationId);
        ImagesUtils.loadRoundedAvatar(context.getApplicationContext(), notifyMessage.data.avatarUrl, notifyMessage.data.gender, target);

//        final NotificationTarget notificationTarget = new NotificationTarget(context, R.id.notification_app_icon, remoteViews, notification, notificationId);
//        ImagesUtils.loadRoundedAvatar(context.getApplicationContext(), UserPreferences.getInstance().getAva(), notificationTarget);

        mNotificationManager.notify(notificationId, notification);

        ShortcutBadger.applyCount(context.getApplicationContext(), notifyMessage.badge); //for 1.1.4+
    }

    public static String getMessageNotification(Context context, NotificationAps notifyMessage) {
        String[] logList = notifyMessage.alert.logArgs;
        String arg1 = "";

        // Get the first argument
        if (logList != null && logList.length > 0) {
            arg1 = logList[0];
        }

        String message = null;

        // get resource type
        int notifyType = notifyMessage.data.notiType;
        switch (notifyType) {
            case NotificationType.NOTI_REPLY_YOUR_COMMENT:
                message = String.format(context.getString(R.string.reply_comment_responded_to_notification), notifyMessage.alert.logArgs[0]);
                break;
            case NotificationType.NOTI_COMMENT_BUZZ:
                message = String.format(context.getString(R.string.buzz_responded_to_notification), notifyMessage.alert.logArgs[0]);
                break;
            case NotificationType.NOTI_LIKE_BUZZ:
                message = String.format(context.getString(R.string.buzz_like_to_notification), notifyMessage.alert.logArgs[0]);
                break;
            case NotificationType.NOTI_NEWS_BUZZ:
                message = notifyMessage.data.buzz;
                break;
            case NotificationType.NOTI_QA_BUZZ:
                message = notifyMessage.data.buzz;
                break;
            case NotificationType.NOTI_FAVORITED_CREATE_BUZZ:
                message = String
                        .format(context
                                        .getString(R.string.buzz_created_notification),
                                arg1.length() > Constants.NOTIFICATION_MAX_LENGTH_NAME ? arg1
                                        .substring(
                                                0,
                                                Constants.NOTIFICATION_MAX_LENGTH_NAME)
                                        + "..."
                                        : arg1);
                break;
            case NotificationType.NOTI_CHAT:
                if (Utils.isEmptyOrNull(arg1)) {
                    message = context.getString(R.string.noti_new_chat_msg_text_from_friend);
                } else {
                    message = String.format(context.getString(R.string.noti_new_chat_msg_text), notifyMessage.alert.logArgs[0]);
                }
                break;
            case NotificationType.NOTI_ONLINE_ALERT:
                message = String
                        .format(context
                                        .getString(R.string.came_online_notification),
                                arg1.length() > Constants.NOTIFICATION_MAX_LENGTH_NAME ? arg1
                                        .substring(
                                                0,
                                                Constants.NOTIFICATION_MAX_LENGTH_NAME)
                                        + "..."
                                        : arg1);
                break;
            case NotificationType.NOTI_DAYLY_BONUS:
                message = context.getString(R.string.earned_point_notification);
                break;
            case NotificationType.NOTI_APPROVED_BUZZ:
            case NotificationType.NOTI_BACKSTAGE_APPROVED:
                message = context.getString(R.string.image_approved);
                break;
            case NotificationType.NOTI_FROM_FREE_PAGE:
                message = notifyMessage.data.buzz;
                break;
            case NotificationType.NOTI_DENIED_BUZZ_IMAGE:
                message = context.getString(R.string.denied_image_buzz);
                break;
            case NotificationType.NOTI_DENIED_BACKSTAGE:
                message = context.getString(R.string.denied_backstage);
                break;
            case NotificationType.NOTI_APPROVE_BUZZ_TEXT:
                message = context.getString(R.string.approve_text_buzz);
                break;
            case NotificationType.NOTI_APPROVE_COMMENT:
                message = context.getString(R.string.approve_comment);
                break;
            case NotificationType.NOTI_APPROVE_SUB_COMMENT:
                message = context.getString(R.string.approve_sub_comment);
                break;
            case NotificationType.NOTI_DENI_SUB_COMMENT:
                message = context.getString(R.string.denied_sub_comment);
                break;
            case NotificationType.NOTI_DENIED_BUZZ_TEXT:
                message = context.getString(R.string.denied_text_buzz);
                break;
            case NotificationType.NOTI_DENIED_COMMENT:
                message = context.getString(R.string.denied_comment);
                break;
            case NotificationType.NOTI_APPROVE_USERINFO:
                message = context.getString(R.string.approve_user_info);
                break;
            case NotificationType.NOTI_APART_OF_USERINFO:
                message = context.getString(R.string.apart_of_user_info);
                break;
            case NotificationType.NOTI_DENIED_USERINFO:
                message = context.getString(R.string.denied_user_info);
                break;
            case NotificationType.NOTI_TAG_BUZZ:
                // logArgs may null
                message = context.getString(R.string.noti_tag_buzz, notifyMessage.data.userName);
                break;
            case NotificationType.NOTI_AUDIO_SHARE_BUZZ:
                message = context.getString(R.string.timeline_audio_share, notifyMessage.alert.logArgs[0]);
                break;
            case NotificationType.NOTI_SHARE_LIVE_STREAM:
                message = context.getResources().getString(R.string.noti_live_stream);
                break;
            case NotificationType.NOTI_LIVESTREAM_FROM_FAVOURIST:
                message = String.format(context.getResources().getString(R.string.noti_livestream_from_favourist), notifyMessage.data.userName);
                break;
            case NotificationType.NOTI_TAG_LIVESTREAM_FROM_FAVOURIST:
                message = String.format(context.getResources().getString(R.string.noti_tag_livestream_from_favourist), notifyMessage.data.userName);
                break;
            default:
                break;
        }
        return message;
    }

    public static int getIconNotification(NotificationAps notifyMessage) {
        int iconRes;
        switch (notifyMessage.data.notiType) {
            case NotificationType.NOTI_CHAT:
            case NotificationType.NOTI_REPLY_YOUR_COMMENT:
            case NotificationType.NOTI_FAVORITED_CREATE_BUZZ:
            case NotificationType.NOTI_COMMENT_BUZZ:
                iconRes = R.drawable.ic_notifi_message;
                break;
            default:
                iconRes = R.drawable.ic_notifi_others;
                break;
        }
        return iconRes;
    }

    public static boolean isOnlineAlertNotification(NotificationAps message) {
        return message.data.notiType == NotificationType.NOTI_ONLINE_ALERT;
    }

    private int getPushId() {
        return new Random().nextInt(99999);
    }
}