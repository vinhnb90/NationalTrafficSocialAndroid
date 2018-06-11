package com.vn.ntsc.utils.chats;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.vn.ntsc.R;
import com.vn.ntsc.app.AppController;
import com.vn.ntsc.repository.model.chat.sql.DatabaseHelper;
import com.vn.ntsc.services.StickerAndGiftDownloadService;
import com.vn.ntsc.ui.chat.sticker.Sticker;
import com.vn.ntsc.ui.chat.sticker.StickerCategory;
import com.vn.ntsc.utils.Decompress;
import com.vn.ntsc.utils.LogUtils;
import com.vn.ntsc.utils.PhotoUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

/**
 * Created by ThoNh on 9/29/2017.
 */

public class ChatUtils {
    public static String pathStorage = Environment.getExternalStorageDirectory() + "/Android/data/com.vn.ntsc.develop" + "/files/";
    public static final String MY_PREFS_VERSION = "MY_PREFS_VERSION";
    public static final String TAG = "ChatUtils";
    public final static String STICKER = "stickers";
    public final static String GIFT = "gifts";
    public final static String EMOJI = "emoji";
    public final static String AUDIO = "audio";


    public static final String PNG_EXTENSION = "png";
    public static final String GIF_EXTENSION = "gif";

    private final static String THUMBNAIL = "thumbnail";
    private final static String INDEX = "index";
    private final static String ORDER = "order";

    private static ArrayList<StickerCategory> mStickerData;


    /**
     * Tạo folder để lưu các sticky
     *
     * @param context    Context
     * @param folderName tên thư mục lưu (chính là CategoryId của sticky)
     * @return
     */
    public static File createFolderSaveSticky(Context context, String folderName) {
        File folder = new File(new File(context.getExternalFilesDir(null), STICKER), folderName);
        if (!folder.exists()) {
            folder.mkdir();
        }
        return folder;
    }

    /**
     * Lấy đường dẫn của folder
     *
     * @param context
     * @param folderName tên Folder (chính là CategoryId của sticky)
     * @return
     */
    public static String getFolderSaveSticky(Context context, String folderCategory, String folderName) {
        File folder = new File(new File(context.getExternalFilesDir(null), folderName), folderCategory);
        return folder.getAbsolutePath();
    }

    public static String getFolderSaveGift(Context context, String folderName) {
        File folder = new File(new File(context.getExternalFilesDir(null), GIFT), folderName);
        return folder.getAbsolutePath();
    }

