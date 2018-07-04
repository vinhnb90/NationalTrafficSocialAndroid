package com.vn.ntsc.ui.comments.helper;

import android.view.View;

import com.vn.ntsc.R;
import com.vn.ntsc.repository.model.timeline.datas.BuzzBean;
import com.vn.ntsc.utils.Constants;
import com.vn.ntsc.widget.views.images.RecyclingImageView;

import butterknife.BindView;

public class MediaTimelineFiveBuzzHelper extends MediaTimelineFourBuzzHelper {

    @BindView(R.id.item_timeline_image_view_5)
    RecyclingImageView imageView5;
    @BindView(R.id.item_timeline_play_video_5)
    RecyclingImageView imagePlayView5;

    public MediaTimelineFiveBuzzHelper(View itemView, int viewType) {
        super(itemView, viewType);
    }

    @Override
    public void onBindView(final BuzzBean bean) {
        super.onBindView(bean);

        final int approved = bean.listChildBuzzes.get(4).isApp;

        if (imageView5 != null) {
            if (approved == Constants.IS_APPROVED) {
                loadImage(bean.listChildBuzzes.get(4).thumbnailUrl, imageView5);
            } else {
                loadImageBlur(bean.listChildBuzzes.get(4).thumbnailUrl, imageView5);
            }

            displayVideoPlayIcon(bean.listChildBuzzes.get(4).buzzType, imagePlayView5);

            imageView5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (bean.isApproved == Constants.IS_APPROVED && approved == Constants.IS_APPROVED) {
                        listener.onDisplayImageDetailScreen(bean, 4, v);
                    } else {
                        listener.onApproval(bean, v);
                    }
                }
            });
        }
    }
}

