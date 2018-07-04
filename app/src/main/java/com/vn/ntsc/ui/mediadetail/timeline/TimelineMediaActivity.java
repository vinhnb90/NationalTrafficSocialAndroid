package com.vn.ntsc.ui.mediadetail.timeline;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.vn.ntsc.R;
import com.vn.ntsc.repository.TypeView;
import com.vn.ntsc.repository.model.media.MediaEntity;
import com.vn.ntsc.repository.model.report.ReportRequest;
import com.vn.ntsc.repository.model.timeline.DeleteBuzzRequest;
import com.vn.ntsc.repository.model.timeline.DeleteBuzzResponse;
import com.vn.ntsc.repository.model.timeline.LikeBuzzRequest;
import com.vn.ntsc.repository.model.timeline.datas.BuzzBean;
import com.vn.ntsc.repository.model.timeline.datas.sub.LikeBean;
import com.vn.ntsc.repository.model.timeline.datas.sub.ListBuzzChild;
import com.vn.ntsc.repository.model.timeline.datas.sub.ListTagFriendsBean;
import com.vn.ntsc.repository.preferece.UserPreferences;
import com.vn.ntsc.ui.comments.CommentActivity;
import com.vn.ntsc.ui.mediadetail.base.AudioFragment;
import com.vn.ntsc.ui.mediadetail.base.BaseMediaActivity;
import com.vn.ntsc.ui.mediadetail.base.DetailMediaPresenter;
import com.vn.ntsc.ui.mediadetail.base.IDetailMediaInteractor;
import com.vn.ntsc.ui.mediadetail.base.MediaAdapter;
import com.vn.ntsc.ui.mediadetail.base.VideoFragment;
import com.vn.ntsc.ui.mediadetail.base.ViewPagerMedia;
import com.vn.ntsc.ui.mediadetail.base.video.PlayerController;
import com.vn.ntsc.ui.tagfriends.TagFriendActivity;
import com.vn.ntsc.utils.Constants;
import com.vn.ntsc.utils.LogUtils;
import com.vn.ntsc.widget.eventbus.RxEventBus;
import com.vn.ntsc.widget.eventbus.SubjectCode;
import com.vn.ntsc.widget.permissions.Permission;
import com.vn.ntsc.widget.toolbar.ToolbarTitleCenter;
import com.vn.ntsc.widget.views.dialog.ProgressAlertDialog;
import com.vn.ntsc.widget.views.images.mediadetail.image.ImageViewTouch.IteractorGestureListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindArray;
import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

import static com.vn.ntsc.repository.ActivityResultRequestCode.REQUEST_ADD_TAG_FRIEND_FOR_SHARE;

/**
 * Created by ThoNh on 11/22/2017.
 */

