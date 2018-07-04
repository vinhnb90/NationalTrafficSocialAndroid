package com.vn.ntsc.ui.profile.media.myalbum;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
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
import com.vn.ntsc.core.model.NetworkError;
import com.vn.ntsc.core.views.BaseActivity;
import com.vn.ntsc.repository.ActivityResultRequestCode;
import com.vn.ntsc.repository.model.myalbum.LoadAlbum.LoadAlbumRequest;
import com.vn.ntsc.repository.model.myalbum.LoadAlbum.LoadAlbumResponse;
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

import static com.vn.ntsc.services.addImageAlbum.UpLoadImageToAlbumService.EXTRA_KEY_ALBUM_ERROR;
import static com.vn.ntsc.services.addImageAlbum.UpLoadImageToAlbumService.EXTRA_KEY_IMAGE_UPLOAD_FAIL;

/**
 * Created by ducng on 11/9/2017.
 */

public class MyAlbumActivity extends BaseActivity<MyAlbumPresenter> implements MyAlbumContract.View,
        MyAlbumAdapter.MyAlbumListener,
        SwipeRefreshLayout.OnRefreshListener,
        MultifunctionAdapter.RequestLoadMoreListener {

    private static final String TAG = MyAlbumActivity.class.getSimpleName();
    private static final String SHARE_ELEMENT_VIEW = "MY_ALBUM_SHARE_ELEMENT_VIEW";
    public static final String MY_ALBUM_PRIVACY_RESULT_KEY = "ALBUM_PRIVACY_RESULT";
    private static final String EXTRA_USER_ID = "EXTRA_USER_ID";
    private static final String EXTRA_USER_NAME = "EXTRA_USER_NAME";
    private static final int SPAN_COUNT = 4;
    private static final int TAKE_DEFAULT = 48;

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

        checkUserId();

        mSwipeRefreshLayout.setOnRefreshListener(this);

        //setLayoutManager adapter
        mMyAlbums.setLayoutManager(new GridLayoutManager(MyAlbumActivity.this, SPAN_COUNT));
        mAdapter = new MyAlbumAdapter(isOwn, this);
        mAdapter.setEnableLoadMore(true);
        mAdapter.setOnLoadMoreListener(MyAlbumActivity.this, mMyAlbums);

        //set adapter
        mMyAlbums.setAdapter(mAdapter);

        ViewCompat.setTransitionName(mToolbar, SHARE_ELEMENT_VIEW);
    }

    @Override
    public void onViewReady() {

        setSupportActionBar(mToolbar);
        mToolbar.setActionbar(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        refreshData();

        setupEventBus();

    }

    private void setupEventBus() {
        //subject to refresh data this here automatically
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

        //when delete albumn
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


        //create new album
        RxEventBus.subscribe(SubjectCode.SUBJECT_CREATE_NEW_ALBUM, this, new Consumer<Object>() {
            @Override
            public void accept(Object o) throws Exception {
                LogUtils.e(TAG, "SUBJECT_CREATE_NEW_ALBUM");
                if (o != null) {
                    LoadAlbumResponse.DataBean bean = (LoadAlbumResponse.DataBean) o;
                    mAdapter.addData(bean);
                }
            }
        });


        //create album error --> remove album
        //create album ok but upload image faile --> stop state its loading
        RxEventBus.subscribe(SubjectCode.SUBJECT_ALBUM_ERROR, this, new Consumer<Object>() {
            @Override
            public void accept(Object o) throws Exception {
                //detect action
                if (o == null)
                    return;

                if (o instanceof Bundle) {
                    Bundle bundle = (Bundle) o;

                    if (bundle.containsKey(EXTRA_KEY_ALBUM_ERROR)) {
                        LoadAlbumResponse.DataBean bean = bundle.getParcelable(EXTRA_KEY_ALBUM_ERROR);
                        LogUtils.d(TAG, bean.toString());

                        int pos = mAdapter.getData().indexOf(bean);
                        mAdapter.remove(pos);
                    }

                    if (bundle.containsKey(EXTRA_KEY_IMAGE_UPLOAD_FAIL)) {
                        LogUtils.e(TAG, "SUBJECT_UPLOAD_IMAGE_FAILURE");
                        //allow show album
                        for (LoadAlbumResponse.DataBean element : mAdapter.getData()) {
                            if (element.isUploading) {
                                element.isUploading = false;
                            }
                        }
                        mAdapter.notifyDataSetChanged();
                        Toast.makeText(MyAlbumActivity.this, R.string.update_my_album_failure, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        //update when upload ok image
        RxEventBus.subscribe(SubjectCode.SUBJECT_UPLOAD_IMAGE_SUCCESS, this, new Consumer<Object>() {
            @Override
            public void accept(Object o) throws Exception {
                LogUtils.e(TAG, "SUBJECT_UPLOAD_IMAGE_SUCCESS");
                if (o != null) {
                    LoadAlbumResponse.DataBean bean = (LoadAlbumResponse.DataBean) o;
                    int indexOf = mAdapter.getData().indexOf(bean);
                    if (indexOf > -1) {
                        mAdapter.replaceData(indexOf, bean);
                    }
                }
            }
        });

        //when finish this activity and go back this
        //need check service upload is running
        //if running --> update state loading process (onResume)
        RxEventBus.subscribe(SubjectCode.SUBJECT_RESPONSE_CHECK_UP_LOAD_ALBUM, this, new Consumer<Object>() {
            @Override
            public void accept(Object o) throws Exception {
                if (o == null) {
                    return;
                }

                List<LoadAlbumResponse.DataBean> beans = (List<LoadAlbumResponse.DataBean>) o;
                for (int i = 0; i < beans.size(); i++) {
                    int index = mAdapter.getData().indexOf(beans.get(i));
                    if (index > -1) {
                        mAdapter.updateData(index, beans.get(i));
                        mAdapter.notifyItemChanged(index);
                    }
                }
            }
        });
    }

    @Override
    public void onResume(View viewRoot) {
        super.onResume(viewRoot);
        RxEventBus.publish(SubjectCode.SUBJECT_REQUEST_CHECK_UP_LOAD_ALBUM, "");
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
        mSkip = mAdapter.getData().size() + 1;
        String token = UserPreferences.getInstance().getToken();
        LoadAlbumRequest request = new LoadAlbumRequest(token, mUserId, mSkip, TAKE_DEFAULT);
        getPresenter().getMoreMyAlbum(request);
    }

    private void refreshData() {
        mAdapter.setEnableLoadMore(false);

        String token = UserPreferences.getInstance().getToken();
        LoadAlbumRequest request = new LoadAlbumRequest(token, mUserId, mSkip, TAKE_DEFAULT);
        getPresenter().getMyAlbum(request);


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
                if (mAdapter.getData().get(i).isCreateNew && mAdapter.getData().get(i).isUploading) {
                    Toast.makeText(this, R.string.uploading_pending, Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            CreateAlbumActivity.startActivity(this, view);
        }
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
        //if success reset allow load more
        mAdapter.setEnableLoadMore(true);

        if (response != null && response.data.isEmpty()) {
            mAdapter.loadMoreEnd(true);
            return;
        }

        if (response != null && !response.data.isEmpty()) {
            for (LoadAlbumResponse.DataBean e : response.data) {
                mAdapter.addData(e);
            }
        }
    }

    @Override
    public void getAlbumFailure(Throwable e) {
        NetworkError error = new NetworkError();
        error.ShowError(this, e);

        //disable load more
        mAdapter.setEnableLoadMore(false);
    }

    @Override
    public void getAlbumComplete() {
        mSwipeRefreshLayout.setRefreshing(false);
        mAdapter.loadMoreComplete();
        if (mAdapter.getItemCount() <= 0) {
            mAdapter.setEmptyView(R.layout.layout_empty_album);
        }
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
            mAdapter.setEmptyView(R.layout.layout_empty_album);
        }
    }

    @Override
    public void getMoreAlbumSuccess(LoadAlbumResponse response) {
        if (null == response.data) {
            mAdapter.loadMoreEnd(true);
        } else {
            if (response.data.isEmpty()) {
                mAdapter.loadMoreEnd(true);
            } else {
//                if (response != null && !response.data.isEmpty()) {
//                    for (LoadAlbumResponse.DataBean e : response.data) {
//                        mAdapter.addData(e);
//                    }
//                }
                mAdapter.addData(response.data);
            }
        }
    }
}
