package com.nankai.designlayout.tabs;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by nankai on 11/8/2016.
 */

public class TabAdapter extends FragmentPagerAdapter {
    private List<Fragment> fragments;

    public TabAdapter(FragmentManager fm) {
        super(fm);
        fragments = new ArrayList<>();
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    public void onClear() {
        fragments.clear();
    }

    public void addFragment(Fragment... fragments) {
        if (fragments == null)
            return;
        if (fragments.length > 0) {
            if (this.fragments == null)
                this.fragments = new ArrayList<>();
            else
                this.fragments.clear();

            List<Fragment> fragmentsNews = new ArrayList<>();
            fragmentsNews.addAll(Arrays.asList(fragments));
            Log.d("TabAdapter", fragmentsNews.size() + " length fragments");
            this.fragments.addAll(fragmentsNews);
            notifyDataSetChanged();
        }
    }

    public int geLength() {
        if (fragments == null)
            return 0;
        return fragments.size();
    }
}
