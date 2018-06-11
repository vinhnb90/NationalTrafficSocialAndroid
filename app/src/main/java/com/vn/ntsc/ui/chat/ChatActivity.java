package com.vn.ntsc.ui.chat;

import android.Manifest;
import android.content.ComponentCallbacks2;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Parcelable;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.tux.mylab.MediaPickerBaseActivity;
import com.example.tux.mylab.camera.Camera;
import com.example.tux.mylab.camera.cameraview.CameraView;
import com.example.tux.mylab.gallery.Gallery;
import com.example.tux.mylab.gallery.data.MediaFile;
import com.example.tux.mylab.utils.MediaSanUtils;
import com.google.gson.Gson;
import com.nankai.designlayout.dialog.DialogMaterial;
import com.nankai.designlayout.dialog.enums.Style;
import com.nankai.designlayout.typing.LoadingDots;
import com.tux.socket.models.Media;
import com.tux.socket.models.Message;
import com.tux.socket.models.MessageDeliveringState;
import com.tux.socket.models.SocketEvent;
import com.tux.socket.models.Sticker;
import com.tux.socket.models.Text;
import com.tux.socket.models.Typing;
import com.vn.ntsc.BuildConfig;
import com.vn.ntsc.R;
import com.vn.ntsc.core.views.BaseActivity;
import com.vn.ntsc.repository.ActivityResultRequestCode;
import com.vn.ntsc.repository.model.block.addblock.AddBlockUserRequest;
import com.vn.ntsc.repository.model.chat.ChatHistoryRequest;
import com.vn.ntsc.repository.model.chat.ChatMessage;
import com.vn.ntsc.repository.model.chat.sql.DatabaseHelper;
import com.vn.ntsc.repository.model.emoji.EmojiReponse;
import com.vn.ntsc.repository.model.favorite.AddFavoriteRequest;
import com.vn.ntsc.repository.model.favorite.RemoveFavoriteRequest;
import com.vn.ntsc.repository.model.gift.Gift;
import com.vn.ntsc.repository.model.media.MediaEntity;
import com.vn.ntsc.repository.model.notification.push.NotificationAps;
import com.vn.ntsc.repository.model.report.ReportRequest;
import com.vn.ntsc.repository.model.user.UserInfoRequest;
import com.vn.ntsc.repository.model.user.UserInfoResponse;
import com.vn.ntsc.repository.preferece.Preferences;
import com.vn.ntsc.repository.preferece.UserPreferences;
import com.vn.ntsc.services.uploadFileChat.UploadFileService;
import com.vn.ntsc.ui.chat.adapter.ChatAdapter;
import com.vn.ntsc.ui.chat.adapter.ChatMessageType;
import com.vn.ntsc.ui.chat.adapter.holder.BaseChatHolder;
import com.vn.ntsc.ui.chat.adapter.holder.MessageReceiverHolder;
import com.vn.ntsc.ui.chat.adapter.holder.MessageSendHolder;
import com.vn.ntsc.ui.chat.audio.RecordFragment;
import com.vn.ntsc.ui.chat.listener.AudioSendListener;
import com.vn.ntsc.ui.chat.listener.OnEmojiClickListener;
import com.vn.ntsc.ui.chat.listener.OnMediaPanelListener;
import com.vn.ntsc.ui.chat.listener.OnSendAudioListener;
import com.vn.ntsc.ui.chat.listener.OnStickerClickListener;
import com.vn.ntsc.ui.chat.media.EmojiStickerFragment;
import com.vn.ntsc.ui.chat.media.MediaFragment;
import com.vn.ntsc.ui.chat.meidiadetail.ChatMediaDetailActivity;
import com.vn.ntsc.ui.gift.GiftActivity;
import com.vn.ntsc.ui.main.MainActivity;
import com.vn.ntsc.ui.onlinealert.ManageOnlineAlertActivity;
import com.vn.ntsc.utils.Constants;
import com.vn.ntsc.utils.DimensionUtils;
import com.vn.ntsc.utils.LogUtils;
import com.vn.ntsc.utils.SystemUtils;
import com.vn.ntsc.utils.Utils;
import com.vn.ntsc.utils.chats.ChatUtils;
import com.vn.ntsc.utils.keyboard.KeyboardHeightObserver;
import com.vn.ntsc.utils.keyboard.KeyboardHeightProvider;
import com.vn.ntsc.widget.eventbus.RxEventBus;
import com.vn.ntsc.widget.eventbus.SubjectCode;
import com.vn.ntsc.widget.socket.RxSocket;
import com.vn.ntsc.widget.toolbar.ToolbarButtonRightClickListener;
import com.vn.ntsc.widget.toolbar.ToolbarTitleCenter;
import com.vn.ntsc.widget.views.edittext.EmoEditext;
import com.vn.ntsc.widget.views.popup.mymore.MyMoreLayout;
import com.vn.ntsc.widget.views.popup.mymore.OnMoreListener;
import com.vn.ntsc.widget.views.textview.EmoTextView;
import com.vn.ntsc.widget.views.textview.GlideImageGetter;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.functions.Consumer;

import static android.support.v7.widget.RecyclerView.SCROLL_STATE_DRAGGING;
import static android.support.v7.widget.RecyclerView.SCROLL_STATE_IDLE;
import static android.support.v7.widget.RecyclerView.SCROLL_STATE_SETTLING;
import static com.tux.socket.models.Gift.TYPE_GIFT;
import static com.tux.socket.models.Media.TYPE_FILE;
import static com.tux.socket.models.MessageDeliveringState.TYPE_DELIVERING_STATE;
import static com.tux.socket.models.Sticker.TYPE_STICKER;
import static com.tux.socket.models.Text.TYPE_TEXT;
import static com.tux.socket.models.Typing.TYPE_WRITING;
import static com.vn.ntsc.utils.time.TimeUtils.getTimeInLocale;

/**
 * Created by dev22 on 8/21/17.
 * chat activity include: text, emoji, video, gift
 */
