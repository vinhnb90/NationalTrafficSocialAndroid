package com.vn.ntsc.ui.profile.media.timeline;

import com.vn.ntsc.core.callback.CallbackListener;
import com.vn.ntsc.core.callback.PresenterListener;
import com.vn.ntsc.repository.publicfile.PublicFileRequest;
import com.vn.ntsc.repository.publicfile.PublicFileResponse;

/**
 * Created by ThoNh on 11/21/2017.
 */

public interface TimelineUserTabContract {

    public interface View extends CallbackListener {
        void initRecyclerView();

        void onGetListPublicImage(PublicFileResponse response);

        void onComplete();
    }

    interface Presenter extends PresenterListener<View> {

        void getPublicMedia(PublicFileRequest request);
    }

}
