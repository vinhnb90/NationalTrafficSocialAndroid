package com.vn.ntsc.ui.profile.media.myalbum;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.Toast;

import com.vn.ntsc.R;
import com.vn.ntsc.core.views.BaseActivity;
import com.vn.ntsc.repository.ActivityResultRequestCode;
import com.vn.ntsc.repository.model.myalbum.LoadAlbum.LoadAlbumRequest;
import com.vn.ntsc.repository.model.myalbum.LoadAlbum.LoadAlbumResponse;
import com.vn.ntsc.repository.model.myalbum.LoadImageInAlbum.LoadAlbumImageRequest;
import com.vn.ntsc.repository.model.myalbum.UpdateAlbum.UpdateAlbumRequest;
import com.vn.ntsc.repository.preferece.UserPreferences;
import com.vn.ntsc.ui.posts.SharePrivacyActivity;
import com.vn.ntsc.ui.profile.media.albumDetail.MyAlbumDetailActivity;
import com.vn.ntsc.ui.profile.media.createAlbum.CreateAlbumActivity;
import com.vn.ntsc.utils.LogUtils;
import com.vn.ntsc.widget.adapter.MultifunctionAdapter;
import com.vn.ntsc.widget.eventbus.RxEventBus;
import com.vn.ntsc.widget.eventbus.SubjectCode;
import com.vn.ntsc.widget.toolbar.ToolbarTitleCenter;

import java.util.List;

import butterknife.BindView;
import io.reactivex.functions.Consumer;

/**
 * Created by ducng on 11/9/2017.
 */