public class ChatActivity extends BaseActivity<ChatPresenter> implements ChatContract.View
        , OnStickerClickListener, OnMediaPanelListener
        , TextWatcher
        , SwipeRefreshLayout.OnRefreshListener
        , OnEmojiClickListener
        , KeyboardHeightObserver
        , BaseChatHolder.MessageOnEventListener
        , OnSendAudioListener, RecyclerView.RecyclerListener, ToolbarButtonRightClickListener {

    AudioSendListener events;
    /**
     * store sending message for save error message by id
     *
     * @see #onDestroy()
     */
    private List<Message> sendingMessageList = new ArrayList<>();
    private String userId;

    public void setListener(AudioSendListener events) {
        this.events = events;
    }

    public static final String TAG = ChatActivity.class.getSimpleName();
    public static final String EXTRA_USR_PROFILE_BEAN = "EXTRA_USR_PROFILE_BEAN";
    private static final String ELEMENT_CHAT = "chat_element";
    //stop typing in 5s, if user no action
    private static final long DELAY_STOP_TYPING = 5000;
    public static final int DELAY = 100;
    public static final int REQUEST = 112;
    public static final int LIMIT_PIC = 20;

    private boolean sIsScrolling;
    KeyboardHeightProvider mKeyboardHeightProvider;
    //count down for stop typing
    private CountDownTimer typingCountDownTimer;
    private UserInfoResponse mUserProfileBean;
    private MyMoreLayout mMoreOptionView;
    private PopupWindow mMoreOptionPopup;
    private ChatAdapter mAdapter;
    @Inject
    RxSocket rxSocket;
    private boolean isChatActive;
    private EmojiStickerFragment emojiStickerFragment;
    private MediaFragment mediaFragment;
    private RecordFragment recordFragment;

    // store last time send typing
    private long lastTypingTime = -1;

    @BindView(R.id.activity_chat_swipe_refresh_history_chat)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @BindView(R.id.activity_chat_rv_chat)
    public RecyclerView mLstChat;

    @BindView(R.id.activity_chat_scrim_view)
    View mScrimView;

    @BindView(R.id.toolbar)
    ToolbarTitleCenter mToolbar;

    @BindView(R.id.activity_chat_imv_add)
    ImageView mImvAdd;

    @BindView(R.id.activity_chat_imv_face)
    ImageView mImvFace;

    @BindView(R.id.activity_chat_edt_msg)
    EmoEditext mEdtMsg;

    @BindView(R.id.activity_chat_imv_send)
    ImageView mImvSend;

    @BindView(R.id.activity_chat_container_bottom_file)
    FrameLayout mFrameLayout;

    @BindView(R.id.activity_chat_keyboard_virtual_frame)
    View mKeyboardVirtualFrameLayout;

    @BindView(R.id.activity_chat_panel_bottom)
    RelativeLayout mInputChatBottomLayout;

    @BindView(R.id.activity_chat_root)
    View mRootView;

    @BindView(R.id.activity_chat_tv_typing)
    LoadingDots loadingDots;

    // Start Chat Activity
    public static void newInstance(Context context, NotificationAps notificationAps) {
        if (notificationAps == null || Utils.isEmptyOrNull(notificationAps.data.userid))
            return;
        UserInfoResponse userProfileBean = new UserInfoResponse(notificationAps.data.userid, notificationAps.alert.logArgs[0]);
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra(EXTRA_USR_PROFILE_BEAN, userProfileBean);
        context.startActivity(intent);
    }

    // start activity
    public static void newInstance(AppCompatActivity activity, UserInfoResponse profileBean) {
        Intent intent = new Intent(activity, ChatActivity.class);
        intent.putExtra(EXTRA_USR_PROFILE_BEAN, profileBean);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
    }

    // start activty with animation
    public static void newInstance(AppCompatActivity context, UserInfoResponse profileBean, View view) {
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(context, view, ELEMENT_CHAT);
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra(EXTRA_USR_PROFILE_BEAN, profileBean);
        context.startActivity(intent, options.toBundle());
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_chat;
    }

    @Override
    public void onStart(View viewRoot) {
        super.onStart(viewRoot);
        try {
            UserPreferences.getInstance().saveCurrentFriendChat(mUserProfileBean.userId);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onCreateView(View rootView) {
        // should close on destroy
        userId = UserPreferences.getInstance().getUserId();

        emojiStickerFragment = EmojiStickerFragment.newInstance(ChatActivity.this, ChatActivity.this);
        mediaFragment = MediaFragment.newInstance(ChatActivity.this);
        recordFragment = RecordFragment.newInstance(ChatActivity.this);

        mKeyboardHeightProvider = new KeyboardHeightProvider(this);
        // Clear previous data
        UserPreferences.getInstance().removeCurrentFriendChat();
        //Move the layout up when the soft keyboard is shown android
//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        getModulesCommonComponent().inject(this);

        if (SystemUtils.isNetworkConnected()) {
            mSwipeRefreshLayout.setRefreshing(true);
            mSwipeRefreshLayout.setOnRefreshListener(this);
        } else {
            mSwipeRefreshLayout.setRefreshing(false);
        }

        getDataBundle();

        setSupportActionBar(mToolbar);
        mToolbar.setActionbar(getSupportActionBar())
                .setDisplayHomeAsUpEnabled(true)
                .setTitleCenter(mUserProfileBean.userName)
                .setButtonRightListener(this);

        initOptionMore();
        // Chat Adapter
        initRecycler();
    }

    @Override
    public void onViewReady() {
        DatabaseHelper.getInstance(getApplicationContext())
                .updateEmojiLocal(getApplicationContext());
        //Get friend info to update username if mUserProfileBean.userName not ready
        getUserInfo(false, mUserProfileBean.userId);

        rxSocket.getWebSocketService().sendMarkMessageAsRead(mUserProfileBean.userId);
        //Listing keyboard changed
        viewRoot.post(new Runnable() {
            public void run() {
                mKeyboardHeightProvider.start();
            }
        });

        // reponse upload file
        respondFileUpload();

//        EmojiRequest emojiRequest = new EmojiRequest(UserPreferences.getInstance().getToken());
//        getPresenter().getDataEmoji(emojiRequest);

        ChatHistoryRequest request = new ChatHistoryRequest(mUserProfileBean.userId, 0, "", UserPreferences.getInstance().getToken());
        getPresenter().getChatHistory(request);

        mEdtMsg.addTextChangedListener(this);

        // time stop typing
        timeTyping();
    }


    //================================ FUNTION ========================================

    //TODO Take data from previous activity

    // get profile user
    private void getDataBundle() {
        Intent intent = getIntent();
        mUserProfileBean = intent.getParcelableExtra(EXTRA_USR_PROFILE_BEAN);

        LogUtils.e(TAG, "mUserProfileBean : " + new Gson().toJson(mUserProfileBean));
        if (mUserProfileBean == null) {
            Toast.makeText(this, R.string.common_error, Toast.LENGTH_SHORT).show();
            finish();
        } else {
            UserPreferences.getInstance().saveFriendId(mUserProfileBean.userId);
        }
    }

    // Handle response return upload file
    private void respondFileUpload() {
        RxEventBus.subscribe(SubjectCode.SUBJECT_UPLOAD_SUCCESS, this, new Consumer<Object>() {
            @Override
            public void accept(Object o) throws Exception {
                if (o != null) {
                    LogUtils.e(TAG, "SUBJECT_UPLOAD_SUCCESS :" + new Gson().toJson(o));
                    ChatMessage message = (ChatMessage) o;
                    sendMediaFile(message);
                }

            }
        });

        // receive event upload error
        RxEventBus.subscribe(SubjectCode.SUBJECT_UPLOAD_FAIL, this, new Consumer<Object>() {
            @Override
            public void accept(Object o) throws Exception {
                if (o != null) {
                    ChatMessage message = (ChatMessage) o;
                    mAdapter.markSendingMessagesFail(message.messageId);
                }
            }
        });
    }

    //set dapter
    private void initRecycler() {
        mAdapter = new ChatAdapter(this, mUserProfileBean.userName, this);
        mAdapter.setFriendAvatarUrl(mUserProfileBean.avatar);
        mAdapter.setGender(mUserProfileBean.gender);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        mLstChat.setHasFixedSize(true);
        mLstChat.setOverScrollMode(View.OVER_SCROLL_NEVER);

        //Cache data
        mLstChat.setItemViewCacheSize(1000);
        mLstChat.setDrawingCacheEnabled(true);
        mLstChat.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_LOW);
        mLstChat.setLayoutManager(mLayoutManager);
        mLstChat.setAdapter(mAdapter);
        mLstChat.setRecyclerListener(this);

        // Load the image after the end of the scroll event
        getHandler().post(new Runnable() {
            @Override
            public void run() {
                mLstChat.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(RecyclerView recyclerView, final int newState) {
                        super.onScrollStateChanged(recyclerView, newState);
                        switch (newState) {
                            case SCROLL_STATE_IDLE:
                                try {
                                    if (sIsScrolling)
                                        Glide.with(ChatActivity.this).resumeRequests();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                sIsScrolling = false;
                                break;
                            case SCROLL_STATE_DRAGGING:
                            case SCROLL_STATE_SETTLING:
                                sIsScrolling = true;
                                try {
                                    Glide.get(ChatActivity.this).trimMemory(ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN);
                                    Glide.with(ChatActivity.this).pauseRequests();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                break;
                        }
                    }
                });
            }
        });
        ViewCompat.setTransitionName(findViewById(R.id.activity_chat_bottom_layout), ELEMENT_CHAT);
    }

    @Override
    public void onReceiveSocket(SocketEvent socketEvent) {
        super.onReceiveSocket(socketEvent);
        if (socketEvent == null) return;
        int eventType = socketEvent.getEventType();
        Message msg = socketEvent.getMessage();

        if (msg == null) {
            LogUtils.w(TAG, "message is null!");
            return;
        }

        LogUtils.d(TAG, "onReceiveSocket33: " + msg.getRawText());

        switch (eventType) {
            case SocketEvent.EVENT_RECEIVE:
                onReceiveMessage(msg);
                break;
            case SocketEvent.EVENT_ERROR:
                saveMessageIntoDb(msg.getRawText());
                break;
            case SocketEvent.EVENT_CLOSE:
                LogUtils.d(TAG, "event close");
                break;
            case SocketEvent.EVENT_AUTH_FAIL:
                break;
            case SocketEvent.EVENT_AUTH_SUCCESS:
                LogUtils.d(TAG, "EVENT_AUTH_SUCCESS");
                break;
            case SocketEvent.EVENT_OPEN:
                break;
            case SocketEvent.EVENT_SERVICE_CONNECTED:
                LogUtils.d(TAG, "EVENT_SERVICE_CONNECTED");
                rxSocket.getWebSocketService().sendMarkMessageAsRead(mUserProfileBean.userId);
                break;
            case SocketEvent.EVENT_SERVICE_DISCONNECTED:
                LogUtils.d(TAG, "event disconneced");
                break;
            case SocketEvent.EVENT_SENDING_MESSAGE:
                sendingMessageList.add(msg);
                LogUtils.d(TAG, "EVENT_SENDING_MESSAGE: " + msg.getRawText());
                break;
        }
    }

    /**
     * will save message into db to resend if error
     *
     * @param rawMessage sending message
     */
    private void saveMessageIntoDb(String rawMessage) {
        Message msg = Message.parse(rawMessage);
        msg.setRawText(rawMessage);
        String type = msg.getMessageType();
        if (type == null) return;

        switch (type) {
            case com.tux.socket.models.Gift.TYPE_GIFT:
            case Media.TYPE_FILE:
            case Sticker.TYPE_STICKER:
            case Text.TYPE_TEXT:
                DatabaseHelper.getInstance(this).saveErrorMessage(
                        msg.getId(),
                        msg.getRawText(),
                        msg.getMessageType(),
                        msg.getOriginTime());
                break;
        }
    }

    /**
     * handle message on event {@link SocketEvent#EVENT_RECEIVE}
     *
     * @param msg received message
     */
    private void onReceiveMessage(Message msg) {
        // check null first
        assert msg != null;

        String messageType = msg.getMessageType();
        LogUtils.e(TAG, "onReceiveMessage " + msg.getRawText());
        // delete or ignore

        if (messageType.equals(TYPE_DELIVERING_STATE)) {
            LogUtils.e(TAG, "TYPE_DELIVERING_STATE " + msg.getMessageContent() + "___" + msg.getOriginTime());
            receiveDeliveringState(msg);
        } else if (!mUserProfileBean.userId.equals(msg.getSenderId())) return;

        switch (messageType) {
            case TYPE_TEXT:
                receiveText(msg);
                //Check if other user sender is ignored
                // case in other room, don't sent read message
                if (!mUserProfileBean.userId.equals(msg.getSenderId())) return;
                sendMarkMessageAsRead(msg);
                break;
            case TYPE_FILE:
                receiveFile(msg);
                sendMarkMessageAsRead(msg);
                break;
            case TYPE_GIFT:
                receiveGift(msg);
                sendMarkMessageAsRead(msg);
                break;
            case TYPE_STICKER:
                receiveSticker(msg);
                sendMarkMessageAsRead(msg);
                break;
            case TYPE_WRITING:
                receiveTyping(msg);
                break;
            case TYPE_DELIVERING_STATE:
                break;
        }
    }

    // send text and emoji
    private void sendMessage() {
        // remember trim() will not work if convert to html, cause space = &nbsp; in html
//        String text = mEdtMsg.getText().toString().trim();
//            LogUtils.e(TAG, "mEdtMsg " + mEdtMsg.getText());
//        SpannableString spannableString = new SpannableString(text);
//        String htmlWithEmojiImgTagMsg = Html.toHtml(spannableString);
//
//            LogUtils.e(TAG, "htmlWithEmojiImgTagMsg " + htmlWithEmojiImgTagMsg);
//        String htmlWithEmojiCodeMsg = ChatUtils.convertEmojiToCode(htmlWithEmojiImgTagMsg);
//
//      LogUtils.e(TAG, "htmlWithEmojiCodeMsg " + htmlWithEmojiCodeMsg);
        String content = mEdtMsg.getText().toString().trim();

        LogUtils.e(TAG, "sendmessage" + content);
        if (!content.isEmpty()) {
            ChatMessage chatMessage = new ChatMessage();
            chatMessage.mContent = content;
            chatMessage.mMsgType = ChatMessage.PP;
            String id = generateMessageId();
            chatMessage.messageId = id;
            chatMessage.mTimeStamp = getTimeInLocale();
            chatMessage.isOwn = true;
            // mark message as sending status
            chatMessage.sendMesasgeStatus = ChatMessage.STATUS_SENDING;

            mAdapter.insertData(chatMessage);
            mLstChat.smoothScrollToPosition(0);
            mEdtMsg.setText("");
            rxSocket.getWebSocketService().sendText(id, mUserProfileBean.userId, chatMessage.mContent, chatMessage.mMsgType);
        } else {
            if (events != null) {
                events.onAudioSend();
            }
        }
    }

    /**
     * @return new message id at this time
     */
    private String generateMessageId() {
        return userId + '&' + mUserProfileBean.userId + '&' + getTimeInLocale();
    }

    /**
     * send File(image,video,audio)
     *
     * @see #onReceiveMessage(Message)
     */
    private void sendMediaFile(ChatMessage chatMessage) {
        rxSocket.getWebSocketService().sendMedia(chatMessage.messageId, mUserProfileBean.userId, chatMessage.listFile);
        // fix issue http://10.64.100.201/issues/10937?issue_count=5&issue_position=4&next_issue_id=9885&prev_issue_id=11102#note-24
        // TODO: 2/26/18 notify ChatMessage and it's index instead
        mAdapter.notifyLastMediaItem(chatMessage);
    }

    // send sticker
    private void sendSickerFile(String pathSticker, String folder, String stickerId, String stickerUrl) {
        if (Utils.isEmptyOrNull(pathSticker)) return;
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.stickerUrl = stickerUrl;
        chatMessage.mMsgType = ChatMessage.STICKER;
        chatMessage.mTimeStamp = getTimeInLocale();
        chatMessage.isOwn = true;
        String id = generateMessageId();
        chatMessage.messageId = id;
        // mark message as sending status
        chatMessage.sendMesasgeStatus = ChatMessage.STATUS_SENDING;

        mAdapter.insertData(chatMessage);
        mLstChat.smoothScrollToPosition(0);
        rxSocket.getWebSocketService().sendSticker(id, mUserProfileBean.userId, folder, stickerId, stickerUrl);
    }


    // send gift
    private void sendGiftFile(Gift gift) {
        // 59f96bf241afe72fc05e7e04|cr7|Đào duy đức|0
        if (gift == null) return;
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.messageId = generateMessageId();
        chatMessage.mContent = gift.gift_id;
        chatMessage.mMsgType = ChatMessage.GIFT;
        chatMessage.mTimeStamp = getTimeInLocale();
        chatMessage.isOwn = true;
        // mark message as sending status
        chatMessage.sendMesasgeStatus = ChatMessage.STATUS_SENDING;

        mAdapter.insertData(chatMessage);
        mLstChat.smoothScrollToPosition(0);
        rxSocket.getWebSocketService().sendGift(chatMessage.messageId, mUserProfileBean.userId, gift.gift_id);
    }

    // mark message as read, only when chat activity is active
    private void sendMarkMessageAsRead(Message message) {
        LogUtils.e(TAG, "sendMarkMessageAsRead " + message);
        if (isChatActive && message != null) {
            rxSocket.getWebSocketService().sendMarkMessageAsRead(message.getSenderId());
        }
    }

    // receive massage text and emoji
    private void receiveText(Message msg) {
        if (msg == null) return;
        if (mUserProfileBean.userId.equalsIgnoreCase(msg.getSenderId())) {
            // TODO: 11/27/17
            ChatMessage chatMessage = new ChatMessage();
            chatMessage.mContent = msg.getValue();
            LogUtils.e(TAG, "mContent " + chatMessage.mContent);
            chatMessage.mMsgType = ChatMessage.PP;
            chatMessage.mTimeStamp = getTimeInLocale();
            chatMessage.isOwn = false;
            mAdapter.insertData(chatMessage);
            mLstChat.smoothScrollToPosition(0);
        }

    }


    // receive message sticker
    private void receiveSticker(Message msg) {
        if (msg == null) return;
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.stickerUrl = msg.getStickerUrl();
        chatMessage.catId = msg.getCatId();
        chatMessage.mContent = msg.getValue();
        chatMessage.mTimeStamp = getTimeInLocale();
        chatMessage.mMsgType = ChatMessage.STICKER;
        chatMessage.isOwn = false;
        mAdapter.insertData(chatMessage);
        mLstChat.smoothScrollToPosition(0);

    }

    // receive message file(image, video,audio)
    private void receiveFile(Message msg) {
        //Check NPE of file Message receive from the Server
        if (msg == null || msg.getListFile() == null || msg.getListFile().isEmpty()) return;
        // audio is only 1 item in listFile
        List<Media.FileBean> fileList = msg.getListFile();
        if (fileList.isEmpty()) return;
        String mMsgType;

        mMsgType = fileList.size() == 1 && fileList.get(0).fileType.equals(Media.FileBean.FILE_TYPE_AUDIO) ? ChatMessage.AUDIO : ChatMessage.MEDIA;

        LogUtils.e(TAG, "mMsgType:" + mMsgType);
        ChatMessage chatMessage = new ChatMessage();

        chatMessage.mMsgType = mMsgType;
        chatMessage.listFile = msg.getListFile();
        chatMessage.isOwn = false;

        LogUtils.e(TAG, "getTimeStamp : " + msg.getId() + "getOriginTime :" + msg.getOriginTime());
        chatMessage.mTimeStamp = msg.getOriginTime();

        mLstChat.smoothScrollToPosition(0);
        mAdapter.insertData(chatMessage);
    }

    // receive massage image gift
    private void receiveGift(Message msg) {
        if (msg == null) return;
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.mContent = msg.getValue();
        chatMessage.mMsgType = ChatMessage.GIFT;
        chatMessage.mTimeStamp = getTimeInLocale();
        chatMessage.isOwn = false;
        mAdapter.insertData(chatMessage);
        mLstChat.smoothScrollToPosition(0);

    }

    // receive typing
    private void receiveTyping(Message msg) {
        if (msg != null) {
            if (mUserProfileBean.userId.equalsIgnoreCase(msg.getSenderId())) {
                if (msg.getValue().equals(Typing.START_TYPING)) {
                    mLstChat.scrollToPosition(0);
                    loadingDots.setVisibility(View.VISIBLE);
                } else if (msg.getValue().equals(Typing.STOP_TYPING)) {
                    mLstChat.scrollToPosition(0);
                    loadingDots.setVisibility(View.GONE);
                }
            }
        }
    }

    //  server return delivering state
    private void receiveDeliveringState(Message msg) {
        if (msg == null) return;
        String messageValue = msg.getValue();
        if (messageValue.equalsIgnoreCase(MessageDeliveringState.VALUE_MARK_MESSAGE_AS_READ)) {
            // 11/30/17 receive read message => notify adapter seen
            Log.e(TAG, "đã xem: ");
            mAdapter.markAllMessageAsRead();
            mLstChat.smoothScrollToPosition(0);
        }

        if (messageValue.equalsIgnoreCase(MessageDeliveringState.VALUE_SENT_MESSAGE_SUCCESS)) {
            // 11/30/17 message sent to server success (message id)
            Log.e(TAG, "đã gửi " + msg.toJson());
            mAdapter.markSentMessage(msg.getId());
            mLstChat.scrollToPosition(0);

            // delete error message if exist
            DatabaseHelper.getInstance(getApplicationContext()).deleteErrorMessage(msg.getId());
        }

        if (messageValue.equalsIgnoreCase(MessageDeliveringState.VALUE_SENT_MESSAGE_SUCCESS_SAME_ACCOUNT) && msg.getReceiverId().equalsIgnoreCase(mUserProfileBean.userId)) {
            appendMessage(msg);
        }
    }

    // append message to end of list chat
    private void appendMessage(Message msg) {
        if (msg == null) return;
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.mContent = msg.getMessageContent();
        chatMessage.mMsgType = ChatMessage.PP;
        chatMessage.mTimeStamp = msg.getOriginTime();
        chatMessage.isOwn = true;
        mAdapter.insertData(chatMessage);
        mLstChat.smoothScrollToPosition(0);
    }

    //set image thumbnail when the send File
    private void addItemFile(ChatMessage chatMessage) {
        if (chatMessage != null) {
            // save media file to db to check file sent success or not
            Media media = new Media(userId, mUserProfileBean.userId, chatMessage.listFile);
            media.setId(chatMessage.messageId);
            saveMessageIntoDb(media.toJson());

            mAdapter.insertData(chatMessage);
            mLstChat.smoothScrollToPosition(0);
            upLoadFile(chatMessage);

            if (events != null) {
                events.onAudioSendSucces();
            }
        }
    }

    /**
     * upload file in server
     *
     * @see #respondFileUpload()
     */
    private void upLoadFile(ChatMessage chatMessage) {
        if (chatMessage == null) return;

        Intent intents = new Intent(this, UploadFileService.class);
        intents.putExtra(UploadFileService.INPUT, chatMessage);
        startService(intents);
    }


    // resend message
    // resend text and emoji
    private void resendMessage(String messageId, int position) {
        Message msg = DatabaseHelper.getInstance(getApplicationContext()).getErrorMessage(messageId);
        if (msg != null) {
            String type = msg.getMessageType();
            ChatMessage chatMessage = mAdapter.markErrorMessageAsSending(position);
            if (type.equals(ChatMessage.FILE)) {
                // if file upload
                upLoadFile(chatMessage);
            } else {
                rxSocket.getWebSocketService().sendMessage(msg.getRawText());
            }
        }
    }

    // resend or delete message send error
    private void checkResendMassage(final int pos) {
        final String[] sorts = new String[]{getResources().getString(R.string.common_resend), getResources().getString(R.string.delete_message_error)};
        new AlertDialog.Builder(this, R.style.Dialog_SingleChoice)
                .setTitle(getResources().getString(R.string.message_error_title))
                .setCancelable(true)
                .setSingleChoiceItems(sorts, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                })
                .setPositiveButton(R.string.common_ok_2, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setNegativeButton(R.string.common_cancel_2, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .show();
    }


    // handle popup view (gift,favorite,block,report)

    // Create layout for Option More
    private void initOptionMore() {
        mMoreOptionView = new MyMoreLayout(this, mOnMoreListener, mUserProfileBean);
//        LogUtils.e(TAG, "isFavorite : " + mUserProfileBean.isFavorite);
//        mMoreOptionView.setFavorite(mUserProfileBean.isFavorite);
        mMoreOptionPopup = new PopupWindow(
                mMoreOptionView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mMoreOptionPopup.setElevation(DimensionUtils.convertDpToPx(8));
        }
    }

    private void showMoreOption() {
        if (mMoreOptionPopup != null && !mMoreOptionPopup.isShowing()) {
            mMoreOptionPopup.showAsDropDown(mToolbar);
            mScrimView.setVisibility(View.VISIBLE);
        }
    }

    private void hideMoreOption() {
        if (mMoreOptionPopup != null && mMoreOptionPopup.isShowing()) {
            mMoreOptionPopup.dismiss();
            mScrimView.setVisibility(View.GONE);
        }
    }

    private boolean isMoreOptionShow() {
        return mMoreOptionPopup.isShowing();
    }

    private void executeReportUser() {
        Resources resource = getResources();
        final int[] position = new int[1];

        String[] items;
        items = resource.getStringArray(R.array.report_user_type);
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                R.layout.layout_item_single_choice, items);
        View reportView = LayoutInflater.from(context).inflate(R.layout.layout_report_user, null, false);
        ListView listView = reportView.findViewById(R.id.listView);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.setAdapter(adapter);

        int defaultSelected = 0, noreId = 0;
        listView.setItemChecked(defaultSelected, true);
        listView.performItemClick(listView.getSelectedView(), defaultSelected, noreId);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                position[0] = i;
            }
        });

        DialogMaterial.Builder builder = new DialogMaterial.Builder(context);
        final String[] finalItems = items;


        builder.setStyle(Style.HEADER_WITH_TITLE)
                .setTitle(R.string.dialog_confirm_report_user_title)
                .setCustomView(reportView)
                .onNegative(R.string.common_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        LogUtils.e(TAG, finalItems[position[0]]);
                        String token = UserPreferences.getInstance().getToken();
                        ReportRequest reportRequest = new ReportRequest(token,
                                mUserProfileBean.userId, position[0], Constants.REPORT_TYPE_USER);
                        getPresenter().reportUser(reportRequest);
                    }
                });
        builder.setButtonTextColor(getResources().getColor(R.color.colorPrimary));
        builder.show();
    }

    private void executeBlockUser() {
        DialogMaterial.Builder builder = new DialogMaterial.Builder(context);
        builder.setStyle(Style.HEADER_WITH_TITLE)
                .setTitle(R.string.dialog_confirm_block_user_title)
                .setContent(R.string.dialog_confirm_block_user_content)
                .onPositive(R.string.common_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        AddBlockUserRequest addBlockUserRequest = new AddBlockUserRequest(UserPreferences.getInstance().getToken(), mUserProfileBean.userId);
                        getPresenter().blockUser(addBlockUserRequest);
                        dialogInterface.dismiss();
                    }
                })
                .onNegative(R.string.common_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
        builder.show();
    }

    // set time stop styping
    private void timeTyping() {
        typingCountDownTimer = new CountDownTimer(DELAY_STOP_TYPING, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                rxSocket.getWebSocketService().sendStopTyping(mUserProfileBean.userId);
                lastTypingTime = -1;
            }
        };
    }

    /**
     * hide typing and stop count down timer
     */
    private void stopTypingCountDown() {
        typingCountDownTimer.cancel();
        typingCountDownTimer.onFinish();
    }

    //  show typing and reset count down timer
    private void resetTypingCountDown() {
        // reset count down
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastTypingTime >= DELAY_STOP_TYPING) {
            rxSocket.getWebSocketService().sendStartTyping(mUserProfileBean.userId);
            lastTypingTime = currentTime;
        }
        typingCountDownTimer.cancel();
        typingCountDownTimer.start();
    }

    private void showFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.menu_up, R.anim.menu_down, R.anim.menu_pop_enter, R.anim.menu_pop_exit)
                .replace(R.id.activity_chat_container_bottom_file, fragment)
                .addToBackStack(fragment.getClass().getSimpleName())
                .commit();

    }

    private void getMediaData(Intent data, boolean showConfirm) {
        Parcelable[] mediaFiles = data.getParcelableArrayExtra(MediaPickerBaseActivity.RESULT_KEY);

        if (mediaFiles != null) {
            LogUtils.d(TAG, "pick_photo " + mediaFiles.length);
            final ChatMessage chatMessage = generateMediaItem();
            chatMessage.mMsgType = ChatMessage.MEDIA;
            chatMessage.sendMesasgeStatus = ChatMessage.STATUS_SENDING;

            for (Parcelable file : mediaFiles) {
                MediaFile takeMediaFile = (MediaFile) file;
                // pick photo

                Media.FileBean fileBean = new Media.FileBean();
                fileBean.thumbnailUrl = takeMediaFile.getPath();
                if (MediaSanUtils.isPhoto(takeMediaFile.getPath())) {
                    fileBean.fileType = Media.FileBean.FILE_TYPE_IMAGE;
                    fileBean.originalUrl = takeMediaFile.getPath();
                } else {
                    // pick video
                    fileBean.fileType = Media.FileBean.FILE_TYPE_VIDEO;
                    fileBean.fileUrl = takeMediaFile.getPath();
                }

                chatMessage.listFile.add(fileBean);
            }

            if (showConfirm) {
                int confirmStringResId = chatMessage.listFile.get(0).fileType.equals(Media.FileBean.FILE_TYPE_IMAGE) ? R.string.would_you_like_to_send_image : R.string.would_you_like_to_send_video;
                // case camera only -> 1 image
                new AlertDialog.Builder(this, R.style.Dialog_SingleChoice)
                        .setTitle(getResources().getString(R.string.common_alert))
                        .setMessage(confirmStringResId)
                        .setCancelable(true)
                        .setPositiveButton(R.string.common_ok_2, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                addItemFile(chatMessage);
                            }
                        })
                        .setNegativeButton(R.string.common_cancel_2, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .show();
            } else addItemFile(chatMessage);
        }
    }

    /**
     * @return chat message item for media
     */
    private ChatMessage generateMediaItem() {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.sendMesasgeStatus = ChatMessage.STATUS_SENDING;
        chatMessage.isOwn = true;
        chatMessage.messageId = generateMessageId();
//        chatMessage.mTimeStamp = getTimeInLocale();
        LogUtils.e(TAG, "generateMediaItem : " + chatMessage.mTimeStamp);
        return chatMessage;
    }

    //================================ OPTION MORE LISTENER ========================================

    @OnClick({R.id.activity_chat_imv_add, R.id.activity_chat_imv_face, R.id.activity_chat_imv_send, R.id.activity_chat_edt_msg})
    public void onViewClicked(View view) {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.activity_chat_container_bottom_file);

        switch (view.getId()) {
            case R.id.activity_chat_imv_add:
                ChatUtils.hideKeyboard(ChatActivity.this, mEdtMsg);

                if (fragment != null) {
                    getSupportFragmentManager().popBackStack();
                }

                showFragment(mediaFragment);
                getHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mLstChat.scrollToPosition(0);
                    }
                }, DELAY);
                break;
            case R.id.activity_chat_imv_face:
                ChatUtils.hideKeyboard(ChatActivity.this, mEdtMsg);

                if (fragment != null) {
                    getSupportFragmentManager().popBackStack();
                }

                showFragment(emojiStickerFragment);
                getHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mLstChat.scrollToPosition(0);
                    }
                }, DELAY);

                break;
            case R.id.activity_chat_imv_send:
                LogUtils.e(TAG, "edt_msg " + events);
                rxSocket.getWebSocketService().sendStopTyping(mUserProfileBean.userId);
                sendMessage();
                break;
            case R.id.activity_chat_edt_msg:
                if (fragment != null) {
                    getSupportFragmentManager().popBackStack();
                }
                ChatUtils.showKeyboard(ChatActivity.this);

