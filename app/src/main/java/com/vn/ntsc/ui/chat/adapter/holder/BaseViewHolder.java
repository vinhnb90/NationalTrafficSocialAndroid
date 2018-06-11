package com.vn.ntsc.ui.chat.adapter.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.tux.socket.models.Media;
import com.vn.ntsc.repository.model.chat.ChatMessage;
import com.vn.ntsc.repository.model.media.MediaEntity;
import com.vn.ntsc.ui.mediadetail.album.AlbumDetailMediaActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dev22 on 12/23/17.
 */
public abstract class BaseViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private final ChatMessage chatMessage;
    private final String type;

    public BaseViewHolder(View itemView, ChatMessage chatMessage, String type) {
        super(itemView);
        this.type = type;
        this.chatMessage = chatMessage;
    }

    /**
     * Convert list fileBean to MediaList to preview
     * @param listFile
     * @param type
     * @return
     */
    public List<MediaEntity> convertToListMediaEntity(List<Media.FileBean> listFile, String type) {
        if (listFile == null) return null;
        else {
            List<MediaEntity> datas = new ArrayList<>();
            for (int i = 0; i < listFile.size(); i++) {
                Media.FileBean fileBean = listFile.get(i);
                datas.add(new MediaEntity(i, fileBean.fileUrl, type, fileBean.thumbnailUrl));
            }
            return datas;
        }
    }

    @Override
    public void onClick(View v) {
        AlbumDetailMediaActivity.launch(v.getContext(), convertToListMediaEntity(chatMessage.listFile, type), 0);
    }
}
