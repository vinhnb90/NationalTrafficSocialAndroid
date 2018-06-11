package com.vn.ntsc.ui.onlinealert;

import com.vn.ntsc.core.BasePresenter;
import com.vn.ntsc.core.callback.SubscriberCallback;
import com.vn.ntsc.repository.model.onlinealert.AddOnlineAlertRequest;
import com.vn.ntsc.repository.model.onlinealert.AddOnlineAlertResponse;
import com.vn.ntsc.repository.model.onlinealert.GetOnlineAlertRequest;
import com.vn.ntsc.repository.model.onlinealert.GetOnlineAlertResponse;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by nankai on 9/19/2017.
 */

public class ManageOnlineAlertPresenter extends BasePresenter<ManageOnlineAlertContract.View> implements ManageOnlineAlertContract.Presenter {

    @Inject
    public ManageOnlineAlertPresenter() {
    }


    @Override
    public void getOnlineAlert(GetOnlineAlertRequest request) {
        addSubscriber(apiService.getOnlineAlert(request)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new SubscriberCallback<GetOnlineAlertResponse>(view) {
                    @Override
                    public void onSuccess(GetOnlineAlertResponse response) {
                        view.onGetOnlineAlert(response);
                    }

                    @Override
                    public void onCompleted() {

                    }
                }));
    }

    @Override
    public void addOnlineAlert(AddOnlineAlertRequest request) {
        addSubscriber(apiService.addOnlineAlert(request)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new SubscriberCallback<AddOnlineAlertResponse>(view) {
                    @Override
                    public void onSuccess(AddOnlineAlertResponse response) {
                        view.onAddOnlineAlert(response);
                    }

                    @Override
                    public void onCompleted() {

                    }
                }));
    }
}
