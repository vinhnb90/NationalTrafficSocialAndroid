package com.vn.ntsc.repository.model.notification.push;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.tux.socket.models.Message;

/**
 * Created by nankai on 9/11/2017.
 */

public class NotificationAps implements Parcelable {

    public final static String TAG_ALERT = "alert";
    public final static String TAG_DATA = "data";
    public final static String TAG_SOUND = "sound";
    public final static String TAG_BADGE = "badge";
    public final static String TAG_NOTI = "total_noti";
    public final static String TAG_NOTI_CHAT = "total_noti_chat";
    public final static String TAG_NOTI_ONLINE = "total_noti_online";

    @SerializedName(TAG_BADGE)
    public int badge;
    @SerializedName(TAG_DATA)
    public NotificationData data;
    @SerializedName(TAG_ALERT)
    public NotificationAlert alert;
    @SerializedName(TAG_SOUND)
    public String sound;
    @SerializedName(TAG_NOTI)
    public int totalNotify;
    @SerializedName(TAG_NOTI_CHAT)
    public int totalNotifyChat;
    @SerializedName(TAG_NOTI_ONLINE)
    public int totalNotifyOnline;
    public NotificationAps() {
    }

    public static NotificationAps newInstance(Message message) {
        NotificationAps notificationAps = new NotificationAps();

        NotificationData notificationData = new NotificationData();
        NotificationAlert notificationAlert = new NotificationAlert();

        notificationData.userid = message.getSenderId() != null ? message.getSenderId() : "";
        notificationData.buzz = message.getValue() != null ? message.getValue() : "";
        notificationData.message = message.getValue() != null ? message.getValue() : "";
        notificationAlert.logArgs = new String[]{"", ""};
        notificationData.notiType = NotificationType.NOTI_CHAT;

        notificationAps.data = notificationData;
        notificationAps.alert = notificationAlert;

        return notificationAps;
    }

    protected NotificationAps(Parcel in) {
        badge = in.readInt();
        totalNotify = in.readInt();
        totalNotifyChat = in.readInt();
        totalNotifyOnline = in.readInt();
        data = in.readParcelable(NotificationData.class.getClassLoader());
        alert = in.readParcelable(NotificationAlert.class.getClassLoader());
        sound = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(badge);
        dest.writeInt(totalNotify);
        dest.writeInt(totalNotifyChat);
        dest.writeInt(totalNotifyOnline);
        dest.writeParcelable(data, flags);
        dest.writeParcelable(alert, flags);
        dest.writeString(sound);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<NotificationAps> CREATOR = new Creator<NotificationAps>() {
        @Override
        public NotificationAps createFromParcel(Parcel in) {
            return new NotificationAps(in);
        }

        @Override
        public NotificationAps[] newArray(int size) {
            return new NotificationAps[size];
        }
    };
}
