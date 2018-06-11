package com.vn.ntsc.ui.notices.notification;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.vn.ntsc.R;
import com.vn.ntsc.core.views.BaseActivity;
import com.vn.ntsc.core.views.BaseFragment;
import com.vn.ntsc.repository.TypeView;
import com.vn.ntsc.repository.model.notification.ClickNotificationRequest;
import com.vn.ntsc.repository.model.notification.DelNotificationRequest;
import com.vn.ntsc.repository.model.notification.NotificationItem;
import com.vn.ntsc.repository.model.notification.NotificationRequest;
import com.vn.ntsc.repository.model.notification.NotificationResponse;
import com.vn.ntsc.repository.model.notification.push.NotificationType;
import com.vn.ntsc.repository.model.timeline.BuzzDetailRequest;
import com.vn.ntsc.repository.model.timeline.BuzzDetailResponse;
import com.vn.ntsc.repository.model.user.UserInfoRequest;
import com.vn.ntsc.repository.model.user.UserInfoResponse;
import com.vn.ntsc.repository.model.webview.WebViewBean;
import com.vn.ntsc.repository.preferece.UserPreferences;
import com.vn.ntsc.services.UserLiveStreamService;
import com.vn.ntsc.ui.comment.CommentActivity;
import com.vn.ntsc.ui.profile.detail.ProfileDetailActivity;
import com.vn.ntsc.ui.profile.my.MyProfileActivity;
import com.vn.ntsc.ui.webview.WebViewActivity;
import com.vn.ntsc.widget.adapter.MultifunctionAdapter;
import com.vn.ntsc.widget.decoration.SpacesItemDecoration;

import butterknife.BindView;

/**
 * Created by ThoNh on 8/30/2017.
 */

public class NotificationFragment extends BaseFragment<NotificationPresenter> implements NotificationContract.View, NotificationAdapter.INotificationAdapterEvent, SwipeRefreshLayout.OnRefreshListener, MultifunctionAdapter.RequestLoadMoreListener {

    /*If request server with Flag REQUEST_TYPE_REFRESH --> adapter clear old data, add new Data*/
    private static final int REQUEST_TYPE_REFRESH = 0;
    /*If request server with Flag REQUEST_TYPE_LOADMORE --> adapter append data*/
    private static final int REQUEST_TYPE_LOADMORE = 1;

    private static final int TAKE = 20;

