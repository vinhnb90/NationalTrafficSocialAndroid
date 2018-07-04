package com.vn.ntsc.ui.timeline.viewholder;

import android.view.View;

import com.vn.ntsc.repository.model.timeline.datas.BuzzBean;
import com.vn.ntsc.ui.timeline.core.TimelineListener;
import com.vn.ntsc.utils.Constants;

import java.util.Objects;

/**
 * Created by nankai on 10/25/2017.
 */

public class VideoAudioTimelineViewHolder extends BaseMediaTimelineViewHolder {

    public VideoAudioTimelineViewHolder(View itemView,
                                        int viewType) {
        super(itemView, viewType);
    }

    @Override
    public void onBindViewTemplate(final BuzzBean bean, final int position, TimelineListener listener) {
        super.onBindViewTemplate(bean, position, listener);
        loadImageLocal(bean.listChildBuzzes.get(0).thumbnailUrl, imageView);
    }

    @Override
    public void onBindView(final BuzzBean bean, final int position, final TimelineListener listener) {
        super.onBindView(bean, position, listener);
        final int approved = bean.listChildBuzzes.get(0).isApp;
        if (imageView != null) {
            if (approved == Constants.IS_APPROVED) {
                loadImageFillWidth(bean.listChildBuzzes.get(0).thumbnailUrl, imageView);
            } else {
                loadImageFillWidthBlur(bean.listChildBuzzes.get(0).thumbnailUrl, imageView);
            }
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (bean.isApproved == Constants.IS_APPROVED && approved == Constants.IS_APPROVED) {
                        listener.onDisplayImageDetailScreen(bean, position, 0, v);
                    } else {
                        listener.onApproval(bean, position, v);
                    }
                }
            });
        }
    }
}