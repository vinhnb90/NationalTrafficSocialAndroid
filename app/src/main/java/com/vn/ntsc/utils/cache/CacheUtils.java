package com.vn.ntsc.utils.cache;

import android.content.Context;

import com.google.gson.Gson;
import com.vn.ntsc.app.AppController;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by nankai on 1/26/2018.
 */

public class CacheUtils {

    /**
     * Save the data you need
     *
     * @param key     key to save and retrieve data
     * @param json    which you will save (needs to be in JSON format)
     * @return true -> ok / false -> Unable to save
     */
    static boolean saveData(String key, Object json) {
        return writeObject(AppController.getAppContext(), key, new Gson().toJson(json));
    }

    /**
     * Get some previously saved data
     *
     * @param key     key to save and retrieve data
     * @return em {@code String} o JSON salvo on the memoir
     */
   static String retrieveData(String key) {
        return String.valueOf(readObject(AppController.getAppContext(), key));
    }

    private static boolean writeObject(Context context, String key, String object) {
        try {
            //saving the object in Android memory
            FileOutputStream fos = context.openFileOutput(key, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(object);
            oos.close();
            fos.close();

        } catch (IOException e) {
            return false; // if it is not possible to save, return false
        }
        return true; // if it is possible to save, return true
    }

    private static Object readObject(Context context, String key) {
        try {
            // Opening the created "file"
            FileInputStream fis = context.openFileInput(key);
            ObjectInputStream ois = new ObjectInputStream(fis);
            return ois.readObject(); // returning object

        } catch (Exception e) {
            return null; // return null if there is no file still saved
        }
    }

    static void deleteAllOldFile() {
        try {
            for (final String fileEntry : AppController.getAppContext().fileList()) {
                File file = AppController.getAppContext().getFileStreamPath(fileEntry);
                if (file.exists()) {
                    Calendar time = Calendar.getInstance();
                    time.add(Calendar.DAY_OF_YEAR, -7);
                    //I store the required attributes here and delete them
                    Date lastModified = new Date(file.lastModified());
                    if (lastModified.before(time.getTime())) {
                        //file is older than a week
                        file.deleteOnExit();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
