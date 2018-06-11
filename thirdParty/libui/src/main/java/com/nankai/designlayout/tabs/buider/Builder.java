package com.nankai.designlayout.tabs.buider;

import android.content.Context;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.nankai.designlayout.tabs.TabAdapter;
import com.nankai.designlayout.tabs.TabListener;

/**
 * Created by nankai on 11/8/2016.
 */

public class Builder implements ViewPager.OnPageChangeListener {

    public TabAdapter tabAdapter;
    public ViewPager viewPager;
    public TabLayout tabLayout;
    private Context context;
    public TabListener onTabViewListener;
    private Header header;

    /**
     * @param viewPager getViewPager()
     * @param tabLayout getTabLayout()
     * @param fragment  View root
     */
    public Builder(Fragment fragment, TabLayout tabLayout, ViewPager viewPager) {
        this.context = fragment.getActivity();
        this.tabLayout = tabLayout;
        this.viewPager = viewPager;

        this.tabAdapter = new TabAdapter(fragment.getChildFragmentManager());
        this.viewPager.setAdapter(tabAdapter);
        this.tabLayout.setupWithViewPager(this.viewPager);
    }


    /**
     * @param viewPager getViewPager()
     * @param tabLayout getTabLayout()
     * @param activity  View root
     */
    public Builder(AppCompatActivity activity, TabLayout tabLayout, ViewPager viewPager) {
        this.context = activity.getApplicationContext();
        this.tabLayout = tabLayout;
        this.viewPager = viewPager;

        this.tabAdapter = new TabAdapter(activity.getSupportFragmentManager());
        this.viewPager.setAdapter(tabAdapter);
        this.tabLayout.setupWithViewPager(this.viewPager);
    }

    /**
     * @param fragments type Fragment[]
     * @return Header
     */
    public Header addFragments(Fragment... fragments) {
        if (tabAdapter == null)
            return null;
        tabAdapter.onClear();
        tabAdapter.addFragment(fragments);
        viewPager.setOffscreenPageLimit(fragments.length);
        header = new Header(this, context, tabLayout, tabAdapter);
        return header;
    }

    public TabAdapter getTabAdapter() {
        return tabAdapter;
    }

    public Header with() {
        return header;
    }

    /**
     * TabListener
     *
     * @param onTabViewListener TabListener
     */
    public Builder addOnPageChangeListener(TabListener onTabViewListener) {
        this.onTabViewListener = onTabViewListener;
        viewPager.addOnPageChangeListener(this);
        return this;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        //TODO
        Log.d("tabView", "onPageScrolled");
        if (onTabViewListener == null)
            Log.d("onTabViewListener", "null");
        else
            onTabViewListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
    }

    @Override
    public void onPageSelected(int position) {
        //TODO
        Log.d("tabView", "onPageSelected");
        if (onTabViewListener == null)
            Log.d("onTabViewListener", "null");
        else
            onTabViewListener.onPageSelected(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        //TODO
        Log.d("tabView", "onPageScrollStateChanged");
        if (onTabViewListener == null)
            Log.d("onTabViewListener", "null");
        else {
            onTabViewListener.onPageScrollStateChanged(state);
        }
    }
}
