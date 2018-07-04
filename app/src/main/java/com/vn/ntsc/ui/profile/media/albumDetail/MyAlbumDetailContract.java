package com.vn.ntsc.ui.profile.media.albumDetail;

import com.vn.ntsc.core.callback.CallbackListener;
import com.vn.ntsc.core.callback.PresenterListener;
import com.vn.ntsc.repository.model.myalbum.AddImageToAlbum.AddImageAlbumResponse;
import com.vn.ntsc.repository.model.myalbum.DeleteAlbum.DelAlbumRequest;
import com.vn.ntsc.repository.model.myalbum.DeleteImageInAlbum.DelAlbumImageRequest;
import com.vn.ntsc.repository.model.myalbum.DeleteImageInAlbum.DelAlbumImageResponse;
import com.vn.ntsc.repository.model.myalbum.ItemImageInAlbum;
import com.vn.ntsc.repository.model.myalbum.LoadAlbum.LoadAlbumResponse;
import com.vn.ntsc.repository.model.myalbum.LoadImageInAlbum.LoadAlbumImageRequest;
import com.vn.ntsc.repository.model.myalbum.LoadImageInAlbum.LoadAlbumImageResponse;
import com.vn.ntsc.repository.model.myalbum.UpdateAlbum.UpdateAlbumRequest;
import com.vn.ntsc.repository.model.myalbum.UpdateAlbum.UpdateAlbumResponse;

import java.util.List;

/**
 * Created by ThoNh on 11/15/2017.
 */

public interface MyAlbumDetailContract {

    interface View extends CallbackListener {

        public void initEditLayout(LoadAlbumResponse.DataBean album);

        public void initRecyclerView();

        public void getImageAlbumSuccess(LoadAlbumImageResponse response);

        public void getImageAlbumFailure(Throwable e);

        public void getImageAlbumComplete();

        public void addImagesToAlbumSuccess(AddImageAlbumResponse response);

        public void addImagesFailure();

        public void complete();

        public void deleteImagesInAlbumComplete();

        public void deleteImagesInAlbumSuccess(DelAlbumImageResponse response, List<ItemImageInAlbum> imagesSelected);

        public void updateAlbumSuccess(UpdateAlbumResponse response);

        public void updateAlbumFailure();

        public void updateComplete();

        public void deleteAlbumSuccess();

        public void deleteAlbumComplete();
    }

    interface Presenter extends PresenterListener<View> {
        public void getImageAlbum(LoadAlbumImageRequest request);

        public void deteleAlbum(DelAlbumRequest request);

        public void deleteImagesInAlbum(DelAlbumImageRequest request, List<ItemImageInAlbum> imagesSelected);

        public void updateAlbum(UpdateAlbumRequest request);
    }

}
