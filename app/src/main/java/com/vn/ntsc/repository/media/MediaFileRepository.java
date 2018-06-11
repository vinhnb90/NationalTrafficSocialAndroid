package com.vn.ntsc.repository.media;

import android.net.Uri;
import android.provider.MediaStore;

import com.vn.ntsc.widget.mediafile.rx.RxCursorLoader;

/**
 * Created by nankai on 4/6/2018.
 */

public class MediaFileRepository {

    public static final int AUDIO_LOADER_ID_MODE = 1952;
    public static final int IMAGE_LOADER_ID_MODE = 1983;
    public static final int VIDEO_LOADER_ID_MODE = 1988;
    public static final int MEDIA_LOADER_ID_MODE = 1115;

    private static final Uri URI_FILE = MediaStore.Files.getContentUri("external");

    /**
     * Get the content:// style URI for the image media table on the
     * given volume.
     */
    private static final Uri URI = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

    private static final String[] COLUMNS_FILE = new String[]{
            MediaStore.Files.FileColumns._ID,
            MediaStore.Files.FileColumns.DATA,
            MediaStore.Files.FileColumns.DATE_ADDED,
            MediaStore.Files.FileColumns.MEDIA_TYPE,
            MediaStore.Files.FileColumns.MIME_TYPE,
            MediaStore.Files.FileColumns.TITLE,
            MediaStore.Files.FileColumns.SIZE
    };

    private static final String[] COLUMNS_VIDEO = new String[]{
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.DATA,
            MediaStore.Video.VideoColumns.DURATION,
            MediaStore.Video.Media.DATE_ADDED,
            MediaStore.Video.Media.MIME_TYPE
    };

    private static final String[] COLUMNS_IMAGE = new String[]{
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DATA,
            MediaStore.Images.ImageColumns.ORIENTATION,
            MediaStore.Images.Media.DATE_ADDED,
            MediaStore.Images.Media.MIME_TYPE
    };

    private static final String[] COLUMNS_AUDIO = new String[]{
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.AudioColumns.DURATION,
            MediaStore.Audio.Media.DATE_ADDED,
            MediaStore.Audio.Media.MIME_TYPE
    };

    public static RxCursorLoader.Query getAudio(int limit,int offset) {
        String sortOrder;
        if (limit == 0)
            sortOrder = MediaStore.Files.FileColumns.DATE_ADDED + " DESC ";
        else {
            sortOrder = MediaStore.Files.FileColumns.DATE_ADDED + " DESC LIMIT " + limit +" OFFSET " + offset;
        }
        return new RxCursorLoader.Query.Builder()
                .setContentUri(URI)
                .setProjection(COLUMNS_AUDIO)
                .setSelection(MediaStore.Audio.AudioColumns.SIZE + " > 0 ")
                .setSortOrder(sortOrder)
                .create();
    }

    public static RxCursorLoader.Query getImage(int limit,int offset) {
        String sortOrder;
        if (limit == 0)
            sortOrder = MediaStore.Files.FileColumns.DATE_ADDED + " DESC ";
        else {
            sortOrder = MediaStore.Files.FileColumns.DATE_ADDED + " DESC LIMIT " + limit +" OFFSET " + offset;
        }
        return new RxCursorLoader.Query.Builder()
                .setContentUri(URI)
                .setProjection(COLUMNS_IMAGE)
                .setSelection(MediaStore.Images.ImageColumns.SIZE + " > 0 ")
                .setSortOrder(sortOrder)
                .create();
    }

    public static RxCursorLoader.Query getVideo(int limit,int offset) {
        String sortOrder;
        if (limit == 0)
            sortOrder = MediaStore.Files.FileColumns.DATE_ADDED + " DESC ";
        else {
            sortOrder = MediaStore.Files.FileColumns.DATE_ADDED + " DESC LIMIT " + limit +" OFFSET " + offset;
        }
        return new RxCursorLoader.Query.Builder()
                .setContentUri(URI)
                .setProjection(COLUMNS_VIDEO)
                .setSelection(MediaStore.Video.VideoColumns.SIZE + " > 0 ")
                .setSortOrder(sortOrder)
                .create();
    }

    public static RxCursorLoader.Query getMediaFile(int limit,int offset) {

        String sortOrder;
        if (limit == 0)
            sortOrder = MediaStore.Files.FileColumns.DATE_ADDED + " DESC ";
        else {
            sortOrder = MediaStore.Files.FileColumns.DATE_ADDED + " DESC LIMIT " + limit +" OFFSET " + offset;
        }

        return new RxCursorLoader.Query.Builder()
                .setContentUri(URI_FILE)
                .setProjection(COLUMNS_FILE)
                .setSelection("(" + MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                        + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
                        + " OR "
                        + MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                        + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO
                        + " OR "
                        + MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                        + MediaStore.Files.FileColumns.MEDIA_TYPE_AUDIO + ") AND " + MediaStore.Files.FileColumns.SIZE + " > 0 ")
                .setSortOrder(sortOrder)
                .create();
    }
}
