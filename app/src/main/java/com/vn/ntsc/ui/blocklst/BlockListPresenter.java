package com.vn.ntsc.ui.blocklst;

import com.vn.ntsc.core.BasePresenter;
import com.vn.ntsc.core.callback.SubscriberCallback;
import com.vn.ntsc.repository.model.block.addblock.AddBlockResponse;
import com.vn.ntsc.repository.model.block.addblock.AddBlockUserRequest;
import com.vn.ntsc.repository.model.block.blocklst.BlockListRequest;
import com.vn.ntsc.repository.model.block.blocklst.BlockListResponse;
import com.vn.ntsc.repository.model.block.blocklst.BlockLstItem;
import com.vn.ntsc.repository.model.block.rmvblock.UnBlockRequest;
import com.vn.ntsc.repository.model.block.rmvblock.UnblockResponse;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by ThoNh on 9/22/2017.
 */

public class BlockListPresenter extends BasePresenter<BlockListContracts.View> implements BlockListContracts.Presenter {

    @Inject
    public BlockListPresenter() {

    }

    @Override
    public void requestBlockLst(BlockListRequest request) {
        addSubscriber(apiService
                .getLstBlock(request)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new SubscriberCallback<BlockListResponse>(view) {
                    @Override
                    public void onSuccess(BlockListResponse response) {
                        view.getLstBlockSuccess(response);
                    }

                    @Override
                    public void onCompleted() {
                        view.getLstBlockComplete();
                    }
                }));
    }

    @Override
    public void unBlock(UnBlockRequest request, final BlockLstItem item, final int position) {
        addSubscriber(apiService
                .unblock(request)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io()).subscribeWith(new SubscriberCallback<UnblockResponse>(view) {
                    @Override
                    public void onSuccess(UnblockResponse response) {
                        view.unBlockSuccess(item, position);
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        view.unBlockFailure();
                    }

                    @Override
                    public void onCompleted() {

                    }
                }));
    }

    @Override
    public void reBlock(AddBlockUserRequest request, final BlockLstItem item, final int position) {
        addSubscriber(apiService
                .addBlockUser(request)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io()).subscribeWith(new SubscriberCallback<AddBlockResponse>(view) {
                    @Override
                    public void onSuccess(AddBlockResponse response) {
                        view.reBlockSuccess(item, position);
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        view.reBlockFailure();
                    }

                    @Override
                    public void onCompleted() {

                    }
                }));
    }
}