    private static boolean saveStickerFile(Context context, String folder,
                                           byte[] data, String index) {
        int targSize = context.getResources().getDimensionPixelSize(
                R.dimen.item_ticker_chat_size);

        File fileZip = new File(new File(context.getExternalFilesDir(null),
                STICKER), "sticker_tmp");
        File fileUnzip = createFolderSaveSticky(context, folder);
        if (!fileUnzip.exists()) {
            fileUnzip.mkdir();
        }
        FileOutputStream fileOutputStream;
        try {
            fileOutputStream = new FileOutputStream(fileZip);
            fileOutputStream.write(data);
            fileOutputStream.flush();
            fileOutputStream.close();

            // save index file
            FileOutputStream outputStream = new FileOutputStream(fileUnzip
                    + File.separator + "index");
            outputStream.write(index.getBytes());
            outputStream.flush();
            outputStream.close();

            // decompress file
            Decompress des = new Decompress(fileZip.getPath(),
                    fileUnzip.getPath());
            des.unzip();

            // resize ticker after unzip
            if (fileUnzip.listFiles() != null) {
                for (File child : fileUnzip.listFiles()) {
                    if (child.isDirectory()) {
                        LogUtils.e("ChatUtils-lines 214", "isDirectory");
                    } else {
                        String name = child.getName();
                        if (!name.equals(INDEX)) {
                            String folderPatch = fileUnzip.getPath();
                            createThumbnailSticker(folderPatch, name, targSize,
                                    targSize);
                        }
                    }
                }
            }

            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Nhanv: resize bitmap with new width and new height
     *
     * @param repWidth
     * @param repHeight
     */
    public static void createThumbnailSticker(String folderPatch,
                                              String stickerName, int repWidth, int repHeight) {
        // resize bitmap
        String imgPatch = folderPatch + File.separator + stickerName;
        Bitmap resizeImg = PhotoUtils.decodeSampledBitmapFromFile(imgPatch,
                repWidth, repHeight);

        // create folder thumbnail sticker if not exists
        File thumFolder = new File(folderPatch, THUMBNAIL);
        if (!thumFolder.exists())
            thumFolder.mkdirs();

        // create thumbnail for sticker
        File file = new File(thumFolder, stickerName);
        if (file.exists())
            file.delete();

        try {
            FileOutputStream outputStream = new FileOutputStream(file);
            resizeImg.compress(Bitmap.CompressFormat.PNG, 100, outputStream);

            outputStream.flush();
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Decodes the base64 string into byte array
     *
     * @param imageDataString - a {@link String}
     * @return byte array
     */
    private static byte[] decodeFile(String imageDataString)
            throws IllegalArgumentException {
        return Base64.decode(imageDataString, Base64.DEFAULT);
    }


    /**
     * Save sticky to storage
     *
     * @param context
     * @param folderName Category_id_sticky
     * @param fileData   String zip file
     * @param index      String of arrat name of sticky
     * @return
     */
    public static boolean saveFileStickerFromString(Context context, String folderName, String fileData, String index) {
        File file = new File(context.getExternalFilesDir(null), STICKER);
        if (!file.exists()) {
            file.mkdir();
        }
        try {
            return saveStickerFile(context, folderName, decodeFile(fileData), index);
        } catch (Exception exception) {
            LogUtils.e(TAG, String.valueOf(exception.getMessage()));
        }
        return false;
    }

    /**
     * Get sticky save in storage
     *
     * @param context
     * @param contentMsg "content": "574eb73ce4b0ccb75f393e4d_100037.png",
     * @return /storage/emulated/0/Android/data/pkgName/files/stickers/574eb73ce4b0ccb75f393e4d/100037.png
     */
    public static String getPathStickerByPackageAndId(Context context,
                                                      String contentMsg) {
        try {
            if (contentMsg.contains("_")) {
                String[] data = contentMsg.split("_");
                String packageId = data[0];
                String idSticker = data[1];
                File file = new File(context.getExternalFilesDir(null), STICKER);
                File packageSticker = new File(file, packageId);
                File sticker = new File(packageSticker, idSticker);
                return sticker.getPath();
            } else {
                return contentMsg;
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return null;
    }

    public static boolean saveGiftFile(Context context, String catId, String giftId, byte[] data) {
        // create path if not exist
        File giftFolder = new File(context.getExternalFilesDir(null), GIFT + "/" + catId);
        if (!giftFolder.exists()) giftFolder.mkdirs();

        Log.d(StickerAndGiftDownloadService.TAG, "saveFileGift: " + giftFolder.getPath());
        FileOutputStream fileOutputStream;
        try {
            // create file if not exist
            File file = new File(giftFolder, giftId + ".png");
            if (!file.exists()) {
                file.createNewFile();
                fileOutputStream = new FileOutputStream(file);
                fileOutputStream.write(data);
                fileOutputStream.flush();
                fileOutputStream.close();
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }


    public static void downloadFromUrl(Context context, String DownloadUrl, String folderCategory, String fileName, int sktnum, String folderName) {
        try {
            LogUtils.d(TAG, "downloadFromUrl ");
            File root = new File(context.getExternalFilesDir(null), folderName);
            File dir = new File(root.getAbsolutePath() + "/" + folderCategory);

            if (!dir.exists()) {
                if (!dir.isDirectory()) {
                    dir.mkdirs();
                }
            }

            URL url = new URL(DownloadUrl); //you can write here any link
            File file = new File(dir, fileName);

            if (file.exists()) {
                LogUtils.e(TAG, "already exist");
                return;
            }

           /* Open a connection to that URL. */
            URLConnection ucon = url.openConnection();
           /*
            * Define InputStreams to read from the URLConnection.
            */
            InputStream is = ucon.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);
            FileOutputStream fos = new FileOutputStream(file);
            int current = 0;
            while ((current = bis.read()) != -1) {
                fos.write(current);
            }
            fos.flush();
            fos.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean checkStickyExist(String folderName, String folderNameStorage, int numberStickyInCatServer) {
        String pathSticky = ChatUtils.getFolderSaveSticky(AppController.getAppContext(), folderNameStorage, folderName);

        LogUtils.d(TAG, "checkStickyExist " + pathSticky);

        File folderStickyCat = new File(pathSticky);
        if (folderStickyCat.exists()) {
            int numberStickyStorage = folderStickyCat.list().length; // 3 : thumbnail folder , file index, image description
            if (numberStickyInCatServer == numberStickyStorage) {
                Log.e(TAG, "Same number sticky, don't need download");
                return true;
            }
        }
        return false;
    }

    // load gif from folder
    public static ArrayList<StickerCategory> getEmojiInternal(Context context, String namefolder) {
        String pathEmoji = new File(context.getExternalFilesDir(null), namefolder).toString();
        ArrayList<StickerCategory> lstStickersCats = new ArrayList<>();
        File folder = new File(pathEmoji);

        if (!folder.exists()) {
            folder.mkdirs();
            try {
                folder.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        int numberFolderSticker = folder.listFiles().length;
        // number folder sticker by type
        LogUtils.e(TAG, "numberFolderSticker " + numberFolderSticker);

        for (int i = 0; i < numberFolderSticker; i++) {
            ArrayList<Sticker> stickers = new ArrayList<>();
            File folderSticker = folder.listFiles()[i];
            int numberStickerInFolder = folderSticker.listFiles().length;

            if (numberStickerInFolder == 0) return null;

            for (int n = 0; n < numberStickerInFolder; n++) {
                File sticker = folderSticker.listFiles()[n]; // ---> sticker
                stickers.add(new Sticker(sticker.getAbsolutePath()));
            }
            StickerCategory stickerCat = new StickerCategory(stickers.get(0), stickers);
            lstStickersCats.add(stickerCat);
        }

        return lstStickersCats;
    }

    public static void setStickerData(ArrayList<StickerCategory> stickerData) {
        mStickerData = stickerData;
    }

    public static ArrayList<StickerCategory> getStickerData() {
        return mStickerData;
    }

    public static ArrayList<Sticker> getAudioInternal(Context context, String namefolder) {
        String pathAudio = new File(context.getExternalFilesDir(null), namefolder).toString();
        File folder = new File(pathAudio);
        if (folder.exists()) {

            ArrayList<Sticker> audio = new ArrayList<>();

            File folderSticker = folder.listFiles()[0];
            int number = folderSticker.listFiles().length;
            for (int i = 0; i < number; i++) {
                File sticker = folderSticker.listFiles()[i]; // ---> sticker
                audio.add(new Sticker(sticker.getAbsolutePath() + ".wav"));
            }
            return audio;
        }
        return null;

    }

    public static boolean checkEmojiExistsInString(Context context, String content) {
        boolean b = false;
        DatabaseHelper databaseHelper = new DatabaseHelper(context);

        for (int i = 0; i < databaseHelper.getAllEmoji().size(); i++) {
            if (content.contains(databaseHelper.getAllEmoji().get(i).getCode())) {
                b = true;
            } else {
                b = false;
            }
            if (b) {
                return b;
            }
        }
        return b;
    }

    /**
     * hihe keyboard
     */
    public static void hideKeyboard(Context context, View view) {
        InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        assert inputManager != null;
        inputManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /**
     * show key board
     */
    public static void showKeyboard(Context context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        assert imm != null;
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    /**
     * check duration video
     *
     * @param path audio/video file path
     * @return
     */
    public static String checkTimeVideo(String path) {
        String finalTimerString = "";
        String secondsString;
        MediaMetadataRetriever retriever = null;
        try {
            retriever = new MediaMetadataRetriever();
            retriever.setDataSource(path);
            String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            long timeInmillisec = Long.parseLong(time);

            int minutes = Math.round((timeInmillisec % (1000 * 60 * 60)) / (1000f * 60));
            int seconds = Math.round((timeInmillisec % (1000 * 60 * 60)) % (1000 * 60) / 1000f);

            LogUtils.e(TAG, "seconds " + seconds);

            if (seconds < 10) {
                secondsString = "0" + seconds;
            } else {
                secondsString = "" + seconds;
            }

            finalTimerString = finalTimerString + minutes + ":" + secondsString;

            LogUtils.e(TAG, "finalTimerString:" + finalTimerString);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (retriever != null)
                retriever.release();
        }
        return finalTimerString;
    }


    // send email
    public static void sendEmail(Context context, String email) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_EMAIL, email);
        intent.putExtra(Intent.EXTRA_SUBJECT, R.string.email_subject);
        intent.putExtra(Intent.EXTRA_TEXT, R.string.email_content);
        context.startActivity(Intent.createChooser(intent, "Send Email"));
    }

    // click web link
    public static void webLink(Context context, String link) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        try {
            i.setData(Uri.parse(link));
            context.startActivity(i);
        } catch (ActivityNotFoundException e) {
            i.setData(Uri.parse("http://" + link));
            context.startActivity(i);
        }
    }

    // get file size
    public static double getFileSize(String path) {
        File file = new File(path);
        double length = file.length();
        length = length * 1.0f / 1024;
        return length;
    }

    // time recording
    public static String formatSeconds(int seconds) {
        return getTwoDecimalsValue(seconds / 60) + ":"
                + getTwoDecimalsValue(seconds % 60);
    }

    private static String getTwoDecimalsValue(int value) {
        if (value >= 0 && value <= 9) {
            return "0" + value;
        } else {
            return value + "";
        }
    }

    /**
     * @param path to get duration
     * @return duration of video|audio, -1 if error
     */
    public static int getDurationFile(String path) {
        MediaMetadataRetriever retriever = null;
        try {
            retriever = new MediaMetadataRetriever();
            retriever.setDataSource(path);
            String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            LogUtils.e(TAG, "getDurationFile:" + time);
            return Math.round(Long.parseLong(time) / 1000f);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (retriever != null)
                retriever.release();
        }
        return 0;
    }

    /**
     * @param url to download
     * @return emoji file base on url download  /emoji/categoryId/name.gif
     */
    public static File predicateEmojiFile(Context context, String url) {
        // create folder Emoji struct: /emoji/categoryId/emojiName.png
        File emojiContainer = createFolder(context.getApplicationContext().getExternalFilesDir(null), ChatUtils.EMOJI);

        // get emoji name, category from url
        int slashEmojiName = url.lastIndexOf("/");
        int slashEmojiCategory = url.lastIndexOf("/", slashEmojiName - 1);
        // emoji file name
        String currentEmojiName = url.substring(slashEmojiName + 1);
        // create emoji folder
        String category = url.substring(slashEmojiCategory + 1, slashEmojiName);
        File categoryFile = createFolder(emojiContainer, category);
        return new File(categoryFile, currentEmojiName);
    }

    /**
     * create folder if need
     *
     * @param parent file parent
     * @param name   folder name
     */
    private static File createFolder(File parent, String name) {
        File dir = new File(parent, name);

        if (!dir.exists() || !dir.isDirectory()) {
            dir.mkdirs();
        }
        return dir;
    }

}
