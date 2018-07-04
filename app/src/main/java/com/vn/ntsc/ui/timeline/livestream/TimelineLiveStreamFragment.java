package com.vn.ntsc.ui.timeline.livestream;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import com.vn.ntsc.repository.model.timeline.BuzzListResponse;
import com.vn.ntsc.ui.timeline.core.TimelineFragment;
import com.vn.ntsc.ui.timeline.livestream.header.TimelineLiveStreamViewHolder;
import com.vn.ntsc.utils.LogUtils;

/**
 * Created by nankai on 12/14/2017.
 */

public abstract class TimelineLiveStreamFragment extends TimelineFragment {
    //----------------------------------------------------------------
    //------------------------ Variable ------------------------------
    //----------------------------------------------------------------

    TimelineLiveStreamViewHolder liveStreamViewHolder;

    @Override
    protected void onCreateView(View rootView, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(rootView, container, savedInstanceState);

        liveStreamViewHolder = new TimelineLiveStreamViewHolder();
        liveStreamViewHolder.setFragment(this);
        liveStreamViewHolder.initInjectView(adapter);
    }

    //----------------------------------------------------------------
    //------------------------ Loading -------------------------------
    //----------------------------------------------------------------
    @Override
    public void onRefresh() {
        onRequestBuzzList(0);
    }

    @Override
    public void onLoadMoreRequested() {
        super.onLoadMoreRequested();
    }

    //----------------------------------------------------------
    //----------------------Server event -----------------------
    //----------------------------------------------------------
    @Override
    public void onGetRoomLiveStream(BuzzListResponse response) {
        LogUtils.i(TAG,"Get Room LiveStream success");
        liveStreamViewHolder.setData(response.data);
    }

    @Override
    public void onTimelineLiveStreamEmptyView() {
        super.onTimelineLiveStreamEmptyView();
        LogUtils.i(TAG,"Timeline LiveStream is EmptyView");
        liveStreamViewHolder.setData(null);
    }
}
