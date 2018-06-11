package com.vn.ntsc.utils.cache;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by nankai on 2/21/2018.
 */
@StringDef({CacheType.CACHE_TIMELINE_ALL,
        CacheType.CACHE_TIMELINE_FAVORITE,
        CacheType.CACHE_TIMELINE_LIVE_STREAM_ALL,
        CacheType.CACHE_TIMELINE_LIVE_STREAM_FAVORITE,
        //Cache by user ID
        CacheType.CACHE_TIMELINE_USER_ID,
        CacheType.CACHE_TIMELINE_PUBLIC_IMAGES_ID,
        CacheType.CACHE_TIMELINE_USER_PROFILE_ID,
        CacheType.CACHE_TIMELINE_USER_INFO_ID,
        //Cache by buzz ID
        CacheType.CACHE_TIMELINE_DETAIL_BY_ID,
        CacheType.CACHE_TIMELINE_DETAIL_LIST_COMMENT_BY_ID,
        //BuzzID + SubCmtID
        CacheType.CACHE_TIMELINE_DETAIL_LIST_SUB_COMMENT_BY_ID
})
@Retention(RetentionPolicy.SOURCE)
public @interface CacheType {
    /** cache response timeline all */
    String CACHE_TIMELINE_ALL = "tl_all";
    /** cache response timeline favorite */
    String CACHE_TIMELINE_FAVORITE = "tl_f";
    /** cache response timeline live stream all */
    String CACHE_TIMELINE_LIVE_STREAM_ALL = "tl_ls_all";
    /** cache response timeline live stream favorite */
    String CACHE_TIMELINE_LIVE_STREAM_FAVORITE = "tl_ls_f";
    //Cache by user ID
    /** cache response timeline profile by user id */
    String CACHE_TIMELINE_USER_ID = "tl_user_by_%1$s";
    /** cache response public images by user id */
    String CACHE_TIMELINE_PUBLIC_IMAGES_ID = "lpi_by_%1$s";
    /** cache response timeline profile by user id */
    String CACHE_TIMELINE_USER_PROFILE_ID = "up_by_%1$s";
    /** cache response timeline profile by user id */
    String CACHE_TIMELINE_USER_INFO_ID = "ui_by_%1$s";
    //Cache by buzz ID
    /** cache response timeline detail by buzz ID*/
    String CACHE_TIMELINE_DETAIL_BY_ID = "tld_by_%1$s";
    /** cache response list comment by buzz ID*/
    String CACHE_TIMELINE_DETAIL_LIST_COMMENT_BY_ID = "tld_c_by_%1$s";
    //BuzzID + SubCmtID
    /** cache response list sub comment by buzz ID vs comment id*/
    String CACHE_TIMELINE_DETAIL_LIST_SUB_COMMENT_BY_ID = "tld_sc_by_%1$s_%2$s";
}
