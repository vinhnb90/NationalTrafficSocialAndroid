package com.vn.ntsc.repository.model.notification.push;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by nankai on 4/17/2018.
 */
@IntDef({NotificationType.NOTI_DEFAULT
        , NotificationType.NOTI_CHECK_OUT_UNLOCK
        , NotificationType.NOTI_FAVORITED_UNLOCK
        , NotificationType.NOTI_LIKE_BUZZ
        , NotificationType.NOTI_LIKE_OTHER_BUZZ
        , NotificationType.NOTI_COMMENT_BUZZ
        , NotificationType.NOTI_COMMENT_OTHER_BUZZ
        , NotificationType.NOTI_UNLOCK_BACKSTAGE
        , NotificationType.NOTI_FRIEND
        , NotificationType.NOTI_CHAT
        , NotificationType.NOTI_ONLINE_ALERT
        , NotificationType.NOTI_DAYLY_BONUS
        , NotificationType.NOTI_APPROVED_BUZZ
        , NotificationType.NOTI_BACKSTAGE_APPROVED
        , NotificationType.NOTI_FROM_FREE_PAGE
        , NotificationType.NOTI_FAVORITED_CREATE_BUZZ
        , NotificationType.NOTI_REPLY_YOUR_COMMENT
        , NotificationType.NOTI_DENIED_BUZZ_IMAGE
        , NotificationType.NOTI_DENIED_BACKSTAGE
        , NotificationType.NOTI_APPROVE_BUZZ_TEXT
        , NotificationType.NOTI_DENIED_BUZZ_TEXT
        , NotificationType.NOTI_APPROVE_COMMENT
        , NotificationType.NOTI_DENIED_COMMENT
        , NotificationType.NOTI_APPROVE_SUB_COMMENT
        , NotificationType.NOTI_DENI_SUB_COMMENT
        , NotificationType.NOTI_APPROVE_USERINFO
        , NotificationType.NOTI_APART_OF_USERINFO
        , NotificationType.NOTI_DENIED_USERINFO
        , NotificationType.NOTI_NEWS_BUZZ
        , NotificationType.NOTI_QA_BUZZ
        , NotificationType.NOTI_TAG_BUZZ
        , NotificationType.NOTI_AUDIO_SHARE_BUZZ
        , NotificationType.NOTI_SHARE_LIVE_STREAM
        , NotificationType.NOTI_LIVESTREAM_FROM_FAVOURIST
        , NotificationType.NOTI_TAG_LIVESTREAM_FROM_FAVOURIST
})
@Retention(RetentionPolicy.SOURCE)
public @interface NotificationType {
    int NOTI_DEFAULT = -1;
    int NOTI_CHECK_OUT_UNLOCK = 2;
    int NOTI_FAVORITED_UNLOCK = 4;
    int NOTI_LIKE_BUZZ = 5;
    int NOTI_LIKE_OTHER_BUZZ = 6;
    int NOTI_COMMENT_BUZZ = 7;
    int NOTI_COMMENT_OTHER_BUZZ = 8;
    int NOTI_UNLOCK_BACKSTAGE = 9;
    int NOTI_FRIEND = 10;
    int NOTI_CHAT = 11;
    int NOTI_ONLINE_ALERT = 12;
    int NOTI_DAYLY_BONUS = 13;
    int NOTI_APPROVED_BUZZ = 15;
    int NOTI_BACKSTAGE_APPROVED = 16;
    int NOTI_FROM_FREE_PAGE = 18;
    int NOTI_FAVORITED_CREATE_BUZZ = 19;
    int NOTI_REPLY_YOUR_COMMENT = 20;
    int NOTI_DENIED_BUZZ_IMAGE = 21;
    int NOTI_DENIED_BACKSTAGE = 22;
    int NOTI_APPROVE_BUZZ_TEXT = 24;
    int NOTI_DENIED_BUZZ_TEXT = 25;
    int NOTI_APPROVE_COMMENT = 26;
    int NOTI_DENIED_COMMENT = 27;
    int NOTI_APPROVE_SUB_COMMENT = 28;
    int NOTI_DENI_SUB_COMMENT = 29;
    int NOTI_APPROVE_USERINFO = 30;
    int NOTI_APART_OF_USERINFO = 31;
    int NOTI_DENIED_USERINFO = 32;
    int NOTI_NEWS_BUZZ = 33;
    int NOTI_QA_BUZZ = 34;
    int NOTI_TAG_BUZZ = 37;
    int NOTI_AUDIO_SHARE_BUZZ = 38;
    int NOTI_SHARE_LIVE_STREAM = 39;
    int NOTI_LIVESTREAM_FROM_FAVOURIST = 40;
    int NOTI_TAG_LIVESTREAM_FROM_FAVOURIST = 41;
}
