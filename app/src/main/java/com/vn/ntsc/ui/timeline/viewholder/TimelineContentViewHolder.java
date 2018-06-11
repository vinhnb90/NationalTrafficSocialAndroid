package com.vn.ntsc.ui.timeline.viewholder;

import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.view.ViewGroup;

import com.vn.ntsc.R;
import com.vn.ntsc.app.AppController;

/**
 * Created by nankai on 12/27/2017.
 */

public class TimelineContentViewHolder extends BaseTimelineViewHolder {

    public TimelineContentViewHolder(View itemView, int viewType) {
        super(itemView, viewType);
        layoutContent = itemView.findViewById(R.id.layout_content);
        if (layoutContent.getTag() == null) {
            ViewGroup.LayoutParams layoutParams = layoutContent.getLayoutParams();
            layoutParams.height = AppController.SCREEN_WIDTH;
            layoutParams.width = AppController.SCREEN_WIDTH;
            layoutContent.setTag(this);
        } else {
            layoutContent = (ConstraintLayout) layoutContent.getTag();
        }
        itemView.setTag(this);
    }
}
