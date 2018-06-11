package com.vn.ntsc.ui.mediadetail.album;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.text.format.DateFormat;
import android.util.Log;

import com.vn.ntsc.core.BasePresenter;
import com.vn.ntsc.core.callback.SubscriberCallback;
import com.vn.ntsc.core.model.ServerResponse;
import com.vn.ntsc.repository.model.myalbum.DeleteImageInAlbum.DelAlbumImageRequest;
import com.vn.ntsc.repository.model.myalbum.DeleteImageInAlbum.DelAlbumImageResponse;
import com.vn.ntsc.repository.model.myalbum.ItemImageInAlbum;
import com.vn.ntsc.repository.model.report.ReportRequest;
import com.vn.ntsc.repository.model.report.ReportResponse;
import com.vn.ntsc.utils.Constants;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

import static android.os.Environment.DIRECTORY_PICTURES;

/**
 * Created by ThoNh on 1/9/2018.
 */

public class AlbumDetailMediaPresenter extends BasePresenter<AlbumDetailMediaContract.View> implements AlbumDetailMediaContract.Presenter {

    @Inject
    public AlbumDetailMediaPresenter() {

    }

    @Override
    public void deleteImagesInAlbum(final DelAlbumImageRequest request) {
        addSubscriber(apiService.deleteImagesInAlbum(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new SubscriberCallback<DelAlbumImageResponse>(view) {
                    @Override
                    public void onSuccess(DelAlbumImageResponse response) {
                        if (response.code == ServerResponse.DefinitionCode.SERVER_SUCCESS) {
                            view.deleteImagesInAlbumSuccess(response);
                        } else {
                            view.deleteImagesInAlbumFailure();
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        view.deleteImagesInAlbumFailure();
                    }

                    @Override
                    public void onCompleted() {
                        view.deleteImagesInAlbumComplete();
                    }
                }));
    }

    @Override
    public void reportImage(ReportRequest request) {
        addSubscriber(apiService.reportUser(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .filter(new Predicate<ReportResponse>() {
                    @Override
                    public boolean test(@NonNull ReportResponse reportResponse) throws Exception {
                        if (reportResponse.code != ServerResponse.DefinitionCode.SERVER_SUCCESS) {
                            view.reportFailure();
                            return false;
                        }

                        return true;
                    }
                })
                .subscribeWith(new SubscriberCallback<ReportResponse>(view) {
                    @Override
                    public void onSuccess(ReportResponse response) {
                        view.reportSuccess();
                    }

                    @Override
                    public void onCompleted() {

                    }
                }));
    }

    @Override
    public void saveImage(final Context context, final ItemImageInAlbum album) {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                try {
                    File fileToSave = createFileToSave();

                    InputStream inputStream = null;
                    inputStream = new URL(album.originalUrl).openStream();
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    File fileImage = new File(fileToSave.getAbsolutePath());
                    fileImage.createNewFile();
                    FileOutputStream fOut = new FileOutputStream(fileImage.getAbsolutePath());
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                    fOut.getFD().sync();
                    fOut.close();
                    if (fileImage.exists()) {
                        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2) {
                            MediaScannerConnection.scanFile(context, new String[]{fileImage.toString()}, null, new MediaScannerConnection.OnScanCompletedListener() {
                                public void onScanCompleted(String path, Uri uri) {
                                    Log.i("ExternalStorage", "Scanned " + path + ":");
                                    Log.i("ExternalStorage", "-> uri=" + uri);
                                }
                            });
                            Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
                            mediaScanIntent.setData(Uri.fromFile(fileImage));
                            context.sendBroadcast(mediaScanIntent);
                        } else {
                            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory())));
                        }
                        e.onNext(fileToSave.getAbsolutePath());
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
                        view.saveImageSuccess();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        view.saveImageFailure();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private File createFileToSave() {
        try {
            File filePath;
            String parentPath = Environment.getExternalStorageDirectory()
                    + File.separator + DIRECTORY_PICTURES + File.separator + Constants.FOLDER_SAVE_FILE;
            String prefix = "photo_";
            String suffix = ".jpg";

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
}
