package com.vn.ntsc.utils;

import android.content.Context;

import com.vn.ntsc.repository.model.chat.sql.DatabaseHelper;
import com.vn.ntsc.ui.splash.SplashContract;

import java.util.ArrayList;

/**
 * filter dirty words util, after get banned word from server this method will call {@link #init(Context)}
 */
public class DirtyWordUtils {
    private static final String TAG = DirtyWordUtils.class.getSimpleName();

    /**
     * to detect dirty words
     */
    private static ArrayList<String> REGEX_DIRTY_WORDS;

    /**
     * get all banned word from db (should call 1 time)
     *
     * @param context to access db
     * @see SplashContract.View#onCompleteGetBannedWord()
     */
    public static void init(Context context) {
        REGEX_DIRTY_WORDS = DatabaseHelper.getInstance(context).getBannedWords();
//        StringBuilder dirty = new StringBuilder();

//        ArrayList<String> listBannedWord = DatabaseHelper.getInstance(context).getBannedWords();
//        dirty.append("(\\p{Space}+(");
//        for (int i = 0; i < listBannedWord.size(); i++) {
//            dirty.append(listBannedWord.get(i).trim().toLowerCase())
//                    .append("|");
//        }
//        dirty.append("))+\\p{Space}$");
//
//        REGEX_DIRTY_WORDS = dirty.toString().trim().toLowerCase();
//        if (REGEX_DIRTY_WORDS.length() > 0)
//            REGEX_DIRTY_WORDS = REGEX_DIRTY_WORDS.substring(0, REGEX_DIRTY_WORDS.length() - 1);
//
//        LogUtils.i(TAG, "dirty List: " + REGEX_DIRTY_WORDS);
    }

    /**
     * @param input raw string
     * @return array contain banned words at 0, replace word at 1 => ["banned_word", "replace_word"]
     */
    public static ArrayList<String> listDetectsBannedWord(Context context, String input) {
        ArrayList<String> listDetectsBannedWord = new ArrayList<>();

        if (REGEX_DIRTY_WORDS == null)
            REGEX_DIRTY_WORDS = DatabaseHelper.getInstance(context).getBannedWords();

        LogUtils.i(TAG, REGEX_DIRTY_WORDS.toString());

        StringBuilder builder = new StringBuilder(input.toLowerCase());
        builder.insert(0, " ");
        builder.insert(input.length() + 1, " ");
        for (String aList : REGEX_DIRTY_WORDS) {
            if (builder.indexOf(" " + aList.toLowerCase().trim() + " ") >= 0) {
                listDetectsBannedWord.add(aList);
            }
        }

        return listDetectsBannedWord;
    }

    /**
     * @param bw input
     * @return ex: [aa, bb, cc] => "aa", "bb", "cc"
     */
    public static String convertArraySetToString(ArrayList<String> bw) {
        StringBuilder bwBuilder = new StringBuilder();

        for (String s : bw) {
            if (bwBuilder.length() > 0) {
                bwBuilder.append(", ");
            }
            bwBuilder.append("\"");
            bwBuilder.append(s);
            bwBuilder.append("\"");
        }
        return bwBuilder.toString();
    }
}
