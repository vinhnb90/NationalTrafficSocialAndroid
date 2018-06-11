package com.vn.ntsc.ui.setting.notification;

import com.vn.ntsc.core.callback.CallbackListener;
import com.vn.ntsc.core.callback.PresenterListener;

/**
 * Created by ThoNh on 10/4/2017.
 */

public class NotificationSettingContracts {
    private static final String TAG = NotificationSettingContracts.class.getSimpleName();

    interface View extends CallbackListener {

    }

    interface Presenter extends PresenterListener<View>{

    }
}
