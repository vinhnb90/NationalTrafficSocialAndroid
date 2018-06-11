package com.vn.ntsc.utils.notify;

import android.app.Notification;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;

import com.vn.ntsc.R;
import com.vn.ntsc.repository.preferece.NotificationSettingPreference;
import com.vn.ntsc.utils.Constants;

/**
 * Created by nankai on 9/1/2017.
 */

public class NotificationUtils {
    /**
     * notification for upload show notification ic_progress
     *
     * @param context  current activity, maybe app context
     * @param titleRes title of notification in resId
     * @return NotificationCompat.Builder
     */
    public static NotificationCompat.Builder createNotification(Context context, int titleRes, String channelId) {
        return new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(context.getString(titleRes))
                .setUsesChronometer(false)
                .setAutoCancel(true)
                .setPriority(Notification.PRIORITY_HIGH);
    }

    public static void playNotificationSound(Context context) {
        if (!NotificationSettingPreference.getInstance().isSound()) {
            return;
        }

        MediaPlayer mPlayer = MediaPlayer.create(context, R.raw.notice);
        try {
            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp.stop();
                    mp.reset();
                    mp.release();
                    mp = null;
                }
            });
            mPlayer.start();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    public static void vibarateNotification(Context context) {
        if (!NotificationSettingPreference.getInstance().isVibrate()) {
            return;
        }
        Vibrator vibrator = (Vibrator) context
                .getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(Constants.NOTIFICATION_VIBRATOR_TIME);
    }
}