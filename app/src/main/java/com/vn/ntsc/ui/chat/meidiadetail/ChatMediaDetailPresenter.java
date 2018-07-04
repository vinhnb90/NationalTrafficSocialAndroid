package com.vn.ntsc.ui.chat.meidiadetail;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.Toast;

import com.vn.ntsc.R;
import com.vn.ntsc.core.BasePresenter;
import com.vn.ntsc.repository.TypeView;
import com.vn.ntsc.repository.model.chat.ItemFileChat;
import com.vn.ntsc.repository.remote.ApiMediaService;
import com.vn.ntsc.utils.Constants;
import com.vn.ntsc.utils.LogUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.os.Environment.DIRECTORY_MOVIES;
import static android.os.Environment.DIRECTORY_MUSIC;
import static android.os.Environment.DIRECTORY_PICTURES;

/**
 * Created by Doremon on 3/5/2018.
 */

public class ChatMediaDetailPresenter extends BasePresenter<ChatMediaDetailContract.View> implements ChatMediaDetailContract.Presenter {

    private int notificationId = 0x00001;

    @Inject
    ApiMediaService apiMediaService;

    @Inject
    public ChatMediaDetailPresenter() {

    }

    @Override
    public void saveMedia(Context context, ItemFileChat child) {
        File fileToSave = createFileToSave(child);

        if (child.type.equals(TypeView.MediaDetailType.IMAGE_TYPE)) {
            downloadImage(context, fileToSave, child);
            return;
        }

        if (child.type.equals(TypeView.MediaDetailType.VIDEO_TYPE)
                || child.type.equals(TypeView.MediaDetailType.AUDIO_TYPE)) {
            downloadFile(context, fileToSave, child);
        }
    }


    private File createFileToSave(final ItemFileChat child) {
        try {
            File filePath;
            String parentPath = "";
            String prefix = "";
            String suffix = "";

            switch (child.type) {
                case TypeView.MediaDetailType.IMAGE_TYPE:
                    parentPath = Environment.getExternalStorageDirectory()
                            + File.separator + DIRECTORY_PICTURES + File.separator + Constants.FOLDER_SAVE_FILE;
                    prefix = "photo_";
                    suffix = ".jpg";
                    break;

                case TypeView.MediaDetailType.AUDIO_TYPE:
                    parentPath = Environment.getExternalStorageDirectory()
                            + File.separator + DIRECTORY_MUSIC + File.separator + Constants.FOLDER_SAVE_FILE;
                    prefix = "audio_";
                    suffix = ".mp3";
                    break;

                case TypeView.MediaDetailType.VIDEO_TYPE:
                    parentPath = Environment.getExternalStorageDirectory()
                            + File.separator + DIRECTORY_MOVIES + File.separator + Constants.FOLDER_SAVE_FILE;
                    prefix = "video_";
                    suffix = ".mp4";
                    break;
            }

            File file = new File(parentPath);
            file.mkdir();
            file.createNewFile();

            DateFormat df = new DateFormat();
            filePath = new File(parentPath, prefix + df.format("yyyy-MM-dd_hh-mm-ss", new java.util.Date()) + suffix);
            filePath.createNewFile();
            return filePath;
        } catch (IOException e) {
            e.printStackTrace();
            return new File("");
        }
    }

    private void downloadImage(final Context context, final File file, final ItemFileChat child) {

        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                try {
                    InputStream inputStream = null;
                    inputStream = new URL(child.originalUrl).openStream();
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    File fileImage = new File(file.getAbsolutePath());
                    fileImage.createNewFile();
                    FileOutputStream fOut = new FileOutputStream(fileImage.getAbsolutePath());
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                    fOut.getFD().sync();
                    fOut.close();
                    if (fileImage.exists()) {
                        MediaScannerConnection.scanFile(context, new String[]{fileImage.toString()}, null, new MediaScannerConnection.OnScanCompletedListener() {
                            public void onScanCompleted(String path, Uri uri) {
                                Log.i("ExternalStorage", "Scanned " + path + ":");
                                Log.i("ExternalStorage", "-> uri=" + uri);
                            }
                        });
                        Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
                        mediaScanIntent.setData(Uri.fromFile(fileImage));
                        context.sendBroadcast(mediaScanIntent);
                        e.onNext(file.getAbsolutePath());
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull String o) {
                        if (o.isEmpty()) return;
                        LogUtils.e("saveImageFailure", "----------> " + view);
                        if (view != null) {
                            view.saveImageSuccess();
                        } else {
                            Toast.makeText(context, R.string.save_image_success, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        if (view != null) {
                            view.saveImageFailure();
                        } else {
                            Toast.makeText(context, R.string.save_image_failure, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void downloadFile(final Context context, final File fileToSave, final ItemFileChat child) {
        Call<ResponseBody> requestBodyCall = apiMediaService.downloadFile(child.fileUrl);
        requestBodyCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@android.support.annotation.NonNull Call<ResponseBody> call, @android.support.annotation.NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {

                    boolean writtenToDisk = writeFile(context, fileToSave, response.body());
                    if (writtenToDisk) {
                        if (view != null) {
                            view.downloadComplete(fileToSave);
                        } else {
                            Toast.makeText(context, R.string.save_image_success, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        if (view != null) {
                            view.downloadError();
                        } else {
                            Toast.makeText(context, R.string.download_error, Toast.LENGTH_SHORT).show();
                        }

                    }
                } else {
                    if (view != null) {
                        view.onComplete();
                    }
                }
            }

            @Override
            public void onFailure(@android.support.annotation.NonNull Call<ResponseBody> call, @android.support.annotation.NonNull Throwable t) {
                t.printStackTrace();
                if (view != null) {
                    view.downloadError();
                } else {
                    Toast.makeText(context, R.string.download_error, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean writeFile(Context context, File fileToWrite, ResponseBody body) {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            showNotificationDownload(context);
            byte[] fileReader = new byte[4096];
            long fileSizeDownloaded = 0;

            inputStream = body.byteStream();
            try {
                outputStream = new FileOutputStream(fileToWrite);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            while (true) {
                int read = inputStream.read(fileReader);

                if (read == -1) {
                    break;
                }

                outputStream.write(fileReader, 0, read);

                fileSizeDownloaded += read;

            }

            outputStream.flush();

            if (fileToWrite.exists()) {
                MediaScannerConnection.scanFile(context, new String[]{fileToWrite.toString()}, null, new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        Log.i("ExternalStorage", "Scanned " + path + ":");
                        Log.i("ExternalStorage", "-> uri=" + uri);
                    }
                });
                Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
                mediaScanIntent.setData(Uri.fromFile(fileToWrite));
                context.sendBroadcast(mediaScanIntent);
            }

            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            hideNotificationDownload(context);
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


    }

    private void showNotificationDownload(Context context) {
        NotificationManager notifyManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "MediaDetail");
        builder.setContentTitle("Downloading...")
                .setContentText("Download in ic_progress")
                .setSmallIcon(android.R.drawable.stat_sys_download);
        builder.setProgress(0, 0, true);
        if (notifyManager != null)
            notifyManager.notify(notificationId, builder.build());
    }

    private void hideNotificationDownload(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null)
            notificationManager.cancel(notificationId);
    }

}
