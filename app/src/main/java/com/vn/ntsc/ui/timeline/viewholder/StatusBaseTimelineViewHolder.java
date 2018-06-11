package com.vn.ntsc.ui.timeline.viewholder;

import android.view.View;

import com.vn.ntsc.repository.model.timeline.datas.BuzzBean;
import com.vn.ntsc.ui.timeline.core.TimelineListener;

/**
 * Created by nankai on 10/25/2017.
 */

public class StatusBaseTimelineViewHolder extends BaseTimelineViewHolder {

    public StatusBaseTimelineViewHolder(View itemView, int viewType) {
        super(itemView, viewType);
    }

    @Override
    public void onBindView(final BuzzBean bean, final int position, final TimelineListener listener) {
        super.onBindView(bean, position, listener);
    }
}
