package com.vn.ntsc.ui.chat.generalibrary;

import com.vn.ntsc.core.callback.CallbackListener;
import com.vn.ntsc.core.callback.PresenterListener;
import com.vn.ntsc.repository.model.chat.GeneraLibraryRequest;
import com.vn.ntsc.repository.model.chat.GeneraLibraryResponse;

/**
 * Created by Doremon on 2/28/2018.
 */

public interface GeneraLibraryContract {

    interface View extends CallbackListener {
        void onAllFileChat(GeneraLibraryResponse response);

        void onComplete();
    }

    interface Presenter extends PresenterListener<View> {
        void getAllFileChat(GeneraLibraryRequest request);
    }
}
