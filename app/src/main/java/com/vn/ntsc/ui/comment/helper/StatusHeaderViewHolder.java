package com.vn.ntsc.ui.comment.helper;

import android.view.View;

import com.vn.ntsc.repository.model.timeline.datas.BuzzBean;
import com.vn.ntsc.ui.comment.CommentActivity;

/**
 * Created by nankai on 10/25/2017.
 */

public class StatusHeaderViewHolder extends HeaderViewHolder {

    public StatusHeaderViewHolder(int typeHeader, View itemView, int typeView) {
        super(typeHeader, itemView, typeView);
    }

    @Override
    public StatusHeaderViewHolder onBindView(CommentActivity activity, final BuzzBean bean) {
        super.onBindView(activity, bean);
        description.setText(bean.buzzValue);

        return this;
    }
}
