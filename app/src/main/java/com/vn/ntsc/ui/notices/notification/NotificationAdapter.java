package com.vn.ntsc.ui.notices.notification;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.vn.ntsc.R;
import com.vn.ntsc.repository.model.notification.NotificationItem;
import com.vn.ntsc.utils.time.TimeUtils;
import com.vn.ntsc.widget.adapter.BaseAdapterListener;
import com.vn.ntsc.widget.adapter.BaseViewHolder;
import com.vn.ntsc.widget.adapter.MultifunctionAdapter;

import java.text.ParseException;
import java.util.Calendar;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.vn.ntsc.repository.model.notification.push.NotificationType.*;
import static com.vn.ntsc.repository.model.notification.push.NotificationType.NOTI_APART_OF_USERINFO;
import static com.vn.ntsc.repository.model.notification.push.NotificationType.NOTI_APPROVED_BUZZ;
import static com.vn.ntsc.repository.model.notification.push.NotificationType.NOTI_APPROVE_BUZZ_TEXT;
import static com.vn.ntsc.repository.model.notification.push.NotificationType.NOTI_APPROVE_COMMENT;
import static com.vn.ntsc.repository.model.notification.push.NotificationType.NOTI_APPROVE_SUB_COMMENT;
import static com.vn.ntsc.repository.model.notification.push.NotificationType.NOTI_APPROVE_USERINFO;
import static com.vn.ntsc.repository.model.notification.push.NotificationType.NOTI_AUDIO_SHARE_BUZZ;
import static com.vn.ntsc.repository.model.notification.push.NotificationType.NOTI_BACKSTAGE_APPROVED;
import static com.vn.ntsc.repository.model.notification.push.NotificationType.NOTI_CHAT;
import static com.vn.ntsc.repository.model.notification.push.NotificationType.NOTI_CHECK_OUT_UNLOCK;
import static com.vn.ntsc.repository.model.notification.push.NotificationType.NOTI_COMMENT_BUZZ;
import static com.vn.ntsc.repository.model.notification.push.NotificationType.NOTI_COMMENT_OTHER_BUZZ;
import static com.vn.ntsc.repository.model.notification.push.NotificationType.NOTI_DAYLY_BONUS;
import static com.vn.ntsc.repository.model.notification.push.NotificationType.NOTI_DEFAULT;
import static com.vn.ntsc.repository.model.notification.push.NotificationType.NOTI_DENIED_BACKSTAGE;
import static com.vn.ntsc.repository.model.notification.push.NotificationType.NOTI_DENIED_BUZZ_IMAGE;
import static com.vn.ntsc.repository.model.notification.push.NotificationType.NOTI_DENIED_BUZZ_TEXT;
import static com.vn.ntsc.repository.model.notification.push.NotificationType.NOTI_DENIED_COMMENT;
import static com.vn.ntsc.repository.model.notification.push.NotificationType.NOTI_DENIED_USERINFO;
import static com.vn.ntsc.repository.model.notification.push.NotificationType.NOTI_DENI_SUB_COMMENT;
import static com.vn.ntsc.repository.model.notification.push.NotificationType.NOTI_FAVORITED_CREATE_BUZZ;
import static com.vn.ntsc.repository.model.notification.push.NotificationType.NOTI_FAVORITED_UNLOCK;
import static com.vn.ntsc.repository.model.notification.push.NotificationType.NOTI_FRIEND;
import static com.vn.ntsc.repository.model.notification.push.NotificationType.NOTI_FROM_FREE_PAGE;
import static com.vn.ntsc.repository.model.notification.push.NotificationType.NOTI_LIKE_BUZZ;
import static com.vn.ntsc.repository.model.notification.push.NotificationType.NOTI_LIKE_OTHER_BUZZ;
import static com.vn.ntsc.repository.model.notification.push.NotificationType.NOTI_LIVESTREAM_FROM_FAVOURIST;
import static com.vn.ntsc.repository.model.notification.push.NotificationType.NOTI_NEWS_BUZZ;
import static com.vn.ntsc.repository.model.notification.push.NotificationType.NOTI_ONLINE_ALERT;
import static com.vn.ntsc.repository.model.notification.push.NotificationType.NOTI_QA_BUZZ;
import static com.vn.ntsc.repository.model.notification.push.NotificationType.NOTI_REPLY_YOUR_COMMENT;
import static com.vn.ntsc.repository.model.notification.push.NotificationType.NOTI_SHARE_LIVE_STREAM;
import static com.vn.ntsc.repository.model.notification.push.NotificationType.NOTI_TAG_BUZZ;
import static com.vn.ntsc.repository.model.notification.push.NotificationType.NOTI_TAG_LIVESTREAM_FROM_FAVOURIST;
import static com.vn.ntsc.repository.model.notification.push.NotificationType.NOTI_UNLOCK_BACKSTAGE;

