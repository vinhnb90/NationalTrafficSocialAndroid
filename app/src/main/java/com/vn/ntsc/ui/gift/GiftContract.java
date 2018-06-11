package com.vn.ntsc.ui.gift;

import com.vn.ntsc.core.callback.CallbackListener;
import com.vn.ntsc.core.callback.PresenterListener;
import com.vn.ntsc.repository.model.gift.Gift;
import com.vn.ntsc.repository.model.gift.GiftRequest;
import com.vn.ntsc.repository.model.gift.GiftResponse;

/**
 * Created by TuanPC on 10/26/2017.
 */
public interface GiftContract {
    interface View extends CallbackListener {

        void onGetGiftSuccess(GiftResponse response);

        void onFinish();

        /**
         * show not enough point
         *
         * @see #gotoChat(Gift)
         */
        void notEnoughPoint();

        /**
         * point enough -> goto chat
         *
         * @param item gift
         * @see #notEnoughPoint()
         */
        void gotoChat(Gift item);
    }

    interface Presenter extends PresenterListener<View> {
        void getGiftData(GiftRequest request);

        /**
         * check point before send gift
         *
         * @param item          gift item
         * @param token         sender token
         * @param receiveUserId receiver id
         */
        void checkPoint(Gift item, String token, String receiveUserId);
    }
}
