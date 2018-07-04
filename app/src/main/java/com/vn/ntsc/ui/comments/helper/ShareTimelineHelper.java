package com.vn.ntsc.ui.comments.helper;

import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;

import com.vn.ntsc.R;
import com.vn.ntsc.repository.model.timeline.TimelineType;
import com.vn.ntsc.repository.model.timeline.datas.BuzzBean;
import com.vn.ntsc.repository.model.timeline.datas.ShareDetailBean;
import com.vn.ntsc.repository.preferece.UserPreferences;
import com.vn.ntsc.ui.timeline.core.TimelineListener;
import com.vn.ntsc.utils.Constants;
import com.vn.ntsc.utils.RegionUtils;
import com.vn.ntsc.utils.time.TimeUtils;
import com.vn.ntsc.widget.views.images.RecyclingImageView;

import java.util.Calendar;
import java.util.TimeZone;

import butterknife.BindView;

public class ShareTimelineHelper extends CommentHeaderHelper {

    @Nullable
    @BindView(R.id.layout_content)
    ConstraintLayout layoutContentShare;
    @Nullable
    @BindView(R.id.item_timeline_image_view)
    RecyclingImageView imageViewShare;
    @Nullable
    @BindView(R.id.item_timeline_header_event_share)
    RecyclingImageView favoriteShare;
    @Nullable
    @BindView(R.id.item_timeline_avatar_share)
    RecyclingImageView avatarShare;
    @Nullable
    @BindView(R.id.item_timeline_title_share)
    TextView txtUserNameShare;
    @Nullable
    @BindView(R.id.item_timeline_date_post_share)
    TextView dateTimeSharePost;
    @Nullable
    @BindView(R.id.item_timeline_header_description_share)
    TextView descriptionShare;
    @Nullable
    @BindView(R.id.item_timeline_header_more_share)
    TextView seeMoreShare;
    @Nullable
    @BindView(R.id.item_timeline_share_view)
    TextView shareView;
    SpannableStringBuilder spanTxtTitleShare = new SpannableStringBuilder();

    public ShareTimelineHelper(View itemView, int viewType) {
        super(itemView, viewType);
    }

