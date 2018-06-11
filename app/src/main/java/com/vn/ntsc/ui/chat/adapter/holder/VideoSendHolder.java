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
public class VideoSendHolder extends BaseChatHolder<ChatMessage> {

    @BindView(R.id.tv_video_send)
    TextView mTvTime;

    @BindView(R.id.img_video_send)
    ImageView imgVideoSend;

    @BindView(R.id.tv_status_send_video)
    TextView tvStatus;

    private Context mContext;

    public VideoSendHolder(MessageOnEventListener mMessageOnEventListener, View itemView) {
        super(itemView, mMessageOnEventListener);
        mContext = itemView.getContext();
    }

    @Override
    public void bindView(final ChatMessage message, final int position) {
//        initRecyclerView(position, message);
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
        setMessageStatus(tvStatus, message);

        //null check
        // TODO: 2/27/18
        if (message.listFile != null) {
            if (message.listFile.size() != 0) {
//                ImagesUtils.loadImageSend(message.tempFile.get(0), imgVideoSend);
            } else {
                ImagesUtils.loadImageSend(message.getListFile().get(0).thumbnailUrl, imgVideoSend);
            }
        }

        imgVideoSend.setOnClickListener(new View.OnClickListener() {
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

        LogUtils.e("ducnv", "getImageFromUrl:" + message.mTimeStamp);
        SimpleDateFormat sdfDay = new SimpleDateFormat("hh:mm", Locale.getDefault());
        String a = sdfDay.format(new Date(Utils.convertTimeToMilisecond(Utils.convertGMTtoLocale(message.mTimeStamp))));
        mTvTime.setText(a);
    }


//    private void initRecyclerView(int position, ChatMessage chatMessage) {
//        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
//        mRecyclerView.setLayoutManager(layoutManager);
//        VideoSendHolder.VideoSendAdapter videoSendAdapter = new VideoSendHolder.VideoSendAdapter(mMessageOnEventListener, position, chatMessage);
//        mRecyclerView.setAdapter(videoSendAdapter);
//    }


    public class VideoSendAdapter extends RecyclerView.Adapter<VideoSendAdapter.ViewViewHolder> {
        private int position;
        private ChatMessage chatMessage;
        private MessageOnEventListener mMessageOnEventListener;

        public VideoSendAdapter(MessageOnEventListener mMessageOnEventListener, int position, ChatMessage chatMessage) {
            this.position = position;
            this.chatMessage = chatMessage;
            this.mMessageOnEventListener = mMessageOnEventListener;
        }

        @Override
        public ViewViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_send_video, viewGroup, false);
            return new ViewViewHolder(v, chatMessage, TypeView.MediaDetailType.VIDEO_TYPE);
        }

        @Override
        public void onBindViewHolder(ViewViewHolder viewHolder, int i) {
            // TODO: 2/27/18
            if (chatMessage.listFile.size() != 0) {
//                ImagesUtils.loadImageSend(chatMessage.tempFile.get(i), viewHolder.imgVideoSend);
            } else {
                ImagesUtils.loadImageSend(chatMessage.getListFile().get(i).thumbnailUrl, viewHolder.imgVideoSend);
            }
            if (viewHolder.imgVideoSend != null) {
                viewHolder.imgVideoSend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Listen onClick Event and push to ChatActivity for handle by request

                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            // TODO: 2/27/18
            if (this.chatMessage.listFile.size() != 0) {
//                return this.chatMessage.tempFile.size();
                return 0;
            } else {
                return this.chatMessage.getListFile().size();
            }
        }

        public class ViewViewHolder extends BaseViewHolder {
            private ImageView imgVideoSend;

            public ViewViewHolder(View itemView, ChatMessage chatMessage, String type) {
                super(itemView, chatMessage, type);
                imgVideoSend = itemView.findViewById(R.id.view_video);
                //imgVideoSend.setOnClickListener(this);
            }

        }
    }

}
