package com.vn.ntsc.ui.comment;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.nankai.designlayout.dialog.DialogMaterial;
import com.nankai.designlayout.dialog.enums.Style;
import com.tux.socket.models.SocketEvent;
import com.vn.ntsc.R;
import com.vn.ntsc.core.views.BaseActivity;
import com.vn.ntsc.repository.ActivityResultRequestCode;
import com.vn.ntsc.repository.TypeView;
import com.vn.ntsc.repository.model.comment.AddCommentRequest;
import com.vn.ntsc.repository.model.comment.DeleteCommentRequest;
import com.vn.ntsc.repository.model.comment.DeleteCommentResponse;
import com.vn.ntsc.repository.model.comment.DeleteSubCommentRequest;
import com.vn.ntsc.repository.model.comment.DeleteSubCommentResponse;
import com.vn.ntsc.repository.model.comment.ListCommentRequest;
import com.vn.ntsc.repository.model.comment.ListCommentResponse;
import com.vn.ntsc.repository.model.comment.ListSubCommentRequest;
import com.vn.ntsc.repository.model.comment.ListSubCommentResponse;
import com.vn.ntsc.repository.model.favorite.AddFavoriteResponse;
import com.vn.ntsc.repository.model.favorite.RemoveFavoriteResponse;
import com.vn.ntsc.repository.model.notification.push.NotificationAps;
import com.vn.ntsc.repository.model.notification.push.NotificationType;
import com.vn.ntsc.repository.model.timeline.BuzzDetailRequest;
import com.vn.ntsc.repository.model.timeline.BuzzDetailResponse;
import com.vn.ntsc.repository.model.timeline.DeleteBuzzResponse;
import com.vn.ntsc.repository.model.timeline.JoinBuzzRequest;
import com.vn.ntsc.repository.model.timeline.JoinBuzzResponse;
import com.vn.ntsc.repository.model.timeline.LikeBuzzRequest;
import com.vn.ntsc.repository.model.timeline.LikeBuzzResponse;
import com.vn.ntsc.repository.model.timeline.TimelineType;
import com.vn.ntsc.repository.model.timeline.datas.BuzzBean;
import com.vn.ntsc.repository.model.timeline.datas.sub.BuzzSubCommentBean;
import com.vn.ntsc.repository.model.timeline.datas.sub.ListCommentBean;
import com.vn.ntsc.repository.model.user.UserInfoResponse;
import com.vn.ntsc.repository.preferece.UserPreferences;
import com.vn.ntsc.ui.comment.helper.HeaderViewHolder;
import com.vn.ntsc.ui.comment.helper.ImageHeaderViewHolder;
import com.vn.ntsc.ui.comment.helper.LiveStreamHeaderViewHolder;
import com.vn.ntsc.ui.comment.helper.ShareHeaderViewHolder;
import com.vn.ntsc.ui.comment.helper.StatusHeaderViewHolder;
import com.vn.ntsc.ui.comment.subcomment.SubCommentActivity;
import com.vn.ntsc.ui.profile.my.MyProfileActivity;
import com.vn.ntsc.utils.Constants;
import com.vn.ntsc.utils.DirtyWordUtils;
import com.vn.ntsc.utils.LogUtils;
import com.vn.ntsc.utils.SystemUtils;
import com.vn.ntsc.utils.cache.CacheJson;
import com.vn.ntsc.utils.cache.CacheType;
import com.vn.ntsc.widget.adapter.MultifunctionAdapter;
import com.vn.ntsc.widget.decoration.SpacesItemDecoration;
import com.vn.ntsc.widget.eventbus.RxEventBus;
import com.vn.ntsc.widget.eventbus.SubjectCode;
import com.vn.ntsc.widget.toolbar.ToolbarTitleCenter;
import com.vn.ntsc.widget.views.like.LikeView;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

import static com.tux.socket.models.SocketEvent.EVENT_AUTH_SUCCESS;

/**
 * Created by nankai on 8/10/2017.
 */

