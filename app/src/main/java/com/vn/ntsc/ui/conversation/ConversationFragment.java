package com.vn.ntsc.ui.conversation;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.tux.socket.models.Gift;
import com.tux.socket.models.Media;
import com.tux.socket.models.MessageDeliveringState;
import com.tux.socket.models.SocketEvent;
import com.tux.socket.models.Sticker;
import com.tux.socket.models.Text;
import com.vn.ntsc.R;
import com.vn.ntsc.core.views.BaseFragment;
import com.vn.ntsc.repository.TypeView;
import com.vn.ntsc.repository.model.conversation.ConversationItem;
import com.vn.ntsc.repository.model.conversation.ConversationRequest;
import com.vn.ntsc.repository.model.conversation.ConversationResponse;
import com.vn.ntsc.repository.model.conversation.DelConversationRequest;
import com.vn.ntsc.repository.model.conversation.MarkReadAllRequest;
import com.vn.ntsc.repository.model.user.UserInfoResponse;
import com.vn.ntsc.repository.preferece.UserPreferences;
import com.vn.ntsc.ui.chat.ChatActivity;
import com.vn.ntsc.ui.main.MainActivity;
import com.vn.ntsc.ui.profile.my.MyProfileActivity;
import com.vn.ntsc.utils.Constants;
import com.vn.ntsc.utils.Utils;
import com.vn.ntsc.widget.adapter.MultifunctionAdapter;
import com.vn.ntsc.widget.decoration.SpacesItemDecoration;
import com.vn.ntsc.widget.socket.RxSocket;
import com.vn.ntsc.widget.views.textview.EmoTextView;
import com.vn.ntsc.widget.views.textview.GlideImageGetter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by nankai on 8/8/2017.
 */

public class ConversationFragment extends BaseFragment<ConversationPresenter> implements ConversationContract.View, SwipeRefreshLayout.OnRefreshListener, ConversationAdapter.IOnRecyclerViewEvent, MultifunctionAdapter.RequestLoadMoreListener, RecyclerView.RecyclerListener {

    public static final int TAKE_DEFAULT = 20;

    @BindView(R.id.fragment_conversation_swipe_refresh_layout)
    SwipeRefreshLayout mRefreshLayout;

    @BindView(R.id.fragment_conversation_list_message)
    RecyclerView listMessage;

    @BindView(R.id.fragment_conversation_read_all)
    TextView readAll;

    @BindView(R.id.fragment_conversation_delete)
    TextView delete;

    @BindView(R.id.fragment_conversation_delete_all)
    TextView deleteAll;

    @BindView(R.id.fragment_conversation_img_delete_all)
    ImageView imageDelAll;

    @BindView(R.id.fragment_conversation_img_delete)
    ImageView imageDel;

    @BindView(R.id.fragment_conversation_img_read_all)
    ImageView imageReadAll;


    private ConversationAdapter conversationAdapter;
    /**
     * true: hide read all, delete all
     */
    private boolean isEnableDelete;

    private Disposable socketEvent;

    public static ConversationFragment newInstance() {
        Bundle args = new Bundle();
        ConversationFragment fragment = new ConversationFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_conversation;
    }

    @Override
    protected void onCreateView(View rootView, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getModulesCommonComponent().inject(this);
        mRefreshLayout.setRefreshing(true);
        mRefreshLayout.setOnRefreshListener(this);

        conversationAdapter = new ConversationAdapter(this);
        conversationAdapter.setEventListener(this);
        conversationAdapter.setEnableLoadMore(true);
        conversationAdapter.setOnLoadMoreListener(this, listMessage);

        listMessage.setLayoutManager(new LinearLayoutManager(getContext()));
        listMessage.setHasFixedSize(true);
        //1dp as px, value might be obtained e.g. from dimen resources...
        int space = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics());
        listMessage.addItemDecoration(new SpacesItemDecoration(space));

