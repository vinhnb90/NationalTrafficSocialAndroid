package com.vn.ntsc.ui.chat.adapter.holder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.vn.ntsc.R;
import com.vn.ntsc.repository.model.chat.ChatMessage;
import com.vn.ntsc.utils.ImagesUtils;
import com.vn.ntsc.utils.LogUtils;
import com.vn.ntsc.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;

/**
 * Created by ThoNh on 9/26/2017.
 */
@Deprecated
public class PhotoSendHolder extends BaseChatHolder<ChatMessage> {
    public static final String TAG = PhotoSendHolder.class.getSimpleName();
    public static final String SENDING = "sending";
    public static final String SENDT = "sendt";

    public static final int IMAGE_SIZE = 500;
    @BindView(R.id.img_photo_send)
    ImageView imgSend;

    @BindView(R.id.tv_time_photo_send)
    TextView tvTime;

    @BindView(R.id.tv_status_send_photo)
    TextView tvStatus;

    private Context mContext;

    public PhotoSendHolder(MessageOnEventListener mMessageOnEventListener, View itemView) {
        super(itemView, mMessageOnEventListener);
        mContext = itemView.getContext();
    }

    @Override
    public void bindView(final ChatMessage message, final int position) {
//        initRecyclerView(position, message);
        // status send
        LogUtils.d(TAG, "getContent photo " + position);

        setMessageStatus(tvStatus, message);

//        switch (message.sendMesasgeStatus) {
//            case ChatMessage.STATUS_ERROR:
//                tvStatus.setText(R.string.common_error);
//                break;
//            case ChatMessage.STATUS_SEEN:
//                tvStatus.setText(R.string.seen);
//                break;
//            case ChatMessage.STATUS_SENDING:
//                tvStatus.setText(R.string.sending);
//                break;
//            case ChatMessage.STATUS_SUCCESS:
//                tvStatus.setText(R.string.sent);
//                break;
//            default:
//                break;
//        }
//        if (message.getReadTime() == null) {
//            LogUtils.d(TAG, "isSeen1 " + message.isSeen);
//            tvStatus.setText(R.string.sent);
//        } else if (message.getReadTime().equals(SENDING)) {
//            LogUtils.d(TAG, "isSeen2 " + message.isSeen);
//            tvStatus.setText(R.string.sending);
//        } else if (message.getReadTime().equals(SENDT)) {
//            LogUtils.d(TAG, "isSeen3 " + message.isSeen);
//            tvStatus.setText(R.string.sent);
//        } else if (message.getReadTime().equals("")) {
//            LogUtils.d(TAG, "isSeen4 " + message.isSeen);
//            tvStatus.setText(R.string.sent);
//        } else if (message.getReadTime() != null) {
//            LogUtils.d(TAG, "isSeen5 " + message.isSeen);
//            tvStatus.setText(R.string.seen);
//        }
//
//        if (message.isSeen) {
//            tvStatus.setText(R.string.seen);
//        }

        if (message.getListFile() != null) {
            LogUtils.e(TAG, "getListFile " + message.getListFile().get(0).thumbnailUrl);
            ImagesUtils.loadImageSend(message.getListFile().get(0).thumbnailUrl, imgSend);
        } else {
            // TODO: 2/27/18
//            ImagesUtils.loadImageSend(message.tempFile.get(0), imgSend);
        }

        imgSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mMessageOnEventListener != null) {
                    if (message.listFile != null) {
//                        mMessageOnEventListener.onClick(view, ChatMessageType.TYPE_PHOTO_SEND, position, message, convertToListMediaEntity(message.listFile, TypeView.MediaDetailType.IMAGE_TYPE));
                    } else {
                        // TODO: 2/27/18
//                        mMessageOnEventListener.onClick(view, ChatMessageType.TYPE_PHOTO_SEND, position, message, convertToListLocalMediaEntity(message.tempFile, TypeView.MediaDetailType.IMAGE_TYPE));
                    }
                }
            }
        });


        // time send message
        SimpleDateFormat sdfDay = new SimpleDateFormat("hh:mm", Locale.getDefault());
        String currenTime = sdfDay.format(new Date(Utils.convertTimeToMilisecond(Utils.convertGMTtoLocale(message.mTimeStamp))));
        tvTime.setText(currenTime);
    }


//    private void initRecyclerView(int position, ChatMessage chatMessage) {
//        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
//        mRecyclerView.setLayoutManager(layoutManager);
//        PhotoSendAdapter photoSendAdapter = new PhotoSendAdapter(mMessageOnEventListener, position, chatMessage);
//        mRecyclerView.setAdapter(photoSendAdapter);
//    }

    public class PhotoSendAdapter extends RecyclerView.Adapter<PhotoSendAdapter.ViewHolder> {
        private int position;
        private ChatMessage chatMessage;
        private MessageOnEventListener mMessageOnEventListener;

        public PhotoSendAdapter(MessageOnEventListener mMessageOnEventListener, int position, ChatMessage chatMessage) {
            this.position = position;
            this.chatMessage = chatMessage;
            this.mMessageOnEventListener = mMessageOnEventListener;
        }

        @Override
        public PhotoSendAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_send_photo, viewGroup, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(PhotoSendAdapter.ViewHolder viewHolder, int i) {
            //LogUtils.e(TAG, "datahistory " +chatMessage.getListFile().get(i).thumbnailUrl);
            // TODO: 2/27/18
            if (chatMessage.listFile.size() != 0) {
//                ImagesUtils.loadImageSend(chatMessage.tempFile.get(i), viewHolder.imgSend);
            } else {
                LogUtils.e(TAG, "onBindViewHolder " + chatMessage.getListFile().size());
                ImagesUtils.loadImageSend(chatMessage.getListFile().get(i).thumbnailUrl, viewHolder.imgSend);
            }
            if (viewHolder.imgSend != null) {
                viewHolder.imgSend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Listen onClick Event and push to ChatActivity for handle by request

                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            if (this.chatMessage.listFile.size() != 0) {
                LogUtils.e(TAG, "getItemCount ");
                // TODO: 2/27/18
//                return this.chatMessage.listFile.size();
                return 1;
            } else {
                LogUtils.e(TAG, "getItemCount " + this.chatMessage.getListFile().size());
                return this.chatMessage.getListFile().size();
            }
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            ImageView imgSend;

            public ViewHolder(View itemView) {
                super(itemView);
                imgSend = itemView.findViewById(R.id.img_photo_send);
            }
        }
    }

}
