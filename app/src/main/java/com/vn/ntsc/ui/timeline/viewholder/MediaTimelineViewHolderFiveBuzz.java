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

public class MediaTimelineViewHolderFiveBuzz extends MediaTimelineViewHolderFourBuzz {

    @BindView(R.id.item_timeline_image_view_5)
    RecyclingImageView imageView5;
    @BindView(R.id.item_timeline_play_video_5)
    RecyclingImageView imagePlayView5;

    public MediaTimelineViewHolderFiveBuzz(View itemView, int viewType) {
        super(itemView, viewType);
    }

    @Override
    public void onBindViewTemplate(BuzzBean bean, int position, TimelineListener listener) {
        super.onBindViewTemplate(bean, position, listener);
        displayVideoPlayIcon(bean.listChildBuzzes.get(4).buzzType, imagePlayView5);
        loadImageLocal(bean.listChildBuzzes.get(4).thumbnailUrl, imageView5);
    }

    @Override
    public void onBindView(final BuzzBean bean, final int position, final TimelineListener listener) {
        super.onBindView(bean, position, listener);

        if (bean.listChildBuzzes.get(4).isApp == Constants.IS_APPROVED) {
            loadImage(bean.listChildBuzzes.get(4).thumbnailUrl, imageView5);
        } else {
            loadImageBlur(bean.listChildBuzzes.get(4).thumbnailUrl, imageView5);
        }

        displayVideoPlayIcon(bean.listChildBuzzes.get(4).buzzType, imagePlayView5);

        imageView5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onShowImageDetail(bean, position, 4, v);
            }
        });
    }
}
