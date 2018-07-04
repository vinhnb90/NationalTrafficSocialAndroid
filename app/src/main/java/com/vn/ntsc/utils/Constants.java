package com.vn.ntsc.utils;

import android.os.Environment;
import android.util.Patterns;

import com.vn.ntsc.app.AppController;

import java.io.File;

/**
 * Created by nankai on 11/18/2016.
 */

public interface Constants {

    int MAX_CACHE_SIZE = 10 * 1024 * 1024;
    String CACHE_DIR_NAME = "cache_ntsc";

    int APPLICATION_API = 1;
    String FOLDER_SAVE_FILE = "Giao thông quốc gia";

    interface Dir {
        String IMAGE_CACHE_DIR = "images";
        String OUTER_DIR = "duduhuo";
        String SIMPLER_DIR = "simpler";
        String WORK_DIR = Environment.getExternalStorageDirectory()
                + File.separator + OUTER_DIR + File.separator + SIMPLER_DIR;
        String AVATAR_DIR = WORK_DIR + File.separator + "avatar";
        String CACHE_DIR = WORK_DIR + File.separator + "cache";
        String PIC_DIR = WORK_DIR + File.separator + "simpler";
    }

    // Assets Json
    String PATH_BODY_TYPE = "json/body_type.json";
    String PATH_HOBBIES = "json/hobbies.json";
    String PATH_JOBS = "json/jobs.json";
    String PATH_REGIONS = "json/regions.json";
    String PATH_SORT_ODER = "json/search_sort_order.json";
    String PATH_AVATAR = "json/search_avatar.json";
    String PATH_GENDER = "json/gender.json";

    // Default finish register flag
    int FINISH_REGISTER_YES = 1;
    int FINISH_REGISTER_NO = 0;

    // Default dialog showed
    boolean IS_SHOWED_FLAG = false;
    boolean IS_NOT_SHOWED_FLAG = true;

    // Settings
    int DISTANCE_UNIT_MILE = 0;
    int DISTANCE_UNIT_KILOMETER = 1;

    //================= PATH ====================
    String PATH_DATA = AppController.getAppContext().getCacheDir().getAbsolutePath() + File.separator + "data";
    String PATH_CACHE = PATH_DATA + "/NetCache";
    String PATH_SDCARD = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "codeest" + File.separator + "Pets";

    int GENDER_TYPE_MAN = 0;
    int GENDER_TYPE_WOMAN = 1;
    int SEARCH_SETTING_AGE_MIN_LIMIT = 14;
    int SEARCH_SETTING_AGE_MAX_LIMIT = 120;

    int BUZZ_LIST_SHOW_NUMBER_OF_PREVIEW_COMMENTS = 4;
    int BUZZ_LIKE_TYPE_UNKNOW = -1;
    int BUZZ_LIKE_TYPE_UNLIKE = 0;
    int BUZZ_LIKE_TYPE_LIKE = 1;

    String BUZZ_TYPE_FILE_VIDEO = "video";
    String BUZZ_TYPE_FILE_IMAGE = "image";
    String BUZZ_TYPE_FILE_LIVE_STREAM = "stream";
    String BUZZ_TYPE_FILE_AUDIO = "audio";

    int BUZZ_COMMENT_CAN_DELETE = 1;
    int BUZZ_COMMENT_CANNOT_DELETE = 0;
    int BUZZ_TYPE_NONE = -1;
    int BUZZ_TYPE_IS_FAVORITE = 1;
    int BUZZ_TYPE_IS_NOT_FAVORITE = 0;

    //Buzz socket
    String SOCKET_BUZZ_CMT = "BUZZCMT";
    String SOCKET_BUZZ_SUB_CMT = "BUZZSUBCMT";
    String SOCKET_BUZZ_JOIN = "BUZZJOIN";

    //Noti Socket
    String MSG_TYPE_BUZZ = "NOTIBUZZ";

    //===================== ChatMessage =====================/
    String WINK = "WINK";
    String PP = "PP";
    String AUDIO = "AUDIO";
    String VIDEO = "VIDEO";
    String PHOTO = "PHOTO";
    String FILE = "FILE";
    String GIFT = "GIFT";
    String LOCATION = "LCT";
    String STICKER = "STK";
    String STARTVIDEO = "SVIDEO";
    String ENDVIDEO = "EVIDEO";
    String STARTVOICE = "SVOICE";
    String ENDVOICE = "EVOICE";
    String CMD = "CMD";
    String TYPING = "TYPING";
    String CALLREQUEST = "CALLREQ";

    // Call request setting the same iOs
    String CALLREQUEST_VOICE = "voip_voice";
    String CALLREQUEST_VIDEO = "voip_video";

    //RegisterActivity
    String DATE_FORMAT_DISPLAY = "yyyy/MM/dd";
    String DATE_FORMAT_SEND_TO_SERVER = "yyyyMMdd";

    int MAX_LENGTH_NAME_IN_HALF_SIZE = 14;
    int AGE_VERIFICATION_DINED = -1;
    int AGE_VERIFICATION_NONE = -2;

