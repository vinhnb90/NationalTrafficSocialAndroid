package com.vn.ntsc.repository.preferece;

import android.content.Context;
import android.content.SharedPreferences;

import com.vn.ntsc.app.AppController;

/**
 * Dùng cho @see {@link SharedPreferences} <br />
 * <p>
 * khi extend @see {@link BasePrefers} sẽ @Override <b>getFileNamePrefers()</b> -> Tên file SharedPreferences
 * <p>
 * Created by nankai on 11/28/2016.
 */

public abstract class BasePrefers {
    protected Context mContext;

    public BasePrefers() {

    }

    protected SharedPreferences getPreferences() {
        if (mContext == null)
            mContext = AppController.getAppContext();

        return mContext.getSharedPreferences(getFileNamePrefers(),
                Context.MODE_PRIVATE);
    }

    protected SharedPreferences.Editor getEditor() {
        return getPreferences().edit();
    }

    protected abstract String getFileNamePrefers();
}