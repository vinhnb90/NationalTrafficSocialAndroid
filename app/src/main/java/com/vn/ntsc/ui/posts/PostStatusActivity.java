package com.vn.ntsc.ui.posts;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tux.mylab.MediaPickerBaseActivity;
import com.example.tux.mylab.camera.Camera;
import com.example.tux.mylab.camera.cameraview.CameraView;
import com.example.tux.mylab.gallery.Gallery;
import com.example.tux.mylab.gallery.data.MediaFile;
import com.example.tux.mylab.utils.MediaSanUtils;
import com.nankai.designlayout.gallerybottom.GridSpacingItemDecoration;
import com.vn.ntsc.R;
import com.vn.ntsc.core.model.BaseBean;
import com.vn.ntsc.core.views.BaseActivity;
import com.vn.ntsc.repository.ActivityResultRequestCode;
import com.vn.ntsc.repository.media.MediaFileRepository;
import com.vn.ntsc.repository.model.mediafile.MediaFileBean;
import com.vn.ntsc.repository.model.poststatus.uploadsetting.UploadSettingBean;
import com.vn.ntsc.repository.model.poststatus.uploadsetting.UploadSettingRequest;
import com.vn.ntsc.repository.model.poststatus.uploadsetting.UploadSettingResponse;
import com.vn.ntsc.repository.model.timeline.datas.sub.ListTagFriendsBean;
import com.vn.ntsc.repository.preferece.UploadSettingPreference;
import com.vn.ntsc.repository.preferece.UserPreferences;
import com.vn.ntsc.services.uploadFileChat.PostStatusService;
import com.vn.ntsc.ui.tagfriends.TagFriendActivity;
import com.vn.ntsc.utils.Constants;
import com.vn.ntsc.utils.DirtyWordUtils;
import com.vn.ntsc.utils.ImagesUtils;
import com.vn.ntsc.utils.LogUtils;
import com.vn.ntsc.utils.MediaFileUtils;
import com.vn.ntsc.utils.SystemUtils;
import com.vn.ntsc.utils.Utils;
import com.vn.ntsc.utils.keyboard.KeyboardHeightObserver;
import com.vn.ntsc.utils.keyboard.KeyboardHeightProvider;
import com.vn.ntsc.utils.keyboard.KeyboardUtils;
import com.vn.ntsc.utils.time.TimeUtils;
import com.vn.ntsc.utils.upload.UploadFileValidator;
import com.vn.ntsc.widget.adapter.MultifunctionAdapter;
import com.vn.ntsc.widget.mediafile.MediaFileContract;
import com.vn.ntsc.widget.mediafile.MediaFilePickedEventListener;
import com.vn.ntsc.widget.mediafile.MediaFilePresenter;
import com.vn.ntsc.widget.permissions.Permission;
import com.vn.ntsc.widget.toolbar.ToolbarButtonLeftClickListener;
import com.vn.ntsc.widget.toolbar.ToolbarButtonRightClickListener;
import com.vn.ntsc.widget.toolbar.ToolbarTitleCenter;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static com.vn.ntsc.repository.ActivityResultRequestCode.REQUEST_ADD_TAG_FRIEND;
import static com.vn.ntsc.repository.ActivityResultRequestCode.REQUEST_POST_STATUS_PRIVACY;

/**
 * Created by nankai on 12/1/2017.
 * This class is currently replacing @{@link PostStatusActivity}
 */

