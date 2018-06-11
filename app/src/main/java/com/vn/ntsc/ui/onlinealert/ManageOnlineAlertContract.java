package com.vn.ntsc.ui.onlinealert;

import com.vn.ntsc.core.callback.CallbackListener;
import com.vn.ntsc.core.callback.PresenterListener;
import com.vn.ntsc.repository.model.onlinealert.AddOnlineAlertRequest;
import com.vn.ntsc.repository.model.onlinealert.AddOnlineAlertResponse;
import com.vn.ntsc.repository.model.onlinealert.GetOnlineAlertRequest;
import com.vn.ntsc.repository.model.onlinealert.GetOnlineAlertResponse;

/**
 * Created by nankai on 9/19/2017.
 */

public class ManageOnlineAlertContract {

    interface View extends CallbackListener {
        void onGetOnlineAlert(GetOnlineAlertResponse response);

        void onAddOnlineAlert(AddOnlineAlertResponse response);
    }

    interface Presenter extends PresenterListener<ManageOnlineAlertContract.View> {
        void getOnlineAlert(GetOnlineAlertRequest request);

        void addOnlineAlert(AddOnlineAlertRequest request);
    }
}
