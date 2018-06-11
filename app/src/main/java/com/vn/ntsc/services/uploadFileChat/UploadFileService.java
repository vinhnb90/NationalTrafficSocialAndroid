package com.vn.ntsc.services.uploadFileChat;

import android.content.Intent;
import android.support.annotation.Nullable;

import com.tux.socket.models.Media;
import com.vn.ntsc.BuildConfig;
import com.vn.ntsc.di.modules.MediaModule;
import com.vn.ntsc.repository.model.chat.ChatMessage;
import com.vn.ntsc.repository.model.chat.UploadFileResponse;
import com.vn.ntsc.repository.preferece.UserPreferences;
import com.vn.ntsc.repository.remote.ApiMediaService;
import com.vn.ntsc.services.BaseIntentService;
import com.vn.ntsc.services.upload.ProgressRequestBody;
import com.vn.ntsc.utils.LogUtils;
import com.vn.ntsc.widget.eventbus.RxEventBus;
import com.vn.ntsc.widget.eventbus.SubjectCode;

import java.io.File;

import javax.inject.Inject;

import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Created by ducng on 11/21/2017.
 */

public class UploadFileService extends BaseIntentService implements ProgressRequestBody.UploadCallbacks {
    public static final String TAG = UploadFileService.class.getSimpleName();
    public static final String INPUT = "uploadFile_service";
    public static final String TYPE = "type";

    @Inject
    ApiMediaService mApiMediaService;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */

    @Inject
    public UploadFileService() {
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        getServiceMediaComponent(
                new MediaModule(BuildConfig.MEDIA_SERVER,
                        280,
                        480,
                        4800
                        , false)).inject(this);
    }

    @Override
    protected void onHandleIntent(@Nullable final Intent intent) {
        if (intent != null) {
            final ChatMessage message = intent.getParcelableExtra(INPUT);

            MultipartBody.Builder builder = new MultipartBody.Builder();
            builder.setType(MultipartBody.FORM);
            builder.addFormDataPart("token", UserPreferences.getInstance().getToken());
            builder.addFormDataPart("api", "upl_file");

            // will add multiple file in a message , loop message.tempFile
            if (message == null) return;
            if (message.listFile == null) return;

            for (Media.FileBean fileBean : message.listFile) {
                MediaType mediaType = null;
                String type = fileBean.fileType;
                String filePath = null;
                switch (type) {
                    case Media.FileBean.FILE_TYPE_IMAGE:
                        mediaType = MediaType.parse("image/*");
                        filePath = fileBean.originalUrl;
                        break;
                    case Media.FileBean.FILE_TYPE_VIDEO:
                        mediaType = MediaType.parse("video/*");
                        filePath = fileBean.fileUrl;
                        break;
                    case Media.FileBean.FILE_TYPE_AUDIO:
                        mediaType = MediaType.parse("audio/*");
                        filePath = fileBean.fileUrl;
                        break;
                }

                assert filePath != null;
                File file = new File(filePath);

                RequestBody fileReqBody = RequestBody.create(mediaType, file);
                ProgressRequestBody fileBody = new ProgressRequestBody(fileReqBody, this);
                builder.addFormDataPart("files", file.getName(), fileBody);
            }

            mApiMediaService.uploadFile(builder.build())
                    .subscribe(new Observer<UploadFileResponse>() {
                        @Override
                        public void onSubscribe(@NonNull Disposable disposable) {

                        }

                        @Override
                        public void onNext(@NonNull UploadFileResponse chatReponse) {
                            if (chatReponse.listData != null)
                                message.listFile = chatReponse.listData;

                            RxEventBus.publish(SubjectCode.SUBJECT_UPLOAD_SUCCESS, message);
                        }

                        @Override
                        public void onError(@NonNull Throwable throwable) {
                            throwable.printStackTrace();
                            LogUtils.e(TAG, "onError " + throwable.getMessage());
                            RxEventBus.publish(SubjectCode.SUBJECT_UPLOAD_FAIL, message);
                        }

                        @Override
                        public void onComplete() {
                            LogUtils.e(TAG, "onComplete  ");
                        }
                    });

        }
    }

    @Override
    public void onProgressUpdate(int percentage) {

    }

    @Override
    public void onError() {

    }

    @Override
    public void onFinish(int index) {

    }

}
