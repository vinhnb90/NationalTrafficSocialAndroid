package com.vn.ntsc.ui.comments.helper;

import android.view.View;

import com.vn.ntsc.repository.model.timeline.datas.BuzzBean;
import com.vn.ntsc.utils.Constants;

public class VideoAudioTimelineHelper extends BaseMediaTimelineHelper{

    public VideoAudioTimelineHelper(View itemView, int viewType) {
        super(itemView, viewType);
    }

    @Override
    public void onBindView(final BuzzBean bean) {
        super.onBindView(bean);
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
                        listener.onDisplayImageDetailScreen(bean, 0, v);
                    } else {
                        listener.onApproval(bean, v);
                    }
                }
            });
        }
    }
}
