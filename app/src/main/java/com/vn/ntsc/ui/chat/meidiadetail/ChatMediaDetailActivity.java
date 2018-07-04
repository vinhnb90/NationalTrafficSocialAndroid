package com.vn.ntsc.ui.chat.meidiadetail;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.tux.socket.models.Media;
import com.vn.ntsc.BuildConfig;
import com.vn.ntsc.R;
import com.vn.ntsc.repository.TypeView;
import com.vn.ntsc.repository.model.chat.ChatMessage;
import com.vn.ntsc.repository.model.chat.ItemFileChat;
import com.vn.ntsc.repository.model.media.MediaEntity;
import com.vn.ntsc.ui.chat.ChatActivity;
import com.vn.ntsc.ui.chat.generalibrary.GeneraLibraryActivity;
import com.vn.ntsc.ui.mediadetail.base.AudioFragment;
import com.vn.ntsc.ui.mediadetail.base.BaseMediaActivity;
import com.vn.ntsc.ui.mediadetail.base.DetailMediaPresenter;
import com.vn.ntsc.ui.mediadetail.base.IDetailMediaInteractor;
import com.vn.ntsc.ui.mediadetail.base.MediaAdapter;
import com.vn.ntsc.ui.mediadetail.base.VideoFragment;
import com.vn.ntsc.ui.mediadetail.base.ViewPagerMedia;
import com.vn.ntsc.ui.mediadetail.base.video.PlayerController;
import com.vn.ntsc.utils.LogUtils;
import com.vn.ntsc.widget.permissions.Permission;
import com.vn.ntsc.widget.toolbar.ToolbarButtonRightClickListener;
import com.vn.ntsc.widget.toolbar.ToolbarTitleCenter;
import com.vn.ntsc.widget.views.dialog.ProgressAlertDialog;
import com.vn.ntsc.widget.views.images.mediadetail.image.ImageViewTouch;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

/**
 * Created by dev22 on 3/2/18.
 */
