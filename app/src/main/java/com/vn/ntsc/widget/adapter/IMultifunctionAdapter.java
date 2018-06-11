package com.vn.ntsc.widget.adapter;

import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.vn.ntsc.core.model.BaseBean;
import com.vn.ntsc.widget.adapter.animation.BaseAnimation;
import com.vn.ntsc.widget.adapter.loadmore.LoadMoreView;

import java.util.Collection;
import java.util.List;

/**
 * Đây là template cài đặt các phương thức cho {@LinK MultifunctionAdapter}
 */

public interface IMultifunctionAdapter<T extends BaseBean> {

    /**
     * Use with {@link #openLoadAnimation}
     */
    int ALPHAIN = 0x00000001;
    /**
     * Use with {@link #openLoadAnimation}
     */
    int SCALEIN = 0x00000002;
    /**
     * Use with {@link #openLoadAnimation}
     */
    int SLIDEIN_BOTTOM = 0x00000003;
    /**
     * Use with {@link #openLoadAnimation}
     */
    int SLIDEIN_LEFT = 0x00000004;
    /**
     * Use with {@link #openLoadAnimation}
     */
    int SLIDEIN_RIGHT = 0x00000005;
    int HEADER_VIEW = 0x00000111;

    //Animation
    int LOADING_VIEW = 0x00000222;
    int FOOTER_VIEW = 0x00000333;
    int EMPTY_VIEW = 0x00000555;

    /**
     * same as recyclerView.setAdapter(), and save the instance of recyclerView
     */
    void bindToRecyclerView(RecyclerView recyclerView);

    /**
     * @see #setOnLoadMoreListener(MultifunctionAdapter.RequestLoadMoreListener, RecyclerView)
     * @deprecated This method is because it can lead to crash: always call this method while RecyclerView is computing a layout or scrolling.
     * Please use {@link #setOnLoadMoreListener(MultifunctionAdapter.RequestLoadMoreListener, RecyclerView)}
     */
    @Deprecated
    void setOnLoadMoreListener(MultifunctionAdapter.RequestLoadMoreListener requestLoadMoreListener);

    /**
     * @see #setOnLoadMoreListener(MultifunctionAdapter.RequestLoadMoreListener, RecyclerView)
     * @deprecated This method is because it can lead to crash: always call this method while RecyclerView is computing a layout or scrolling.
     * Please use {@link #setOnLoadMoreListener(MultifunctionAdapter.RequestLoadMoreListener, RecyclerView)}
     */
    void setOnLoadMoreListener(MultifunctionAdapter.RequestLoadMoreListener requestLoadMoreListener, RecyclerView recyclerView);

    /**
     * bind recyclerView {@link #bindToRecyclerView(RecyclerView)} before use!
     *
     * @see #disableLoadMoreIfNeed(RecyclerView)
     */
    void disableLoadMoreIfNeed();

    /**
     * check if full page after {@link #setNewData(List)}, if full, it will enable load more again.
     *
     * @param recyclerView your recyclerView
     * @see #setNewData(List)
     */
    void disableLoadMoreIfNeed(RecyclerView recyclerView);

    /**
     * up fetch end
     */
    void setNotDoAnimationCount(int count);

    /**
     * Set custom load more
     *
     * @param loadingView
     */
    void setLoadMoreView(LoadMoreView loadingView);

    /**
     * Load more view count
     *
     * @return 0 or 1
     */
    int getLoadMoreViewCount();

    /**
     * Gets to load more locations
     *
     * @return
     */
    int getLoadMoreViewPosition();

    /**
     * @return Whether the Adapter is actively showing load
     * ic_progress.
     */
    boolean isLoading();

    /**
     * Refresh end, no more data
     */
    void loadMoreEnd();

    /**
     * Refresh end, no more data
     */
    void loadMoreEmpty();

    /**
     * Refresh end, no more data
     *
     * @param gone if true gone the load more view
     */
    void loadMoreEnd(boolean gone);

    /**
     * Refresh getLstBlockComplete
     */
    void loadMoreComplete();

    /**
     * Refresh failed
     */
    void loadMoreFail();

    /**
     * Set the enabled state of load more.
     *
     * @param enable True if load more is enabled, false otherwise.
     */
    void setEnableLoadMore(boolean enable);

    /**
     * Returns the enabled status for load more.
     *
     * @return True if load more is enabled, false otherwise.
     */
    boolean isLoadMoreEnable();

    /**
     * Sets the duration of the animation.
     *
     * @param duration The length of the animation, in milliseconds.
     */
    void setDuration(int duration);

