package com.vn.ntsc.ui.comment.helper;

import android.content.DialogInterface;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nankai.designlayout.dialog.DialogMaterial;
import com.nankai.designlayout.dialog.enums.Style;
import com.vn.ntsc.R;
import com.vn.ntsc.repository.model.timeline.TimelineType;
import com.vn.ntsc.repository.model.timeline.datas.BuzzBean;
import com.vn.ntsc.repository.model.timeline.datas.ShareDetailBean;
import com.vn.ntsc.services.UserLiveStreamService;
import com.vn.ntsc.ui.comment.CommentActivity;
import com.vn.ntsc.ui.mediadetail.timeline.TimelineMediaActivity;
import com.vn.ntsc.utils.Constants;
import com.vn.ntsc.utils.ImagesUtils;
import com.vn.ntsc.utils.RegionUtils;
import com.vn.ntsc.utils.time.TimeUtils;
import com.vn.ntsc.widget.views.images.RecyclingImageView;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import butterknife.BindView;

/**
 * Created by nankai on 10/25/2017.
 */

public class ShareHeaderViewHolder extends HeaderViewHolder {

    @BindView(R.id.layout_content)
    ConstraintLayout layoutContentShare;
    @BindView(R.id.item_timeline_image_view)
    RecyclingImageView imageViewShare;
    @BindView(R.id.item_timeline_header_event_share)
    RecyclingImageView favoriteShare;
    @BindView(R.id.item_timeline_avatar_share)
    RecyclingImageView avatarShare;
    @BindView(R.id.item_timeline_username_share)
    TextView txtUserNameShare;
    @BindView(R.id.item_timeline_date_post_share)
    TextView dateTimeSharePost;
    @BindView(R.id.item_timeline_header_description_share)
    TextView descriptionShare;
    @BindView(R.id.item_timeline_header_more_share)
    TextView seeMoreShare;
    @BindView(R.id.item_timeline_share_view)
    TextView shareView;

    @Nullable
    @BindView(R.id.view)
    TextView viewCurrent;
    @Nullable
    @BindView(R.id.title_time)
    TextView titleTime;
    @Nullable
    @BindView(R.id.layout_timeline_live_stream)
    RelativeLayout layoutTimelineLiveStream;

    public ShareHeaderViewHolder(int typeHeader, View itemView, int typeView) {
        super(typeHeader, itemView, typeView);
    }

