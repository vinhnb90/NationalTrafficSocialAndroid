package com.vn.ntsc.ui.blocklst;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.vn.ntsc.R;
import com.vn.ntsc.core.views.BaseActivity;
import com.vn.ntsc.repository.TypeView;
import com.vn.ntsc.repository.model.block.addblock.AddBlockUserRequest;
import com.vn.ntsc.repository.model.block.blocklst.BlockListRequest;
import com.vn.ntsc.repository.model.block.blocklst.BlockListResponse;
import com.vn.ntsc.repository.model.block.blocklst.BlockLstItem;
import com.vn.ntsc.repository.model.block.rmvblock.UnBlockRequest;
import com.vn.ntsc.repository.model.user.UserInfoResponse;
import com.vn.ntsc.repository.preferece.UserPreferences;
import com.vn.ntsc.ui.profile.my.MyProfileActivity;
import com.vn.ntsc.widget.adapter.MultifunctionAdapter;
import com.vn.ntsc.widget.decoration.SpacesItemDecoration;
import com.vn.ntsc.widget.toolbar.ToolbarTitleCenter;

import butterknife.BindDimen;
import butterknife.BindView;

/**
 * Created by ThoNh on 9/22/2017.
 */

public class BlockListActivity extends BaseActivity<BlockListPresenter>
        implements BlockListContracts.View, BlockListAdapter.IBlockListEvent,
        MultifunctionAdapter.RequestLoadMoreListener, SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.toolbar)
    ToolbarTitleCenter mToolbar;

    @BindView(R.id.activity_block_list_swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @BindView(R.id.activity_block_list_list_view_act_block)
    RecyclerView listBlockUser;
    
    @BindDimen(R.dimen.spacing_0_2dp)
    int space;

    private BlockListAdapter mAdapter;

    private int page = 0;


    public static void newInstance(AppCompatActivity activity) {
        Intent intent = new Intent(activity, BlockListActivity.class);
        activity.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_block_list;
    }

    @Override
    public void onCreateView(View rootView) {
        getModulesCommonComponent().inject(this);

        mAdapter = new BlockListAdapter(this);
        mAdapter.setOnLoadMoreListener(this, listBlockUser);
        mAdapter.setEnableLoadMore(true);

        mSwipeRefreshLayout.setOnRefreshListener(this);
        listBlockUser.setLayoutManager(new LinearLayoutManager(this));
        listBlockUser.addItemDecoration(new SpacesItemDecoration(space));
        listBlockUser.setAdapter(mAdapter);
    }

    @Override
    public void onViewReady() {
        setSupportActionBar(mToolbar);
        mToolbar.setActionbar(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        requestBlockLst(false);
    }

    // ======================================== Event ==============================================

    @Override
    public void onRefresh() {
        requestBlockLst(false);
    }

    @Override
    public void onLoadMoreRequested() {
        requestBlockLst(true);
    }

    @Override
    public void onItemClick(View view, BlockLstItem item, int position) {
        if (!item.isBlocked) {
            UserInfoResponse bean = new UserInfoResponse(item.userId, item.gender, item.userName, item.avatarId, item.isFav);
            MyProfileActivity.launch(this, view, bean, TypeView.ProfileType.COME_FROM_OTHER);
        }
    }

    @Override
    public void onUnBlockItemClick(View view, BlockLstItem item, int position) {
        UnBlockRequest request = new UnBlockRequest(item.userId, UserPreferences.getInstance().getToken());
        getPresenter().unBlock(request, item, position);
    }

    @Override
    public void onReBlockItemClick(View view, BlockLstItem item, int position) {
        AddBlockUserRequest request = new AddBlockUserRequest(UserPreferences.getInstance().getToken(), item.userId);
        getPresenter().reBlock(request, item, position);
    }


    // ========================================== Api Response =====================================

    @Override
    public void getLstBlockSuccess(BlockListResponse response) {
        if (response.mData.isEmpty()) {
            mAdapter.loadMoreEmpty();
            return;
        }
        mAdapter.addData(response.mData);
    }

    @Override
    public void getLstBlockComplete() {
        mSwipeRefreshLayout.setRefreshing(false);
        if (mAdapter.getData().isEmpty()) {
            mAdapter.setEmptyView(R.layout.layout_empty);
        }
    }

    @Override
    public void unBlockSuccess(BlockLstItem item, int position) {
        item.isBlocked = false; // đã bỏ chặn
        mAdapter.notifyItemChanged(position); // updateUi
    }

    @Override
    public void unBlockFailure() {
        Toast.makeText(this, R.string.act_un_block_failure, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void reBlockSuccess(BlockLstItem item, int position) {
        item.isBlocked = true; // chắc là bỏ chặn nhầm, chặn lại ---> ngu người
        mAdapter.notifyItemChanged(position);
    }

    @Override
    public void reBlockFailure() {
        Toast.makeText(this, R.string.act_block_failure, Toast.LENGTH_SHORT).show();
    }

    // =========================================End=================================================


    /**
     * @param increase if{@code :true --> load more} {@code: false --> refresh}
     */
    private void requestBlockLst(boolean increase) {
        if (increase) {
            page++;
            int skip = page * BlockListRequest.TAKE;
            requestBlockLst(skip);

        } else {
            if (mAdapter != null) {
                mAdapter.getData().clear();
                mAdapter.notifyDataSetChanged();
            }
            requestBlockLst(0);
        }
    }

    private void requestBlockLst(int skip) {
        mSwipeRefreshLayout.setRefreshing(true);
        BlockListRequest request = new BlockListRequest(skip, UserPreferences.getInstance().getToken());
        getPresenter().requestBlockLst(request);
    }
}
