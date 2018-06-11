package com.nankai.designlayout.tabs;

/**
 * Created by nankai on 11/8/2016.
 */

public interface TabListener {
    void onPageScrolled(int position, float positionOffset, int positionOffsetPixels);

    void onPageSelected(int position);

    void onPageScrollStateChanged(int state);
}
