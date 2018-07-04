package com.vn.ntsc.ui.profile.media.timeline;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.vn.ntsc.R;
import com.vn.ntsc.core.views.BaseActivity;
import com.vn.ntsc.repository.model.timeline.datas.sub.ListBuzzChild;
import com.vn.ntsc.repository.preferece.UserPreferences;
import com.vn.ntsc.repository.publicfile.PublicFileRequest;
import com.vn.ntsc.repository.publicfile.PublicFileResponse;
import com.vn.ntsc.ui.mediadetail.timeline.TimelineMediaActivity;
import com.vn.ntsc.utils.DimensionUtils;
import com.vn.ntsc.widget.adapter.MultifunctionAdapter;
import com.vn.ntsc.widget.eventbus.RxEventBus;
import com.vn.ntsc.widget.eventbus.SubjectCode;
import com.vn.ntsc.widget.toolbar.ToolbarTitleCenter;
import com.vn.ntsc.widget.views.gallerybottom.GridSpacingItemDecoration;

import java.util.List;

import butterknife.BindView;
import io.reactivex.functions.Consumer;


public class TimelineUserTabActivity extends BaseActivity<TimelineUserTabPresenter>
        implements TimelineUserTabContract.View, MediaTimelineAdapter.MediaTimelineEventListener,
        MultifunctionAdapter.RequestLoadMoreListener, SwipeRefreshLayout.OnRefreshListener {

    private static final String EXTRA_USER_ID = "EXTRA_USER_ID_TIMLINE_USER";
    private static final String SHARE_ELEMENT_VIEW = "SHARE_ELEMENT_VIEW_TIMELINE_USER";
    private static final int SPAN_COUNT = 4;
    private static final int TAKE = 48;


    public static void startActivity(Activity activity, String userId, View view) {
        ActivityOptionsCompat compat = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, view, SHARE_ELEMENT_VIEW);
        Intent intent = new Intent(activity, TimelineUserTabActivity.class);
        intent.putExtra(EXTRA_USER_ID, userId);
        activity.startActivity(intent, compat.toBundle());
    }

    @BindView(R.id.toolbar)
    ToolbarTitleCenter mToolbar;

    @BindView(R.id.activity_tab_timeline_user_swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @BindView(R.id.activity_tab_timeline_user_rv_media)
    RecyclerView mRecyclerView;

    private String mUserId;
    private MediaTimelineAdapter mAdapter;
    private int skip = 0;

    @Override
    public int getLayoutId() {
        return R.layout.activity_tab_timeline_user;
    }

    @Override
    public void onCreateView(View rootView) {
        getModulesCommonComponent().inject(this);

        mSwipeRefreshLayout.setOnRefreshListener(this);

        initRecyclerView();

        if (getIntent() != null && getIntent().hasExtra(EXTRA_USER_ID)) {
            mUserId = getIntent().getStringExtra(EXTRA_USER_ID);
        } else {
            Toast.makeText(this, R.string.common_error, Toast.LENGTH_SHORT).show();
            finish();
        }


        RxEventBus.subscribe(SubjectCode.SUBJECT_UPDATE_DATA, this, new Consumer<Object>() {
            @Override
            public void accept(Object o) throws Exception {
                if (null != o) {
                    try {
                        List<ListBuzzChild> mData = (List<ListBuzzChild>) o;
                        mAdapter.replaceData(mData);
                    } catch (ClassCastException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
    }

    @Override
    public void onViewReady() {

        setSupportActionBar(mToolbar);
        mToolbar.setActionbar(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        mSwipeRefreshLayout.setRefreshing(true);
        requestPublicMedia();
    }

    @Override
    public void initRecyclerView() {
        mAdapter = new MediaTimelineAdapter(this);
        mAdapter.setEnableLoadMore(true);
        mAdapter.setOnLoadMoreListener(this, mRecyclerView);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, SPAN_COUNT));
        mRecyclerView.addItemDecoration(new GridSpacingItemDecoration(4, DimensionUtils.convertDpToPx(4), true));
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onLoadMoreRequested() {
        requestLoadmorePublicMedia();
    }

    @Override
    public void onRefresh() {
        skip = 0;
        mAdapter.getData().clear();
        mAdapter.loadMoreComplete();
        requestPublicMedia();
    }

    @Override
    public void onItemClick(ListBuzzChild bean, int position, View view) {
        TimelineMediaActivity.launch(this, view, mAdapter.getData(), position);
    }

    private void requestPublicMedia() {
        String token = UserPreferences.getInstance().getToken();
        PublicFileRequest request = new PublicFileRequest(skip, token, mUserId, PublicFileRequest.TYPE_TIMELINE);
        getPresenter().getPublicMedia(request);
    }

    private void requestLoadmorePublicMedia() {
        skip += TAKE;
        requestPublicMedia();
    }


    /*********************************** SERVER RESPONSE *******************************************/

    @Override
    public void onGetListPublicImage(PublicFileResponse response) {

        if (response != null && response.mData != null) {

            if (!response.mData.isEmpty()) {
                mAdapter.addData(response.mData);
            } else {
                mAdapter.loadMoreEmpty();
            }
        }
        mAdapter.loadMoreEnd();
    }

    @Override
    public void onComplete() {
        mSwipeRefreshLayout.setRefreshing(false);
        if (mAdapter.getData().isEmpty()) {
            mAdapter.setEmptyView(R.layout.layout_empty_album_timeline);
        }
    }

}
