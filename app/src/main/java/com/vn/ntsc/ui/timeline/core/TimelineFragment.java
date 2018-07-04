package com.vn.ntsc.ui.timeline.core;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareHashtag;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.gson.Gson;
import com.nankai.designlayout.dialog.DialogMaterial;
import com.nankai.designlayout.dialog.enums.Style;
import com.tux.socket.models.Message;
import com.tux.socket.models.SocketEvent;
import com.vn.ntsc.BuildConfig;
import com.vn.ntsc.R;
import com.vn.ntsc.core.views.BaseActivity;
import com.vn.ntsc.core.views.BaseFragment;
import com.vn.ntsc.repository.ActivityResultRequestCode;
import com.vn.ntsc.repository.TypeView;
import com.vn.ntsc.repository.model.favorite.AddFavoriteRequest;
import com.vn.ntsc.repository.model.favorite.AddFavoriteResponse;
import com.vn.ntsc.repository.model.favorite.RemoveFavoriteRequest;
import com.vn.ntsc.repository.model.favorite.RemoveFavoriteResponse;
import com.vn.ntsc.repository.model.notification.push.NotificationMessage;
import com.vn.ntsc.repository.model.notification.push.NotificationType;
import com.vn.ntsc.repository.model.profile.EvaluateUserProfileResponse;
import com.vn.ntsc.repository.model.share.AddNumberShareRequest;
import com.vn.ntsc.repository.model.share.AddNumberShareResponse;
import com.vn.ntsc.repository.model.timeline.BuzzDetailRequest;
import com.vn.ntsc.repository.model.timeline.BuzzDetailResponse;
import com.vn.ntsc.repository.model.timeline.BuzzListRequest;
import com.vn.ntsc.repository.model.timeline.BuzzListResponse;
import com.vn.ntsc.repository.model.timeline.DeleteBuzzRequest;
import com.vn.ntsc.repository.model.timeline.DeleteBuzzResponse;
import com.vn.ntsc.repository.model.timeline.LikeBuzzRequest;
import com.vn.ntsc.repository.model.timeline.LikeBuzzResponse;
import com.vn.ntsc.repository.model.timeline.datas.BuzzBean;
import com.vn.ntsc.repository.model.timeline.datas.ShareDetailBean;
import com.vn.ntsc.repository.model.timeline.datas.sub.ListBuzzChild;
import com.vn.ntsc.repository.model.timeline.datas.sub.ListTagFriendsBean;
import com.vn.ntsc.repository.model.user.GetUserInfoResponse;
import com.vn.ntsc.repository.model.user.UserInfoResponse;
import com.vn.ntsc.repository.preferece.UserPreferences;
import com.vn.ntsc.repository.publicfile.PublicFileResponse;
import com.vn.ntsc.services.UserLiveStreamService;
import com.vn.ntsc.ui.comments.CommentActivity;
import com.vn.ntsc.ui.mediadetail.timeline.TimelineMediaActivity;
import com.vn.ntsc.ui.profile.my.MyProfileActivity;
import com.vn.ntsc.ui.tagfriends.TagFriendActivity;
import com.vn.ntsc.ui.timeline.adapter.TimelineAdapter;
import com.vn.ntsc.ui.timeline.livestream.TimelineLiveStreamViewHolderListener;
import com.vn.ntsc.utils.Constants;
import com.vn.ntsc.utils.LogUtils;
import com.vn.ntsc.utils.SystemUtils;
import com.vn.ntsc.utils.cache.CacheJson;
import com.vn.ntsc.utils.cache.CacheType;
import com.vn.ntsc.widget.adapter.IMultifunctionAdapter;
import com.vn.ntsc.widget.adapter.MultifunctionAdapter;
import com.vn.ntsc.widget.decoration.SpacesItemDecoration;
import com.vn.ntsc.widget.eventbus.RxEventBus;
import com.vn.ntsc.widget.eventbus.SubjectCode;
import com.vn.ntsc.widget.socket.RxSocket;
import com.vn.ntsc.widget.views.like.LikeView;
import com.vn.ntsc.widget.views.popup.OnPopupShareListener;
import com.vn.ntsc.widget.views.popup.PopupShare;

import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Objects;

import javax.inject.Inject;

import butterknife.BindView;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

import static com.vn.ntsc.repository.TypeView.TypeViewTimeline.TIMELINE_ALL;

/**
 * Created by nankai on 12/13/2017.
 */

