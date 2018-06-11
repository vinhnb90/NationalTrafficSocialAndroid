package com.vn.ntsc.ui.comment;

import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vn.ntsc.R;
import com.vn.ntsc.repository.model.timeline.datas.sub.BuzzSubCommentBean;
import com.vn.ntsc.repository.preferece.UserPreferences;
import com.vn.ntsc.utils.Constants;
import com.vn.ntsc.utils.ImagesUtils;
import com.vn.ntsc.utils.LogUtils;
import com.vn.ntsc.utils.time.TimeUtils;
import com.vn.ntsc.widget.adapter.BaseViewHolder;
import com.vn.ntsc.widget.adapter.MultifunctionAdapter;

import java.text.ParseException;
import java.util.Calendar;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by nankai on 8/23/2017.
 */

public class PreviewSubCommentAdapter extends MultifunctionAdapter<PreviewSubCommentAdapter.ViewHolder, BuzzSubCommentBean> {

    private PreviewAdapterListener adapterListener;
    private boolean isOwner;

    /**
     * @param isOwner
     */
    public PreviewSubCommentAdapter(boolean isOwner) {
        super(null);
        this.isOwner = isOwner;
    }

    @Override
    protected ViewHolder onInjectViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_sub_comment_adapter, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    public void setAdapterListener(PreviewAdapterListener adapterListener) {
        this.adapterListener = adapterListener;
    }

    @Override
    protected void onViewReady(ViewHolder helper, BuzzSubCommentBean item, int position) {
        if (mData.size() > 0) {
            helper.bindView(item, isOwner, adapterListener);
        }
    }

    public static class ViewHolder extends BaseViewHolder {
        @Nullable
        @BindView(R.id.layout_item_sub_comment_adapter_avatar)
        public ImageView avatar;
        @Nullable
        @BindView(R.id.layout_item_sub_comment_adapter_user_name)
        public TextView userName;
        @Nullable
        @BindView(R.id.layout_item_sub_comment_adapter_comment)
        public TextView comment;
        @Nullable
        @BindView(R.id.layout_item_sub_comment_adapter_time)
        public TextView commentTime;
        @Nullable
        @BindView(R.id.layout_item_comment_adapter_remove)
        public ImageView removeSubComment;
        @Nullable
        @BindView(R.id.layout_item_sub_comment_adapter_header)
        RelativeLayout relativeLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bindView(final BuzzSubCommentBean itemBean, boolean isOwner, final PreviewAdapterListener adapterListener) {

            if (itemBean.isApprove == Constants.IS_APPROVED) {
                itemView.setAlpha(Constants.APPROVED_ALPHA);
            } else {
                itemView.setAlpha(Constants.NOT_APPROVED_ALPHA);
            }

            //Avatar
            ImagesUtils.loadRoundedAvatar(itemBean.avatar, itemBean.gender, avatar);

            if (isOwner || itemBean.userId.equals(UserPreferences.getInstance().getUserId())) {
                removeSubComment.setVisibility(View.VISIBLE);
            } else {
                removeSubComment.setVisibility(View.INVISIBLE);
            }

            try {
                Calendar calendarNow = Calendar.getInstance();

                Calendar calendarSend = Calendar.getInstance(TimeZone.getDefault());
                calendarSend.setTime(TimeUtils.YYYYMMDDHHMMSS.parse(itemBean.time));

                commentTime.setText(TimeUtils.getTimelineDif(calendarSend, calendarNow));
            } catch (ParseException e) {
                e.printStackTrace();
                commentTime.setText(R.string.common_now);
            }
            userName.setText(itemBean.userName);
            LogUtils.i(TAG,itemBean.value);
            comment.setText(itemBean.value);

            relativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    adapterListener.onOpenSubComment();
                }
            });

            avatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    adapterListener.onViewProfileUserComment(itemBean, v);
                }
            });

            comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    adapterListener.onOpenSubComment();
                }
            });

            removeSubComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    adapterListener.onRemoveSubComment(itemBean);
                }
            });
        }
    }
}
