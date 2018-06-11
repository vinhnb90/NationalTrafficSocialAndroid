package com.vn.ntsc.repository.model.timeline;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by nankai on 2/21/2018.
 */
@IntDef({TimelineType.BUZZ_TYPE_STATUS,
        //Multi buzz
        TimelineType.BUZZ_TYPE_MULTI_BUZZ_IMAGE_1,
        TimelineType.BUZZ_TYPE_MULTI_BUZZ_VIDEO_1,
        TimelineType.BUZZ_TYPE_MULTI_BUZZ_LIVE_STREAM,
        TimelineType.BUZZ_TYPE_MULTI_BUZZ_AUDIO,
        TimelineType.BUZZ_TYPE_MULTI_BUZZ_2,
        TimelineType.BUZZ_TYPE_MULTI_BUZZ_3,
        TimelineType.BUZZ_TYPE_MULTI_BUZZ_4,
        TimelineType.BUZZ_TYPE_MULTI_BUZZ_5,
        TimelineType.BUZZ_TYPE_MULTI_BUZZ_MORE,
        //template
        TimelineType.BUZZ_TYPE_MULTI_BUZZ_IMAGE_1_TEMPLATE,
        TimelineType.BUZZ_TYPE_MULTI_BUZZ_VIDEO_1_TEMPLATE,
        TimelineType.BUZZ_TYPE_MULTI_BUZZ_AUDIO_TEMPLATE,
        TimelineType.BUZZ_TYPE_MULTI_BUZZ_2_TEMPLATE,
        TimelineType.BUZZ_TYPE_MULTI_BUZZ_3_TEMPLATE,
        TimelineType.BUZZ_TYPE_MULTI_BUZZ_4_TEMPLATE,
        TimelineType.BUZZ_TYPE_MULTI_BUZZ_5_TEMPLATE,
        TimelineType.BUZZ_TYPE_MULTI_BUZZ_MORE_TEMPLATE,
        TimelineType.BUZZ_TYPE_STATUS_TEMPLATE,
        //Share
        TimelineType.BUZZ_TYPE_SHARE_AUDIO,
        TimelineType.BUZZ_TYPE_SHARE_LIVE_STREAM})
@Retention(RetentionPolicy.SOURCE)
public @interface TimelineType {

    int BUZZ_TYPE_STATUS = 0;
    //Multi buzz
    int BUZZ_TYPE_MULTI_BUZZ_IMAGE_1 = 1;
    int BUZZ_TYPE_MULTI_BUZZ_VIDEO_1 = 2;
    int BUZZ_TYPE_MULTI_BUZZ_LIVE_STREAM = 3;
    int BUZZ_TYPE_MULTI_BUZZ_AUDIO = 4;
    int BUZZ_TYPE_MULTI_BUZZ_2 = 5;
    int BUZZ_TYPE_MULTI_BUZZ_3 = 6;
    int BUZZ_TYPE_MULTI_BUZZ_4 = 7;
    int BUZZ_TYPE_MULTI_BUZZ_5 = 8;
    int BUZZ_TYPE_MULTI_BUZZ_MORE = 9;

    int BUZZ_TYPE_MULTI_BUZZ_IMAGE_1_TEMPLATE = 10;
    int BUZZ_TYPE_MULTI_BUZZ_VIDEO_1_TEMPLATE = 11;
    int BUZZ_TYPE_MULTI_BUZZ_AUDIO_TEMPLATE = 12;
    int BUZZ_TYPE_MULTI_BUZZ_2_TEMPLATE = 13;
    int BUZZ_TYPE_MULTI_BUZZ_3_TEMPLATE = 14;
    int BUZZ_TYPE_MULTI_BUZZ_4_TEMPLATE = 15;
    int BUZZ_TYPE_MULTI_BUZZ_5_TEMPLATE = 16;
    int BUZZ_TYPE_MULTI_BUZZ_MORE_TEMPLATE = 17;
    int BUZZ_TYPE_STATUS_TEMPLATE = 18;

    int BUZZ_TYPE_SHARE_AUDIO = 19;
    int BUZZ_TYPE_SHARE_LIVE_STREAM = 20;
}
