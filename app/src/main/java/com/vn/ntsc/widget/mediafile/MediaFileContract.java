package com.vn.ntsc.widget.mediafile;

import android.content.ContentResolver;
import android.database.Cursor;

/**
 * Created by nankai on 2017 Sep 17
 * This specifies the Get all media file contract between the view and the presenter.
 */
public interface MediaFileContract {
    interface View {
        /**
         * update data when get all media files success
         *
         * @param cursor Cursor
         * @param mediaType type of MEDIA_TYPE
         */
        void onCursorLoaded(Cursor cursor, int mediaType);

        void onCursorLoadFailed(Throwable throwable, int mediaType);
    }

    interface Presenter {
        /**
         * after show ui complete
         */
        void onExecuteGetMediaFileReady(int mediaType, int limit, int offset, ContentResolver contentResolver);

        void destroy();
    }
}
