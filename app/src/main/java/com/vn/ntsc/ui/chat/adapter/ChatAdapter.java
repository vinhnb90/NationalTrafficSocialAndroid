package com.vn.ntsc.ui.chat.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.tux.socket.models.Media;
import com.vn.ntsc.R;
import com.vn.ntsc.repository.model.chat.ChatMessage;
import com.vn.ntsc.ui.chat.ChatActivity;
import com.vn.ntsc.ui.chat.adapter.holder.AudioReceiverHolder;
import com.vn.ntsc.ui.chat.adapter.holder.AudioSendHolder;
import com.vn.ntsc.ui.chat.adapter.holder.BaseChatHolder;
import com.vn.ntsc.ui.chat.adapter.holder.GiftReceiverHolder;
import com.vn.ntsc.ui.chat.adapter.holder.GiftSendHolder;
import com.vn.ntsc.ui.chat.adapter.holder.HeaderHolder;
import com.vn.ntsc.ui.chat.adapter.holder.IDataChat;
import com.vn.ntsc.ui.chat.adapter.holder.MessageReceiverHolder;
import com.vn.ntsc.ui.chat.adapter.holder.MessageSendHolder;
import com.vn.ntsc.ui.chat.adapter.holder.NoDefinedHolder;
import com.vn.ntsc.ui.chat.adapter.holder.StickerReceiverHolder;
import com.vn.ntsc.ui.chat.adapter.holder.StickerSendHolder;
import com.vn.ntsc.ui.chat.adapter.holder.TypingHolder;
import com.vn.ntsc.ui.chat.adapter.holder.mediaMultiple.Media1Holder;
import com.vn.ntsc.ui.chat.adapter.holder.mediaMultiple.Media2Holder;
import com.vn.ntsc.ui.chat.adapter.holder.mediaMultiple.Media3Holder;
import com.vn.ntsc.ui.chat.adapter.holder.mediaMultiple.Media4Holder;
import com.vn.ntsc.ui.chat.adapter.holder.mediaMultiple.Media5Holder;
import com.vn.ntsc.ui.chat.adapter.holder.mediaMultiple.MediaMoreHolder;
import com.vn.ntsc.ui.chat.audio.SequenceAudioPlayer;
import com.vn.ntsc.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

import static com.vn.ntsc.utils.time.TimeUtils.getTimeInLocale;

/**
 * Created by ThoNh on 9/26/2017.
 */

public class ChatAdapter extends RecyclerView.Adapter<BaseChatHolder> implements IDataChat, SequenceAudioPlayer.MediaEventListener {
    public static final String TAG = ChatAdapter.class.getSimpleName();
    private final SequenceAudioPlayer audioPlayer;
    private BaseChatHolder.MessageOnEventListener listener;
    private List<ChatMessage> mData;
    private String dateTime = "none";
    private String mFriendAvatarUrl;
    private int mGender;
    private String userName;
    private ChatActivity activity;
    /**
     * store current playing audio position
     */
    private int currentPlayingPosition = -1;

    public ChatAdapter(BaseChatHolder.MessageOnEventListener listener, String userName, ChatActivity activity) {
        this.activity = activity;
        this.listener = listener;
        this.userName = userName;
        this.mData = new ArrayList<>();
        audioPlayer = new SequenceAudioPlayer(this);
    }

    /**
     * Avatar url of friends
     *
     * @param url
     */
    public void setFriendAvatarUrl(String url) {
        this.mFriendAvatarUrl = url;
    }

    /**
     * 1 : nam , 0 : nữ
     *
     * @param gender
     */
    public void setGender(int gender) {
        this.mGender = gender;
    }

