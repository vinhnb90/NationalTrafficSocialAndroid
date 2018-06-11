package com.vn.ntsc.ui.gift;

import android.util.Log;

import com.vn.ntsc.core.BasePresenter;
import com.vn.ntsc.core.callback.SubscriberCallback;
import com.vn.ntsc.repository.model.gift.Gift;
import com.vn.ntsc.repository.model.gift.GiftRequest;
import com.vn.ntsc.repository.model.gift.GiftResponse;
import com.vn.ntsc.repository.model.point.GetPointRequest;
import com.vn.ntsc.repository.model.point.GetPointResponse;
import com.vn.ntsc.utils.LogUtils;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by TuanPC on 10/26/2017.
 */
public class GiftPresenter extends BasePresenter<GiftContract.View> implements GiftContract.Presenter {
    private final String TAG = "GiftPresenter";

    @Inject
    public GiftPresenter() {

    }

    @Override
    public void getGiftData(GiftRequest request) {
        Log.d("apiService", "" + apiService);
        addSubscriber(apiService.onDownLoadGif(request)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new SubscriberCallback<GiftResponse>(view) {
                    @Override
                    public void onSuccess(GiftResponse response) {
                        view.onGetGiftSuccess(response);
                        Log.d("response", "" + response.mDataGift);
                    }

                    @Override
                    public void onCompleted() {
                        view.onFinish();
                    }
                }));
    }

    @Override
    public void checkPoint(final Gift item, String token, final String receiveUserId) {
        GetPointRequest getPointRequest = new GetPointRequest(item.gift_id, token, receiveUserId);
        addSubscriber(apiService.getPoint(getPointRequest)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new SubscriberCallback<GetPointResponse>(view) {
                    @Override
                    public void onSuccess(GetPointResponse response) {
                        if (response.mUserPoint.point >= item.gift_pri) {
                            LogUtils.d(TAG, " " + response.mUserPoint);
                            view.gotoChat(item);
                        } else {
                            view.notEnoughPoint();
                        }
                    }

                    @Override
                    public void onCompleted() {

                    }
                }));
    }
}