    @Override
    public void onBindView(final BuzzBean bean) {
        super.onBindView(bean);

        spanTxtTitleShare.clear();

        if (viewType == TimelineType.BUZZ_TYPE_SHARE_LIVE_STREAM) {
            if (bean.shareDetailBean.listChildBuzzes.get(0).isApp == Constants.IS_APPROVED) {
                loadImageLiveStreamFillWidth(bean.shareDetailBean.listChildBuzzes.get(0).thumbnailUrl, imageViewShare);
            } else {
                loadImageLiveStreamFillWidthBlur(bean.shareDetailBean.listChildBuzzes.get(0).thumbnailUrl, imageViewShare);
            }
        } else {
            if (bean.shareDetailBean.listChildBuzzes.get(0).isApp == Constants.IS_APPROVED) {
                loadImageFillWidth(bean.shareDetailBean.listChildBuzzes.get(0).thumbnailUrl, imageViewShare);
            } else {
                loadImageFillWidthBlur(bean.shareDetailBean.listChildBuzzes.get(0).thumbnailUrl, imageViewShare);
            }
        }

        spanTxtTitleShare.clear();

        loadImageRounded(bean.shareDetailBean.avatar, bean.shareDetailBean.gender, avatarShare);

        if (bean.shareDetailBean.userName != null) {
            spanTxtTitleShare.append(bean.shareDetailBean.userName);

            spanTxtTitleShare.setSpan(new ClickableSpan() {
                @Override
                public void updateDrawState(TextPaint ds) {
                    ds.setColor(ds.linkColor);    // you can use custom color
                    ds.setUnderlineText(false);    // this remove the underline
                }

                @Override
                public void onClick(View widget) {
                    listener.onDisplayProfileScreen(bean.shareDetailBean.userId, widget);
                }

            }, 0, spanTxtTitleShare.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        if (viewType == TimelineType.BUZZ_TYPE_SHARE_LIVE_STREAM) {
            if (bean.shareDetailBean.listChildBuzzes.get(0).streamStatus.equals(Constants.LIVE_STREAM_ON)) {
                spanTxtTitleShare.append(itemView.getResources().getString(R.string.timeline_time_live_stream_on));
            } else {
                spanTxtTitleShare.append(itemView.getResources().getString(R.string.timeline_time_live_stream_off));
            }
        }

        final boolean isOwn = bean.shareDetailBean.userId.equals(UserPreferences.getInstance().getUserId());
        if (isOwn) {
            imageViewShare.setImageResource(R.drawable.ic_buzz_delete);
        } else {
            imageViewShare.setImageResource(bean.isFavorite == Constants.BUZZ_TYPE_IS_FAVORITE ? R.drawable.ic_list_buzz_item_favorited : R.drawable.ic_list_buzz_item_favorite);
        }

        onClickTagFriends(bean.shareDetailBean);
        txtUserNameShare.setText(spanTxtTitleShare);
        txtUserNameShare.setHighlightColor(Color.TRANSPARENT);
        txtUserNameShare.setMovementMethod(LinkMovementMethod.getInstance());

        String time;
        try {
            Calendar calendarNow = Calendar.getInstance();

            Calendar calendarSend = Calendar.getInstance(TimeZone.getDefault());
            calendarSend.setTime(TimeUtils.YYYYMMDDHHMMSS.parse(bean.shareDetailBean.buzzTime));

            time = TimeUtils.getTimelineDif(calendarSend, calendarNow);
        } catch (Exception e) {
            e.printStackTrace();
            time = itemView.getResources().getString(R.string.common_now);
        }

        shareView.setText(String.format(itemView.getResources().getString(R.string.timeline_time_view), bean.shareDetailBean.listChildBuzzes.get(0).viewNumber));
        //time location
        dateTimeSharePost.setText(String.format(itemView.getResources().getString(R.string.timeline_time_location), time, RegionUtils.getInstance(itemView.getContext()).getRegionName(bean.shareDetailBean.region)));

        if (!bean.shareDetailBean.buzzValue.equals("")) {
            descriptionShare.setText(bean.shareDetailBean.buzzValue);
            descriptionShare.post(new Runnable() {
                @Override
                public void run() {
                    if (descriptionShare.getLineCount() > 4) {
                        seeMoreShare.setVisibility(View.VISIBLE);
                    } else
                        seeMoreShare.setVisibility(View.INVISIBLE);
                }
            });
        } else {
            descriptionShare.setVisibility(View.GONE);
        }

        imageViewShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewType == TimelineType.BUZZ_TYPE_SHARE_AUDIO) {
                    listener.onDisplayShareAudioPlayScreen(bean, v);
                } else if (viewType == TimelineType.BUZZ_TYPE_SHARE_LIVE_STREAM) {
                    listener.onDisplayShareLiveStreamScreen(bean, v);
                }
            }
        });
    }

    private void onClickTagFriends(final ShareDetailBean bean) {
        if (bean.tagNumber >= 1) {
            spanTxtTitleShare.append(itemView.getResources().getString(R.string.timeline_time_with));
            spanTxtTitleShare.append(bean.tagList.get(0).userName);
            spanTxtTitleShare.setSpan(new ClickableSpan() {
                @Override
                public void updateDrawState(TextPaint ds) {
                    ds.setColor(ds.linkColor);    // you can use custom color
                    ds.setUnderlineText(false);    // this remove the underline
                }

                @Override
                public void onClick(View widget) {
                    listener.onDisplayProfileScreen(bean.tagList.get(0).userId, txtUserNameShare);
                }

            }, spanTxtTitleShare.length() - bean.tagList.get(0).userName.length(), spanTxtTitleShare.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        if (bean.tagNumber > 1) {
            spanTxtTitleShare.append(itemView.getResources().getString(R.string.timeline_time_and));
            String other = String.format(itemView.getResources().getString(R.string.timeline_time_other), String.valueOf((bean.tagNumber - 1)));
            spanTxtTitleShare.append(other);
            spanTxtTitleShare.setSpan(new ClickableSpan() {

                @Override
                public void updateDrawState(TextPaint ds) {
                    ds.setColor(ds.linkColor);    // you can use custom color
                    ds.setUnderlineText(false);    // this remove the underline
                }

                @Override
                public void onClick(View widget) {
                    listener.onDisplayTagFriendsScreen(bean.tagList, txtUserNameShare);
                }

            }, spanTxtTitleShare.length() - other.length(), spanTxtTitleShare.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }
}