    @Override
    public BaseChatHolder onCreateViewHolder(ViewGroup parent, @ChatMessageType int viewType) {
        return createHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(BaseChatHolder holder, int position) {
        int viewType = getItemViewType(position);
        ChatMessage message = mData.get(position);
        bindView(viewType, holder, message, position);
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    @Override
    public int getItemViewType(int position) {
        ChatMessage message = mData.get(position);
        if (message != null) {
            return getItemViewType(message);
        }
        return -1;
    }

    @Override
    public void setData(List<ChatMessage> data) {
        if (data != null) {

            data = setTimeHeader(data);
            data = parseFile(data);
//            data = convertEmojiStringtoImage(data);
            mData.addAll(data);
            notifyDataSetChanged();

        }
    }


    @Override
    public void setData(int index, List<ChatMessage> data) {
        if (data != null) {
            mData.set(index, data.get(index));
            notifyItemChanged(index);
        }
    }

    @Override
    public void insertData(ChatMessage chatMessage) {
        mData.add(0, chatMessage);
        notifyItemInserted(0);
    }

    @Override
    public ChatMessage getData(int position) {
        return mData.get(position);
    }

    @Override
    public List<ChatMessage> getData() {
        return mData;
    }

    public String getUserName() {
        return userName;
    }

    /* Private method*/
    private BaseChatHolder createHolder(ViewGroup parent, @ChatMessageType int viewType) {
        LayoutInflater mInflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case ChatMessageType.TYPE_HEADER:
                return new HeaderHolder(mInflater.inflate(R.layout.item_chat_header, parent, false));

            case ChatMessageType.TYPE_AUDIO_SEND:
                return new AudioSendHolder(activity, listener, mInflater.inflate(R.layout.item_chat_send_audio, parent, false), ChatAdapter.this, audioPlayer);

            case ChatMessageType.TYPE_AUDIO_RECEIVER:
                return new AudioReceiverHolder(mGender, activity, listener, mFriendAvatarUrl, mInflater.inflate(R.layout.item_chat_receiver_audio, parent, false), ChatAdapter.this, audioPlayer);

            case ChatMessageType.TYPE_GIFT_SEND:
                return new GiftSendHolder(listener, mInflater.inflate(R.layout.item_chat_send_gift, parent, false), ChatAdapter.this);

            case ChatMessageType.TYPE_GIFT_RECEIVER:
                return new GiftReceiverHolder(mGender, listener, mFriendAvatarUrl, mInflater.inflate(R.layout.item_chat_receiver_gift, parent, false), ChatAdapter.this);

            case ChatMessageType.TYPE_MESSAGE_SEND:
                return new MessageSendHolder(listener, mInflater.inflate(R.layout.item_chat_send_message, parent, false));

            case ChatMessageType.TYPE_MESSAGE_RECEIVER:
                return new MessageReceiverHolder(mGender, listener, mFriendAvatarUrl, mInflater.inflate(R.layout.item_chat_receiver_message, parent, false));

            case ChatMessageType.TYPE_STICKER_SEND:
                return new StickerSendHolder(listener, mInflater.inflate(R.layout.item_chat_send_sticker, parent, false));

            case ChatMessageType.TYPE_STICKER_RECEIVER:
                return new StickerReceiverHolder(mGender, listener, mFriendAvatarUrl, mInflater.inflate(R.layout.item_chat_receiver_sticker, parent, false));

            case ChatMessageType.TYPE_MEDIA1_SEND:
                return new Media1Holder(mGender, mFriendAvatarUrl, mInflater.inflate(R.layout.item_media1_send, parent, false), listener);
            case ChatMessageType.TYPE_MEDIA2_SEND:
                return new Media2Holder(mGender, mFriendAvatarUrl, mInflater.inflate(R.layout.item_media2_send, parent, false), listener);
            case ChatMessageType.TYPE_MEDIA3_SEND:
                return new Media3Holder(mGender, mFriendAvatarUrl, mInflater.inflate(R.layout.item_media3_send, parent, false), listener);
            case ChatMessageType.TYPE_MEDIA4_SEND:
                return new Media4Holder(mGender, mFriendAvatarUrl, mInflater.inflate(R.layout.item_media4_send, parent, false), listener);
            case ChatMessageType.TYPE_MEDIA5_SEND:
                return new Media5Holder(mGender, mFriendAvatarUrl, mInflater.inflate(R.layout.item_media5_send, parent, false), listener);
            case ChatMessageType.TYPE_MEDIA_MORE_SEND:
                return new MediaMoreHolder(mGender, mFriendAvatarUrl, mInflater.inflate(R.layout.item_media_more_send, parent, false), listener);

            case ChatMessageType.TYPE_MEDIA1_RECEIVE:
                return new Media1Holder(mGender, mFriendAvatarUrl, mInflater.inflate(R.layout.item_media1_receive, parent, false), listener);
            case ChatMessageType.TYPE_MEDIA2_RECEIVE:
                return new Media2Holder(mGender, mFriendAvatarUrl, mInflater.inflate(R.layout.item_media2_receive, parent, false), listener);
            case ChatMessageType.TYPE_MEDIA3_RECEIVE:
                return new Media3Holder(mGender, mFriendAvatarUrl, mInflater.inflate(R.layout.item_media3_receive, parent, false), listener);
            case ChatMessageType.TYPE_MEDIA4_RECEIVE:
                return new Media4Holder(mGender, mFriendAvatarUrl, mInflater.inflate(R.layout.item_media4_receive, parent, false), listener);
            case ChatMessageType.TYPE_MEDIA5_RECEIVE:
                return new Media5Holder(mGender, mFriendAvatarUrl, mInflater.inflate(R.layout.item_media5_receive, parent, false), listener);
            case ChatMessageType.TYPE_MEDIA_MORE_RECEIVE:
                return new MediaMoreHolder(mGender, mFriendAvatarUrl, mInflater.inflate(R.layout.item_media_more_receive, parent, false), listener);

            case ChatMessageType.TYPE_TYPING:
                return new TypingHolder(mInflater.inflate(R.layout.item_chat_typing, parent, false));

            default:
                return new NoDefinedHolder(mInflater.inflate(R.layout.item_chat_no_defined, parent, false));
        }
    }

    private int getItemViewType(ChatMessage chatMessage) {
        String strType = chatMessage.mMsgType;
        boolean isOwn = chatMessage.isOwn;

        switch (strType) {
            case ChatMessage.PP:
                return isOwn ? ChatMessageType.TYPE_MESSAGE_SEND : ChatMessageType.TYPE_MESSAGE_RECEIVER;

            case ChatMessage.MEDIA:
                return getMediaViewType(chatMessage.listFile.size(), isOwn);

            case ChatMessage.AUDIO:
                return isOwn ? ChatMessageType.TYPE_AUDIO_SEND : ChatMessageType.TYPE_AUDIO_RECEIVER;

            case ChatMessage.STICKER:
                LogUtils.d("TYPE_STICKER_SEND ", "TYPE_STICKER_SEND ");
                return isOwn ? ChatMessageType.TYPE_STICKER_SEND : ChatMessageType.TYPE_STICKER_RECEIVER;

            case ChatMessage.GIFT:
                return isOwn ? ChatMessageType.TYPE_GIFT_SEND : ChatMessageType.TYPE_GIFT_RECEIVER;

            case ChatMessage.HEADER_TIMER:
                return ChatMessageType.TYPE_HEADER;

            case ChatMessage.TYPING:
                return ChatMessageType.TYPE_TYPING;
            default:
                return ChatMessageType.TYPE_NO_DEFINED;
        }
    }

    /**
     * @param size  number of send media
     * @param isOwn identify message from you or not
     * @return view type for media
     */
    private int getMediaViewType(int size, boolean isOwn) {
        int viewType = -1;
        if (size == 1)
            viewType = isOwn ? ChatMessageType.TYPE_MEDIA1_SEND : ChatMessageType.TYPE_MEDIA1_RECEIVE;
        if (size == 2)
            viewType = isOwn ? ChatMessageType.TYPE_MEDIA2_SEND : ChatMessageType.TYPE_MEDIA2_RECEIVE;
        if (size == 3)
            viewType = isOwn ? ChatMessageType.TYPE_MEDIA3_SEND : ChatMessageType.TYPE_MEDIA3_RECEIVE;
        if (size == 4)
            viewType = isOwn ? ChatMessageType.TYPE_MEDIA4_SEND : ChatMessageType.TYPE_MEDIA4_RECEIVE;
        if (size == 5)
            viewType = isOwn ? ChatMessageType.TYPE_MEDIA5_SEND : ChatMessageType.TYPE_MEDIA5_RECEIVE;
        if (size > 5)
            viewType = isOwn ? ChatMessageType.TYPE_MEDIA_MORE_SEND : ChatMessageType.TYPE_MEDIA_MORE_RECEIVE;
        return viewType;
    }

    private void bindView(int viewType, BaseChatHolder holder, ChatMessage message, int position) {
        switch (viewType) {
            case ChatMessageType.TYPE_HEADER:
                ((HeaderHolder) holder).bindView(message, position);
                break;
            case ChatMessageType.TYPE_AUDIO_SEND:
                ((AudioSendHolder) holder).bindView(message, position);
                break;
            case ChatMessageType.TYPE_AUDIO_RECEIVER:
                ((AudioReceiverHolder) holder).bindView(message, position);
                break;
            case ChatMessageType.TYPE_GIFT_SEND:
                ((GiftSendHolder) holder).bindView(message, position);
                break;
            case ChatMessageType.TYPE_GIFT_RECEIVER:
                ((GiftReceiverHolder) holder).bindView(message, position);
                break;
            case ChatMessageType.TYPE_MESSAGE_SEND:
                ((MessageSendHolder) holder).bindView(message, position);
                break;
            case ChatMessageType.TYPE_MESSAGE_RECEIVER:
                ((MessageReceiverHolder) holder).bindView(message, position);
                break;
            case ChatMessageType.TYPE_STICKER_SEND:
                LogUtils.d("TYPE_STICKER_SEND ", "TYPE_STICKER_SEND ");
                ((StickerSendHolder) holder).bindView(message, position);
                break;
            case ChatMessageType.TYPE_STICKER_RECEIVER:
                ((StickerReceiverHolder) holder).bindView(message, position);
                break;
            case ChatMessageType.TYPE_TYPING:
                ((TypingHolder) holder).bindView(message, position);
                break;
            case ChatMessageType.TYPE_MEDIA1_SEND:
            case ChatMessageType.TYPE_MEDIA1_RECEIVE:
                ((Media1Holder) holder).bindView(message, position);
                break;
            case ChatMessageType.TYPE_MEDIA2_SEND:
            case ChatMessageType.TYPE_MEDIA2_RECEIVE:
                ((Media2Holder) holder).bindView(message, position);
                break;
            case ChatMessageType.TYPE_MEDIA3_SEND:
            case ChatMessageType.TYPE_MEDIA3_RECEIVE:
                ((Media3Holder) holder).bindView(message, position);
                break;
            case ChatMessageType.TYPE_MEDIA4_SEND:
            case ChatMessageType.TYPE_MEDIA4_RECEIVE:
                ((Media4Holder) holder).bindView(message, position);
                break;
            case ChatMessageType.TYPE_MEDIA5_SEND:
            case ChatMessageType.TYPE_MEDIA5_RECEIVE:
                ((Media5Holder) holder).bindView(message, position);
                break;

            case ChatMessageType.TYPE_MEDIA_MORE_SEND:
            case ChatMessageType.TYPE_MEDIA_MORE_RECEIVE:
                ((MediaMoreHolder) holder).bindView(message, position);
                break;
            default:
                break;
        }
    }

    //--FUNTION---//
    // seen massage
    public void markAllMessageAsRead() {
        for (ChatMessage message : mData) {
            if (message.isOwn && message.sendMesasgeStatus != ChatMessage.STATUS_ERROR)
                message.sendMesasgeStatus = ChatMessage.STATUS_SEEN;
        }
        notifyDataSetChanged();
    }

    // sending massage
    public void markSentMessage(String messageId) {
        // notify all message with status sending => sent success
        for (int i = 0; i < mData.size(); i++) {
            ChatMessage message = mData.get(i);
            if (message != null && ChatMessage.STATUS_SENDING == message.sendMesasgeStatus && messageId.equals(message.messageId)) {
                message.sendMesasgeStatus = ChatMessage.STATUS_SUCCESS;
                notifyItemChanged(i);
            }
        }
    }

    @Override
    public void onViewRecycled(BaseChatHolder holder) {
        super.onViewRecycled(holder);
    }

    // set time header
    private List<ChatMessage> setTimeHeader(List<ChatMessage> messages) {
        List<ChatMessage> messageHeader = new ArrayList<>();
        if (messages != null) {
            int j = 0;
            for (int i = messages.size() - 1; i >= 0; i--) {
                String pre = dateTime;
                ChatMessage originMsg = messages.get(i);
                // Check 2 values close together
                if (originMsg.mTimeStamp != null) {
                    dateTime = originMsg.mTimeStamp.substring(0, 8);

                    if (!dateTime.contains(pre)) {
                        String mMsgType = ChatMessage.HEADER_TIMER;
                        String stickerUrl = messages.get(i).stickerUrl;
                        String catId = messages.get(i).catId;
                        String mContent = messages.get(i).mContent;
                        boolean isOwn = messages.get(i).isOwn;
                        String mTimeStamp = messages.get(i).mTimeStamp;
                        messageHeader.add(new ChatMessage(mMsgType, isOwn, mContent, mTimeStamp, stickerUrl, catId));
                        messages.add(i + 1, messageHeader.get(j));
                        j++;
                    }
                }
            }
            return messages;
        }
        return null;
    }

    // convert massage Type File - >> image or video or audio
    private List<ChatMessage> parseFile(List<ChatMessage> data) {
        if (data == null || data.isEmpty()) return data;
        for (int i = 0; i < data.size(); i++) {
            ChatMessage msg = data.get(i);
            if (msg != null && msg.getMessageType().equals(ChatMessage.FILE)) {

                List<Media.FileBean> mFileList = msg.getListFile();
                if (mFileList != null && !mFileList.isEmpty()) {
                    // audio only 1 item
                    if (mFileList.size() == 1 && mFileList.get(0).fileType.equals(Media.FileBean.FILE_TYPE_AUDIO)) {
                        msg.mMsgType = ChatMessage.AUDIO;
                    } else {
                        msg.mMsgType = ChatMessage.MEDIA;
                    }
                    data.set(i, msg);
                }
            }
        }
        return data;
    }

    private void updateAudioStatus(final int position, final int audioType) {
        // cause default currentPlayingPosition = -1
        LogUtils.e(TAG, "updateAudioStatus :" + position + "currentPlayingPosition->>> " + currentPlayingPosition);
        if (position > -1) {
            ChatMessage message = getData(position);
            message.audioType = audioType;
            notifyItemChanged(position);
        }
    }

    /**
     * update file data from server
     *
     * @param chatMessage object to view
     */
    public void notifyLastMediaItem(ChatMessage chatMessage) {
        int index = mData.indexOf(chatMessage);
        if (index < 0) return;
        chatMessage.sendMesasgeStatus = ChatMessage.STATUS_SUCCESS;
        chatMessage.mTimeStamp = getTimeInLocale();
        LogUtils.e(TAG, "notifyLastMediaItem :" + chatMessage.mTimeStamp);
        mData.set(index, chatMessage);
        notifyItemChanged(index);
    }

    /**
     * if face any problem about socket, sending messages will not delivery => mark all sending messages  as error
     *
     * @param messageId to find item to change status
     * @see com.tux.socket.models.SocketEvent#EVENT_ERROR
     */
    public void markSendingMessagesFail(String messageId) {
        for (int i = 0; i < mData.size(); i++) {
            ChatMessage message = mData.get(i);
            if (message != null && ChatMessage.STATUS_SENDING == message.sendMesasgeStatus && message.messageId.equals(messageId)) {
                message.sendMesasgeStatus = ChatMessage.STATUS_ERROR;
                notifyItemChanged(i);
            }
        }
    }

    /**
     * @return all message id of sending or error message
     */
    public List<String> getAllSendingOrErrorMessages() {
        List<String> sendingOrErrorMessages = new ArrayList<>();
        for (ChatMessage message : mData) {
            if (message != null && (ChatMessage.STATUS_SENDING == message.sendMesasgeStatus || ChatMessage.STATUS_ERROR == message.sendMesasgeStatus))
                sendingOrErrorMessages.add(message.messageId);
        }
        return sendingOrErrorMessages;
    }

    /**
     * change item at position to sending status
     *
     * @param position to notify item
     */
    public ChatMessage markErrorMessageAsSending(int position) {
        ChatMessage chatMessage = mData.get(position);
        chatMessage.sendMesasgeStatus = ChatMessage.STATUS_SENDING;

        // move item to top
        mData.remove(position);
        mData.add(0, chatMessage);
        notifyItemRangeChanged(0, position + 1);
        return chatMessage;
    }

    @Override
    public void onError(int id) {
        updateAudioStatus(id, ChatMessage.STATE_AUDIO_ERROR);
    }

    @Override
    public void onPlay(int id) {
        updateAudioStatus(id, ChatMessage.STATE_AUDIO_PLAY);
    }

    @Override
    public void onPause(int id) {
        updateAudioStatus(id, ChatMessage.STATE_AUDIO_PAUSE);
    }

    @Override
    public void onPrepare(int id) {
        updateAudioStatus(id, ChatMessage.STATE_AUDIO_PREPARING);
    }

    @Override
    public void onComplete(int id) {
        updateAudioStatus(id, ChatMessage.STATE_AUDIO_COMPLETE);
    }
}
