package com.vn.ntsc.utils;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

/**
 * Created by robert on 9/7/17.
 */

public class MediaFileUtils {
    private static final String TAG = MediaFileUtils.class.getSimpleName();

    /**
     * Get duration of video file by use the MediaMetaDataRetriever with videoPath String input
     * @param mVideoPath
     * @return
     */
    public static long getDuration(String mVideoPath) {

        if (Utils.isEmptyOrNull(mVideoPath)) return 0;

        android.media.MediaMetadataRetriever retriever = new android.media.MediaMetadataRetriever();
        long duration = 0;
        try {
            retriever.setDataSource(mVideoPath);
            String dur = retriever.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_DURATION);
            if (dur != null) {
                duration = Long.parseLong(dur);
            }
            return duration;
        } catch (Exception ex) {
            Log.w(TAG, "MediaMetadataRetriever failed to get duration for " + mVideoPath, ex);
        } finally {
            retriever.release();
        }
        return 0;
    }

    /**
     * Get duration of video file by use the MediaMetaDataRetriever with Uri of video input
     * @param mContext
     * @param mUri
     * @return
     */
    public static long getDuration(Context mContext, Uri mUri) {
        if (mUri == null) return 0;

        android.media.MediaMetadataRetriever retriever = new android.media.MediaMetadataRetriever();
        long duration = 0;
        try {
            retriever.setDataSource(mContext, mUri);
            String dur = retriever.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_DURATION);
            if (dur != null) {
                duration = Long.parseLong(dur);
            }
            return duration;
        } catch (Exception ex) {
            Log.w(TAG, "MediaMetadataRetriever failed to get duration for " + mUri.getPath(), ex);
        } finally {
            retriever.release();
        }
        return 0;
    }
}
