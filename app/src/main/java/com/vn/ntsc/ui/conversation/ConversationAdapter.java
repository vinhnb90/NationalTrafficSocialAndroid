package com.vn.ntsc.ui.conversation;

import android.content.Context;
import android.content.DialogInterface;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nankai.designlayout.dialog.DialogMaterial;
import com.nankai.designlayout.dialog.enums.Style;
import com.vn.ntsc.R;
import com.vn.ntsc.repository.model.conversation.ConversationItem;
import com.vn.ntsc.utils.ImagesUtils;
import com.vn.ntsc.utils.chats.MessageUtils;
import com.vn.ntsc.utils.time.TimeUtils;
import com.vn.ntsc.widget.adapter.BaseAdapterListener;
import com.vn.ntsc.widget.adapter.BaseViewHolder;
import com.vn.ntsc.widget.adapter.MultifunctionAdapter;
import com.vn.ntsc.widget.views.textview.EmoTextView;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ThoNh on 8/31/2017.
 */

public class ConversationAdapter extends MultifunctionAdapter<ConversationAdapter.ViewHolder, ConversationItem> {
    private boolean isEnableDelete;
    private IOnRecyclerViewEvent mEventListener;

    public ConversationAdapter(IOnRecyclerViewEvent listener) {
        super(listener);
        this.mEventListener = listener;
    }

    @Override
    protected ViewHolder onInjectViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_conversation, parent, false));
    }

    @Override
    protected void onViewReady(final ConversationAdapter.ViewHolder holder, final ConversationItem message, final int position) {

        final Context mContext = holder.itemView.getContext();

        Log.d(TAG, "onViewReady: position " + position + message.friendId);
        // toggle delete button
        holder.deleteItem.setVisibility(isEnableDelete ? View.VISIBLE : View.GONE);
        holder.deleteItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogMaterial.Builder builder = new DialogMaterial.Builder(mContext)
                        .setStyle(Style.HEADER_WITH_NOT_HEADER)
                        .setContent(R.string.delete_item_message_confirm)
                        .onPositive(R.string.common_ok_2, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (mEventListener != null) {
                                    mEventListener.onDelConversationClick(message, holder.getAdapterPosition());
                                }
                            }
                        })
                        .onNegative(R.string.common_cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                builder.show();
            }
        });

        // load avatar, transform round
        ImagesUtils.loadRoundedAvatar(message.thumbnailUrl, message.gender, holder.avatar);

        holder.avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    mEventListener.onAvatarClick(message, holder.avatar, position);
                }
            }
        });

        holder.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    mEventListener.onAvatarClick(message, holder.avatar, position);
                }
            }
        });

        holder.constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    mEventListener.onItemMessageClick(message, holder.avatar, position);
                }
            }
        });


        // set bagde
        if (message.unreadNum > 0) {
            holder.bagde.setVisibility(View.VISIBLE);
            holder.bagde.setText(
                    String.valueOf(message.unreadNum)
            );
        } else {
            holder.bagde.setVisibility(View.GONE);
        }

        // set user name
        if (message.name != null) {
            holder.name.setText(message.name);
        } else {
            holder.name.setText(mContext.getResources().getString(R.string.user_deactive));
        }

        // set last message time
        try {
            Date mDateSend = TimeUtils.convertStringToDateDefault(message.sentTime);
            Calendar mCalendarSend = Calendar.getInstance(Locale.getDefault());
            mCalendarSend.setTime(mDateSend);
            holder.lastTime.setText(TimeUtils.getTimelineDif(mCalendarSend, Calendar.getInstance(Locale.getDefault())));

        } catch (ParseException e) {
            e.printStackTrace();
        }

        // set content of last message

        String lastMsg = message.lastMessage;
        String textMessage = "";

        switch (message.messageType) {
            case MessageUtils.WINK:
                Toast.makeText(mContext, "WINK IS OWN", Toast.LENGTH_SHORT).show();
                textMessage = "WINK IS OWN";
                break;
            case MessageUtils.FILE:
                textMessage = message.isOwn ? mContext.getString(R.string.sent_a_file) : mContext.getString(R.string.receive_a_file);
                break;
            case MessageUtils.GIFT:
                textMessage = MessageUtils.getTextFromGiftMsg(mContext, lastMsg, message.isOwn);
                break;
            case MessageUtils.LOCATION:
                textMessage = mContext.getResources().getString(R.string.chat_item_share_a_location);
                break;
            case MessageUtils.STICKER:
                textMessage = message.isOwn ? mContext.getResources().getString(R.string.sticker_sent) : mContext.getResources().getString(R.string.sticker_receiver);
                break;
            case MessageUtils.STARTVIDEO:
            case MessageUtils.ENDVIDEO:
            case MessageUtils.STARTVOICE:
            case MessageUtils.ENDVOICE:
                textMessage = MessageUtils.getTextVideoVoice(mContext, lastMsg);
                break;
            case MessageUtils.CALLREQUEST:
                textMessage = mContext.getString(R.string.message_video_call_response);
                break;
            case MessageUtils.PP:
                textMessage = lastMsg;
                break;
        }

        if (message.isOwn) {
            holder.message.setVisibility(View.GONE);
            holder.messageMe.setVisibility(View.VISIBLE);

            holder.messageMe.setText(textMessage);
        } else {
            holder.messageMe.setVisibility(View.GONE);
            holder.message.setVisibility(View.VISIBLE);

            holder.message.setText(textMessage);

        }
    }

    ConversationItem getLastItem() {
        if (mData.size() != 0) {
            return mData.get(mData.size() - 1);
        }
        return null;
    }

    void setEventListener(IOnRecyclerViewEvent eventListener) {
        this.mEventListener = eventListener;
    }

    /**
     * all data will replace and notify change
     *
     * @param messages list message to update
     */
    void update(List<ConversationItem> messages) {
        mData.addAll(messages);
        notifyDataSetChanged();
    }

    /**
     * mark all message as read => hide all bagde
     */
    void markAllAsRead() {
        for (ConversationItem aListMessage : mData) {
            aListMessage.unreadNum = 0;
        }

        notifyDataSetChanged();
    }

    /**
     * clear all data => list will empty
     */
    void deleteAll() {
        mData.clear();
        notifyDataSetChanged();
    }

    /**
     * show delete on each item
     *
     * @param isEnableDelete true: show
     */
    void toggleDeleteItem(boolean isEnableDelete) {
        this.isEnableDelete = isEnableDelete;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends BaseViewHolder {
        @BindView(R.id.avatar)
        ImageView avatar;
        @BindView(R.id.bagde)
        TextView bagde;
        @BindView(R.id.name)
        TextView name;
        @BindView(R.id.last_time)
        TextView lastTime;
        @BindView(R.id.delete_item)
        Button deleteItem;
        @BindView(R.id.item_message)
        ConstraintLayout constraintLayout;

        /**
         * <b>REMEMBER: **message** and **message_me** will not show at same time</b>
         *
         * @see #message
         * @see #messageMe
         */
        @BindView(R.id.message)
        EmoTextView message;
        @BindView(R.id.message_me)
        EmoTextView messageMe;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, itemView);
        }
    }

    //============================================= Inner class ====================================

    public interface IOnRecyclerViewEvent extends BaseAdapterListener<ConversationItem> {
        void onItemClick(ConversationItem conversationItem, int position);

        void onDelConversationClick(ConversationItem conversationItem, int position);

        void onAvatarClick(ConversationItem conversationItem, View avatar, int position);

        void onItemMessageClick(ConversationItem conversationItem, View avatar, int position);


    }
}
