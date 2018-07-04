package com.vn.ntsc.ui.mediadetail.util;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.vn.ntsc.ui.mediadetail.util.TypeFile.AUDIO;
import static com.vn.ntsc.ui.mediadetail.util.TypeFile.IMAGE;
import static com.vn.ntsc.ui.mediadetail.util.TypeFile.VIDEO;

@Retention(RetentionPolicy.SOURCE)
@IntDef({IMAGE, AUDIO, VIDEO})
public @interface TypeFile {
    int IMAGE = 1;
    int AUDIO = 2;
    int VIDEO = 3;
}
