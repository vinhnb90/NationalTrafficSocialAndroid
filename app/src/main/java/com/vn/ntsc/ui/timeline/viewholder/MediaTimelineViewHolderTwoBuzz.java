package com.vn.ntsc.ui.timeline.viewholder;

import android.view.View;

import com.vn.ntsc.R;
import com.vn.ntsc.repository.model.timeline.datas.BuzzBean;
import com.vn.ntsc.ui.timeline.core.TimelineListener;
import com.vn.ntsc.utils.Constants;
import com.vn.ntsc.widget.views.images.RecyclingImageView;

import butterknife.BindView;

/**
 * Created by nankai on 10/25/2017.
 */

public class MediaTimelineViewHolderTwoBuzz extends MediaTimelineViewHolderOneBuzz {

    @BindView(R.id.item_timeline_image_view_2)
    RecyclingImageView imageView2;
    @BindView(R.id.item_timeline_play_video_2)
    RecyclingImageView imagePlayView2;

    public MediaTimelineViewHolderTwoBuzz(View itemView,
                                          int viewType) {
        super(itemView, viewType);
    }

    @Override
    public void onBindViewTemplate(BuzzBean bean, int position, TimelineListener listener) {
        super.onBindViewTemplate(bean, position, listener);
        displayVideoPlayIcon(bean.listChildBuzzes.get(0).buzzType, imagePlayView);
        displayVideoPlayIcon(bean.listChildBuzzes.get(1).buzzType, imagePlayView2);
        loadImageLocal(bean.listChildBuzzes.get(1).thumbnailUrl, imageView2);
    }

    @Override
    public void onBindView(final BuzzBean bean, final int position, final TimelineListener listener) {
        super.onBindView(bean, position, listener);

        displayVideoPlayIcon(bean.listChildBuzzes.get(0).buzzType, imagePlayView);

        final int approved = bean.listChildBuzzes.get(1).isApp;
        if (imageView2 != null) {
            if (approved == Constants.IS_APPROVED) {
                loadImage(bean.listChildBuzzes.get(1).thumbnailUrl, imageView2);
            } else {
                loadImageBlur(bean.listChildBuzzes.get(1).thumbnailUrl, imageView2);
            }
            
            displayVideoPlayIcon(bean.listChildBuzzes.get(1).buzzType, imagePlayView2);

            imageView2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (bean.isApproved == Constants.IS_APPROVED && approved == Constants.IS_APPROVED) {
                        listener.onDisplayImageDetailScreen(bean, position, 1, v);
                    } else {
                        listener.onApproval(bean, position, v);
                    }
                }
            });
        }
    }
}
