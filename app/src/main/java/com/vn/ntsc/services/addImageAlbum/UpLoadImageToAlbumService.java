package com.vn.ntsc.services.addImageAlbum;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.vn.ntsc.BuildConfig;
import com.vn.ntsc.R;
import com.vn.ntsc.di.modules.MediaModule;
import com.vn.ntsc.repository.model.mediafile.MediaFileBean;
import com.vn.ntsc.repository.model.myalbum.AddAlbum.AddAlbumRequest;
import com.vn.ntsc.repository.model.myalbum.AddAlbum.AddAlbumResponse;
import com.vn.ntsc.repository.model.myalbum.AddImageToAlbum.AddImageAlbumRequest;
import com.vn.ntsc.repository.model.myalbum.AddImageToAlbum.AddImageAlbumResponse;
import com.vn.ntsc.repository.model.myalbum.LoadAlbum.LoadAlbumResponse;
import com.vn.ntsc.repository.preferece.NotificationSettingPreference;
import com.vn.ntsc.repository.remote.ApiMediaService;
import com.vn.ntsc.repository.remote.ApiService;
import com.vn.ntsc.services.BaseIntentService;
import com.vn.ntsc.utils.Constants;
import com.vn.ntsc.utils.LogUtils;
import com.vn.ntsc.utils.notify.NotificationUtils;
import com.vn.ntsc.widget.eventbus.RxEventBus;
import com.vn.ntsc.widget.eventbus.SubjectCode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Created by ThoNh on 12/20/2017.
 */

public class UpLoadImageToAlbumService extends BaseIntentService {
    private static final String TAG = UpLoadImageToAlbumService.class.getSimpleName();
    public static final String EXTRA_TOKEN = "EXTRA_TOKEN";
    public static final String EXTRA_IMAGES = "EXTRA_IMAGES";
    public static final String EXTRA_ITEM_ALBUM = "EXTRA_ITEM_ALBUM";
    public static final String EXTRA_KEY_ALBUM_ERROR = "EXTRA_KEY_ALBUM_ERROR";
    public static final String EXTRA_KEY_IMAGE_UPLOAD_FAIL = "EXTRA_KEY_IMAGE_UPLOAD_FAIL";

    @Inject
    ApiService mApiService;

    @Inject
    ApiMediaService mApiMediaService;

    private NotificationManager notificationMgr;
    private NotificationCompat.Builder notification;

    private String mToken;
    private List<MediaFileBean> mImages;
    private List<LoadAlbumResponse.DataBean> mQueue = new ArrayList<>();

    public UpLoadImageToAlbumService() {
        super("UpLoadImageToAlbumService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        getServiceMediaComponent(new MediaModule(BuildConfig.MEDIA_SERVER, 280, 480,
                4800, false)).inject(this);
        notificationMgr = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notification = NotificationUtils.createNotification(getApplicationContext(), R.string.upload_album_waiting, TAG);
        if (NotificationSettingPreference.getInstance().isSound())
            notification.setSound(Uri.parse("android.resource://" + getApplicationContext().getPackageName() + File.separator + R.raw.notice));
        Notification notify = notification.build();
        notify.flags |= Notification.FLAG_AUTO_CANCEL;
        notificationMgr.notify(Constants.NOTI_STATUS_ID, notify);
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        if (intent != null) {

            // Call when Create New Album or Add more image to album existed
            // When create new album                --> albumId = null      --> add null to array
            // When add more image to exist album   --> albumId != null     --> add albumId to Array
            LoadAlbumResponse.DataBean bean = intent.getParcelableExtra(EXTRA_ITEM_ALBUM);
            bean.isUploading = true;
            mQueue.add(bean);

            // create new album
            if (bean.albumId == null || bean.albumId.isEmpty()) {
                bean.isCreateNew = true;
                RxEventBus.publish(SubjectCode.SUBJECT_CREATE_NEW_ALBUM, bean);
            }
        }

        RxEventBus.subscribe(SubjectCode.SUBJECT_REQUEST_CHECK_UP_LOAD_ALBUM, this, new Consumer<Object>() {
            @Override
            public void accept(Object o) throws Exception {
                List<LoadAlbumResponse.DataBean> mData = new ArrayList<>();
                for (LoadAlbumResponse.DataBean data : mQueue) {
                    if (data.albumId != null) {
                        mData.add(data);
                    }
                }
                RxEventBus.publish(SubjectCode.SUBJECT_RESPONSE_CHECK_UP_LOAD_ALBUM, mData);
            }
        });

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        if (intent != null) {
            mToken = intent.getStringExtra(EXTRA_TOKEN);
            mImages = intent.getParcelableArrayListExtra(EXTRA_IMAGES);
        }

