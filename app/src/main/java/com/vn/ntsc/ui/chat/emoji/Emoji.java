package com.vn.ntsc.ui.chat.emoji;

import android.support.annotation.NonNull;

import java.util.Locale;

/**
 * Created by ducng on 12/6/2017.
 */

public class Emoji implements Comparable<Emoji> {
    public static final String TAG_FORMAT = "<img src=\"%1$s\">";
    private String resource;
    private String code;
    private String tag;

    public Emoji(String resource, String code, String tag) {
        this.resource = resource;
        this.code = code.toLowerCase(Locale.US);
        this.tag = String.format(TAG_FORMAT, tag);
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String codeToTag(String message) {
        return message.replace(code, tag).replace(code.toUpperCase(Locale.US), tag);
    }

    public String tagToCode(String message) {
        return message.replace(tag, code);
    }


    @Override
    public int compareTo(@NonNull Emoji emoji) {
        if (code.length() < emoji.code.length()) {
            return 1;
        } else if (code.length() > emoji.code.length()) {
            return -1;
        } else {
            return 0;
        }
    }
}
