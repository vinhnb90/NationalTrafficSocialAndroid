package com.vn.ntsc.ui.comments;

import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.vn.ntsc.repository.model.timeline.datas.sub.ListCommentBean;
import com.vn.ntsc.repository.preferece.UserPreferences;
import com.vn.ntsc.utils.Constants;
import com.vn.ntsc.utils.ImagesUtils;
import com.vn.ntsc.utils.LogUtils;
import com.vn.ntsc.utils.time.TimeUtils;
import com.vn.ntsc.widget.adapter.BaseViewHolder;
import com.vn.ntsc.widget.adapter.MultifunctionAdapter;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by nankai on 8/10/2017.
 */

public class CommentAdapter extends MultifunctionAdapter<CommentAdapter.ViewHolder, ListCommentBean> {

    private boolean isOwner;

    CommentAdapter(CommentAdapterListener listener) {
        super(listener);
    }

    public void setOwner(boolean owner) {
        isOwner = owner;
    }

    @Override
    protected ViewHolder onInjectViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_comment, parent, false));
    }

    @Override
    protected void onViewReady(ViewHolder helper, ListCommentBean item, final int position) {
        helper.onBindView(item, isOwner, position, (CommentAdapterListener) listener);
    }

    public class ViewHolder extends BaseViewHolder {

        @Nullable
        @BindView(R.id.layout_item_comment_adapter_remove)
        ImageView remove;
        @Nullable
        @BindView(R.id.layout_item_comment_adapter_avatar)
        ImageView avatar;
        @Nullable
        @BindView(R.id.layout_item_comment_adapter_user_name)
        TextView userName;
        @Nullable
        @BindView(R.id.layout_item_comment_adapter_content)
        TextView content;
        @Nullable
        @BindView(R.id.layout_item_comment_adapter_time_post)
        TextView timeComment;
        @Nullable
        @BindView(R.id.comment_approval)
        TextView approval;

        @Nullable
        @BindView(R.id.layout_item_comment_adapter_sub_comment)
        RecyclerView subComment;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, itemView);
        }

        public void onBindView(final ListCommentBean itemBean, final boolean isOwner, final int position, final CommentAdapterListener listener) {
            if (itemBean.isApproved == Constants.IS_APPROVED) {
                if (approval != null) {
                    approval.setVisibility(View.INVISIBLE);
                }
            } else {
                if (approval != null) {
                    approval.setVisibility(View.VISIBLE);
                }
            }

            if (avatar != null) {
                ImagesUtils.loadRoundedAvatar(itemBean.avatar, itemBean.gender, avatar);
                avatar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (listener != null) {
                            listener.onViewProfileUserComment(itemBean, avatar, position);
                        }
                    }
                });
            }

            if (remove != null) {
                if (isOwner || itemBean.userId.equals(UserPreferences.getInstance().getUserId())) {
                    remove.setVisibility(View.VISIBLE);
                } else {
                    remove.setVisibility(View.INVISIBLE);
                }

                remove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onRemoveComment(itemBean, position);
                    }
                });
            }

            if (userName != null) {
                userName.setText(itemBean.userName);
            }

            LogUtils.i("Comment", "position(1): " + position);
            if (content != null) {
                content.setText(itemBean.commentValue);
                content.post(new Runnable() {
                    @Override
                    public void run() {
                        if (content.getLineCount() > Constants.MAX_LINE_COMMENT) {
                            LogUtils.i("Comment", "position(2): " + position);
                            onViewMore(content, itemBean, position, listener);
                        }
                    }
                });
            }

            if (timeComment != null) {
                try {
                    Calendar calendarNow = Calendar.getInstance();

                    Calendar calendarSend = Calendar.getInstance(TimeZone.getDefault());
                    calendarSend.setTime(TimeUtils.YYYYMMDDHHMMSS.parse(itemBean.commentTime));

                    timeComment.setText(TimeUtils.getTimelineDif(calendarSend, calendarNow));

                } catch (ParseException e) {
                    e.printStackTrace();
                    timeComment.setText(R.string.common_now);
                }
            }

            if (subComment != null) {
                subComment.setVisibility(View.VISIBLE);
                subComment.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
                subComment.setHasFixedSize(true);
                PreviewSubCommentAdapter subCommentsAdapter = new PreviewSubCommentAdapter(isOwner);
                subCommentsAdapter.setEnableLoadMore(false);
                subCommentsAdapter.setAdapterListener(new PreviewSubCommentAdapterListener() {
                    @Override
                    public void onViewProfileUserComment(BuzzSubCommentBean bean, View view) {
                        listener.onViewProfileUserComment(bean, view, position);
                    }

                    @Override
                    public void onOpenSubComment() {
                        listener.onOpenSubComment(itemBean, isOwner);
                    }

                    @Override
                    public void onRemoveSubComment(BuzzSubCommentBean bean) {
                        listener.onRemoveSubComment(itemBean, bean, position, isOwner);
                    }
                });
                subComment.setAdapter(subCommentsAdapter);

                //Sub comment preview header
                View subCommentPreviewHeader = LayoutInflater.from(itemView.getContext()).inflate(R.layout.layout_item_sub_comment_preview_header_adapter, null);
                TextView subCommentNumber = subCommentPreviewHeader.findViewById(R.id.layout_item_comment_adapter_sub_comment_number);
                subCommentPreviewHeader.findViewById(R.id.layout_item_comment_adapter_reply).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        listener.onOpenSubComment(itemBean, isOwner);
                    }
                });
                if (itemBean.subCommentNumber > 0) {
                    subCommentNumber.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            listener.onOpenSubComment(itemBean, isOwner);
                        }
                    });
                    subCommentNumber.setVisibility(View.VISIBLE);
                    subCommentNumber.setText(String.format(itemView.getContext().getResources().getString(R.string.sub_comment_number), itemBean.subCommentNumber + ""));
                } else {
                    subCommentNumber.setVisibility(View.GONE);
                }
                subCommentsAdapter.addHeaderView(subCommentPreviewHeader);

                ArrayList<BuzzSubCommentBean> newSubComment = new ArrayList<>();
                if (itemBean.subCommentNumber > 3) {
                    View seeMore = LayoutInflater.from(itemView.getContext()).inflate(R.layout.layout_see_more, null);
                    seeMore.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            listener.onOpenSubComment(itemBean, isOwner);
                        }
                    });
                    subCommentsAdapter.addFooterView(seeMore);
                }

                if (itemBean.subComments.size() > 3) {
                    newSubComment.addAll(itemBean.subComments.subList(itemBean.subComments.size() - 3, itemBean.subComments.size()));
                } else {
                    newSubComment.addAll(itemBean.subComments);
                }
                subCommentsAdapter.setNewData(newSubComment);
            }
        }

        private void onViewMore(final TextView content, final ListCommentBean itemBean, final int position, final CommentAdapterListener listener) {

            String expandText = itemBean.isViewMore ? itemView.getResources().getString(R.string.see_Less) : itemView.getResources().getString(R.string.see_more);
            String text;
            int lineEndIndex;

            if (itemBean.isViewMore) {
                lineEndIndex = content.getLayout().getLineEnd(content.getLayout().getLineCount() - 1);
                text = content.getText().subSequence(0, lineEndIndex) + "\n" + expandText;
            } else {
                lineEndIndex = content.getLayout().getLineEnd(Constants.MAX_LINE_COMMENT - 1);
                text = content.getText().subSequence(0, lineEndIndex - 1) + "... " + expandText;
            }

            Spanned replaceComment = Html.fromHtml(text.replace("\n", "<br />"));
            content.setHighlightColor(Color.TRANSPARENT);
            content.setMovementMethod(LinkMovementMethod.getInstance());
            content.setText(addClickablePartTextViewResizable(replaceComment, expandText, itemBean, position, listener), TextView.BufferType.SPANNABLE);
        }

        private SpannableStringBuilder addClickablePartTextViewResizable(Spanned strSpanned, String expandText, final ListCommentBean itemBean, final int position, final CommentAdapterListener listener) {
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