        listMessage.setAdapter(conversationAdapter);
        listMessage.setRecyclerListener(this);
    }


    @Override
    public void onResume() {
        super.onResume();
        if (!isFirstLoad())
            onRefresh();
    }

    @Override
    protected void setUserVisibleHint() {
        registerEvents();
        onRefresh();
    }

    @Override
    public void onRefresh() {
        ConversationRequest conversationRequest = new ConversationRequest("", UserPreferences.getInstance().getToken(), TAKE_DEFAULT);
        getPresenter().requestListConversation(conversationRequest);
    }

    @Override
    public void onLoadMoreRequested() {
        String sentTime = "";
        if (conversationAdapter != null) {
            ConversationItem lastItem = conversationAdapter.getLastItem();
            sentTime = lastItem.sentTime;
        }
        ConversationRequest conversationRequest = new ConversationRequest(sentTime, UserPreferences.getInstance().getToken(), TAKE_DEFAULT);
        getPresenter().requestLoadMoreListConversation(conversationRequest);
    }

    @OnClick(R.id.fragment_conversation_read_all)
    void onMarkAllMessageAsRead() {
        showConfirmDialog(R.string.mark_all_mesage_as_read_confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                List<String> friendsId = new ArrayList<String>();
                List<ConversationItem> listMessage = conversationAdapter.getData();
                for (ConversationItem item : listMessage) {
                    if (item.unreadNum != 0) {
                        friendsId.add(item.friendId);
                    }
                }
                MarkReadAllRequest request = new MarkReadAllRequest(friendsId, UserPreferences.getInstance().getToken());
                getPresenter().markAllReadConversations(request);
            }
        });
    }

    @OnClick(R.id.fragment_conversation_delete)
    void onDeleteMessage() {
        toggleDeleteHeader();
        conversationAdapter.toggleDeleteItem(isEnableDelete);
    }


    @OnClick(R.id.fragment_conversation_delete_all)
    void onDeleteAllMessage() {

        if (conversationAdapter.getData().size() > 0) {
            // show confirm before delete all
            showConfirmDialog(R.string.delete_all_message_confirm, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    //Mark all friend_id to List to request delete all
                    List<String> dels = new ArrayList<String>();
                    for (ConversationItem item : conversationAdapter.getData()) {
                        dels.add(item.friendId);
                    }
                    //Clear Data List of Adapter
                    conversationAdapter.deleteAll();
                    DelConversationRequest request = new DelConversationRequest(dels, UserPreferences.getInstance().getToken());
                    getPresenter().requestDelAllConversation(request);
                }
            });
        }
    }

    @Override
    public void onConversationsSuccess(ConversationResponse response) {
        conversationAdapter.setNewData(response.datas);
    }

    @Override
    public void onLoadMoreConversationsSuccess(ConversationResponse response) {
        if (response.datas.size() <= 0) {
            if (conversationAdapter.getData().size() >= TAKE_DEFAULT) {
                conversationAdapter.loadMoreEmpty();
            } else {
                conversationAdapter.loadMoreEnd();
            }
        } else {
            conversationAdapter.addData(response.datas);
        }
    }

    @Override
    public void loadComplete() {
        mRefreshLayout.setRefreshing(false);
        conversationAdapter.loadMoreComplete();

        if (conversationAdapter.getData().isEmpty())
            conversationAdapter.setEmptyView(R.layout.layout_empty);
    }


    @Override
    public void onDelConversationClick(ConversationItem conversationItem, int position) {
        List<String> dels = new ArrayList<>();
        dels.add(conversationItem.friendId);
        DelConversationRequest request = new DelConversationRequest(dels, UserPreferences.getInstance().getToken());
        getPresenter().requestDelConversation(request, position);
    }

    @Override
    public void onAvatarClick(ConversationItem itemBean, View view, int position) {
        UserInfoResponse userProfileBean = new UserInfoResponse(itemBean.friendId, itemBean.gender, itemBean.name, itemBean.avaId, Constants.BUZZ_TYPE_IS_NOT_FAVORITE);
        MyProfileActivity.launch((AppCompatActivity) getActivity(), view, userProfileBean, TypeView.ProfileType.COME_FROM_OTHER);
    }

    @Override
    public void onItemMessageClick(ConversationItem conversationItem, View avatar, int position) {
        UserInfoResponse userProfileBean = new UserInfoResponse(conversationItem.friendId, conversationItem.gender, conversationItem.name, conversationItem.thumbnailUrl, Constants.BUZZ_TYPE_IS_NOT_FAVORITE);
        int unreadAllMsgApp=UserPreferences.getInstance().getNumberUnreadMessage()-conversationItem.unreadNum;
        Activity activity = getActivity();
        if (activity instanceof MainActivity) {
            UserPreferences.getInstance().saveNumberUnreadMessage(unreadAllMsgApp);
            ((MainActivity) activity).updateBadge();
        }
        ChatActivity.newInstance((AppCompatActivity) getActivity(), userProfileBean);
    }

    @Override
    public void delConversationSuccess(int positionInAdapter) {
        if (conversationAdapter != null) conversationAdapter.remove(positionInAdapter);
    }

    @Override
    public void delConversationFailure() {
        Toast.makeText(getContext(), context.getString(R.string.delete_conversation_failure), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void delAllConversationSuccess() {
        Toast.makeText(getContext(), context.getString(R.string.delete_all_conversation_success), Toast.LENGTH_SHORT).show();
        if (conversationAdapter != null) {
            conversationAdapter.deleteAll();
        }

        /* Notify unread message*/
        Activity activity = getActivity();
        if (activity instanceof MainActivity) {
            UserPreferences.getInstance().saveNumberUnreadMessage(0);
            ((MainActivity) activity).updateBadge();
        }

    }

    @Override
    public void markAllReadSuccess() {
        if (conversationAdapter != null) conversationAdapter.markAllAsRead();
        UserPreferences.getInstance().saveNumberUnreadMessage(0);
        ((MainActivity) getActivity()).updateBadge();
    }

    @Override
    public void markAllReadFailure() {
    }

    private void toggleDeleteHeader() {
        isEnableDelete = !isEnableDelete;
        int state = isEnableDelete ? View.INVISIBLE : View.VISIBLE;
        readAll.setVisibility(state);
        deleteAll.setVisibility(state);

//        imageDel.setVisibility(state);
        imageDelAll.setVisibility(state);
        imageReadAll.setVisibility(state);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unRegisterEvents();
    }


    /**
     * track all socket events
     * Update Badge from Server via API, don't use cache on local
     */
    private void registerEvents() {
        socketEvent = RxSocket.getSocketEvent()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<SocketEvent>() {
                    @Override
                    public void accept(SocketEvent socketEvent) throws Exception {
                        if (socketEvent.getEventType() != SocketEvent.EVENT_RECEIVE) return;
                        //Refresh list conversation if has new message from all friend  or socket respond for request mark read all message from client
                        if (socketEvent.getMessage() != null
                                //Must be a message sent to yourself
                                && Utils.nullToEmpty(UserPreferences.getInstance().getUserId()).equals(socketEvent.getMessage().getReceiverId())
                                //Only accept TYPE_TEXT|TYPE_GIFT|TYPE_FILE|TYPE_STICKER/EMOJI
                                && ((Text.TYPE_TEXT.equals(socketEvent.getMessage().getMessageType()))
                                || (Text.TYPE_TEXT_EMOJI.equals(socketEvent.getMessage().getMessageType()))
                                || (Gift.TYPE_GIFT.equals(socketEvent.getMessage().getMessageType()))
                                || (Media.TYPE_FILE.equals(socketEvent.getMessage().getMessageType()))
                                || (Sticker.TYPE_STICKER.equals(socketEvent.getMessage().getMessageType()))
                                || (MessageDeliveringState.TYPE_DELIVERING_STATE.equals(socketEvent.getMessage().getMessageType())
                                && MessageDeliveringState.VALUE_MARK_MESSAGE_AS_READ.equals(socketEvent.getMessage().getMessageContent()))
                        )) {

                            onRefresh();
                        }

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });
    }

    /**
     * dispose event tracker to avoid leak
     */
    private void unRegisterEvents() {
        if (socketEvent != null) socketEvent.dispose();
    }

    //========================================== Recycler view event ===============================
    @Override
    public void onItemClick(ConversationItem conversationItem, int position) {

    }

    @Override
    public void onViewRecycled(RecyclerView.ViewHolder viewHolder) {
        // clear when view is hidden
        EmoTextView target1 = null;
        EmoTextView target2 = null;
        if (viewHolder instanceof ConversationAdapter.ViewHolder) {
            ConversationAdapter.ViewHolder holder = (ConversationAdapter.ViewHolder) viewHolder;
            target1 = holder.message;
            target2 = holder.message;
        }

        GlideImageGetter.clear(target1);
        GlideImageGetter.clear(target2);
    }
}