package com.vn.ntsc.ui.chat.adapter.holder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.vn.ntsc.R;
import com.vn.ntsc.repository.TypeView;
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
public class VideoReciverHolder extends BaseChatHolder<ChatMessage> {
    @BindView(R.id.avatar)
    ImageView mAvatar;

    @BindView(R.id.tv_time_video_receiver)
    TextView mTvTime;

    @BindView(R.id.img_video_receiver)
    ImageView imgVideoReceiver;

    private Context mContext;


    public VideoReciverHolder(MessageOnEventListener mMessageOnEventListener, String avatarUrl, View itemView) {
        super(itemView, mMessageOnEventListener);
//        ImagesUtils.loadRoundedAvatar(avatarUrl, mAvatar);
        mContext = itemView.getContext();


    }

    @Override
    public void bindView(final ChatMessage message, final int position) {
        LogUtils.d("bindView", "111 " + message.mTimeStamp);

        // TODO: 2/27/18
        if (message.listFile.size() != 0) {
//            ImagesUtils.loadImageSend(message.tempFile.get(0), imgVideoReceiver);
        } else {
            ImagesUtils.loadImageSend(message.getListFile().get(0).thumbnailUrl, imgVideoReceiver);
        }

        imgVideoReceiver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mMessageOnEventListener != null) {
                    if (message.listFile != null) {
//                        mMessageOnEventListener.onClick(view, ChatMessageType.TYPE_PHOTO_RECEIVER, position, message, convertToListMediaEntity(message.listFile, TypeView.MediaDetailType.VIDEO_TYPE));
                    } else {
                        // TODO: 2/27/18
//                        mMessageOnEventListener.onClick(view, ChatMessageType.TYPE_PHOTO_RECEIVER, position, message, convertToListLocalMediaEntity(message.tempFile, TypeView.MediaDetailType.VIDEO_TYPE));
                    }
                }
            }
        });

        SimpleDateFormat sdfDay = new SimpleDateFormat("hh:mm", Locale.getDefault());
        String a = sdfDay.format(new Date(Utils.convertTimeToMilisecond(Utils.convertGMTtoLocale(message.mTimeStamp))));
        mTvTime.setText(a);
    }

//    private void initRecyclerView(int position, ChatMessage chatMessage) {
//        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
//        mRecyclerView.setLayoutManager(layoutManager);
//        VideoReceiverAdapter videoReciverAdapter = new VideoReceiverAdapter(mMessageOnEventListener, position, chatMessage);
//        mRecyclerView.setAdapter(videoReciverAdapter);
//
//    }

    public class VideoReceiverAdapter extends RecyclerView.Adapter<VideoReceiverAdapter.ViewHolder> {
        private int position;
        private ChatMessage chatMessage;
        private MessageOnEventListener mMessageOnEventListener;

        public VideoReceiverAdapter(MessageOnEventListener mMessageOnEventListener, int position, ChatMessage chatMessage) {
            this.position = position;
            this.chatMessage = chatMessage;
            this.mMessageOnEventListener = mMessageOnEventListener;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_receiver_video, viewGroup, false);
            return new ViewHolder(v, chatMessage, TypeView.MediaDetailType.VIDEO_TYPE);
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            ImagesUtils.loadImageSend(chatMessage.getListFile().get(i).thumbnailUrl, viewHolder.imgReceiverVideo);
            if (viewHolder.imgReceiverVideo != null) {
                viewHolder.imgReceiverVideo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Listen onClick Event and push to ChatActivity for handle by request

                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return this.chatMessage.getListFile().size();
        }

        public class ViewHolder extends BaseViewHolder {
            private ImageView imgReceiverVideo;

            public ViewHolder(View itemView, ChatMessage chatMessage, String type) {
                super(itemView, chatMessage, type);
                imgReceiverVideo = itemView.findViewById(R.id.item_video_receiver);
                //imgReceiverVideo.setOnClickListener(this);
            }
        }
    }


}
