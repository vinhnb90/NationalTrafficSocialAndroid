package com.vn.ntsc.ui.comments;

import android.view.View;

import com.vn.ntsc.repository.model.timeline.datas.sub.BuzzSubCommentBean;
import com.vn.ntsc.widget.adapter.BaseAdapterListener;

/**
 * Created by nankai on 8/23/2017.
 */

public interface PreviewSubCommentAdapterListener extends BaseAdapterListener {

    void onViewProfileUserComment(BuzzSubCommentBean bean, View view);

    void onOpenSubComment();

    void onRemoveSubComment(BuzzSubCommentBean bean);
}