package com.vn.ntsc.ui.posts;

import com.vn.ntsc.core.BasePresenter;
import com.vn.ntsc.core.callback.SubscriberCallback;
import com.vn.ntsc.repository.model.poststatus.uploadsetting.UploadSettingRequest;
import com.vn.ntsc.repository.model.poststatus.uploadsetting.UploadSettingResponse;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Robert on 2017 Aug 28
 * This specifies the Post status the presenter implementation.
 */
public class PostStatusPresenter extends BasePresenter<PostStatusContract.View> implements PostStatusContract.Presenter {
    private static final String TAG = "PostStatusPresenter";

    @Inject
    public PostStatusPresenter() {
    }

    @Override
    public void getUploadSetting(UploadSettingRequest uploadSettingRequest) {
        addSubscriber(apiService.getUploadSetting(uploadSettingRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new SubscriberCallback<UploadSettingResponse>(view) {
                    @Override
                    public void onSuccess(UploadSettingResponse response) {
                        if (view != null) view.onGetUploadSettingResponse(response);
                    }

                    @Override
                    public void onCompleted() {
                    }
                }));
    }
}
