package com.vn.ntsc.widget.adapter.loadmore;


import com.vn.ntsc.R;

/**
 * Created by nankai on 2016/10/11.
 */

public final class SimpleLoadMoreView extends LoadMoreView {

    @Override
    public int getLayoutId() {
        return R.layout.view_load_more;
    }

    @Override
    protected int getLoadingViewId() {
        return R.id.load_more_loading_view;
    }

    @Override
    protected int getLoadFailViewId() {
        return R.id.load_more_load_fail_view;
    }

    @Override
    protected int getLoadEndViewId() {
        return R.id.load_more_load_end_view;
    }

    @Override
    protected int getLoadEmptyViewId() {
        return R.id.load_more_load_empty;
    }
}
