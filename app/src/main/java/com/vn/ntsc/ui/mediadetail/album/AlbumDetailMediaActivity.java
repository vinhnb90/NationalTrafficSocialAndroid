package com.vn.ntsc.ui.mediadetail.album;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.vn.ntsc.R;
import com.vn.ntsc.repository.TypeView;
import com.vn.ntsc.repository.model.media.MediaEntity;
import com.vn.ntsc.repository.model.myalbum.DeleteImageInAlbum.DelAlbumImageRequest;
import com.vn.ntsc.repository.model.myalbum.DeleteImageInAlbum.DelAlbumImageResponse;
import com.vn.ntsc.repository.model.myalbum.ItemImageInAlbum;
import com.vn.ntsc.repository.model.report.ReportRequest;
import com.vn.ntsc.repository.model.timeline.DeleteBuzzResponse;
import com.vn.ntsc.repository.model.timeline.datas.sub.ListBuzzChild;
import com.vn.ntsc.repository.preferece.UserPreferences;
import com.vn.ntsc.ui.mediadetail.base.AudioFragment;
import com.vn.ntsc.ui.mediadetail.base.BaseMediaActivity;
import com.vn.ntsc.ui.mediadetail.base.DetailMediaPresenter;
import com.vn.ntsc.ui.mediadetail.base.IDetailMediaInteractor;
import com.vn.ntsc.ui.mediadetail.base.MediaAdapter;
import com.vn.ntsc.ui.mediadetail.base.VideoFragment;
import com.vn.ntsc.ui.mediadetail.base.ViewPagerMedia;
import com.vn.ntsc.ui.mediadetail.base.video.PlayerController;
import com.vn.ntsc.utils.Constants;
import com.vn.ntsc.utils.LogUtils;
import com.vn.ntsc.widget.eventbus.RxEventBus;
import com.vn.ntsc.widget.eventbus.SubjectCode;
import com.vn.ntsc.widget.toolbar.ToolbarTitleCenter;
import com.vn.ntsc.widget.views.images.mediadetail.image.ImageViewTouch;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindArray;
import butterknife.BindView;

/**
 * Created by ThoNh on 12/4/2017.
 */