public class MyAlbumActivity extends BaseActivity<MyAlbumPresenter> implements MyAlbumContract.View,
        MyAlbumAdapter.MyAlbumListener,
        SwipeRefreshLayout.OnRefreshListener,
        MultifunctionAdapter.RequestLoadMoreListener {

    private static final String TAG = MyAlbumActivity.class.getSimpleName();
    private static final String SHARE_ELEMENT_VIEW = "MY_ALBUM_SHARE_ELEMENT_VIEW";
    private static final String MY_ALBUM_PRIVACY_RESULT_KEY = "ALBUM_PRIVACY_RESULT";
    private static final String EXTRA_USER_ID = "EXTRA_USER_ID";
    private static final String EXTRA_USER_NAME = "EXTRA_USER_NAME";
    private static final int SPAN_COUNT = 4;

    public static void startActivity(AppCompatActivity activity, String userId, View view, String userName) {
        ActivityOptionsCompat compat = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, view, SHARE_ELEMENT_VIEW);
        Intent intent = new Intent(activity, MyAlbumActivity.class);
        intent.putExtra(EXTRA_USER_ID, userId);
        intent.putExtra(EXTRA_USER_NAME, userName);
        activity.startActivity(intent, compat.toBundle());
    }

    @BindView(R.id.toolbar)
    ToolbarTitleCenter mToolbar;

    @BindView(R.id.activity_my_album_swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @BindView(R.id.activity_my_album_rv_my_album)
    RecyclerView mMyAlbums;

    private MyAlbumAdapter mAdapter;
    private String mUserId;
    private String mUserName;
    private boolean isOwn;
    private int mPositionClick = -1;
    private int mSkip = 0;

    private void getDataBundle() {
        if (getIntent() != null && getIntent().hasExtra(EXTRA_USER_ID) && getIntent().hasExtra(EXTRA_USER_NAME)) {
            mUserId = getIntent().getStringExtra(EXTRA_USER_ID);
            mUserName = getIntent().getStringExtra(EXTRA_USER_NAME);
        } else {
            finish();
            Toast.makeText(this, R.string.common_error, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_my_album;
    }

    @Override
    public void onCreateView(View rootView) {
        getModulesCommonComponent().inject(this);

        getDataBundle();

        mSwipeRefreshLayout.setOnRefreshListener(this);
        mMyAlbums.setLayoutManager(new GridLayoutManager(this, SPAN_COUNT));
        mAdapter = new MyAlbumAdapter(isOwn, this);
        mAdapter.setEnableLoadMore(true);
        mAdapter.setOnLoadMoreListener(this, mMyAlbums);
        mMyAlbums.setAdapter(mAdapter);

        ViewCompat.setTransitionName(mToolbar, SHARE_ELEMENT_VIEW);
    }

    @Override
    public void onViewReady() {

        setSupportActionBar(mToolbar);
        mToolbar.setActionbar(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        checkUserId();

        refreshData();

        RxEventBus.subscribe(SubjectCode.SUBJECT_REFRESH_ALBUM, this,
                new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        LogUtils.e(TAG, "SUBJECT_REFRESH_ALBUM");
                        LoadAlbumResponse.DataBean bean = (LoadAlbumResponse.DataBean) o;
                        int pos = mAdapter.getData().indexOf(bean);
                        if (pos > -1) {
                            mAdapter.replaceData(pos, bean);
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                });

        RxEventBus.subscribe(SubjectCode.SUBJECT_DELETE_ALBUM, this,
                new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        LogUtils.e(TAG, "SUBJECT_DELETE_ALBUM");
                        LoadAlbumResponse.DataBean bean = (LoadAlbumResponse.DataBean) o;
                        int pos = mAdapter.getData().indexOf(bean);
                        if (pos > -1) {
                            mAdapter.getData().remove(pos);
                            mAdapter.notifyItemRemoved(pos);
                        }
                    }
                });

        RxEventBus.subscribe(SubjectCode.SUBJECT_ADD_ALBUM, this,
                new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        LogUtils.e(TAG, "SUBJECT_ADD_ALBUM");
                        LoadAlbumResponse.DataBean bean = (LoadAlbumResponse.DataBean) o;
                        if (bean != null)
                            mAdapter.addData(1, bean);
                    }
                });

        RxEventBus.subscribe(SubjectCode.SUBJECT_ADD_IMAGE_TO_ALBUM, this,
                new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        LogUtils.e(TAG, "SUBJECT_ADD_IMAGE_TO_ALBUM");
                        LoadAlbumResponse.DataBean bean = (LoadAlbumResponse.DataBean) o;
                        int pos = mAdapter.getData().indexOf(bean);
                        if (pos > -1) {
                            mAdapter.replaceData(pos, bean);
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                });


        RxEventBus.subscribe(SubjectCode.SUBJECT_CREATE_NEW_ALBUM, this, new Consumer<Object>() {
            @Override
            public void accept(Object o) throws Exception {
                LogUtils.e(TAG, "SUBJECT_CREATE_NEW_ALBUM");
                if (o != null) {
                    LoadAlbumResponse.DataBean bean = (LoadAlbumResponse.DataBean) o;
                    mAdapter.addData(1, bean);
                }
            }
        });


        RxEventBus.subscribe(SubjectCode.SUBJECT_CREATE_NEW_ALBUM_ERROR, this, new Consumer<Object>() {
            @Override
            public void accept(Object o) throws Exception {
                mAdapter.remove(1);
            }
        });

        RxEventBus.subscribe(SubjectCode.SUBJECT_UPLOAD_IMAGE_SUCCESS, this, new Consumer<Object>() {
            @Override
            public void accept(Object o) throws Exception {
                LogUtils.e(TAG, "SUBJECT_UPLOAD_IMAGE_SUCCESS");
                if (o != null) {
                    LoadAlbumResponse.DataBean bean = (LoadAlbumResponse.DataBean) o;
                    int indexOf = mAdapter.getData().indexOf(bean);
                    if (indexOf > -1) {
                        mAdapter.setData(indexOf, bean);
                    }
                }
            }
        });

        RxEventBus.subscribe(SubjectCode.SUBJECT_UPLOAD_IMAGE_FAILURE, this, new Consumer<Object>() {
            @Override
            public void accept(Object o) throws Exception {
                LogUtils.e(TAG, "SUBJECT_UPLOAD_IMAGE_SUCCESS");
                if (o != null) {
                    LoadAlbumResponse.DataBean bean = (LoadAlbumResponse.DataBean) o;
                    int indexOf = mAdapter.getData().indexOf(bean);
                    if (indexOf > -1) {
                        mAdapter.remove(indexOf);
                    }
                }
            }
        });
    }

    @Override
    public void onResume(View viewRoot) {
        super.onResume(viewRoot);

        RxEventBus.publish(SubjectCode.SUBJECT_REQUEST_CHECK_UP_LOAD_ALBUM, "");
        RxEventBus.subscribe(SubjectCode.SUBJECT_RESPONSE_CHECK_UP_LOAD_ALBUM, this, new Consumer<Object>() {
            @Override
            public void accept(Object o) throws Exception {
                List<LoadAlbumResponse.DataBean> beans = (List<LoadAlbumResponse.DataBean>) o;
                for (int i = 0; i < beans.size(); i++) {
                    int index = mAdapter.getData().indexOf(beans.get(i));
                    if (index > -1) {
                        mAdapter.setData(index, beans.get(i));
                        mAdapter.notifyItemChanged(index);
                    }
                }
            }
        });
    }

    private void checkUserId() {
        if (!mUserId.equals(UserPreferences.getInstance().getUserId())) {
            String tabAlbum = getString(R.string.tab_album) + " ";
            Spannable nameSpannable = new SpannableString(tabAlbum + mUserName);
            nameSpannable.setSpan(new StyleSpan(Typeface.BOLD), tabAlbum.length(), nameSpannable.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            mToolbar.setTitleCenter(nameSpannable.toString());
            isOwn = false;
        } else {
            isOwn = true;
            mToolbar.setTitleCenter(R.string.activity_my_album_toolbar_title);
        }
    }

    private void loadMoreData() {
        mSkip += LoadAlbumImageRequest.TAKE;
        refreshData();
    }

    private void refreshData() {
        String token = UserPreferences.getInstance().getToken();
        LoadAlbumRequest request = new LoadAlbumRequest(token, mUserId, mSkip);
        getPresenter().getMyAlbum(request);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {

            switch (requestCode) {
                case ActivityResultRequestCode.REQUEST_PRIVACY_ALBUM:

                    LoadAlbumResponse.DataBean dataBean = mAdapter.getData(mPositionClick);
                    int privacy = data.getIntExtra(MY_ALBUM_PRIVACY_RESULT_KEY, 0);
                    int currentPrivacy = dataBean.privacy;

                    if (privacy != currentPrivacy) {
                        dataBean.privacy = privacy;
                        mAdapter.notifyItemChanged(mPositionClick);

                        String token = UserPreferences.getInstance().getToken();
                        UpdateAlbumRequest request = new UpdateAlbumRequest(token, dataBean.albumId,
                                dataBean.albumName, dataBean.albumDes, privacy);
                        getPresenter().updateMyAlbum(request);
                    }

                    break;
            }
        }
    }


    /*********************************** RECYCLER VIEW EVENT **************************************/

    @Override
    public void onRefresh() {
        mSkip = 0;
        mAdapter.clearData();
        refreshData();
    }

    @Override
    public void onLoadMoreRequested() {
        loadMoreData();
    }

    @Override
    public void onCreateAlbum(View view) {

        // If first item is Create new Album --> prevent create new album
        if (isOwn) {
            for (int i = 0; i < mAdapter.getData().size(); i++) {
                if (mAdapter.getData().get(i).isCreateNew) {
                    Toast.makeText(this, R.string.uploading_pending, Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        }

        CreateAlbumActivity.startActivity(this, view);
    }

    @Override
    public void onItemClick(LoadAlbumResponse.DataBean dataBean, int position, View view) {
        MyAlbumDetailActivity.startActivity(this, dataBean, view);
    }

    @Override
    public void onChangePrivacy(LoadAlbumResponse.DataBean dataBean, int position, View view) {
        mPositionClick = position;
        SharePrivacyActivity.startActivityForResult(this, MY_ALBUM_PRIVACY_RESULT_KEY, dataBean.privacy, ActivityResultRequestCode.REQUEST_PRIVACY_ALBUM);
    }


    /******************************************* SERVER *******************************************/

    @Override
    public void getAlbumSuccess(LoadAlbumResponse response) {

        if (response != null && response.data.isEmpty()) {
            mAdapter.loadMoreEnd(true);
            return;
        }

        if (response != null && !response.data.isEmpty()) {
            mAdapter.addData(response.data);

        }
    }

    @Override
    public void getAlbumFailure() {
        Toast.makeText(this, R.string.get_albums_failure, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void getAlbumComplete() {
        mSwipeRefreshLayout.setRefreshing(false);
        mAdapter.loadMoreComplete();
        if (mAdapter.getData().isEmpty()) {
            mAdapter.setEmptyView(R.layout.layout_album_empty);
        }
        RxEventBus.publish(SubjectCode.SUBJECT_REQUEST_CHECK_UP_LOAD_ALBUM, "");
    }


    @Override
    public void updateAlbumSuccess() {
        Toast.makeText(this, R.string.update_my_album_success, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void updateAlbumFailure() {
        Toast.makeText(this, R.string.update_my_album_failure, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void updateComplete() {

        mAdapter.loadMoreComplete();

        if (mAdapter.getData().isEmpty()) {
            mAdapter.setEmptyView(R.layout.layout_album_empty);
        }
    }
}
