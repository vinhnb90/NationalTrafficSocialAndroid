package com.vn.ntsc.widget.adapter;

import android.animation.Animator;
import android.support.annotation.IntDef;
import android.support.annotation.IntRange;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.vn.ntsc.R;
import com.vn.ntsc.core.model.BaseBean;
import com.vn.ntsc.utils.LogUtils;
import com.vn.ntsc.widget.adapter.animation.AlphaInAnimation;
import com.vn.ntsc.widget.adapter.animation.BaseAnimation;
import com.vn.ntsc.widget.adapter.animation.ScaleInAnimation;
import com.vn.ntsc.widget.adapter.animation.SlideInBottomAnimation;
import com.vn.ntsc.widget.adapter.animation.SlideInLeftAnimation;
import com.vn.ntsc.widget.adapter.animation.SlideInRightAnimation;
import com.vn.ntsc.widget.adapter.loadmore.LoadMoreView;
import com.vn.ntsc.widget.adapter.loadmore.SimpleLoadMoreView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * Giá trị chuyền vào khi extend lại VH , E <br />
 * VH là 1 BaseTimelineViewHolder được extend từ @see {@link BaseViewHolder} <br />
 * E là 1 Bean được extend từ @see {@link BaseBean} <br />
 * datas Là list Data được sử dụng trong Adapter <br />
 * listener Khi bạn muốn sử dụng 1 callBack ra ngoài phía View nếu không dùng thì trong super cho nó = null <br />
 * hàm được sử dụng
 * onInjectItemViewType dùng để set type cho view
 * onInjectViewHolder dùng để khởi tạo BaseTimelineViewHolder ở đây
 * onViewReady Sau khi viewHolder được khởi tạo sẽ gọi nó
 * <p>
 * <p>
 * Created by nankai on 11/18/2016.
 */

public abstract class MultifunctionAdapter<VH extends BaseViewHolder, E extends BaseBean> extends RecyclerView.Adapter<VH> implements IMultifunctionAdapter<E> {

    //============================================================================================//
    //======================================  Variable  ==========================================//
    //============================================================================================//
    protected static final String TAG = MultifunctionAdapter.class.getSimpleName();

    protected BaseAdapterListener listener;
    protected LayoutInflater mLayoutInflater;
    protected List<E> mData;
    //load more
    private boolean isNextLoadEnable = false;
    private boolean isLoadMoreEnable = false;
    private boolean isLoading = true;
    private LoadMoreView mLoadMoreView = new SimpleLoadMoreView();
    private MultifunctionAdapter.RequestLoadMoreListener loadMoreListener;
    private boolean isEnableLoadMoreEndClick = false;
    private boolean isFirstOnlyEnable = true;
    private boolean isOpenAnimationEnable = false;
    private boolean isPauseLoadmore = false;
    //Animation
    private Interpolator mInterpolator = new LinearInterpolator();
    private int mDuration = 300;
    private int mLastPosition = -1;
    private BaseAnimation mCustomAnimation;
    private BaseAnimation mSelectAnimation = new AlphaInAnimation();
    //Header, footer layout
    private LinearLayout mHeaderLayout;
    private LinearLayout mFooterLayout;
    /**
     * if asFlow is true, footer/header will arrange like normal item view.
     * only works when use {@link GridLayoutManager},and it will ignore span size.
     */
    private boolean headerViewAsFlow, footerViewAsFlow;
    private MultifunctionAdapter.SpanSizeLookup mSpanSizeLookup;
    private int mPreLoadNumber = 1;
    //empty
    private FrameLayout mEmptyLayout;
    private boolean isUseEmpty = true;
    private boolean isHeadAndEmptyEnable;
    private boolean isFootAndEmptyEnable;
    private RecyclerView mRecyclerView;

