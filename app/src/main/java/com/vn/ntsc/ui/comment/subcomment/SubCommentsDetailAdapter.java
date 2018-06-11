package com.vn.ntsc.ui.comment.subcomment;

import android.graphics.Color;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

import java.util.Calendar;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by nankai on 8/24/2017.
 */

public class SubCommentsDetailAdapter extends MultifunctionAdapter<SubCommentsDetailAdapter.ViewHolder, BuzzSubCommentBean> {

    boolean isOwner;

    public SubCommentsDetailAdapter(SubCommentAdapterListener listener) {
        super(listener);
    }

    public void setOwner(boolean isOwner) {
        this.isOwner = isOwner;
    }

    @Override
    protected SubCommentsDetailAdapter.ViewHolder onInjectViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_sub_comment_detail_adapter, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    protected void onViewReady(ViewHolder helper, BuzzSubCommentBean item, int position) {
        if (mData.size() > 0)
            helper.bindView(item, isOwner, (SubCommentAdapterListener) listener, position);
    }

    public static class ViewHolder extends BaseViewHolder {

        @Nullable
        @BindView(R.id.layout_item_comment_adapter_remove)
        ImageView removeComment;
        @Nullable
        @BindView(R.id.layout_item_sub_comment_detail_adapter_avatar)
        public ImageView avatar;
        @Nullable
        @BindView(R.id.layout_item_sub_comment_detail_adapter_user_name)
        public TextView userName;
        @Nullable
        @BindView(R.id.layout_item_sub_comment_detail_adapter_comment)
        public TextView comment;
        @Nullable
        @BindView(R.id.layout_item_sub_comment_detail_adapter_time)
        public TextView commentTime;
        @Nullable
        @BindView(R.id.comment_approval)
        public TextView approval;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bindView(final BuzzSubCommentBean itemBean, boolean isOwner, final SubCommentAdapterListener listener, final int position) {
            LogUtils.i(TAG, "SubCommentsDetailAdapter ---> is_app: " + itemBean.isApprove);

            if (itemBean.isApprove == Constants.IS_APPROVED) {
                approval.setVisibility(View.INVISIBLE);
            } else {
                approval.setVisibility(View.VISIBLE);
            }

            userName.setText(itemBean.userName);
            comment.setText(itemBean.value);
            comment.post(new Runnable() {
                @Override
                public void run() {
                    if (comment.getLineCount() > Constants.MAX_LINE_COMMENT) {
                        onViewMore(comment, itemBean, position, listener);
                    }
                }
            });

            //Avatar
            ImagesUtils.loadRoundedAvatar(itemBean.avatar, itemBean.gender, avatar);

            try {
                Calendar calendarNow = Calendar.getInstance();

                Calendar calendarSend = Calendar.getInstance(TimeZone.getDefault());
                calendarSend.setTime(TimeUtils.YYYYMMDDHHMMSS.parse(itemBean.time));

                commentTime.setText(TimeUtils.getTimelineDif(calendarSend, calendarNow));
            } catch (Exception e) {
                e.printStackTrace();
                commentTime.setText(R.string.common_now);
            }

            if (itemBean.userId.equals(UserPreferences.getInstance().getUserId()) || isOwner) {
                removeComment.setVisibility(View.VISIBLE);
            } else {
                removeComment.setVisibility(View.INVISIBLE);
            }

            removeComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onRemoved(itemBean, position);
                }
            });

            avatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.viewProfileSubComment(itemBean, avatar, position);
                }
            });
        }

        private void onViewMore(final TextView comment, final BuzzSubCommentBean itemBean, final int position, final SubCommentAdapterListener listener) {

            String expandText = itemBean.isViewMore ? itemView.getResources().getString(R.string.see_Less) : itemView.getResources().getString(R.string.see_more);
            String text;
            int lineEndIndex;

            if (itemBean.isViewMore) {
                lineEndIndex = comment.getLayout().getLineEnd(comment.getLayout().getLineCount() - 1);
                text = comment.getText().subSequence(0, lineEndIndex) + "\n" + expandText;
            } else {
                lineEndIndex = comment.getLayout().getLineEnd(Constants.MAX_LINE_COMMENT - 1);
                text = comment.getText().subSequence(0, lineEndIndex - 1) + "... " + expandText;
            }

            Spanned replaceComment = Html.fromHtml(text.replace("\n", "<br />"));
            comment.setHighlightColor(Color.TRANSPARENT);
            comment.setMovementMethod(LinkMovementMethod.getInstance());
            comment.setText(addClickablePartTextViewResizable(replaceComment, expandText, itemBean, position, listener), TextView.BufferType.SPANNABLE);
        }

        private SpannableStringBuilder addClickablePartTextViewResizable(Spanned strSpanned, String expandText, final BuzzSubCommentBean itemBean, final int position, final SubCommentAdapterListener listener) {
            String str = strSpanned.toString();

            SpannableStringBuilder ssb = new SpannableStringBuilder(strSpanned);
            ssb.setSpan(new ClickableSpan() {
                @Override
                public void updateDrawState(TextPaint ds) {
                    ds.setColor(ds.linkColor);    // you can use custom color
                    ds.setUnderlineText(false);    // this remove the underline
                }

                @Override
                public void onClick(View widget) {
                    itemBean.isViewMore = !itemBean.isViewMore;
                    listener.onViewMore(itemBean, position);
                }
            }, str.indexOf(expandText), str.indexOf(expandText) + expandText.length(), 0);

            return ssb;
        }
    }
}
