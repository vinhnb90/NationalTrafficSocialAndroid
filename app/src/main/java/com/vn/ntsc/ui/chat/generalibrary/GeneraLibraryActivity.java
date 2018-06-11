package com.vn.ntsc.ui.chat.generalibrary;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.vn.ntsc.R;
import com.vn.ntsc.core.views.BaseActivity;
import com.vn.ntsc.repository.model.chat.GeneraLibraryRequest;
import com.vn.ntsc.repository.model.chat.GeneraLibraryResponse;
import com.vn.ntsc.repository.model.chat.ItemFileChat;
import com.vn.ntsc.repository.preferece.UserPreferences;
import com.vn.ntsc.ui.chat.adapter.GeneraLibraryAdapter;
import com.vn.ntsc.ui.chat.meidiadetail.ChatMediaDetailActivity;
import com.vn.ntsc.widget.adapter.MultifunctionAdapter;
import com.vn.ntsc.widget.toolbar.ToolbarTitleCenter;

import java.util.List;

import butterknife.BindView;

/**
 * Created by Doremon on 2/28/2018.
 */

public class GeneraLibraryActivity extends BaseActivity<GeneraLibraryPresenter> implements GeneraLibraryContract.View
        , SwipeRefreshLayout.OnRefreshListener
        , MultifunctionAdapter.RequestLoadMoreListener
        , GeneraLibraryAdapter.GeneraLibraryEventListener {

    public static final String TAG = GeneraLibraryActivity.class.getSimpleName();

    public static final int TYPE = -1; // Returns all images, videos, audio
    public static final int SPAN_COUNT = 4;
    public static final int TAKE = 48;

    @BindView(R.id.toolbar)
    ToolbarTitleCenter mToolbar;

    @BindView(R.id.activity_genara_library_recycler_genera_library)
    RecyclerView recyclerGeneraLibrary;
    @BindView(R.id.activity_genara_library_swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    private int skip = 0;
    private int totalFile;

    private GeneraLibraryAdapter mGeneraLibraryAdapter;

    public static void startActivity(Activity activity) {
        Intent intent = new Intent(activity, GeneraLibraryActivity.class);
        activity.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_genara_library;
    }

    @Override
    public void onCreateView(View rootView) {
        getModulesCommonComponent().inject(this);

        swipeRefreshLayout.setOnRefreshListener(this);

        initRecyclerView();
    }

    @Override
    public void onViewReady() {
        super.onViewReady();

        setSupportActionBar(mToolbar);
        mToolbar.setActionbar(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        swipeRefreshLayout.setRefreshing(true);
        requestAllFileChat();
    }

    @Override
    public void onAllFileChat(GeneraLibraryResponse response) {
        if (response != null && response.getData() != null) {
            totalFile = response.getData().sizeAllFile;
            if (!response.getData().allFileChat.isEmpty()) {
                mGeneraLibraryAdapter.addData(response.getData().allFileChat);
            } else {
                mGeneraLibraryAdapter.loadMoreEmpty();
            }
        }
        mGeneraLibraryAdapter.loadMoreEnd();
    }

    @Override
    public void onComplete() {
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onRefresh() {
        skip = 0;
        mGeneraLibraryAdapter.getData().clear();
        mGeneraLibraryAdapter.loadMoreComplete();
        requestAllFileChat();
    }

    public void initRecyclerView() {
        mGeneraLibraryAdapter = new GeneraLibraryAdapter(this);
        mGeneraLibraryAdapter.setEnableLoadMore(true);
        mGeneraLibraryAdapter.setOnLoadMoreListener(this, recyclerGeneraLibrary);
        recyclerGeneraLibrary.setLayoutManager(new GridLayoutManager(this, SPAN_COUNT));
        recyclerGeneraLibrary.setAdapter(mGeneraLibraryAdapter);
    }

    @Override
    public void onLoadMoreRequested() {
        requestLoadmore();
    }

    @Override
    public void onItemClick(List<ItemFileChat> mData, ItemFileChat bean, int position, View view) {
        ChatMediaDetailActivity.lauch(this, mData, position, totalFile);
    }

    private void requestAllFileChat() {
        GeneraLibraryRequest request = new GeneraLibraryRequest(UserPreferences.getInstance().getToken()
                , UserPreferences.getInstance().getUserId()
                , UserPreferences.getInstance().getFriendId()
                , TYPE, skip, TAKE, -1);
        getPresenter().getAllFileChat(request);
    }

    private void requestLoadmore() {
        skip += TAKE;
        requestAllFileChat();
    }
}
