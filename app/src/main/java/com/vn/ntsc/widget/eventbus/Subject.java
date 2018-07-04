package com.vn.ntsc.widget.eventbus;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.SOURCE;

/**
 * Created by nankai on 8/30/2017.
 */

@Retention(SOURCE)
@IntDef({SubjectCode.SUBJECT_UPDATE_TIMELINE,
        SubjectCode.SUBJECT_REMOVE_TIMELINE,
        SubjectCode.SUBJECT_UPDATE_TIMELINE_PROFILE,
        SubjectCode.SUBJECT_UPDATE_TIMELINE_COMMENT,
        SubjectCode.SUBJECT_UPDATE_TIMELINE_LIKE,
        SubjectCode.SUBJECT_UPDATE_TIMELINE_FAVORITE,
        SubjectCode.SUBJECT_UPDATE_TIMELINE_UNFAVORITE,
        SubjectCode.SUBJECT_UPDATE_TIMELINE_SHARE,
        SubjectCode.SUBJECT_UPDATE_TIMELINE_FROM_LIVE_STREAM,

        SubjectCode.SUBJECT_UPDATE_LIKE_IN_MEDIA_ACTIVITY,
        SubjectCode.SUBJECT_UPDATE_COMMENT_IN_MEDIA_ACTIVITY,
        SubjectCode.SUBJECT_DEL_BUZZ_CHILD_IN_LIST_MEDIA,
        SubjectCode.SUBJECT_UPDATE_LIKE_IN_HEADER_PROFILE,
        SubjectCode.SUBJECT_REFRESH_ALBUM,
        SubjectCode.SUBJECT_DELETE_ALBUM,
        SubjectCode.SUBJECT_ADD_ALBUM,
        SubjectCode.SUBJECT_UPLOAD_SUCCESS,
        SubjectCode.SUBJECT_UPLOAD_FAIL,
        SubjectCode.SUBJECT_REFRESH_UPLOAD_DONE,
        SubjectCode.SUBJECT_REFRESH_UPLOAD_FALSE,
        SubjectCode.SUBJECT_ADD_IMAGE_TO_ALBUM,
        SubjectCode.SUBJECT_POST_STATUS,
        SubjectCode.SUBJECT_POST_STATUS_ERROR,
        SubjectCode.SUBJECT_POST_STATUS_SUCCESS,
        SubjectCode.SUBJECT_DOWLOAD_EMOJI,
        SubjectCode.SUBJECT_CREATE_NEW_ALBUM,
        SubjectCode.SUBJECT_ALBUM_ERROR,
        SubjectCode.SUBJECT_REQUEST_CHECK_UP_LOAD_ALBUM,
        SubjectCode.SUBJECT_RESPONSE_CHECK_UP_LOAD_ALBUM,
        SubjectCode.SUBJECT_UPLOAD_IMAGE_SUCCESS,
        SubjectCode.SUBJECT_UPLOAD_IMAGE_FAILURE,
        SubjectCode.SUBJECT_DELETE_IMAGE_IN_ALBUM,
        SubjectCode.SUBJECT_UPDATE_DATA

})
public @interface Subject {
}