    public static NotificationFragment newInstance() {
        Bundle args = new Bundle();
        NotificationFragment fragment = new NotificationFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @BindView(R.id.fragment_notification_swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @BindView(R.id.fragment_notification_list_notification)
    RecyclerView mListNotification;
    private NotificationAdapter mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_notification;
    }

    @Override
    protected void onCreateView(View rootView, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getModulesCommonComponent().inject(this);

        mSwipeRefreshLayout.setRefreshing(true);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mAdapter = new NotificationAdapter();
        mAdapter.setEventListener(this);
        mAdapter.setEnableLoadMore(true);
        mAdapter.setOnLoadMoreListener(this, mListNotification);

        mListNotification.setLayoutManager(new LinearLayoutManager(getContext()));
        mListNotification.setHasFixedSize(true);
        //1dp as px, value might be obtained e.g. from dimen resources...
        int space = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics());
        mListNotification.addItemDecoration(new SpacesItemDecoration(space));

        mListNotification.setAdapter(mAdapter);

        // Swipe Item recyclerView will be request Delete notification
        // If Request Success, responseCode = 0 , remove Item from recyclerView
        ItemTouchHelper recyclerViewItemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {

                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                if (direction == ItemTouchHelper.LEFT) {

                    if (mAdapter != null) {
                        int position = viewHolder.getAdapterPosition();
                        String notiId = mAdapter.getData().get(position).notiId;
                        String token = UserPreferences.getInstance().getToken();
                        DelNotificationRequest delNotificationRequest = new DelNotificationRequest(notiId, token);
                        getPresenter().delNotification(delNotificationRequest, position);
                    }
                }
            }
        });
        // attach swipeHelper to RecyclerView
        recyclerViewItemTouchHelper.attachToRecyclerView(mListNotification);
    }

    @Override
    protected void setUserVisibleHint() {
        // First request, timeStamp = ""
        NotificationRequest request = new NotificationRequest(UserPreferences.getInstance().getToken(), TAKE);
        getPresenter().getNotifications(request, REQUEST_TYPE_REFRESH);
    }

    @Override
    public void onRefresh() {
        NotificationRequest request = new NotificationRequest(UserPreferences.getInstance().getToken(), TAKE);
        getPresenter().getNotifications(request, REQUEST_TYPE_REFRESH);
    }

    @Override
    public void onLoadMoreRequested() {
        String timeStamp = mAdapter.getLastItem().time;
        NotificationRequest request = new NotificationRequest(timeStamp, UserPreferences.getInstance().getToken(), TAKE);
        getPresenter().getNotifications(request, REQUEST_TYPE_LOADMORE);
    }

    @Override
    public void getNotificationSuccess(NotificationResponse response, int requestType) {
        mAdapter.loadMoreComplete();

        if (requestType == REQUEST_TYPE_REFRESH) {
            if (!response.data.isEmpty()) {
                mAdapter.getData().clear();
                mAdapter.setNewData(response.data);
            }
        }

        if (requestType == REQUEST_TYPE_LOADMORE) {
            if (!response.data.isEmpty()) {
                mAdapter.addData(response.data);
            } else {
                mAdapter.loadMoreEmpty();
            }
        }
    }

    @Override
    public void getNotificationFailure() {

    }

    @Override
    public void markReadNotificationSuccess(int position) {
        mAdapter.markReadNotification(position);
    }

    @Override
    public void delNotificationSuccess(int position) {
        mAdapter.remove(position);
        mAdapter.notifyItemChanged(position);
    }

    @Override
    public void delNotificationFailure() {

    }

    @Override
    public void getFriendsInfoSuccess(UserInfoResponse itemBean, View view, int position) {
        if (itemBean != null) {
            UserInfoResponse userProfileBean = new UserInfoResponse(itemBean.userId, itemBean.gender, itemBean.userName, itemBean.avatar, itemBean.isFavorite);
            MyProfileActivity.launch((AppCompatActivity) getActivity(), view, userProfileBean, TypeView.ProfileType.COME_FROM_OTHER);
        } else {
            Toast.makeText(getContext(), R.string.user_not_found, Toast.LENGTH_LONG).show();
            delNotifyRequest(position);
        }
    }

    //=========================== Notify Chat --> kiểm tra xem buzz bị xóa chưa
    @Override
    public void onBuzzDetailFromNotificationId(BuzzDetailResponse response) {
        CommentActivity.launch((AppCompatActivity) getActivity(), mSwipeRefreshLayout, response.data.buzzId, response.data.userId);
    }

    @Override
    public void onCompleted() {
        mSwipeRefreshLayout.setRefreshing(false);
        mAdapter.loadMoreComplete();
        if (mAdapter.getData().isEmpty()) {
            mAdapter.setEmptyView(R.layout.layout_empty);
        }
    }

    @Override
    public void onBuzzNotFoundFromNotificationId(int position) {
        Toast.makeText(getContext(), R.string.buzz_item_not_found, Toast.LENGTH_SHORT).show();
        delNotifyRequest(position);
    }

    @Override
    public void onNonReadNotification() {

    }

    //===========================================================================================

    @Override
    public void onItemClick(View view, NotificationItem item, int position) {
        onRequestClickNotification(item, position);
        switch (item.notiType) {
            case NotificationType.NOTI_LIKE_BUZZ:
            case NotificationType.NOTI_LIKE_OTHER_BUZZ:
            case NotificationType.NOTI_COMMENT_BUZZ:
            case NotificationType.NOTI_COMMENT_OTHER_BUZZ:
            case NotificationType.NOTI_APPROVED_BUZZ:
            case NotificationType.NOTI_FAVORITED_CREATE_BUZZ:
            case NotificationType.NOTI_APPROVE_BUZZ_TEXT:
            case NotificationType.NOTI_REPLY_YOUR_COMMENT:
            case NotificationType.NOTI_APPROVE_COMMENT:
            case NotificationType.NOTI_DENIED_COMMENT:
            case NotificationType.NOTI_APPROVE_SUB_COMMENT:
            case NotificationType.NOTI_DENI_SUB_COMMENT:
            case NotificationType.NOTI_DENIED_BUZZ_IMAGE:
            case NotificationType.NOTI_DENIED_BUZZ_TEXT:
            case NotificationType.NOTI_TAG_BUZZ:
                BuzzDetailRequest requestBuzzDetail = new BuzzDetailRequest(UserPreferences.getInstance().getToken(), item.notifyBuzzId);
                getPresenter().getBuzzFromNotificationId(requestBuzzDetail, position);
                break;
            case NotificationType.NOTI_CHECK_OUT_UNLOCK:
            case NotificationType.NOTI_FAVORITED_UNLOCK:
            case NotificationType.NOTI_UNLOCK_BACKSTAGE:
            case NotificationType.NOTI_FRIEND:
            case NotificationType.NOTI_APPROVE_USERINFO:
            case NotificationType.NOTI_DENIED_USERINFO:
            case NotificationType.NOTI_APART_OF_USERINFO:
                ProfileDetailActivity.launch((AppCompatActivity) getContext(), item.notiUserId, view);
                break;
            case NotificationType.NOTI_DAYLY_BONUS:
                WebViewActivity.launch(((AppCompatActivity) getActivity()), TypeView.PageTypeWebView.PAGE_TYPE_BUY_POINT);
                break;
            case NotificationType.NOTI_BACKSTAGE_APPROVED:
                //TODO
                break;
            case NotificationType.NOTI_DENIED_BACKSTAGE:
//                //TODO
                break;
            case NotificationType.NOTI_FROM_FREE_PAGE:
                String url = item.url;
                WebViewBean bean = new WebViewBean(TypeView.PageTypeWebView.PAGE_TYPE_WEB_VIEW, url, item.content, "");
                WebViewActivity.launch(((AppCompatActivity) getActivity()), bean);
                break;
            case NotificationType.NOTI_ONLINE_ALERT:
                if (item.notiUserId == null) {
                    Toast.makeText(getContext(), R.string.user_not_found, Toast.LENGTH_LONG).show();
                    delNotifyRequest(position);
                } else {
                    UserInfoRequest userInfoRequest = new UserInfoRequest(UserPreferences.getInstance().getToken(), item.notiUserId);
                    getPresenter().requestFriendsInfo(userInfoRequest, view, position);
                }
                break;
            case NotificationType.NOTI_LIVESTREAM_FROM_FAVOURIST:
            case NotificationType.NOTI_TAG_LIVESTREAM_FROM_FAVOURIST:
                if (item.streamStatus == 0) {
                    BuzzDetailRequest requestBuzzStreamDetail = new BuzzDetailRequest(UserPreferences.getInstance().getToken(), item.notifyBuzzId);
                    getPresenter().getBuzzFromNotificationId(requestBuzzStreamDetail, position);
                } else {
                    ((BaseActivity) activity).onLiveStreamOption(UserLiveStreamService.Mode.VIEW, item.notifyBuzzId, item.streamId, "");
                }
                break;
            default:
                break;
        }

    }

    private void onRequestClickNotification(NotificationItem item, int position) {
        ClickNotificationRequest clickBuzzNotificationRequest =
                new ClickNotificationRequest(UserPreferences.getInstance().getToken(), item.notiId, item.notiUserId);
        getPresenter().markReadNotification(clickBuzzNotificationRequest, position);
    }

    private void delNotifyRequest(int position) {
        String notifyId = mAdapter.getData().get(position).notiId;
        String token = UserPreferences.getInstance().getToken();
        DelNotificationRequest delNotificationRequest = new DelNotificationRequest(notifyId, token);
        getPresenter().delNotification(delNotificationRequest, position);
    }

}