public class CommentActivity extends BaseActivity<CommentPresenter> implements CommentContract.View,
        MultifunctionAdapter.RequestLoadMoreListener,
        SwipeRefreshLayout.OnRefreshListener, CommentAdapterListener {

    private static final String TAG = CommentActivity.class.getSimpleName();

    //----------------------------------------------------------------
    //------------------------ Variable ------------------------------
    //----------------------------------------------------------------
    private static final String ELEMENT_COMMENT = "comment_element";
    public static final String KEY_USER_ID = "key.user.id";
    public static final String KEY_BUZZ_ID = "key.buzz.id";
    public static final String KEY_BUZZ_LIKE = "key.buzz.ic_like";
    public static final String KEY_TYPE_VIEW = "key.type.view";
    public static final String KEY_FROM = "key.activityComeFrom";
    public static final String EXTRA_FROM_TIMELINE_MEDIA_DETAIL = "TimelineMediaActivity";

    protected final int TAKE_COMMENT = 40;
    @TypeView.TypeViewTimeline
    private int typeView;

    @BindView(R.id.activity_comment_layout_comment)
    RelativeLayout layoutComment;
    @BindView(R.id.toolbar)
    ToolbarTitleCenter mToolbar;
    @BindView(R.id.activity_comment_refresh)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.activity_comment_edt_comment)
    EditText edtComment;
    @BindView(R.id.activity_comment_btn_send_comment)
    ImageView btnSend;
    @BindView(R.id.activity_comment_layout_item_buzz_list_adapter_image_like)
    ImageView likeListener;
    @BindView(R.id.activity_comment_list_comment)
    RecyclerView mRecyclerView;

    private CommentAdapter adapter;
    private LikeView mLikeView;

    private HeaderViewHolder headerViewHolder;
    private boolean isPreloadComment = false;

    private String buzzID;
    private String userID;
    private String activityComeFrom;

    private int isLike;

    @Inject
    public UserInfoResponse userInfo;

    Gson gson;
    CompositeDisposable disposables;
    BuzzBean buzzBean;

    protected Subject<ListCommentResponse> listCommentResponseSubject = PublishSubject.create();

    //----------------------------------------------------------------
    //------------------------ launch  -------------------------------
    //----------------------------------------------------------------
    public static void launch(AppCompatActivity activity, View v, String buzzID, String userID, int isLike, @TypeView.TypeViewTimeline int typeView, @ActivityResultRequestCode int requestCode) {
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, v, ELEMENT_COMMENT);
        final Intent intent = new Intent(activity, CommentActivity.class);
        intent.putExtra(KEY_USER_ID, userID);
        intent.putExtra(KEY_BUZZ_ID, buzzID);
        intent.putExtra(KEY_BUZZ_LIKE, isLike);
        intent.putExtra(KEY_TYPE_VIEW, typeView);
        activity.startActivityForResult(intent, requestCode, options.toBundle());
    }

    public static void launch(AppCompatActivity activity, View v, String buzzID, String userID) {
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, v, ELEMENT_COMMENT);
        final Intent intent = new Intent(activity, CommentActivity.class);
        intent.putExtra(KEY_USER_ID, userID);
        intent.putExtra(KEY_BUZZ_ID, buzzID);
        activity.startActivity(intent, options.toBundle());
    }

    public static void launch(AppCompatActivity activity, View v, String buzzID, String userID, String from) {
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, v, ELEMENT_COMMENT);
        final Intent intent = new Intent(activity, CommentActivity.class);
        intent.putExtra(KEY_USER_ID, userID);
        intent.putExtra(KEY_BUZZ_ID, buzzID);
        intent.putExtra(KEY_FROM, from);
        activity.startActivity(intent, options.toBundle());
    }

    public static void launch(AppCompatActivity activity, String buzzID, String userID) {
        // ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, v, ELEMENT_COMMENT);
        final Intent intent = new Intent(activity, CommentActivity.class);
        intent.putExtra(KEY_USER_ID, userID);
        intent.putExtra(KEY_BUZZ_ID, buzzID);
        activity.startActivity(intent);
    }

    //----------------------------------------------------------------
    //------------------------ View  ---------------------------------
    //----------------------------------------------------------------
    @Override
    public int getLayoutId() {
        return R.layout.activity_comment;
    }

    //----------------------------------------------------------------
    //------------------------ life cycle ----------------------------
    //----------------------------------------------------------------
    @Override
    public void onCreateView(View rootView) {
        getTimelineComponent().inject(this);

        disposables = new CompositeDisposable();
        mLikeView = new LikeView(getBaseContext());

        gson = new Gson();

        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null) {
            typeView = bundle.getInt(KEY_TYPE_VIEW, -1);
            buzzID = bundle.getString(KEY_BUZZ_ID);
            userID = bundle.getString(KEY_USER_ID);
            activityComeFrom = bundle.getString(KEY_FROM);
            isLike = bundle.getInt(KEY_BUZZ_LIKE, Constants.BUZZ_LIKE_TYPE_UNLIKE);
        } else {
            finish();
        }

        if (null == buzzID || buzzID.equals(""))
            finish();

        mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);

        //8dp as px, value might be obtained e.g. from dimen resources...
        int space = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics());
        mRecyclerView.addItemDecoration(new SpacesItemDecoration(space));

        adapter = new CommentAdapter(this);
        adapter.setEnableLoadMore(true);
        adapter.setHeaderAndEmpty(true);
        adapter.setOnLoadMoreListener(this, mRecyclerView);

        mRecyclerView.setAdapter(adapter);

        mSwipeRefreshLayout.setOnRefreshListener(this);

        setLikeIcon(isLike, false);

        setObserverEventBus();
        onObservable();

        ViewCompat.setTransitionName(layoutComment, ELEMENT_COMMENT);
    }

    @Override
    public void onViewReady() {
        setSupportActionBar(mToolbar);
        mToolbar.setActionbar(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        requestDetailTimeline(200);
        loadListComment(TAKE_COMMENT, 500);
    }

    @Override
    protected void onStop() {
        super.onStop();
        saveCacheListComment();
    }

    @Override
    protected void onDestroy() {

        if (disposables != null) {
            disposables.isDisposed();
            disposables = null;
        }

        if (headerViewHolder != null && headerViewHolder.getData() != null) {
            RxEventBus.publish(SubjectCode.SUBJECT_UPDATE_TIMELINE, headerViewHolder.getData());
        }

        if (headerViewHolder != null) {
            headerViewHolder.clearReference();
            headerViewHolder = null;
        }
        super.onDestroy();
    }

    @Override
    public void onReceiveSocket(SocketEvent socketEvent) {
        if (socketEvent.getEventType() == EVENT_AUTH_SUCCESS) {
            getPresenter().sendBuzzJoin(new JoinBuzzRequest(UserPreferences.getInstance().getUserId(), buzzID, UserPreferences.getInstance().getToken()));
        } else if (socketEvent.getEventType() == SocketEvent.EVENT_RECEIVE) {

            if (socketEvent.getMessage() == null) {
                LogUtils.w(TAG, "Socket message is null!");
                return;
            }

            if (socketEvent.getMessage().getRawText() == null) {
                LogUtils.w(TAG, "Socket message raw is null!");
                return;
            }

            switch (socketEvent.getMessage().getMessageType()) {
                case Constants.SOCKET_BUZZ_CMT:
                    ListCommentBean listCommentBean = gson.fromJson(socketEvent.getMessage().getRawText(), ListCommentBean.class);
                    if (listCommentBean.buzzId.equals(this.buzzID)) {
                        //buzzID
                        LogUtils.i(TAG, "socket message buzzID : " + this.buzzID);
                        headerViewHolder.updateCommentNumber(listCommentBean, CommentActivity.this);
                        RxEventBus.publish(SubjectCode.SUBJECT_UPDATE_TIMELINE, headerViewHolder.getData());
                        adapter.addFirst(listCommentBean);
                    }
                    break;
                case Constants.SOCKET_BUZZ_SUB_CMT:
                    BuzzSubCommentBean buzzSubCommentBean = gson.fromJson(socketEvent.getMessage().getRawText(), BuzzSubCommentBean.class);
                    if (buzzSubCommentBean.buzzId.equals(this.buzzID)) {
                        for (ListCommentBean bean : adapter.getData()) {
                            if (buzzSubCommentBean.cmtId.equals(bean.cmtId)) {
                                //buzzID
                                LogUtils.i(TAG, "socket message buzzID : " + this.buzzID + " | cmtId: " + bean.cmtId);
                                headerViewHolder.updateCommentNumber(buzzSubCommentBean, CommentActivity.this);
                                RxEventBus.publish(SubjectCode.SUBJECT_UPDATE_TIMELINE, headerViewHolder.getData());
                                if (bean.subComments == null)
                                    bean.subComments = new ArrayList<>();

                                ++bean.subCommentNumber;

                                bean.subComments.add(buzzSubCommentBean);
                                int index = adapter.getData().indexOf(bean);
                                if (index != -1) {
                                    adapter.addData(index, bean);
                                }
                            }
                        }
                    }
                    break;
                case Constants.SOCKET_BUZZ_JOIN:
                    JoinBuzzResponse joinBuzzResponse = gson.fromJson(socketEvent.getMessage().getRawText(), JoinBuzzResponse.class);
                    if (joinBuzzResponse.buzzId.equals(this.buzzID)) {
                        //TODO
                        LogUtils.i(TAG, "Join buzz response code: " + joinBuzzResponse.code);
                    }
                    break;
            }
        }
    }

    @Override
    public void onShowNotification(NotificationAps notifyMessage) {
        if (notifyMessage == null) {
            LogUtils.w(TAG, "Can't show notification using socket because NotificationAps is null");
            return;
        }

        int type = notifyMessage.data.notiType;

        switch (type) {
            case NotificationType.NOTI_LIKE_BUZZ:
            case NotificationType.NOTI_LIKE_OTHER_BUZZ:
            case NotificationType.NOTI_COMMENT_BUZZ:
            case NotificationType.NOTI_COMMENT_OTHER_BUZZ:
            case NotificationType.NOTI_FAVORITED_CREATE_BUZZ:
            case NotificationType.NOTI_REPLY_YOUR_COMMENT:
                if (!notifyMessage.data.buzzid.equals(buzzID)) {
                    super.onShowNotification(notifyMessage);
                } else {
                    LogUtils.w(TAG, "Do not display the notification because buzz_id is the same as the buzz_id of the current ic_comment screen.!");
                }
                break;
            case NotificationType.NOTI_APPROVE_BUZZ_TEXT:
            case NotificationType.NOTI_APPROVED_BUZZ:
            case NotificationType.NOTI_APPROVE_COMMENT:
            case NotificationType.NOTI_APPROVE_SUB_COMMENT:
                if (notifyMessage.data.buzzid != null && notifyMessage.data.buzzid.equals(buzzID)) {

                    if (type == NotificationType.NOTI_APPROVE_BUZZ_TEXT
                            || type == NotificationType.NOTI_APPROVED_BUZZ) {
                        requestDetailTimeline(0);
                    } else {
                        for (ListCommentBean bean : adapter.getData()) {
                            if (bean.cmtId.equals(notifyMessage.data.cmtId)) {
                                if (type == NotificationType.NOTI_APPROVE_SUB_COMMENT) {
                                    if (bean.subComments != null && bean.subComments.size() > 0) {
                                        for (BuzzSubCommentBean subComments : bean.subComments) {
                                            if (subComments.subCommentId.equals(notifyMessage.data.subCmtId)) {
                                                subComments.isApprove = Constants.IS_APPROVED;
                                                adapter.notifyDataSetChanged();
                                                break;
                                            }
                                        }
                                    }
                                } else {
                                    bean.isApproved = Constants.IS_APPROVED;
                                    adapter.notifyDataSetChanged();
                                }
                                break;
                            }
                        }
                    }
                }
                super.onShowNotification(notifyMessage);
                break;
            default:
                super.onShowNotification(notifyMessage);
                break;
        }
    }

    //----------------------------------------------------------------
    //------------------------ Listener ------------------------------
    //----------------------------------------------------------------
    @OnClick(R.id.activity_comment_layout_item_buzz_list_adapter_image_like)
    void clickLike() {
        if (buzzBean != null && buzzBean.isApproved == Constants.IS_APPROVED) {
            int likeType = isLike == Constants.BUZZ_LIKE_TYPE_LIKE ? Constants.BUZZ_LIKE_TYPE_UNLIKE : Constants.BUZZ_LIKE_TYPE_LIKE;
            this.isLike = likeType;
            LikeBuzzRequest likeBuzzRequest = new LikeBuzzRequest(UserPreferences.getInstance().getToken(), this.buzzID, likeType);
            getPresenter().onLike(likeBuzzRequest);
        } else {
            DialogMaterial.Builder builder = new DialogMaterial.Builder(this)
                    .setStyle(Style.HEADER_WITH_TITLE)
                    .setTitle(R.string.common_approved)
                    .setContent(R.string.not_approved_buzz)
                    .onPositive(R.string.common_ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            builder.show();
        }
    }

    @OnClick(R.id.activity_comment_btn_send_comment)
    void clickSendComment() {
        if (buzzBean != null && buzzBean.isApproved == Constants.IS_APPROVED) {
            String value = edtComment.getText().toString().trim();
            ArrayList<String> listDetectsBannedWord = DirtyWordUtils.listDetectsBannedWord(getBaseContext(), value);
            if (listDetectsBannedWord.size() > 0) {
                showConfirmDialog(getString(R.string.banned_word_error, DirtyWordUtils.convertArraySetToString(listDetectsBannedWord)), null, false);
                return;
            }

            if (!value.isEmpty()) {
                String token = UserPreferences.getInstance().getToken();
                String commentValue = edtComment.getText().toString().trim();
                if (commentValue.length() > Constants.MAX_LENGTH_COMMENT) {
                    Toast.makeText(getBaseContext(),
                            String.format(getResources().getString(R.string.validate_comment_limit), String.valueOf(Constants.MAX_LENGTH_COMMENT)),
                            Toast.LENGTH_LONG)
                            .show();
                } else {
                    AddCommentRequest addCommentRequest = new AddCommentRequest(token, this.buzzID, commentValue);
                    getPresenter().sendComment(addCommentRequest);
                    edtComment.getText().clear();
                    SystemUtils.hideSoftKeyboard(CommentActivity.this);
                }
            }
        } else {
            DialogMaterial.Builder builder = new DialogMaterial.Builder(this)
                    .setStyle(Style.HEADER_WITH_TITLE)
                    .setTitle(R.string.common_approved)
                    .setContent(R.string.not_approved_buzz)
                    .onPositive(R.string.common_ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            builder.show();
        }
    }

    @OnTextChanged(R.id.activity_comment_edt_comment)
    void CheckCharSequenceComment(CharSequence charSequence) {
        if (charSequence.length() > 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                btnSend.setColorFilter(context.getResources().getColor(R.color.default_app, context.getTheme()));
            } else {
                btnSend.setColorFilter(context.getResources().getColor(R.color.default_app));
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                btnSend.setColorFilter(context.getResources().getColor(R.color.gray, context.getTheme()));
            } else {
                btnSend.setColorFilter(context.getResources().getColor(R.color.gray));
            }
        }
    }

    //----------------------------------------------------------------
    //------------------------ Loading -------------------------------
    //----------------------------------------------------------------
    @Override
    public void onRefresh() {
        requestDetailTimeline(0);
        loadListComment(TAKE_COMMENT, 0);
    }

    @Override
    public void onLoadMoreRequested() {
        if (adapter.getData().size() > 0) {
            int take = adapter.getData().size() + TAKE_COMMENT;
            int skip = adapter.getData().size();
            loadMoreListComment(skip, take);
        }
    }

    //------------------------------------------------------------
    //-------------------Adapter listener ------------------------
    //------------------------------------------------------------

    @Override
    public void onOpenSubComment(ListCommentBean itemBean, boolean isOwner) {
        if (itemBean.isApproved == Constants.IS_APPROVED)
            SubCommentActivity.launch(CommentActivity.this, itemBean, this.buzzID, isOwner, ActivityResultRequestCode.REQUEST_SUB_COMMENT);
        else {
            DialogMaterial.Builder builder = new DialogMaterial.Builder(this)
                    .setStyle(Style.HEADER_WITH_TITLE)
                    .setTitle(R.string.common_approved)
                    .setContent(R.string.not_approved_buzz)
                    .onPositive(R.string.common_ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            builder.show();
        }
    }

    @Override
    public void onRemoveSubComment(final ListCommentBean bean, final BuzzSubCommentBean itemBean, final int position, boolean isOwner) {
        new DialogMaterial.Builder(this).setStyle(Style.HEADER_WITH_TITLE)
                .setTitle(R.string.common_remove_comment)
                .setContent(R.string.do_you_want_remove_comment)
                .onNegative(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).onPositive(R.string.common_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DeleteSubCommentRequest deleteSubCommentRequest = new DeleteSubCommentRequest(
                        UserPreferences.getInstance().getToken(), CommentActivity.this.buzzID, bean.cmtId,
                        itemBean.subCommentId);
                getPresenter().deleteSubComment(bean, deleteSubCommentRequest, position);
            }
        }).show();
    }

    @Override
    public void onRemoveComment(final ListCommentBean itemBean, final int position) {
        new DialogMaterial.Builder(this).setStyle(Style.HEADER_WITH_TITLE)
                .setTitle(R.string.common_remove_comment)
                .setContent(R.string.do_you_want_remove_comment)
                .onNegative(R.string.common_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).onPositive(R.string.common_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String token = UserPreferences.getInstance().getToken();
                DeleteCommentRequest deleteCommentRequest = new DeleteCommentRequest(token, CommentActivity.this.buzzID, itemBean.cmtId);
                getPresenter().deleteComment(deleteCommentRequest, position);
                dialog.dismiss();
            }
        }).show();
    }

    @Override
    public void onViewProfileUserComment(ListCommentBean itemBean, View view, int position) {
        UserInfoResponse userProfileBean = new UserInfoResponse(itemBean.userId, itemBean.gender, itemBean.userName, itemBean.avatar, Constants.BUZZ_TYPE_IS_NOT_FAVORITE);
        MyProfileActivity.launch(this, view, userProfileBean, TypeView.ProfileType.COME_FROM_OTHER);
    }

    @Override
    public void onViewProfileUserComment(BuzzSubCommentBean itemBean, View view, int position) {
        UserInfoResponse userProfileBean = new UserInfoResponse(itemBean.userId, itemBean.gender, itemBean.userName, itemBean.avatar, Constants.BUZZ_TYPE_IS_NOT_FAVORITE);
        MyProfileActivity.launch(this, view, userProfileBean, TypeView.ProfileType.COME_FROM_OTHER);
    }

    @Override
    public void onViewMore(ListCommentBean itemBean, int position) {
        adapter.notifyItemChanged(position + adapter.getHeaderLayoutCount());
    }

    //----------------------------------------------------------
    //----------------------Server event -----------------------
    //----------------------------------------------------------
    @Override
    public void onTimelineDetail(final BuzzDetailResponse response) {
        if (response.data != null) {

            this.buzzBean = response.data;

            if (headerViewHolder != null && headerViewHolder.getData() != null) {
                headerViewHolder.getData().setDataBean(response.data);
            }

            if (response.data.buzzId != null)
                this.buzzID = response.data.buzzId;

            RxEventBus.publish(SubjectCode.SUBJECT_UPDATE_TIMELINE, response.data);

            if (response.data.userId != null)
                //check buzz is owner
                adapter.setOwner(response.data.userId.equals(UserPreferences.getInstance().getUserId()));

            //Update timeline detail
            updateView(response.data);
        }

        getPresenter().sendBuzzJoin(new JoinBuzzRequest(UserPreferences.getInstance().getUserId(), this.buzzID, UserPreferences.getInstance().getToken()));
    }

    @Override
    public void onBuzzListComment(ListCommentResponse response) {
        LogUtils.i(TAG, "onBuzzListComment");
        if (null == response.data) {
            adapter.loadMoreEnd(true);
        } else {
            if (response.data.isEmpty()) {
                adapter.loadMoreEnd(true);
                LogUtils.i(TAG, "response.data.isEmpty");
            } else {
                LogUtils.i(TAG, "add new list comment");
                adapter.setNewData(response.data);
            }
        }
    }

    @Override
    public void onLoadMoreBuzzListComment(ListCommentResponse response, boolean hasScrollLastPosition) {
        LogUtils.i(TAG, "onLoadMoreBuzzListComment");
        if (null == response.data) {
            adapter.loadMoreEnd(true);
        } else {
            if (response.data.isEmpty()) {
                adapter.loadMoreEnd(true);
            } else {
                adapter.addData(response.data);
            }
        }
    }

    @Override
    public void onFavorite(AddFavoriteResponse response) {
        RxEventBus.publish(SubjectCode.SUBJECT_UPDATE_TIMELINE_FAVORITE, userID);
        requestDetailTimeline(0);
    }

    @Override
    public void onUnFavorite(RemoveFavoriteResponse response) {
        RxEventBus.publish(SubjectCode.SUBJECT_UPDATE_TIMELINE_UNFAVORITE, userID);
        requestDetailTimeline(0);
    }

    @Override
    public void onDelete(DeleteBuzzResponse response) {
        //publish to TimelineFragment
        RxEventBus.publish(SubjectCode.SUBJECT_REMOVE_TIMELINE, this.buzzID);
        finish();
    }

    @Override
    public void onLike(LikeBuzzResponse response) {
        RxEventBus.publish(SubjectCode.SUBJECT_UPDATE_TIMELINE_LIKE, this.buzzID);
        requestDetailTimeline(0);
    }

    @Override
    public void onLikeFail() {
        isLike = isLike == Constants.BUZZ_LIKE_TYPE_LIKE ? Constants.BUZZ_LIKE_TYPE_UNLIKE : Constants.BUZZ_LIKE_TYPE_LIKE;
    }

    @Override
    public void onDeleteComment(DeleteCommentResponse response, int position) {
        --headerViewHolder.getData().commentNumber;

        Toast.makeText(context, getString(R.string.timeline_delete_comment_success), Toast.LENGTH_SHORT).show();

        headerViewHolder.updateCommentNumber(this);

        adapter.remove(position);

        saveCacheListComment();

        RxEventBus.publish(SubjectCode.SUBJECT_UPDATE_TIMELINE, headerViewHolder.getData());
    }

    @Override
    public void onDeleteSubComment(DeleteSubCommentResponse response, ListCommentBean bean, int position) {
        requestDetailTimeline(0);
        loadListComment(adapter.getData().size(), 0);
    }

    @Override
    public void onComplete() {
        if (mSwipeRefreshLayout.isRefreshing())
            mSwipeRefreshLayout.setRefreshing(false);

        adapter.loadMoreComplete();
        saveCacheListComment();
    }

    @Override
    public void onBuzzNotFoundFromNotificationId() {
        Toast.makeText(this, R.string.buzz_item_not_found, Toast.LENGTH_SHORT).show();
        finish();
    }

    //----------------------------------------------------------------
    //------------------- Activity for result ------------------------
    //----------------------------------------------------------------
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == ActivityResultRequestCode.REQUEST_SUB_COMMENT) { //Update buzz
                ListCommentBean commentBean = data.getParcelableExtra(SubCommentActivity.KEY_SUB_COMMENT);
                updateSubComment(commentBean);
            }
        }
    }

    private void updateSubComment(final ListCommentBean commentBean) {
        if (headerViewHolder == null || adapter == null)
            return;

        for (int i = 0; i < adapter.getData().size(); i++) {

            if (adapter.getData().get(i).cmtId.equals(commentBean.cmtId)) {

                headerViewHolder.updateCommentNumber(commentBean, this);

                adapter.getData().get(i).updateData(commentBean);
                adapter.notifyDataSetChanged();

                saveCacheListComment();
                RxEventBus.publish(SubjectCode.SUBJECT_UPDATE_TIMELINE, headerViewHolder.getData());
                break;
            }
        }
    }

    //----------------------------------------------------------------
    //------------------------ Function ------------------------------
    //----------------------------------------------------------------

    void saveCacheListComment() {
        ListCommentResponse listCommentResponse = new ListCommentResponse();
        listCommentResponse.data = adapter.getData();
        listCommentResponseSubject.onNext(listCommentResponse);
    }

    void setLikeIcon(int isLike, boolean isAnimation) {
        this.isLike = isLike;
        if (isLike == Constants.BUZZ_LIKE_TYPE_LIKE) {
            likeListener.setImageResource(R.drawable.ic_like);
            if (isAnimation)
                animationLike(findViewById(R.id.activity_comment_layout_item_buzz_list_adapter_image_like), "-1");
        } else {
            likeListener.setImageResource(R.drawable.ic_unlike);
            if (isAnimation)
                animationLike(findViewById(R.id.activity_comment_layout_item_buzz_list_adapter_image_like), "+1");
        }
    }

    public void animationLike(View view, String str) {
        mLikeView.setTextColor(getResources().getColor(R.color.gray));
        mLikeView.setText(str);
        mLikeView.show(view);
    }

    private void requestDetailTimeline(int millisecond) {
        if (!mSwipeRefreshLayout.isRefreshing())
            mSwipeRefreshLayout.setRefreshing(true);
        UserPreferences userPreference = new UserPreferences();
        BuzzDetailRequest buzzDetailRequest = new BuzzDetailRequest(userPreference.getToken(), this.buzzID);
        getPresenter().getTimelineDetail(buzzDetailRequest, millisecond);
    }

    private void loadListComment(int take, int millisecond) {
        ListCommentRequest listCommentRequest = new ListCommentRequest(UserPreferences.getInstance().getToken(), this.buzzID, 0, take);
        getPresenter().getBuzzListComment(listCommentRequest, millisecond);
    }

    private void loadMoreListComment(int skip, int take) {
        ListCommentRequest listCommentRequest = new ListCommentRequest(UserPreferences.getInstance().getToken(), this.buzzID, skip, take);
        getPresenter().getMoreBuzzListComment(listCommentRequest, false);
    }

    private void updateView(BuzzBean itemBuzzBean) {
        adapter.setHeaderView(initLayoutHeader(itemBuzzBean), 0);

        if (isPreloadComment && null != itemBuzzBean.commentsList)
            adapter.setNewData(itemBuzzBean.commentsList);

        isPreloadComment = false;
        setLikeIcon(itemBuzzBean.like.isLike, false);
        headerViewHolder.updateFavoriteIcon(itemBuzzBean.isFavorite, itemBuzzBean.userId);
    }

    //----------------------------------------------------------------
    //---------------- Header timeline detail ------------------------
    //----------------------------------------------------------------
    private View initLayoutHeader(BuzzBean itemBuzzBean) {
        final @TimelineType int typeHeader = getTypeView(itemBuzzBean);
        LogUtils.e(TAG, "type header: " + typeHeader);
        View headerView;
        switch (typeHeader) {
            case TimelineType.BUZZ_TYPE_SHARE_AUDIO:
                headerView = LayoutInflater.from(this).inflate(R.layout.layout_item_comment_share_audio, null);
                headerViewHolder = new ShareHeaderViewHolder(typeHeader, headerView, typeView);
                break;
            case TimelineType.BUZZ_TYPE_SHARE_LIVE_STREAM:
                headerView = LayoutInflater.from(this).inflate(R.layout.layout_item_comment_share_live_stream, null);
                headerViewHolder = new ShareHeaderViewHolder(typeHeader, headerView, typeView);
                break;
            case TimelineType.BUZZ_TYPE_MULTI_BUZZ_LIVE_STREAM:
                headerView = LayoutInflater.from(this).inflate(R.layout.layout_item_comment_live_stream, null);
                headerViewHolder = new LiveStreamHeaderViewHolder(typeHeader, headerView, typeView);
                break;
            case TimelineType.BUZZ_TYPE_MULTI_BUZZ_VIDEO_1:
            case TimelineType.BUZZ_TYPE_MULTI_BUZZ_AUDIO:
            case TimelineType.BUZZ_TYPE_MULTI_BUZZ_IMAGE_1:
                headerView = LayoutInflater.from(this).inflate(R.layout.layout_item_comment_image_1, null);
                headerViewHolder = new ImageHeaderViewHolder(typeHeader, headerView, typeView);
                // If user leaving from TimelineMediaActivity to CommentActivity by press icon Comment
                // It send only item ListBuzzChild, single image or audio or video
                // So setActivityComeFrom for typeHeader = 1 item
                ((ImageHeaderViewHolder) headerViewHolder).setActivityComeFrom(activityComeFrom);
                break;
            case TimelineType.BUZZ_TYPE_MULTI_BUZZ_2:
                headerView = LayoutInflater.from(this).inflate(R.layout.layout_item_comment_image_2, null);
                headerViewHolder = new ImageHeaderViewHolder(typeHeader, headerView, typeView);
                break;
            case TimelineType.BUZZ_TYPE_MULTI_BUZZ_3:
                headerView = LayoutInflater.from(this).inflate(R.layout.layout_item_comment_image_3, null);
                headerViewHolder = new ImageHeaderViewHolder(typeHeader, headerView, typeView);
                break;
            case TimelineType.BUZZ_TYPE_MULTI_BUZZ_4:
                headerView = LayoutInflater.from(this).inflate(R.layout.layout_item_comment_image_4, null);
                headerViewHolder = new ImageHeaderViewHolder(typeHeader, headerView, typeView);
                break;
            case TimelineType.BUZZ_TYPE_MULTI_BUZZ_5:
                headerView = LayoutInflater.from(this).inflate(R.layout.layout_item_comment_image_5, null);
                headerViewHolder = new ImageHeaderViewHolder(typeHeader, headerView, typeView);
                break;
            case TimelineType.BUZZ_TYPE_MULTI_BUZZ_MORE:
                headerView = LayoutInflater.from(this).inflate(R.layout.layout_item_comment_image_more, null);
                headerViewHolder = new ImageHeaderViewHolder(typeHeader, headerView, typeView);
                break;
            default:
                headerView = LayoutInflater.from(this).inflate(R.layout.layout_item_comment_status, null);
                headerViewHolder = new StatusHeaderViewHolder(typeHeader, headerView, typeView);
                break;
        }

        headerViewHolder.onBindView(this, itemBuzzBean);
        return headerView;
    }

    public @TimelineType
    int getTypeView(BuzzBean itemBuzzBean) {
        int childNumber = itemBuzzBean.childNumber;
        if (itemBuzzBean.isBuzzShare()) {
            if (itemBuzzBean.shareDetailBean.listChildBuzzes.get(0).buzzType.equals(Constants.BUZZ_TYPE_FILE_LIVE_STREAM))
                return TimelineType.BUZZ_TYPE_SHARE_LIVE_STREAM;
            return TimelineType.BUZZ_TYPE_SHARE_AUDIO;
        } else if (childNumber == 1) {
            if (itemBuzzBean.listChildBuzzes.get(0).buzzType.equals(Constants.BUZZ_TYPE_FILE_VIDEO)) {
                return TimelineType.BUZZ_TYPE_MULTI_BUZZ_VIDEO_1;
            } else if (itemBuzzBean.listChildBuzzes.get(0).buzzType.equals(Constants.BUZZ_TYPE_FILE_LIVE_STREAM)) {
                return TimelineType.BUZZ_TYPE_MULTI_BUZZ_LIVE_STREAM;
            } else if (itemBuzzBean.listChildBuzzes.get(0).buzzType.equals(Constants.BUZZ_TYPE_FILE_AUDIO)) {
                return TimelineType.BUZZ_TYPE_MULTI_BUZZ_AUDIO;
            } else {
                return TimelineType.BUZZ_TYPE_MULTI_BUZZ_IMAGE_1;
            }
        } else if (childNumber == 2) {
            return TimelineType.BUZZ_TYPE_MULTI_BUZZ_2;
        } else if (childNumber == 3) {
            return TimelineType.BUZZ_TYPE_MULTI_BUZZ_3;
        } else if (childNumber == 4) {
            return TimelineType.BUZZ_TYPE_MULTI_BUZZ_4;
        } else if (childNumber == 5) {
            return TimelineType.BUZZ_TYPE_MULTI_BUZZ_5;
        } else if (childNumber > 5) {
            return TimelineType.BUZZ_TYPE_MULTI_BUZZ_MORE;
        } else {
            return TimelineType.BUZZ_TYPE_STATUS;
        }
    }

    //----------------------------------------------------------------
    //------------------- subject event bus --------------------------
    //----------------------------------------------------------------
    private void setObserverEventBus() {
        RxEventBus.subscribe(SubjectCode.SUBJECT_UPDATE_TIMELINE_LIKE, this, new Consumer<Object>() {

            @Override
            public void accept(Object buzzId) throws Exception {
                if (buzzId != null) {
                    int isLike = CommentActivity.this.isLike == Constants.BUZZ_LIKE_TYPE_LIKE ? Constants.BUZZ_LIKE_TYPE_UNLIKE : Constants.BUZZ_LIKE_TYPE_LIKE;
                    setLikeIcon(isLike, true);
                }
            }
        });

        RxEventBus.subscribe(SubjectCode.SUBJECT_UPDATE_TIMELINE_SHARE, this, new Consumer<Object>() {

            @Override
            public void accept(Object buzzId) throws Exception {
                if (buzzId != null) {
                    headerViewHolder.updateShare(CommentActivity.this);
                }
            }
        });

        RxEventBus.subscribe(SubjectCode.SUBJECT_UPDATE_TIMELINE_FAVORITE, this, new Consumer<Object>() {

            @Override
            public void accept(Object userId) throws Exception {
                if (userId != null) {
                    headerViewHolder.updateFavoriteIcon(Constants.BUZZ_TYPE_IS_FAVORITE, userID);
                }
            }
        });

        RxEventBus.subscribe(SubjectCode.SUBJECT_UPDATE_TIMELINE_UNFAVORITE, this, new Consumer<Object>() {

            @Override
            public void accept(Object userId) throws Exception {
                if (userId != null) {
                    headerViewHolder.updateFavoriteIcon(Constants.BUZZ_TYPE_IS_NOT_FAVORITE, userID);
                }
            }
        });
    }

    private void onObservable() {
        Disposable listCommentDisposable = listCommentResponseSubject
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.io())
                .subscribe(new Consumer<ListCommentResponse>() {
                    @Override
                    public void accept(ListCommentResponse listCommentResponse) throws Exception {
                        if (SystemUtils.isNetworkConnected())
                            CacheJson.saveObject(String.format(CacheType.CACHE_TIMELINE_DETAIL_LIST_COMMENT_BY_ID, buzzID), listCommentResponse);
                    }
                });
        disposables.add(listCommentDisposable);

        Disposable refreshToken = getModulesCommonComponent().onRefreshTokenSubject().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        if (SystemUtils.isNetworkConnected())
                            getPresenter().sendBuzzJoin(new JoinBuzzRequest(UserPreferences.getInstance().getUserId(), CommentActivity.this.buzzID, UserPreferences.getInstance().getToken()));
                    }
                });
        disposables.add(refreshToken);
    }
}
