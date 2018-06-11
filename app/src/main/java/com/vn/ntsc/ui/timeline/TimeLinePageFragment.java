package com.vn.ntsc.ui.timeline;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nankai.designlayout.tabs.buider.Builder;
import com.vn.ntsc.R;
import com.vn.ntsc.repository.TypeView;
import com.vn.ntsc.ui.timeline.all.TimelineAllFragment;
import com.vn.ntsc.ui.timeline.fravorite.TimelineFavoriteFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by nankai on 8/8/2017.
 */

public class TimeLinePageFragment extends Fragment {
    @BindView(R.id.fragment_timeline_page_view_pager)
    ViewPager viewPager;
    @BindView(R.id.fragment_timeline_page_tab_layout)
    TabLayout tabLayout;

    public static TimeLinePageFragment newInstance() {
        return new TimeLinePageFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_timeline_page, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        new Builder(this, tabLayout, viewPager)
                .addFragments(TimelineAllFragment.newInstance(TypeView.TypeViewTimeline.TIMELINE_ALL),
                        TimelineFavoriteFragment.newInstance(TypeView.TypeViewTimeline.TIMELINE_FAVORITES))
                .setTitle(R.string.menu_new_feed_all, R.string.menu_new_feed_favorite);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
