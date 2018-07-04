package com.vn.ntsc.ui.comments.helper;

import android.view.View;
import android.widget.TextView;
import com.vn.ntsc.R;
import com.vn.ntsc.repository.model.timeline.datas.BuzzBean;
import com.vn.ntsc.utils.Constants;

import butterknife.BindView;

public class MediaTimelineMoreBuzzHelper extends MediaTimelineFiveBuzzHelper {

    @BindView(R.id.item_timeline_text_view_more)
    TextView imageViewMore;

    public MediaTimelineMoreBuzzHelper(View itemView, int viewType) {
        super(itemView, viewType);
    }

    @Override
    public void onBindView(final BuzzBean bean) {
        super.onBindView(bean);
        if (imageViewMore != null) {
            imageViewMore.setText((bean.childNumber - 5) + "+");
            imageViewMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (bean.isApproved == Constants.IS_APPROVED) {
                        listener.onDisplayImageDetailScreen(bean, 4, v);
                    } else {
                        listener.onApproval(bean, v);
                    }
                }
            });
        }
    }
}

