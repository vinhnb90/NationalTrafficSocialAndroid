package com.vn.ntsc.ui.profile.media.createAlbum;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tux.mylab.MediaPickerBaseActivity;
import com.example.tux.mylab.camera.Camera;
import com.example.tux.mylab.camera.cameraview.CameraView;
import com.example.tux.mylab.gallery.Gallery;
import com.example.tux.mylab.gallery.data.MediaFile;
import com.vn.ntsc.widget.views.gallerybottom.GridSpacingItemDecoration;
import com.vn.ntsc.R;
import com.vn.ntsc.core.views.BaseActivity;
import com.vn.ntsc.repository.media.MediaFileRepository;
import com.vn.ntsc.repository.model.mediafile.MediaFileBean;
import com.vn.ntsc.repository.model.myalbum.ItemImageInAlbum;
import com.vn.ntsc.repository.model.myalbum.LoadAlbum.LoadAlbumResponse;
import com.vn.ntsc.repository.preferece.UploadSettingPreference;
import com.vn.ntsc.repository.preferece.UserPreferences;
import com.vn.ntsc.services.addImageAlbum.UpLoadImageToAlbumService;
import com.vn.ntsc.ui.profile.media.edit.description.album.EditAlbumDescriptionActivity;
import com.vn.ntsc.utils.DimensionUtils;
import com.vn.ntsc.widget.mediafile.MediaFileContract;
import com.vn.ntsc.widget.mediafile.MediaFilePresenter;
import com.vn.ntsc.widget.permissions.Permission;
import com.vn.ntsc.widget.toolbar.ToolbarButtonRightClickListener;
import com.vn.ntsc.widget.toolbar.ToolbarTitleCenter;
import com.vn.ntsc.widget.views.textview.TextViewVectorCompat;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static com.vn.ntsc.repository.ActivityResultRequestCode.REQUEST_PRIVACY_CREATE_ALBUM;

/**
 * Created by ThoNh on 11/14/2017.
 */

public class CreateAlbumActivity extends BaseActivity implements CreateAlbumContract.View, MediaFileContract.View, ToolbarButtonRightClickListener {

    private static final String TAG = CreateAlbumActivity.class.getSimpleName();
    private static final String SHARE_ELEMENT_VIEW = "CREATE_ALBUM_SHARE_ELEMENT_VIEW";
    private static final String CREATE_ALBUM_PRIVACY = "CREATE_ALBUM_PRIVACY";
    private static final int REQUEST_READ_EXTERNAL_STORAGE_CODE = 0x11;
    private static final int REQUEST_CAMERA_CODE = 0x12;
    private static final int MAX_LENGTH_TITLE = 30;
    private static final int MAX_LENGTH_DESC = 512;

    private final int ORDER_BY_LIMIT_DEFAULT = 0;
    private final int ORDER_BY_OFFSET_DEFAULT = 0;

    private int orderByLimit = ORDER_BY_LIMIT_DEFAULT;
    private int orderByOffset = ORDER_BY_OFFSET_DEFAULT;

