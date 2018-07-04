package com.vn.ntsc.ui.mediadetail.album;

import android.content.Context;

import com.vn.ntsc.core.callback.CallbackListener;
import com.vn.ntsc.core.callback.PresenterListener;
import com.vn.ntsc.repository.model.myalbum.DeleteImageInAlbum.DelAlbumImageRequest;
import com.vn.ntsc.repository.model.myalbum.DeleteImageInAlbum.DelAlbumImageResponse;
import com.vn.ntsc.repository.model.myalbum.ItemImageInAlbum;
import com.vn.ntsc.repository.model.report.ReportRequest;
import com.vn.ntsc.ui.mediadetail.base.IDetailMediaInteractor;
import com.vn.ntsc.ui.mediadetail.timeline.MediaDetailContract;

/**
 * Created by ThoNh on 1/9/2018.
 */

public interface AlbumDetailMediaContract {
    interface View extends CallbackListener, MediaDetailContract.View  {

        public void deleteImagesInAlbumComplete();

        public void deleteImagesInAlbumFailure();

        public void deleteImagesInAlbumSuccess(DelAlbumImageResponse response);

        public void saveImageSuccess();

        public void saveImageFailure();

        public void reportFailure();

        public void reportSuccess();
    }

    interface Presenter extends PresenterListener<AlbumDetailMediaContract.View>{

        void deleteImagesInAlbum(DelAlbumImageRequest request);

        void saveImage(Context context, ItemImageInAlbum album);

        void reportImage(ReportRequest request);
    }
}
