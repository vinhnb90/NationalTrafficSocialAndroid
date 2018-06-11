package com.vn.ntsc.utils.upload;

import com.vn.ntsc.repository.model.mediafile.MediaFileBean;
import com.vn.ntsc.repository.model.poststatus.uploadsetting.UploadSettingBean;
import com.vn.ntsc.ui.posts.PostStatusActivity;

import java.io.File;
import java.util.Map;

/**
 * Created by robert on 10/25/17.
 */
public class UploadFileValidator {
    private final String TAG = "UploadFileValidator";

    public interface Toast {
        // Call when detected file not exists via file path
        void fileNotExists();

        //Maximum file size allowed - Exceeded file size allowed
        void overFileSizeAllowed(int type);

        //Maximum total size allowed - Exceeded total size allowed
        void overTotalSizeAllowed();

        //Maximum total size allowed - Exceeded total file allowed for per time upload
        void overTotalFileAllowed(int type);

        //Validate successful and ready to use to upload
        void canUpload(MediaFileBean mediaFile, int fileSizeInMB, int position);
    }

    //Validate successful and ready to use to upload
    public void onValidate(Toast listener, MediaFileBean mediaFile, int position, UploadSettingBean uploadSetting, Map<String, Long> uploadPickedInfo) {

        final File file = new File(mediaFile.mediaUri);

        if (file == null || !file.exists()) {
            listener.fileNotExists();
            return;
        }

        long fileSizeInKB = file.length()/1024;
        long fileSizeInMB = fileSizeInKB/1024;

        //Over file size allowed
        if (fileSizeInKB > uploadSetting.maxFileSize) {
            listener.overFileSizeAllowed(mediaFile.mediaType);
            return;
        }

        //Over total file size allowed
        if (uploadPickedInfo.get(PostStatusActivity.MEDIA_PICKED_TOTAL_SIZE) >= uploadSetting.totalFileSize) {
            listener.overTotalSizeAllowed();
            return;
        }

        //Over total file allowed
        if (uploadPickedInfo.get(PostStatusActivity.MEDIA_PICKED_TOTAL_FILE) >= uploadSetting.totalFileUpload) {
            listener.overTotalFileAllowed(MediaFileBean.NONE);
            return;
        }

        //Over total audio file allowed
        if (mediaFile.mediaType == MediaFileBean.AUDIO && uploadPickedInfo.get(PostStatusActivity.AUDIO_PICKED_TOTAL_FILE) >= uploadSetting.maxAudioNumber) {
            listener.overTotalFileAllowed(mediaFile.mediaType);
            return;
        }

        //Over total image file allowed
        if (mediaFile.mediaType == MediaFileBean.IMAGE && uploadPickedInfo.get(PostStatusActivity.IMAGE_PICKED_TOTAL_FILE) >= uploadSetting.maxImageNumber) {
            listener.overTotalFileAllowed(mediaFile.mediaType);
            return;
        }

        //Over total video file allowed
        if (mediaFile.mediaType == MediaFileBean.VIDEO && uploadPickedInfo.get(PostStatusActivity.VIDEO_PICKED_TOTAL_FILE) >= uploadSetting.maxVideoNumber) {
            listener.overTotalFileAllowed(mediaFile.mediaType);
            return;
        }
        //passed thought all constraint
        listener.canUpload(mediaFile, (int)fileSizeInKB, position);
    }

}
