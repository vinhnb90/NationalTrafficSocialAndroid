package com.vn.ntsc.repository;

import android.support.annotation.IntDef;
import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * class dùng để khởi tạo các biến type view
 * <p>
 * Created by nankai on 8/8/2017.
 */

public class TypeView {

    @IntDef({TypeViewTimeline.TIMELINE_USER, TypeViewTimeline.TIMELINE_ALL, TypeViewTimeline.TIMELINE_FRIENDS, TypeViewTimeline.TIMELINE_FAVORITES, TypeViewTimeline.TIMELINE_LIVE_STREAM})
    @Retention(RetentionPolicy.SOURCE)
    public @interface TypeViewTimeline {
        int TIMELINE_USER = 0;
        int TIMELINE_ALL = 1;
        int TIMELINE_FRIENDS = 2;
        int TIMELINE_FAVORITES = 3;
        int TIMELINE_LIVE_STREAM = 4;
    }

    //========= type region ===============//
    @IntDef({RegionTypeView.REGION_GROUP, RegionTypeView.REGION})
    @Retention(RetentionPolicy.SOURCE)
    public @interface RegionTypeView {
        int REGION_GROUP = 1;
        int REGION = 2;
    }

    // FavoritePageFragment
    @IntDef({TypeViewFavorite.ME_FAVORITE, TypeViewFavorite.FAVORITE_ME})
    @Retention(RetentionPolicy.SOURCE)
    public @interface TypeViewFavorite {
        int FAVORITE_ME = 3;
        int ME_FAVORITE = 4;
    }

    // SearchResult Fragment
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({SearchType.TYPE_SETTING, SearchType.TYPE_BY_NAME})
    public @interface SearchType {
        int TYPE_SETTING = 1;
        int TYPE_BY_NAME = 2;
    }


    // SearchResult Fragment
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({MenuLeft.MENU_LEFT_HOME
            , MenuLeft.MENU_LEFT_NOTI
            , MenuLeft.MENU_LEFT_ONLINE_ALERT
            , MenuLeft.MENU_LEFT_SETTING_ACCOUNT
            , MenuLeft.MENU_LEFT_SETTING_NOTI
            , MenuLeft.MENU_LEFT_LIST_BLOCK
            , MenuLeft.MENU_LEFT_HELP
            , MenuLeft.MENU_LEFT_LOGOUT
            , MenuLeft.MENU_LEFT_LOGIN})
    public @interface MenuLeft {
        int MENU_LEFT_HOME = 0;
        int MENU_LEFT_NOTI = 1;
        int MENU_LEFT_ONLINE_ALERT = 2;
        int MENU_LEFT_SETTING_ACCOUNT = 3;
        int MENU_LEFT_SETTING_NOTI = 4;
        int MENU_LEFT_LIST_BLOCK = 5;
        int MENU_LEFT_HELP = 6;
        int MENU_LEFT_LOGOUT = 7;
        int MENU_LEFT_LOGIN = 8;
    }


    // SearchResult Fragment
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({MenuBottom.MENU_BOTTOM_HOME
            , MenuBottom.MENU_BOTTOM_FRIENDS
            , MenuBottom.MENU_BOTTOM_LIVE_STREAM
            , MenuBottom.MENU_BOTTOM_MESSAGE
            , MenuBottom.MENU_BOTTOM_NOTI})
    public @interface MenuBottom {
        int MENU_BOTTOM_HOME = 0;
        int MENU_BOTTOM_FRIENDS = 1;
        int MENU_BOTTOM_LIVE_STREAM = 2;
        int MENU_BOTTOM_MESSAGE = 3;
        int MENU_BOTTOM_NOTI = 4;
    }

