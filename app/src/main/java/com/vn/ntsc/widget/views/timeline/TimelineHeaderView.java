package com.vn.ntsc.widget.views.timeline;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.DrawableRes;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.widget.AppCompatTextView;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.vn.ntsc.R;
import com.vn.ntsc.repository.model.timeline.TimelineType;
import com.vn.ntsc.repository.model.timeline.datas.BuzzBean;
import com.vn.ntsc.repository.model.timeline.datas.sub.ListTagFriendsBean;
import com.vn.ntsc.repository.preferece.UserPreferences;
import com.vn.ntsc.utils.Constants;
import com.vn.ntsc.utils.ImagesUtils;
import com.vn.ntsc.utils.RegionUtils;
import com.vn.ntsc.utils.time.TimeUtils;
import com.vn.ntsc.widget.views.images.RecyclingImageView;
import com.vn.ntsc.widget.views.textview.TextViewVectorCompat;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TimelineHeaderView extends ConstraintLayout {

    @BindView(R.id.item_timeline_avatar)
    RecyclingImageView avatar;
    @BindView(R.id.item_timeline_event)
    RecyclingImageView events;
    @BindView(R.id.item_timeline_title)
    AppCompatTextView txtTitle;
    @BindView(R.id.item_timeline_date_post)
    TextViewVectorCompat dateTimeAndLocal;

    HeaderListener listener;
    RequestManager glide;
    SpannableStringBuilder spanTitle;

    ConstraintSet constraintSet;

    public interface HeaderListener {

        void onEvent(BuzzBean bean, View view);

        void onDisplayProfileScreen(String userId, View view);

        void onDisplayProfileScreen(BuzzBean bean, View view);

        void onDisplayTagFriendsScreen(ArrayList<ListTagFriendsBean> tagList, View view);
    }

    public TimelineHeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public TimelineHeaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.layout_item_timeline_header, this, true);
        this.setId(R.id.item_timeline_header);
        ButterKnife.bind(this);
        spanTitle = new SpannableStringBuilder();

