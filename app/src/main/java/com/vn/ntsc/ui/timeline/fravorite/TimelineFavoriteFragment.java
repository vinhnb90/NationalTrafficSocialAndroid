package com.vn.ntsc.ui.timeline.fravorite;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import com.vn.ntsc.repository.TypeView;
import com.vn.ntsc.repository.model.timeline.BuzzListRequest;
import com.vn.ntsc.repository.model.timeline.datas.BuzzBean;
import com.vn.ntsc.repository.preferece.UserPreferences;
import com.vn.ntsc.ui.timeline.livestream.TimelineLiveStreamFragment;

/**
 * Created by nankai on 12/13/2017.
 */

public class TimelineFavoriteFragment extends TimelineLiveStreamFragment {
    //----------------------------------------------------------------
    //------------------------ Instance ------------------------------
    //----------------------------------------------------------------
    public static TimelineFavoriteFragment newInstance(@TypeView.TypeViewTimeline int typeView) {
        Bundle args = new Bundle();
        TimelineFavoriteFragment fragment = new TimelineFavoriteFragment();
        args.putInt(BUNDLE_TYPE, typeView);
        fragment.setArguments(args);
        return fragment;
    }

    //----------------------------------------------------------------
    //------------------------ life cycle ----------------------------
    //----------------------------------------------------------------
    @Override
    protected void onCreateView(View rootView, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getTimelineComponent().inject(this);
        super.onCreateView(rootView, container, savedInstanceState);
    }

    @Override
    protected void setUserVisibleHint() {
        mSwipeRefreshLayout.setRefreshing(true);

        UserPreferences userPreferences = UserPreferences.getInstance();
        String token = userPreferences.getToken();
        double longitude = 0;
        double latitude = 0;

        BuzzListRequest buzzListRequest = new BuzzListRequest(token,
                null,
                TypeView.TypeViewTimeline.TIMELINE_LIVE_STREAM,
                true,
                longitude, latitude,
                0,
                TAKE_NUMBER);
        getPresenter().getRoomLiveStream(buzzListRequest, typeView, 0);

        onRequestBuzzList(0);
    }

    //----------------------------------------------------------------
    //------------------------ Loading -------------------------------
    //----------------------------------------------------------------
    @Override
    public void onRefresh() {
        super.onRefresh();
        UserPreferences userPreferences = UserPreferences.getInstance();
        String token = userPreferences.getToken();
        double longitude = 0;
        double latitude = 0;

        BuzzListRequest buzzListRequest = new BuzzListRequest(token,
                null,
                TypeView.TypeViewTimeline.TIMELINE_LIVE_STREAM,
                true,
                longitude, latitude,
                0,
                TAKE_NUMBER);
        getPresenter().getRoomLiveStream(buzzListRequest, typeView, 0);
    }

    //----------------------------------------------------------
    //---------------------- Update data -----------------------
    //----------------------------------------------------------

    @Override
    public void onItemTimelineClick(BuzzBean item, int position, View view) {

    }
}