    //========== page type Web view activity==========//
    // Page type
    @IntDef({PageTypeWebView.PAGE_TYPE_NOT_SET, PageTypeWebView.PAGE_TYPE_WEB_VIEW, PageTypeWebView.PAGE_TYPE_LOGIN_OTHER_SYS, PageTypeWebView.PAGE_TYPE_TERM_OF_SERVICE,
            PageTypeWebView.PAGE_TYPE_PRIVACY_POLICY, PageTypeWebView.PAGE_TYPE_TERM_OF_USE, PageTypeWebView.PAGE_TYPE_VERIFY_AGE, PageTypeWebView.PAGE_TYPE_AUTO_VERIFY_AGE,
            PageTypeWebView.PAGE_TYPE_ANDG_HOMEPAGE, PageTypeWebView.PAGE_TYPE_ABOUT_PAYMENT, PageTypeWebView.PAGE_TYPE_HOW_TO_USE, PageTypeWebView.PAGE_TYPE_SUPPORT, PageTypeWebView.PAGE_TYPE_FREE_POINT,
            PageTypeWebView.PAGE_TYPE_BUY_POINT, PageTypeWebView.PAGE_TYPE_CONTACT, PageTypeWebView.PAGE_TYPE_NOTICE, PageTypeWebView.PAGE_TYPE_POINT, PageTypeWebView.PAGE_TYPE_INFORMATION, PageTypeWebView.PAGE_TYPE_FROM_NOTI
            , PageTypeWebView.PAGE_TYPE_QA, PageTypeWebView.PAGE_TYPE_QA_HISTORY})
    @Retention(RetentionPolicy.SOURCE)
    public @interface PageTypeWebView {
        int PAGE_TYPE_NOT_SET = 0;
        int PAGE_TYPE_WEB_VIEW = 1;
        int PAGE_TYPE_LOGIN_OTHER_SYS = 2;
        int PAGE_TYPE_TERM_OF_SERVICE = 3;
        int PAGE_TYPE_PRIVACY_POLICY = 4;
        int PAGE_TYPE_TERM_OF_USE = 5;
        int PAGE_TYPE_VERIFY_AGE = 6;
        int PAGE_TYPE_AUTO_VERIFY_AGE = 7;
        int PAGE_TYPE_ANDG_HOMEPAGE = 8;
        int PAGE_TYPE_ABOUT_PAYMENT = 9;
        int PAGE_TYPE_HOW_TO_USE = 10;
        int PAGE_TYPE_SUPPORT = 11;
        int PAGE_TYPE_FREE_POINT = 12;
        int PAGE_TYPE_BUY_POINT = 13;
        int PAGE_TYPE_CONTACT = 14;
        int PAGE_TYPE_NOTICE = 15;
        int PAGE_TYPE_POINT = 16;
        int PAGE_TYPE_INFORMATION = 17;
        int PAGE_TYPE_FROM_NOTI = 18;
        int PAGE_TYPE_QA = 19;
        int PAGE_TYPE_QA_HISTORY = 20;
    }

    //    ProfileDetailActivity
    @IntDef({ProfileType.COME_FROM_TIMELINE, ProfileType.COME_FROM_NOTI, ProfileType.COME_FROM_OTHER, ProfileType.COME_FROM_TIMELINE_BY_ID})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ProfileType {
        int COME_FROM_TIMELINE = 1;
        int COME_FROM_NOTI = 2;
        int COME_FROM_OTHER = 3;
        int COME_FROM_TIMELINE_BY_ID = 4;
    }

    //Media type
    @StringDef({MediaDetailType.VIDEO_TYPE, MediaDetailType.IMAGE_TYPE, MediaDetailType.AUDIO_TYPE, MediaDetailType.STREAM_TYPE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface MediaDetailType {
        String VIDEO_TYPE = "video";
        String IMAGE_TYPE = "image";
        String AUDIO_TYPE = "audio";
        String STREAM_TYPE = "stream";
    }

    //MoreLayout
    @IntDef({MyMoreLayout.TYPE_CHAT, MyMoreLayout.TYPE_INFO})
    @Retention(RetentionPolicy.SOURCE)
    public @interface MyMoreLayout {
        int TYPE_CHAT = 0;
        int TYPE_INFO = 1;
    }

}
