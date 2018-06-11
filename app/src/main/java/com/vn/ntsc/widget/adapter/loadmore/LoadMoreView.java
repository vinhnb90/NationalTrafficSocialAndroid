package com.vn.ntsc.widget.adapter.loadmore;

import android.support.annotation.IdRes;
import android.support.annotation.IntDef;
import android.support.annotation.LayoutRes;
import android.view.View;

import com.vn.ntsc.widget.adapter.BaseViewHolder;
import com.vn.ntsc.widget.adapter.MultifunctionAdapter;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.vn.ntsc.widget.adapter.loadmore.LoadMoreView.Status.*;

/**
 * Created by nankai on 2016/11/11.
 */

public abstract class LoadMoreView {

    @Status
    private int mLoadMoreStatus = Status.STATUS_DEFAULT;

    private boolean mLoadMoreEndGone = false;

    @IntDef({Status.STATUS_DEFAULT, Status.STATUS_LOADING, Status.STATUS_FAIL, Status.STATUS_END, Status.STATUS_EMPTY})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Status{
        int STATUS_DEFAULT = 1;
        int STATUS_LOADING = 2;
        int STATUS_FAIL = 3;
        int STATUS_END = 4;
        int STATUS_EMPTY = 5;
    }

    @Status
    public int getLoadMoreStatus() {
        return mLoadMoreStatus;
    }

    public void setLoadMoreStatus(@Status int loadMoreStatus) {
        this.mLoadMoreStatus = loadMoreStatus;
    }

    public void convert(BaseViewHolder holder) {
        switch (mLoadMoreStatus) {
            case Status.STATUS_LOADING:
                visibleEmpty(holder, false);
                visibleLoading(holder, true);
                visibleLoadFail(holder, false);
                visibleLoadEnd(holder, false);
                break;
            case Status.STATUS_FAIL:
                visibleEmpty(holder, false);
                visibleLoading(holder, false);
                visibleLoadFail(holder, true);
                visibleLoadEnd(holder, false);
                break;
            case Status.STATUS_END:
                visibleEmpty(holder, false);
                visibleLoading(holder, false);
                visibleLoadFail(holder, false);
                visibleLoadEnd(holder, true);
                break;
            case Status.STATUS_DEFAULT:
                visibleEmpty(holder, false);
                visibleLoading(holder, false);
                visibleLoadFail(holder, false);
                visibleLoadEnd(holder, false);
                break;
            case Status.STATUS_EMPTY:
                visibleEmpty(holder, true);
                visibleLoading(holder, false);
                visibleLoadFail(holder, false);
                visibleLoadEnd(holder, false);
                break;
        }
    }

    private void visibleLoading(BaseViewHolder holder, boolean visible) {
        setVisible(holder, getLoadingViewId(), visible);
    }

    private void visibleEmpty(BaseViewHolder holder, boolean visible) {
        setVisible(holder, getLoadEmptyViewId(), visible);
    }

    private void visibleLoadFail(BaseViewHolder holder, boolean visible) {
        setVisible(holder, getLoadFailViewId(), visible);
    }

    private void visibleLoadEnd(BaseViewHolder holder, boolean visible) {
        final int loadEndViewId = getLoadEndViewId();
        if (loadEndViewId != 0) {
            setVisible(holder, loadEndViewId, visible);
        }
    }

    public void setVisible(BaseViewHolder holder, @IdRes int viewId, boolean visible) {
        View view = holder.itemView.findViewById(viewId);
        view.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
    }

    public final void setLoadMoreEndGone(boolean loadMoreEndGone) {
        this.mLoadMoreEndGone = loadMoreEndGone;
    }

    public final boolean isLoadEndMoreGone() {
        if (getLoadEndViewId() == 0) {
            return true;
        }
        return mLoadMoreEndGone;
    }

    /**
     * No more data is hidden
     *
     * @return true for no more data hidden load more
     * @deprecated Use {@link MultifunctionAdapter#loadMoreEnd(boolean)} instead.
     */
    @Deprecated
    public boolean isLoadEndGone() {
        return mLoadMoreEndGone;
    }

    /**
     * load more layout
     *
     * @return LayoutRes
     */
    @LayoutRes
    public abstract int getLayoutId();

    /**
     * layout_loading view
     *
     * @return @IdRes
     */
    @IdRes
    protected abstract int getLoadingViewId();

    /**
     * load fail view
     *
     * @return @IdRes
     */
    @IdRes
    protected abstract int getLoadFailViewId();

    /**
     * load end view, you can return 0
     *
     * @return @IdRes
     */
    @IdRes
    protected abstract int getLoadEndViewId();

    /**
     * load end view, you can return 0
     *
     * @return @IdRes
     */
    @IdRes
    protected abstract int getLoadEmptyViewId();
}
