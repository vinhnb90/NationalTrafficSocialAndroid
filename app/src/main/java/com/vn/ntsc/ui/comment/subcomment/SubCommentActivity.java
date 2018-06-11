package com.vn.ntsc.ui.comment.subcomment;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.nankai.designlayout.dialog.DialogMaterial;
import com.nankai.designlayout.dialog.enums.Style;
import com.tux.socket.models.SocketEvent;
import com.vn.ntsc.R;
import com.vn.ntsc.core.views.BaseActivity;
import com.vn.ntsc.repository.ActivityResultRequestCode;
import com.vn.ntsc.repository.TypeView;
import com.vn.ntsc.repository.model.comment.AddSubCommentRequest;
import com.vn.ntsc.repository.model.comment.CommentDetailRequest;
import com.vn.ntsc.repository.model.comment.CommentDetailResponse;
import com.vn.ntsc.repository.model.comment.DeleteSubCommentRequest;
import com.vn.ntsc.repository.model.comment.DeleteSubCommentResponse;
import com.vn.ntsc.repository.model.comment.ListSubCommentRequest;
import com.vn.ntsc.repository.model.comment.ListSubCommentResponse;
import com.vn.ntsc.repository.model.notification.push.NotificationAps;
import com.vn.ntsc.repository.model.notification.push.NotificationType;
import com.vn.ntsc.repository.model.timeline.JoinBuzzRequest;
import com.vn.ntsc.repository.model.timeline.JoinBuzzResponse;
import com.vn.ntsc.repository.model.timeline.datas.sub.BuzzSubCommentBean;
import com.vn.ntsc.repository.model.timeline.datas.sub.ListCommentBean;
import com.vn.ntsc.repository.model.user.UserInfoResponse;
import com.vn.ntsc.repository.preferece.UserPreferences;
import com.vn.ntsc.ui.profile.my.MyProfileActivity;
import com.vn.ntsc.utils.Constants;
import com.vn.ntsc.utils.DirtyWordUtils;
import com.vn.ntsc.utils.ImagesUtils;
import com.vn.ntsc.utils.LogUtils;
import com.vn.ntsc.utils.SystemUtils;
import com.vn.ntsc.utils.cache.CacheJson;
import com.vn.ntsc.utils.cache.CacheType;
import com.vn.ntsc.utils.time.TimeUtils;
import com.vn.ntsc.widget.adapter.MultifunctionAdapter;
import com.vn.ntsc.widget.decoration.SpacesItemDecoration;
import com.vn.ntsc.widget.toolbar.ToolbarTitleCenter;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

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

