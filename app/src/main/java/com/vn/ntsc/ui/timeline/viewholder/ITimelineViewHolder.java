package com.vn.ntsc.ui.timeline.viewholder;

import com.bumptech.glide.RequestManager;
import com.vn.ntsc.repository.model.timeline.datas.BuzzBean;
import com.vn.ntsc.ui.timeline.core.TimelineListener;

/**
 * init interface for Timeline view holder
 * Created by nankai on 2/6/2018.
 */

public interface ITimelineViewHolder {

    /**
     * Use {@link RequestManager} to manage the resume and pause image layout_loading
     *
     * @param glide {@link RequestManager}
     * @return {@link BaseTimelineViewHolder}
     */
    BaseTimelineViewHolder initGlide(RequestManager glide);

    /**
     * @param isTemplate If true, this is a tempate item, otherwise the item is actually retrieved from the server response
     * @param item {@link BuzzBean}
     * @param position position item
     * @param listener {@link TimelineListener}
     * @return {@link BaseTimelineViewHolder}
     */
    BaseTimelineViewHolder initBindView(boolean isTemplate, BuzzBean item, int position, TimelineListener listener);
}
