package com.vn.ntsc.ui.livestream;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.vn.ntsc.R;
import com.vn.ntsc.repository.model.timeline.datas.sub.ListCommentBean;
import com.vn.ntsc.utils.ImagesUtils;
import com.vn.ntsc.widget.adapter.BaseAdapterListener;
import com.vn.ntsc.widget.adapter.BaseViewHolder;
import com.vn.ntsc.widget.adapter.MultifunctionAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by nankai on 12/22/2017.
 */

public class LiveStreamAdapter extends MultifunctionAdapter<LiveStreamAdapter.ViewHolder, ListCommentBean> {

    public LiveStreamAdapter(BaseAdapterListener listener) {
        super(listener);
    }

    @Override
    protected ViewHolder onInjectViewHolder(ViewGroup parent, int viewType) {
        return new LiveStreamAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_live_stream_adapter, parent, false));
    }

    @Override
    protected void onViewReady(ViewHolder holder, ListCommentBean item, int position) {
        holder.onBindView(item);
    }

    static class ViewHolder extends BaseViewHolder {
        @BindView(R.id.live_stream_user_name)
        TextView userName;
        @BindView(R.id.live_stream_comment)
        TextView comment;
        @BindView(R.id.live_stream_avatar)
        ImageView avatar;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, itemView);
        }

        public void onBindView(final ListCommentBean itemBean) {
            userName.setText(itemBean.userName);
            comment.setText(itemBean.commentValue);
            ImagesUtils.loadRoundedAvatar(itemBean.avatar, itemBean.gender, avatar);

        }
    }
}
