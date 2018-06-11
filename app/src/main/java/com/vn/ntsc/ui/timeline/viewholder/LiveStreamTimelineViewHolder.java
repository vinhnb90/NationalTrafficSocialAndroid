package com.vn.ntsc.ui.timeline.viewholder;

import android.graphics.Typeface;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vn.ntsc.R;
import com.vn.ntsc.repository.model.timeline.datas.BuzzBean;
import com.vn.ntsc.ui.timeline.core.TimelineListener;
import com.vn.ntsc.utils.Constants;
import com.vn.ntsc.widget.views.textview.TextViewVectorCompat;

import butterknife.BindView;

/**
 * Created by nankai on 10/25/2017.
 */

public class LiveStreamTimelineViewHolder extends BaseMediaTimelineViewHolder {

    @BindView(R.id.view)
    TextViewVectorCompat view;
    @BindView(R.id.title_time)
    TextView titleTime;
    @BindView(R.id.layout_timeline_live_stream)
    RelativeLayout layoutTimelineLiveStream;

    public LiveStreamTimelineViewHolder(View itemView,int viewType) {
        super(itemView, viewType);
        view.setTypeface(null, Typeface.BOLD);
        titleTime.setTypeface(null, Typeface.BOLD);
    }

    @Override
    public void onBindView(final BuzzBean bean, final int position, final TimelineListener listener) {
        super.onBindView(bean, position, listener);

        if (bean.isApproved == Constants.IS_APPROVED){
            loadImageLiveStreamFillWidth(bean.listChildBuzzes.get(0).thumbnailUrl,imageView);
        }else {
            loadImageLiveStreamFillWidthBlur(bean.listChildBuzzes.get(0).thumbnailUrl,imageView);
        }

        view.setText(bean.listChildBuzzes.get(0).currentViewNumber);

        //time location
        if (bean.listChildBuzzes.get(0).streamStatus.equals(Constants.LIVE_STREAM_ON)) {
            int seconds = Integer.parseInt(bean.listChildBuzzes.get(0).streamDuration);
            int minutes = seconds / 60;
            int hours = minutes / 60;
            minutes %= 60;
            seconds %= 60;
            hours %= 12;

            layoutTimelineLiveStream.setVisibility(View.VISIBLE);
            titleTime.setBackgroundResource(R.drawable.bg_red_text_radius_50);

            if (minutes>0){
                titleTime.setText(String.format(itemView.getResources().getString(R.string.live_streamed_total_time), String.format("%02d:%02d", hours, minutes)));
            }else {
                titleTime.setText(String.format(itemView.getResources().getString(R.string.live_streamed_total_time), String.format("%02d:%02d", minutes, seconds)));
            }

        } else {
            layoutTimelineLiveStream.setVisibility(View.GONE);
        }

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onShowLiveStream(bean, position, 0, imageView);
            }
        });
    }
}