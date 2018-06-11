package com.vn.ntsc.repository;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by nankai on 8/24/2017.
 * class dùng để quản lý các Activity Result Request Code
 * Khởi tạo các request code code startActivityForResult ở đây
 * sử dụng enum annotation để thực hiện
 */

@IntDef({
        ActivityResultRequestCode.REQUEST_BUZZ_DETAILS,
        ActivityResultRequestCode.REQUEST_SUB_COMMENT,
        ActivityResultRequestCode.REQUEST_BUZZ_PROFILE,
        ActivityResultRequestCode.REQUEST_POST_STATUS,
        ActivityResultRequestCode.REQUEST_GALLERY_CODE,
        ActivityResultRequestCode.READ_REQUEST_CODE,
        ActivityResultRequestCode.LIVE_STREAM_REQUEST_CODE,
        ActivityResultRequestCode.REQUEST_CODE_CHOOSE_AREA,
        ActivityResultRequestCode.REQUEST_CODE_SEARCH_BY_NAME,
        ActivityResultRequestCode.REQUEST_CODE_SEARCH,
        ActivityResultRequestCode.REQUEST_TAKE_PHOTO,
        ActivityResultRequestCode.REQUEST_RECORD_VIDEO,
        ActivityResultRequestCode.REQUEST_ADD_TAG_FRIEND,
        ActivityResultRequestCode.REQUEST_CODE_FINISH,
        ActivityResultRequestCode.REQUEST_CODE_EDIT_PROFILE,
        ActivityResultRequestCode.REQUEST_POST_STATUS_PRIVACY,

        ActivityResultRequestCode.REQUEST_GET_GIFT,

        ActivityResultRequestCode.REQUEST_EDIT_MESSAGE,
        ActivityResultRequestCode.REQUEST_CHOOSE_AREAS,
        ActivityResultRequestCode.REQUEST_ADD_TAG_FRIEND_FOR_LIVE_STREAM,
        ActivityResultRequestCode.REQUEST_PRIVACY_FOR_LIVE_STREAM,
        ActivityResultRequestCode.REQUEST_PRIVACY_ALBUM,
        ActivityResultRequestCode.REQUEST_PRIVACY_CREATE_ALBUM,
        ActivityResultRequestCode.REQUEST_PRIVACY_EDIT_ALBUM,
        ActivityResultRequestCode.FACEBOOK_SHARE,
        ActivityResultRequestCode.NOTIFICATION_POST_STATUS,
        ActivityResultRequestCode.NOTIFICATION,
        ActivityResultRequestCode.REQUEST_START_SEND_GIFT_FROM_CHAT,
        ActivityResultRequestCode.REQUEST_START_SEND_GIFT_FROM_PROFILE,
        ActivityResultRequestCode.REQUEST_ADD_TAG_FRIEND_FOR_SHARE
})

@Retention(RetentionPolicy.SOURCE)
public @interface ActivityResultRequestCode {
    //Buzz detail
    int REQUEST_BUZZ_DETAILS                        =   1000;
    //Buzz SubComment
    int REQUEST_SUB_COMMENT                         =   1001;
    //Buzz profile
    int REQUEST_BUZZ_PROFILE                        =   1003;
    int REQUEST_POST_STATUS                         =   1004;
    int REQUEST_GALLERY_CODE                        =   1005;
    int READ_REQUEST_CODE                           =   1006;
    int LIVE_STREAM_REQUEST_CODE                    =   1007;
    // SearchSettingFragment
    int REQUEST_CODE_SEARCH                         =   1008; // from MainActivity
    int REQUEST_CODE_CHOOSE_AREA                    =   1009;
    int REQUEST_CODE_SEARCH_BY_NAME                 =   1010;
    //Post status with take media file request code
    int REQUEST_TAKE_PHOTO                          =   1011;
    int REQUEST_RECORD_VIDEO                        =   1012;
    //Post status tag friend
    int REQUEST_ADD_TAG_FRIEND                      =   1013;
    //online alert
    int REQUEST_MANAGE_ONLINE_ALERT                 =   1014;
    //finish activity
    int REQUEST_CODE_FINISH                         =   1015;
    // edit profile
    int REQUEST_CODE_EDIT_PROFILE                   =   1016;
    // Gift
    int REQUEST_GET_GIFT                            =   1017;
    int REQUEST_POST_STATUS_PRIVACY                 =   1018;
    int REQUEST_EDIT_MESSAGE                        =   1019;
    int REQUEST_CHOOSE_AREAS                        =   1020;
    int REQUEST_PRIVACY_ALBUM                       =   1021;
    int REQUEST_PRIVACY_CREATE_ALBUM                =   1022;
    int REQUEST_PRIVACY_EDIT_ALBUM                  =   1023;
    //Live stream tag friend
    int REQUEST_ADD_TAG_FRIEND_FOR_LIVE_STREAM      =   1024;
    //Live stream choose privacy
    int REQUEST_PRIVACY_FOR_LIVE_STREAM             =   1025;
    int FACEBOOK_SHARE                              =   1026;
    int NOTIFICATION_POST_STATUS                    =   1027;
    int NOTIFICATION                                =   1028;
    // Gift
    int REQUEST_START_SEND_GIFT_FROM_CHAT           =   1029;//Use in ChatActivity
    int REQUEST_START_SEND_GIFT_FROM_PROFILE        =   1030;//Use in MyProfileActivity
    //tag friend media share
    int REQUEST_ADD_TAG_FRIEND_FOR_SHARE = 9119;
}
