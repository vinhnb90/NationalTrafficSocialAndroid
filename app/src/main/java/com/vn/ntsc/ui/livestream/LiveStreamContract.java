package com.vn.ntsc.ui.livestream;

import com.vn.ntsc.core.callback.CallbackListener;
import com.vn.ntsc.core.callback.PresenterListener;
import com.vn.ntsc.repository.model.comment.AddCommentRequest;
import com.vn.ntsc.repository.model.comment.ListCommentRequest;
import com.vn.ntsc.repository.model.comment.ListCommentResponse;
import com.vn.ntsc.repository.model.poststatus.PostStatusResponse;
import com.vn.ntsc.repository.model.timeline.BuzzDetailRequest;
import com.vn.ntsc.repository.model.timeline.BuzzDetailResponse;
import com.vn.ntsc.repository.model.timeline.JoinBuzzRequest;
import com.vn.ntsc.repository.model.token.CheckTokenRequest;
import com.vn.ntsc.repository.model.token.CheckTokenResponse;
import com.vn.ntsc.ui.mediadetail.timeline.MediaDetailContract;

import java.util.List;

/**
 * Created by Robert on 2017  Nov25
 * This specifies the Live Stream contract between the view and the presenter.
 */
public interface LiveStreamContract {

    interface View extends CallbackListener {

        void onTimelineDetail(BuzzDetailResponse response);

        void onBuzzListComment(ListCommentResponse response);

        void onBuzzNotFoundFromNotificationId();

        void onComplete();

        void onRefreshToken(CheckTokenResponse checkTokenResponse);

        void handleBuzzNotFound(String buzzId);

        /**
         * share media post error
         *
         * @see MediaDetailContract.Presenter#shareMedia(String, String, List, String)
         */
        void shareMediaFailure();

        /**
         * success to share buzz
         * @param response
         */
        void shareMediaSuccess(PostStatusResponse response);
    }

    interface Presenter extends PresenterListener<LiveStreamContract.View> {

        void sendBuzzJoin(JoinBuzzRequest joinBuzzRequest);

        void getTimelineDetail(BuzzDetailRequest listDetailRequest);

        void getBuzzListComment(ListCommentRequest response);

        void sendComment(AddCommentRequest addCommentRequest);

        void shareMedia(String token, String buzzValue, List<String> listUserId, String buzzId);

        void checkToken(CheckTokenRequest checkTokenRequest);
    }
}
