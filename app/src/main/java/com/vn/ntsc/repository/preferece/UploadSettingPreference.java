package com.vn.ntsc.repository.preferece;

import com.vn.ntsc.repository.model.poststatus.uploadsetting.UploadSettingBean;

/**
 * Created by nankai on 12/25/2017.
 *
 * Get settings for downloading files to the server from the backend
 */
public class UploadSettingPreference extends BasePrefers {

    public static final String KEY_MAX_AUDIO_NUMBER = "key.max.audio.number";
    public static final String KEY_TOTAL_FILE_SIZE = "key.total.file.size";
    public static final String KEY_MAX_VIDEO_NUMBER = "key.max.video.number";
    public static final String KEY_DEFAULT_AUDIO_IMAGE = "key.default.audio.image";
    public static final String KEY_MAX_FILE_SIZE = "key.max.file.size";
    public static final String KEY_MAX_IMAGE_NUMBER = "key.max.image.number";
    public static final String KEY_MAX_LENGTH_NUMBER = "key.max.length.number";
    public static final String KEY_MAX_FILE_PER_ALBUM = "key.max.file.per.album";
    public static final String KEY_TOTAL_FILE_UPLOAD = "key.total.file.upload";

    public UploadSettingPreference() {
        super();
    }

    private static UploadSettingPreference instance;

    public static synchronized UploadSettingPreference getInstance() {
        if (instance == null) {
            instance = new UploadSettingPreference();
        }
        return instance;
    }

    @Override
    protected String getFileNamePrefers() {
        return "UploadSettingPreference";
    }

    public void setUploadSetting(UploadSettingBean uploadSeting) {
        saveMaxAudioNumber(uploadSeting.maxAudioNumber);
        saveTotalFileSize(uploadSeting.totalFileSize);
        saveMaxVideoNumber(uploadSeting.maxVideoNumber);
        saveDefaultAudioImg(uploadSeting.defaultAudioImg);
        saveMaxFileSize(uploadSeting.maxFileSize);
        saveMaxImageNumber(uploadSeting.maxImageNumber);
        saveMaxCommentLengthAllowed(uploadSeting.maxCommentLengthAllowed);
        saveMaxFilePerAlbum(uploadSeting.maxFilePerAlbum);
        saveTotalFileUpload(uploadSeting.totalFileUpload);
    }

    public UploadSettingBean getUploadSetting(){
            return new UploadSettingBean(getMaxAudioNumber(),getTotalFileSize()
                    ,getMaxVideoNumber(),getDefaultAudioImg()
                    ,getMaxFileSize(),getMaxImageNumber()
                    ,getMaxCommentLengthAllowed(),getMaxFilePerAlbum()
                    ,getTotalFileUpload());
    }

    //-------------- max_audio_number --------------------
    public void saveMaxAudioNumber(int value) {
        getEditor().putInt(KEY_MAX_AUDIO_NUMBER, value).commit();
    }

    public int getMaxAudioNumber() {
        return getPreferences().getInt(KEY_MAX_AUDIO_NUMBER, 20);
    }

    //-------------- total_file_size --------------------
    public void saveTotalFileSize(int value) {
        getEditor().putInt(KEY_TOTAL_FILE_SIZE, value).commit();
    }

    public int getTotalFileSize() {
        return getPreferences().getInt(KEY_TOTAL_FILE_SIZE, 512000);//500MB
    }

    //-------------- max_video_number --------------------
    public void saveMaxVideoNumber(int value) {
        getEditor().putInt(KEY_MAX_VIDEO_NUMBER, value).commit();
    }

    public int getMaxVideoNumber() {
        return getPreferences().getInt(KEY_MAX_VIDEO_NUMBER, 20);
    }

    //-------------- default_audio_img --------------------
    public void saveDefaultAudioImg(String value) {
        getEditor().putString(KEY_DEFAULT_AUDIO_IMAGE, value).commit();
    }

    public String getDefaultAudioImg() {
        return getPreferences().getString(KEY_DEFAULT_AUDIO_IMAGE, "");
    }

    //-------------- max_file_size --------------------
    public void saveMaxFileSize(int value) {
        getEditor().putInt(KEY_MAX_FILE_SIZE, value).commit();
    }

    public int getMaxFileSize() {
        return getPreferences().getInt(KEY_MAX_FILE_SIZE, 102400);//100MB
    }

    //-------------- max_image_number --------------------
    public void saveMaxImageNumber(int value) {
        getEditor().putInt(KEY_MAX_IMAGE_NUMBER, value).commit();
    }

    public int getMaxImageNumber() {
        return getPreferences().getInt(KEY_MAX_IMAGE_NUMBER, 20);
    }

    //-------------- max_length_buzz --------------------
    public void saveMaxCommentLengthAllowed(int value) {
        getEditor().putInt(KEY_MAX_LENGTH_NUMBER, value).commit();
    }

    public int getMaxCommentLengthAllowed() {
        return getPreferences().getInt(KEY_MAX_LENGTH_NUMBER, 60000);
    }

    //-------------- total_file_upload --------------------
    public void saveTotalFileUpload(int value) {
        getEditor().putInt(KEY_TOTAL_FILE_UPLOAD, value).commit();
    }

    public int getTotalFileUpload() {
        return getPreferences().getInt(KEY_TOTAL_FILE_UPLOAD, 20);
    }

    //-------------- max_file_per_album --------------------
    public void saveMaxFilePerAlbum(int value) {
        getEditor().putInt(KEY_MAX_FILE_PER_ALBUM, value).commit();
    }

    public int getMaxFilePerAlbum() {
        return getPreferences().getInt(KEY_MAX_FILE_PER_ALBUM, 30);
    }
}
