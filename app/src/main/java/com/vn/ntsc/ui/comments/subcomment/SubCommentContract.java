package com.vn.ntsc.ui.comments.subcomment;

import com.vn.ntsc.core.callback.CallbackListener;
import com.vn.ntsc.core.callback.PresenterListener;
import com.vn.ntsc.repository.model.comment.AddSubCommentRequest;
import com.vn.ntsc.repository.model.comment.CommentDetailRequest;
import com.vn.ntsc.repository.model.comment.CommentDetailResponse;
import com.vn.ntsc.repository.model.comment.DeleteSubCommentRequest;
import com.vn.ntsc.repository.model.comment.DeleteSubCommentResponse;
import com.vn.ntsc.repository.model.comment.ListSubCommentRequest;
import com.vn.ntsc.repository.model.comment.ListSubCommentResponse;
import com.vn.ntsc.repository.model.timeline.JoinBuzzRequest;

/**
 * Created by nankai on 8/10/2017.
 */

public interface SubCommentContract {

    interface View extends CallbackListener {

        void onGetCommentDetail(CommentDetailResponse response);

        void onSubListComment(ListSubCommentResponse response);

        void onDeleteComment(DeleteSubCommentResponse response, int position);

        void onCompleted();
    }

    interface Presenter extends PresenterListener<SubCommentContract.View> {

        void sendBuzzJoin(JoinBuzzRequest joinBuzzRequest);

        void getCommentDetail(CommentDetailRequest request);

        void getSubListComment(ListSubCommentRequest listSubCommentRequest);

        void sendSubComment(AddSubCommentRequest addSubCommentRequest);

        void deleteComment(DeleteSubCommentRequest deleteCommentRequest, int position);

    }
}
