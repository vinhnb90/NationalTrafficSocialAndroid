package com.vn.ntsc.ui.chat.adapter.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.tux.socket.models.Media;
import com.vn.ntsc.R;
import com.vn.ntsc.core.model.BaseBean;
import com.vn.ntsc.repository.model.chat.ChatMessage;
import com.vn.ntsc.repository.model.media.MediaEntity;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

/**
 * Created by ThoNh on 9/26/2017.
 */

public abstract class BaseChatHolder<E extends BaseBean> extends RecyclerView.ViewHolder {

    abstract public void bindView(E message, int position);

    public interface MessageOnEventListener {
        void onItemClickSenMessageError(String messageId, int position);

        void onItemClickEmailMessage(String email);

        void onItemClickPhoneMessage(String phone);

        void onItemClickWebLinkMessage(String webLink);

        void onClick(final View mView, final int viewType, final int position, final ChatMessage chatMessage, final List<MediaEntity> mMediaFileList);

        void onClickMedia(ChatMessage listFile, int indexInListMedia);
    }

    protected MessageOnEventListener mMessageOnEventListener;

    /**
     * Use the for object none listener, eg: HeaderHolder
     *
     * @param itemView
     */
    public BaseChatHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public BaseChatHolder(View itemView, MessageOnEventListener mMessageOnEventListener) {
        super(itemView);
        this.mMessageOnEventListener = mMessageOnEventListener;
        ButterKnife.bind(this, itemView);
    }

    /**
     * Convert list fileBean to MediaList to preview
     *
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

    /**
     * Convert list fileBean to MediaList to preview
     *
     * @param listFile
     * @param type
     * @return
     */
    public List<MediaEntity> convertToListLocalMediaEntity(List<String> listFile, String type) {
        if (listFile == null) return null;
        else {
            List<MediaEntity> datas = new ArrayList<>();
            for (int i = 0; i < listFile.size(); i++) {
                String filePath = listFile.get(i);
                datas.add(new MediaEntity(i, filePath, type, filePath));
            }
            return datas;
        }
    }

    protected void setMessageStatus(TextView tvStatus, ChatMessage message){
        switch (message.sendMesasgeStatus) {
            case ChatMessage.STATUS_ERROR:
                tvStatus.setText(R.string.common_error);
                break;
            case ChatMessage.STATUS_SEEN:
                tvStatus.setText(R.string.common_seen);
                break;
            case ChatMessage.STATUS_SENDING:
                tvStatus.setText(R.string.common_sending);
                break;
            case ChatMessage.STATUS_SUCCESS:
                tvStatus.setText(R.string.common_sent_success);
                break;
            default:
                break;
        }
    }
}
