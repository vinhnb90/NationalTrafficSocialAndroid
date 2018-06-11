package com.tux.socket;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class TimeUtils {
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.US);

    /**
     * use for socket connection
     *
     * @return GMT time
     */
    public static String getGMTDateTime() {
        // get time in GMT
        TimeZone tz = TimeZone.getDefault();
        long number = tz.getRawOffset();
        long local = System.currentTimeMillis();
        Date date = new Date(local - number);
        return sdf.format(date);
    }
}