    @Override
    public ShareHeaderViewHolder onBindView(final CommentActivity activity, final BuzzBean bean) {
        super.onBindView(activity, bean);

        String time;
        try {

            Calendar calendarNow = Calendar.getInstance();

            Calendar calendarSend = Calendar.getInstance(TimeZone.getDefault());
            Date dateSend = TimeUtils.YYYYMMDDHHMMSS.parse(bean.listChildBuzzes.get(0).streamEndTime);
            calendarSend.setTime(dateSend);

            time = TimeUtils.getTimelineDif(calendarSend, calendarNow);
        } catch (Exception e) {
            time = activity.getResources().getString(R.string.common_now);
        }

        ImagesUtils.loadRoundedAvatar(bean.shareDetailBean.avatar, bean.gender, avatarShare);

        SpannableStringBuilder spanTxtTitleShare = new SpannableStringBuilder();
        spanTxtTitleShare.append(bean.shareDetailBean.userName);
        spanTxtTitleShare.setSpan(new ClickableSpan() {

            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setColor(ds.linkColor);    // you can use custom color
                ds.setUnderlineText(false);    // this remove the underline
            }

            @Override
            public void onClick(View widget) {
                //TODO
            }

        }, 0, spanTxtTitleShare.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        if (getTypeHeader() == TimelineType.BUZZ_TYPE_SHARE_LIVE_STREAM) {
            if (bean.shareDetailBean.listChildBuzzes.get(0).streamStatus.equals(Constants.LIVE_STREAM_ON)) {
                spanTxtTitleShare.append(activity.getResources().getString(R.string.timeline_time_live_stream_on));
            } else {
                spanTxtTitleShare.append(activity.getResources().getString(R.string.timeline_time_live_stream_off));
            }
        }

        onClickTagFriends(bean.shareDetailBean, activity, spanTxtTitleShare);

        txtUserNameShare.setText(spanTxtTitleShare);
        txtUserNameShare.setHighlightColor(Color.TRANSPARENT);
        txtUserNameShare.setMovementMethod(LinkMovementMethod.getInstance());

        shareView.setText(String.format(activity.getResources().getString(R.string.timeline_time_view), bean.shareDetailBean.listChildBuzzes.get(0).viewNumber));
        //time location
        dateTimeSharePost.setText(String.format(activity.getResources().getString(R.string.timeline_time_location), time, RegionUtils.getInstance(activity).getRegionName(bean.shareDetailBean.region)));

        descriptionShare.setText(bean.shareDetailBean.buzzValue);
        if (descriptionShare.getLineCount() > 4)
            seeMoreShare.setVisibility(View.VISIBLE);
        else
            seeMoreShare.setVisibility(View.INVISIBLE);

        if (getTypeHeader() == TimelineType.BUZZ_TYPE_SHARE_LIVE_STREAM) {
            viewCurrent.setText(bean.shareDetailBean.listChildBuzzes.get(0).currentViewNumber);

            ImagesUtils.loadImageLiveStreamFillWidth(bean.shareDetailBean.listChildBuzzes.get(0).thumbnailUrl, imageViewShare);

            //time location
            if (bean.shareDetailBean.listChildBuzzes.get(0).streamStatus.equals(Constants.LIVE_STREAM_ON)) {
                layoutTimelineLiveStream.setVisibility(View.VISIBLE);

                int seconds = Integer.parseInt(bean.shareDetailBean.listChildBuzzes.get(0).streamDuration);
                int minutes = seconds / 60;
                int hours = minutes / 60;
                minutes %= 60;
                seconds %= 60;
                hours %= 12;

                titleTime.setBackgroundResource(R.drawable.bg_red_text_radius_50);

                if (minutes > 0) {
                    titleTime.setText(String.format(titleTime.getResources().getString(R.string.live_streamed_total_time), String.format("%02d:%02d", hours, minutes)));
                } else {
                    titleTime.setText(String.format(titleTime.getResources().getString(R.string.live_streamed_total_time), String.format("%02d:%02d", minutes, seconds)));
                }

            } else {
                layoutTimelineLiveStream.setVisibility(View.GONE);
            }
        } else {
            ImagesUtils.loadImageFillWidth(bean.shareDetailBean.listChildBuzzes.get(0).buzzVal, imageViewShare);
        }

        imageViewShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareDetailBean shareDetail = bean.shareDetailBean;

                if (shareDetail.listChildBuzzes.get(0).isApp == Constants.IS_APPROVED) {
                    if (getTypeHeader() == TimelineType.BUZZ_TYPE_SHARE_AUDIO) {
                        TimelineMediaActivity.launch(activity, v, shareDetail.listChildBuzzes, 0);
                    } else if (getTypeHeader() == TimelineType.BUZZ_TYPE_SHARE_LIVE_STREAM) {
                        if (shareDetail.listChildBuzzes.get(0).streamStatus.equals(Constants.LIVE_STREAM_ON))
                            activity.onLiveStreamOption(UserLiveStreamService.Mode.VIEW,
                                    shareDetail.buzzId, shareDetail.listChildBuzzes.get(0).streamId,
                                    shareDetail.listChildBuzzes.get(0).thumbnailUrl);
                        else {
                            TimelineMediaActivity.launch(activity, v, shareDetail.listChildBuzzes, 0);
                        }
                    }
                } else {
                    DialogMaterial.Builder builder = new DialogMaterial.Builder(activity)
                            .setStyle(Style.HEADER_WITH_TITLE)
                            .setTitle(R.string.common_approved)
                            .setContent(R.string.not_approved_buzz)
                            .onPositive(R.string.common_ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    builder.show();
                }
            }
        });

        return this;
    }

    private void onClickTagFriends(final ShareDetailBean bean, CommentActivity activity, SpannableStringBuilder spanTxtTitleShare) {
        if (bean.tagNumber >= 1) {
            spanTxtTitleShare.append(activity.getResources().getString(R.string.timeline_time_with));
            spanTxtTitleShare.append(bean.tagList.get(0).userName);
            spanTxtTitleShare.setSpan(new ClickableSpan() {
                @Override
                public void updateDrawState(TextPaint ds) {
                    ds.setColor(ds.linkColor);    // you can use custom color
                    ds.setUnderlineText(false);    // this remove the underline
                }


                @Override
                public void onClick(View widget) {
                    //TODO
                }

            }, spanTxtTitleShare.length() - bean.tagList.get(0).userName.length(), spanTxtTitleShare.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        if (bean.tagNumber > 1) {
            spanTxtTitleShare.append(activity.getResources().getString(R.string.timeline_time_and));
            String other = String.format(activity.getResources().getString(R.string.timeline_time_other), String.valueOf((bean.tagNumber - 1)));
            spanTxtTitleShare.append(other);
            spanTxtTitleShare.setSpan(new ClickableSpan() {

                @Override
                public void updateDrawState(TextPaint ds) {
                    ds.setColor(ds.linkColor);    // you can use custom color
                    ds.setUnderlineText(false);    // this remove the underline
                }

                @Override
                public void onClick(View widget) {
                    //TODO
                }

            }, spanTxtTitleShare.length() - other.length(), spanTxtTitleShare.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }
}