package com.vn.ntsc.repository.model.notification.push;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by nankai on 3/28/2018.
 */

public class NotificationData implements Parcelable {

    private final static String TAG_NOTI_TYPE = "noti_type";
    private final static String TAG_AVATAR_URL = "ava_url";
    private final static String TAG_GENDER = "gender";
    private final static String TAG_STREAM_ID = "stream_id";
    private final static String TAG_USER_NAME = "user_name";
    private final static String TAG_BUZZ = "text_buzz";
    private final static String TAG_USER_ID = "userid";
    private final static String TAG_BUZZ_ID = "buzzid";
    private final static String TAG_COMMENT_ID = "cmt_id";
    private final static String TAG_SUB_COMMENT_ID = "sub_cmt_id";
    private final static String TAG_BUZZ_CHILD_ID = "buzz_child_id";
    private final static String TAG_OWNER_ID = "ownerid";
    private final static String TAG_THUM_URL = "thumnail_url";

    @SerializedName(TAG_NOTI_TYPE)
    @NotificationType
    public int notiType;
    @SerializedName(TAG_USER_ID)
    public String userid;
    @SerializedName(TAG_BUZZ_ID)
    public String buzzid;
    @SerializedName(TAG_COMMENT_ID)
    public String cmtId;
    @SerializedName(TAG_SUB_COMMENT_ID)
    public String subCmtId;
    @SerializedName(TAG_BUZZ_CHILD_ID)
    public String buzzChildId;
    @SerializedName(TAG_OWNER_ID)
    public String ownerId;
    @SerializedName(TAG_AVATAR_URL)
    public String avatarUrl;
    @SerializedName(TAG_BUZZ)
    public String buzz;
    @SerializedName(TAG_THUM_URL)
    public String thumnailUrl;

    @SerializedName(TAG_GENDER)
    public int gender;
    @SerializedName(TAG_USER_NAME)
    public String userName;
    @SerializedName(TAG_STREAM_ID)
    public String streamId;

    public String message = "";

    public NotificationData() {
    }

    protected NotificationData(Parcel in) {
        notiType = in.readInt();
        userid = in.readString();
        buzzid = in.readString();
        cmtId = in.readString();
        subCmtId = in.readString();
        buzzChildId = in.readString();
        ownerId = in.readString();
        avatarUrl = in.readString();
        buzz = in.readString();
        thumnailUrl = in.readString();
        gender = in.readInt();
        userName = in.readString();
        streamId = in.readString();
        message = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(notiType);
        dest.writeString(userid);
        dest.writeString(buzzid);
        dest.writeString(cmtId);
        dest.writeString(subCmtId);
        dest.writeString(buzzChildId);
        dest.writeString(ownerId);
        dest.writeString(avatarUrl);
        dest.writeString(buzz);
        dest.writeString(thumnailUrl);
        dest.writeInt(gender);
        dest.writeString(userName);
        dest.writeString(streamId);
        dest.writeString(message);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<NotificationData> CREATOR = new Creator<NotificationData>() {
        @Override
        public NotificationData createFromParcel(Parcel in) {
            return new NotificationData(in);
        }

        @Override
        public NotificationData[] newArray(int size) {
            return new NotificationData[size];
        }
    };
}
