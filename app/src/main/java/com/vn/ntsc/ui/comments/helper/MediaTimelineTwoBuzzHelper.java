package com.vn.ntsc.ui.comments.helper;

import android.view.View;

import com.vn.ntsc.R;
import com.vn.ntsc.repository.model.timeline.datas.BuzzBean;
import com.vn.ntsc.utils.Constants;
import com.vn.ntsc.widget.views.images.RecyclingImageView;

import butterknife.BindView;

public class MediaTimelineTwoBuzzHelper extends MediaTimelineOneBuzzHelper {

    @BindView(R.id.item_timeline_image_view_2)
    RecyclingImageView imageView2;
    @BindView(R.id.item_timeline_play_video_2)
    RecyclingImageView imagePlayView2;

    public MediaTimelineTwoBuzzHelper(View itemView, int viewType) {
        super(itemView, viewType);
    }


    @Override
    public void onBindView(final BuzzBean bean) {
        super.onBindView(bean);

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
                        listener.onDisplayImageDetailScreen(bean, 1, v);
                    } else {
                        listener.onApproval(bean, v);
                    }
                }
            });
        }
    }
}
