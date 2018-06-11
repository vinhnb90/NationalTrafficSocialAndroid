package com.vn.ntsc.repository.preferece;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.util.Pair;
import android.util.Base64;

/**
 * Created by dev22 on 8/25/17.
 */
public class RememberLoginPreference {
    /**
     * name may help hacker, make him difficult to understand
     */
    private static String preferenceName = "rlp";
    /**
     * ->M ai ->L
     */
    private static final String KEY_EMAIL = "ml";
    /**
     * ->P asswor ->D
     */
    private static final String KEY_PWD = "pd";

    /**
     * save login info to share preference, all values will encode base64 (not safe but something better than nothing)
     *
     * @param context  application context
     * @param email    to save
     * @param password to save
     */
    public static void saveLoginInfo(Context context, String email, String password) {
        SharedPreferences.Editor editor = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE).edit();
        editor.putString(KEY_EMAIL, Base64.encodeToString(email.getBytes(), Base64.DEFAULT))
                .putString(KEY_PWD, Base64.encodeToString(password.getBytes(), Base64.DEFAULT))
                .apply();
    }

    /**
     * get pair email/pass value
     *
     * @param context application context
     * @return pair(email, password)
     */
    public static Pair<String, String> getLoginInfo(Context context) {
        SharedPreferences sharePreference = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
        byte[] emailRaw = Base64.decode(sharePreference.getString(KEY_EMAIL, ""), Base64.DEFAULT);
        byte[] pwdRaw = Base64.decode(sharePreference.getString(KEY_PWD, ""), Base64.DEFAULT);
        return new Pair<>(new String(emailRaw), new String(pwdRaw));
    }

    /**
     * clear remember info
     *
     * @param context application context
     */
    public static void clear(Context context) {
        SharedPreferences.Editor sharePreference = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE).edit();
        sharePreference.clear()
                .apply();
    }
}