//                KeyboardUtils.showDelayKeyboard(mEdtMsg, 100);
                getHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // show fragment emoji and sticker
                        mLstChat.smoothScrollToPosition(0);
                    }
                }, 300);

                break;
        }
    }

    @Override
    public void getChatHistorySuccess(List<ChatMessage> messages) {
        // all message from history will mask as read all, only message you sent
        for (ChatMessage message : messages) {
//            if (message.isOwn) message.sendMesasgeStatus = ChatMessage.STATUS_SUCCESS;
            if (message.readTime.isEmpty()) {
                message.sendMesasgeStatus = ChatMessage.STATUS_SUCCESS;
            } else {
                message.sendMesasgeStatus = ChatMessage.STATUS_SEEN;
            }
        }

        // add error message into chat list
        List<Message> listErrorMessage = DatabaseHelper.getInstance(this).getErrorMessages();
        for (Message message : listErrorMessage) {
            // add error message if userId same as receiverId
            if (message.getReceiverId().equals(mUserProfileBean.userId)) {
                ChatMessage chatMessage = new ChatMessage();
                chatMessage.messageId = message.getId();
                chatMessage.isOwn = true;
                chatMessage.mMsgType = parseMessageType(message);
                chatMessage.sendMesasgeStatus = ChatMessage.STATUS_ERROR;
                chatMessage.senderId = message.getSenderId();
                chatMessage.receiverId = message.getReceiverId();
                chatMessage.mMsgType = message.getMessageType();
                chatMessage.catId = message.getCatId();
                chatMessage.stickerUrl = message.getStickerUrl();
                chatMessage.mContent = message.getValue();
                chatMessage.mTimeStamp = message.getOriginTime();
                chatMessage.listFile = message.getListFile();

                messages.add(0, chatMessage);
            }
        }

        mAdapter.setData(messages);
        mLstChat.scrollToPosition(0);
    }

    /**
     * correct msg type for FILE
     *
     * @param message input data
     */
    private String parseMessageType(Message message) {
        if (message != null) {
            List<Media.FileBean> files = message.getListFile();

            // check first file, don't care about another cause we cannot send audio mix with other type (image, video)
            if (files != null && !files.isEmpty()) {
                return files.get(0).fileType.equals(Media.FileBean.FILE_TYPE_AUDIO) ? ChatMessage.AUDIO : ChatMessage.MEDIA;
            }

            // else return default
            return message.getMessageType();
        }
        return null;
    }

    @Override
    public void getChatHistoryError() {
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void loadComplete() {
        mSwipeRefreshLayout.setRefreshing(false);
        Gift gift = getIntent().getExtras().getParcelable(GiftActivity.GIFT_ITEM);
        if (gift != null) {
            sendGiftFile(gift);
        }
    }

    @Override
    public void getDataEmojiSucess(EmojiReponse emojiReponse) {
        if (emojiReponse != null) {
            Gson gson = new Gson();
            SharedPreferences sharedpreferences = getSharedPreferences(ChatUtils.MY_PREFS_VERSION, Context.MODE_PRIVATE);
            for (int i = 0; i < emojiReponse.data.size(); i++) {
                LogUtils.e(TAG, "version " + gson.toJson(emojiReponse.data));
                gson.toJson(emojiReponse.data);

                SharedPreferences.Editor editor = sharedpreferences.edit();

                editor.putString("version", gson.toJson(emojiReponse.data));

                editor.apply();
            }
        }
    }

    @Override
    public void onFavouriteUser(String userId) {
        // notify favourite change
        RxEventBus.publish(SubjectCode.SUBJECT_UPDATE_TIMELINE_FAVORITE, userId);
        // update ui
        mMoreOptionView.setFavorite(Constants.BUZZ_TYPE_IS_FAVORITE);
    }

    @Override
    public void onUnFavouriteUser(String userId) {
        // notify favourite change
        RxEventBus.publish(SubjectCode.SUBJECT_UPDATE_TIMELINE_UNFAVORITE, userId);
        // update ui
        mMoreOptionView.setFavorite(Constants.BUZZ_TYPE_IS_NOT_FAVORITE);
    }


    @Override
    public void onUserInfo(UserInfoResponse userInfoResponse) {

        if (userInfoResponse == null) return;
        if (userInfoResponse.userId.equals(mUserProfileBean.userId)) {
            //Update userName String toolbar title
            mToolbar.setTitleCenter(userInfoResponse.userName);
            LogUtils.e(TAG, "isFavorite : " + userInfoResponse.isFavorite);
            mMoreOptionView.setFavorite(userInfoResponse.isFavorite);

        }
    }

    @Override
    public void onReportUser() {
        DialogMaterial.Builder builder = new DialogMaterial.Builder(context);
        builder.setStyle(Style.HEADER_WITH_TITLE)
                .setTitle(R.string.dialog_confirm_report_user_title)
                .setContent(R.string.dialog_confirm_report_user_content)
                .onNegative(R.string.common_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
        builder.setButtonTextColor(getResources().getColor(R.color.colorPrimary));
        builder.show();
    }

    @Override
    public void onAddBlockUser() {
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();
    }

    @OnClick(R.id.activity_chat_scrim_view)
    public void optionScrimView() {
        hideMoreOption();
    }

    private OnMoreListener mOnMoreListener = new OnMoreListener() {
        @Override
        public void onSendGift(UserInfoResponse userProfileBean) {
            if (userProfileBean != null) {
                GiftActivity.launch(ChatActivity.this, userProfileBean, mToolbar, ActivityResultRequestCode.REQUEST_START_SEND_GIFT_FROM_CHAT);
                hideMoreOption();
            }
        }

        @Override
        public void onFavorite(UserInfoResponse userProfileBean) {
            hideMoreOption();
            //Send request favorite
            String token = UserPreferences.getInstance().getToken();
            if (userProfileBean.isFavorite == Constants.BUZZ_TYPE_IS_NOT_FAVORITE)
                getPresenter().setFavorite(new AddFavoriteRequest(token, userProfileBean.userId), userProfileBean.userId);
            else
                getPresenter().setUnFavorite(new RemoveFavoriteRequest(token, userProfileBean.userId), userProfileBean.userId);
        }

        @Override
        public void onBlock(final UserInfoResponse userProfileBean) {
            if (userProfileBean != null) {
                executeBlockUser();
                hideMoreOption();
            }
        }

        @Override
        public void onReport(UserInfoResponse userProfileBean) {
            if (userProfileBean != null) {
                executeReportUser();
                hideMoreOption();
            }
        }

        @Override
        public void onAlertOnline(UserInfoResponse userProfileBean) {
            if (userProfileBean != null) {
                LogUtils.d(TAG, "onAlertOnline -> " + userProfileBean);
                hideMoreOption();
                ManageOnlineAlertActivity.launch(ChatActivity.this,
                        mMoreOptionView.getViewOnline(),
                        userProfileBean
                );
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtils.d(TAG, "requestCode= " + requestCode + "|resultCode=" + resultCode + "\n" + Utils.dumpIntent(data));
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case ActivityResultRequestCode.REQUEST_START_SEND_GIFT_FROM_CHAT:
                    //Receive Gift item request to send to friend after choose send Gift from ChatActivity
                    Gift gift = data.getParcelableExtra(GiftActivity.GIFT_ITEM);
                    if (gift != null) {
                        sendGiftFile(gift);
                    }
                    break;
                case Camera.REQUEST_CODE_CAMERA:
                    getMediaData(data, true);
                    break;
                case Gallery.REQUEST_CODE_GALLERY:
                    getMediaData(data, false);
                    break;

            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        LogUtils.e(TAG, "--->onPause is calling...");
        isChatActive = false;
        mKeyboardHeightProvider.setKeyboardHeightObserver(null);
    }


    @Override
    public void onResume(View viewRoot) {
        super.onResume(viewRoot);

        isChatActive = true;
        if (mKeyboardHeightProvider != null)
            mKeyboardHeightProvider.setKeyboardHeightObserver(this);
        // TODO: 11/30/17 load history again
    }


    /**
     * path sticker item
     *
     * @param pathSticker
     */
    @Override
    public void onStickerClick(String pathSticker) {
//        /storage/emulated/0/Android/data/com.vn.ntsc.develop/files/stickers/574eb345e4b0ccb75f393e2d/574eb47fe4b0ccb75f393e2f_100001.png
        LogUtils.e(TAG, "onStickerClick " + pathSticker);
        if (Utils.isEmptyOrNull(pathSticker)) return;

        String name = pathSticker.substring(pathSticker.lastIndexOf("/") + 1, pathSticker.length());

        String[] split = pathSticker.split("/");

        //574eb47fe4b0ccb75f393e2f_100001.png
        LogUtils.e(TAG, "onStickerClick " + split[split.length - 2]);
        String folder = split[split.length - 2];

        String[] nameSticker = name.split("_");
        if (nameSticker.length > 0 && folder != null) {

            String stkId = nameSticker[0];
            String code = nameSticker[1];
            if (stkId != null && code != null) {
                String stickerUrl = BuildConfig.STICKER_URL + code;
                sendSickerFile(pathSticker, folder, stkId, stickerUrl);
                LogUtils.d(TAG, "onPhotoClickListener " + folder + "__" + code + "__" + stickerUrl);
            }

        }

    }

    // show media image
    @Override
    public void onPhotoClickListener() {
        new Gallery.Builder()
                .sortType(Gallery.SORT_BY_PHOTOS)
                .isMultichoice(true)
                .limitChoice(LIMIT_PIC)
                .build()
                .start(this);
        LogUtils.d(TAG, "onPhotoClickListener ");
    }

    // show camera
    @Override
    public void onCameraClickListener() {
        new Camera.Builder()
                .isVideoMode(false)
                .flashMode(CameraView.FLASH_AUTO)
                .build()
                .start(this);
        LogUtils.d(TAG, "onCameraClickListener ");
    }

    // show media video
    @Override
    public void onVideoClickListener() {
        new Gallery.Builder()
                .sortType(Gallery.SORT_BY_VIDEOS)
                .isMultichoice(true)
                .limitChoice(LIMIT_PIC)
                .build()
                .start(ChatActivity.this);
        LogUtils.d(TAG, "onVideoClickListener ");
    }


    @Override
    public void onRecordVideoListener() {
        new Camera.Builder()
                .facing(CameraView.FACING_FRONT)
                .isVideoMode(true)
                .flashMode(CameraView.FLASH_ON)
                .build()
                .start(ChatActivity.this);
        LogUtils.d(TAG, "onRecordViewListener ");
    }

    @Override
    public void onRecordSendClick(String path) {
        if (Utils.isEmptyOrNull(path)) return;

        LogUtils.e(TAG, "onRecordSendClick " + path);

        ChatMessage chatMessage = generateMediaItem();
        chatMessage.messageId = generateMessageId();
        chatMessage.mMsgType = ChatMessage.AUDIO;
        chatMessage.sendMesasgeStatus = ChatMessage.STATUS_SENDING;

        Media.FileBean fileBean = new Media.FileBean();
        fileBean.fileUrl = path;

        // duration error message = 0, http://10.64.100.201/issues/10937#note-92
//        fileBean.fileDuration = ChatUtils.getDurationFile(path);
        LogUtils.e(TAG, "fileDuration:" + fileBean.fileDuration);
        fileBean.fileType = Media.FileBean.FILE_TYPE_AUDIO;
        chatMessage.listFile.add(fileBean);

        addItemFile(chatMessage);
    }

    @Override
    public void onRecordingListenner() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.activity_chat_container_bottom_file);
        if (fragment != null) {
            getSupportFragmentManager().popBackStack();
        }
        showFragment(recordFragment);
    }

    /**
     * Get user info via userId String
     *
     * @param isMyInfo - true if need get yourself info - false if want get friend info
     * @param friendId userId of friend
     */
    private void getUserInfo(boolean isMyInfo, String friendId) {
        UserPreferences userPreferences = UserPreferences.getInstance();
        String token = userPreferences.getToken();

        if (isMyInfo && !Utils.isEmptyOrNull(token) && !Utils.isEmptyOrNull(userId)) {
            UserInfoRequest userInfoRequest = new UserInfoRequest(token, userId);
            getPresenter().getUserInfo(userInfoRequest);
        } else if (!isMyInfo && !Utils.isEmptyOrNull(token) && !Utils.isEmptyOrNull(friendId)) {
            UserInfoRequest friendInfoRequest = new UserInfoRequest(token, friendId);
            getPresenter().getUserInfo(friendInfoRequest);
        }
    }

    @Override
    public void onRefresh() {
        mSwipeRefreshLayout.setRefreshing(false);
//        ChatHistoryRequest request = new ChatHistoryRequest(mUserProfileBean.userId, 0, "", UserPreferences.getInstance().getToken());
//        getPresenter().getChatHistory(request);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        // don't allow start with space http://10.64.100.201/issues/10030#note-42
        String text = mEdtMsg.getText().toString();
        if (text.startsWith(" ")) {
            mEdtMsg.setText(text.trim());
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
        int length = s.length();
        if (length > 0) {
            resetTypingCountDown();
        } else {
            stopTypingCountDown();
        }
    }

    /**
     * massage error
     *
     * @param messageId
     * @param position
     */
    @Override
    public void onItemClickSenMessageError(final String messageId, final int position) {
        if (SystemUtils.isNetworkConnected()) {
            getDialog(position).setStyle(Style.HEADER_WITH_NOT_HEADER)
                    .setContent(R.string.common_confirm_resend)
                    .onPositive(R.string.common_ok_2, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            resendMessage(messageId, position);
                        }
                    })
                    .onNegative(R.string.common_cancel_2, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .show();
        } else {
            // show dialog check network
            getDialog(position).setStyle(Style.HEADER_WITH_TITLE)
                    .setTitle(R.string.common_not_connect_network)
                    .setContent(R.string.common_ms_no_internet)
                    .onPositive(R.string.common_settings_wifi, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent networkSetting = new Intent(Settings.ACTION_WIFI_SETTINGS);
                            startActivity(networkSetting);
                        }
                    })
                    .onNegative(R.string.common_settings_mobile_internet, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int i) {
                            Intent networkSetting = new Intent(Settings.ACTION_DATA_ROAMING_SETTINGS);
                            startActivity(networkSetting);
                        }
                    })
                    .onNeutral(R.string.common_cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    }).show();
        }
    }

    @Override
    public void onItemClickEmailMessage(String email) {
        // send email
        if (email != null) {
            ChatUtils.sendEmail(this, email);
        }
    }

    @Override
    public void onItemClickPhoneMessage(String phone) {
        // call
        if (ActivityCompat.checkSelfPermission(ChatActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ChatActivity.this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST);
        } else {
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:" + phone));
            startActivity(intent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "The app was  allowed to.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "The app was not allowed to call.", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @Override
    public void onItemClickWebLinkMessage(String weblink) {
        // click web link
        if (weblink != null) {
            ChatUtils.webLink(this, weblink);
        }
    }

    @Override
    public void onEmojiClick(String code) {
        if (code != null) {
            int start = Math.max(mEdtMsg.getSelectionStart(), 0);
            int end = Math.max(mEdtMsg.getSelectionEnd(), 0);
            mEdtMsg.getText().replace(Math.min(start, end), Math.max(start, end),
                    code, 0, code.length());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // remove RxBus
        RxEventBus.unregister(this);

        // Must place removeUserIdToSend here (onDetach() called after onStart() of another ChatActivity)
        UserPreferences.getInstance().removeCurrentFriendChat();
        if (mKeyboardHeightProvider != null) {
            mKeyboardHeightProvider.close();
        }

        // get all sending or error message
        // 2/23/18 save error or sending message
        DatabaseHelper.getInstance(this).saveErrorMessage(sendingMessageList, mAdapter.getAllSendingOrErrorMessages());
    }

    @Override
    protected void onStop() {
        super.onStop();
//        if (mPlayer != null) {
//            mPlayer.release();
//            mPlayer = null;
//        }
    }

    @Override
    public void onBackPressed() {
        Fragment fragment1 = getSupportFragmentManager().findFragmentById(R.id.activity_chat_container_bottom_file);
        LogUtils.e(TAG, "onBackPressed " + fragment1);
        if (fragment1 != null) {
            getSupportFragmentManager().popBackStack();
        } else {
            if (isMoreOptionShow()) {
                hideMoreOption();
                return;
            }
            super.onBackPressed();
        }
    }

    @Override
    public void onKeyboardHeightChanged(int height, int orientation) {
        String or = orientation == Configuration.ORIENTATION_PORTRAIT ? "portrait" : "landscape";
        LogUtils.i(TAG, "onKeyboardHeightChanged in pixels: " + height + "|mInputChatBottomLayout.getHeight()=" + mInputChatBottomLayout.getHeight());

        //mLstChat.scrollToPosition(0);

        if (height == 0) {
            //setHeightKeyboardVirtualFrame(height + mInputChatBottomLayout.getHeight());
        } else if (height > 100) {//height > 100 <-> ensure keyboard available shown
            //setHeightKeyboardVirtualFrame(height);
        }
    }

    private void setHeightKeyboardVirtualFrame(int heightKeyboardVirtualFrame) {
        //mInputChatBottomLayout.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, heightKeyboardVirtualFrame));
        //mInputChatBottomLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, heightKeyboardVirtualFrame));
        mFrameLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, heightKeyboardVirtualFrame));
        Preferences.getInstance().saveKeyboardHeight(heightKeyboardVirtualFrame);
    }

    @Override
    public void onClick(final View mView, final int viewType, final int position, final ChatMessage chatMessage, final List<MediaEntity> mMediaFileList) {
        // Handle onClick event for all Message type
        switch (viewType) {
//            case ChatMessageType.TYPE_PHOTO_SEND:
//            case ChatMessageType.TYPE_PHOTO_RECEIVER:
//                List<Media.FileBean> mArrayFile = chatMessage.getListFile();
//                ArrayList<String> filePaths = new ArrayList<>();
//
//                if (mArrayFile != null) {
//                    for (Media.FileBean file : mArrayFile) {
//                        filePaths.add(file.originalUrl);
//                    }
//                } else {
//                    filePaths.addAll(files);
//                }
            //PreviewImageActivity.startActivity(this, filePaths, 0);
//                AlbumDetailMediaActivity.launch(this, mMediaFileList, 0);
//                break;
//            case ChatMessageType.TYPE_PHOTO_SEND:
//
//
//            case ChatMessageType.TYPE_PHOTO_RECEIVER:
//                List<String> files = chatMessage.tempFile;
//                List<Media.FileBean> mArrayFile = chatMessage.getListFile();
//                ArrayList<String> filePaths = new ArrayList<>();
//
//                if (mArrayFile != null) {
//                    for (Media.FileBean file : mArrayFile) {
//                        filePaths.add(file.originalUrl);
//                    }
//                } else {
//                    for (String path : files) {
//                        filePaths.add(path);
//                    }
//                }
//                //PreviewImageActivity.startActivity(this, filePaths, 0);
//                AlbumDetailMediaActivity.launch(this, mMediaFileList, 0);

//                GeneraLibraryActivity.startActivity(this);
//                break;

            case ChatMessageType.TYPE_AUDIO_SEND:

                break;
            case ChatMessageType.TYPE_AUDIO_RECEIVER:

                break;

            case ChatMessageType.TYPE_GIFT_SEND:
            case ChatMessageType.TYPE_GIFT_RECEIVER:
                break;

            case ChatMessageType.TYPE_MESSAGE_SEND:
            case ChatMessageType.TYPE_MESSAGE_RECEIVER:
                break;

            case ChatMessageType.TYPE_STICKER_SEND:
            case ChatMessageType.TYPE_STICKER_RECEIVER:
                break;

//            case ChatMessageType.TYPE_VIDEO_SEND:
//            case ChatMessageType.TYPE_VIDEO_RECEIVER:
//                AlbumDetailMediaActivity.launch(this, mMediaFileList, 0);
//                break;
        }
    }

    @Override
    public void onClickMedia(ChatMessage chatMessage, int indexInListMedia) {
        // TODO: 3/2/18 onclick media
        Log.d(TAG, "onClickMedia: ");
        ChatMediaDetailActivity.launch(this, chatMessage, indexInListMedia);
    }


    @Override
    public boolean hasRegisterSocket() {
        return true;
    }

    @Override
    public void onViewRecycled(RecyclerView.ViewHolder viewHolder) {
        EmoTextView target = null;
        if (viewHolder instanceof MessageSendHolder) {
            MessageSendHolder holder = (MessageSendHolder) viewHolder;
            target = holder.mTvSend;
        }
        if (viewHolder instanceof MessageReceiverHolder) {
            MessageReceiverHolder holder = (MessageReceiverHolder) viewHolder;
            target = holder.mTvReceiver;
        }

        GlideImageGetter.clear(target);
    }

    @Override
    public void onToolbarButtonRightClick(View view) {
        if (mMoreOptionPopup != null) {
            if (!mMoreOptionPopup.isShowing()) {
                showMoreOption();
            } else {
                hideMoreOption();
            }
        }
    }
}