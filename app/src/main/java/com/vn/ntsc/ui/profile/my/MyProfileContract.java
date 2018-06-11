package com.vn.ntsc.ui.profile.my;

import com.vn.ntsc.core.callback.CallbackListener;
import com.vn.ntsc.core.callback.PresenterListener;
import com.vn.ntsc.repository.model.block.addblock.AddBlockUserRequest;
import com.vn.ntsc.repository.model.favorite.AddFavoriteRequest;
import com.vn.ntsc.repository.model.favorite.AddFavoriteResponse;
import com.vn.ntsc.repository.model.favorite.RemoveFavoriteRequest;
import com.vn.ntsc.repository.model.favorite.RemoveFavoriteResponse;
import com.vn.ntsc.repository.model.report.ReportRequest;
import com.vn.ntsc.repository.model.user.UserInfoRequest;
import com.vn.ntsc.repository.model.user.UserInfoResponse;

/**
 * Created by nankai on 9/12/2017.
 */

public interface MyProfileContract {

    interface View extends CallbackListener {

        void onFavoriteResponse(AddFavoriteResponse response, String userId);

        void onUnFavoriteResponse(RemoveFavoriteResponse response, String userId);

        void onUserInfo(UserInfoResponse userInfoResponse);

        void onReportUser();

        void onAddBlockUser();
    }

    interface Presenter extends PresenterListener<MyProfileContract.View> {

        void setFavorite(AddFavoriteRequest addFavoriteRequest, String userId);

        void setUnFavorite(RemoveFavoriteRequest removeFavoriteRequest, String userId);

        void getUserInfo(UserInfoRequest userInfoRequest, int delay);

        void reportUser(ReportRequest reportRequest);

        void blockUser(AddBlockUserRequest request);
    }
}
