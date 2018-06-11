package com.vn.ntsc.ui.profile.media.videoaudio;

import com.vn.ntsc.core.callback.CallbackListener;
import com.vn.ntsc.core.callback.PresenterListener;
import com.vn.ntsc.repository.model.videoaudio.VideoAudioResponse;
import com.vn.ntsc.repository.publicfile.PublicFileRequest;

/**
 * Created by ThoNh on 11/20/2017.
 */

public interface VideoAudioContract {

    public interface View extends CallbackListener {

        public void initRecyclerView();

        public void getLstPublicVideoAudioSuccess(VideoAudioResponse response);

        public void getLstPublicVideoAudioFailure();

        public void onComplete();
    }

    public interface Presenter extends PresenterListener<View> {
        public void requestLstPublicVideoAudio(PublicFileRequest request);
    }
}
