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
 * for send/receive file include 5
 */
public class Media5Holder extends MediaHolder {
    @BindView(R.id.item_timeline_image_view)
    RecyclingImageView image;

    @BindView(R.id.item_timeline_image_view_2)
    RecyclingImageView image2;

    @BindView(R.id.item_timeline_image_view_3)
    RecyclingImageView image3;

    @BindView(R.id.item_timeline_image_view_4)
    RecyclingImageView image4;

    @BindView(R.id.item_timeline_image_view_5)
    RecyclingImageView image5;

    @BindView(R.id.item_timeline_play_video)
    RecyclingImageView videoIcon;

    @BindView(R.id.item_timeline_play_video_2)
    RecyclingImageView videoIcon2;

    @BindView(R.id.item_timeline_play_video_3)
    RecyclingImageView videoIcon3;

    @BindView(R.id.item_timeline_play_video_4)
    RecyclingImageView videoIcon4;

    @BindView(R.id.item_timeline_play_video_5)
    RecyclingImageView videoIcon5;

    public Media5Holder(int gender, String avatarUrl, View itemView, MessageOnEventListener mMessageOnEventListener) {
        super(gender, avatarUrl, itemView, mMessageOnEventListener);
    }

    @Override
    public void bindView(ChatMessage chatMessage, int position) {
        super.bindView(chatMessage, position);
        Media.FileBean fileBean = chatMessage.listFile.get(0);
        videoIcon.setVisibility(fileBean.fileType.equals(Media.FileBean.FILE_TYPE_VIDEO) ? View.VISIBLE : View.INVISIBLE);
        ImagesUtils.loadImageSend(fileBean.thumbnailUrl, image);

        Media.FileBean fileBean2 = chatMessage.listFile.get(1);
        videoIcon2.setVisibility(fileBean2.fileType.equals(Media.FileBean.FILE_TYPE_VIDEO) ? View.VISIBLE : View.INVISIBLE);
        ImagesUtils.loadImageSend(fileBean2.thumbnailUrl, image2);

        Media.FileBean fileBean3 = chatMessage.listFile.get(2);
        videoIcon3.setVisibility(fileBean3.fileType.equals(Media.FileBean.FILE_TYPE_VIDEO) ? View.VISIBLE : View.INVISIBLE);
        ImagesUtils.loadImageSend(fileBean3.thumbnailUrl, image3);

        Media.FileBean fileBean4 = chatMessage.listFile.get(3);
        videoIcon4.setVisibility(fileBean4.fileType.equals(Media.FileBean.FILE_TYPE_VIDEO) ? View.VISIBLE : View.INVISIBLE);
        ImagesUtils.loadImageSend(fileBean4.thumbnailUrl, image4);

        Media.FileBean fileBean5 = chatMessage.listFile.get(4);
        videoIcon5.setVisibility(fileBean5.fileType.equals(Media.FileBean.FILE_TYPE_VIDEO) ? View.VISIBLE : View.INVISIBLE);
        ImagesUtils.loadImageSend(fileBean5.thumbnailUrl, image5);

        setOnClickMedia(image, chatMessage, 0);
        setOnClickMedia(image2, chatMessage, 1);
        setOnClickMedia(image3, chatMessage, 2);
        setOnClickMedia(image4, chatMessage, 3);
        setOnClickMedia(image5, chatMessage, 4);
    }
}
