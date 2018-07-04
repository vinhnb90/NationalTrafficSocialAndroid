package com.vn.ntsc.ui.chat.meidiadetail;

import android.content.Context;

import com.vn.ntsc.core.callback.CallbackListener;
import com.vn.ntsc.core.callback.PresenterListener;
import com.vn.ntsc.repository.model.chat.ItemFileChat;
import com.vn.ntsc.ui.mediadetail.base.IDetailMediaInteractor;

import java.io.File;

/**
 * Created by Doremon on 3/5/2018.
 */

public class ChatMediaDetailContract {
    interface View extends CallbackListener, IDetailMediaInteractor.ActivityView{

        void saveImageSuccess();

        void saveImageFailure();

        void downloadComplete(File file);

        void downloadError();

        void onComplete();

    }

    interface Presenter extends PresenterListener<ChatMediaDetailContract.View> {
        void saveMedia(Context context, ItemFileChat child);
    }

}
