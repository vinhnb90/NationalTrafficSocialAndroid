package com.vn.ntsc.ui.profile.media.albumDetail;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tux.mylab.MediaPickerBaseActivity;
import com.example.tux.mylab.gallery.Gallery;
import com.example.tux.mylab.gallery.data.MediaFile;
import com.nankai.designlayout.dialog.DialogMaterial;
import com.nankai.designlayout.gallerybottom.GridSpacingItemDecoration;
import com.vn.ntsc.R;
import com.vn.ntsc.core.views.BaseActivity;
import com.vn.ntsc.repository.model.mediafile.MediaFileBean;
import com.vn.ntsc.repository.model.myalbum.AddImageToAlbum.AddImageAlbumResponse;
import com.vn.ntsc.repository.model.myalbum.DeleteAlbum.DelAlbumRequest;
import com.vn.ntsc.repository.model.myalbum.DeleteImageInAlbum.DelAlbumImageRequest;
import com.vn.ntsc.repository.model.myalbum.DeleteImageInAlbum.DelAlbumImageResponse;
import com.vn.ntsc.repository.model.myalbum.ItemImageInAlbum;
import com.vn.ntsc.repository.model.myalbum.LoadAlbum.LoadAlbumResponse;
import com.vn.ntsc.repository.model.myalbum.LoadImageInAlbum.LoadAlbumImageRequest;
import com.vn.ntsc.repository.model.myalbum.LoadImageInAlbum.LoadAlbumImageResponse;
import com.vn.ntsc.repository.model.myalbum.UpdateAlbum.UpdateAlbumRequest;
import com.vn.ntsc.repository.model.myalbum.UpdateAlbum.UpdateAlbumResponse;
import com.vn.ntsc.repository.preferece.UploadSettingPreference;
import com.vn.ntsc.repository.preferece.UserPreferences;
import com.vn.ntsc.services.addImageAlbum.UpLoadImageToAlbumService;
import com.vn.ntsc.ui.mediadetail.album.AlbumDetailMediaActivity;
import com.vn.ntsc.ui.posts.SharePrivacyActivity;
import com.vn.ntsc.ui.profile.media.edit.description.album.EditAlbumDescriptionActivity;
import com.vn.ntsc.utils.DimensionUtils;
import com.vn.ntsc.utils.LogUtils;
import com.vn.ntsc.utils.keyboard.KeyboardUtils;
import com.vn.ntsc.widget.adapter.MultifunctionAdapter;
import com.vn.ntsc.widget.eventbus.RxEventBus;
import com.vn.ntsc.widget.eventbus.SubjectCode;
import com.vn.ntsc.widget.toolbar.ToolbarTitleCenter;
import com.vn.ntsc.widget.views.dialog.ProgressAlertDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.functions.Consumer;

import static com.vn.ntsc.repository.ActivityResultRequestCode.REQUEST_PRIVACY_EDIT_ALBUM;
import static com.vn.ntsc.widget.eventbus.SubjectCode.SUBJECT_DELETE_IMAGE_IN_ALBUM;

/**
 * Created by ThoNh on 11/15/2017.
 */

