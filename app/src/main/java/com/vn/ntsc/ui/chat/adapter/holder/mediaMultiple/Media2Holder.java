package com.vn.ntsc.ui.chat.adapter.holder.mediaMultiple;

import android.view.View;

import com.tux.socket.models.Media;
import com.vn.ntsc.R;
import com.vn.ntsc.repository.model.chat.ChatMessage;
import com.vn.ntsc.utils.ImagesUtils;
import com.vn.ntsc.widget.views.images.RecyclingImageView;

import butterknife.BindView;

/**
 * Created by dev22 on 2/27/18.
 * for send/receive file include 2
 */
public class Media2Holder extends MediaHolder {
    @BindView(R.id.item_timeline_image_view)
    RecyclingImageView image;

    @BindView(R.id.item_timeline_image_view_2)
    RecyclingImageView image2;

    @BindView(R.id.item_timeline_play_video)
    RecyclingImageView videoIcon;

    @BindView(R.id.item_timeline_play_video_2)
    RecyclingImageView videoIcon2;

    public Media2Holder(int gender, String avaterUrl, View itemView, MessageOnEventListener mMessageOnEventListener) {
        super(gender, avaterUrl, itemView, mMessageOnEventListener);
    }

    @Override
    public void bindView(ChatMessage chatMessage, int position) {
        super.bindView(chatMessage, position);
        // 1
        Media.FileBean fileBean = chatMessage.listFile.get(0);
        videoIcon.setVisibility(fileBean.fileType.equals(Media.FileBean.FILE_TYPE_VIDEO) ? View.VISIBLE : View.INVISIBLE);
        ImagesUtils.loadImageSend(fileBean.thumbnailUrl, image);

        // 2
        Media.FileBean fileBean2 = chatMessage.listFile.get(1);
        videoIcon2.setVisibility(fileBean2.fileType.equals(Media.FileBean.FILE_TYPE_VIDEO) ? View.VISIBLE : View.INVISIBLE);
        ImagesUtils.loadImageSend(fileBean2.thumbnailUrl, image2);

        setOnClickMedia(image, chatMessage, 0);
        setOnClickMedia(image2, chatMessage, 1);
    }
}
