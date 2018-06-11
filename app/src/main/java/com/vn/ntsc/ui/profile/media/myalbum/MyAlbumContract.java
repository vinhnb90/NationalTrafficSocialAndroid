package com.vn.ntsc.ui.profile.media.myalbum;

import com.vn.ntsc.core.callback.CallbackListener;
import com.vn.ntsc.core.callback.PresenterListener;
import com.vn.ntsc.repository.model.myalbum.LoadAlbum.LoadAlbumRequest;
import com.vn.ntsc.repository.model.myalbum.LoadAlbum.LoadAlbumResponse;
import com.vn.ntsc.repository.model.myalbum.UpdateAlbum.UpdateAlbumRequest;

/**
 * Created by ThoNh on 11/14/2017.
 */

public interface MyAlbumContract {

    interface View extends CallbackListener {

        public void getAlbumSuccess(LoadAlbumResponse response);

        public void getAlbumFailure();

        public void updateAlbumSuccess();


        public void updateAlbumFailure();

        public void getAlbumComplete();

        public void updateComplete();
    }

    interface Presenter extends PresenterListener<View> {

        public void getMyAlbum(LoadAlbumRequest request);

        public void updateMyAlbum(UpdateAlbumRequest request);

    }

}
