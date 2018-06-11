package com.vn.ntsc.ui.profile.detail;

import com.vn.ntsc.core.BasePresenter;
import com.vn.ntsc.core.callback.SubscriberCallback;
import com.vn.ntsc.repository.model.user.GetUserInfoResponse;
import com.vn.ntsc.repository.model.user.UserInfoRequest;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

class ProfileDetailPresenter extends BasePresenter<ProfileDetailContract.View> implements ProfileDetailContract.Presenter {
    @Inject
    ProfileDetailPresenter() {
    }

    @Override
    public void getUserInfo(String userId, String token) {
        UserInfoRequest request = new UserInfoRequest(token, userId);
        view.showLoadingDialog();
        apiService.getUserInfo(request)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new SubscriberCallback<GetUserInfoResponse>(view) {
                    @Override
                    public void onSuccess(GetUserInfoResponse response) {
                        view.update(response);
                    }

                    @Override
                    public void onCompleted() {
                        view.hideLoadingDialog();
                    }
                });
    }
}
