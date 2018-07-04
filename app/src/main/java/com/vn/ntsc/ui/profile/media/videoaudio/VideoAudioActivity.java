package com.vn.ntsc.ui.profile.media.videoaudio;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.vn.ntsc.R;
import com.vn.ntsc.core.views.BaseActivity;
import com.vn.ntsc.repository.model.timeline.datas.sub.ListBuzzChild;
import com.vn.ntsc.repository.model.videoaudio.VideoAudioRequest;
import com.vn.ntsc.repository.model.videoaudio.VideoAudioResponse;
import com.vn.ntsc.repository.preferece.UserPreferences;
import com.vn.ntsc.repository.publicfile.PublicFileRequest;
import com.vn.ntsc.ui.mediadetail.timeline.TimelineMediaActivity;
import com.vn.ntsc.utils.DimensionUtils;
import com.vn.ntsc.widget.adapter.MultifunctionAdapter;
import com.vn.ntsc.widget.eventbus.RxEventBus;
import com.vn.ntsc.widget.eventbus.SubjectCode;
import com.vn.ntsc.widget.toolbar.ToolbarTitleCenter;
import com.vn.ntsc.widget.views.gallerybottom.GridSpacingItemDecoration;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.reactivex.functions.Consumer;

/**
 * Created by ducng on 11/9/2017.
 */

public class VideoAudioActivity extends BaseActivity<VideoAudioPresenter> implements VideoAudioContract.View, VideoAudioAdapter.VideoAudioEventListener, SwipeRefreshLayout.OnRefreshListener, MultifunctionAdapter.RequestLoadMoreListener {
    private static final String SHARE_ELEMENT_VIEW = "VIDEO_AUDIO_ELEMENT_VIEW";
    private static final String EXTRA_USER_ID = "EXTRA_USER_ID";
    private static final int SPAN_COUNT = 4;


    public static void startActivity(Activity activity, String userId, View view) {
        ActivityOptionsCompat compat = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, view, SHARE_ELEMENT_VIEW);
        Intent intent = new Intent(activity, VideoAudioActivity.class);
        intent.putExtra(EXTRA_USER_ID, userId);
        activity.startActivity(intent, compat.toBundle());
    }


    @BindView(R.id.toolbar)
    ToolbarTitleCenter mToolbar;

    @BindView(R.id.activity_video_audio_rv_video_audio)
    RecyclerView mRecyclerViewVideoAdapter;

    @BindView(R.id.activity_video_audio_swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;


    private VideoAudioAdapter mAdapter;
    private String userId;
    private int skip = 0;


    @Override
    public int getLayoutId() {
        return R.layout.activity_video_audio;
    }

    @Override
    public void onCreateView(View rootView) {
        getModulesCommonComponent().inject(this);


        mSwipeRefreshLayout.setRefreshing(false);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        if (getIntent() != null && getIntent().hasExtra(EXTRA_USER_ID)) {
            userId = getIntent().getStringExtra(EXTRA_USER_ID);
            if (userId == null) {
                Toast.makeText(this, R.string.common_error, Toast.LENGTH_SHORT).show();
                finish();
            }
        }

        initRecyclerView();

        ViewCompat.setTransitionName(mToolbar, SHARE_ELEMENT_VIEW);
    }

    @Override
    public void onViewReady() {
        setSupportActionBar(mToolbar);
        mToolbar.setActionbar(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

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

        requestPublicLstVideoAudio();
    }

    @Override
    public void initRecyclerView() {
        mAdapter = new VideoAudioAdapter( this);
        mAdapter.setEnableLoadMore(true);
        mAdapter.setOnLoadMoreListener(this, mRecyclerViewVideoAdapter);
        mRecyclerViewVideoAdapter.setLayoutManager(new GridLayoutManager(this, SPAN_COUNT));
        mRecyclerViewVideoAdapter.addItemDecoration(new GridSpacingItemDecoration(4, DimensionUtils.convertDpToPx(4), true));
        mRecyclerViewVideoAdapter.setAdapter(mAdapter);
    }

    @Override
    public void onItemClick(List<ListBuzzChild> mData, ListBuzzChild bean, int position, View view) {
        List<ListBuzzChild> buzzChildren = new ArrayList<>();
        buzzChildren.addAll(mData);
        TimelineMediaActivity.launch(this, view, buzzChildren, position);
    }

    @Override
    public void onRefresh() {
        skip = 0;
        mAdapter.getData().clear();
        mAdapter.loadMoreComplete();
        requestPublicLstVideoAudio();
    }

    @Override
    public void onLoadMoreRequested() {
        requestLoadmorePublicVideoAudio();
    }

    private void requestPublicLstVideoAudio() {
        PublicFileRequest publicFileRequest = new PublicFileRequest(0, UserPreferences.getInstance().getToken(), userId, PublicFileRequest.TYPE_VIDEO_AUDIO);
        getPresenter().requestLstPublicVideoAudio(publicFileRequest);
    }

    private void requestLoadmorePublicVideoAudio() {
        skip += VideoAudioRequest.TAKE;
        requestPublicLstVideoAudio();
    }

    /********************************  SERVER RESPONSE *********************************************/
    @Override
    public void getLstPublicVideoAudioSuccess(VideoAudioResponse response) {
        if (response != null && response.data != null) {

            if (!response.data.isEmpty()) {
                mAdapter.addData(response.data);
            } else {
                mAdapter.loadMoreEmpty();
            }
        }
        mAdapter.loadMoreEnd();
    }

    @Override
    public void getLstPublicVideoAudioFailure() {
        Toast.makeText(this, R.string.common_error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onComplete() {

        if (mAdapter.getData().isEmpty()) {
            mAdapter.setEmptyView(R.layout.layout_empty_album_audio_video);
        }

        mSwipeRefreshLayout.setRefreshing(false);
    }
}