//        ConstraintLayout.LayoutParams prams = (ConstraintLayout.LayoutParams) getLayoutParams();
//        prams.width = 0;
//        prams.height = ConstraintSet.WRAP_CONTENT;
//        prams.topToTop = ConstraintSet.PARENT_ID;
//        prams.rightToRight = ConstraintSet.PARENT_ID;
//        prams.leftToLeft = ConstraintSet.PARENT_ID;
//
//
//        RecyclingImageView imageEvent = new RecyclingImageView(getContext());
//        imageEvent.setId(R.id.item_timeline_event);
//
//
//        addView(imageEvent);
//
//        constraintSet = new ConstraintSet();
//        constraintSet.clone(this);
//
//        constraintSet.connect(imageEvent.getId(),ConstraintSet.);
    }

    public void setGlide(RequestManager glide) {
        if (this.glide == null)
            this.glide = glide;
    }

    public RequestManager getGlide() {
        if (glide == null)
            glide = Glide.with(getContext());
        return glide;
    }

    public void setListener(HeaderListener listener) {
        if (this.listener == null)
            this.listener = listener;
    }

    public void setAvatar(final BuzzBean bean) {
        if (bean.avatar == null || bean.avatar.equals("")) {
            avatar.setImageResource(bean.gender == Constants.GENDER_TYPE_MAN
                    ? R.drawable.ic_avatar_circle_male_border
                    : R.drawable.ic_avatar_circle_female_border);
        } else
            getGlide().load(bean.avatar)
                    .apply(bean.gender == Constants.GENDER_TYPE_MAN
                            ? ImagesUtils.OPTION_ROUNDED_AVATAR_MAN_BORDER
                            : ImagesUtils.OPTION_ROUNDED_AVATAR_WOMAN_BORDER)
                    .into(avatar);

        avatar.setOnClickListener(null);
        avatar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null)
                    listener.onDisplayProfileScreen(bean, v);
            }
        });
    }

    public void setIconEvents(final BuzzBean bean) {
        final boolean isOwn = bean.userId.equals(UserPreferences.getInstance().getUserId());
        if (isOwn) {
            events.setImageResource(R.drawable.ic_buzz_delete);
        } else {
            events.setImageResource(bean.isFavorite == Constants.BUZZ_TYPE_IS_FAVORITE ? R.drawable.ic_list_buzz_item_favorited : R.drawable.ic_list_buzz_item_favorite);
        }
        events.setOnClickListener(null);
        events.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null)
                    listener.onEvent(bean, v);
            }
        });
    }

    public void setIconEvents(@DrawableRes int idRes) {
        events.setImageResource(idRes);
    }

    public void setPrivacy(BuzzBean bean) {
        if (bean.privacy == Constants.PRIVACY_PUBLIC)
            dateTimeAndLocal.setVectorDrawableRight(R.drawable.ic_small_privacy_public_24dp_gray);
        else
            dateTimeAndLocal.setVectorDrawableRight(R.drawable.ic_small_privacy_only_me_24dp_gray);
    }

    public void setTimeAndLocal(BuzzBean bean) {
        String strTimeAnLocal;
        try {
            Calendar calendarNow = Calendar.getInstance();

            Calendar calendarSend = Calendar.getInstance(TimeZone.getDefault());
            Date dateSend = TimeUtils.YYYYMMDDHHMMSS.parse(bean.buzzTime);
            calendarSend.setTime(dateSend);

            strTimeAnLocal = TimeUtils.getTimelineDif(calendarSend, calendarNow);
        } catch (ParseException e) {
            e.printStackTrace();
            strTimeAnLocal = getResources().getString(R.string.common_now);
        }

        dateTimeAndLocal.setText(String.format(getResources().getString(R.string.timeline_time_location), strTimeAnLocal, RegionUtils.getInstance(getContext()).getRegionName(bean.region)));
    }

    public void setTimeAndLocal(String time, int region) {
        dateTimeAndLocal.setText(String.format(getResources().getString(R.string.timeline_time_location), time,
                RegionUtils.getInstance(getContext()).getRegionName(region)));
    }

    public void setTitle(final BuzzBean bean, @TimelineType int viewType) {
        spanTitle.clear();
        spanTitle.append(bean.userName);
        spanTitle.setSpan(new ClickableSpan() {

            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setColor(ds.linkColor);    // you can use custom color
                ds.setUnderlineText(false);    // this remove the underline
            }

            @Override
            public void onClick(View widget) {
                if (listener != null)
                    listener.onDisplayProfileScreen(bean, avatar);
            }

        }, 0, spanTitle.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        if (viewType == TimelineType.BUZZ_TYPE_MULTI_BUZZ_LIVE_STREAM) {
            if (bean.listChildBuzzes.get(0).streamStatus.equals(Constants.LIVE_STREAM_ON)) {
                spanTitle.append(getResources().getString(R.string.timeline_time_live_stream_on));
            } else {
                spanTitle.append(getResources().getString(R.string.timeline_time_live_stream_off));
            }
        } else if (viewType == TimelineType.BUZZ_TYPE_SHARE_AUDIO) {
            spanTitle.append(" ");
            spanTitle.append(Html.fromHtml(getResources().getString(R.string.timeline_time_share_audio)));
            onClickSharedOwner(bean);
        } else if (viewType == TimelineType.BUZZ_TYPE_SHARE_LIVE_STREAM) {
            spanTitle.append(" ");
            spanTitle.append(Html.fromHtml(getResources().getString(R.string.timeline_time_share_video)));
            onClickSharedOwner(bean);
        }

        //Display Tag friends on Title
        onClickTagFriends(bean);
        //Set span text
        txtTitle.setText(spanTitle);
        txtTitle.setHighlightColor(Color.TRANSPARENT);
        txtTitle.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void onClickSharedOwner(final BuzzBean bean) {
        spanTitle.append(bean.shareDetailBean.userName);
        spanTitle.setSpan(new ClickableSpan() {

            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setColor(ds.linkColor);    // you can use custom color
                ds.setUnderlineText(false);    // this remove the underline
            }

            @Override
            public void onClick(View widget) {
                if (listener != null)
                    listener.onDisplayProfileScreen(bean, avatar);
            }
        }, spanTitle.length() - bean.shareDetailBean.userName.length(), spanTitle.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    private void onClickTagFriends(final BuzzBean bean) {
        if (bean.tagNumber >= 1) {
            spanTitle.append(getResources().getString(R.string.timeline_time_with));
            spanTitle.append(bean.tagList.get(0).userName);
            spanTitle.setSpan(new ClickableSpan() {
                @Override
                public void updateDrawState(TextPaint ds) {
                    ds.setColor(ds.linkColor);    // you can use custom color
                    ds.setUnderlineText(false);    // this remove the underline
                }

                @Override
                public void onClick(View widget) {
                    if (listener != null)
                        listener.onDisplayProfileScreen(bean.tagList.get(0).userId, txtTitle);
                }

            }, spanTitle.length() - bean.tagList.get(0).userName.length(), spanTitle.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        if (bean.tagNumber > 1) {
            spanTitle.append(getResources().getString(R.string.timeline_time_and));
            String other = String.format(getResources().getString(R.string.timeline_time_other), String.valueOf((bean.tagNumber - 1)));
            spanTitle.append(other);
            spanTitle.setSpan(new ClickableSpan() {

                @Override
                public void updateDrawState(TextPaint ds) {
                    ds.setColor(ds.linkColor);    // you can use custom color
                    ds.setUnderlineText(false);    // this remove the underline
                }

                @Override
                public void onClick(View widget) {
                    if (listener != null)
                        listener.onDisplayTagFriendsScreen(bean.tagList, txtTitle);
                }

            }, spanTitle.length() - other.length(), spanTitle.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }
}
