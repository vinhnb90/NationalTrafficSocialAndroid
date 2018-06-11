package com.vn.ntsc.ui.posts;

import com.vn.ntsc.core.callback.CallbackListener;
import com.vn.ntsc.core.callback.PresenterListener;
import com.vn.ntsc.repository.model.poststatus.uploadsetting.UploadSettingRequest;
import com.vn.ntsc.repository.model.poststatus.uploadsetting.UploadSettingResponse;

/**
 * Created by Robert on 2017 Aug 28
 * This specifies the Post status contract between the view and the presenter.
 */
public interface PostStatusContract {

    interface View extends CallbackListener {
        void onGetUploadSettingResponse(UploadSettingResponse response);
    }

    interface Presenter extends PresenterListener<PostStatusContract.View> {

        abstract void getUploadSetting(UploadSettingRequest uploadSettingRequest);
    }
}
