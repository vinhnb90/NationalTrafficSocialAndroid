package com.vn.ntsc.ui.comment;

import android.view.View;

import com.vn.ntsc.repository.model.timeline.datas.sub.BuzzSubCommentBean;
import com.vn.ntsc.repository.model.timeline.datas.sub.ListCommentBean;
import com.vn.ntsc.widget.adapter.BaseAdapterListener;

/**
 * Created by nankai on 8/10/2017.
 */

public interface CommentAdapterListener extends BaseAdapterListener {

    void onOpenSubComment(ListCommentBean itemBean, boolean isOwner);

    void onRemoveSubComment(ListCommentBean bean, BuzzSubCommentBean itemBean, int position, boolean isOwner);

    void onRemoveComment(ListCommentBean itemBean, int position);

    void onViewProfileUserComment(ListCommentBean itemBean, View view, int position);

    void onViewProfileUserComment(BuzzSubCommentBean bean, View view, int position);

    void onViewMore(ListCommentBean itemBean, int position);
}
