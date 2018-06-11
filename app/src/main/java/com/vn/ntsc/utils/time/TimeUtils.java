package com.vn.ntsc.utils.time;

import android.content.Context;

import com.vn.ntsc.R;
import com.vn.ntsc.app.AppController;
import com.vn.ntsc.utils.LogUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by nankai on 8/3/2017.
 */

public class TimeUtils {

    public static final String DATE_ONLY_WITH_SLASH_FORMAT = "yyyy/MM/dd";
    public static final String DATE_ONLY_VI_FORMAT_SHORT = "dd 'Th'M 'lúc' HH:mm";
    public static final String YYYYMMDDHHMMSS_SSS_FORMAT = "yyyyMMddHHmmssSSS";

    private static final int SEC = 1000;
    private static final int MIN = SEC * 60;
    private static final int HOUR = MIN * 60;
    private static final int DAY = HOUR * 24;
    private static final long WEEK = DAY * 7;
    private static final long YEAR = WEEK * 52;

    public static String TAG = TimeUtils.class.getSimpleName();

    public static String CURRENT_TIMELINE_ZONE_ID = "UTC";

    public static Locale LOCALE = Locale.ENGLISH;

    public static SimpleDateFormat YYYYMMDDHHMMSS = new SimpleDateFormat(
            "yyyyMMddHHmmss", LOCALE);

    public static String convertMillisToTimeString(long millis) {
        try {
            String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                    TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                    TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
            return hms;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getLoginTime() {
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        Date date = calendar.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat(YYYYMMDDHHMMSS_SSS_FORMAT,
                Locale.getDefault());
        return dateFormat.format(date);
    }

    public static String convertGMTtoLocale(String time) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(YYYYMMDDHHMMSS_SSS_FORMAT,
                Locale.getDefault());
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        try {
            Date date = dateFormat.parse(time);
            dateFormat = new SimpleDateFormat(YYYYMMDDHHMMSS_SSS_FORMAT, Locale.getDefault());
            dateFormat.setTimeZone(TimeZone.getDefault());
            return dateFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String convertGMTtoLocale(String time, String formatServer,
                                            String formatLocale) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(formatServer,
                Locale.getDefault());
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        try {
            Date date = dateFormat.parse(time);
            dateFormat = new SimpleDateFormat(formatLocale, Locale.getDefault());
            dateFormat.setTimeZone(TimeZone.getDefault());
            return dateFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * @return current time string in format @CURRENT_PATTERN_FORMAT
     */
    public static String getTimeInLocale() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat(YYYYMMDDHHMMSS_SSS_FORMAT, Locale.getDefault());
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        return dateFormat.format(date);
    }

    public static long convertTimeToMilisecond(String time) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(YYYYMMDDHHMMSS_SSS_FORMAT,
                Locale.getDefault());
        Date date;
        try {
            date = dateFormat.parse(time);
            return date.getTime();
        } catch (ParseException e) {
            LogUtils.e(TAG, String.valueOf(e.getMessage()));
        }
        return 0L;
    }

    public static String getTimelineDif(Calendar startDateTime,
                                        Calendar endDateTime) {

        Context context = AppController.getAppContext();

        long milliseconds1 = startDateTime.getTimeInMillis();
        long milliseconds2 = endDateTime.getTimeInMillis();
        long diff = milliseconds2 - milliseconds1;

        if (endDateTime.get(Calendar.YEAR) - startDateTime.get(Calendar.YEAR) > 0) {
            return getTimelineAfterYear(startDateTime.getTime());
        }

        int hours = (int) (diff / (1000 * 60 * 60));
        if (hours >= 24) {
            return getTimeLineAfter24h(startDateTime.getTime());
        }

        if (hours > 0) {
            return (String.valueOf(hours + 1) + context
                    .getString(R.string.time_hour_ago));
        }

        int minutes = (int) (diff / (1000 * 60));
        if (minutes > 30) {
            return (String.valueOf(1) + context
                    .getString(R.string.time_hour_ago));
        }

        if (minutes > 0) {
            return (minutes + context.getString(R.string.time_minute_ago));
        }

        return (context.getString(R.string.common_now));
    }

    private static String getTimeLineAfter24h(Date date) {
        SimpleDateFormat format = new SimpleDateFormat(DATE_ONLY_VI_FORMAT_SHORT, LOCALE);
        return format.format(date);
    }

    private static String getTimelineAfterYear(Date date) {
        SimpleDateFormat format = new SimpleDateFormat(DATE_ONLY_WITH_SLASH_FORMAT, LOCALE);
        return format.format(date);
    }

    /**
     * Parse Date from the date time string input
     * @param inputTime The String input with format yyyyMMddHHmmssSSS (UTC 00)
     * @return A <code>Date</code> parsed from the string.
     * @throws ParseException
     *
     * @author Created by Robert on 2018 Jan 31
     */
    public static Date convertStringToDateDefault(String inputTime) throws ParseException {
        // Format Time from input String time
        SimpleDateFormat inputFormat = new SimpleDateFormat(YYYYMMDDHHMMSS_SSS_FORMAT, LOCALE);
        inputFormat.setTimeZone(TimeZone.getTimeZone(CURRENT_TIMELINE_ZONE_ID));
        Date date = inputFormat.parse(inputTime);

        SimpleDateFormat outputFormat = new SimpleDateFormat(YYYYMMDDHHMMSS_SSS_FORMAT, Locale.getDefault());      // String format
        inputFormat.setTimeZone(TimeZone.getDefault());
        String format = inputFormat.format(date);

        return outputFormat.parse(format);
    }
}
