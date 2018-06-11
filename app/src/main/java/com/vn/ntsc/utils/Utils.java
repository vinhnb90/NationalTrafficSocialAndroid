package com.vn.ntsc.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.DrawableRes;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.vn.ntsc.R;
import com.vn.ntsc.repository.model.user.GetUserStatusRequest;
import com.vn.ntsc.repository.preferece.UserPreferences;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeMap;

import okhttp3.Request;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;

/**
 * Created by nankai on 8/3/2017.
 */

public class Utils {
    private static final String TAG = "Utils";
    private static final Charset UTF8 = Charset.forName("UTF-8");

    public static void setVectorDrawableLeft(@DrawableRes int idRs, TextView view, int color) {
        Drawable drawable;
        drawable = VectorDrawableCompat.create(view.getResources(), idRs, view.getContext().getTheme());
        assert drawable != null;
        DrawableCompat.setTint(drawable, color);
        DrawableCompat.setTintMode(drawable, PorterDuff.Mode.SRC_IN);
        view.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
    }

    public static void setVectorDrawableLeft(@DrawableRes int idRs, TextView view) {
        Drawable drawable;
        drawable = VectorDrawableCompat.create(view.getResources(), idRs, view.getContext().getTheme());
        view.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
    }

    public static void setVectorDrawableRight(@DrawableRes int idRs, TextView view) {
        Drawable drawable;
        drawable = VectorDrawableCompat.create(view.getResources(), idRs, view.getContext().getTheme());
        view.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null);
    }

    public static void setVectorDrawableTop(@DrawableRes int idRs, TextView view) {
        Drawable drawable;
        drawable = VectorDrawableCompat.create(view.getResources(), idRs, view.getContext().getTheme());
        view.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);
    }

    public static void setVectorDrawableBottom(@DrawableRes int idRs, TextView view) {
        Drawable drawable;
        drawable = VectorDrawableCompat.create(view.getResources(), idRs, view.getContext().getTheme());
        view.setCompoundDrawablesWithIntrinsicBounds(null, null, null, drawable);
    }

    public static int isNeededGetUserStatus() {
        UserPreferences prefers = UserPreferences.getInstance();
        LogUtils.d("STATUS ", TAG + "isNeededGetUserStatus : " + prefers.getRegEmail());
        if (!TextUtils.isEmpty(prefers.getFacebookId()) && prefers.isLogout()) {
            return GetUserStatusRequest.TYPE_FACEBOOK;
        } else if (!TextUtils.isEmpty(prefers.getRegEmail()) && prefers.isLogout()) {
            return GetUserStatusRequest.TYPE_EMAIL;
        } else {
            return GetUserStatusRequest.TYPE_NONE;
        }
    }

    public static String encryptPassword(String unencryptedPassword) {
        String encryptedPassword = unencryptedPassword;
        StringBuilder stringBuilder = new StringBuilder();

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            byte[] bytes = unencryptedPassword.getBytes("UTF-8");
            digest.update(bytes, 0, bytes.length);
            bytes = digest.digest();
            int length = bytes.length;

            for (int i = 0; i < length; i++) {
                byte b = bytes[i];
                stringBuilder.append(String.format("%02x", b));
            }
            encryptedPassword = stringBuilder.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } finally {
            stringBuilder.delete(0, stringBuilder.length());
        }

        return encryptedPassword;
    }

    /**
     * @return current time format yyyyMMddHHmmss
     */
    public static String getLoginTime() {
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        Date date = calendar.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss",
                Locale.getDefault());
        return dateFormat.format(date);
    }

    /**
     * @param context application context
     * @return current app version
     */
    public static String getAppVersionName(Context context) {
        String packageName = context.getPackageName();
        PackageInfo packageInfo = null;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(
                    packageName, 0);
        } catch (Exception e) {
            LogUtils.e(TAG, String.valueOf(e.getMessage()));
        }
        String appVersion = "";
        if (packageInfo != null && packageInfo.versionName != null) {
            String version = packageInfo.versionName;
            if (version.contains(" ")) {
                appVersion = packageInfo.versionName.substring(0,
                        packageInfo.versionName.indexOf(" "));
            } else {
                appVersion = version;
            }
        }
        return appVersion;
    }

    /**
     * TODO Dumping data keyword and value in intent object
     *
     * @param intent
     * @return
     * @author Created by Robert on 2015 Dec 11
     */
    public static String dumpIntent(Intent intent) {
        StringBuilder dumIntent = new StringBuilder("IntentData:");
        if (intent == null) return dumIntent.toString();
        try {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                Set<String> keys = bundle.keySet();
                Iterator<String> it = keys.iterator();
                Log.e("DumpIntent", "------>Dumping Intent start");
                while (it.hasNext()) {
                    String key = it.next();
                    dumIntent.append("\nKey:" + key + "-->Value:" + Utils.nullToEmpty(bundle.get(key)));
                    Log.e("DumpIntent", "[" + key + "=" + Utils.nullToEmpty(bundle.get(key)) + "]");
                }
                Log.e("DumpIntent", "------>Dumping Intent end");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dumIntent.toString();
    }

    public static String dumpMapData(Map<String, String> mMapData) {

        if (mMapData == null) {
            LogUtils.w(TAG, "notify MapData is null.!");
            return "";
        }

        StringBuilder dumBundle = new StringBuilder();

        try {

            Set<String> keys = mMapData.keySet();

            dumBundle.append("{\"value\":{\"aps\":");

            for (String key : keys) {
                dumBundle.append(Utils.nullToEmpty(mMapData.get(key)));
            }

            dumBundle.append("}}");

        } catch (Exception e) {
            e.printStackTrace();
        }

        return dumBundle.toString();
    }

    /*
 * **********************************************************************
 * Recycle
 * *********************************************************************
 */
    public static void clearHandler(Handler... handlers) throws Exception {
        if (handlers == null) {
            return;
        }
        if (handlers.length <= 0) {
            return;
        }
        for (Handler handler : handlers) {
            if (handler != null) {
                handler.removeCallbacksAndMessages(null);
            }
        }
    }

    public static String convertGMTtoLocale(String time) {
        String stringFomat = "yyyyMMddHHmmssSSS";
        SimpleDateFormat dateFormat = new SimpleDateFormat(stringFomat,
                Locale.getDefault());
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        try {
            Date date = dateFormat.parse(time);
            dateFormat = new SimpleDateFormat(stringFomat, Locale.getDefault());
            dateFormat.setTimeZone(TimeZone.getDefault());
            return dateFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static long convertTimeToMilisecond(String time) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS",
                Locale.getDefault());
        Date date;
        try {
            date = dateFormat.parse(time);
            long timeInMili = date.getTime();
            return timeInMili;
        } catch (ParseException e) {
        }
        return 0L;
    }

    public static String convertSecondToTimeFormat(int timeSecond) {
        int minutes = timeSecond / (60);
        int seconds = (timeSecond) % 60;
        return String.format(Locale.getDefault(), "%d:%02d", minutes, seconds);
    }

    private static final NavigableMap<Long, String> suffixes = new TreeMap<>();

    static {
        suffixes.put(1_000L, "k");
        suffixes.put(1_000_000L, "M");
        suffixes.put(1_000_000_000L, "G");
        suffixes.put(1_000_000_000_000L, "T");
        suffixes.put(1_000_000_000_000_000L, "P");
        suffixes.put(1_000_000_000_000_000_000L, "E");
    }

    public static String format(long value) {
        //Long.MIN_VALUE == -Long.MIN_VALUE so we need an adjustment here
        if (value == Long.MIN_VALUE) return format(Long.MIN_VALUE + 1);
        if (value < 0) return "-" + format(-value);
        if (value < 1000) return Long.toString(value); //deal with easy case
        Map.Entry<Long, String> e = suffixes.floorEntry(value);
        Long divideBy = e.getKey();
        String suffix = e.getValue();
        long truncated = value / (divideBy / 10); //the number part of the output times 10
        boolean hasDecimal = truncated < 100 && (truncated / 10d) != (truncated / 10);
        return hasDecimal ? (truncated / 10d) + suffix : (truncated / 10) + suffix;
    }

    public static String bodyRequestToString(final Request request) {
        try {
            final Request copy = request.newBuilder().build();
            final Buffer buffer = new Buffer();
            copy.body().writeTo(buffer);
            return buffer.readUtf8();
        } catch (final IOException e) {
            return "did not work";
        }
    }

    public static String bodyResponseToString(final ResponseBody responseBody) {
        try {
            BufferedSource source = responseBody != null ? responseBody.source() : null;
            if (source != null) {
                source.request(Long.MAX_VALUE); // Buffer the entire body.
            }
            Buffer buffer = source != null ? source.buffer() : null;
            String jsonResponse = buffer != null ? buffer.clone().readString(UTF8) : null;
            return jsonResponse;
        } catch (final IOException e) {
            return "did not work";
        }
    }

    public static void makeTextViewResizable(final TextView tv, final int maxLine, final String expandText, final boolean viewMore) {

        if (tv.getTag() == null) {
            tv.setTag(tv.getText());
        }

        ViewTreeObserver vto = tv.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                String text;
                int lineEndIndex;
                ViewTreeObserver obs = tv.getViewTreeObserver();
                obs.removeOnPreDrawListener(this);
                if (tv.getLineCount() > Constants.MAX_LINE_COMMENT) {
                    if (maxLine == 0) {
                        lineEndIndex = tv.getLayout().getLineEnd(0);
                        text = tv.getText().subSequence(0, lineEndIndex - expandText.length() + 1) + " " + expandText;
                    } else if (maxLine > 0 && tv.getLineCount() >= maxLine) {
                        lineEndIndex = tv.getLayout().getLineEnd(maxLine - 1);
                        text = tv.getText().subSequence(0, lineEndIndex - expandText.length() + 1) + " " + expandText;
                    } else {
                        lineEndIndex = tv.getLayout().getLineEnd(tv.getLayout().getLineCount() - 1);
                        text = tv.getText().subSequence(0, lineEndIndex) + " " + expandText;
                    }
                    tv.setText(text);
                    tv.setMovementMethod(LinkMovementMethod.getInstance());
                    tv.setText(addClickablePartTextViewResizable(Html.fromHtml(tv.getText().toString()), tv, expandText,
                            viewMore), TextView.BufferType.SPANNABLE);
                }
                return true;
            }
        });
    }

    private static SpannableStringBuilder addClickablePartTextViewResizable(final Spanned strSpanned, final TextView tv, final String spanableText, final boolean viewMore) {
        String str = strSpanned.toString();
        SpannableStringBuilder ssb = new SpannableStringBuilder(strSpanned);

        if (str.contains(spanableText)) {

            ssb.setSpan(new ClickableSpan() {
                @Override
                public void updateDrawState(TextPaint ds) {
                    ds.setUnderlineText(false);
                    ds.setColor(Color.parseColor("#343434"));
                }

                @Override
                public void onClick(View widget) {
                    tv.setLayoutParams(tv.getLayoutParams());
                    tv.setText(tv.getTag().toString(), TextView.BufferType.SPANNABLE);
                    tv.invalidate();
                    if (viewMore) {
                        makeTextViewResizable(tv, -1, widget.getResources().getString(R.string.see_Less), false);
                    } else {
                        makeTextViewResizable(tv, Constants.MAX_LINE_COMMENT, widget.getResources().getString(R.string.see_more), true);
                    }

                }
            }, str.indexOf(spanableText), str.indexOf(spanableText) + spanableText.length(), 0);

        }
        return ssb;

    }

    /**
     * Checks if is empty or null.
     *
     * @param input the input
     * @return true, if is empty or null
     */
    public static boolean isEmptyOrNull(String input) {
        if (input == null) return true;
        if (input.trim().length() == 0 || "null".equalsIgnoreCase(input.trim())) return true;
        return false;
    }

    /**
     * Checks if is empty or null.
     *
     * @param input the object
     * @return true, if is empty or null
     */
    public static boolean isEmptyOrNull(Object input) {
        if (input == null) return true;
        if (nullToEmpty(input).trim().length() == 0) return true;
        return false;
    }

    /**
     * *************************************************************************
     * Method check StringBuffer isEmptyOrNull.
     *
     * @param input the input
     * @return boolean
     * * @author Hoang Minh Duc
     * ************************************************************************
     */
    public static boolean isEmptyOrNull(StringBuffer input) {
        if (input == null) return true;
        String sInput = input.toString();
        if (sInput.trim().length() == 0) return true;
        return false;
    }

    /**
     * *************************************************************************
     * Method check StringBuilder isEmptyOrNull.
     *
     * @param input the input
     * @return boolean
     * * @author Hoang Minh Duc
     * ************************************************************************
     */
    public static boolean isEmptyOrNull(StringBuilder input) {
        if (input == null) return true;
        String sInput = nullToEmpty(input).trim();
        //if ("".equals(sInput.trim())) return true;
        if (sInput.length() == 0) return true;
        return false;
    }

    /**
     * Null to empty.
     *
     * @param input the input
     * @return the string
     */
    public static String nullToEmpty(Object input) {
        return (input == null ? "" : ("null".equals(input) ? "" : input.toString()));
    }

    /**
     * repalce all String parameter replace with String parameter with in data String.
     *
     * @param input   the input
     * @param replace the replace
     * @param with    the with
     * @return the string
     * @author Hoang Minh Duc: 0989664386<br>
     */
    public static String replaceAll(String input, String replace, String with) {
        try {
            if (input == null || "".equals(input)) return null;
            if (replace == null || "".equals(replace)) return input;
            if (input.length() < replace.length()) return input;
            int from = -1;
            while ((from = input.indexOf(replace)) > -1) {
                input = input.substring(0, from) + with + input.substring(from + replace.length());
            }
            return input;
        } catch (Exception e) {
        }
        return null;
    }
}