    public static void startActivity(AppCompatActivity activity, View view) {
        ActivityOptionsCompat compat = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, view, SHARE_ELEMENT_VIEW);
        Intent intent = new Intent(activity, CreateAlbumActivity.class);
        activity.startActivity(intent, compat.toBundle());
    }

    @BindView(R.id.toolbar)
    ToolbarTitleCenter mToolbar;

    @BindView(R.id.activity_comment_rv_image_selected)
    RecyclerView mImageSelected;

    @BindView(R.id.activity_comment_rv_image_choose)
    RecyclerView mImageChoose;

    @BindView(R.id.activity_create_new_album_privacy)
    TextViewVectorCompat mImvPrivacy;

    @BindView(R.id.activity_comment_edit_desc)
    EditText mEditDesc;

    @BindView(R.id.activity_comment_edt_name)
    EditText mEdtName;

    Disposable disposable;

    private SelectedImageAdapter adapterSelected;
    private ChooseImageAdapter chooseadapter;
    private MediaFilePresenter mMediaFilePresenter;
    private int privacy = 0;

    @Override
    public int getLayoutId() {
        return R.layout.activity_create_new_album;
    }

    @Override
    public void onCreateView(View rootView) {

        mEditDesc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditAlbumDescriptionActivity.launch(CreateAlbumActivity.this, mEditDesc, true, mEditDesc.getText().toString(), false, true);
            }
        });

        initSelectedRecyclerView();
        initChooseRecyclerView();

        ViewCompat.setTransitionName(mToolbar, SHARE_ELEMENT_VIEW);
    }

    @Override
    public void onViewReady() {
        setSupportActionBar(mToolbar);
        mToolbar.setActionbar(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        mToolbar.setVisibilityButtonRight(true);
        mToolbar.setButtonRightListener(this);

        //set focus to show hide key board
        setupUI(super.viewRoot);
    }

    @Override
    public void onResume(View viewRoot) {
        super.onResume(viewRoot);
        requestPermission(REQUEST_READ_EXTERNAL_STORAGE_CODE, Manifest.permission.READ_EXTERNAL_STORAGE);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (disposable != null)
            disposable.dispose();
    }

    @Override
    public void initSelectedRecyclerView() {
        adapterSelected = new SelectedImageAdapter(
                new SelectedImageAdapter.SelectedImageEventListener() {
                    @Override
                    public void onDeselect(MediaFileBean bean, int position, View view) {
                        adapterSelected.remove(bean);
                        chooseadapter.unSelected(bean);
                    }
                });

        mImageSelected.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mImageSelected.setAdapter(adapterSelected);
    }

    @Override
    public void initChooseRecyclerView() {
        chooseadapter = new ChooseImageAdapter(
                new ChooseImageAdapter.ChooseImageEventListener() {
                    @Override
                    public void onChoose(MediaFileBean bean, int position, View view) {
                        adapterSelected.addData(bean);
                        mImageSelected.smoothScrollToPosition(adapterSelected.getItemCount());
                    }

                    @Override
                    public void onUnChoose(MediaFileBean bean, int position, View view) {
                        adapterSelected.remove(bean);
                        mImageSelected.smoothScrollToPosition(adapterSelected.getItemCount());
                    }

                    @Override
                    public void onOutOfSizeAlbum() {
                        String format = String.format(Locale.getDefault(), getString(R.string.max_image_size_up_load), UploadSettingPreference.getInstance().getMaxImageNumber());
                        Toast.makeText(context, format, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onOpenCamera() {
                        requestPermission(REQUEST_CAMERA_CODE, Manifest.permission.CAMERA);
                    }
                });

        mImageChoose.setLayoutManager(new GridLayoutManager(this, 4));
        mImageChoose.addItemDecoration(new GridSpacingItemDecoration(4, DimensionUtils.convertDpToPx(4), true));

        mImageChoose.setItemViewCacheSize(1000);
//        mImageChoose.setDrawingCacheEnabled(true);
//        mImageChoose.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_LOW);
        mImageChoose.setHasFixedSize(true);

        mImageChoose.setAdapter(chooseadapter);
    }

    private void openCameraAndTakePhoto() {
        new Camera.Builder()
                .isVideoMode(false)
                .flashMode(CameraView.FLASH_AUTO)
                .build()
                .start(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Camera.REQUEST_CODE_CAMERA:
                case Gallery.REQUEST_CODE_GALLERY:
                    Parcelable[] photos = data.getParcelableArrayExtra(MediaPickerBaseActivity.RESULT_KEY);
                    for (Parcelable photo : photos) {
                        MediaFile takeMediaUri = (MediaFile) photo;
                        MediaFileBean bean = new MediaFileBean(-9999, takeMediaUri.getPath(), MediaFileBean.IMAGE, true);
                        // position 0 is camera
                        // set to position 1
                        chooseadapter.setData(1, bean);
                        adapterSelected.addData(bean);
                    }

                case REQUEST_PRIVACY_CREATE_ALBUM:
                    int privacy = data.getIntExtra(CREATE_ALBUM_PRIVACY, 0);
                    this.privacy = privacy;
                    switch (privacy) {
                        case 0: // public
                            mImvPrivacy.setVectorDrawableLeft(R.drawable.ic_privacy_public_24dp_gray);
                            mImvPrivacy.setText(R.string.public_privacy);
                            break;
                        case 2: // private
                            mImvPrivacy.setVectorDrawableLeft(R.drawable.ic_privacy_only_me_24dp_gray);
                            mImvPrivacy.setText(R.string.onlyme_privacy);
                            break;
                    }
                    break;

                case EditAlbumDescriptionActivity.REQ_CODE_EDIT:
                    Bundle bundle = data.getExtras();
                    if (bundle != null) {
                        String des = bundle.getString(EditAlbumDescriptionActivity.EXTRA_DESCRIPTION_RETURN);
                        mEditDesc.setText(des == null ? "" : des);
                    }
                    break;
            }
        }
    }


    @SuppressLint("CheckResult")
    private void requestPermission(final int requestCode, String... permissions) {
        getRxPermissions().requestEach(permissions)
                .subscribe(new Consumer<Permission>() {
                    @Override
                    public void accept(Permission permission) throws Exception {
                        if (permission.granted) {
                            switch (permission.name) {
                                case Manifest.permission.CAMERA:
                                    if (requestCode == REQUEST_CAMERA_CODE) {
                                        openCameraAndTakePhoto();
                                    }
                                    break;
                                case Manifest.permission.READ_EXTERNAL_STORAGE:
                                    if (requestCode == REQUEST_READ_EXTERNAL_STORAGE_CODE) {
                                        mMediaFilePresenter = new MediaFilePresenter(CreateAlbumActivity.this);
                                        mMediaFilePresenter.onExecuteGetMediaFileReady(MediaFileRepository.IMAGE_LOADER_ID_MODE, orderByLimit, orderByOffset, getContentResolver());
                                    }
                                    break;
                            }

                        } else if (permission.shouldShowRequestPermissionRationale) {
                            // Denied permission without ask never again
                            Toast.makeText(context, getString(R.string.request_access_permission_denied), Toast.LENGTH_SHORT).show();
                        } else {
                            // Denied permission with ask never again Need to go to the settings
                            Toast.makeText(context, getString(R.string.request_access_permission_denied), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onCursorLoaded(final Cursor cursor, int mediaType) {
        Observable<MediaFileBean> observable = new Observable<MediaFileBean>() {
            @Override
            protected void subscribeActual(Observer<? super MediaFileBean> observer) {
                if (cursor != null) {
                    while (cursor.moveToNext()) {

                        try {
                            long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media._ID));
                            String imagePath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));

                            File file = new File(imagePath);
                            if (file.exists()) {
                                observer.onNext(new MediaFileBean(id, file.getPath(), MediaFileBean.IMAGE));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    Log.d(TAG, "subscribeActual: end task load file image!");
                } else {
                    observer.onError(new Exception("cursor is null.!"));
                }

                observer.onComplete();
            }
        };

        disposable = observable
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<MediaFileBean>() {
                               @Override
                               public void accept(MediaFileBean mediaFileBeans) throws Exception {
                                   Log.d(TAG, "accept: mediaFileBeans " + mediaFileBeans.mediaUri);
                                   chooseadapter.addData(mediaFileBeans);
                               }
                           }
                        , new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                throwable.printStackTrace();
                            }
                        });
    }

    @Override
    public void onCursorLoadFailed(Throwable throwable, int mediaType) {
        chooseadapter.setEmptyView(R.layout.layout_empty);
    }

    @Override
    public void onToolbarButtonRightClick(View view) {
        String token = UserPreferences.getInstance().getToken();
        String name = mEdtName.getText().toString().trim();
        String desc = mEditDesc.getText().toString().trim();

        if (adapterSelected.getItemCount() < 1) {
            Toast.makeText(CreateAlbumActivity.this, R.string.choose_image, Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(name)) {
            Toast.makeText(CreateAlbumActivity.this, R.string.enter_title, Toast.LENGTH_SHORT).show();
            return;
        }


        LoadAlbumResponse.DataBean bean = new LoadAlbumResponse.DataBean();
        bean.numberImage = adapterSelected.getData().size();
        bean.albumId = null;
        bean.privacy = privacy;
        bean.albumDes = desc;
        bean.albumName = name;

        bean.userId = UserPreferences.getInstance().getUserId();

        ItemImageInAlbum item = new ItemImageInAlbum();
        item.thumbnailUrl = adapterSelected.getData().get(0).mediaUri;
        bean.imageList = item;
        bean.isUploading = true;

        Intent intent = new Intent(this, UpLoadImageToAlbumService.class);
        intent.putExtra(UpLoadImageToAlbumService.EXTRA_TOKEN, token);
        intent.putExtra(UpLoadImageToAlbumService.EXTRA_ITEM_ALBUM, bean);
        intent.putParcelableArrayListExtra(UpLoadImageToAlbumService.EXTRA_IMAGES,
                (ArrayList<? extends Parcelable>) adapterSelected.getData());

        startService(intent);

        finish();
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }

    public void setupUI(View view) {

        // Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard(CreateAlbumActivity.this);
                    return false;
                }
            });
        }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView);
            }
        }
    }
}
