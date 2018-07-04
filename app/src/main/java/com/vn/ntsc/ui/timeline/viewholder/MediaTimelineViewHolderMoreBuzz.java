package com.vn.ntsc.ui.timeline.viewholder;

import android.view.View;
import android.widget.TextView;

import com.vn.ntsc.R;
import com.vn.ntsc.repository.model.timeline.datas.BuzzBean;
import com.vn.ntsc.ui.timeline.core.TimelineListener;
import com.vn.ntsc.utils.Constants;

import butterknife.BindView;

/**
 * Created by nankai on 10/25/2017.
 */

public class MediaTimelineViewHolderMoreBuzz extends MediaTimelineViewHolderFiveBuzz {

    @BindView(R.id.item_timeline_text_view_more)
    TextView imageViewMore;

    public MediaTimelineViewHolderMoreBuzz(View itemView,
                                           int viewType) {
        super(itemView, viewType);
    }

    @Override
    public void onBindViewTemplate(BuzzBean bean, int position, TimelineListener listener) {
        super.onBindViewTemplate(bean, position, listener);
        imageViewMore.setText((bean.childNumber - 5) + "+");
    }

    @Override
    public void onBindView(final BuzzBean bean, final int position, final TimelineListener listener) {
        super.onBindView(bean, position, listener);
        if (imageViewMore != null) {
            imageViewMore.setText((bean.childNumber - 5) + "+");
            imageViewMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (bean.isApproved == Constants.IS_APPROVED) {
                        listener.onDisplayImageDetailScreen(bean, position, 4, v);
                    } else {
                        listener.onApproval(bean, position, v);
                    }
                }
            });
        }
    }
}