public class SubCommentActivity extends BaseActivity<SubCommentPresenter> implements SubCommentContract.View,
        MultifunctionAdapter.RequestLoadMoreListener,
        SwipeRefreshLayout.OnRefreshListener, SubCommentAdapterListener {

    private static final String TAG = SubCommentActivity.class.getSimpleName();

    public static final String KEY_SUB_COMMENT = "key.sub.ic_comment";
    public static final String KEY_BUZZ_ID = "key.buzz.id";
    public static final String KEY_IS_OWNER = "key.is.owner";
    public static final String KEY_EVENT_RETURN = "key.event.return";

    public static final int RETURN_UPDATE = 1;
    protected final int TAKE_COMMENT = 20;

    @BindView(R.id.toolbar)
    ToolbarTitleCenter mToolbar;

    @BindView(R.id.activity_comment_layout_comment)
    RelativeLayout layoutComment;
    @BindView(R.id.activity_comment_refresh)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.activity_comment_edt_comment)
    EditText edtComment;
    @BindView(R.id.activity_comment_btn_send_comment)
    ImageView btnSend;
    @BindView(R.id.activity_comment_list_comment)
    RecyclerView mRecyclerView;

    SubCommentsDetailAdapter adapter;

    ListCommentBean itemBuzzListBean;
    String buzzID;
    boolean isOwner;
    private boolean isFirstLoad = true;

    Gson gson;

    CompositeDisposable disposables;
    protected Subject<ListSubCommentResponse> listSubCommentResponseSubject = PublishSubject.create();

    public static void launch(AppCompatActivity activity, ListCommentBean itemBean, String buzzID, boolean isOwner, @ActivityResultRequestCode int requestCode) {
        final Intent intent = new Intent(activity, SubCommentActivity.class);
        intent.putExtra(KEY_SUB_COMMENT, itemBean);
        intent.putExtra(KEY_BUZZ_ID, buzzID);
        intent.putExtra(KEY_IS_OWNER, isOwner);
        activity.startActivityForResult(intent, requestCode);
        activity.overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_sub_comment;
    }

    @Override
    public void onCreateView(View rootView) {
        getModulesCommonComponent().inject(this);

        disposables = new CompositeDisposable();
        gson = new Gson();

        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null) {
            buzzID = bundle.getString(KEY_BUZZ_ID);
            isOwner = bundle.getBoolean(KEY_IS_OWNER);
            itemBuzzListBean = bundle.getParcelable(KEY_SUB_COMMENT);
        } else {
            finish();
        }

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        mRecyclerView.setHasFixedSize(true);
        //1dp as px, value might be obtained e.g. from dimen resources...
        int space = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics());
        mRecyclerView.addItemDecoration(new SpacesItemDecoration(space));

        adapter = new SubCommentsDetailAdapter(this);
        adapter.setOwner(isOwner);

        adapter.addHeaderView(initLayoutHeader());
        adapter.setEnableLoadMore(true);
        adapter.setHeaderAndEmpty(true);
        adapter.setOnLoadMoreListener(this, mRecyclerView);
        mRecyclerView.setAdapter(adapter);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        onObservable();
    }

    @Override
    public void onViewReady() {
        setSupportActionBar(mToolbar);
        mToolbar.setActionbar(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        isFirstLoad = true;
        int skip = 0;
        getListSubComment(skip, TAKE_COMMENT);
        getCommentDetail();
    }

    @Override
    public void onResume(View viewRoot) {
        super.onResume(viewRoot);
        getPresenter().sendBuzzJoin(new JoinBuzzRequest(UserPreferences.getInstance().getUserId(), this.buzzID, UserPreferences.getInstance().getToken()));
    }

    @Override
    public void onBackPressed() {
        returnBackData();
        super.onBackPressed();
    }

    @Override
    protected void onStop() {
        super.onStop();

        saveCacheListSubCommentResponse();
    }

    @Override
    protected void onDestroy() {
        if (disposables != null) {
            disposables.isDisposed();
            disposables = null;
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
                case Constants.SOCKET_BUZZ_SUB_CMT:
                    BuzzSubCommentBean buzzSubCommentBean = gson.fromJson(socketEvent.getMessage().getRawText(), BuzzSubCommentBean.class);
                    if (buzzSubCommentBean.buzzId.equals(this.buzzID)) {
                        if (buzzSubCommentBean.cmtId.equals(itemBuzzListBean.cmtId)) {
                            //buzzID
                            adapter.addData(0, buzzSubCommentBean);
                            ++itemBuzzListBean.subCommentNumber;
                            ++itemBuzzListBean.allSubCommentNumber;
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
            case NotificationType.NOTI_APPROVED_BUZZ:
            case NotificationType.NOTI_FAVORITED_CREATE_BUZZ:
            case NotificationType.NOTI_REPLY_YOUR_COMMENT:
                if (!notifyMessage.data.buzzid.equals(buzzID)) {
                    super.onShowNotification(notifyMessage);
                } else {
                    LogUtils.w(TAG, "Do not display the notification because buzz_id is the same as the buzz_id of the current ic_comment screen.!");
                }
                break;
            default:
                super.onShowNotification(notifyMessage);
        }
    }

    private View initLayoutHeader() {
        View headerView = LayoutInflater.from(this).inflate(R.layout.layout_item_sub_comment_detail_header_adapter, null);
        ImageView avatar = headerView.findViewById(R.id.layout_item_sub_comment_detail_adapter_avatar);
        TextView userName = headerView.findViewById(R.id.layout_item_sub_comment_detail_adapter_user_name);
        TextView status = headerView.findViewById(R.id.layout_item_sub_comment_detail_adapter_comment);
        TextView commentTime = headerView.findViewById(R.id.layout_item_sub_comment_detail_adapter_time);

        //Avatar
        ImagesUtils.loadRoundedAvatar(itemBuzzListBean.avatar, itemBuzzListBean.gender, avatar);

        try {
            Calendar calendarNow = Calendar.getInstance();

            Calendar calendarSend = Calendar.getInstance(TimeZone.getDefault());
            calendarSend.setTime(TimeUtils.YYYYMMDDHHMMSS.parse(itemBuzzListBean.commentTime));

            commentTime.setText(TimeUtils.getTimelineDif(calendarSend, calendarNow));
        } catch (ParseException e) {
            e.printStackTrace();
            commentTime.setText(R.string.common_now);
        }

        userName.setText(itemBuzzListBean.userName);
        status.setText(itemBuzzListBean.commentValue);
        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserInfoResponse userProfileBean = new UserInfoResponse(itemBuzzListBean.userId, itemBuzzListBean.gender, itemBuzzListBean.userName, itemBuzzListBean.avatar, Constants.BUZZ_TYPE_IS_NOT_FAVORITE);
                MyProfileActivity.launch(SubCommentActivity.this, v, userProfileBean, TypeView.ProfileType.COME_FROM_OTHER);
            }
        });
        return headerView;
    }

    private void getListSubComment(int skip, int take) {
        String token = UserPreferences.getInstance().getToken();
        ListSubCommentRequest listSubCommentRequest = new ListSubCommentRequest(token, buzzID, itemBuzzListBean.cmtId, skip, take);
        getPresenter().getSubListComment(listSubCommentRequest);
    }

    private void getCommentDetail() {
        String token = UserPreferences.getInstance().getToken();
        CommentDetailRequest commentDetailRequest = new CommentDetailRequest(token, buzzID, itemBuzzListBean.cmtId);
        getPresenter().getCommentDetail(commentDetailRequest);
    }

    private void returnBackData() {
        //Add new SubComment
        itemBuzzListBean.subComments.clear();
        itemBuzzListBean.subComments.addAll(adapter.getData());

        LogUtils.i(TAG, "ic_comment number: " + itemBuzzListBean.commentNumber);
        LogUtils.i(TAG, "all sub ic_comment number: " + itemBuzzListBean.allSubCommentNumber);
        LogUtils.i(TAG, "sub ic_comment number: " + itemBuzzListBean.subCommentNumber);

        Intent data = new Intent();
        data.putExtra(KEY_SUB_COMMENT, itemBuzzListBean);
        data.putExtra(KEY_EVENT_RETURN, RETURN_UPDATE);
        setResult(Activity.RESULT_OK, data);
        finish();
    }

    @OnClick(R.id.activity_comment_btn_send_comment)
    void clickSendComment() {
        String value = edtComment.getText().toString().trim();

        ArrayList<String> listDetectsBannedWord = DirtyWordUtils.listDetectsBannedWord(getBaseContext(),value);
        if (listDetectsBannedWord.size() > 0 ) {
            showConfirmDialog(getString(R.string.banned_word_error, DirtyWordUtils.convertArraySetToString(listDetectsBannedWord)), null, false);
            return;
        }

        if (!value.trim().isEmpty()) {
            String token = UserPreferences.getInstance().getToken();
            String commentValue = edtComment.getText().toString().trim();

            if (commentValue.length() > Constants.MAX_LENGTH_COMMENT) {
                Toast.makeText(getBaseContext(), String.format(getResources().getString(R.string.validate_comment_limit), String.valueOf(Constants.MAX_LENGTH_COMMENT)), Toast.LENGTH_LONG).show();
            } else {
                AddSubCommentRequest addSubCommentRequest = new AddSubCommentRequest(token, itemBuzzListBean.cmtId, commentValue, buzzID);
                getPresenter().sendSubComment(addSubCommentRequest);
                edtComment.getText().clear();
            }

            SystemUtils.hideSoftKeyboard(SubCommentActivity.this);
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

    @Override
    public void onRemoved(final BuzzSubCommentBean itemBean, final int position) {
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
                        UserPreferences.getInstance().getToken(), buzzID, itemBuzzListBean.cmtId,
                        itemBean.subCommentId);
                getPresenter().deleteComment(deleteSubCommentRequest, position);
            }
        }).show();
    }

    @Override
    public void viewProfileSubComment(BuzzSubCommentBean itemBean, View view, int position) {
        if (itemBean.isApprove == Constants.IS_APPROVED) {
            UserInfoResponse userProfileBean = new UserInfoResponse(itemBean.userId, itemBean.gender, itemBean.userName, itemBean.avatar, Constants.BUZZ_TYPE_IS_NOT_FAVORITE);
            MyProfileActivity.launch(this, view, userProfileBean, TypeView.ProfileType.COME_FROM_OTHER);
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

    @Override
    public void onViewMore(BuzzSubCommentBean itemBean, int position) {
        adapter.notifyItemChanged(position + adapter.getHeaderLayoutCount());
    }

    @Override
    public void onRefresh() {
        isFirstLoad = true;
        int skip = 0;
        getListSubComment(skip, TAKE_COMMENT);
        getCommentDetail();
    }

    @Override
    public void onLoadMoreRequested() {
        if (adapter.getData().size() > 0) {
            int take = adapter.getData().size() + TAKE_COMMENT;
            int skip = adapter.getData().size();
            isFirstLoad = false;
            getListSubComment(skip, take);
        }
    }

    // --------------- Server response ----------------------

    @Override
    public void onGetCommentDetail(CommentDetailResponse response) {

        LogUtils.i(TAG, "ic_comment number: " + response.data.commentNumber);
        LogUtils.i(TAG, "all sub ic_comment number: " + response.data.allSubCommentNumber);
        LogUtils.i(TAG, "sub ic_comment number: " + response.data.subCommentNumber);

        if (itemBuzzListBean != null)
            itemBuzzListBean.updateData(response.data);
    }

    @Override
    public void onSubListComment(ListSubCommentResponse response) {
        if (null == response.data) {
            adapter.loadMoreEnd(true);
        } else {
            if (response.data.isEmpty()) {
                adapter.loadMoreEnd(true);
            } else {
                if (isFirstLoad) {
                    adapter.setNewData(response.data);
                    isFirstLoad = false;
                    adapter.loadMoreComplete();
                } else {
                    adapter.addData(response.data);
                }
            }
        }
    }

    @Override
    public void onDeleteComment(DeleteSubCommentResponse response, int position) {
        Toast.makeText(context, getString(R.string.timeline_delete_comment_success), Toast.LENGTH_SHORT).show();
        adapter.remove(position);
        --itemBuzzListBean.subCommentNumber;
        --itemBuzzListBean.allSubCommentNumber;
    }

    @Override
    public void onCompleted() {
        if (mSwipeRefreshLayout.isRefreshing())
            mSwipeRefreshLayout.setRefreshing(false);
        adapter.loadMoreComplete();
        saveCacheListSubCommentResponse();
    }
    // ---------------End Server response ----------------------

    void saveCacheListSubCommentResponse() {
        ListSubCommentResponse listSubCommentResponse = new ListSubCommentResponse();
        listSubCommentResponse.data = adapter.getData();
        listSubCommentResponseSubject.onNext(listSubCommentResponse);
    }

    private void onObservable() {
        Disposable listSubCommentDisposable = listSubCommentResponseSubject
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.io())
                .subscribe(new Consumer<ListSubCommentResponse>() {
                    @Override
                    public void accept(ListSubCommentResponse listSubCommentResponse) throws Exception {
                        if (SystemUtils.isNetworkConnected())
                            CacheJson.saveObject(String.format(CacheType.CACHE_TIMELINE_DETAIL_LIST_SUB_COMMENT_BY_ID, buzzID, itemBuzzListBean.cmtId), listSubCommentResponse);
                    }
                });
        disposables.add(listSubCommentDisposable);

        Disposable refreshToken = getModulesCommonComponent()
                .onRefreshTokenSubject()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        if (SystemUtils.isNetworkConnected())
                            getPresenter().sendBuzzJoin(new JoinBuzzRequest(UserPreferences.getInstance().getUserId(), SubCommentActivity.this.buzzID, UserPreferences.getInstance().getToken()));
                    }
                });
        disposables.add(refreshToken);
    }
}
