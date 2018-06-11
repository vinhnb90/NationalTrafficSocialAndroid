package com.vn.ntsc.ui.blocklst;

import com.vn.ntsc.core.callback.CallbackListener;
import com.vn.ntsc.core.callback.PresenterListener;
import com.vn.ntsc.repository.model.block.addblock.AddBlockUserRequest;
import com.vn.ntsc.repository.model.block.blocklst.BlockListRequest;
import com.vn.ntsc.repository.model.block.blocklst.BlockListResponse;
import com.vn.ntsc.repository.model.block.blocklst.BlockLstItem;
import com.vn.ntsc.repository.model.block.rmvblock.UnBlockRequest;

/**
 * Created by ThoNh on 9/22/2017.
 */

public interface BlockListContracts {

    interface View extends CallbackListener {

        void getLstBlockSuccess(BlockListResponse response);

        void getLstBlockComplete();

        void unBlockSuccess(BlockLstItem item, int position);

        void unBlockFailure();

        void reBlockSuccess(BlockLstItem item, int position);

        void reBlockFailure();
    }

    interface Presenter extends PresenterListener<View> {
        void requestBlockLst(BlockListRequest request);

        void unBlock(UnBlockRequest request, BlockLstItem item, int position);

        void reBlock(AddBlockUserRequest request, BlockLstItem item, int position);
    }

}
