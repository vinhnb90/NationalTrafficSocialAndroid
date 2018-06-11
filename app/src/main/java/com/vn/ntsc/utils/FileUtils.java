package com.vn.ntsc.utils;


import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import okhttp3.Cache;

/**
 * Utilities class about some other common method
 * <p>
 * Created by Robert on 2016-01-15.
 */
public class FileUtils {
    private static final String TAG = FileUtils.class.getSimpleName();

    public static File getDiskCacheDir(Context context, String uniqueName) {
        File folder = new File(Environment.getExternalStorageDirectory() + "/" + uniqueName);
        if (!folder.exists()) {
            folder.mkdir();
            try {
                folder.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return folder;
    }

    public static Cache getCache(File diskCacheDir, int maxCacheSize) {
        return new Cache(diskCacheDir, maxCacheSize);
    }

    /**
     * @param output
     * @param filePathName
     * @return boolean (0: when write successful; false when failure)
     */
    public static boolean writeFile(byte[] output, String filePathName) {
        boolean result = true;
        try {
            File f = new File(filePathName);
            if (!f.exists()) f.createNewFile();
            FileOutputStream out = new FileOutputStream(f, false);
            out.write(output);
            out.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            result = false;
            return result;

        }
        return result;

    }

    /**
     * @param output
     * @param file
     * @return boolean (0: when write successful; false when failure)
     */
    public static boolean writeFile(byte[] output, File file) {
        boolean result = true;
        try {
            FileOutputStream out = new FileOutputStream(file, false);
            out.write(output);
            out.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            result = false;
            return result;
        }
        return result;

    }

    public static String dataFileToString(String file) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line = null;
        StringBuilder stringBuilder = new StringBuilder();
        String ls = System.getProperty("line.separator");

        try {
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
                stringBuilder.append(ls);
            }

            return stringBuilder.toString();
        } finally {
            reader.close();
        }
    }

    public static String convertStreamToString(InputStream is) throws Exception {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            return sb.toString();
        } catch (Exception e) {
        }
        return "";
    }

    public static String getStringFromFile(File file) throws Exception {
        try {
            FileInputStream fin = new FileInputStream(file);
            String result = convertStreamToString(fin);
            fin.close();
            return result;
        } catch (Exception e) {
        }
        return null;
    }

    public static String dataFileMD5EncryptedToString(File encTarget) {
        InputStream is = null;
        byte[] buffer = new byte[1024];
        int read;
        try {
            MessageDigest mdEnc = MessageDigest.getInstance("MD5");
            mdEnc.reset();
            is = new FileInputStream(encTarget);
            while ((read = is.read(buffer)) > 0) {
                mdEnc.update(buffer, 0, read);
            }

            byte[] md5sum = mdEnc.digest();
            StringBuilder builder = new StringBuilder();
            final int HEX = 16;
            int md5Size = md5sum.length;
            for (int i = 0; i < md5Size; i++) {
                builder.append(Integer.toString((md5sum[i] & 0xff) + 0x100, HEX).substring(1));
            }

            String output = builder.toString();
            Log.i(TAG, output);
            return output;
        } catch (NoSuchAlgorithmException e) {
            Log.i(TAG, "Exception while encrypting to md5");
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            Log.i(TAG, "Exception while getting FileInputStream");
            e.printStackTrace();
        } catch (IOException e) {
            Log.i(TAG, "Unable to process file for MD5");
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.i(TAG, "Exception on closing MD5 input stream");
                }
            }
        }

        return "";
    }
}
