package com.vn.ntsc.ui.comments.subcomment;

import android.view.View;

import com.vn.ntsc.repository.model.timeline.datas.sub.BuzzSubCommentBean;
import com.vn.ntsc.widget.adapter.BaseAdapterListener;

/**
 * Created by nankai on 8/24/2017.
 */

public interface SubCommentAdapterListener extends BaseAdapterListener {
    void onRemoved(BuzzSubCommentBean itemBean, int position);

    void viewProfileSubComment(BuzzSubCommentBean itemBean, View view, int position);

    void onViewMore(BuzzSubCommentBean itemBean, int position);
}