public abstract class TimelineFragment extends BaseFragment<TimeLinePresenter> implements TimeLineContract.View,
        MultifunctionAdapter.RequestLoadMoreListener, SwipeRefreshLayout.OnRefreshListener,
        TimelineListener<BuzzBean>, OnPopupShareListener, TimelineLiveStreamViewHolderListener {

    //----------------------------------------------------------------
    //------------------------ Variable ------------------------------
    //----------------------------------------------------------------
    protected static final String BUNDLE_TYPE = "bundle.type";

    public static final String KEY_EVENT_RETURN = "key.event.return";
    public static final int RETURN_UPDATE = 1;
    public static final String KEY_BUZZ_RETURN = "key.buzz.return";

    public static final int TAKE_NUMBER = 20;

    protected int typeView = TIMELINE_ALL;
    private int take = TAKE_NUMBER;
    private boolean sIsScrolling;

    @BindView(R.id.fragment_timeline_swipe_container)
    protected SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.fragment_timeline_recycler_view)
    protected RecyclerView recyclerView;

    protected LikeView mLikeView;

    protected TimelineAdapter<BuzzBean> adapter;

    protected RequestManager glide;

    @Inject
    protected UserInfoResponse userInfo;

    //Facebook call back
    @Inject
    protected CallbackManager callbackManager;

    //RxEvenBus Subject
    protected CompositeDisposable disposables;
    protected Subject<BuzzDetailResponse> buzzDetailResponseSubject = PublishSubject.create();
    protected Subject<BuzzListResponse> listBuzzSubject = PublishSubject.create();
    protected Subject<BuzzListResponse> listBuzzLiveStreamSubject = PublishSubject.create();
    protected Subject<PublicFileResponse> listPublicFiles = PublishSubject.create();
    protected Subject<UserInfoResponse> userInfoSubject = PublishSubject.create();

    //----------------------------------------------------------------
    //------------------------ View  ---------------------------------
    //----------------------------------------------------------------
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_timeline;
    }

    //----------------------------------------------------------------
    //------------------------ life cycle ----------------------------
    //----------------------------------------------------------------
    @Override
    protected void onCreateView(View rootView, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        TAG = TimelineFragment.this.getClass().getSimpleName();

        disposables = new CompositeDisposable();

        glide = Glide.with(this);

        mLikeView = new LikeView(getContext());
        if (getArguments() != null)
            typeView = getArguments().getInt(BUNDLE_TYPE);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        //8dp as px, value might be obtained e.g. from dimen resources...
        int space = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics());
        recyclerView.addItemDecoration(new SpacesItemDecoration(space));
        //Cache
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(1000);
//        recyclerView.setDrawingCacheEnabled(true);
//        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        adapter = new TimelineAdapter<>(glide, LayoutInflater.from(context), this);
        adapter.isUseEmpty(true);
        adapter.setEnableLoadMore(true);
        adapter.setOnLoadMoreListener(this, recyclerView);
        recyclerView.setAdapter(adapter);

        mSwipeRefreshLayout.setOnRefreshListener(this);

        recyclerView.post(new Runnable() {
            @Override
            public void run() {
                recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(RecyclerView recyclerView, final int newState) {
                        super.onScrollStateChanged(recyclerView, newState);
                        if (newState == RecyclerView.SCROLL_STATE_DRAGGING || newState == RecyclerView.SCROLL_STATE_SETTLING) {
                            sIsScrolling = true;
                            glide.pauseRequests();
                        } else if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                            if (sIsScrolling) {
                                glide.resumeRequests();
                            }
                            sIsScrolling = false;
                        }
                    }
                });
            }
        });

        setObserverEventBus();

        setObserverCache();

        registerEvents();
    }

    @Override
    protected void setUserVisibleHint() {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mLikeView != null)
            mLikeView.reset();
        if (disposables != null)
            disposables.dispose();
    }

    //----------------------------------------------------------------
    //------------------------ Loading -------------------------------
    //----------------------------------------------------------------

    @Override
    public void onLoadMoreRequested() {
        requestMoreList();
    }

    //------------------------------------------------------------
    //-------------------Adapter listener ------------------------
    //------------------------------------------------------------
    @Override
    public void onChoiceShareSuccess(int position, int type) {
        if (type == PopupShare.TYPE_SHARE_WALL) {
            Snackbar.make(Objects.requireNonNull(getView()), "Share wall", Snackbar.LENGTH_SHORT);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDisplayImageDetailScreen(BuzzBean bean, int positionTimeLineAdapter, int positionIndexItem, View view) {
        TimelineMediaActivity.launch((AppCompatActivity) getActivity(), view, bean, positionIndexItem);
    }

    @Override
    public void onDisplayLiveStreamScreen(BuzzBean item, int positionTimeLineAdapter, int positionIndexItem, View view) {
        if (activity instanceof BaseActivity) {
            ListBuzzChild buzzChild = item.listChildBuzzes.get(0);
            if (buzzChild.streamStatus.equals(Constants.LIVE_STREAM_ON))
                ((BaseActivity) activity).onLiveStreamOption(UserLiveStreamService.Mode.VIEW, item.buzzId, item.shareId, item.listChildBuzzes.get(0).thumbnailUrl);
            else
                TimelineMediaActivity.launch((AppCompatActivity) getActivity(), view, item, positionIndexItem);
        }
    }

    @Override
    public void onDisplayCommentScreen(BuzzBean bean, int position, View view) {
        LogUtils.e(TAG, bean.buzzId);
        CommentActivity.launch((AppCompatActivity) getActivity(), view, bean.buzzId, bean.userId, bean.like.isLike, typeView, ActivityResultRequestCode.REQUEST_BUZZ_DETAILS);
    }

    @Override
    public void onLike(BuzzBean bean, int position, View view) {
        int likeType = Constants.BUZZ_LIKE_TYPE_LIKE;
        if (bean.like.isLike == Constants.BUZZ_LIKE_TYPE_LIKE)
            likeType = Constants.BUZZ_LIKE_TYPE_UNLIKE;

        LikeBuzzRequest likeBuzzRequest = new LikeBuzzRequest(UserPreferences.getInstance().getToken(), bean.buzzId, likeType);
        getPresenter().setLike(likeBuzzRequest, bean.buzzId, view);
    }

    @Override
    public void onShare(BuzzBean bean, int position, View view) {
        shareFacebook(bean);
    }

    @Override
    public void onRemoveStatus(final BuzzBean bean, int position, View view) {
        new DialogMaterial.Builder(getActivity()).setStyle(Style.HEADER_WITH_NOT_HEADER)
                .setContent(R.string.do_you_want_remove_buzz)
                .onPositive(R.string.common_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DeleteBuzzRequest deleteBuzzRequest = new DeleteBuzzRequest(UserPreferences.getInstance().getToken(), bean.buzzId);
                        getPresenter().requestDeleteBuzz(deleteBuzzRequest, bean.buzzId);
                    }
                }).onNegative(R.string.common_no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).show();

    }

    @Override
    public void onFavorite(BuzzBean bean, int position, View view) {
        String token = UserPreferences.getInstance().getToken();
        if (bean.isFavorite == Constants.BUZZ_TYPE_IS_FAVORITE) {
            RemoveFavoriteRequest removeFavoriteRequest = new RemoveFavoriteRequest(token, bean.userId);
            getPresenter().setUnFavorite(removeFavoriteRequest, bean.userId);
        } else {
            AddFavoriteRequest addFavoriteRequest = new AddFavoriteRequest(token,
                    bean.userId);
            getPresenter().setFavorite(addFavoriteRequest, bean.userId);
        }
    }

    @Override
    public void onApproval(BuzzBean bean, int position, View view) {
        DialogMaterial.Builder builder = new DialogMaterial.Builder(getActivity())
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

    @Override
    public void onDisplayShareAudioPlayScreen(BuzzBean bean, int position, View view) {
        ShareDetailBean shareDetail = bean.shareDetailBean;
        TimelineMediaActivity.launch((AppCompatActivity) getActivity(), view, shareDetail.listChildBuzzes, 0);
    }

    @Override
    public void onDisplayShareLiveStreamScreen(BuzzBean bean, int position, View view) {
        ShareDetailBean shareDetail = bean.shareDetailBean;
        if (shareDetail.listChildBuzzes.get(0).streamStatus.equals(Constants.LIVE_STREAM_ON)) {
            ((BaseActivity) activity).onLiveStreamOption(UserLiveStreamService.Mode.VIEW,
                    shareDetail.buzzId, shareDetail.listChildBuzzes.get(0).streamId,
                    shareDetail.listChildBuzzes.get(0).thumbnailUrl);
        } else {
            TimelineMediaActivity.launch((AppCompatActivity) getActivity(), view, shareDetail.listChildBuzzes, 0);
        }
    }

    @Override
    public void onDisplayProfileScreen(BuzzBean bean, int position, View view) {
        if (typeView == TypeView.TypeViewTimeline.TIMELINE_USER) { // You are in the profile of the current user can not move again
            LogUtils.w(TAG, "You are in the profile of the current user can not move again");
        } else {
            MyProfileActivity.launch(activity, view, bean, ActivityResultRequestCode.REQUEST_BUZZ_PROFILE, TypeView.ProfileType.COME_FROM_TIMELINE);
        }
    }

    @Override
    public void onDisplayProfileScreen(String userId, int position, View view) {
        MyProfileActivity.launch(activity, view, userId, TypeView.ProfileType.COME_FROM_TIMELINE_BY_ID);
    }

    @Override
    public void onDisplayTagFriendsScreen(ArrayList<ListTagFriendsBean> listTagFriendsBeans, int position, View view) {
        TagFriendActivity.launch(getActivity(), listTagFriendsBeans, false);
    }

    @Override
    public void onRemoveStatusTemplate(BuzzBean bean, int position, View view) {
        adapter.removeTemplate(bean.buzzId);
    }

    @Override
    public void onRetryBuzzDetailRequest(BuzzDetailRequest request, String templateId, int position, View view) {
        //TODO retry load item buzz
    }
    //End Adapter listener -----------------------------

    //----------------------------------------------------------
    //----------------------Server event -----------------------
    //----------------------------------------------------------

    @Override
    public void onBuzzDetail(BuzzDetailResponse response, String templateId) {
        BuzzBean bean = new BuzzBean();
        bean.setDataBean(response.data);
        adapter.updateTemplate(bean, templateId);

        updateCacheListBuzzResponse();
    }

    @Override
    public void onBuzzDetailError(BuzzDetailRequest listDetailRequest, String templateId) {
        adapter.updateErrorRequestBuzzDetail(listDetailRequest, templateId, true);
    }

    @Override
    public void onBuzzListResponse(BuzzListResponse response) {
        adapter.setNewData(response.data);
        //update TimelineDataBean
        updateCacheListBuzzResponse();
    }

    /**
     * This method will not be super {@link com.vn.ntsc.ui.timeline.all.TimelineAllFragment}
     *
     * @param response {@link BuzzListResponse}
     */
    @Override
    public void onLoadMoreBuzzListResponse(BuzzListResponse response) {
        adapter.addData(response.data);
        updateCacheListBuzzResponse();
    }

    /**
     * This method will not be super {@link com.vn.ntsc.ui.timeline.all.TimelineAllFragment}
     */
    @Override
    public void onLoadMoreEmpty() {
        adapter.loadMoreEmpty();
    }

    @Override
    public void onLikeResponse(LikeBuzzResponse response, String buzzId, View view) {
        updateLike(buzzId, view);
    }

    @Override
    public void onDeleteBuzzResponse(DeleteBuzzResponse response, String buzzID) {
        deleteTimeline(buzzID);
    }

    @Override
    public void onFavoriteResponse(AddFavoriteResponse response, String userId) {
        RxEventBus.publish(SubjectCode.SUBJECT_UPDATE_TIMELINE_FAVORITE, userId);
    }

    @Override
    public void onUnFavoriteResponse(RemoveFavoriteResponse response, String userId) {
        RxEventBus.publish(SubjectCode.SUBJECT_UPDATE_TIMELINE_UNFAVORITE, userId);
    }

    @Override
    public void onAddNumberShare(AddNumberShareResponse response, String buzzID) {
        RxEventBus.publish(SubjectCode.SUBJECT_UPDATE_TIMELINE_SHARE, buzzID);
    }

    @Override
    public void onGetListPublicFile(PublicFileResponse response) {
    }

    @Override
    public void onComplete() {
        if (adapter.getData().size() <= 0)
            adapter.setEmptyView(R.layout.layout_empty);
        refreshDefaultLoad();
    }

    @Override
    public void handleBuzzNotFound(String buzzID) {
        deleteTimeline(buzzID);
        Context context = getActivity();
        String message = context.getString(R.string.buzz_item_not_found);
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onEvaluateUser(EvaluateUserProfileResponse response) {
        //TODO
    }

    //----------------------------------------------------------------
    //------------------------ Function ------------------------------
    //----------------------------------------------------------------

    public void updateCacheListBuzzResponse() {
        if (SystemUtils.isNetworkConnected()) {
            BuzzListResponse buzzListResponse = new BuzzListResponse();
            buzzListResponse.data = adapter.getData();
            listBuzzSubject.onNext(buzzListResponse);
        } else {
            LogUtils.w(TAG, "Can not update cache because network not connected!");
        }
    }

    public void saveCacheTimelineDetail(BuzzBean bean) {
        BuzzDetailResponse detailResponse = new BuzzDetailResponse();
        detailResponse.data = bean;
        buzzDetailResponseSubject.onNext(detailResponse);
    }

    /**
     * update ui icon live on recyclerView
     *
     * @param buzzId
     * @param view
     */
    @SuppressLint("CheckResult")
    void updateLike(final String buzzId, final View view) {
        Observable.fromIterable(adapter.getData()).filter(new Predicate<BuzzBean>() {
            @Override
            public boolean test(BuzzBean item) throws Exception {
                if (item.buzzId.equals(buzzId)) {
                    return true;
                }

                return false;
            }
        }).forEach(new Consumer<BuzzBean>() {
            @Override
            public void accept(BuzzBean item) throws Exception {
                if (item.like.isLike == Constants.BUZZ_LIKE_TYPE_LIKE) {
                    animationLike(view, "-1");
                } else {
                    animationLike(view, "+1");
                }
                RxEventBus.publish(SubjectCode.SUBJECT_UPDATE_TIMELINE_LIKE, buzzId);
            }
        });
    }

    public void animationLike(View view, String str) {
        mLikeView.setText(str);
        mLikeView.show(view);
    }

    private void shareFacebook(final BuzzBean bean) {
        final String URL_POST = BuildConfig.SERVER_URL + "/post/";
        ShareLinkContent content = new ShareLinkContent
                .Builder()
                .setShareHashtag(new ShareHashtag.Builder().setHashtag("#NTSC").build())
                .setContentUrl(Uri.parse(URL_POST + bean.userId + "/" + bean.buzzId))
                .setQuote(bean.buzzId)
                .build();

        ShareDialog shareDialog = new ShareDialog(this);
        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {
                AddNumberShareRequest addNumberShareRequest = new AddNumberShareRequest(bean.buzzId, UserPreferences.getInstance().getToken());
                getPresenter().requestAddNumberShare(addNumberShareRequest, bean.buzzId);
            }

            @Override
            public void onCancel() {
                LogUtils.e(TAG, "Share onCancel: -----------------------");
            }

            @Override
            public void onError(FacebookException e) {
                LogUtils.e(TAG, "Share onError: -----------------------");
            }
        });
        shareDialog.show(content, ShareDialog.Mode.AUTOMATIC);
    }

    private void deleteTimeline(String buzzID) {
        for (int i = 0; i < adapter.getData().size(); i++) {
            if (adapter.getData().get(i).buzzId == null) {
                adapter.getData().remove(i);
            } else {
                if (adapter.getData().get(i).buzzId.equals(buzzID)) {
                    adapter.getData().remove(i);
                    break;
                }
            }
        }
        adapter.notifyDataSetChanged();

        Toast.makeText(getContext(), getResources().getString(R.string.buzz_item_delete_success), Toast.LENGTH_LONG).show();

        updateCacheListBuzzResponse();
    }

    private void requestMoreList() {
        UserPreferences userPreferences = UserPreferences.getInstance();
        String token = userPreferences.getToken();
        double longitude = 0;
        double latitude = 0;
        BuzzListRequest buzzListRequest;
        if (typeView == TypeView.TypeViewTimeline.TIMELINE_USER) {
            buzzListRequest = new BuzzListRequest(token, userInfo.userId, typeView,
                    longitude, latitude, adapter.getData().size() + 1, take);
        } else {
            buzzListRequest = new BuzzListRequest(token, null, typeView,
                    longitude, latitude, adapter.getData().size() + 1, take);
        }
        getPresenter().getMoreBuzzList(buzzListRequest, userInfo, typeView);
    }

    protected void onRequestBuzzList(int delay) {
        String token = UserPreferences.getInstance().getToken();
        double longitude = 0;
        double latitude = 0;
        BuzzListRequest buzzListRequest;
        if (typeView == TypeView.TypeViewTimeline.TIMELINE_USER) {
            buzzListRequest = new BuzzListRequest(token, userInfo.userId, typeView,
                    longitude, latitude, 0, take);
        } else {
            buzzListRequest = new BuzzListRequest(token, null, typeView,
                    longitude, latitude, 0, take);
        }

        getPresenter().getBuzzList(buzzListRequest, userInfo, typeView, delay);
    }

    protected void refreshDefaultLoad() {
        mSwipeRefreshLayout.setRefreshing(false);
        adapter.loadMoreComplete();
    }

    public void onFavorite(UserInfoResponse userInfo) {
        String token = UserPreferences.getInstance().getToken();
        if (userInfo.isFavorite == Constants.BUZZ_TYPE_IS_FAVORITE) {
            RemoveFavoriteRequest removeFavoriteRequest = new RemoveFavoriteRequest(token, userInfo.userId);
            getPresenter().setUnFavorite(removeFavoriteRequest, userInfo.userId);
        } else {
            AddFavoriteRequest addFavoriteRequest = new AddFavoriteRequest(token,
                    userInfo.userId);
            getPresenter().setFavorite(addFavoriteRequest, userInfo.userId);
        }
    }

    protected void requestBuzzDetail(String buzzId, String templateId) {
        UserPreferences userPreference = new UserPreferences();
        BuzzDetailRequest buzzDetailRequest = new BuzzDetailRequest(userPreference.getToken(), buzzId);
        getPresenter().getBuzzDetail(buzzDetailRequest, templateId);
    }

    //----------------------------------------------------------------
    //------------------- subject event bus --------------------------
    //----------------------------------------------------------------

    /**
     * track all socket events
     * Listen all message to show notification on the SnackBar
     */
    protected final void registerEvents() {
        LogUtils.i(TAG, "Register socket event");
        if (disposables == null || disposables.isDisposed())
            disposables = new CompositeDisposable();
        Disposable socketEvent = RxSocket.getSocketEvent()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<SocketEvent>() {
                    @Override
                    public void accept(SocketEvent socketEvent) throws Exception {

                        if (socketEvent == null || socketEvent.getEventType() != SocketEvent.EVENT_RECEIVE) {
                            LogUtils.w(TAG, "socket Event -------> null!");
                            return;
                        }

                        UserPreferences userPreferences = UserPreferences.getInstance();
                        if (userPreferences.getCurrentFriendChat().equals(socketEvent.getMessage().getSenderId())) {
                            //Yourself online in chat room
                            LogUtils.w(TAG, "socket Event ------->Yourself online in chat room");
                            return;
                        }

                        //Send broad cast to show notification panel if has new message from all friend if need
                        if (socketEvent.getMessage() != null && socketEvent.getMessage().getMessageType() != null) {
                            LogUtils.i(TAG, "socket Event ------->Show notification on the SnackBar: " + socketEvent.getMessage().getMessageType().intern());
                            onShowNotification(socketEvent.getMessage());
                        }

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        LogUtils.e(TAG, "throwable socket " + throwable.getMessage());
                        throwable.printStackTrace();
                    }
                });

        disposables.add(socketEvent);
    }

    private void onShowNotification(Message message) {
        if (message == null) {
            LogUtils.w(TAG, "Can't show notification using socket because Message is null");
            return;
        }

        if (message.getMessageType() == null) {
            LogUtils.w(TAG, "Can't show notification using socket because Message Type is null");
            return;
        }

        if (Message.MessageType.NOTI_BUZZ.equals(message.getMessageType())) {
            Gson gson = new Gson();
            NotificationMessage notificationMessage = gson.fromJson(message.getRawText(), NotificationMessage.class);

            if (notificationMessage != null
                    && notificationMessage.value != null
                    && notificationMessage.value.aps != null
                    && notificationMessage.value.aps.data != null
                    && notificationMessage.value.aps.data.buzzid != null) {
                if (notificationMessage.value.aps.data.notiType == NotificationType.NOTI_APPROVE_BUZZ_TEXT
                        || notificationMessage.value.aps.data.notiType == NotificationType.NOTI_APPROVED_BUZZ) {
                    for (BuzzBean buzzDataBean : adapter.getData()) {
                        if (buzzDataBean.buzzId.equals(notificationMessage.value.aps.data.buzzid)) {
                            requestBuzzDetail(buzzDataBean.buzzId, buzzDataBean.buzzId);
                            break;
                        }
                    }
                }
            }
        }
    }

    private void setObserverCache() {

        Disposable buzzDetailDisposable = buzzDetailResponseSubject
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.io())
                .subscribe(new Consumer<BuzzDetailResponse>() {
                    @Override
                    public void accept(BuzzDetailResponse buzzDetailResponse) throws Exception {
                        if (SystemUtils.isNetworkConnected())
                            CacheJson.saveObject(String.format(CacheType.CACHE_TIMELINE_DETAIL_BY_ID, buzzDetailResponse.data.buzzId), buzzDetailResponse);
                    }
                });
        disposables.add(buzzDetailDisposable);

        Disposable listBuzzBenDisposable = listBuzzSubject
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.io())
                .subscribe(new Consumer<BuzzListResponse>() {
                    @Override
                    public void accept(BuzzListResponse buzzListResponse) throws Exception {
                        if (SystemUtils.isNetworkConnected())
                            if (typeView == TIMELINE_ALL) {
                                CacheJson.saveObject(CacheType.CACHE_TIMELINE_ALL, buzzListResponse);
                            } else if (typeView == TypeView.TypeViewTimeline.TIMELINE_FAVORITES) {
                                CacheJson.saveObject(CacheType.CACHE_TIMELINE_FAVORITE, buzzListResponse);
                            } else if (typeView == TypeView.TypeViewTimeline.TIMELINE_USER) {
                                CacheJson.saveObject(String.format(CacheType.CACHE_TIMELINE_USER_ID, userInfo.userId), buzzListResponse);
                            }
                    }
                });
        disposables.add(listBuzzBenDisposable);

        Disposable listBuzzLiveStreamDisposable = listBuzzLiveStreamSubject
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.io())
                .subscribe(new Consumer<BuzzListResponse>() {
                    @Override
                    public void accept(BuzzListResponse buzzListResponse) throws Exception {
                        if (SystemUtils.isNetworkConnected())
                            if (typeView == TIMELINE_ALL) {
                                CacheJson.saveObject(CacheType.CACHE_TIMELINE_LIVE_STREAM_ALL, buzzListResponse);
                            } else if (typeView == TypeView.TypeViewTimeline.TIMELINE_FAVORITES) {
                                CacheJson.saveObject(CacheType.CACHE_TIMELINE_LIVE_STREAM_FAVORITE, buzzListResponse);
                            }
                    }
                });

        disposables.add(listBuzzLiveStreamDisposable);

        Disposable listPublicImagesDisposable = listPublicFiles
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.io())
                .subscribe(new Consumer<PublicFileResponse>() {
                    @Override
                    public void accept(PublicFileResponse listPublicImageResponse) throws Exception {
                        if (SystemUtils.isNetworkConnected())
                            CacheJson.saveObject(String.format(CacheType.CACHE_TIMELINE_PUBLIC_IMAGES_ID, userInfo.userId), listPublicImageResponse);
                    }
                });
        disposables.add(listPublicImagesDisposable);

        Disposable userInfoDisposable = userInfoSubject
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.io())
                .subscribe(new Consumer<UserInfoResponse>() {
                    @Override
                    public void accept(UserInfoResponse userInfoResponse) throws Exception {
                        if (SystemUtils.isNetworkConnected()) {
                            GetUserInfoResponse getUserInfoResponse = new GetUserInfoResponse();
                            getUserInfoResponse.data = userInfoResponse;
                            CacheJson.saveObject(String.format(CacheType.CACHE_TIMELINE_USER_INFO_ID, userInfoResponse.userId), getUserInfoResponse);
                        }
                    }
                });
        disposables.add(userInfoDisposable);
    }

    protected void setObserverEventBus() {
        RxEventBus.subscribe(SubjectCode.SUBJECT_UPDATE_TIMELINE_FROM_LIVE_STREAM, this, new Consumer<Object>() {
            @Override
            public void accept(Object buzzBean) throws Exception {
                if (buzzBean != null)
                    if (buzzBean instanceof BuzzBean) {
                        adapter.addFirst((BuzzBean) buzzBean);
                        updateCacheListBuzzResponse();
                    }
            }
        });

        RxEventBus.subscribe(SubjectCode.SUBJECT_UPDATE_TIMELINE, this, new Consumer<Object>() {

            @Override
            public void accept(Object buzzBean) throws Exception {
                if (buzzBean != null) {
                    BuzzBean item = (BuzzBean) buzzBean;
                    LogUtils.d(TAG, "-------------------> SUBJECT_UPDATE_TIMELINE");
                    int index = adapter.getData().indexOf(item);
                    if (index != -1) {
                        adapter.getData().set(index, item);
                        saveCacheTimelineDetail(item);
                        adapter.notifyItemChanged(index + adapter.getHeaderLayoutCount());
                    }

                    updateCacheListBuzzResponse();
                }
            }
        });

        RxEventBus.subscribe(SubjectCode.SUBJECT_REMOVE_TIMELINE, this, new Consumer<Object>() {

            @Override
            public void accept(Object buzzId) throws Exception {
                if (buzzId != null) {
                    LogUtils.d(TAG, "-------------------> SUBJECT_REMOVE_TIMELINE");
                    for (int i = 0; i < adapter.getData().size(); i++) {
                        if (adapter.getData().get(i).buzzId.equals(buzzId.toString())) {
                            adapter.getData().remove(i);
                            adapter.notifyItemChanged(i + adapter.getHeaderLayoutCount());
                            break;
                        }
                    }

                    updateCacheListBuzzResponse();
                }
            }
        });

        RxEventBus.subscribe(SubjectCode.SUBJECT_UPDATE_TIMELINE_LIKE, this, new Consumer<Object>() {

            @Override
            public void accept(Object buzzId) throws Exception {
                if (buzzId != null) {
                    for (int i = 0; i < adapter.getData().size(); i++) {
                        if (adapter.getData().get(i).buzzId.equals(buzzId.toString())) {
                            LogUtils.d(TAG, "-------------------> SUBJECT_UPDATE_TIMELINE_LIKE");

                            adapter.getData().get(i).like.isLike = adapter.getData().get(i).like.isLike == Constants.BUZZ_LIKE_TYPE_LIKE ? Constants.BUZZ_LIKE_TYPE_UNLIKE : Constants.BUZZ_LIKE_TYPE_LIKE;
                            if (adapter.getData().get(i).like.isLike == Constants.BUZZ_LIKE_TYPE_LIKE) {
                                ++adapter.getData().get(i).like.likeNumber;
                            } else {
                                if (adapter.getData().get(i).like.likeNumber > 0)
                                    --adapter.getData().get(i).like.likeNumber;
                            }

                            adapter.notifyItemChanged(i + adapter.getHeaderLayoutCount());
                            break;
                        }
                    }

                    updateCacheListBuzzResponse();
                }
            }
        });

        RxEventBus.subscribe(SubjectCode.SUBJECT_UPDATE_TIMELINE_SHARE, this, new Consumer<Object>() {

            @Override
            public void accept(Object buzzId) throws Exception {
                if (buzzId != null) {
                    for (int i = 0; i < adapter.getData().size(); i++) {
                        if (adapter.getData().get(i).buzzId.equals(buzzId.toString())) {
                            LogUtils.d(TAG, "-------------------> SUBJECT_UPDATE_TIMELINE_LIKE");
                            ++adapter.getData().get(i).shareNumber;
                            adapter.notifyItemChanged(i + adapter.getHeaderLayoutCount());
                            break;
                        }
                    }

                    updateCacheListBuzzResponse();
                }
            }
        });

        RxEventBus.subscribe(SubjectCode.SUBJECT_UPDATE_TIMELINE_FAVORITE, this, new Consumer<Object>() {

            @Override
            public void accept(Object userId) throws Exception {
                if (userId != null) {
                    ListIterator listIterator = adapter.getData().listIterator();
                    while (listIterator.hasNext()) {
                        BuzzBean buzzDataBean = (BuzzBean) listIterator.next();
                        if (buzzDataBean.userId.equals(userId.toString())) {
                            LogUtils.d(TAG, "-------------------> SUBJECT_UPDATE_TIMELINE_FAVORITE");
                            buzzDataBean.isFavorite = Constants.BUZZ_TYPE_IS_FAVORITE;
                        }
                    }
                    adapter.notifyDataSetChanged();

                    updateCacheListBuzzResponse();
                }
            }
        });

        RxEventBus.subscribe(SubjectCode.SUBJECT_UPDATE_TIMELINE_UNFAVORITE, this, new Consumer<Object>() {

            @Override
            public void accept(Object userId) throws Exception {
                if (userId != null) {
                    ListIterator listIterator = adapter.getData().listIterator();
                    while (listIterator.hasNext()) {
                        BuzzBean buzzDataBean = (BuzzBean) listIterator.next();
                        if (buzzDataBean.userId.equals(userId.toString())) {
                            LogUtils.d(TAG, "-------------------> SUBJECT_UPDATE_TIMELINE_UNFAVORITE");
                            buzzDataBean.isFavorite = Constants.BUZZ_TYPE_IS_NOT_FAVORITE;
                        }
                    }
                    adapter.notifyDataSetChanged();

                    updateCacheListBuzzResponse();
                }
            }
        });
    }

    //----------------------------------------------------------------
    //------------------------ Abstract ------------------------------
    //----------------------------------------------------------------

    protected void onAddTemplate(BuzzBean bean) {
    }

    @Override
    public void onTimelineLiveStreamEmptyView() {
    }
}

