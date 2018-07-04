package com.vn.ntsc.ui.timeline.viewholder;

import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vn.ntsc.R;
import com.vn.ntsc.repository.model.timeline.datas.BuzzBean;
import com.vn.ntsc.ui.timeline.core.TimelineListener;
import com.vn.ntsc.utils.Constants;

import butterknife.BindView;

/**
 * Created by nankai on 10/25/2017.
 */

public class ShareLiveStreamTimelineViewHolder extends ShareTimelineViewHolder {

    @Nullable
    @BindView(R.id.view)
    TextView view;
    @Nullable
    @BindView(R.id.title_time)
    TextView titleTime;
    @Nullable
    @BindView(R.id.layout_timeline_live_stream)
    RelativeLayout layoutTimelineLiveStream;


    public ShareLiveStreamTimelineViewHolder(View itemView, int viewType) {
        super(itemView, viewType);
        view.setTypeface(null, Typeface.BOLD);
        titleTime.setTypeface(null, Typeface.BOLD);
    }

    @Override
    public void onBindView(final BuzzBean bean, final int position, final TimelineListener listener) {
        super.onBindView(bean, position, listener);
        //time location
        if (bean.shareDetailBean.listChildBuzzes.get(0).streamStatus.equals(Constants.LIVE_STREAM_ON)) {
            view.setText(bean.shareDetailBean.listChildBuzzes.get(0).currentViewNumber);
            int seconds = Integer.parseInt(bean.shareDetailBean.listChildBuzzes.get(0).streamDuration);
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
    }
}