    /**
     * setting up a new instance to data;
     *
     * @param data
     */
    void setNewData(@Nullable List<T> data);

    /**
     * insert  a item associated with the specified position of adapter
     *
     * @param position
     * @param item
     * @deprecated use  instead
     */
    @Deprecated
    void add(@IntRange(from = 0) int position, @NonNull T item);

    /**
     * add one new data in to certain location
     *
     * @param position
     */
    void addData(@IntRange(from = 0) int position, @NonNull T data);

    /**
     * add one new data in to first list
     *
     * @param data
     */
    void addFirst(@NonNull T data);

    /**
     * add one new data
     */
    void addData(@NonNull T data);

    /**
     * add new data in to certain location
     *
     * @param position the insert position
     * @param newData  the new data collection
     */
    void addData(@IntRange(from = 0) int position, @NonNull Collection<? extends T> newData);

    /**
     * add new data to the end of mData
     *
     * @param newData the new data collection
     */
    void addData(@NonNull Collection<? extends T> newData);

    /**
     * remove the item associated with the specified position of adapter
     *
     * @param position
     */
    void remove(@IntRange(from = 0) int position);

    /**
     * remove the item associated with the specified position of adapter
     *
     * @param adapterItemPosition
     * @param dataPosition
     */
    void removeByPositionAdapter(@IntRange(from = 0) int adapterItemPosition, @IntRange(from = 0) int dataPosition);

    /**
     * change data
     */
    void setData(@IntRange(from = 0) int index, @NonNull T data);

    /**
     * change data
     */
    void updateData(@IntRange(from = 0) int index, @NonNull T data);

    /**
     * use data to replace all item in mData. this method is different {@link #setNewData(List)},
     * it doesn't change the mData reference
     *
     * @param data data collection
     */
    void replaceData(@NonNull Collection<? extends T> data);

    void replaceData(@IntRange(from = 0) int position, @NonNull T data);

    /**
     * Get the data of list
     *
     * @return List<T>
     */
    @NonNull
    List<T> getData();

    /**
     * Get the data item associated with the specified position in the data set.
     *
     * @param position Position of the item whose data we want within the adapter's
     *                 data set.
     * @return The data at the specified position.
     */
    T getItem(@IntRange(from = 0) int position);

    /**
     * @param position
     * @return data at the specified position.
     */
    T getData(@IntRange(from = 0) int position);

    /**
     * if setHeadView will be return 1 if not will be return 0.
     * notice: Deprecated! Use {@link ViewGroup#getChildCount()} of {@link #getHeaderLayout()} to replace.
     *
     * @return
     */
    @Deprecated
    int getHeaderViewsCount();

    /**
     * if mFooterLayout will be return 1 or not will be return 0.
     * notice: Deprecated! Use {@link ViewGroup#getChildCount()} of {@link #getFooterLayout()} to replace.
     *
     * @return
     */
    @Deprecated
    int getFooterViewsCount();

    /**
     * if addHeaderView will be return 1, if not will be return 0
     */
    int getHeaderLayoutCount();

    /**
     * if addFooterView will be return 1, if not will be return 0
     */
    int getFooterLayoutCount();

    /**
     * if show empty view will be return 1 or not will be return 0
     *
     * @return
     */
    int getEmptyViewCount();


    int getItemCount();

    int getItemViewType(int position);

    /**
     * The notification starts the callback and loads more
     */
    void notifyLoadMoreToLoading();

    /**
     * Load more without data when settings are clicked loaded
     *
     * @param enable
     */
    void enableLoadMoreEndClick(boolean enable);


    boolean isHeaderViewAsFlow();

    void setHeaderViewAsFlow(boolean headerViewAsFlow);

    boolean isFooterViewAsFlow();

    void setFooterViewAsFlow(boolean footerViewAsFlow);

    /**
     * @param spanSizeLookup instance to be used to query number of spans occupied by each item
     */
    void setSpanSizeLookup(MultifunctionAdapter.SpanSizeLookup spanSizeLookup);

    /**
     * Return root layout of header
     */
    LinearLayout getHeaderLayout();

    /**
     * Return root layout of footer
     */
    LinearLayout getFooterLayout();

    /**
     * Append header to the rear of the mHeaderLayout.
     *
     * @param header
     */
    int addHeaderView(View header);

