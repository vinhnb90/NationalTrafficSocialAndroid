package com.vn.ntsc.utils;

import android.content.Context;

import com.google.gson.Gson;
import com.vn.ntsc.core.model.BaseBean;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created by ThoNh on 9/21/2017.
 */

public class AssetsUtils {

    public static <E extends BaseBean> E getDataAssets(Context context, String pathJsonAssets, Class<E> typeParamCls) {

        try {
            String json;
            InputStream is = context.getAssets().open(pathJsonAssets);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
            return new Gson().fromJson(json, typeParamCls);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static <E extends List> E getDataArrayAssets(Context context, String pathJsonAssets, Class<E> typeParamCls) {

        try {
            String json;
            InputStream is = context.getAssets().open(pathJsonAssets);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
            return new Gson().fromJson(json, typeParamCls);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