public class ChatMediaDetailActivity extends BaseMediaActivity<ChatMediaDetailPresenter>
        implements ChatMediaDetailContract.View, ToolbarButtonRightClickListener,
        ImageViewTouch.IteractorGestureListener,
        PlayerController.VisibilityListener,
        ViewPagerMedia.IDoTaskWhenTouch {

    /*----------------------------------------var----------------------------------------*/
    public static final String TAG = ChatMediaDetailActivity.class.getSimpleName();

    private static final String CHAT_MESSAGE = "input_data";
    private static final String CHAT_MESSAGE_ALL = "input_data_all";
    private static final String CHAT_TYPE = "chat_type";
    private static final String CHAT_TOTAL_FILE = "chat_total_file";
    private static final String INDEX = "index";

    private static final int REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE = 0x00;

    @BindView(R.id.activity_media_chat_container)
    RelativeLayout container;

    @BindView(R.id.activity_media_chat_view_pager)
    ViewPagerMedia viewPager;

    @BindView(R.id.toolbar)
    ToolbarTitleCenter mToolbar;

    private ArrayList<ItemFileChat> mListFileChat;
    private String type;
    private ProgressAlertDialog mAlertDialog;
    private int total;
    private IDetailMediaInteractor.Presenter mPresenter;

    /*----------------------------------------instance----------------------------------------*/
    public static void launch(AppCompatActivity activity, ChatMessage chatMessage, int index) {
        Intent intent = new Intent(activity, ChatMediaDetailActivity.class);
        intent.putExtra(CHAT_MESSAGE, chatMessage);
        intent.putExtra(CHAT_TYPE, activity.getClass().getSimpleName());
        intent.putExtra(INDEX, index);
        LogUtils.e(TAG, "activity:" + activity.getClass().getSimpleName());
        activity.startActivity(intent);
    }

    public static void lauch(AppCompatActivity activity, List<ItemFileChat> mListFileChat, int index, int totalFile) {
        Intent intent = new Intent(activity, ChatMediaDetailActivity.class);
        intent.putParcelableArrayListExtra(CHAT_MESSAGE_ALL, (ArrayList<? extends Parcelable>) mListFileChat);
        intent.putExtra(CHAT_TYPE, activity.getClass().getSimpleName());
        intent.putExtra(INDEX, index);
        intent.putExtra(CHAT_TOTAL_FILE, totalFile);
        activity.startActivity(intent);
    }

    /*----------------------------------------lifecycle----------------------------------------*/
    @Override
    public void onCreateView(View rootView) {
//        setupToolbar(toolbar);
        getMediaComponent().inject(this);

        mAlertDialog = new ProgressAlertDialog(this);
        ChatMessage data = getIntent().getParcelableExtra(CHAT_MESSAGE);
        int index = getIntent().getIntExtra(INDEX, 0);
        total = getIntent().getIntExtra(CHAT_TOTAL_FILE, 0);
        type = getIntent().getStringExtra(CHAT_TYPE);
        checkType(index, total, type);

        //presenter
        new DetailMediaPresenter.Builder(null, this).build();

        List<MediaEntity> mediaData = new ArrayList<>();
        if (data != null) {
            mediaData = convertData(data);
            ViewPagerBuilder builder = new ViewPagerBuilder(mediaData, viewPager, index);
            builder
                    .setViewContainer(container)
                    .setiDoTaskWhenTouch(this)
                    .build();

            //save data
            if (mPresenter != null)
                mPresenter.saveListMediaPlaying(mediaData);
        } else {
            mListFileChat = getIntent().getParcelableArrayListExtra(CHAT_MESSAGE_ALL);

            mediaData = convertData1(mListFileChat);
            ViewPagerBuilder builder = new ViewPagerBuilder(mediaData, viewPager, index);
            builder
                    .setViewContainer(container)
                    .setiDoTaskWhenTouch(this)
                    .build();

            //save data
            if (mPresenter != null)
                mPresenter.saveListMediaPlaying(mediaData);
        }


    }

    /*----------------------------------------override----------------------------------------*/
    @Override
    public int getLayoutId() {
        return R.layout.activity_media_chat;
    }

    @Override
    public void onViewReady() {
        super.onViewReady();
        setSupportActionBar(mToolbar);
        mToolbar.setActionbar(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        super.onPageSelected(position);
        int pos = position + 1;
        LogUtils.e(TAG, "onPageSelected:" + position + "type-->" + type);
        if (type.equals(GeneraLibraryActivity.class.getSimpleName())) {
            mToolbar.setTitleCenter(String.format("(%d/%d)", pos, total));
        }

    }

    @Override
    public void onPageScrollStateChanged(int state) {

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
    public void downloadComplete(File file) {
        Toast.makeText(this, R.string.end_download, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void downloadError() {
        Toast.makeText(this, R.string.download_error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onComplete() {
        mAlertDialog.hide();
    }

    @Override
    public void onToolbarButtonRightClick(View view) {
        if (type.equals(ChatActivity.class.getSimpleName())) {
            GeneraLibraryActivity.startActivity(this);
        } else {
            requestAccessPermission(REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
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
            mToolbar.setVisibility(View.INVISIBLE);
        } else {
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

    //-----------------------------------------end set up view pager custom media

    /*----------------------------------------func----------------------------------------*/
    /*----------------------------------------inner----------------------------------------*/


    private List<MediaEntity> convertData(ChatMessage data) {
        List<MediaEntity> mediaEntities = new ArrayList<>();
        for (int i = 0; i < data.listFile.size(); i++) {
            Media.FileBean fileBean = data.listFile.get(i);
            MediaEntity mediaEntity = new MediaEntity(i, fileBean.fileUrl, fileBean.fileType, fileBean.thumbnailUrl);
            mediaEntities.add(mediaEntity);
        }
        return mediaEntities;
    }

    private List<MediaEntity> convertData1(List<ItemFileChat> itemFileChats) {
        List<MediaEntity> mediaEntities = new ArrayList<>();
        for (int i = 0; i < itemFileChats.size(); i++) {
            ItemFileChat child = itemFileChats.get(i);
            MediaEntity mediaEntity = new MediaEntity(i, child.fileUrl, child.type, child.thumbnailUrl);
            mediaEntities.add(mediaEntity);
        }
        return mediaEntities;
    }

    private void checkType(int index, int total, String type) {
        if (type.equals(ChatActivity.class.getSimpleName())) {
            mToolbar.setIconRight(R.drawable.ic_gallery);
        } else {
            mToolbar.setIconRight(R.drawable.ic_dowload_file);
            int pos = index + 1;
            mToolbar.setTitleCenter(String.format("(%d/%d)", pos, total));
        }
    }

    public void requestAccessPermission(final int requestCode, final String... permissions) {
        getRxPermissions().requestEach(permissions)
                .subscribe(new Consumer<Permission>() {
                    @Override
                    public void accept(Permission permission) {
                        Log.i(TAG, "Permission result " + permission);
                        if (permission != null && permission.granted) {

                            switch (permission.name) {
                                case Manifest.permission.WRITE_EXTERNAL_STORAGE:
                                    if (requestCode == REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE) {
                                        showDialogDownload();
                                    }
                                    break;
                            }
                        } else if (permission != null && permission.shouldShowRequestPermissionRationale) {
                            // Denied permission without ask never again
                            Toast.makeText(context, getString(R.string.request_access_permission_denied), Toast.LENGTH_SHORT).show();
                        } else {
                            // Denied permission with ask never again Need to go to the settings
                            Toast.makeText(context, getString(R.string.request_access_permission_denied), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable t) {
                        Log.e(TAG, "onError", t);
                    }
                }, new Action() {
                    @Override
                    public void run() {
                        Log.i(TAG, "OnComplete");
                    }
                });
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
                                Toast.makeText(ChatMediaDetailActivity.this, R.string.start_download, Toast.LENGTH_SHORT).show();
                                getPresenter().saveMedia(ChatMediaDetailActivity.this, getCurrentFile());
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

    public ItemFileChat getCurrentFile() {
        ItemFileChat child = mListFileChat.get(getCurrentPositionMedia());
        switch (child.type) {
            case TypeView.MediaDetailType.IMAGE_TYPE:
            case TypeView.MediaDetailType.VIDEO_TYPE:
            case TypeView.MediaDetailType.AUDIO_TYPE:

                if (child.fileUrl != null && !child.fileUrl.contains(BuildConfig.SERVER_MEDIA_URL)) {
                    child.fileUrl = BuildConfig.SERVER_MEDIA_URL + child.fileUrl;
                }

                if (child.thumbnailUrl != null && !child.thumbnailUrl.contains(BuildConfig.SERVER_MEDIA_URL)) {
                    child.thumbnailUrl = BuildConfig.SERVER_MEDIA_URL + child.thumbnailUrl;
                }
                break;
            case TypeView.MediaDetailType.STREAM_TYPE:
                break;
        }

        return child;
    }

}

