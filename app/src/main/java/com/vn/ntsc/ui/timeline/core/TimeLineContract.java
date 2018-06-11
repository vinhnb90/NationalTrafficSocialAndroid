package com.vn.ntsc.ui.timeline.core;

import com.vn.ntsc.core.callback.CallbackListener;
import com.vn.ntsc.core.callback.PresenterListener;
import com.vn.ntsc.repository.TypeView;
import com.vn.ntsc.repository.model.favorite.AddFavoriteRequest;
import com.vn.ntsc.repository.model.favorite.AddFavoriteResponse;
import com.vn.ntsc.repository.model.favorite.RemoveFavoriteRequest;
import com.vn.ntsc.repository.model.favorite.RemoveFavoriteResponse;
import com.vn.ntsc.repository.model.profile.EvaluateUserProfileRequest;
import com.vn.ntsc.repository.model.profile.EvaluateUserProfileResponse;
import com.vn.ntsc.repository.model.share.AddNumberShareRequest;
import com.vn.ntsc.repository.model.share.AddNumberShareResponse;
import com.vn.ntsc.repository.model.timeline.BuzzDetailRequest;
import com.vn.ntsc.repository.model.timeline.BuzzDetailResponse;
import com.vn.ntsc.repository.model.timeline.BuzzListRequest;
import com.vn.ntsc.repository.model.timeline.BuzzListResponse;
import com.vn.ntsc.repository.model.timeline.DeleteBuzzRequest;
import com.vn.ntsc.repository.model.timeline.DeleteBuzzResponse;
import com.vn.ntsc.repository.model.timeline.LikeBuzzRequest;
import com.vn.ntsc.repository.model.timeline.LikeBuzzResponse;
import com.vn.ntsc.repository.model.user.UserInfoResponse;
import com.vn.ntsc.repository.publicfile.PublicFileRequest;
import com.vn.ntsc.repository.publicfile.PublicFileResponse;

/**
 * Created by nankai on 8/3/2017.
 */

public interface TimeLineContract {

    interface View extends CallbackListener {

        void onBuzzDetail(BuzzDetailResponse response, String templateId);

        void onBuzzDetailError(BuzzDetailRequest listDetailRequest, String templateId);

        void onBuzzListResponse(BuzzListResponse response);

        void onLoadMoreBuzzListResponse(BuzzListResponse response);

        void onLoadMoreEmpty();

        void onLikeResponse(LikeBuzzResponse response, String buzzId, android.view.View view);

        void onDeleteBuzzResponse(DeleteBuzzResponse response, String buzzID);

        void onFavoriteResponse(AddFavoriteResponse response, String userId);

        void onUnFavoriteResponse(RemoveFavoriteResponse response, String userId);

        void onAddNumberShare(AddNumberShareResponse response, String buzzID);

        void onGetListPublicFile(PublicFileResponse response);

        void onComplete();

        void onGetRoomLiveStream(BuzzListResponse response);

        void handleBuzzNotFound(String buzzID);

        void onTimelineLiveStreamEmptyView();

        void onEvaluateUser(EvaluateUserProfileResponse response);
    }

    interface Presenter extends PresenterListener<TimeLineContract.View> {

        void getBuzzDetail(BuzzDetailRequest listDetailRequest, String templateId);

        void getBuzzList(BuzzListRequest buzzListRequest, UserInfoResponse profileBean, @TypeView.TypeViewTimeline int TypeView, int delay);

        void getMoreBuzzList(BuzzListRequest buzzListRequest, final UserInfoResponse profileBean, @TypeView.TypeViewTimeline final int typeView);

        void setFavorite(AddFavoriteRequest addFavoriteRequest, String userId);

        void setUnFavorite(RemoveFavoriteRequest removeFavoriteRequest, String userId);

        void setLike(LikeBuzzRequest likeBuzzRequest, String buzzId, android.view.View view);

        void requestDeleteBuzz(DeleteBuzzRequest deleteBuzzRequest, String buzzID);

        void requestAddNumberShare(AddNumberShareRequest addNumberShareRequest, String buzzID);

        void getListPublicFile(PublicFileRequest listPublicImageRequest, UserInfoResponse profileBean, @TypeView.TypeViewTimeline final int typeView, int delay);

        void getRoomLiveStream(BuzzListRequest buzzListRequest, @TypeView.TypeViewTimeline final int typeView, int delay);

        void getEvaluateUser(EvaluateUserProfileRequest evaluateUserProfileRequest);
    }
}
