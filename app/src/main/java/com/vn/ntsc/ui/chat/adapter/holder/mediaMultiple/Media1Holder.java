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
 * for send/receive file include one
 */
public class Media1Holder extends MediaHolder {
    @BindView(R.id.item_timeline_image_view)
    RecyclingImageView image;

    @BindView(R.id.item_timeline_play_video)
    RecyclingImageView videoIcon;

    public Media1Holder(int gender,String avatar, View itemView, MessageOnEventListener mMessageOnEventListener) {
        super(gender,avatar,itemView, mMessageOnEventListener);
    }

    @Override
    public void bindView(ChatMessage chatMessage, int position) {
        super.bindView(chatMessage, position);
        Media.FileBean fileBean = chatMessage.listFile.get(0);

        // show icon video file
        videoIcon.setVisibility(fileBean.fileType.equals(Media.FileBean.FILE_TYPE_VIDEO) ? View.VISIBLE : View.INVISIBLE);
        ImagesUtils.loadImageSend(fileBean.thumbnailUrl, image);

        setOnClickMedia(image, chatMessage, 0);
    }
}