public class AlbumDetailMediaActivity extends BaseMediaActivity<AlbumDetailMediaPresenter>
        implements AlbumDetailMediaContract.View, IDetailMediaInteractor.ActivityView, ImageViewTouch.IteractorGestureListener,
        PlayerController.VisibilityListener, ViewPagerMedia.IDoTaskWhenTouch {

    /*----------------------------------------var----------------------------------------*/

    private static final String ELEMENT_MEDIA = "ALBUM_MEDIA_ELEMENT_MEDIA";
    private static final String EXTRA_FIRST_POSITION = "EXTRA_FIRST_POSITION";
    private static final String EXTRA_ALBUM_DATA = "EXTRA_ALBUM_DATA";
    private static final String EXTRA_LIST_IMAGE = "EXTRA_LIST_IMAGE";
    private static final String EXTRA_ALBUM_ID = "EXTRA_ALBUM_ID";
    private static final String EXTRA_IS_OWN_ALBUM = "EXTRA_IS_OWN_ALBUM";
    private static final String TAG = AlbumDetailMediaActivity.class.getName();

    @BindView(R.id.activity_media_album_detail_container)
    RelativeLayout mContainer;

    @BindView(R.id.activity_media_album_detail_image_pager)
    ViewPagerMedia viewPager;

    @BindView(R.id.toolbar)
    ToolbarTitleCenter mToolbar;

    @BindView(R.id.activity_media_album_detail_scrim_view)
    View scrimView;

    @BindView(R.id.activity_media_album_detail_panel_bottom)
    RelativeLayout mBottomPanel;

    @BindView(R.id.activity_media_album_detail_button_center)
    TextView mButtonCenter;

    @BindView(R.id.activity_media_album_detail_button_right)
    TextView mButtonRight;

    @BindArray(R.array.ReportArray)
    String[] reportArray;

    private List<ItemImageInAlbum> mListImage;
    private List<MediaEntity> mData;
    private int mFirstPosition;
    private String mAlbumId;
    private boolean mIsOwn;
    private IDetailMediaInteractor.Presenter mPresenter;

    /*----------------------------------------instance----------------------------------------*/
    public static void launch(Context context, List<MediaEntity> images, int positionIndexItem) {
        final Intent intent = new Intent(context, AlbumDetailMediaActivity.class);
        intent.putExtra(EXTRA_ALBUM_DATA, (ArrayList<? extends Parcelable>) images);
        intent.putExtra(EXTRA_FIRST_POSITION, positionIndexItem);
        context.startActivity(intent);
    }

    public static void launch(AppCompatActivity activity, View v, List<ItemImageInAlbum> images, String albumId, int positionIndexItem, boolean isOwn) {
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, v, ELEMENT_MEDIA);
        final Intent intent = new Intent(activity, AlbumDetailMediaActivity.class);
        intent.putExtra(EXTRA_LIST_IMAGE, (ArrayList<? extends Parcelable>) images);
        intent.putExtra(EXTRA_FIRST_POSITION, positionIndexItem);
        intent.putExtra(EXTRA_ALBUM_ID, albumId);
        intent.putExtra(EXTRA_IS_OWN_ALBUM, isOwn);
        activity.startActivity(intent, options.toBundle());
    }

    /*----------------------------------------lifecycle----------------------------------------*/
    @Override
    public void onCreateView(View rootView) {
        getModulesCommonComponent().inject(this);

        ViewCompat.setTransitionName(viewPager, ELEMENT_MEDIA);

        mListImage = new ArrayList<>();

        getDataBundle();

        //presenter
        new DetailMediaPresenter.Builder(null, this).build();
        ViewPagerBuilder builder = new ViewPagerBuilder(mData, viewPager, mFirstPosition);
        builder
                .setViewContainer(mContainer)
                .setiDoTaskWhenTouch(this)
                .build();

        //save data
        if (mPresenter != null)
            mPresenter.saveListMediaPlaying(convertListMediaEntity(mListImage));

        setBottomPanel();
    }

    @Override
    public void onBackPressed() {
        finish();
    }


    /*----------------------------------------override----------------------------------------*/
    @Override
    public int getLayoutId() {
        return R.layout.activity_media_album_detail;
    }

    @Override
    public void onViewReady() {
        setSupportActionBar(mToolbar);
        mToolbar.setActionbar(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void orientationChange(int orient) {
        switch (orient) {
            case Configuration.ORIENTATION_LANDSCAPE:
                updateViewLandscape();
                break;
            case Configuration.ORIENTATION_PORTRAIT:
                updateViewPortrait();
                break;
        }
    }


    //-----------------------------------------set up view pager custom media
    @Override
    public void setPresenter(@NonNull IDetailMediaInteractor.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void onFullScreen(boolean needFull) {
        if (needFull) {
            mBottomPanel.setVisibility(View.INVISIBLE);
            mToolbar.setVisibility(View.INVISIBLE);
        } else {
            mBottomPanel.setVisibility(View.VISIBLE);
            mToolbar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDoubleTapZoom(boolean isZoomed) {
        LogUtils.i(TAG, "View child is in state onDoubleTapZoom!");
        viewPager.updateStatusViewChildIsDoTaskIfself(isZoomed);
    }

    @Override
    public void onScrollZoom(boolean isZoomed) {
        LogUtils.i(TAG, "View child is in state onScrollZoom!");
        viewPager.updateStatusViewChildIsDoTaskIfself(isZoomed);
    }

    @Override
    public void onFling(boolean isCanFlingHorizolltal) {
        LogUtils.i(TAG, "View child is in state onFling!");
        viewPager.updateStatusViewChildIsDoTaskIfself(isCanFlingHorizolltal);
    }

    @Override
    public void onScaleZoom(boolean isZoomed) {
        LogUtils.i(TAG, "View child is in state onScaleZoom!");
        viewPager.updateStatusViewChildIsDoTaskIfself(isZoomed);
    }

    @Override
    public boolean isAllowZoom() {
        return viewPager.isCanAllowDoTaskIfself();
    }

    //-----------------------------------------end set up view pager custom media


    @Override
    public void deleteImagesInAlbumComplete() {

    }

    @Override
    public void deleteImagesInAlbumFailure() {
        Toast.makeText(this, R.string.common_error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void deleteImagesInAlbumSuccess(DelAlbumImageResponse response) {
        if (mListImage != null && getCurrentPositionMedia() > -1) {
            mListImage.remove(getCurrentPositionMedia());
            removeItem(getCurrentPositionMedia());
            Toast.makeText(this, R.string.delete_image_success, Toast.LENGTH_SHORT).show();
            RxEventBus.publish(SubjectCode.SUBJECT_DELETE_IMAGE_IN_ALBUM, mListImage);
        }
    }

    @Override
    public void saveImageSuccess() {
        Toast.makeText(this, R.string.save_image_success, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void saveImageFailure() {
        Toast.makeText(this, R.string.save_image_failure, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void buzzNotFound() {

    }

    @Override
    public void deleteBuzzSuccess(DeleteBuzzResponse response, ListBuzzChild child) {

    }

    @Override
    public void onComplete() {

    }

    @Override
    public void downloadComplete(File file) {

    }

    @Override
    public void downloadError() {

    }

    @Override
    public void shareMediaFailure() {

    }

    @Override
    public void shareMediaSuccess() {

    }

    @Override
    public void reportFailure() {
        Toast.makeText(this, R.string.report_image_failure, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void likeMediaSuccess() {

    }

    @Override
    public void likeMediaFailure() {

    }

    @Override
    public void reportSuccess() {
        new AlertDialog.Builder(this, R.style.AlertDialogApp)
                .setTitle(R.string.title_dialog_report_success)
                .setMessage(getString(R.string.content_dialog_report_success))
                .setPositiveButton(R.string.common_ok_2, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }

    /*----------------------------------------func----------------------------------------*/
    /*----------------------------------------inner----------------------------------------*/
    private void getDataBundle() {
        if (getIntent() != null) {
            if (getIntent().hasExtra(EXTRA_ALBUM_DATA)) {
                mData = getIntent().getParcelableArrayListExtra(EXTRA_ALBUM_DATA);
            }
            if (getIntent().hasExtra(EXTRA_ALBUM_ID)) {
                mAlbumId = getIntent().getStringExtra(EXTRA_ALBUM_ID);
            }
            if (getIntent().hasExtra(EXTRA_FIRST_POSITION)) {
                mFirstPosition = getIntent().getIntExtra(EXTRA_FIRST_POSITION, 0);
            }

            if (getIntent().hasExtra(EXTRA_IS_OWN_ALBUM)) {
                mIsOwn = getIntent().getBooleanExtra(EXTRA_IS_OWN_ALBUM, false);
            }

            if (getIntent().hasExtra(EXTRA_LIST_IMAGE)) {
                mListImage = getIntent().getParcelableArrayListExtra(EXTRA_LIST_IMAGE);
                mData = convertListMediaEntity(mListImage);
            }

        } else {
            Toast.makeText(this, R.string.common_error, Toast.LENGTH_SHORT).show();
            finish();
        }

        mBottomPanel.setVisibility(mAlbumId != null ? View.VISIBLE : View.GONE);
    }

    public void setBottomPanel() {
        if (mIsOwn) {

            int idRes = R.string.delete_image;
            switch (getCurrentMedia().mType) {
                case TypeView.MediaDetailType.AUDIO_TYPE:
                    idRes = R.string.delete_audio;
                    break;
                case TypeView.MediaDetailType.VIDEO_TYPE:
                case TypeView.MediaDetailType.STREAM_TYPE:
                    idRes = R.string.delete_video;
                    break;
                case TypeView.MediaDetailType.IMAGE_TYPE:
                    idRes = R.string.delete_image;
                    break;
            }

            // delete
            mButtonCenter.setText(idRes);
            mButtonCenter.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_delete_24dp, 0, 0, 0);

            // save
            mButtonRight.setText(R.string.save_image);
            mButtonRight.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_save_24dp, 0, 0, 0);

            mButtonCenter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDialogDelete();
                }
            });

            mButtonRight.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDialogDownload();
                }
            });

        } else {

            // save
            mButtonCenter.setText(R.string.save_image);
            mButtonCenter.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_save_24dp, 0, 0, 0);

            // report
            mButtonRight.setText(R.string.common_report_image);
            mButtonRight.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_report_24dp, 0, 0, 0);

            mButtonCenter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDialogDownload();
                }
            });

            mButtonRight.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDialogReport();
                }
            });
        }
    }

    public void updateViewPortrait() {
        showStatusBar();
        mToolbar.setVisibility(View.VISIBLE);
    }

    public void updateViewLandscape() {
        hideStatusBar();
        mToolbar.setVisibility(View.GONE);
    }

    private void hideStatusBar() {
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }

    private void showStatusBar() {
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_VISIBLE;
        decorView.setSystemUiVisibility(uiOptions);
    }

    private List<MediaEntity> convertListMediaEntity(List<ItemImageInAlbum> srcData) {
        List<MediaEntity> datas = new ArrayList<>();
        for (int i = 0; i < srcData.size(); i++) {
            ItemImageInAlbum itemImageInAlbum = srcData.get(i);
            datas.add(new MediaEntity(i, itemImageInAlbum.originalUrl, TypeView.MediaDetailType.IMAGE_TYPE, itemImageInAlbum.thumbnailUrl));
        }

        return datas;
    }

    private void showDialogDelete() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this, R.style.AlertDialogApp);
        alertDialog.setTitle(R.string.confirm_delete_title);

        if (getCurrentMedia() != null) {
            switch (getCurrentMedia().mType) {
                case TypeView.MediaDetailType.AUDIO_TYPE:
                    alertDialog.setMessage(R.string.confirm_delete_audio);
                    break;
                case TypeView.MediaDetailType.VIDEO_TYPE:
                    alertDialog.setMessage(R.string.confirm_delete_video);
                    break;
                case TypeView.MediaDetailType.IMAGE_TYPE:
                    alertDialog.setMessage(R.string.confirm_delete_image);
                    break;
            }
        }

        alertDialog
                .setPositiveButton(R.string.common_ok_2,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (mAlbumId != null && !mAlbumId.isEmpty() && !mListImage.isEmpty()) {
                                    List<String> fileDel = new ArrayList<>();
                                    fileDel.add(mListImage.get(getCurrentPositionMedia()).fileId);
                                    String token = UserPreferences.getInstance().getToken();
                                    DelAlbumImageRequest request = new DelAlbumImageRequest(token, mAlbumId, fileDel);
                                    getPresenter().deleteImagesInAlbum(request);
                                } else {
                                    Toast.makeText(AlbumDetailMediaActivity.this, R.string.common_error, Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                }
                            }
                        });

        alertDialog
                .setNegativeButton(R.string.common_cancel_2,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

        alertDialog.show();
    }

    private void showDialogDownload() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this, R.style.AlertDialogApp);
        alertDialog.setTitle(R.string.confirm_download);
        alertDialog.setMessage(R.string.confirm_download_content);

        alertDialog
                .setPositiveButton(R.string.common_ok_2,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(AlbumDetailMediaActivity.this, R.string.start_download, Toast.LENGTH_SHORT).show();
                                getPresenter().saveImage(AlbumDetailMediaActivity.this, mListImage.get(getCurrentPositionMedia()));
                            }
                        });

        alertDialog
                .setNegativeButton(R.string.common_cancel_2,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

        alertDialog.show();
    }

    private void showDialogReport() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(AlbumDetailMediaActivity.this, R.style.AlertDialogApp);
        alertDialog.setTitle(R.string.report_media);
        alertDialog.setCancelable(true);

        final ListAdapter listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, reportArray);

        alertDialog.setAdapter(listAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    dialog.dismiss();
                    return;
                }
                String token = UserPreferences.getInstance().getToken();
                String imageId = mListImage.get(getCurrentPositionMedia()).fileId;
                ReportRequest request = new ReportRequest(token, imageId, which, Constants.REPORT_ALBUM_IMG);
                getPresenter().reportImage(request);
            }
        });
        alertDialog.show();
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void doTaskWhenTouchDown() {

    }

    @Override
    public void doTaskWhenTouchMove(boolean isInStateSwipeHorizontall, boolean isInStateSwipeVertical, boolean isViewChildIsDoTaskIfSelf) {
        //get Fragment
        try {
            Fragment fragment = ((MediaAdapter) viewPager.getAdapter()).getFragmentHashMap().get(viewPager.getCurrentItem());
            if (fragment instanceof AudioFragment) {
                ((AudioFragment) fragment).onPause();
            }

            if (fragment instanceof VideoFragment) {
                ((VideoFragment) fragment).onPause();
            }
        } catch (Exception e) {
            LogUtils.e(TAG, "Error when switch Play pause video / audio!");
            return;
        }
    }

    @Override
    public void doTaskWhenTouchUp(boolean isRestoreView) {
        //get Fragment
        try {
            Fragment fragment = ((MediaAdapter) viewPager.getAdapter()).getFragmentHashMap().get(viewPager.getCurrentItem());
            if (fragment instanceof AudioFragment) {
                if (isRestoreView)
                    ((AudioFragment) fragment).onResume();
                else
                    ((AudioFragment) fragment).onPause();
            }

            if (fragment instanceof VideoFragment) {
                if (isRestoreView)
                    ((VideoFragment) fragment).onResume();
                else
                    ((VideoFragment) fragment).onPause();
            }
        } catch (Exception e) {
            LogUtils.e(TAG, "Error when switch Play pause video / audio!");
            return;
        }
    }
}
