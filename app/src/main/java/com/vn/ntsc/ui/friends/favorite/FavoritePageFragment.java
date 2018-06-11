package com.vn.ntsc.ui.friends.favorite;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import com.nankai.designlayout.tabs.buider.Builder;
import com.vn.ntsc.R;
import com.vn.ntsc.core.views.BaseFragment;
import com.vn.ntsc.repository.TypeView;

import butterknife.BindView;

/**
 * Created by ThoNH on 16/08/2017.
 */

public class FavoritePageFragment extends BaseFragment {
    @BindView(R.id.fragment_favorite_tab_layout)
    TabLayout mTabLayout;
    @BindView(R.id.fragment_favorite_view_pager)
    ViewPager mViewPager;

    public static FavoritePageFragment newInstance() {
        Bundle args = new Bundle();
        FavoritePageFragment fragment = new FavoritePageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_favorite;
    }

    @Override
    protected void onCreateView(View rootView, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

    }

    @Override
    protected void setUserVisibleHint() {
        new Builder(this, mTabLayout, mViewPager)
                .addFragments(FriendsFavoriteFragment.newInstance(TypeView.TypeViewFavorite.ME_FAVORITE),
                        FriendsFavoriteFragment.newInstance(TypeView.TypeViewFavorite.FAVORITE_ME))
                .setTitle(R.string.title_fragment_me_favorite, R.string.title_fragment_favorite_me);
    }
}