public class MyAlbumDetailActivity extends BaseActivity<MyAlbumDetailPresenter> implements MyAlbumDetailContract.View,
        MyAlbumDetailAdapter.MyAlbumDetailEventListener, MultifunctionAdapter.RequestLoadMoreListener, SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = MyAlbumDetailActivity.class.getSimpleName();
    private static final String SHARE_ELEMENT_VIEW = "MY_ALBUM_DETAIL_SHARE_ELEMENT_VIEW";
    private static final String EXTRA_BUNDLE_ALBUM = "EXTRA_BUNDLE_ALBUM";
    private static final String EDIT_ALBUM_PRIVACY = "EDIT_ALBUM_PRIVACY";
    private static final int SPAN_COUNT = 4;


    public static void startActivity(AppCompatActivity activity, LoadAlbumResponse.DataBean bean, View view) {
        ActivityOptionsCompat compat = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, view, SHARE_ELEMENT_VIEW);
        Intent intent = new Intent(activity, MyAlbumDetailActivity.class);
        intent.putExtra(EXTRA_BUNDLE_ALBUM, bean);
        activity.startActivity(intent, compat.toBundle());
    }

    @BindView(R.id.toolbar)
    ToolbarTitleCenter mToolbar;

    @BindView(R.id.activity_my_album_detail_edt_name)
    EditText mAlbumName;

    @BindView(R.id.activity_my_album_detail_edit_desc)
    TextView mAlbumDesc;

    @BindView(R.id.activity_my_album_detail_rv_image_album)
    RecyclerView mRecyclerViewImage;

    @BindView(R.id.activity_my_album_detail_container_edit)
    CardView mContainerEdit;

    @BindView(R.id.activity_my_album_detail_container_desciption)
    CardView mContainerDescription;

    @BindView(R.id.activity_my_album_detail_txt_description)
    TextView mTextDescription;

    @BindView(R.id.activity_my_album_detail_imv_privacy)
    ImageView mImvPrivacy;

    @BindView(R.id.activity_my_album_detail_edt_privacy)
    TextView mEdtPrivacy;

    @BindView(R.id.activity_my_album_detail_swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;


    private ProgressAlertDialog mProgressAlertDialog;
    private Menu mMenu;
    private MyAlbumDetailAdapter mAdapter;
    private LoadAlbumResponse.DataBean mAlbum;
    private int privacy;
    private int skip = 0;
    private boolean mIsOwn = false;

    @Override
    public int getLayoutId() {
        return R.layout.activity_my_album_detail;
    }

    @Override
    public void onCreateView(View rootView) {
        getMediaComponent().inject(this);

        mAlbumDesc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAlbumDesc != null)
                    EditAlbumDescriptionActivity.launch(MyAlbumDetailActivity.this, mAlbumDesc, mIsOwn, mAlbumDesc.getText().toString(), !mIsOwn);
            }
        });

        mContainerDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mTextDescription.getText().toString().isEmpty())
                    EditAlbumDescriptionActivity.launch(MyAlbumDetailActivity.this, mAlbumDesc, mIsOwn, mTextDescription.getText().toString(), true);
            }
        });

        mProgressAlertDialog = new ProgressAlertDialog(this);
        if (getIntent().getParcelableExtra(EXTRA_BUNDLE_ALBUM) != null) {
            mAlbum = getIntent().getParcelableExtra(EXTRA_BUNDLE_ALBUM);
            initEditLayout(mAlbum);
        }

        initRecyclerView();

        ViewCompat.setTransitionName(mRecyclerViewImage, SHARE_ELEMENT_VIEW);
    }


    @Override
    public void onViewReady() {
        setSupportActionBar(mToolbar);
        mToolbar.setActionbar(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        refreshAlbum();

        RxEventBus.subscribe(SUBJECT_DELETE_IMAGE_IN_ALBUM, this, new Consumer<Object>() {
            @Override
            public void accept(Object o) throws Exception {
                if (o != null) {
                    List<ItemImageInAlbum> mListImage = (List<ItemImageInAlbum>) o;
                    mAdapter.clearData();
                    mAdapter.addData(mListImage);

                    // update data item in Previous activity
                    // convert fake data
                    mAlbum.numberImage = mAdapter.getData().size();
                    if (mAdapter.getData().size() == 0) {
                        mAlbum.imageList.thumbnailUrl = "";
                        mAlbum.imageList.originalUrl = "";
                        mAlbum.isUploading = false;
                        mAlbum.isCreateNew = false;
                    }
                    RxEventBus.publish(SubjectCode.SUBJECT_UPLOAD_IMAGE_SUCCESS, mAlbum);
                }
            }
        });
    }

    @Override
    public void onLoadMoreRequested() {
        LogUtils.e(TAG, "onLoadMoreRequested");
        loadMoreAlbum();
    }

    @Override
    public void onRefresh() {
        LogUtils.e(TAG, "onRefresh");
        refreshAlbum();
        mAdapter.loadMoreComplete();
    }

    @Override
    public void initEditLayout(LoadAlbumResponse.DataBean album) {
        mIsOwn = album.userId.equals(UserPreferences.getInstance().getUserId());
        mToolbar.setTitleCenter(album.albumName);

        if (mIsOwn) {
            mAlbumName.setText(album.albumName.trim());
            mAlbumDesc.setText(album.albumDes);
            switch (album.privacy) {
                case 0: // public
                    mImvPrivacy.setImageResource(R.drawable.ic_public);
                    mEdtPrivacy.setText(R.string.public_privacy);
                    break;
                case 2: // private
                    mImvPrivacy.setImageResource(R.drawable.ic_privacy_only_me);
                    mEdtPrivacy.setText(R.string.onlyme_privacy);
                    break;
            }
        }
        if (album.albumDes != null && !album.albumDes.isEmpty()) {
            showContainerDescription();
        }
    }


    @Override
    public void initRecyclerView() {
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mAdapter = new MyAlbumDetailAdapter(mIsOwn, this);
        mAdapter.setOnLoadMoreListener(this, mRecyclerViewImage);
        mAdapter.setEnableLoadMore(true);
        mRecyclerViewImage.setLayoutManager(new GridLayoutManager(this, SPAN_COUNT));
        mRecyclerViewImage.addItemDecoration(new GridSpacingItemDecoration(SPAN_COUNT, DimensionUtils.convertDpToPx(4), true));
        mRecyclerViewImage.setAdapter(mAdapter);
        if (mIsOwn) {
            mAdapter.addHeaderData(true);
        }
    }


    @OnClick({R.id.activity_my_album_detail_tv_del_album, R.id.activity_my_album_detail_layout_privacy})
    public void onViewClicked(View view) {

        switch (view.getId()) {
            case R.id.activity_my_album_detail_layout_privacy:
                LogUtils.e(TAG, "layout_privacy");
                SharePrivacyActivity.startActivityForResult(this, EDIT_ALBUM_PRIVACY, privacy, REQUEST_PRIVACY_EDIT_ALBUM);
                break;
            case R.id.activity_my_album_detail_tv_del_album:
                LogUtils.e(TAG, "tv_del_album");
                new DialogMaterial.Builder(this)
                        .removeHeader()
                        .setTitle(R.string.title_delete_album)
                        .setContent(R.string.confirm_delete_album)
                        .onNegative(R.string.common_ok_2,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        requestDeleteAlbum();
                                    }
                                })
                        .onPositive(R.string.common_cancel_2,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }).show();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {

            switch (requestCode) {
                case Gallery.REQUEST_CODE_GALLERY:
                    requestAddMoreImageToAlbum(data);
                    break;

                case REQUEST_PRIVACY_EDIT_ALBUM:
                    int privacy = data.getIntExtra(EDIT_ALBUM_PRIVACY, 0);
                    Log.e(TAG, "privacy:" + privacy);

                    /**
                     * @see SharePrivacyActivity.onViewReady
                     */

                    this.privacy = privacy;
                    switch (privacy) {
                        case 0: // public
                            mImvPrivacy.setImageResource(R.drawable.ic_public);
                            mEdtPrivacy.setText(R.string.public_privacy);
                            break;
                        case 2: // private
                            mImvPrivacy.setImageResource(R.drawable.ic_privacy_only_me);
                            mEdtPrivacy.setText(R.string.onlyme_privacy);
                            break;
                    }

                    break;

                case EditAlbumDescriptionActivity.REQ_CODE_EDIT:
                    String des = data.getStringExtra(EditAlbumDescriptionActivity.EXTRA_DESCRIPTION_RETURN);
                    if (des != null) {
                        mAlbumDesc.setText(des);
                    }
                    break;
            }
        }
    }

    @Override
    public void onBackPressed() {

        if (mAdapter.isShowingPickerDelete()) {
            mAdapter.hidePickerDelete();
            if (mContainerEdit.getVisibility() == View.VISIBLE) {
                initEditLayout(mAlbum);
                return;
            }
            showTextMenuEdit();
            return;
        }

        // Khi COntainer Edit đang hiển thị mà ấn nút back thì ẩn container edit đi, show Container Desciption lên
        if (mContainerEdit.getVisibility() == View.VISIBLE) {
            showContainerDescription();
            return;
        }

        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_my_album_detail, menu);
        mMenu = menu;
        if (mIsOwn) {
            showTextMenuEdit();
        } else {
            hideAllMenu();
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_done:
                requestUpdateAlbum();
                KeyboardUtils.hideSoftKeyboard(this);
                break;
            case R.id.action_del:
                requestDeleteImageInAlbum();
                break;
            case R.id.action_edit:
                showContainerEdit();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**********************************************************************************************/

    // request delete album
    private void requestDeleteAlbum() {
        DelAlbumRequest request = new DelAlbumRequest(UserPreferences.getInstance().getToken(), mAlbum.albumId);
        getPresenter().deteleAlbum(request);
        mProgressAlertDialog.show();
    }

    // request add more image to album
    private void requestAddMoreImageToAlbum(Intent data) {
        Parcelable[] photos = data.getParcelableArrayExtra(MediaPickerBaseActivity.RESULT_KEY);
        List<MediaFileBean> beans = new ArrayList<>();


        int sizeData = mIsOwn ? (mAdapter.getData().size()) : mAdapter.getData().size() - 1;
        LogUtils.e(TAG, "size picked:" + photos.length);
        LogUtils.e(TAG, "size adapter:" + sizeData);

        if (photos.length > UploadSettingPreference.getInstance().getMaxImageNumber()) {
            String format = String.format(Locale.getDefault(), getString(R.string.max_image_size_up_load), UploadSettingPreference.getInstance().getMaxImageNumber());
            Toast.makeText(context, format, Toast.LENGTH_SHORT).show();
            return;
        }

        if (sizeData + photos.length > UploadSettingPreference.getInstance().getMaxFilePerAlbum() && UploadSettingPreference.getInstance().getMaxFilePerAlbum() != 0) {
            String format = String.format(Locale.getDefault(), getString(R.string.max_image_size_up_load_album), UploadSettingPreference.getInstance().getMaxFilePerAlbum());
            Toast.makeText(context, format, Toast.LENGTH_SHORT).show();
            return;
        }

        for (Parcelable photo : photos) {
            MediaFile takeMediaFile = (MediaFile) photo;
            MediaFileBean bean = new MediaFileBean(-999 /*dont' care*/, takeMediaFile.getPath(), MediaFileBean.IMAGE);
            beans.add(bean);
        }
        mAlbum.isUploading = true;
        Intent intent = new Intent(this, UpLoadImageToAlbumService.class);
        intent.putExtra(UpLoadImageToAlbumService.EXTRA_ITEM_ALBUM, mAlbum);
        intent.putExtra(UpLoadImageToAlbumService.EXTRA_TOKEN, UserPreferences.getInstance().getToken());
        intent.putParcelableArrayListExtra(UpLoadImageToAlbumService.EXTRA_IMAGES, (ArrayList<? extends Parcelable>) beans);
        startService(intent);
        finish();
    }

    // request delete image in Album
    private void requestDeleteImageInAlbum() {
        List<ItemImageInAlbum> imagesSelected = mAdapter.getImagesSelected();
        List<String> imageIds = mAdapter.getImagesIdSelected(imagesSelected);
        if (!imagesSelected.isEmpty()) {
            String token = UserPreferences.getInstance().getToken();
            String albumId = mAlbum.albumId;
            DelAlbumImageRequest request = new DelAlbumImageRequest(token, albumId, imageIds);
            getPresenter().deleteImagesInAlbum(request, imagesSelected);
            mProgressAlertDialog.show();
        } else {
            onBackPressed();
        }
    }

    // request update album
    private void requestUpdateAlbum() {
        String token = UserPreferences.getInstance().getToken();
        String albumID = mAlbum.albumId;
        String albumName = mAlbumName.getText().toString().trim();
        String albumDes = mAlbumDesc.getText().toString().trim();
        UpdateAlbumRequest request = new UpdateAlbumRequest(token, albumID, albumName, albumDes, privacy);
        getPresenter().updateAlbum(request);

    }

    // first load album or refresh load album
    private void refreshAlbum() {
        mAdapter.clearData();
        skip = 0;
        LoadAlbumImageRequest request = new LoadAlbumImageRequest(UserPreferences.getInstance().getToken(), mAlbum.albumId, skip);
        getPresenter().getImageAlbum(request);
        mSwipeRefreshLayout.setRefreshing(true);

        // after call loadMoreEnd() -> adapter cant call  onLoadMoreRequested()
        // call this func for enable call again onLoadMoreRequested() when scroll down
        mAdapter.notifyLoadMoreToLoading();
    }

    private void loadMoreAlbum() {
        skip += LoadAlbumImageRequest.TAKE;
        LoadAlbumImageRequest request = new LoadAlbumImageRequest(UserPreferences.getInstance().getToken(), mAlbum.albumId, skip);
        getPresenter().getImageAlbum(request);
    }

    private void showTextDoneInMenu() {

        if (mMenu != null) {
            mMenu.findItem(R.id.action_del).setVisible(false);
            mMenu.findItem(R.id.action_edit).setVisible(false);
            mMenu.findItem(R.id.action_done).setVisible(true);
        }
        mToolbar.setTitleCenter(R.string.title_menu_edit);
    }

    private void showTextMenuEdit() {

        if (mMenu != null) {
            mMenu.findItem(R.id.action_del).setVisible(false);
            mMenu.findItem(R.id.action_edit).setVisible(true);
            mMenu.findItem(R.id.action_done).setVisible(false);
        }
        mToolbar.setTitleCenter(mAlbum.albumName);
    }

    private void showMenuDelInMenu() {
        if (mMenu != null) {
            mMenu.findItem(R.id.action_del).setVisible(true);
            mMenu.findItem(R.id.action_edit).setVisible(false);
            mMenu.findItem(R.id.action_done).setVisible(false);
        }
        mToolbar.setTitleCenter(R.string.title_menu_delete);
    }

    private void hideAllMenu() {
        if (mMenu != null) {
            mMenu.findItem(R.id.action_del).setVisible(false);
            mMenu.findItem(R.id.action_edit).setVisible(false);
            mMenu.findItem(R.id.action_done).setVisible(false);
        }
    }

    private void showContainerEdit() {

        if (mContainerDescription != null) {
            mContainerDescription.setVisibility(View.GONE);
        }

        if (mContainerEdit != null) {
//            showViewAlpha(mContainerEdit); // animation alpha
            mContainerEdit.setVisibility(View.VISIBLE);
        }

        showTextDoneInMenu();  // show menu done
    }


    private void showContainerDescription() {

        if (mContainerEdit != null) {
            mContainerEdit.setVisibility(View.GONE);
        }

        if (mContainerDescription != null) {
//            showViewAlpha(mContainerDescription);// animation alpha
            mContainerDescription.setVisibility(View.VISIBLE);
            mTextDescription.setText(mAlbum.albumDes);

        }
        showTextMenuEdit(); // show text menu edit
    }


    /*************************************** RECYCLER VIEW ****************************************/
    @Override
    public void onItemClick(ItemImageInAlbum bean, int position, View view) {
        if (mIsOwn) {                    // if this is my album minus 1 because first item is "add image"
            position--;
        }

        AlbumDetailMediaActivity.launch(this, view, mAdapter.getData(), mAlbum.albumId, position, mIsOwn);

    }


    @Override
    public void onAddImage() {
        new Gallery.Builder().sortType(Gallery.SORT_BY_PHOTOS).isMultichoice(true).build().start(this);
    }

    @Override
    public void onShowPickerDelete() {
        showMenuDelInMenu();
    }

    /***************************************SERVER RESPONSE ***************************************/
    @Override
    public void getImageAlbumSuccess(LoadAlbumImageResponse response) {
        if (response != null && response.data != null && response.data.listImage != null) {
            mAdapter.addData(response.data.listImage);
        }

        if (mAdapter.getData().isEmpty()) {
            mAdapter.loadMoreEmpty();
        } else {
            mAdapter.loadMoreComplete();
        }
        mAdapter.loadMoreEnd();
    }

    @Override
    public void getImageAlbumFailure() {
        Toast.makeText(this, R.string.load_image_album_failure, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void getImageAlbumComplete() {
        mSwipeRefreshLayout.setRefreshing(false);

    }


    // ADD IMAGE TO ALBUM
    @Override
    public void addImagesToAlbumSuccess(AddImageAlbumResponse response) {
        // Add more image to album success
        mAlbum = response.data;
        RxEventBus.publish(SubjectCode.SUBJECT_ADD_IMAGE_TO_ALBUM, response.data);
        onRefresh();
    }

    @Override
    public void addImagesFailure() {
        Toast.makeText(this, R.string.cant_add_more_to_album, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void complete() {
        mProgressAlertDialog.hide();
    }


    @Override
    public void deleteImagesInAlbumComplete() {
        mProgressAlertDialog.hide();
    }

    @Override
    public void deleteImagesInAlbumSuccess(DelAlbumImageResponse response, List<ItemImageInAlbum> imagesSelected) {
        Toast.makeText(this, R.string.delete_album_success, Toast.LENGTH_SHORT).show();
        mAdapter.removeItem(imagesSelected);
        onBackPressed();

        // decrease 1 because first item is "add image"
        mAlbum.numberImage = mAlbum.numberImage - imagesSelected.size();
        // update data item in Previous activity
        RxEventBus.publish(SubjectCode.SUBJECT_REFRESH_ALBUM, mAlbum);
    }

    @Override
    public void updateAlbumSuccess(UpdateAlbumResponse response) {

        if (!response.data.albumName.equals(mAlbum.albumName) || !response.data.albumDes.equals(mAlbum.albumDes) || response.data.privacy != mAlbum.privacy) {
            Toast.makeText(this, R.string.update_album_success, Toast.LENGTH_SHORT).show();
        }

        onBackPressed();

        // update field in edit layout
        mAlbum = response.data;
        initEditLayout(mAlbum);

        if (mAlbum.albumDes != null && !mAlbum.albumDes.isEmpty()) {
            showContainerDescription();
        } else {
            mContainerDescription.setVisibility(View.GONE);
        }

        // update data item in Previous activity
        RxEventBus.publish(SubjectCode.SUBJECT_REFRESH_ALBUM, mAlbum);
    }

    @Override
    public void updateAlbumFailure() {
        Toast.makeText(this, R.string.update_album_failure, Toast.LENGTH_SHORT).show();
        onBackPressed();
    }

    @Override
    public void updateComplete() {

    }

    @Override
    public void deleteAlbumSuccess() {
        RxEventBus.publish(SubjectCode.SUBJECT_DELETE_ALBUM, mAlbum);
        mAlbum = null;
        finish();
    }

    @Override
    public void deleteAlbumComplete() {
        mProgressAlertDialog.hide();
    }


    // To animate view slide out from bottom to top
    public void hideViewAlpha(View view) {
        AlphaAnimation animate = new AlphaAnimation(1.0f, 0.0f);
        animate.setDuration(500);
        animate.setFillAfter(true);
        view.startAnimation(animate);
        view.setVisibility(View.GONE);
    }

    // To animate view slide out from bottom to top
    public void showViewAlpha(View view) {
        AlphaAnimation animate = new AlphaAnimation(0.0f, 1.0f);
        animate.setDuration(500);
        animate.setFillAfter(true);
        view.startAnimation(animate);
        view.setVisibility(View.VISIBLE);
    }


}
