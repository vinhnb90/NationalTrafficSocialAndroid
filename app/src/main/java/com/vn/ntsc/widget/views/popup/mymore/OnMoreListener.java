package com.vn.ntsc.widget.views.popup.mymore;

import com.vn.ntsc.repository.model.user.UserInfoResponse;

/**
 * Created by nankai on 9/13/2017.
 */

public interface OnMoreListener {
    void onSendGift(UserInfoResponse userProfileBean);

    void onFavorite(UserInfoResponse userProfileBean);

    void onBlock(UserInfoResponse userProfileBean);

    void onReport(UserInfoResponse userProfileBean);

    void onAlertOnline(UserInfoResponse userProfileBean);

    void onWarningNotLogin();
}
