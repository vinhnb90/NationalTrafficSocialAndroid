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
public class PhotoReceiverHolder extends BaseChatHolder<ChatMessage> {
    public static final String TAG = PhotoReceiverHolder.class.getSimpleName();
    private Context mContext;

    @BindView(R.id.avatar)
    ImageView mAvatar;

    @BindView(R.id.img_photo_receiver)
    ImageView imgReceiver;

    @BindView(R.id.tv_time_photo_receiver)
    TextView tvTime;


    public PhotoReceiverHolder(MessageOnEventListener mMessageOnEventListener, String avatarUrl, View itemView) {
        super(itemView, mMessageOnEventListener);
        this.mContext = itemView.getContext();
//        ImagesUtils.loadRoundedAvatar(avatarUrl, mAvatar);
    }

    @Override
    public void bindView(final ChatMessage message, final int position) {
        LogUtils.d("bindView", "1111");
//        initRecyclerView(position, message);

        // TODO: 2/27/18
        if (message.listFile.size() != 0) {
//            ImagesUtils.loadImageSend(message.tempFile.get(0), imgReceiver);
        } else {
            LogUtils.e(TAG, "getListFile ");
            ImagesUtils.loadImageSend(message.getListFile().get(0).thumbnailUrl, imgReceiver);
        }

        imgReceiver.setOnClickListener(new View.OnClickListener() {
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


        SimpleDateFormat sdfDay = new SimpleDateFormat("hh:mm", Locale.getDefault());
        String currentTime = sdfDay.format(new Date(Utils.convertTimeToMilisecond(Utils.convertGMTtoLocale(message.mTimeStamp))));
        tvTime.setText(currentTime);
    }


//    private void initRecyclerView(int position, ChatMessage chatMessage) {
//        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
//        mRecyclerView.setLayoutManager(layoutManager);
//        PhotoReceiverAdapter receiverAdapter = new PhotoReceiverAdapter(mMessageOnEventListener, position, chatMessage);
//        mRecyclerView.setAdapter(receiverAdapter);
//
//    }

    public class PhotoReceiverAdapter extends RecyclerView.Adapter<PhotoReceiverAdapter.ViewHolder> {
        private int position;
        private ChatMessage chatMessage;
        private MessageOnEventListener mMessageOnEventListener;

        public PhotoReceiverAdapter(MessageOnEventListener mMessageOnEventListener, int position, ChatMessage chatMessage) {
            this.position = position;
            this.chatMessage = chatMessage;
            this.mMessageOnEventListener = mMessageOnEventListener;
        }

        @Override
        public PhotoReceiverAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_receiver_photo, viewGroup, false);
            return new PhotoReceiverHolder.PhotoReceiverAdapter.ViewHolder(v/*, chatMessage, TypeView.MediaDetailType.IMAGE_TYPE*/);
        }

        @Override
        public void onBindViewHolder(PhotoReceiverAdapter.ViewHolder viewHolder, int i) {
            ImagesUtils.loadImageSend(chatMessage.getListFile().get(i).thumbnailUrl, viewHolder.imgReceiver);
            if (viewHolder.imgReceiver != null) {
                viewHolder.imgReceiver.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Listen onClick Event and push to ChatActivity for handle by request
                        if (mMessageOnEventListener != null) {

                            if (chatMessage.listFile != null) {
//                                mMessageOnEventListener.onClick(view, ChatMessageType.TYPE_PHOTO_SEND, position, chatMessage, convertToListMediaEntity(chatMessage.listFile, TypeView.MediaDetailType.IMAGE_TYPE));
                            } else {
                                // TODO: 2/27/18  
//                                mMessageOnEventListener.onClick(view, ChatMessageType.TYPE_PHOTO_SEND, position, chatMessage, convertToListLocalMediaEntity(chatMessage.tempFile, TypeView.MediaDetailType.IMAGE_TYPE));
                            }

                        }
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return this.chatMessage.getListFile().size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            ImageView imgReceiver;

            public ViewHolder(View itemView) {
                super(itemView);
                imgReceiver = itemView.findViewById(R.id.img_photo_receiver);
            }
        }

        /*public class HeaderViewHolder extends BaseViewHolder {
            private ImageView imgReceiver;

            public HeaderViewHolder(View itemView, ChatMessage chatMessage, String type) {
                super(itemView, chatMessage, type);
                imgReceiver = itemView.findViewById(R.id.img_photo_receiver);
                imgReceiver.setOnClickListener(this);
            }

        }*/

    }


}