public class TimelineMediaActivity extends BaseMediaActivity<MediaDetailPresenter> implements MediaDetailContract.View, IDetailMediaInteractor.ActivityView, IteractorGestureListener,
        PlayerController.VisibilityListener, ViewPagerMedia.IDoTaskWhenTouch {

    /*-----------------------------------var-----------------------------------*/
    private static final String TAG = TimelineMediaActivity.class.getSimpleName();

    private static final String ELEMENT_MEDIA = "media_element";
    public static final String EXTRA_BUZZ_DATA = "EXTRA_BUZZ_DATA";
    public static final String EXTRA_BUZZ_CHILD_DATA = "EXTRA_BUZZ_CHILD_DATA";
    public static final String EXTRA_ITEM_FIRST_SHOW = "EXTRA_ITEM_FIRST_SHOW";
    public static final String EXTRA_TAG_FROM_ACTIVITY = "EXTRA_TAG_FROM_ACTIVITY";
    private static final int REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE = 0x00;
    /**
     * for receive data when pick friends to ic_share
     *
     * @see #onActivityResult(int, int, Intent)
     */
    private static final String RESULT_KEY_TAG_FRIEND_FOR_SHARE = "result_tag";

    /**
     * State of menu ic_share in toolbar
     * mIsMenuVisible = true --> icon ic_share is shown
     * mIsMenuVisible = false --> icon ic_share is not shown
     * <p>
     * Call invalidateOptionsMenu() to confirm the change state
     */
    private boolean mIsMenuVisible;

    private ProgressAlertDialog mAlertDialog;
    private BuzzBean mOriginData;
    private List<ListBuzzChild> mBuzzs;
    private String mUserIdPost;
    private int mFirstShowIndex;
    private String mFromActivity;
    private IDetailMediaInteractor.Presenter mPresenter;

    /**
     * store selected friends to ic_share
     *
     * @see #onOptionsItemSelected(MenuItem)
     */
    private ArrayList<ListTagFriendsBean> selectedFriends = new ArrayList<>();

    //butter knife
    @BindView(R.id.activity_image_detail_container)
    RelativeLayout mContainer;

    @BindView(R.id.toolbar)
    ToolbarTitleCenter mToolbar;

    @BindView(R.id.activity_image_detail_image_pager)
    ViewPagerMedia viewPager;

    @BindView(R.id.activity_image_detail_txt_number_like)
    TextView mTxtNumberLike;

    @BindView(R.id.activity_image_detail_txt_number_comment)
    TextView mTxtNumberComment;

    @BindView(R.id.activity_image_detail_txt_delete)
    TextView mTxtDelete;

    @BindView(R.id.activity_image_detail_txt_report)
    TextView mTxtReport;

    @BindView(R.id.activity_image_detail_txt_save)
    TextView mTxtSave;

    @BindView(R.id.activity_image_detail_panel_bottom)
    RelativeLayout bottomPanel;

    @BindView(R.id.activity_image_detail_scrim_view)
    View scrimView;

    @BindArray(R.array.ReportArray)
    String[] reportArray;

    /*-----------------------------------instance-----------------------------------*/

    /**
     * Come from TimelineFragment (Send to this class BuzzBean)
     */
    public static void launch(AppCompatActivity activity, View v, BuzzBean buzzBean,
                              int positionIndexItem) {
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, v, ELEMENT_MEDIA);
        final Intent intent = new Intent(activity, TimelineMediaActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(TimelineMediaActivity.EXTRA_BUZZ_DATA, buzzBean);
        intent.putExtra(TimelineMediaActivity.EXTRA_ITEM_FIRST_SHOW, positionIndexItem);
        activity.startActivity(intent, options.toBundle());

    }

    public static void launch(AppCompatActivity activity, View v, BuzzBean buzzBean, int positionIndexItem, String fromActivity) {
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, v, ELEMENT_MEDIA);
        final Intent intent = new Intent(activity, TimelineMediaActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(TimelineMediaActivity.EXTRA_BUZZ_DATA, buzzBean);
        intent.putExtra(TimelineMediaActivity.EXTRA_TAG_FROM_ACTIVITY, fromActivity);
        intent.putExtra(TimelineMediaActivity.EXTRA_ITEM_FIRST_SHOW, positionIndexItem);
        activity.startActivity(intent, options.toBundle());

    }

    /**
     * Come from TimelineHeaderProfileHolder (send to this class List<ListBuzzChild>)
     */
    public static void launch(AppCompatActivity activity, View v, List<ListBuzzChild> mListChild,
                              int positionIndexItem) {
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, v, ELEMENT_MEDIA);
        Intent intent = new Intent(activity, TimelineMediaActivity.class);
        intent.putParcelableArrayListExtra(TimelineMediaActivity.EXTRA_BUZZ_CHILD_DATA, (ArrayList<? extends Parcelable>) mListChild);
        intent.putExtra(TimelineMediaActivity.EXTRA_ITEM_FIRST_SHOW, positionIndexItem);
        activity.startActivity(intent, options.toBundle());
    }

    /*-----------------------------------lifecycle-----------------------------------*/

    @Override
    public int getLayoutId() {
        return R.layout.activity_image_detail;
    }

    @Override
    public void onCreateView(View rootView) {
        //init
        mAlertDialog = new ProgressAlertDialog(this);
        getMediaComponent().inject(this);

        //get data
        getBundleData();

        //presenter
        new DetailMediaPresenter.Builder(null, this).build();

        //setup view pager
        List<MediaEntity> mediaEntityList = convertTimelineDataToMediaData();
        ViewPagerBuilder builder = new ViewPagerBuilder(mediaEntityList, viewPager, mFirstShowIndex);
        builder
                .setViewContainer(mContainer)
                .setiDoTaskWhenTouch(this)
                .build();

        //save data
        if (mPresenter != null)
            mPresenter.saveListMediaPlaying(mediaEntityList);

        //Sets the name of the View to be used to identify Views in Transitions
        ViewCompat.setTransitionName(scrimView, ELEMENT_MEDIA);

        //set text bottom
        setupBottomPanel();
    }

    @Override
    public void onViewReady() {
        super.onViewReady();

        //action bar
        setSupportActionBar(mToolbar);
        mToolbar.setActionbar(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        //update text bottom
        updateBottomView(getCurrentBuzz());

        // update menu toolbar
        invalidateMenu();

        //subscribe bus
        setupEventBus();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInf = getMenuInflater();
        menuInf.inflate(R.menu.menu_media_share, menu);
        MenuItem item = menu.findItem(R.id.share);
        item.setVisible(mIsMenuVisible);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_ADD_TAG_FRIEND_FOR_SHARE) {
            switch (requestCode) {
                case REQUEST_ADD_TAG_FRIEND_FOR_SHARE:
                    handleShare(data);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.share) {
            TagFriendActivity.startActivityForResult(this, RESULT_KEY_TAG_FRIEND_FOR_SHARE, selectedFriends, REQUEST_ADD_TAG_FRIEND_FOR_SHARE);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Data {ic_like, fav, ic_comment} maybe changed -> should return value for TimelineFragment
        if (mOriginData != null) {

            if (mBuzzs.size() == 1 && mBuzzs.get(0) != null) {
                String buzzId = mBuzzs.get(0).buzzId;
                if (mOriginData.buzzId.equals(buzzId)) {
                    mOriginData.commentNumber = mBuzzs.get(0).commentNumber;
                    mOriginData.like = mBuzzs.get(0).like;
                }
            }
            RxEventBus.publish(SubjectCode.SUBJECT_UPDATE_TIMELINE, mOriginData);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }

    /*-----------------------------------override-----------------------------------*/

    @Override
    public void setPresenter(@NonNull IDetailMediaInteractor.Presenter presenter) {
        mPresenter = presenter;
    }

    //PlayerController.VisibilityListener
    @Override
    public void onFullScreen(boolean needFull) {
        if (needFull) {
            bottomPanel.setVisibility(View.INVISIBLE);
            mToolbar.setVisibility(View.INVISIBLE);
        } else {
            bottomPanel.setVisibility(View.VISIBLE);
            mToolbar.setVisibility(View.VISIBLE);
        }
    }

    //ImageViewTouch.IteractorGestureListener
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

    @OnClick({R.id.activity_image_detail_txt_number_like, R.id.activity_image_detail_txt_number_comment, R.id.activity_image_detail_txt_delete, R.id.activity_image_detail_txt_save, R.id.activity_image_detail_txt_report})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.activity_image_detail_txt_number_like:
                if (getCurrentBuzz().like != null) {
                    String token = UserPreferences.getInstance().getToken();
                    String buzzId = getCurrentBuzz().buzzId;
                    int likeType = getCurrentBuzz().like.isLike == Constants.BUZZ_LIKE_TYPE_LIKE ? Constants.BUZZ_LIKE_TYPE_UNKNOW : Constants.BUZZ_LIKE_TYPE_LIKE;
                    LikeBuzzRequest request = new LikeBuzzRequest(token, buzzId, likeType);
                    getPresenter().likeMedia(request);
                } else {
                    Toast.makeText(this, R.string.common_error, Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.activity_image_detail_txt_number_comment:
                if (mFromActivity != null && mFromActivity.equals(CommentActivity.class.getSimpleName())) {
                    finish();
                    return;
                }
                CommentActivity.launch(this, view, getCurrentBuzz().buzzId, getCurrentBuzz().userId, CommentActivity.EXTRA_FROM_TIMELINE_MEDIA_DETAIL);
                break;

            case R.id.activity_image_detail_txt_delete:
                showDialogDelete();
                break;

            case R.id.activity_image_detail_txt_report:
                showDialogReport();
                break;

            case R.id.activity_image_detail_txt_save:
                requestAccessPermission(REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                break;
        }
    }

    //SERVER RESPONSE
    @Override
    public void likeMediaSuccess() {
        int like = getCurrentBuzz().like.isLike == Constants.BUZZ_LIKE_TYPE_LIKE ? Constants.BUZZ_LIKE_TYPE_UNLIKE : Constants.BUZZ_LIKE_TYPE_LIKE;
        getCurrentBuzz().like.isLike = like;
        if (like == Constants.BUZZ_LIKE_TYPE_LIKE) {            // update likeNumber
            getCurrentBuzz().like.likeNumber++;
        } else {
            getCurrentBuzz().like.likeNumber--;
        }

        updateBottomView(getCurrentBuzz());
        if (mOriginData != null) {
            // Update to TimeLineFragment, TimeLineFragment sent to This Activity : BuzzBean
            RxEventBus.publish(SubjectCode.SUBJECT_UPDATE_TIMELINE, mOriginData);
        }

        // Update to TimeLineHeaderProfile,  TimeLineHeaderProfile sent to This Activity List<ListBuzzChild>
        RxEventBus.publish(SubjectCode.SUBJECT_UPDATE_LIKE_IN_HEADER_PROFILE, getCurrentBuzz());
    }

    @Override
    public void likeMediaFailure() {
        Toast.makeText(this, R.string.common_error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void reportSuccess() {
        String type = null;
        switch (getCurrentMedia().mType) {
            case TypeView.MediaDetailType.AUDIO_TYPE:
                type = getString(R.string.audio_type);
                break;
            case TypeView.MediaDetailType.VIDEO_TYPE:
                type = getString(R.string.video_type);
                break;
            case TypeView.MediaDetailType.IMAGE_TYPE:
                type = getString(R.string.image_type);
                break;
        }

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

    @Override
    public void reportFailure() {
        Toast.makeText(this, R.string.report_image_failure, Toast.LENGTH_SHORT).show();
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
        /*Remove item list*/
//        removeItem(getCurrentPositionMedia());          /*Remove item viewPager*/
        mBuzzs.remove(child);

        Toast.makeText(this, R.string.delete_image_success, Toast.LENGTH_SHORT).show();
        if (mOriginData != null) {
            mOriginData.childNumber--;
            RxEventBus.publish(SubjectCode.SUBJECT_UPDATE_TIMELINE, mOriginData);
        }

        // Update data in TimelineUserTabActivity.java
        if (mBuzzs != null) {
            RxEventBus.publish(SubjectCode.SUBJECT_UPDATE_DATA, mBuzzs);
        }
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
    public void shareMediaFailure() {
        Toast.makeText(this, R.string.share_media_error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void shareMediaSuccess() {
        Toast.makeText(this, R.string.share_media_success, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onComplete() {
        mAlertDialog.hide();
    }

    /*-----------------------------------func-----------------------------------*/

    /**
     * http://10.64.100.201/issues/11299
     *
     * @return {@link ListBuzzChild}
     */
    public ListBuzzChild getCurrentBuzz() {
        ListBuzzChild child = mBuzzs.get(getCurrentPositionMedia());

        switch (child.buzzType) {
            case TypeView.MediaDetailType.IMAGE_TYPE:
            case TypeView.MediaDetailType.VIDEO_TYPE:
            case TypeView.MediaDetailType.AUDIO_TYPE:
                break;
            case TypeView.MediaDetailType.STREAM_TYPE:
                break;
        }

        return child;
    }

    /**
     * Request permission access to write external storage use Download Image
     *
     * @param requestCode
     * @param permissions
     */
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

    private void handleShare(Intent data) {
        this.selectedFriends.clear();
        ArrayList<ListTagFriendsBean> selectedFriends = data.getParcelableArrayListExtra(RESULT_KEY_TAG_FRIEND_FOR_SHARE);
        if (selectedFriends != null) this.selectedFriends = selectedFriends;

        List<String> listUserId = new ArrayList<>();
        for (ListTagFriendsBean favoriteBean : this.selectedFriends) {
            listUserId.add(favoriteBean.userId);
        }

        String buzzId;
        buzzId = getCurrentBuzz().buzzId;
        getPresenter().shareMedia(UserPreferences.getInstance().getToken(), "", listUserId, buzzId);
    }

    private void updateBottomView(ListBuzzChild bean) {
        setupBottomPanel();
        int res = bean.like.isLike == Constants.BUZZ_LIKE_TYPE_LIKE ? R.drawable.ic_list_buzz_item_favorited : R.drawable.ic_unlike;
        mTxtNumberLike.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, res, 0);
        mTxtNumberLike.setText(String.valueOf(bean.like.likeNumber));
        mTxtNumberComment.setText(String.valueOf(bean.commentNumber));
    }

    private List<MediaEntity> convertTimelineDataToMediaData() {
        List<MediaEntity> mediaEntities = new ArrayList<>();
        for (int i = 0; i < mBuzzs.size(); i++) {
            ListBuzzChild child = mBuzzs.get(i);
            String url = child.buzzType.equals(TypeView.MediaDetailType.IMAGE_TYPE) ? child.buzzVal : child.url;
            mediaEntities.add(new MediaEntity(i, url, child.buzzType, child.thumbnailUrl));
        }
        return mediaEntities;
    }

    // Nếu ảnh của mình thì cho hiện option "Xóa"
    // Nếu ảnh là của người khác thì cho hiện option "Báo cáo"
    private void setupBottomPanel() {
        switch (getCurrentMedia().mType) {

            case TypeView.MediaDetailType.AUDIO_TYPE:
                mTxtDelete.setText(R.string.delete_audio);
                mTxtReport.setText(R.string.common_report);
                break;

            case TypeView.MediaDetailType.VIDEO_TYPE:
            case TypeView.MediaDetailType.STREAM_TYPE:
                mTxtDelete.setText(R.string.delete_video);
                mTxtReport.setText(R.string.common_report);
                break;

            case TypeView.MediaDetailType.IMAGE_TYPE:
                mTxtDelete.setText(R.string.delete_image);
                mTxtReport.setText(R.string.common_report);

                break;
        }

        String userId = UserPreferences.getInstance().getUserId();

        int idResDelete = R.string.common_remove;
        int idResSave = R.string.common_save;
        int idResReport = R.string.common_report;

        if (getCurrentMedia() != null) {
            switch (getCurrentMedia().mType) {
                case TypeView.MediaDetailType.AUDIO_TYPE:
                    idResDelete = R.string.delete_audio;
                    idResSave = R.string.save_audio;
                    idResReport = R.string.common_report;
                    break;
                case TypeView.MediaDetailType.VIDEO_TYPE:
                case TypeView.MediaDetailType.STREAM_TYPE:
                    idResDelete = R.string.delete_video;
                    idResSave = R.string.save_video;
                    idResReport = R.string.common_report;
                    break;
                case TypeView.MediaDetailType.IMAGE_TYPE:
                    idResDelete = R.string.delete_image;
                    idResSave = R.string.save_image;
                    idResReport = R.string.common_report;
                    break;
            }

            mTxtDelete.setText(idResDelete);
            mTxtSave.setText(idResSave);
            mTxtReport.setText(idResReport);

        }


        if (mUserIdPost == null || mUserIdPost.isEmpty() || !mUserIdPost.equals(userId)) {
            mTxtDelete.setVisibility(View.GONE);
            mTxtReport.setVisibility(View.VISIBLE);
        } else if (!mUserIdPost.isEmpty() && mUserIdPost.equals(userId)) {
            mTxtDelete.setVisibility(View.VISIBLE);
            mTxtReport.setVisibility(View.GONE);

        } else {
            bottomPanel.setVisibility(View.GONE);
        }
    }

    private void getBundleData() {
        if (getIntent().hasExtra(TimelineMediaActivity.EXTRA_BUZZ_DATA)) {
            mOriginData = getIntent().getParcelableExtra(TimelineMediaActivity.EXTRA_BUZZ_DATA);
            mFromActivity = getIntent().getStringExtra(EXTRA_TAG_FROM_ACTIVITY);
            if (mOriginData != null) {
                mBuzzs = mOriginData.listChildBuzzes;
            } else {
                Toast.makeText(this, R.string.common_error, Toast.LENGTH_SHORT).show();
                finish();
            }

        } else if (getIntent().hasExtra(TimelineMediaActivity.EXTRA_BUZZ_CHILD_DATA)) {
            mBuzzs = getIntent().getParcelableArrayListExtra(TimelineMediaActivity.EXTRA_BUZZ_CHILD_DATA);
        }

        if (mBuzzs != null) {
            mFirstShowIndex = getIntent().getIntExtra(TimelineMediaActivity.EXTRA_ITEM_FIRST_SHOW, 0);
            mUserIdPost = mBuzzs.get(0).userId;
        } else {
            Toast.makeText(this, R.string.common_error, Toast.LENGTH_SHORT).show();
            finish();
        }

    }

    private void setupEventBus() {
        RxEventBus.subscribe(SubjectCode.SUBJECT_UPDATE_COMMENT_IN_MEDIA_ACTIVITY,
                this,
                new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        int commentNumber = (int) o;
                        getCurrentBuzz().commentNumber = commentNumber;
                        if (mOriginData != null) {              // Update to TimeLineFragment, TimeLineFragment sent to This Activity : BuzzBean
                            RxEventBus.publish(SubjectCode.SUBJECT_UPDATE_TIMELINE, mOriginData);
                        }
                        mTxtNumberComment.setText(String.valueOf(commentNumber));
                    }
                });

        RxEventBus.subscribe(SubjectCode.SUBJECT_UPDATE_LIKE_IN_MEDIA_ACTIVITY,
                this,
                new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        if (o != null) {
                            getCurrentBuzz().like = (LikeBean) o;
                            if (mOriginData != null) {              // Update to TimeLineFragment, TimeLineFragment sent to This Activity : BuzzBean
                                RxEventBus.publish(SubjectCode.SUBJECT_UPDATE_TIMELINE, mOriginData);
                            }
                            RxEventBus.publish(SubjectCode.SUBJECT_UPDATE_LIKE_IN_HEADER_PROFILE, getCurrentBuzz());

                            updateBottomView(getCurrentBuzz());

                        }
                    }
                });
    }

    //SHOW DIALOG
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

                                String token = UserPreferences.getInstance().getToken();
                                String imageId = getCurrentBuzz().buzzId;
                                DeleteBuzzRequest deleteBuzzRequest = new DeleteBuzzRequest(token, imageId);
                                getPresenter().deleteMedia(deleteBuzzRequest, getCurrentBuzz());
                                mAlertDialog.show();
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
                                Toast.makeText(TimelineMediaActivity.this, R.string.start_download, Toast.LENGTH_SHORT).show();
                                getPresenter().saveMedia(TimelineMediaActivity.this, getCurrentBuzz());
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
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(TimelineMediaActivity.this, R.style.AlertDialogApp);
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
                String imageId = getCurrentBuzz().fileId;
                ReportRequest request = new ReportRequest(token, imageId, which, getReportType());
                getPresenter().reportImage(request);
            }
        });
        alertDialog.show();
    }

    public int getReportType() {
        switch (getCurrentMedia().mType) {
            case TypeView.MediaDetailType.AUDIO_TYPE:
                return Constants.REPORT_AUDIO;
            case TypeView.MediaDetailType.VIDEO_TYPE:
                return Constants.REPORT_VIDEO;
            case TypeView.MediaDetailType.STREAM_TYPE:
                return Constants.REPORT_VIDEO;
            case TypeView.MediaDetailType.IMAGE_TYPE:
                return Constants.REPORT_TYPE_IMAGE;
            default:
                return 0;
        }
    }

    private void invalidateMenu() {
        mIsMenuVisible = getCurrentMedia() != null && getCurrentMedia().mType.equals(TypeView.MediaDetailType.AUDIO_TYPE);
        invalidateOptionsMenu();
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
    /*-----------------------------------interface-----------------------------------*/

}
