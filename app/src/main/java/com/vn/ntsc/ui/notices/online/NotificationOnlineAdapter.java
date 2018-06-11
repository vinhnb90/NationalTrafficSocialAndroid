package com.vn.ntsc.ui.notices.online;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.vn.ntsc.R;
import com.vn.ntsc.repository.model.notification.OnlineNotificationItem;
import com.vn.ntsc.utils.ImagesUtils;
import com.vn.ntsc.widget.adapter.BaseViewHolder;
import com.vn.ntsc.widget.adapter.MultifunctionAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ThoNh on 9/1/2017.
 */

public class NotificationOnlineAdapter extends MultifunctionAdapter<NotificationOnlineAdapter.ViewHolder, OnlineNotificationItem> {

    private OnlineNotificationEvent mOnlineNotificationEvent;

    public NotificationOnlineAdapter(OnlineNotificationEvent listener) {
        super(null);
        this.mOnlineNotificationEvent = listener;
    }

    @Override
    protected ViewHolder onInjectViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_online_notification, parent, false));
    }

    @Override
    protected void onViewReady(final ViewHolder holder, final OnlineNotificationItem item, final int position) {

        ImagesUtils.loadRoundedAvatar(item.avaUrl, item.gender, holder.mItemAvatar);

        holder.mItemName.setText(item.userName);

        holder.mItemBtnAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnlineNotificationEvent != null) {
                    mOnlineNotificationEvent.onItemActionClick(holder.mItemBtnAction, item, position);
                }
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnlineNotificationEvent != null) {
                    mOnlineNotificationEvent.onItemClick(holder.itemView, item, position);
                }
            }
        });
    }

    public static class ViewHolder extends BaseViewHolder {

        @BindView(R.id.item_avatar)
        ImageView mItemAvatar;
        @BindView(R.id.item_name)
        TextView mItemName;
        @BindView(R.id.item_btn_action)
        TextView mItemBtnAction;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public interface OnlineNotificationEvent {
        void onItemActionClick(View view, OnlineNotificationItem item, int position);

        void onItemClick(View view, OnlineNotificationItem item, int position);
    }
}