    @IntDef({ALPHAIN, SCALEIN, SLIDEIN_BOTTOM, SLIDEIN_LEFT, SLIDEIN_RIGHT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface AnimationType {
    }

    public interface SpanSizeLookup {
        int getSpanSize(GridLayoutManager gridLayoutManager, int position);
    }

    public interface RequestLoadMoreListener {
        void onLoadMoreRequested();
    }

    /**
     * @param listener extend {@link BaseAdapterListener}
     */
    public MultifunctionAdapter(@Nullable BaseAdapterListener listener) {
        this.mData = new ArrayList<>();
        this.listener = listener;
    }

    public RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    public void setRecyclerView(RecyclerView mRecyclerView) {
        this.mRecyclerView = mRecyclerView;
    }

    private boolean isCheckRecyclerNotNull() {
        if (null == getRecyclerView()) {
            LogUtils.e(TAG, "please bind recyclerView first!");
            return false;
        }
        return true;
    }

    //============================================================================================//
    //======================================  IBaseAdapter  ======================================//
    //============================================================================================//
    @Override
    public void bindToRecyclerView(RecyclerView recyclerView) {
        if (null != getRecyclerView()) {
            throw new RuntimeException("Don't bind twice");
        }
        isLoading = false;
        setRecyclerView(recyclerView);
        getRecyclerView().setAdapter(this);
    }

    @Deprecated
    @Override
    public void setOnLoadMoreListener(MultifunctionAdapter.RequestLoadMoreListener requestLoadMoreListener) {
        openLoadMore(requestLoadMoreListener);
    }

    @Override
    public void setOnLoadMoreListener(MultifunctionAdapter.RequestLoadMoreListener requestLoadMoreListener, RecyclerView recyclerView) {
        openLoadMore(requestLoadMoreListener);
        if (getRecyclerView() == null) {
            setRecyclerView(recyclerView);
        }
    }

    @Override
    public void disableLoadMoreIfNeed() {
        if (!isCheckRecyclerNotNull())
            return;
        disableLoadMoreIfNeed(getRecyclerView());
    }

    @Override
    public void disableLoadMoreIfNeed(RecyclerView recyclerView) {
        setEnableLoadMore(false);
        if (null == recyclerView)
            return;
        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        if (null == manager)
            return;
        if (manager instanceof LinearLayoutManager) {
            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) manager;
            recyclerView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if ((linearLayoutManager.findLastCompletelyVisibleItemPosition() + 1) != getItemCount()) {
                        setEnableLoadMore(true);
                    }
                }
            }, 50);
        } else if (manager instanceof StaggeredGridLayoutManager) {
            final StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager) manager;
            recyclerView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    final int[] positions = new int[staggeredGridLayoutManager.getSpanCount()];
                    staggeredGridLayoutManager.findLastCompletelyVisibleItemPositions(positions);
                    int position = getTheBiggestNumber(positions) + 1;
                    if (position != getItemCount())
                        setEnableLoadMore(true);
                }
            }, 50);
        }
    }

    @Override
    public void notifyLoadMoreToLoading() {
        if (mLoadMoreView.getLoadMoreStatus() == LoadMoreView.Status.STATUS_LOADING) {
            return;
        }
        isPauseLoadmore = false;
        mLoadMoreView.setLoadMoreStatus(LoadMoreView.Status.STATUS_DEFAULT);
        notifyItemChanged(getLoadMoreViewPosition());
    }

    @Override
    public void enableLoadMoreEndClick(boolean enable) {
        isEnableLoadMoreEndClick = enable;
    }


    @Override
    public boolean isHeaderViewAsFlow() {
        return headerViewAsFlow;
    }

    @Override
    public void setHeaderViewAsFlow(boolean headerViewAsFlow) {
        this.headerViewAsFlow = headerViewAsFlow;
    }

    @Override
    public boolean isFooterViewAsFlow() {
        return footerViewAsFlow;
    }

    @Override
    public void setFooterViewAsFlow(boolean footerViewAsFlow) {
        this.footerViewAsFlow = footerViewAsFlow;
    }

    @Override
    public void setSpanSizeLookup(MultifunctionAdapter.SpanSizeLookup spanSizeLookup) {
        this.mSpanSizeLookup = spanSizeLookup;
    }

    @Override
    public void setNotDoAnimationCount(int count) {
        mLastPosition = count;
    }

    @Override
    public void setLoadMoreView(LoadMoreView loadingView) {
        this.mLoadMoreView = loadingView;
    }

    @Override
    public int getLoadMoreViewCount() {
        if (loadMoreListener == null || !isLoadMoreEnable) {
            LogUtils.w(TAG, "loadMoreViewCount:\nloadMoreListener:" + loadMoreListener + "\nisLoadMoreEnable: " + !isLoadMoreEnable);
            return 0;
        }
        if (!isNextLoadEnable && mLoadMoreView.isLoadEndMoreGone()) {
            LogUtils.w(TAG, "loadMoreViewCount:\nisNextLoadEnable:" + isNextLoadEnable + "\nmLoadMoreView.isLoadEndMoreGone(): " + mLoadMoreView.isLoadEndMoreGone());
            return 0;
        }
        if (mData.size() == 0) {
            LogUtils.w(TAG, "loadMoreViewCount:\nmData.size():" + mData.size());
            return 0;
        }
        return 1;
    }

    @Override
    public int getLoadMoreViewPosition() {
        return getHeaderLayoutCount() + mData.size() + getFooterLayoutCount();
    }

    @Override
    public boolean isLoading() {
        return isLoading;
    }

    @Override
    public void setEnableLoadMore(boolean enable) {
        int oldLoadMoreCount = getLoadMoreViewCount();
        isLoadMoreEnable = enable;
        int newLoadMoreCount = getLoadMoreViewCount();
        if (oldLoadMoreCount == 1) {
            if (newLoadMoreCount == 0) {
                notifyItemChanged(getLoadMoreViewPosition());
            }
        } else {
            if (newLoadMoreCount == 1) {
                mLoadMoreView.setLoadMoreStatus(LoadMoreView.Status.STATUS_DEFAULT);
                notifyItemChanged(getLoadMoreViewPosition());
            }
        }
    }

    @Override
    public boolean isLoadMoreEnable() {
        return isLoadMoreEnable;
    }

    @Override
    public void loadMoreEnd() {
        loadMoreEnd(true);
    }

    @Override
    public void loadMoreEnd(boolean gone) {
        if (getLoadMoreViewCount() == 0) {
            LogUtils.w(TAG, "load more end not working");
            return;
        }
        isPauseLoadmore = true;
        isLoading = false;
        isNextLoadEnable = false;
        mLoadMoreView.setLoadMoreEndGone(gone);
        if (gone)
            notifyItemChanged(getLoadMoreViewPosition());
        else {
            mLoadMoreView.setLoadMoreStatus(LoadMoreView.Status.STATUS_END);
            notifyItemChanged(getLoadMoreViewPosition());
        }
    }

    @Override
    public void loadMoreEmpty() {
        if (getLoadMoreViewCount() == 0) {
            LogUtils.w(TAG, "load more empty not working");
            return;
        }
        isPauseLoadmore = true;
        isLoading = false;
        isNextLoadEnable = false;
        mLoadMoreView.setLoadMoreEndGone(false);
        mLoadMoreView.setLoadMoreStatus(LoadMoreView.Status.STATUS_EMPTY);
        notifyItemChanged(getLoadMoreViewPosition());
    }

    @Override
    public void loadMoreFail() {
        if (getLoadMoreViewCount() == 0) {
            LogUtils.w(TAG, "load more fail not working");
            return;
        }
        isPauseLoadmore = true;
        isLoading = false;
        mLoadMoreView.setLoadMoreStatus(LoadMoreView.Status.STATUS_FAIL);
        notifyItemChanged(getLoadMoreViewPosition());
    }

    @Override
    public void loadMoreComplete() {
        if (getLoadMoreViewCount() == 0 || isPauseLoadmore) {
            return;
        }
        isLoading = false;
        isNextLoadEnable = true;
        mLoadMoreView.setLoadMoreStatus(LoadMoreView.Status.STATUS_DEFAULT);
        notifyItemChanged(getLoadMoreViewPosition());
    }

    @Override
    public void setDuration(int duration) {
        this.mDuration = duration;
    }

    @Override
    public void setNewData(@Nullable List<E> data) {
        this.mData = null == data ? new ArrayList<E>() : data;
        if (null != loadMoreListener) {
            isNextLoadEnable = true;
            isLoadMoreEnable = true;
            isLoading = false;
            mLoadMoreView.setLoadMoreStatus(LoadMoreView.Status.STATUS_DEFAULT);
        }

        mLastPosition = -1;
        notifyDataSetChanged();

        if (null != getRecyclerView())
            setEmptyView(R.layout.layout_empty);
    }

    @Override
    public void setData(@IntRange(from = 0) int position, @NonNull E data) {
        mData.set(position, data);
        notifyItemInserted(position + getHeaderLayoutCount());
    }

    @Deprecated
    @Override
    public void add(@IntRange(from = 0) int position, @NonNull E item) {
        addData(position, item);
    }

    @Override
    public void addData(@IntRange(from = 0) int position, @NonNull E data) {
        mData.add(position, data);
        notifyItemInserted(position + getHeaderLayoutCount());
    }

    @Override
    public void addFirst(@NonNull E data) {
        mData.add(0, data);
        notifyDataSetChanged();
    }

    @Override
    public void addData(@NonNull E data) {
        mData.add(data);
        notifyItemInserted(mData.size() + getHeaderLayoutCount());
    }

    @Override
    public void remove(@IntRange(from = 0) int position) {
        if (null != mData && mData.size() > 0) {
            if (position <= mData.size() && position >= 0) {
                mData.remove(position);
                notifyDataSetChanged();
            }
        }
    }

    @Override
    public void removeByPositionAdapter(@IntRange(from = 0) int adapterItemPosition, @IntRange(from = 0) int dataPosition) {
        if (null != mData && mData.size() > 0) {
            mData.remove(dataPosition);
            notifyDataSetChanged();
        }
    }

    @Override
    public void addData(@IntRange(from = 0) int position, @NonNull Collection<? extends E> newData) {
        mData.addAll(position, newData);
        notifyItemRangeInserted(position + getHeaderLayoutCount(), newData.size());
    }

    @Override
    public void addData(@NonNull Collection<? extends E> newData) {
        mData.addAll(newData);
        notifyItemRangeInserted((mData.size() > 0 ? mData.size() - 1 : 0) + getHeaderLayoutCount(), newData.size());
    }

    @Override
    public void updateData(@IntRange(from = 0) int position, @NonNull E data) {
        if (null != mData && mData.size() >= position) {
            mData.set(position, data);
            notifyItemChanged(position);
        }
    }

    @Override
    public void replaceData(@NonNull Collection<? extends E> data) {
        if (mData != null && data != mData) {
            mData.clear();
            mData.addAll(data);
            notifyDataSetChanged();
        }
    }

    @Override
    public void replaceData(@IntRange(from = 0) int position, @NonNull E data) {
        if (mData != null && data != mData) {
            mData.set(position, data);
            notifyItemChanged(position - getHeaderLayoutCount(), data);
        }
    }

    @NonNull
    @Override
    public List<E> getData() {
        return mData;
    }

    @Override
    public E getItem(int position) {
        if (position < mData.size())
            return mData.get(position);
        return null;
    }

    @Override
    public E getData(@IntRange(from = 0) int position) {
        int index = position - getHeaderLayoutCount();
        if (index >= mData.size())
            return null;
        return mData.get(index);
    }

    /**
     * Get the row id associated with the specified position in the list.
     *
     * @param position The position of the item within the adapter's data set whose row id we want.
     * @return The id of the item at the specified position.
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Deprecated
    @Override
    public int getHeaderViewsCount() {
        return getHeaderLayoutCount();
    }

    @Deprecated
    @Override
    public int getFooterViewsCount() {
        return getFooterLayoutCount();
    }

    @Override
    public int getHeaderLayoutCount() {
        if (mHeaderLayout == null || mHeaderLayout.getChildCount() == 0) {
            return 0;
        }
        return 1;
    }

    @Override
    public int getFooterLayoutCount() {
        if (mFooterLayout == null || mFooterLayout.getChildCount() == 0) {
            return 0;
        }
        return 1;
    }

    @Override
    public int getEmptyViewCount() {
        if (mEmptyLayout == null || mEmptyLayout.getChildCount() == 0) {
            return 0;
        }
        if (!isUseEmpty) {
            return 0;
        }
        if (mData.size() != 0) {
            return 0;
        }
        return 1;
    }

    @Override
    public int getItemCount() {
        int count;
        if (getEmptyViewCount() == 1) {
            count = 1;
            if (isHeadAndEmptyEnable && getHeaderLayoutCount() != 0) {
                count++;
            }
            if (isFootAndEmptyEnable && getFooterLayoutCount() != 0) {
                count++;
            }
        } else {
            /**
             * java.lang.NullPointerException: Attempt to invoke interface method 'int java.util.List.size()' on a null object reference
             * at com.vn.ntsc.base.views.adapter.MultifunctionAdapter.getItemCount(MultifunctionAdapter.java:515)
             */

            count = getHeaderLayoutCount() + (mData != null ? mData.size() : 0) + getFooterLayoutCount() + getLoadMoreViewCount();
        }
        return count;
    }


    @Override
    public int getItemViewType(int position) {
        if (getEmptyViewCount() == 1) {
            boolean header = isHeadAndEmptyEnable && getHeaderLayoutCount() != 0;
            switch (position) {
                case 0:
                    if (header)
                        return HEADER_VIEW;
                    return EMPTY_VIEW;
                case 1:
                    if (header)
                        return EMPTY_VIEW;
                    return FOOTER_VIEW;
                case 2:
                    return FOOTER_VIEW;
                default:
                    return EMPTY_VIEW;
            }
        }
        int numHeaders = getHeaderLayoutCount();
        if (position < numHeaders)
            return HEADER_VIEW;
        int adjPosition = position - numHeaders;
        int adapterCount = mData.size();
        if (adjPosition < adapterCount)
            return onInjectItemViewType(adjPosition);
        adjPosition = adjPosition - adapterCount;
        int numFooters = getFooterLayoutCount();
        if (adjPosition < numFooters)
            return FOOTER_VIEW;

        return LOADING_VIEW;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        VH baseViewHolder;
        this.mLayoutInflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case LOADING_VIEW:
                baseViewHolder = getLoadingView(parent);
                break;
            case HEADER_VIEW:
                baseViewHolder = (VH) new MultifunctionAdapter.HeaderViewHolder(mHeaderLayout);
                break;
            case EMPTY_VIEW:
                baseViewHolder = (VH) new MultifunctionAdapter.EmptyViewHolder(mEmptyLayout);
                break;
            case FOOTER_VIEW:
                baseViewHolder = (VH) new MultifunctionAdapter.FooterViewHolder(mFooterLayout);
                break;
            default:
                baseViewHolder = onInjectViewHolder(parent, viewType);
                addAnimation(baseViewHolder);
                break;
        }
        return baseViewHolder;
    }

    /**
     * To bind different types of holder and solve different the bind events
     *
     * @param holder
     * @param position
     * @see #onInjectItemViewType(int)
     */
    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        //Do not move position, need to change before LoadMoreView binding
        autoLoadMore(position);
        int viewType = holder.getItemViewType();
        switch (viewType) {
            case LOADING_VIEW:
                mLoadMoreView.convert(holder);
                break;
            case HEADER_VIEW:
                break;
            case EMPTY_VIEW:
                break;
            case FOOTER_VIEW:
                break;
            default:
                onViewReady(holder, getItem(position - getHeaderLayoutCount()), position - getHeaderLayoutCount());
                break;
        }
    }

    @Override
    public LinearLayout getHeaderLayout() {
        return mHeaderLayout;
    }

    @Override
    public LinearLayout getFooterLayout() {
        return mFooterLayout;
    }

    @Override
    public int addHeaderView(View header) {
        return addHeaderView(header, -1);
    }

    @Override
    public int addHeaderView(View header, int index) {
        return addHeaderView(header, index, LinearLayout.VERTICAL);
    }

    @Override
    public int addHeaderView(View header, int index, int orientation) {
        if (mHeaderLayout == null) {
            mHeaderLayout = new LinearLayout(header.getContext());
            if (orientation == LinearLayout.VERTICAL) {
                mHeaderLayout.setOrientation(LinearLayout.VERTICAL);
                mHeaderLayout.setLayoutParams(new RecyclerView.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
            } else {
                mHeaderLayout.setOrientation(LinearLayout.HORIZONTAL);
                mHeaderLayout.setLayoutParams(new RecyclerView.LayoutParams(WRAP_CONTENT, MATCH_PARENT));
            }
        }
        final int childCount = mHeaderLayout.getChildCount();
        if (index < 0 || index > childCount) {
            index = childCount;
        }
        mHeaderLayout.addView(header, index);
        if (mHeaderLayout.getChildCount() == 1) {
            int position = getHeaderViewPosition();
            if (position != -1) {
                notifyItemInserted(position);
            }
        }
        return index;
    }

    @Override
    public int setHeaderView(View header) {
        return setHeaderView(header, 0, LinearLayout.VERTICAL);
    }

    @Override
    public int setHeaderView(View header, int index) {
        return setHeaderView(header, index, LinearLayout.VERTICAL);
    }

    @Override
    public int setHeaderView(View header, int index, int orientation) {
        if (mHeaderLayout == null || mHeaderLayout.getChildCount() <= index) {
            return addHeaderView(header, index, orientation);
        } else {
            mHeaderLayout.removeViewAt(index);
            mHeaderLayout.addView(header, index);
            return index;
        }
    }

    @Override
    public int addFooterView(View footer) {
        return addFooterView(footer, -1, LinearLayout.VERTICAL);
    }

    @Override
    public int addFooterView(View footer, int index) {
        return addFooterView(footer, index, LinearLayout.VERTICAL);
    }

    @Override
    public int addFooterView(View footer, int index, int orientation) {
        if (mFooterLayout == null) {
            mFooterLayout = new LinearLayout(footer.getContext());
            if (orientation == LinearLayout.VERTICAL) {
                mFooterLayout.setOrientation(LinearLayout.VERTICAL);
                mFooterLayout.setLayoutParams(new RecyclerView.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
            } else {
                mFooterLayout.setOrientation(LinearLayout.HORIZONTAL);
                mFooterLayout.setLayoutParams(new RecyclerView.LayoutParams(WRAP_CONTENT, MATCH_PARENT));
            }
        }
        final int childCount = mFooterLayout.getChildCount();
        if (index < 0 || index > childCount) {
            index = childCount;
        }
        mFooterLayout.addView(footer, index);
        if (mFooterLayout.getChildCount() == 1) {
            int position = getFooterViewPosition();
            if (position != -1) {
                notifyItemInserted(position);
            }
        }
        return index;
    }

    @Override
    public int setFooterView(View header) {
        return setFooterView(header, 0, LinearLayout.VERTICAL);
    }

    @Override
    public int setFooterView(View header, int index) {
        return setFooterView(header, index, LinearLayout.VERTICAL);
    }

    @Override
    public int setFooterView(View header, int index, int orientation) {
        if (mFooterLayout == null || mFooterLayout.getChildCount() <= index) {
            return addFooterView(header, index, orientation);
        } else {
            mFooterLayout.removeViewAt(index);
            mFooterLayout.addView(header, index);
            return index;
        }
    }

    @Override
    public void removeHeaderView(View header) {
        if (getHeaderLayoutCount() == 0) return;

        mHeaderLayout.removeView(header);
        if (mHeaderLayout.getChildCount() == 0) {
            int position = getHeaderViewPosition();
            if (position != -1) {
                notifyItemRemoved(position);
            }
        }
    }

    @Override
    public void removeFooterView(View footer) {
        if (getFooterLayoutCount() == 0) return;

        mFooterLayout.removeView(footer);
        if (mFooterLayout.getChildCount() == 0) {
            int position = getFooterViewPosition();
            if (position != -1) {
                notifyItemRemoved(position);
            }
        }
    }

    @Override
    public void removeAllHeaderView() {
        if (getHeaderLayoutCount() == 0) return;

        mHeaderLayout.removeAllViews();
        int position = getHeaderViewPosition();
        if (position != -1) {
            notifyItemRemoved(position);
        }
    }

    @Override
    public void removeAllFooterView() {
        if (getFooterLayoutCount() == 0) return;

        mFooterLayout.removeAllViews();
        int position = getFooterViewPosition();
        if (position != -1) {
            notifyItemRemoved(position);
        }
    }

    @Override
    public void setEmptyView(int layoutResId, ViewGroup viewGroup) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(layoutResId, viewGroup, false);
        setEmptyView(view);
    }

    @Override
    public void setEmptyView(int layoutResId) {
        if (!isCheckRecyclerNotNull())
            return;
        setEmptyView(layoutResId, getRecyclerView());
    }

    @Override
    public void setHeaderAndEmpty(boolean isHeadAndEmpty) {
        setHeaderFooterEmpty(isHeadAndEmpty, false);
    }

    @Override
    public void setHeaderFooterEmpty(boolean isHeadAndEmpty, boolean isFootAndEmpty) {
        isHeadAndEmptyEnable = isHeadAndEmpty;
        isFootAndEmptyEnable = isFootAndEmpty;
    }

    @Override
    public void isUseEmpty(boolean isUseEmpty) {
        this.isUseEmpty = isUseEmpty;
    }

    @Override
    public View getEmptyView() {
        return mEmptyLayout;
    }

    @Override
    public void setEmptyView(View emptyView) {
        boolean insert = false;

        if (mEmptyLayout == null) {
            mEmptyLayout = new FrameLayout(emptyView.getContext());
            final RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.MATCH_PARENT);
            final ViewGroup.LayoutParams lp = emptyView.getLayoutParams();
            if (lp != null) {
                layoutParams.width = lp.width;
                layoutParams.height = lp.height;
            }
            mEmptyLayout.setLayoutParams(layoutParams);
            insert = true;
        }

        mEmptyLayout.removeAllViews();
        mEmptyLayout.addView(emptyView);
        isUseEmpty = true;

        if (insert) {
            if (getEmptyViewCount() == 1) {
                notifyDataSetChanged();
            }
        }
    }

    @Override
    @Deprecated
    public void setAutoLoadMoreSize(int preLoadNumber) {
        setPreLoadNumber(preLoadNumber);
    }

    @Override
    public void setPreLoadNumber(int preLoadNumber) {
        if (preLoadNumber > 1) {
            mPreLoadNumber = preLoadNumber;
        }
    }

    @Override
    final public void openLoadAnimation(@MultifunctionAdapter.AnimationType int animationType) {
        this.isOpenAnimationEnable = true;
        mCustomAnimation = null;
        switch (animationType) {
            case ALPHAIN:
                mSelectAnimation = new AlphaInAnimation();
                break;
            case SCALEIN:
                mSelectAnimation = new ScaleInAnimation();
                break;
            case SLIDEIN_BOTTOM:
                mSelectAnimation = new SlideInBottomAnimation();
                break;
            case SLIDEIN_LEFT:
                mSelectAnimation = new SlideInLeftAnimation();
                break;
            case SLIDEIN_RIGHT:
                mSelectAnimation = new SlideInRightAnimation();
                break;
            default:
                break;
        }
    }

    @Override
    final public void openLoadAnimation(BaseAnimation animation) {
        this.isOpenAnimationEnable = true;
        this.mCustomAnimation = animation;
    }

    @Override
    final public void openLoadAnimation() {
        this.isOpenAnimationEnable = true;
    }

    @Override
    final public void isFirstOnly(boolean firstOnly) {
        this.isFirstOnlyEnable = firstOnly;
    }

    /**
     * Called when a view created by this adapter has been attached to a window.
     * simple to solve item will layout using all
     * {@link #setFullSpan(RecyclerView.ViewHolder)}
     *
     * @param holder
     */
    @Override
    public void onViewAttachedToWindow(VH holder) {
        super.onViewAttachedToWindow(holder);
        int type = holder.getItemViewType();
        if (type == EMPTY_VIEW || type == HEADER_VIEW || type == FOOTER_VIEW || type == LOADING_VIEW) {
            setFullSpan(holder);
        } else {
            addAnimation(holder);
        }
    }

    @Override
    public void onAttachedToRecyclerView(final RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        if (manager instanceof GridLayoutManager) {
            final GridLayoutManager gridManager = ((GridLayoutManager) manager);
            gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    int type = getItemViewType(position);
                    if (type == HEADER_VIEW && isHeaderViewAsFlow()) {
                        return 1;
                    }
                    if (type == FOOTER_VIEW && isFooterViewAsFlow()) {
                        return 1;
                    }
                    if (mSpanSizeLookup == null) {
                        return isFixedViewType(type) ? gridManager.getSpanCount() : 1;
                    } else {
                        return (isFixedViewType(type)) ? gridManager.getSpanCount() : mSpanSizeLookup.getSpanSize(gridManager,
                                position - getHeaderLayoutCount());
                    }
                }


            });
        }
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        this.listener = null;
        this.mRecyclerView = null;
        this.loadMoreListener = null;
        this.mCustomAnimation = null;
    }

    @Override
    public void onViewRecycled(VH holder) {
        super.onViewRecycled(holder);
        holder.onRecycled();
    }

    //============================================================================================//
    //======================================  Function  ==========================================//
    //============================================================================================//
    private void openLoadMore(MultifunctionAdapter.RequestLoadMoreListener requestLoadMoreListener) {
        this.loadMoreListener = requestLoadMoreListener;
        isNextLoadEnable = true;
        isLoadMoreEnable = true;
        isLoading = false;
        isPauseLoadmore = false;
    }


    /**
     * When set to true, the item will layout using all span area. That means, if orientation
     * is vertical, the view will have full width; if orientation is horizontal, the view will
     * have full height.
     * if the hold view use StaggeredGridLayoutManager they should using all span area
     *
     * @param holder True if this item should traverse all spans.
     */
    protected void setFullSpan(RecyclerView.ViewHolder holder) {
        if (holder.itemView.getLayoutParams() instanceof StaggeredGridLayoutManager.LayoutParams) {
            StaggeredGridLayoutManager.LayoutParams params = (StaggeredGridLayoutManager.LayoutParams) holder
                    .itemView.getLayoutParams();
            params.setFullSpan(true);
        }
    }

    protected boolean isFixedViewType(int type) {
        return type == EMPTY_VIEW || type == HEADER_VIEW || type == FOOTER_VIEW || type ==
                LOADING_VIEW;
    }

    private int getHeaderViewPosition() {
        //Return to header view notify position
        if (getEmptyViewCount() == 1) {
            if (isHeadAndEmptyEnable) {
                return 0;
            }
        } else {
            return 0;
        }
        return -1;
    }

    private int getFooterViewPosition() {
        //Return to footer view notify position
        if (getEmptyViewCount() == 1) {
            int position = 1;
            if (isHeadAndEmptyEnable && getHeaderLayoutCount() != 0) {
                position++;
            }
            if (isFootAndEmptyEnable) {
                return position;
            }
        } else {
            return getHeaderLayoutCount() + mData.size();
        }
        return -1;
    }

    private int getTheBiggestNumber(int[] numbers) {
        int tmp = -1;
        if (numbers == null || numbers.length == 0) {
            return tmp;
        }
        for (int num : numbers) {
            if (num > tmp) {
                tmp = num;
            }
        }
        return tmp;
    }


    private VH getLoadingView(ViewGroup parent) {
        View view = getItemView(mLoadMoreView.getLayoutId(), parent);
        MultifunctionAdapter.LoadingViewHolder holder = new MultifunctionAdapter.LoadingViewHolder(view);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mLoadMoreView.getLoadMoreStatus() == LoadMoreView.Status.STATUS_FAIL || mLoadMoreView.getLoadMoreStatus() == LoadMoreView.Status.STATUS_EMPTY) {
                    notifyLoadMoreToLoading();
                }
                if (isEnableLoadMoreEndClick && mLoadMoreView.getLoadMoreStatus() == LoadMoreView.Status.STATUS_END) {
                    notifyLoadMoreToLoading();
                }
            }
        });
        return (VH) holder;
    }

    /**
     * add animation when you want to show time
     *
     * @param holder
     */
    private void addAnimation(RecyclerView.ViewHolder holder) {
        if (isOpenAnimationEnable) {
            if (!isFirstOnlyEnable || holder.getLayoutPosition() > mLastPosition) {
                BaseAnimation animation = null;
                if (mCustomAnimation != null) {
                    animation = mCustomAnimation;
                } else {
                    animation = mSelectAnimation;
                }
                for (Animator anim : animation.getAnimators(holder.itemView)) {
                    startAnim(anim, holder.getLayoutPosition());
                }
                mLastPosition = holder.getLayoutPosition();
            }
        }
    }

    private void autoLoadMore(int position) {
        if (getLoadMoreViewCount() == 0) {
            return;
        }
        if (position < getItemCount() - mPreLoadNumber) {
            return;
        }
        if (mLoadMoreView.getLoadMoreStatus() != LoadMoreView.Status.STATUS_DEFAULT) {
            return;
        }
        mLoadMoreView.setLoadMoreStatus(LoadMoreView.Status.STATUS_LOADING);
        if (!isLoading) {
            isLoading = true;
            if (getRecyclerView() != null) {
                getRecyclerView().post(new Runnable() {
                    @Override
                    public void run() {
                        loadMoreListener.onLoadMoreRequested();
                    }
                });
            } else {
                loadMoreListener.onLoadMoreRequested();
            }
        }
    }

    /**
     * set anim to start when layout_loading
     *
     * @param anim
     * @param index
     */
    private void startAnim(Animator anim, int index) {
        anim.setDuration(mDuration).start();
        anim.setInterpolator(mInterpolator);
    }

    /**
     * @param layoutResId ID for an XML layout resource to load
     * @param parent      Optional view to be the parent of the generated hierarchy or else simply an object that
     *                    provides a set of LayoutParams values for root of the returned
     *                    hierarchy
     * @return view will be return
     */
    private View getItemView(@LayoutRes int layoutResId, ViewGroup parent) {
        return mLayoutInflater.inflate(layoutResId, parent, false);
    }

    protected int onInjectItemViewType(int position) {
        return super.getItemViewType(position);
    }

    protected abstract VH onInjectViewHolder(ViewGroup parent, int viewType);

    /**
     * Implement this method and use the helper to adapt the view to the given item.
     *
     * @param holder A fully initialized helper.
     * @param item   The item that needs to be displayed.
     */
    protected abstract void onViewReady(VH holder, E item, int position);

    //============================================================================================//
    //======================================  inner class  =======================================//
    //============================================================================================//
    public static class HeaderViewHolder extends BaseViewHolder {

        HeaderViewHolder(View view) {
            super(view);
        }
    }

    public static class FooterViewHolder extends BaseViewHolder {

        FooterViewHolder(View view) {
            super(view);
        }
    }

    public static class EmptyViewHolder extends BaseViewHolder {

        EmptyViewHolder(View view) {
            super(view);
        }
    }

    public static class LoadingViewHolder extends BaseViewHolder {

        LoadingViewHolder(View view) {
            super(view);
        }
    }
}
