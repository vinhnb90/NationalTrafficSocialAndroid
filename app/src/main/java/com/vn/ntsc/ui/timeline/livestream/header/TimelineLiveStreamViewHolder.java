package com.vn.ntsc.ui.timeline.livestream.header;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.vn.ntsc.R;
import com.vn.ntsc.repository.model.timeline.datas.BuzzBean;
import com.vn.ntsc.repository.model.timeline.datas.sub.ListBuzzChild;
import com.vn.ntsc.services.UserLiveStreamService;
import com.vn.ntsc.ui.main.MainActivity;
import com.vn.ntsc.ui.mediadetail.timeline.TimelineMediaActivity;
import com.vn.ntsc.ui.timeline.adapter.TimelineAdapter;
import com.vn.ntsc.ui.timeline.core.TimelineViewHolder;
import com.vn.ntsc.ui.timeline.livestream.LiveStreamListener;
import com.vn.ntsc.ui.timeline.livestream.TimelineLiveStreamAdapter;
import com.vn.ntsc.utils.Constants;
import com.vn.ntsc.widget.adapter.MultifunctionAdapter;
import com.vn.ntsc.widget.views.images.CircleImageView;

import java.util.List;

import butterknife.BindView;

/**
 * Created by nankai on 4/26/2018.
 */

public class TimelineLiveStreamViewHolder extends TimelineViewHolder<BuzzBean> implements LiveStreamListener<BuzzBean> {

    @BindView(R.id.list_live_stream)
    RecyclerView liveStreamRecyclerView;
    @BindView(R.id.layout_live_stream)
    CardView layoutLiveStream;
    @BindView(R.id.live_stream)
    CircleImageView liveStream;

    TimelineLiveStreamAdapter liveStreamAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.layout_timeline_live_stream;
    }

    @Override
    public void initInjectView(TimelineAdapter adapter) {
        liveStreamRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        liveStreamAdapter = new TimelineLiveStreamAdapter(this);
        liveStreamAdapter.openLoadAnimation(MultifunctionAdapter.SLIDEIN_RIGHT);
        liveStreamAdapter.isUseEmpty(true);
        liveStreamAdapter.bindToRecyclerView(liveStreamRecyclerView);
        adapter.addHeaderView(view);
        liveStream.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onStartLiveStreamListener(v);
            }
        });
    }

    @Override
    public void setData(List<BuzzBean> datas) {
        if (datas == null || datas.size() <= 0) {
            liveStreamAdapter.setEmptyView(R.layout.layout_timeline_list_live_stream_empty);
        } else {
            liveStreamAdapter.setNewData(datas);
        }
    }

    @Override
    public void onItemLiveStreamListener(BuzzBean item, int position, View view) {
        if (getContext() instanceof MainActivity) {
            ListBuzzChild buzzChild = item.listChildBuzzes.get(0);
            if (buzzChild.streamStatus.equals(Constants.LIVE_STREAM_ON)) {
                ((MainActivity) getContext()).onLiveStreamOption(UserLiveStreamService.Mode.VIEW, item.buzzId, item.shareId, item.listChildBuzzes.get(0).thumbnailUrl);
            } else {
                TimelineMediaActivity.launch((AppCompatActivity) getContext(), view, item, 0);
            }
        }
    }

    private void onStartLiveStreamListener(View view) {
        if (getContext() instanceof MainActivity)
            ((MainActivity) getContext()).onLiveStreamOption(UserLiveStreamService.Mode.CHAT);
    }
}