/**
 * Created by ThoNh on 8/30/2017.
 */

public class NotificationAdapter extends MultifunctionAdapter<NotificationAdapter.ViewHolder, NotificationItem> {

    private INotificationAdapterEvent listener;

    public NotificationAdapter() {
        super(null);
    }

    @Override
    protected void onViewReady(final ViewHolder holder, final NotificationItem item, final int position) {
        setData(holder, item);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onItemClick(holder.itemView, item, position);
                }
            }
        });
    }

    @Override
    protected ViewHolder onInjectViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false));
    }

    public void setEventListener(INotificationAdapterEvent event) {
        this.listener = event;
    }

    private void setData(ViewHolder holder, NotificationItem item) {
        Context mContext = holder.getViewRoot().getContext();

        String msg = "";
        int iconRes = 0;
        switch (item.notiType) {
            case NOTI_DEFAULT:
                break;
            case NOTI_CHECK_OUT_UNLOCK:
                break;
            case NOTI_FAVORITED_UNLOCK:
                break;
            case NOTI_LIKE_BUZZ:
                iconRes = R.drawable.ic_notification_like_buzz;
                msg = String.format(mContext.getString(R.string.buzz_liked_to_notification), item.notiUserName);
                break;
            case NOTI_LIKE_OTHER_BUZZ:
                break;
            case NOTI_COMMENT_BUZZ:
                iconRes = R.drawable.ic_notification_comment_buzz;
                msg = String.format(mContext.getString(R.string.buzz_responded_to_notification), item.notiUserName);
                break;
            case NOTI_COMMENT_OTHER_BUZZ:
                break;
            case NOTI_UNLOCK_BACKSTAGE:
                break;
            case NOTI_FRIEND:
                break;
            case NOTI_CHAT:
                break;
            case NOTI_ONLINE_ALERT:
                iconRes = R.drawable.ic_notification_online_alert;
                msg = String.format(mContext.getString(R.string.came_online_notification), item.notiUserName);
                break;
            case NOTI_DAYLY_BONUS:
                iconRes = R.drawable.ic_notification_point_bonus;
                msg = mContext.getString(R.string.earned_point_notification);
                break;
            case NOTI_APPROVED_BUZZ:
            case NOTI_BACKSTAGE_APPROVED:
                iconRes = R.drawable.ic_notification_image_approved;
                msg = mContext.getString(R.string.image_approved);
                break;
            case NOTI_FROM_FREE_PAGE:
                iconRes = R.drawable.ic_notification_settings;
                msg = item.content;
                break;
            case NOTI_FAVORITED_CREATE_BUZZ:
                iconRes = R.drawable.ic_notification_comment_buzz;
                msg = String.format(mContext.getString(R.string.buzz_created_notification), item.notiUserName);
                break;
            case NOTI_REPLY_YOUR_COMMENT:
                iconRes = R.drawable.ic_notification_comment_buzz;
                msg = String.format(mContext.getString(R.string.reply_comment_responded_to_notification), item.notiUserName);
                break;
            case NOTI_DENIED_BUZZ_IMAGE:
                iconRes = R.drawable.ic_notification_image_approved;
                msg = mContext.getString(R.string.denied_image_buzz);
                break;
            case NOTI_DENIED_BACKSTAGE:
                iconRes = R.drawable.ic_notification_image_approved;
                msg = mContext.getString(R.string.denied_backstage);
                break;
            case NOTI_APPROVE_BUZZ_TEXT:
                iconRes = R.drawable.ic_notification_comment_buzz;
                msg = mContext.getString(R.string.approve_text_buzz);
                break;
            case NOTI_DENIED_BUZZ_TEXT:
                iconRes = R.drawable.ic_notification_comment_buzz;
                msg = mContext.getString(R.string.denied_text_buzz);
                break;
            case NOTI_APPROVE_COMMENT:
                iconRes = R.drawable.ic_notification_comment_buzz;
                msg = mContext.getString(R.string.approve_comment);
                break;
            case NOTI_DENIED_COMMENT:
                iconRes = R.drawable.ic_notification_comment_buzz;
                msg = mContext.getString(R.string.denied_comment);
                break;
            case NOTI_APPROVE_SUB_COMMENT:
                iconRes = R.drawable.ic_notification_comment_buzz;
                msg = mContext.getString(R.string.approve_sub_comment);
                break;
            case NOTI_DENI_SUB_COMMENT:
                iconRes = R.drawable.ic_notification_comment_buzz;
                msg = mContext.getString(R.string.denied_sub_comment);
                break;
            case NOTI_APPROVE_USERINFO:
                iconRes = R.drawable.ic_profile_online_alert;
                msg = mContext.getString(R.string.approve_user_info);
                break;
            case NOTI_APART_OF_USERINFO:
                iconRes = R.drawable.ic_profile_online_alert;
                msg = mContext.getString(R.string.apart_of_user_info);
                break;
            case NOTI_DENIED_USERINFO:
                iconRes = R.drawable.ic_profile_online_alert;
                msg = mContext.getString(R.string.denied_user_info);
                break;
            case NOTI_NEWS_BUZZ:
                iconRes = R.drawable.ic_notification_news_buzz_list;
                msg = item.content;
                break;
            case NOTI_QA_BUZZ:
                iconRes = R.drawable.ic_notification_qc_buzz;
                msg = item.content;
                break;
            case NOTI_LIVESTREAM_FROM_FAVOURIST:
                iconRes = R.drawable.ic_noti_live_stream;
                msg = String.format(holder.itemView.getContext().getResources().getString(R.string.noti_livestream_from_favourist), item.notiUserName);
                break;
            case NOTI_TAG_LIVESTREAM_FROM_FAVOURIST:
                iconRes = R.drawable.ic_noti_live_stream;
                msg = String.format(holder.itemView.getContext().getResources().getString(R.string.noti_tag_livestream_from_favourist), item.notiUserName);
                break;
            case NOTI_AUDIO_SHARE_BUZZ:
                iconRes = R.drawable.ic_share;
                msg = String.format(holder.itemView.getContext().getResources().getString(R.string.noti_share_audio), item.notiUserName);
                break;
            case NOTI_SHARE_LIVE_STREAM:
                iconRes = R.drawable.ic_share;
                msg = String.format(holder.itemView.getContext().getResources().getString(R.string.noti_share_live_stream), item.notiUserName);
                break;
            case NOTI_TAG_BUZZ:
                iconRes = R.drawable.ic_tag_friend_gray;
                msg = String.format(holder.itemView.getContext().getResources().getString(R.string.noti_tag_buzz), item.notiUserName);
                break;
        }

        holder.mItemTitle.setText(msg);
        holder.mItemIcon.setImageResource(iconRes);

        try {
            Calendar calendarNow = Calendar.getInstance();

            Calendar calendarSend = Calendar.getInstance(TimeZone.getDefault());
            calendarSend.setTime(TimeUtils.YYYYMMDDHHMMSS.parse(item.time));

            holder.mItemTime.setText(TimeUtils.getTimelineDif(calendarSend, calendarNow));
        } catch (ParseException e) {
            e.printStackTrace();
            holder.mItemTime.setText(R.string.common_now);
        }

        holder.itemView.setBackgroundColor((item.isRead == 0) ? Color.parseColor("#10d33131") : Color.TRANSPARENT);
    }

    public void markReadNotification(int position) {
        mData.get(position).isRead = 1;
        notifyItemChanged(position);
    }

    public NotificationItem getLastItem() {
        return mData.get(mData.size() - 1);
    }

    public static class ViewHolder extends BaseViewHolder {
        @BindView(R.id.item_icon)
        ImageView mItemIcon;
        @BindView(R.id.item_title)
        TextView mItemTitle;
        @BindView(R.id.item_time)
        TextView mItemTime;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    //========================================= InnerClass =======================================

    public interface INotificationAdapterEvent extends BaseAdapterListener<NotificationItem> {
        void onItemClick(View view, NotificationItem item, int position);
    }
}