public class PostStatusActivity extends BaseActivity<PostStatusPresenter> implements PostStatusContract.View, MediaFileContract.View,
        KeyboardHeightObserver, PostStatusEventListener<BaseBean>, MediaFilePickedEventListener<BaseBean>, UploadFileValidator.Toast
        , ToolbarButtonRightClickListener, ToolbarButtonLeftClickListener {

    public static final String TAG = PostStatusActivity.class.getSimpleName();

    private static final String ELEMENT_POST_STATUS = "post_status_element";

    private final int REQUEST_READ_EXTERNAL_FROM_DISPLAY_MEDIA_MODE = 102;
    private final int REQUEST_READ_EXTERNAL_WHEN_CLICK_SUBMIT_POST_MODE = 103;
    private final int MEDIA_LOADER_ID_MODE = 106;
    private final int REQUEST_PERMISSION_CAMERA = 107;

    private final int ORDER_BY_LIMIT_DEFAULT = 0;
    private final int ORDER_BY_OFFSET_DEFAULT = 0;
    private final int SPACING = 4;
    private final int SPAN_COUNT = 3;

    private final String TAG_FRIEND_PRIVACY_RESULT = "TAG_FRIEND_PRIVACY_RESULT";
    private final String TAG_FRIEND_LIST_RESULT = "TAG_FRIEND_LIST_RESULT";

    public static final String AUDIO_PICKED_TOTAL_FILE = "audio.picked.total.file";
    public static final String IMAGE_PICKED_TOTAL_FILE = "image.picked.total.file";
    public static final String VIDEO_PICKED_TOTAL_FILE = "video.picked.total.file";
    public static final String MEDIA_PICKED_TOTAL_FILE = "media.picked.total.file";
    public static final String MEDIA_PICKED_TOTAL_SIZE = "media.picked.total.size";
    public static final String POST_COMMENT_MAX_CHARACTER = "post.ic_comment.max.character";

    int privacy = 0;// 0 <=> public; 1 <=> friend
    boolean isSetPeekHeight = false;
    boolean hasShownKeyboard = false;
    int heightBottomView = 0;

    private int orderByLimit = ORDER_BY_LIMIT_DEFAULT;
    private int orderByOffset = ORDER_BY_OFFSET_DEFAULT;

    KeyboardHeightProvider mKeyboardHeightProvider;
    BottomSheetBehavior bottomSheetBehavior;
    MediaFilePresenter mMediaFilePresenter;
    UploadFileValidator mUploadFileValidator;
    UploadSettingBean mUploadSetting;
    GalleryAdapter mGalleryAdapter;
    ArrayList<ListTagFriendsBean> mTaggedFriend = new ArrayList<>();
    Map<String, Long> mUploadPickedInfo = new HashMap<>();
    MediaPickedAdapter mMediaPickedAdapter;

    boolean isUpdateGallery = true;

    @BindView(R.id.toolbar)
    ToolbarTitleCenter mToolbar;

    @BindView(R.id.layout_post_status_content)
    RelativeLayout layoutContent;
    @BindView(R.id.layout_post_status_avatar)
    ImageView mAvatar;
    @BindView(R.id.layout_post_status_user_name)
    TextView mTxtUsername;
    @BindView(R.id.layout_post_status_tag)
    TextView txtTag;
    @BindView(R.id.layout_post_status_private_icon)
    ImageView mPrivacyIcon;
    @BindView(R.id.layout_post_status_private_text)
    TextView mTxtPrivacy;
    @BindView(R.id.layout_post_status_edt_your_mind)
    EditText mYourMind;
    @BindView(R.id.layout_post_status_list_picker)
    RecyclerView mRecyclerViewMediaPicked;
    @BindView(R.id.layout_post_status_header_bottom)
    RelativeLayout layoutItemHeaderPostStatus;
    @BindView(R.id.layout_post_status_header_bottom_take_option)
    ImageView mTakeMediaOption;
    @BindView(R.id.layout_post_status_header_bottom_view)
    View bottomView;

    @BindView(R.id.lyaout_content_botton_sheet_gallery)
    RecyclerView mRecyclerViewGallery;

    Disposable disposable;

    /**
     * @param activity    {@link AppCompatActivity}
     * @param requestCode {@link ActivityResultRequestCode}
     */
    public static void launch(AppCompatActivity activity, View v, @ActivityResultRequestCode int requestCode) {
//        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, v, ELEMENT_POST_STATUS);
        final Intent intent = new Intent(activity, PostStatusActivity.class);
//        activity.startActivityForResult(intent, requestCode,options.toBundle());
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_post_status;
    }

    @Override
    public void onCreateView(View rootView) {

        getModulesCommonComponent().inject(this);

        setSupportActionBar(mToolbar);
        mToolbar.setActionbar(getSupportActionBar())
                .setButtonRightListener(this)
                .setButtonLeftListener(this);

        mKeyboardHeightProvider = new KeyboardHeightProvider(this);

        mMediaFilePresenter = new MediaFilePresenter(this);

        mUploadFileValidator = new UploadFileValidator();

        mUploadSetting = new UploadSettingBean();

        mUploadSetting = UploadSettingPreference.getInstance().getUploadSetting();

        bottomSheetBehavior = BottomSheetBehavior.from(findViewById(R.id.bottomSheetLayout));
        bottomSheetBehavior.setPeekHeight(getResources().getDimensionPixelSize(R.dimen.keyboard_height));
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        GridLayoutManager mGridLayoutManager = new GridLayoutManager(getApplicationContext(), SPAN_COUNT);
        mRecyclerViewGallery.setLayoutManager(mGridLayoutManager);
        GridSpacingItemDecoration mDecorationSpacing = new GridSpacingItemDecoration(mGridLayoutManager.getSpanCount(), SPACING, false);
        mRecyclerViewGallery.addItemDecoration(mDecorationSpacing);

        //Initialize media adapter
        mGalleryAdapter = new GalleryAdapter(this);
        mGalleryAdapter.openLoadAnimation(MultifunctionAdapter.SLIDEIN_BOTTOM);
        mRecyclerViewGallery.setAdapter(mGalleryAdapter);

        //Picked media
        mMediaPickedAdapter = new MediaPickedAdapter(this);
        mMediaPickedAdapter.openLoadAnimation(MultifunctionAdapter.SLIDEIN_RIGHT);
        mRecyclerViewMediaPicked.setHasFixedSize(true);
        mRecyclerViewMediaPicked.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
        mRecyclerViewMediaPicked.setAdapter(mMediaPickedAdapter);

        displayMediaPicked(false);


//        ViewCompat.setTransitionName(mAvatar, ELEMENT_POST_STATUS);
    }

    @Override
    public void onViewReady() {
// Capturing the callbacks for bottom sheet
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        LogUtils.i("Bottom Sheet Behaviour", "STATE_COLLAPSED");
                        break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        LogUtils.i("Bottom Sheet Behaviour", "STATE_DRAGGING");
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        LogUtils.i("Bottom Sheet Behaviour", "STATE_EXPANDED");
                        break;
                    case BottomSheetBehavior.STATE_HIDDEN:
                        LogUtils.i("Bottom Sheet Behaviour", "STATE_HIDDEN");
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        LogUtils.i("Bottom Sheet Behaviour", "STATE_SETTLING");
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, final float slideOffset) {
                if (!hasShownKeyboard) {
                    if (slideOffset >= -1 && slideOffset <= 0) runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            float newSlideOffset = slideOffset;
                            newSlideOffset -= -1;
                            int height = Math.round(bottomSheetBehavior.getPeekHeight() * newSlideOffset);

                            boolean hasUpdate = true;
                            if (newSlideOffset == 0.0f || newSlideOffset == 1.0f)
                                hasUpdate = heightBottomView != height;
                            if (hasUpdate) {
                                if (!checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                                    //Request read external storage permission
                                    requestAccessPermission(REQUEST_READ_EXTERNAL_FROM_DISPLAY_MEDIA_MODE, Manifest.permission.READ_EXTERNAL_STORAGE);
                                } else {
                                    ViewGroup.LayoutParams params = bottomView.getLayoutParams();
                                    params.height = Math.round(height);
                                    bottomView.setLayoutParams(params);
                                    heightBottomView = height;
                                }
                            }
                        }
                    });
                }
            }
        });

        fillTaggedInfo(mTaggedFriend);

        getPresenter().getUploadSetting(new UploadSettingRequest(UserPreferences.getInstance().getToken()));
        //Initialize media file info were picked to post status
        initUploadPickedInfo();
        //Bind user info to view
        bindUserInfo();
        //Set privacy content to post status
        setPrivacyContent();

        mKeyboardHeightProvider.start();
    }

    @Override
    public void onResume(View viewRoot) {
        super.onResume(viewRoot);
        if (mKeyboardHeightProvider != null)
            mKeyboardHeightProvider.setKeyboardHeightObserver(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        SystemUtils.hideInputMethod(mYourMind);
        if (mKeyboardHeightProvider != null)
            mKeyboardHeightProvider.setKeyboardHeightObserver(null);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mMediaFilePresenter != null)
            mMediaFilePresenter.destroy();
        if (mKeyboardHeightProvider != null)
            mKeyboardHeightProvider.close();
        if (mTaggedFriend != null)
            mTaggedFriend = null;
        if (bottomSheetBehavior != null)
            bottomSheetBehavior = null;
        if (mUploadFileValidator != null)
            mUploadFileValidator = null;
        if (mUploadSetting != null)
            mUploadSetting = null;
        if (mUploadPickedInfo != null) {
            mUploadPickedInfo.clear();
            mUploadPickedInfo = null;
        }
        if (disposable != null) {
            disposable.dispose();
            disposable = null;
        }
    }

    @Override
    public void onBackPressed() {
        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
            hasShownKeyboard = false;
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        } else {
            super.onBackPressed();
        }
    }

    @OnClick({R.id.layout_post_status_header_bottom_choose_file})
    public void onClick(View v) {
        // Collapsing the bottom sheet
        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            SystemUtils.showInputMethod(mYourMind);
            ((ImageView) v).setImageResource(R.drawable.ic_update_status_video);
        } else {
            ((ImageView) v).setImageResource(R.drawable.ic_keyboard_gray_24dp);
            if (isUpdateGallery) {
                mMediaFilePresenter.onExecuteGetMediaFileReady(MediaFileRepository.MEDIA_LOADER_ID_MODE, orderByLimit, orderByOffset, getContentResolver());
                isUpdateGallery = false;
            }
            SystemUtils.hideInputMethod(mYourMind);
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    }

    @OnClick(R.id.layout_post_status_header_bottom_take_option)
    void showTakeOption() {
        //Hide soft keyboard if need
        KeyboardUtils.hideSoftKeyboard(this);
        if (!checkPermission(Manifest.permission.CAMERA)) {
            //Request accept camera, record video  and write external storage permission
            requestAccessPermission(REQUEST_PERMISSION_CAMERA,
                    Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE);
            return;
        }
        showTakeMediaFileOption();
    }

    /**
     * Show when click to show list friend for add tag from tag friend Button
     */
    @OnClick(R.id.layout_post_status_header_bottom_tag_friends)
    void chooseFriend() {
        startTagFriendActivity();
    }

    /**
     * Show when click to tagged friend TextView show list friend for add tag
     */
    @OnClick(R.id.layout_post_status_tag)
    void chooseFriendFromTaggedTextView() {
        if (mTaggedFriend == null || mTaggedFriend.isEmpty()) {
            return;
        }
        startTagFriendActivity();
    }

    private void startTagFriendActivity() {
        //Hide soft keyboard if need
        KeyboardUtils.hideSoftKeyboard(this);

        //Start Tag Friend Activity
        TagFriendActivity.startActivityForResult(this, TAG_FRIEND_LIST_RESULT, mTaggedFriend, REQUEST_ADD_TAG_FRIEND);
    }

    @OnClick(R.id.layout_post_status_private)
    void selectPrivacy() {
        //Hide soft keyboard if need
        KeyboardUtils.hideSoftKeyboard(this);

        //Start choose privacy Activity
        SharePrivacyActivity.startActivityForResult(this, TAG_FRIEND_PRIVACY_RESULT, privacy, REQUEST_POST_STATUS_PRIVACY);
    }

    @Override
    public void onKeyboardHeightChanged(final int height, int orientation) {
        if (height > 100) {
            if (heightBottomView != height)
                heightBottomView = height;

            hasShownKeyboard = true;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ViewGroup.LayoutParams params = bottomView.getLayoutParams();
                    params.height = heightBottomView;
                    bottomView.setLayoutParams(params);
                }
            });

            if (!isSetPeekHeight) {
                isSetPeekHeight = true;
                bottomSheetBehavior.setPeekHeight(heightBottomView);
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);


        } else {
            hasShownKeyboard = false;
            if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_HIDDEN)
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ViewGroup.LayoutParams params = bottomView.getLayoutParams();
                        params.height = 0;
                        bottomView.setLayoutParams(params);
                    }
                });
        }
    }

    @Override
    public void fileNotExists() {
        Toast.makeText(this, getString(R.string.file_not_found), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void overFileSizeAllowed(int type) {

        if (type == MediaFileBean.AUDIO) {
            Toast.makeText(this, String.format(getString(R.string.upload_setting_maximum_audio_size), mUploadSetting.maxFileSize / 1024), Toast.LENGTH_SHORT).show();
            return;
        }
        if (type == MediaFileBean.IMAGE) {
            Toast.makeText(this, String.format(getString(R.string.upload_setting_maximum_image_size), mUploadSetting.maxFileSize / 1024), Toast.LENGTH_SHORT).show();
            return;
        }
        if (type == MediaFileBean.VIDEO) {
            Toast.makeText(this, String.format(getString(R.string.upload_setting_maximum_video_size), mUploadSetting.maxFileSize / 1024), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void overTotalSizeAllowed() {
        Toast.makeText(this, String.format(getString(R.string.upload_setting_maximum_total_size), mUploadSetting.totalFileSize / 1024), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void overTotalFileAllowed(int type) {

        if (type == MediaFileBean.AUDIO) {
            Toast.makeText(this, String.format(getString(R.string.upload_setting_maximum_audio_total_file), mUploadSetting.maxAudioNumber), Toast.LENGTH_SHORT).show();
            return;
        }
        if (type == MediaFileBean.IMAGE) {
            Toast.makeText(this, String.format(getString(R.string.upload_setting_maximum_image_total_file), mUploadSetting.maxImageNumber), Toast.LENGTH_SHORT).show();
            return;
        }
        if (type == MediaFileBean.VIDEO) {
            Toast.makeText(this, String.format(getString(R.string.upload_setting_maximum_video_total_file), mUploadSetting.maxVideoNumber), Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, String.format(getString(R.string.upload_setting_maximum_total_file), mUploadSetting.totalFileUpload), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void canUpload(MediaFileBean mediaFile, int fileSizeInKB, int position) {
        //Update UI to views
        updateUI(mediaFile, position);
        //Update info of upload media file were picked
        mediaFile.fileSizeInKB = fileSizeInKB;
        updateUploadPickedInfo(mediaFile);
    }

    @Override
    public void onItemClick(BaseBean data, View view, int position) {
        validate(data, view, position);
    }

    @Override
    public void onCheckBoxClick(BaseBean data, View view, int position) {
        validate(data, view, position);
    }

    @Override
    public void onUnSelectFile(BaseBean data, View view, int position) {
        MediaFileBean itemEntity = (MediaFileBean) data;
        updateUI(itemEntity, position);
        //Update info of upload media file were picked
        itemEntity.fileSizeInKB = itemEntity.fileSizeInKB * -1;//mark to decrease file size in mUploadPickedInfo Map
        updateUploadPickedInfo(itemEntity);
    }

    private void displayMediaPicked(boolean isShow) {
        if (isShow) mRecyclerViewMediaPicked.setVisibility(View.VISIBLE);
        else mRecyclerViewMediaPicked.setVisibility(View.GONE);
    }

    /**
     * Request permission access to read external storage use Observer
     */
    @SuppressLint("CheckResult")
    public void requestAccessPermission(final int requestCode, final String... permissions) {
        getRxPermissions().requestEach(permissions)
                .subscribe(new Consumer<Permission>() {
                    @Override
                    public void accept(Permission permission) {
                        LogUtils.i(TAG, "Permission result " + permission);
                        if (permission != null && permission.granted) {

                            switch (permission.name) {
                                case Manifest.permission.CAMERA:
                                    if (requestCode == REQUEST_PERMISSION_CAMERA) {
                                        showTakeMediaFileOption();
                                    }
                                    break;
                                case Manifest.permission.READ_EXTERNAL_STORAGE:
                                    if (requestCode == REQUEST_READ_EXTERNAL_FROM_DISPLAY_MEDIA_MODE) {
                                        fillTaggedInfo(mTaggedFriend);
                                        mMediaFilePresenter.onExecuteGetMediaFileReady(MediaFileRepository.MEDIA_LOADER_ID_MODE, orderByLimit, orderByOffset, getContentResolver());
                                        //Show keyboard soft
                                        KeyboardUtils.showDelayKeyboard(mYourMind, 0);
                                    } else if (requestCode == REQUEST_READ_EXTERNAL_WHEN_CLICK_SUBMIT_POST_MODE) {
                                        //Execution upload file to server via URI
                                        postStatusWithUploadMultiplePart();
                                    }

                                    break;
                                default:
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
                        LogUtils.e(TAG, "onError" + t.getMessage());
                    }
                }, new Action() {
                    @Override
                    public void run() {
                        LogUtils.i(TAG, "OnComplete");
                    }
                });
    }

    public void validate(BaseBean data, View view, int position) {
        MediaFileBean itemEntity = (MediaFileBean) data;
        if (itemEntity.isCheck) {//was checked
            // set to unchecked
            updateUI(itemEntity, position);
            //Update info of upload media file were picked
            updateUploadPickedInfo(itemEntity);
        } else {
            mUploadFileValidator.onValidate(this, itemEntity, position, mUploadSetting, mUploadPickedInfo);
        }
    }

    private void showTakeMediaFileOption() {

        PopupMenu popup = new PopupMenu(this, mTakeMediaOption);

        popup.getMenuInflater().inflate(R.menu.menu_take_media_option, popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.take_photo_option:
                        new Camera.Builder()
                                .isVideoMode(false)
                                .flashMode(CameraView.FLASH_AUTO)
                                .build()
                                .start(PostStatusActivity.this);
                        return true;
                    case R.id.take_video_option:
                        new Camera.Builder()
                                .facing(CameraView.FACING_FRONT)
                                .isVideoMode(true)
                                .flashMode(CameraView.FLASH_ON)
                                .build()
                                .start(PostStatusActivity.this);
                        return true;
                    default:
                        return false;
                }
            }
        });
        popup.show();
    }

    private void postStatusWithUploadMultiplePart() {
        // Updated by Robert on 2017 Nov 09 - Allow post status with ic_comment is empty
        if (isValidParams()) {
            if (mMediaPickedAdapter.getData().size() > 0 || !mYourMind.getText().toString().trim().isEmpty()) {
                Intent intents = new Intent();
                intents.putParcelableArrayListExtra(PostStatusService.INPUT_TAG_FRIENDS, mTaggedFriend);
                intents.putParcelableArrayListExtra(PostStatusService.INPUT_FILE, (ArrayList<MediaFileBean>) mMediaPickedAdapter.getData());
                intents.putExtra(PostStatusService.INPUT_YOUR_MIND, Utils.nullToEmpty(mYourMind.getText()));
                intents.putExtra(PostStatusService.INPUT_PRIVACY, privacy);
                intents.putExtra(PostStatusService.INPUT_STREAM, "streamUrl");
                intents.putExtra(PostStatusService.INPUT_TOKEN, UserPreferences.getInstance().getToken());

                setResult(RESULT_OK, intents);
                finish();
            }
        }
    }

    /**
     * The handle check/unchecked into/outside of MediaPicked list necessary to upload
     *
     * @param mediaFile The MediaFile need to handle check/unchecked into/outside of MediaPicked list
     * @param position  the position of MediaItem has been checked/unchecked on Gallery
     */
    private void updateUI(MediaFileBean mediaFile, int position) {

        LogUtils.e(TAG, "--->Picked list before add more "
                + "\n|item.size                    = " + mMediaPickedAdapter.getData().size()
                + "\n|mediaFile.isCheck         = " + mediaFile.isCheck
                + "\n|mediaFile.mediaType       = " + mediaFile.mediaType
                + "\n|position                  = " + position
                + "\n|mediaFile.pickedPosition  = " + mediaFile.pickedPosition);

        if (mediaFile.isCheck) {//was checked
            if (mMediaPickedAdapter.getData().size() == 1 && mRecyclerViewMediaPicked.getVisibility() == View.VISIBLE)
                displayMediaPicked(false);
            // set to unchecked
            mediaFile.isCheck = false;
            mMediaPickedAdapter.removeItem(mediaFile);
            mMediaPickedAdapter.notifyDataSetChanged();
            mRecyclerViewMediaPicked.smoothScrollToPosition(mMediaPickedAdapter.getItemCount());

            mGalleryAdapter.reorderUnchecked(mediaFile.pickedPosition, mediaFile);
        } else {// is not check
            if (mMediaPickedAdapter.getData().size() == 0 && mRecyclerViewMediaPicked.getVisibility() == View.GONE)
                displayMediaPicked(true);
            //Set to checked
            mediaFile.isCheck = true;
            //Add mediaItem was checked int mMediaPickedList
            mMediaPickedAdapter.addData(mediaFile);
            mMediaPickedAdapter.notifyDataSetChanged();
            mRecyclerViewMediaPicked.smoothScrollToPosition(mMediaPickedAdapter.getItemCount());

            mGalleryAdapter.increaseOrderOfItem(position, mMediaPickedAdapter.getData().size());
            mGalleryAdapter.notifyItemChanged(mediaFile.pickedPosition);
        }
    }

    private boolean isValidParams() {
        long maxCommentLengthAllowed = (mUploadSetting != null ? mUploadSetting.maxCommentLengthAllowed : mUploadPickedInfo.get(POST_COMMENT_MAX_CHARACTER));

        if (!Utils.isEmptyOrNull(mYourMind.getText().toString()) && Utils.nullToEmpty(mYourMind.getText()).length() > maxCommentLengthAllowed) {
            mYourMind.setFocusable(true);
            mYourMind.requestFocus();
            mYourMind.setError(String.format(getString(R.string.validate_post_status_comment_limit), maxCommentLengthAllowed));
            return false;
        }

        return true;
    }

    /**
     * Initialize media file info were picked to post status
     */
    private void initUploadPickedInfo() {
        mUploadPickedInfo.put(AUDIO_PICKED_TOTAL_FILE, 0L);
        mUploadPickedInfo.put(IMAGE_PICKED_TOTAL_FILE, 0L);
        mUploadPickedInfo.put(VIDEO_PICKED_TOTAL_FILE, 0L);
        mUploadPickedInfo.put(MEDIA_PICKED_TOTAL_SIZE, 0L);
        mUploadPickedInfo.put(MEDIA_PICKED_TOTAL_FILE, 0L);
        mUploadPickedInfo.put(POST_COMMENT_MAX_CHARACTER, 60000L);
    }

    private void setPrivacyContent() {
        if (privacy == Constants.PRIVACY_PUBLIC) {
            mPrivacyIcon.setImageResource(R.drawable.ic_public);
            mTxtPrivacy.setText(getString(R.string.public_privacy));
        } else if (privacy == Constants.PRIVACY_FRIENDS) {
            mPrivacyIcon.setImageResource(R.drawable.ic_status_friends);
            mTxtPrivacy.setText(getString(R.string.friend_privacy));
        } else if (privacy == Constants.PRIVACY_PRIVATE) {
            mPrivacyIcon.setImageResource(R.drawable.ic_privacy_only_me);
            mTxtPrivacy.setText(getString(R.string.onlyme_privacy));
        }
    }

    private void bindUserInfo() {
        //Lazy load avatar bind to view
        ImagesUtils.loadRoundedAvatar(UserPreferences.getInstance().getAva(), UserPreferences.getInstance().getGender(), mAvatar);

        //Fill username to view
        if (!Utils.isEmptyOrNull(UserPreferences.getInstance().getUserName())) {
            mTxtUsername.setText(UserPreferences.getInstance().getUserName());
        }
    }

    /**
     * Fill all tagged friend into view
     *
     * @param mTaggedFriend the list tagged friend to share new post
     */
    private void fillTaggedInfo(List<ListTagFriendsBean> mTaggedFriend) {

        if (mTaggedFriend == null || mTaggedFriend.isEmpty()) {
            txtTag.setText(getString(R.string.tag_friend_blank));
        } else {
            String firstTaggedFrd = mTaggedFriend.get(0).userName;
            String tagFriendStringFormat = getString(R.string.tag_friend_txt);
            if (mTaggedFriend.size() > 1) {
                tagFriendStringFormat = getString(R.string.tag_friends_txt);
            }
            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.N) {
                txtTag.setText(Html.fromHtml(String.format(tagFriendStringFormat, firstTaggedFrd, (mTaggedFriend.size() == 1 ? "" : (mTaggedFriend.size() - 1) + ""))));
            } else {
                txtTag.setText(Html.fromHtml(String.format(tagFriendStringFormat, firstTaggedFrd, (mTaggedFriend.size() == 1 ? "" : (mTaggedFriend.size() - 1) + "")), Html.FROM_HTML_OPTION_USE_CSS_COLORS));
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) return;
        switch (requestCode) {
            case Camera.REQUEST_CODE_CAMERA:
            case Gallery.REQUEST_CODE_GALLERY:
                Parcelable[] videos = data.getParcelableArrayExtra(MediaPickerBaseActivity.RESULT_KEY);
                for (Parcelable parcelable : videos) {
                    MediaFile takeMediaFile = (MediaFile) parcelable;
                    File file = new File(takeMediaFile.getPath());
                    if (file.exists()) {//If exists file then continue handle

                        MediaFileBean mediaFile;
                        // check file is photo
                        if (MediaSanUtils.isPhoto(file.getAbsolutePath())) {
                            mediaFile = new MediaFileBean(takeMediaFile.getTime(), file.getPath(), MediaFileBean.IMAGE);
                        } else { // file video
                            Uri uri = Uri.fromFile(file);
                            //Get Duration from file path
                            String time = TimeUtils.convertMillisToTimeString(MediaFileUtils.getDuration(context, uri));
                            mediaFile = new MediaFileBean(takeMediaFile.getTime(), file.getPath(), time, MediaFileBean.VIDEO);
                        }

                        //Add mediaItem was  taken from camera
                        mGalleryAdapter.addData(0, mediaFile);

                        mUploadFileValidator.onValidate(this, mediaFile, 0, mUploadSetting, mUploadPickedInfo);
                    }
                }
                break;
            case REQUEST_ADD_TAG_FRIEND:
                //Utils.dumpIntent(data);
                //Get tagged friend list from TagFriendActivity
                mTaggedFriend = data.getParcelableArrayListExtra(TAG_FRIEND_LIST_RESULT);
                fillTaggedInfo(mTaggedFriend);
                break;
            case REQUEST_POST_STATUS_PRIVACY:
                //Get the privacy option from SharePrivacyActivity
                privacy = data.getIntExtra(TAG_FRIEND_PRIVACY_RESULT, 0);
                //Set privacy content to post status
                setPrivacyContent();
                break;
            default:
                break;
        }

    }

    /**
     * Update info of upload media file were picked
     *
     * @param mediaFile
     * @author Created by Robert on 2017 Nov 01
     */
    private void updateUploadPickedInfo(MediaFileBean mediaFile) {

        if (mUploadPickedInfo == null) mUploadPickedInfo = new HashMap<>();
        int flag = (mediaFile.isCheck ? 1 : -1);//is 1 or -1 mark to negative or positive sign

        mUploadPickedInfo.put(MEDIA_PICKED_TOTAL_FILE, mUploadPickedInfo.get(MEDIA_PICKED_TOTAL_FILE) + flag);
        mUploadPickedInfo.put(MEDIA_PICKED_TOTAL_SIZE, mUploadPickedInfo.get(MEDIA_PICKED_TOTAL_SIZE) + flag * mediaFile.fileSizeInKB);

        if (mediaFile.mediaType == MediaFileBean.AUDIO) {
            mUploadPickedInfo.put(AUDIO_PICKED_TOTAL_FILE, mUploadPickedInfo.get(AUDIO_PICKED_TOTAL_FILE) + flag);
            return;
        }
        if (mediaFile.mediaType == MediaFileBean.IMAGE) {
            mUploadPickedInfo.put(IMAGE_PICKED_TOTAL_FILE, mUploadPickedInfo.get(IMAGE_PICKED_TOTAL_FILE) + flag);
            return;
        }
        if (mediaFile.mediaType == MediaFileBean.VIDEO) {
            mUploadPickedInfo.put(VIDEO_PICKED_TOTAL_FILE, mUploadPickedInfo.get(VIDEO_PICKED_TOTAL_FILE) + flag);
        }
    }

    @Override
    public void onGetUploadSettingResponse(UploadSettingResponse response) {
        UploadSettingPreference.getInstance().setUploadSetting(response.setting);
        mUploadSetting = UploadSettingPreference.getInstance().getUploadSetting();
    }

    @Override
    public void onCursorLoaded(final Cursor cursor, int mediaType) {
        Observable<MediaFileBean> observable = new Observable<MediaFileBean>() {
            @Override
            protected void subscribeActual(Observer<? super MediaFileBean> observer) {
                int i = 0;
                if (cursor != null) {

                    while (cursor.moveToNext()) {

                        LogUtils.i(TAG, "cursor.moveToNext ---> " + i++);

                        String mediaPath = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA));
                        int mediaType = cursor.getInt(cursor.getColumnIndex(MediaStore.Files.FileColumns.MEDIA_TYPE));
                        long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Files.FileColumns._ID));

                        if (mediaType == MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO) {
                            long duration = MediaFileUtils.getDuration(mediaPath);
                            if (duration > 0) {
                                String time = TimeUtils.convertMillisToTimeString(duration);
                                observer.onNext(new MediaFileBean(id, mediaPath, time, MediaFileBean.VIDEO));
                            }
                        } else if (mediaType == MediaStore.Files.FileColumns.MEDIA_TYPE_AUDIO) {
                            long duration = MediaFileUtils.getDuration(mediaPath);
                            if (duration > 0) {
                                String time = TimeUtils.convertMillisToTimeString(duration);
                                observer.onNext(new MediaFileBean(id, mediaPath, time, MediaFileBean.AUDIO));
                            }
                        } else if (mediaType == MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE) {
                            observer.onNext(new MediaFileBean(id, mediaPath, MediaFileBean.IMAGE));
                        }

                    }

                    cursor.close();
                } else {
                    observer.onError(new Exception("cursor is null.!"));
                }

                observer.onComplete();
            }
        };

        disposable = observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<MediaFileBean>() {
                    @Override
                    public void accept(MediaFileBean mediaFileBeans) throws Exception {
                        mGalleryAdapter.addData(mediaFileBeans);
                    }
                });
    }

    @Override
    public void onCursorLoadFailed(Throwable throwable, int mediaType) {
        mGalleryAdapter.setEmptyView(R.layout.layout_empty);
    }

    @Override
    public void onToolbarButtonLeftClick(View view) {
        finish();
    }

    @Override
    public void onToolbarButtonRightClick(View view) {
        ArrayList<String> listDetectsBannedWord = DirtyWordUtils.listDetectsBannedWord(getBaseContext(), mYourMind.getText().toString());
        if (listDetectsBannedWord.size() > 0) {
            showConfirmDialog(getString(R.string.banned_word_error, DirtyWordUtils.convertArraySetToString(listDetectsBannedWord)), null, false);
            return;
        }
        postStatusWithUploadMultiplePart();
    }
}
