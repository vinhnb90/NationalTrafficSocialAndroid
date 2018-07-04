package com.vn.ntsc.ui.comments;

import com.vn.ntsc.core.callback.CallbackListener;
import com.vn.ntsc.core.callback.PresenterListener;
import com.vn.ntsc.repository.model.comment.AddCommentRequest;
import com.vn.ntsc.repository.model.comment.DeleteCommentRequest;
import com.vn.ntsc.repository.model.comment.DeleteCommentResponse;
import com.vn.ntsc.repository.model.comment.DeleteSubCommentRequest;
import com.vn.ntsc.repository.model.comment.DeleteSubCommentResponse;
import com.vn.ntsc.repository.model.comment.ListCommentRequest;
import com.vn.ntsc.repository.model.comment.ListCommentResponse;
import com.vn.ntsc.repository.model.comment.ListSubCommentRequest;
import com.vn.ntsc.repository.model.comment.ListSubCommentResponse;
import com.vn.ntsc.repository.model.favorite.AddFavoriteRequest;
import com.vn.ntsc.repository.model.favorite.AddFavoriteResponse;
import com.vn.ntsc.repository.model.favorite.RemoveFavoriteRequest;
import com.vn.ntsc.repository.model.favorite.RemoveFavoriteResponse;
import com.vn.ntsc.repository.model.timeline.BuzzDetailRequest;
import com.vn.ntsc.repository.model.timeline.BuzzDetailResponse;
import com.vn.ntsc.repository.model.timeline.DeleteBuzzRequest;
import com.vn.ntsc.repository.model.timeline.DeleteBuzzResponse;
import com.vn.ntsc.repository.model.timeline.JoinBuzzRequest;
import com.vn.ntsc.repository.model.timeline.LikeBuzzRequest;
import com.vn.ntsc.repository.model.timeline.LikeBuzzResponse;
import com.vn.ntsc.repository.model.timeline.datas.sub.ListCommentBean;

/**
 * Created by nankai on 8/10/2017.
 */

public interface CommentContract {

    interface View extends CallbackListener {
        void onTimelineDetail(BuzzDetailResponse response);

        void onBuzzListComment(ListCommentResponse response);

        void onLoadMoreBuzzListComment(ListCommentResponse response, boolean hasScrollLastPosition);

        void onFavorite(AddFavoriteResponse response);

        void onUnFavorite(RemoveFavoriteResponse response);

        void onDelete(DeleteBuzzResponse response);

        void onLike(LikeBuzzResponse response);

        void onLikeFail();

        void onDeleteComment(DeleteCommentResponse response, int position);

        void onDeleteSubComment(DeleteSubCommentResponse response, ListCommentBean bean, int position);

        void onComplete();

        void onBuzzNotFoundFromNotificationId();
    }

    interface Presenter extends PresenterListener<CommentContract.View> {

        void sendBuzzJoin(JoinBuzzRequest joinBuzzRequest);

        void getTimelineDetail(BuzzDetailRequest listDetailRequest, int delay);

        void getBuzzListComment(ListCommentRequest listCommentRequest, int delay);

        void getMoreBuzzListComment(ListCommentRequest response, boolean hasScrollLastPosition);

        void onFavorite(AddFavoriteRequest addFavoriteRequest);

        void onUnFavorite(RemoveFavoriteRequest removeFavoriteRequest);

        void onDelete(DeleteBuzzRequest deleteBuzzRequest);

        void onLike(LikeBuzzRequest likeBuzzRequest);

        void sendComment(AddCommentRequest addCommentRequest);

        void deleteComment(DeleteCommentRequest deleteCommentRequest, int position);

        void deleteSubComment(final ListCommentBean bean, DeleteSubCommentRequest deleteSubCommentRequest, final int position);
    }
}
