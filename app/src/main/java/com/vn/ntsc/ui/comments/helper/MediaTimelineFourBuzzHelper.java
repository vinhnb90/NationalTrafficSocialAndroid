package com.vn.ntsc.ui.comments.helper;

import android.view.View;

import com.vn.ntsc.R;
import com.vn.ntsc.repository.model.timeline.datas.BuzzBean;
import com.vn.ntsc.utils.Constants;
import com.vn.ntsc.widget.views.images.RecyclingImageView;

import butterknife.BindView;

public class MediaTimelineFourBuzzHelper extends MediaTimelineThreeBuzzHelper {

    @BindView(R.id.item_timeline_image_view_4)
    RecyclingImageView imageView4;
    @BindView(R.id.item_timeline_play_video_4)
    RecyclingImageView imagePlayView4;

    public MediaTimelineFourBuzzHelper(View itemView, int viewType) {
        super(itemView, viewType);
    }

    @Override
    public void onBindView(final BuzzBean bean) {
        super.onBindView(bean);
        final int approved = bean.listChildBuzzes.get(3).isApp;
        if (imageView4 != null) {
            if (approved == Constants.IS_APPROVED) {
                loadImage(bean.listChildBuzzes.get(3).thumbnailUrl, imageView4);
            } else {
                loadImageBlur(bean.listChildBuzzes.get(3).thumbnailUrl, imageView4);
            }

            displayVideoPlayIcon(bean.listChildBuzzes.get(3).buzzType, imagePlayView4);


            imageView4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (bean.isApproved == Constants.IS_APPROVED && approved == Constants.IS_APPROVED) {
                        listener.onDisplayImageDetailScreen(bean, 3, v);
                    } else {
                        listener.onApproval(bean, v);
                    }
                }
            });
        }
    }
}