    // request grant permission
    int KEY_GRANT_LOCATION_PERMISSION = 1;
    int KEY_GRANT_AUDIO_PERMISSION = 2;
    int KEY_GRANT_CAMERA_PERMISSION = 3;
    int KEY_GRANT_STORAGE_PERMISSION = 4;
    int KEY_GRANT_PHONE_STATE_PERMISSION = 5;

    String INTENT_FINISH_REGISTER_FLAG = "intent_finish_register_flag";
    String EXTRA_REGION_SELECTED = "region_selected";
    String INTENT_RECEIVER_EMAIL = "ReceiverEmail";
    // package point type
    int PACKAGE_DEFAULT = 9;
    int PACKAGE_POINT_CHAT = 1;
    int PACKAGE_POINT_VOICE_CALL = 2;
    int PACKAGE_POINT_VIDEO_CALL = 3;
    int PACKAGE_POINT_GIFT = 4;
    int PACKAGE_POINT_COMMENT = 5;
    int PACKAGE_POINT_SUB_COMMENT = 6;
    int PACKAGE_UNLOCK_BACKSTAGE = 7;
    int PACKAGE_SAVE_IMAGE = 8;
    // request login another system
    int LOADER_LOGIN_FACEBOOK = 0;
    int LOADER_LOGIN_MOCOM = 1;
    int LOADER_LOGIN_FAMU = 2;
    int LOADER_INSTALL_COUNT = 3;

    // NOTIFICATION
    int NOTIFICATION_MAX_LENGTH_NAME = 20;
    int NOTIFICATION_VIBRATOR_TIME = 400;
    int NOTI_STATUS_ID = 9117;
    int NOTI_STATUS = 9999;

    // Report
    int REPORT_TYPE_IMAGE = 1;
    int REPORT_TYPE_USER = 2;
    int REPORT_BUZZ = 0;
    int REPORT_VIDEO = 3;
    int REPORT_AUDIO = 4;
    int REPORT_ALBUM_IMG = 5;

    //Online alert
    int MANAGE_ONLINE_NEVER = -1;
    int MANAGE_ONLINE_EVERY_TIME = 0;
    int MANAGE_ONLINE_MAX_TEN = 10;
    int MANAGE_ONLINE_MAX_FIVE = 5;
    int MANAGE_ONLINE_ONCE_PER_DAY = 1;

    int IS_NOT_APPROVED = 0;
    int IS_APPROVED = 1;

    int PRIVACY_PUBLIC = 0;
    int PRIVACY_FRIENDS = 1;
    int PRIVACY_PRIVATE = 2;

    String LIVE_STREAM_OFF = "off";
    String LIVE_STREAM_ON = "on";

    // upload file
    int TYPE_IMAGE = 1;
    int TYPE_VIDEO = 2;
    int TYPE_AUDIO = 3;

    // auto link
    String EMAIL = "email";
    String PHONE = "phone";
    String WEBLINK = "weblink";

    String WEB_URL =
            "((?:(http|https|Http|Https|rtsp|Rtsp):\\/\\/(?:(?:[a-zA-Z0-9\\$\\-\\_\\.\\+\\!\\*\\'\\(\\)"
                    + "\\,\\;\\?\\&\\=]|(?:\\%[a-fA-F0-9]{2})){1,64}(?:\\:(?:[a-zA-Z0-9\\$\\-\\_"
                    + "\\.\\+\\!\\*\\'\\(\\)\\,\\;\\?\\&\\=]|(?:\\%[a-fA-F0-9]{2})){1,25})?\\@)?)?"
                    + "((?:(?:[" + Patterns.GOOD_IRI_CHAR + "][" + Patterns.GOOD_IRI_CHAR + "\\-]{0,64}\\.)+"   // named host
                    + Patterns.TOP_LEVEL_DOMAIN_STR_FOR_WEB_URL
                    + "|(?:(?:25[0-5]|2[0-4]" // or ip address
                    + "[0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9])\\.(?:25[0-5]|2[0-4][0-9]"
                    + "|[0-1][0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(?:25[0-5]|2[0-4][0-9]|[0-1]"
                    + "[0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(?:25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}"
                    + "|[1-9][0-9]|[0-9])))"
                    + "(?:\\:\\d{1,5})?)" // plus option port number
                    + "(\\/(?:(?:[" + Patterns.GOOD_IRI_CHAR + "\\;\\/\\?\\:\\@\\&\\=\\#\\~"  // plus option query params
                    + "\\-\\.\\+\\!\\*\\'\\(\\)\\,\\_])|(?:\\%[a-fA-F0-9]{2}))*)?"
                    + "(?:\\b|$)";

    String PHONE_PATTERN = Patterns.PHONE.pattern();
    String EMAIL_PATTERN = Patterns.EMAIL_ADDRESS.pattern();

    float NOT_APPROVED_ALPHA = 0.2f;
    float APPROVED_ALPHA = 1.0f;

    int MAX_LENGTH_COMMENT = 512;
    int MAX_LINE_COMMENT = 4;


}
