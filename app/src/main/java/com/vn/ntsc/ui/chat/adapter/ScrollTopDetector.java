package com.vn.ntsc.ui.chat.adapter;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

/**
 * Created by dev22 on 9/15/17.
 */
public abstract class ScrollTopDetector extends RecyclerView.OnScrollListener {
    private final LinearLayoutManager layoutManager;
    private final int mThreshold;
    /**
     * true: scroll down
     */
    private boolean isScrollDown;

    /**
     * trigger when scroll to top, depend on threshold number (when have enough item to scroll)
     */
    protected abstract void onScrollTop();

    /**
     * detect scroll to bottom of recycle view
     *
     * @param layoutManager current layout manager
     * @param threshold     should 2 >= threshold < 5
     */
    public ScrollTopDetector(LinearLayoutManager layoutManager, int threshold) {
        this.layoutManager = layoutManager;
        mThreshold = threshold;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        isScrollDown = dy >= 0;
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
        int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();


            Log.e("ThoNH","state:" + newState);



        // scroll end animation and visible item count < total item count (in case total item count not enought to scroll)
        if (newState == RecyclerView.SCROLL_STATE_IDLE && !isScrollDown && firstVisibleItemPosition - mThreshold <= 0) {
            onScrollTop();
        }
    }
}
