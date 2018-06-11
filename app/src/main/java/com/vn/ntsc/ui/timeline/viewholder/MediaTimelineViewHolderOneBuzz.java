package com.vn.ntsc.ui.timeline.viewholder;

import android.view.View;

import com.vn.ntsc.repository.model.timeline.datas.BuzzBean;
import com.vn.ntsc.ui.timeline.core.TimelineListener;
import com.vn.ntsc.utils.Constants;

/**
 * Created by nankai on 10/25/2017.
 */

public class MediaTimelineViewHolderOneBuzz extends BaseMediaTimelineViewHolder {

    public MediaTimelineViewHolderOneBuzz(View itemView,
                                          int viewType) {
        super(itemView, viewType);
    }


    @Override
    public void onBindViewTemplate(BuzzBean bean, int position, TimelineListener listener) {
        super.onBindViewTemplate(bean, position, listener);

        loadImageLocal(bean.listChildBuzzes.get(0).thumbnailUrl, imageView);
    }

    @Override
    public void onBindView(final BuzzBean bean, final int position, final TimelineListener listener) {
        super.onBindView(bean, position, listener);

        final int approved = bean.listChildBuzzes.get(0).isApp;

        if (bean.childNumber > 1) {
            if (approved == Constants.IS_APPROVED) {
                loadImageFillWidth(bean.listChildBuzzes.get(0).thumbnailUrl, imageView);
            } else {
                loadImageFillWidthBlur(bean.listChildBuzzes.get(0).thumbnailUrl, imageView);
            }
        } else {
            if (approved == Constants.IS_APPROVED) {
                loadImage(bean.listChildBuzzes.get(0).thumbnailUrl, imageView);
            } else {
                loadImageBlur(bean.listChildBuzzes.get(0).thumbnailUrl, imageView);
            }
        }

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onShowImageDetail(bean, position, 0, v);
            }
        });
    }
}
