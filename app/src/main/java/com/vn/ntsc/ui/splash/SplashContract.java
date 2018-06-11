package com.vn.ntsc.ui.splash;

import com.vn.ntsc.core.callback.CallbackListener;
import com.vn.ntsc.core.callback.PresenterListener;
import com.vn.ntsc.core.model.ServerResponse;
import com.vn.ntsc.repository.model.applicationinfo.GetApplicationInfoResponse;
import com.vn.ntsc.repository.model.applicationinfo.GetUpdateInfoFlagRequest;
import com.vn.ntsc.repository.model.applicationinfo.GetUpdateInfoFlagResponse;
import com.vn.ntsc.repository.model.installcount.InstallCountRequest;
import com.vn.ntsc.repository.model.installcount.InstallCountResponse;
import com.vn.ntsc.repository.model.user.BannedWordRequest;
import com.vn.ntsc.repository.model.user.BannedWordResponse;
import com.vn.ntsc.repository.model.user.GetUserStatusRequest;
import com.vn.ntsc.repository.model.user.GetUserStatusResponse;
import com.vn.ntsc.repository.model.user.SetCareUserInfoRequest;
import com.vn.ntsc.repository.model.user.SetCareUserInfoResponse;

/**
 * Created by nankai on 8/3/2017.
 */

public interface SplashContract {

    interface View extends CallbackListener {
        void onApplicationInfo(GetApplicationInfoResponse response);

        void onInstallCount(InstallCountResponse response);

        void onUpdateInfoFlag(GetUpdateInfoFlagResponse response);

        void onUserStatus(GetUserStatusResponse response);

        void onCareUserInfo(SetCareUserInfoResponse response);

        void onInvalid(ServerResponse response);

        /**
         * update list banned word into db if request {@link com.vn.ntsc.repository.remote.ApiService#getBannedWords(BannedWordRequest)} success
         *
         * @param response server response
         */
        void onUpdateBannedWords(BannedWordResponse response);

        /**
         * when call get banned word complete no matter success or error
         */
        void onCompleteGetBannedWord();
    }

    interface Presenter extends PresenterListener<View> {

        abstract void getApplicationInfo();

        abstract void sendInstallCount(InstallCountRequest installCountRequest);

        abstract void updateInfoFlagRequest(GetUpdateInfoFlagRequest updateInfoFlagRequest);

        abstract void getUserStatusRequest(GetUserStatusRequest userStatusRequest);

        abstract void setCareUserInfo(SetCareUserInfoRequest setCareUserInfoRequest);

        void getBannedWords(int bannedWordVersion);
    }
}
