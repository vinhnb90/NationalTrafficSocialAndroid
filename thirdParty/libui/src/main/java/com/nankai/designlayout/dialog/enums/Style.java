package com.nankai.designlayout.dialog.enums;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.nankai.designlayout.dialog.enums.Style.*;

@IntDef({HEADER_WITH_ICON, HEADER_WITH_TITLE,HEADER_WITH_NOT_HEADER})
@Retention(RetentionPolicy.SOURCE)
public @interface Style {
    int HEADER_WITH_ICON = 0;
    int HEADER_WITH_TITLE = 1;
    int HEADER_WITH_NOT_HEADER = 2;
}