        // AlbumID not exist --> create New Album & up images to it
        if (null == mQueue.get(0).albumId || mQueue.get(0).albumId.isEmpty()) {

            AddAlbumRequest request = new AddAlbumRequest(mToken, mQueue.get(0).albumName, mQueue.get(0).albumDes, mQueue.get(0).privacy);

            notificationMgr.notify(Constants.NOTI_STATUS_ID, notification.setContentText(getResources().getString(R.string.upload_album_uploading) + mQueue.get(0).albumName).build());

            createNewAlbum(request);

        } else {// AlbumId exist --> up images to this album
            uploadImagesToAlbum(mImages, mQueue.get(0).albumId);
        }

    }


    private void createNewAlbum(final AddAlbumRequest request) {
        mApiService.addAlbum(request)
                .subscribe(new Observer<AddAlbumResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(AddAlbumResponse response) {
                        // Create new album success --> put images to this album
                        mQueue.get(0).albumId = response.data.albumId;
                        uploadImagesToAlbum(mImages, response.data.albumId);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        notification.setContentTitle(getResources().getString(R.string.common_error));
                        notificationMgr.notify(Constants.NOTI_STATUS_ID, notification.setContentText(getResources().getString(R.string.upload_album_create_error) + mQueue.get(0).albumName).build());

                        if (mQueue.get(0).isCreateNew) {
                            //detect action fire of createNewAlbum error
                            Bundle bundle = new Bundle();
                            bundle.putParcelable(EXTRA_KEY_ALBUM_ERROR, mQueue.get(0));
                            RxEventBus.publish(SubjectCode.SUBJECT_ALBUM_ERROR, bundle);

                            mQueue.remove(0);
                        }
                    }

                    @Override
                    public void onComplete() {
                        LogUtils.e(TAG, "createNewAlbum -> onCompleted ->");
                    }
                });

    }


    private void uploadImagesToAlbum(List<MediaFileBean> images, String albumId) {
        notification.setContentTitle(getResources().getString(R.string.upload_album_start_add_image));
        notificationMgr.notify(Constants.NOTI_STATUS_ID, notification.setContentText("").build());
        final AddImageAlbumRequest request = new AddImageAlbumRequest(images, mToken, albumId);

        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);

        for (int i = 0; i < request.imagePaths.size(); i++) {
            File file = new File(request.imagePaths.get(i).mediaUri);
            RequestBody fileBody = RequestBody.create(MediaType.parse("image/*"), file);
            builder.addFormDataPart("files", file.getName(), fileBody);
        }

        builder.addFormDataPart("token", request.token);
        builder.addFormDataPart("album_id", request.albumId);
        builder.addFormDataPart("api", request.api);


        mApiMediaService.addImageToAlbum(builder.build())
                .subscribe(new Observer<AddImageAlbumResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(AddImageAlbumResponse response) {


                        if (response.data != null) {
                            RxEventBus.publish(SubjectCode.SUBJECT_UPLOAD_IMAGE_SUCCESS, response.data);
                            notification.setContentTitle(getResources().getString(R.string.upload_album_upload_image_success));
                            notificationMgr.notify(Constants.NOTI_STATUS_ID, notification.setContentText(getResources().getString(R.string.upload_album_success_at) + mQueue.get(0).albumName).build());
                        } else {
                            notification.setContentTitle(getResources().getString(R.string.upload_album_upload_image_error));
                            notificationMgr.notify(Constants.NOTI_STATUS_ID, notification.setContentText("").build());
                            RxEventBus.publish(SubjectCode.SUBJECT_UPLOAD_IMAGE_FAILURE, mQueue.get(0));
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        notification.setContentTitle(getResources().getString(R.string.upload_album_upload_image_error));
                        notificationMgr.notify(Constants.NOTI_STATUS_ID, notification.setContentText(getResources().getString(R.string.upload_album_noun) + mQueue.get(0).albumName + getResources().getString(R.string.upload_album_can_not_upload)).build());

                        //detect action fire of upload image error
                        Bundle bundle = new Bundle();
                        bundle.putParcelable(EXTRA_KEY_IMAGE_UPLOAD_FAIL, mQueue.get(0));
                        RxEventBus.publish(SubjectCode.SUBJECT_ALBUM_ERROR, bundle);

                        mQueue.clear();
                    }

                    @Override
                    public void onComplete() {
                        mQueue.remove(0);
                    }
                });
    }
}
