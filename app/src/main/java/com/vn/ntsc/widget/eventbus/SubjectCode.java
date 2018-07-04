package com.vn.ntsc.widget.eventbus;

/**
 * Created by nankai on 8/30/2017.
 */

public class SubjectCode {
    //Timeline
    public static final int SUBJECT_UPDATE_TIMELINE = 0;
    public static final int SUBJECT_REMOVE_TIMELINE = 1;
    public static final int SUBJECT_UPDATE_TIMELINE_PROFILE = 2;
    public static final int SUBJECT_UPDATE_TIMELINE_COMMENT = 3;
    public static final int SUBJECT_UPDATE_TIMELINE_LIKE = 4;
    public static final int SUBJECT_UPDATE_TIMELINE_FAVORITE = 5;
    public static final int SUBJECT_UPDATE_TIMELINE_UNFAVORITE = 6;
    public static final int SUBJECT_UPDATE_TIMELINE_SHARE = 7;
    public static final int SUBJECT_UPDATE_TIMELINE_FROM_LIVE_STREAM = 8;

    // media detail
    public static final int SUBJECT_UPDATE_COMMENT_IN_MEDIA_ACTIVITY = 11;
    public static final int SUBJECT_UPDATE_LIKE_IN_MEDIA_ACTIVITY = 12;
    public static final int SUBJECT_DEL_BUZZ_CHILD_IN_LIST_MEDIA = 13;
    public static final int SUBJECT_UPDATE_LIKE_IN_HEADER_PROFILE = 14;

    // MY ALBUM
    public static final int SUBJECT_REFRESH_ALBUM = 19844;
    public static final int SUBJECT_DELETE_ALBUM = 19845;
    public static final int SUBJECT_ADD_ALBUM = 19846;
    public static final int SUBJECT_ADD_IMAGE_TO_ALBUM = 18888;


    //Post status
    public static final int SUBJECT_POST_STATUS = 19;
    public static final int SUBJECT_POST_STATUS_ERROR = 20;
    public static final int SUBJECT_POST_STATUS_SUCCESS = 21;


    // UPLOAD FILE
    public static final int SUBJECT_UPLOAD_SUCCESS = 22;
    public static final int SUBJECT_UPLOAD_FAIL = 23;
    public static final int SUBJECT_REFRESH_UPLOAD_DONE = 24;
    public static final int SUBJECT_REFRESH_UPLOAD_FALSE = 25;

    // dowload emoji
    public static final int SUBJECT_DOWLOAD_EMOJI = 26;

    // UpLoadImageAlbum
    public static final int SUBJECT_CREATE_NEW_ALBUM = 27;
    public static final int SUBJECT_ALBUM_ERROR = 28;
    public static final int SUBJECT_REQUEST_CHECK_UP_LOAD_ALBUM = 29;
    public static final int SUBJECT_RESPONSE_CHECK_UP_LOAD_ALBUM = 30;
    public static final int SUBJECT_UPLOAD_IMAGE_SUCCESS = 31;
    public static final int SUBJECT_UPLOAD_IMAGE_FAILURE = 32;

    // Image Album
    public static final int SUBJECT_DELETE_IMAGE_IN_ALBUM = 33;

    // TAB Timeline MyProfile
    public static final int SUBJECT_UPDATE_DATA = 34;

}