    /**
     * Add header view to mHeaderLayout and set header view position in mHeaderLayout.
     * When index = -1 or index >= child count in mHeaderLayout,
     * the effect of this method is the same as that of {@link #addHeaderView(View)}.
     *
     * @param header
     * @param index  the position in mHeaderLayout of this header.
     *               When index = -1 or index >= child count in mHeaderLayout,
     *               the effect of this method is the same as that of {@link #addHeaderView(View)}.
     */
    int addHeaderView(View header, int index);

    /**
     * @param header
     * @param index
     * @param orientation
     */
    int addHeaderView(View header, int index, int orientation);


    int setHeaderView(View header);

    int setHeaderView(View header, int index);

    int setHeaderView(View header, int index, int orientation);

    /**
     * Append footer to the rear of the mFooterLayout.
     *
     * @param footer
     */
    int addFooterView(View footer);

    int addFooterView(View footer, int index);


    /**
     * Add footer view to mFooterLayout and set footer view position in mFooterLayout.
     * When index = -1 or index >= child count in mFooterLayout,
     * the effect of this method is the same as that of {@link #addFooterView(View)}.
     *
     * @param footer
     * @param index  the position in mFooterLayout of this footer.
     *               When index = -1 or index >= child count in mFooterLayout,
     *               the effect of this method is the same as that of {@link #addFooterView(View)}.
     */
    int addFooterView(View footer, int index, int orientation);

    int setFooterView(View header);

    int setFooterView(View header, int index);

    int setFooterView(View header, int index, int orientation);

    /**
     * remove header view from mHeaderLayout.
     * When the child count of mHeaderLayout is 0, mHeaderLayout will be set to null.
     *
     * @param header
     */
    void removeHeaderView(View header);

    /**
     * remove footer view from mFooterLayout,
     * When the child count of mFooterLayout is 0, mFooterLayout will be set to null.
     *
     * @param footer
     */
    void removeFooterView(View footer);

    /**
     * remove all header view from mHeaderLayout and set null to mHeaderLayout
     */
    void removeAllHeaderView();

    /**
     * remove all footer view from mFooterLayout and set null to mFooterLayout
     */
    void removeAllFooterView();

    /**
     * bind recyclerView {@link #bindToRecyclerView(RecyclerView)} before use!
     *
     * @see #bindToRecyclerView(RecyclerView)
     */
    void setEmptyView(int layoutResId, ViewGroup viewGroup);

    /**
     * bind recyclerView {@link #bindToRecyclerView(RecyclerView)} before use!
     *
     * @see #bindToRecyclerView(RecyclerView)
     */
    void setEmptyView(int layoutResId);

    /**
     * Call before {@link RecyclerView#setAdapter(RecyclerView.Adapter)}
     *
     * @param isHeadAndEmpty false will not show headView if the data is empty true will show emptyView and headView
     */
    void setHeaderAndEmpty(boolean isHeadAndEmpty);

    /**
     * set emptyView show if adapter is empty and want to show headview and footview
     * Call before {@link RecyclerView#setAdapter(RecyclerView.Adapter)}
     *
     * @param isHeadAndEmpty
     * @param isFootAndEmpty
     */
    void setHeaderFooterEmpty(boolean isHeadAndEmpty, boolean isFootAndEmpty);

    /**
     * Set whether to use empty view
     *
     * @param isUseEmpty
     */
    void isUseEmpty(boolean isUseEmpty);

    /**
     * When the current adapter is empty, the BaseQuickAdapter can display a special view
     * called the empty view. The empty view is used to provide feedback to the user
     * that no data is available in this AdapterView.
     *
     * @return The view to show if the adapter is empty.
     */
    View getEmptyView();

    void setEmptyView(View emptyView);

    void setAutoLoadMoreSize(int preLoadNumber);

    void setPreLoadNumber(int preLoadNumber);

    /**
     * Set the view animation type.
     *
     * @param animationType One of {@link #@AnimationType.ALPHAIN}, {@link #@AnimationType.SCALEIN}, {@link #@AnimationType.SLIDEIN_BOTTOM},
     *                      {@link #@AnimationType.SLIDEIN_LEFT}, {@link #@AnimationType.SLIDEIN_RIGHT}.
     */
    void openLoadAnimation(@MultifunctionAdapter.AnimationType int animationType);

    /**
     * Set Custom ObjectAnimator
     *
     * @param animation ObjectAnimator
     */
    void openLoadAnimation(BaseAnimation animation);

    /**
     * To open the animation when layout_loading
     */
    void openLoadAnimation();

    /**
     * @param firstOnly true just show anim when first layout_loading false show anim when load the data every time
     */
    void isFirstOnly(boolean firstOnly);

}
