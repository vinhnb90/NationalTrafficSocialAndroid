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

public class MediaTimelineViewHolderFourBuzz extends MediaTimelineViewHolderThreeBuzz {

    @BindView(R.id.item_timeline_image_view_4)
    RecyclingImageView imageView4;
    @BindView(R.id.item_timeline_play_video_4)
    RecyclingImageView imagePlayView4;

    public MediaTimelineViewHolderFourBuzz(View itemView,
                                           int viewType) {
        super(itemView, viewType);
    }

    @Override
    public void onBindViewTemplate(BuzzBean bean, int position, TimelineListener listener) {
        super.onBindViewTemplate(bean, position, listener);

        displayVideoPlayIcon(bean.listChildBuzzes.get(3).buzzType, imagePlayView4);
        loadImageLocal(bean.listChildBuzzes.get(3).thumbnailUrl, imageView4);
    }

    @Override
    public void onBindView(final BuzzBean bean, final int position, final TimelineListener listener) {
        super.onBindView(bean, position, listener);

        if (bean.listChildBuzzes.get(3).isApp == Constants.IS_APPROVED) {
            loadImage(bean.listChildBuzzes.get(3).thumbnailUrl, imageView4);
        } else {
            loadImageBlur(bean.listChildBuzzes.get(3).thumbnailUrl, imageView4);
        }

        displayVideoPlayIcon(bean.listChildBuzzes.get(3).buzzType, imagePlayView4);


        imageView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onShowImageDetail(bean, position, 3, v);
            }
        });
    }
}