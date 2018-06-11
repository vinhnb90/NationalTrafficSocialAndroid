package com.vn.ntsc.utils.cache;

import com.google.gson.Gson;
import com.google.gson.internal.Primitives;
import com.vn.ntsc.utils.LogUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by nankai on 1/26/2018.
 */

public class CacheJson extends CacheUtils {
    private static final String TAG = CacheJson.class.getSimpleName();

    /**
     * delete file older than a week
     */
    public static void deleteFileOlderWeek() {
        deleteAllOldFile();
    }

    /**
     * Retrieving a list of data
     *
     * @param key   - Like saving preferences, this String is a key to save and get specific data
     * @param model - Base Class that you want to recover. For example, if your List is something like List<String>, you will need put here String[].class
     * @return the saved list, or {@code null} if the list does not exists
     */
    public static <T> List<T> retrieveList(String key, Class<T[]> model) {

        T[] obj;

        String modelString = CacheUtils.retrieveData(key);
        LogUtils.i(TAG, modelString);
        obj = new Gson().fromJson(modelString, model);
        if (obj == null)
            return new ArrayList<>();
        else
            return Arrays.asList(obj);
    }

    /**
     * Saving a list of data
     *
     * @param key  - Like saving preferences, this String is a key to save and get specific data
     * @param list - List of content you want
     */
    public static <T> void saveList(String key, List<T> list) {
        CacheUtils.saveData(key, list);
    }

    /**
     * Retrieving only one object
     *
     * @param key         - Like saving preferences, this String is a key to save and get specific data
     * @param objectModel - Base Class that you want to recover. For example String.class, or MyObject.class
     * @return the saved object, or {@code null} if the object does not exists
     */
    public static <T> T retrieveObject(String key, Class<T> objectModel) {
        LogUtils.i(TAG, "Retrieve key: " + key);
        String modelString = CacheUtils.retrieveData(key);
        LogUtils.i(TAG, "Retrieve Json: " + modelString);
        Object model = null;
        try {
            model = new Gson().fromJson(modelString, objectModel);
        } catch (Exception e) {
        }
        return Primitives.wrap(objectModel).cast(model);
    }

    /**
     * Saving a list of data
     *
     * @param key         - Like saving preferences, this String is a key to save and get specific data
     * @param objectModel - Object you want to save
     */
    public static Object saveObject(String key, Object objectModel) {
        LogUtils.i(TAG, "Save key: " + key);
        return CacheUtils.saveData(key, objectModel);
    }
}
