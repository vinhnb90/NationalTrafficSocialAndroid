package com.vn.ntsc.ui.timeline.viewholder;

import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.AppCompatTextView;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.nankai.designlayout.progress.LoadingIndicatorView;
import com.vn.ntsc.R;
import com.vn.ntsc.repository.model.timeline.TimelineType;
import com.vn.ntsc.repository.model.timeline.datas.BuzzBean;
import com.vn.ntsc.repository.preferece.UserPreferences;
import com.vn.ntsc.ui.timeline.core.TimelineListener;
import com.vn.ntsc.utils.Constants;
import com.vn.ntsc.utils.ImagesUtils;
import com.vn.ntsc.utils.RegionUtils;
import com.vn.ntsc.utils.Utils;
import com.vn.ntsc.utils.time.TimeUtils;
import com.vn.ntsc.widget.adapter.BaseViewHolder;
import com.vn.ntsc.widget.views.images.RecyclingImageView;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by nankai on 10/25/2017.
 */

public abstract class BaseTimelineViewHolder extends BaseViewHolder implements ITimelineViewHolder {
    private final String TAG = BaseTimelineViewHolder.class.getSimpleName();

    @TimelineType
    int viewType;
    protected RequestManager glide;

    @Nullable
    @BindView(R.id.layout_content)
    ConstraintLayout layoutContent;

    @Nullable
    @BindView(R.id.item_timeline_header_event)
    RecyclingImageView eventsTop;
    @Nullable
    @BindView(R.id.item_timeline_header_event_remove_template)
    RecyclingImageView eventsRemoveTemplate;
    @Nullable
    @BindView(R.id.item_timeline_refresh)
    RecyclingImageView refresh;
    @Nullable
    @BindView(R.id.item_timeline_loading_title)
    AppCompatTextView loadTitle;
    @Nullable
    @BindView(R.id.item_timeline_loading)
    LoadingIndicatorView loading;
    @Nullable
    @BindView(R.id.item_timeline_icon_status_private)
    RecyclingImageView privacy;
    @Nullable
    @BindView(R.id.item_timeline_username)
    AppCompatTextView txtUserName;
    @Nullable
    @BindView(R.id.item_timeline_header_description)
    AppCompatTextView description;
    @Nullable
    @BindView(R.id.item_timeline_header_more)
    AppCompatTextView seeMore;
    @Nullable
    @BindView(R.id.item_timeline_date_post)
    AppCompatTextView dateTimePost;
    @Nullable
    @BindView(R.id.item_timeline_avatar)
    RecyclingImageView avatar;
    @Nullable
    @BindView(R.id.layout_item_timeline_comment_number)
    AppCompatTextView commentNumber;
    @Nullable
    @BindView(R.id.layout_item_timeline_share_number)
    TextView shareNumber;
    @Nullable
    @BindView(R.id.layout_item_timeline_like)
    RelativeLayout layoutLike;
    @Nullable
    @BindView(R.id.layout_item_timeline_like_number)
    AppCompatTextView likeNumber;
    @Nullable
    @BindView(R.id.layout_item_timeline_image_like)
    RecyclingImageView like;
    @Nullable
    @BindView(R.id.layout_item_timeline_share)
    RelativeLayout share;
    @Nullable
    @BindView(R.id.layout_item_timeline_comment)
    RelativeLayout comment;
    @Nullable
    @BindView(R.id.timeline_view_number)
    AppCompatTextView viewNumber;
    @Nullable
    @BindView(R.id.item_timeline_approve_buzz)
    View approve;

    private SpannableStringBuilder spanTxtTitle;
    SpannableStringBuilder spanTxtTitleShare;

    public BaseTimelineViewHolder(View itemView, int viewType) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.viewType = viewType;
    }

    @Override
    public BaseTimelineViewHolder initSpanTitle(SpannableStringBuilder spanTxtTitle) {
        this.spanTxtTitle = spanTxtTitle;
        return this;
    }

    @Override
    public BaseTimelineViewHolder initSpanTitleShare(SpannableStringBuilder spanTxtTitleShare) {
        this.spanTxtTitleShare = spanTxtTitleShare;
        return this;
    }

    @Override
    public BaseTimelineViewHolder initGlide(RequestManager glide) {
        this.glide = glide;
        return this;
    }

    @Override
    public BaseTimelineViewHolder initBindView(boolean isTemplate, BuzzBean item, int position, TimelineListener listener) {
        if (isTemplate)
            onBindViewTemplate(item, position, listener);
        else
            onBindView(item, position, listener);
        return this;
    }

    //------------------------------------------------------------------------------//
    //-----------------------------------BindView ----------------------------------//
    //------------------------------------------------------------------------------//

    /**
     * @param bean     {@link BuzzBean}
     * @param position position add template
     * @param listener After the preview is created and then the response from the server returns the upload to alcohol,
     *                 then delete the preview and then the standard version from the server, but if the case can not get the standard version Instead, the reset button will be displayed for the user to update manually
     *                 <p>
     *                 Called when we want to create a preview image of the buzz will be uploaded to the server
     */
    protected void onBindViewTemplate(final BuzzBean bean, final int position, final TimelineListener listener) {
        spanTxtTitle.clear();

        loadImage(R.drawable.ic_buzz_delete, eventsTop);
        loadImage(R.drawable.ic_buzz_delete, eventsRemoveTemplate);

        if (bean.isError) {
            refresh.setVisibility(View.VISIBLE);
            loadTitle.setVisibility(View.INVISIBLE);
            loading.setVisibility(View.INVISIBLE);
            refresh.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onRetryBuzzDetailRequest(bean.buzzDetailRequest, bean.buzzId, position, view);
                }
            });

        } else {
            refresh.setVisibility(View.INVISIBLE);
            loadTitle.setVisibility(View.VISIBLE);
            loading.setVisibility(View.VISIBLE);
        }

        if (bean.privacy == Constants.PRIVACY_PUBLIC)
            loadImage(R.drawable.ic_public, privacy);
        else
            loadImage(R.drawable.ic_privacy_only_me, privacy);

        description.setText(bean.buzzValue);
        description.post(new Runnable() {
            @Override
            public void run() {
                if (description.getLineCount() > 4)
                    seeMore.setVisibility(View.VISIBLE);
                else
                    seeMore.setVisibility(View.INVISIBLE);
            }
        });

        String time = itemView.getResources().getString(R.string.common_now);
        //time location
        dateTimePost.setText(String.format(itemView.getResources().getString(R.string.timeline_time_location), time, RegionUtils.getInstance(itemView.getContext()).getRegionName(bean.region)));
        //icon privacy

        //display avatar
        loadImageRounded(bean.avatar, bean.gender, avatar);

        String strCommentNumber = String.format(itemView.getResources().getString(R.string.timeline_comment), String.valueOf(0));
        String strLikeNumber = String.format(itemView.getResources().getString(R.string.timeline_like), String.valueOf(0));
        String strShareNumber = String.format(itemView.getResources().getString(R.string.timeline_share), String.valueOf(0));
        //Comment number
        commentNumber.setText(strCommentNumber);
        //share number
        shareNumber.setText(strShareNumber);
        //ic_like number
        likeNumber.setText(strLikeNumber);

        spanTxtTitle.append(bean.userName);

        //event click icon favorite
        eventsRemoveTemplate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onRemoveStatusTemplate(bean, position, view);
            }
        });
    }

    protected void onBindView(final BuzzBean bean, final int position, final TimelineListener listener) {

        if (approve != null)
            if (bean.isApproved == Constants.IS_NOT_APPROVED)
                approve.setVisibility(View.VISIBLE);
            else
                approve.setVisibility(View.INVISIBLE);

        spanTxtTitle.clear();

        String time;
        try {
            Calendar calendarNow = Calendar.getInstance();

            Calendar calendarSend = Calendar.getInstance(TimeZone.getDefault());
            Date dateSend = TimeUtils.YYYYMMDDHHMMSS.parse(bean.buzzTime);
            calendarSend.setTime(dateSend);

            time = TimeUtils.getTimelineDif(calendarSend, calendarNow);
        } catch (ParseException e) {
            e.printStackTrace();
            time = itemView.getResources().getString(R.string.common_now);
        }


        final boolean isOwn = bean.userId.equals(UserPreferences.getInstance().getUserId());

        //display remove when is own  and  favorite or unFavorite when not is own
        if (isOwn) {
            loadImage(R.drawable.ic_buzz_delete, eventsTop);
        } else {
            loadImage(bean.isFavorite == Constants.BUZZ_TYPE_IS_FAVORITE ? R.drawable.ic_list_buzz_item_favorited : R.drawable.ic_list_buzz_item_favorite, eventsTop);
        }

        //status ic_like or ic_unlike
        if (bean.like.isLike == Constants.BUZZ_LIKE_TYPE_UNLIKE)
            loadImage(R.drawable.ic_unlike, like);
        else
            loadImage(R.drawable.ic_like, like);

        //icon privacy
        if (bean.privacy == Constants.PRIVACY_PUBLIC)
            loadImage(R.drawable.ic_public, privacy);
        else
            loadImage(R.drawable.ic_privacy_only_me, privacy);

        if (!Utils.isEmptyOrNull(bean.buzzValue)) {
            description.setVisibility(View.VISIBLE);
            description.setText(bean.buzzValue);
            description.post(new Runnable() {
                @Override
                public void run() {
                    if (description.getLineCount() > 4) {
                        seeMore.setVisibility(View.VISIBLE);
                        seeMore.setTypeface(null, Typeface.BOLD);
                    } else {
                        seeMore.setVisibility(View.INVISIBLE);
                    }
                }
            });
        } else {
            description.setText("");
            description.setVisibility(View.INVISIBLE);
        }

        String strCommentNumber = String.format(itemView.getResources().getString(R.string.timeline_comment), Utils.format(bean.getTotalCommentNumber()));
        String strLikeNumber = String.format(itemView.getResources().getString(R.string.timeline_like), Utils.format(bean.like.likeNumber));
        String strShareNumber = String.format(itemView.getResources().getString(R.string.timeline_share), Utils.format(bean.shareNumber));
        //Comment number
        commentNumber.setText(strCommentNumber);
        //share number
        shareNumber.setText(strShareNumber);
        //ic_like number
        likeNumber.setText(strLikeNumber);

        //time location
        dateTimePost.setText(String.format(itemView.getResources().getString(R.string.timeline_time_location), time, RegionUtils.getInstance(itemView.getContext()).getRegionName(bean.region)));

        //display avatar
        loadImageRounded(bean.avatar, bean.gender, avatar);

        if (!Utils.isEmptyOrNull(bean.userName)) {
            spanTxtTitle.append(bean.userName);
            spanTxtTitle.setSpan(new ClickableSpan() {

                @Override
                public void updateDrawState(TextPaint ds) {
                    ds.setColor(ds.linkColor);    // you can use custom color
                    ds.setUnderlineText(false);    // this remove the underline
                }

                @Override
                public void onClick(View widget) {
                    listener.onShowProfile(bean, position, avatar);
                }

            }, 0, spanTxtTitle.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        viewNumber.setVisibility(View.INVISIBLE);

        if (viewType == TimelineType.BUZZ_TYPE_MULTI_BUZZ_LIVE_STREAM) {
            if (bean.listChildBuzzes.get(0).streamStatus.equals(Constants.LIVE_STREAM_ON)) {
                spanTxtTitle.append(itemView.getResources().getString(R.string.timeline_time_live_stream_on));
            } else {
                spanTxtTitle.append(itemView.getResources().getString(R.string.timeline_time_live_stream_off));
                viewNumber.setVisibility(View.VISIBLE);
                viewNumber.setText(String.format(itemView.getResources().getString(R.string.timeline_time_view), bean.listChildBuzzes.get(0).viewNumber));
            }
        } else if (viewType == TimelineType.BUZZ_TYPE_SHARE_AUDIO) {
            spanTxtTitle.append(" ");
            spanTxtTitle.append(Html.fromHtml(itemView.getResources().getString(R.string.timeline_time_share_audio)));
            onClickSharedOwner(bean, position, listener);
        } else if (viewType == TimelineType.BUZZ_TYPE_SHARE_LIVE_STREAM) {
            spanTxtTitle.append(" ");
            spanTxtTitle.append(Html.fromHtml(itemView.getResources().getString(R.string.timeline_time_share_video)));
            onClickSharedOwner(bean, position, listener);
        } else if (viewType == TimelineType.BUZZ_TYPE_MULTI_BUZZ_VIDEO_1_TEMPLATE || viewType == TimelineType.BUZZ_TYPE_MULTI_BUZZ_VIDEO_1) {
            viewNumber.setVisibility(View.VISIBLE);
            viewNumber.setText(String.format(itemView.getResources().getString(R.string.timeline_time_view), bean.listChildBuzzes.get(0).viewNumber));
        }

        onClickTagFriends(bean, position, listener);

        txtUserName.setText(spanTxtTitle);
        txtUserName.setHighlightColor(Color.TRANSPARENT);
        txtUserName.setMovementMethod(LinkMovementMethod.getInstance());

        //avatar click
        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onShowProfile(bean, position, view);
            }
        });

        seeMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onShowComment(bean, position, comment);
            }
        });

        //event click icon favorite
        eventsTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isOwn) {
                    listener.onDeleteStatus(bean, position, view);
                } else {
                    if (bean.isApproved == Constants.IS_APPROVED)
                        listener.onFavorite(bean, position, view);
                    else
                        listener.onApproval(bean, position, itemView);
                }
            }
        });

        //share
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bean.isApproved == Constants.IS_APPROVED)
                    listener.onShare(bean, position, view);
                else
                    listener.onApproval(bean, position, itemView);
            }
        });

        //show ic_comment detail
        comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bean.isApproved == Constants.IS_APPROVED)
                    listener.onShowComment(bean, position, view);
                else
                    listener.onApproval(bean, position, itemView);
            }
        });

        //ic_like
        layoutLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bean.isApproved == Constants.IS_APPROVED)
                    listener.onLike(bean, position, view);
                else
                    listener.onApproval(bean, position, itemView);
            }
        });
    }

    //------------------------------------------------------------------------------//
    //----------------------------------- Click span -------------------------------//
    //------------------------------------------------------------------------------//

    private void onClickSharedOwner(final BuzzBean bean, final int position, final TimelineListener listener) {
        spanTxtTitle.append(bean.shareDetailBean.userName);
        spanTxtTitle.setSpan(new ClickableSpan() {

            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setColor(ds.linkColor);    // you can use custom color
                ds.setUnderlineText(false);    // this remove the underline
            }

            @Override
            public void onClick(View widget) {
                listener.onShowProfile(bean.shareDetailBean.userId, position, txtUserName);
            }
        }, spanTxtTitle.length() - bean.shareDetailBean.userName.length(), spanTxtTitle.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    private void onClickTagFriends(final BuzzBean bean, final int position, final TimelineListener listener) {
        if (bean.tagNumber >= 1) {
            spanTxtTitle.append(itemView.getResources().getString(R.string.timeline_time_with));
            spanTxtTitle.append(bean.tagList.get(0).userName);
            spanTxtTitle.setSpan(new ClickableSpan() {
                @Override
                public void updateDrawState(TextPaint ds) {
                    ds.setColor(ds.linkColor);    // you can use custom color
                    ds.setUnderlineText(false);    // this remove the underline
                }

                @Override
                public void onClick(View widget) {
                    listener.onShowProfile(bean.tagList.get(0).userId, position, txtUserName);
                }

            }, spanTxtTitle.length() - bean.tagList.get(0).userName.length(), spanTxtTitle.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        if (bean.tagNumber > 1) {
            spanTxtTitle.append(itemView.getResources().getString(R.string.timeline_time_and));
            String other = String.format(itemView.getResources().getString(R.string.timeline_time_other), String.valueOf((bean.tagNumber - 1)));
            spanTxtTitle.append(other);
            spanTxtTitle.setSpan(new ClickableSpan() {

                @Override
                public void updateDrawState(TextPaint ds) {
                    ds.setColor(ds.linkColor);    // you can use custom color
                    ds.setUnderlineText(false);    // this remove the underline
                }

                @Override
                public void onClick(View widget) {
                    listener.onShowTagFriendsDetail(bean.tagList, position, txtUserName);
                }

            }, spanTxtTitle.length() - other.length(), spanTxtTitle.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }

    //------------------------------------------------------------------------------//
    //----------------------------------- Glide ------------------------------------//
    //------------------------------------------------------------------------------//

    protected void loadImage(String url, RequestOptions options, ImageView imageView) {
        if (url == null) {
            glide.clear(imageView);
            imageView.setImageResource(R.color.default_image_loading);
        } else
            glide.load(url)
                    .thumbnail(ImagesUtils.THUMBNAIL)
                    .apply(ImagesUtils.OPTION_DEFAULT)
                    .apply(options)
                    .into(imageView);
    }

    protected void loadImageBlur(String url, RequestOptions options, ImageView imageView) {
        if (url == null) {
            glide.clear(imageView);
            imageView.setImageResource(R.color.default_image_loading);
        } else
            glide.load(url)
                    .thumbnail(ImagesUtils.THUMBNAIL)
                    .apply(ImagesUtils.OPTION_DEFAULT)
                    .apply(ImagesUtils.OPTION_BLUR)
                    .apply(options)
                    .into(imageView);
    }

    protected void loadImage(String url, ImageView imageView) {
        if (url == null) {
            glide.clear(imageView);
            imageView.setImageResource(R.color.default_image_loading);
        } else
            glide.load(url)
                    .thumbnail(ImagesUtils.THUMBNAIL)
                    .apply(ImagesUtils.OPTION_DEFAULT)
                    .into(imageView);
    }

    protected void loadImageBlur(String url, ImageView imageView) {
        if (url == null) {
            glide.clear(imageView);
            imageView.setImageResource(R.color.default_image_loading);
        } else
            glide.load(url)
                    .thumbnail(ImagesUtils.THUMBNAIL)
                    .apply(ImagesUtils.OPTION_DEFAULT)
                    .apply(ImagesUtils.OPTION_BLUR)
                    .into(imageView);
    }

    protected void loadImageRounded(String url, int gender, final ImageView imageView) {
        if (url == null || url.equals("")) {
            glide.clear(imageView);
            imageView.setImageResource(gender == Constants.GENDER_TYPE_MAN
                    ? R.drawable.ic_avatar_circle_male_border
                    : R.drawable.ic_avatar_circle_female_border);
        } else
            glide.load(url)
                    .thumbnail(ImagesUtils.THUMBNAIL)
                    .apply(gender == Constants.GENDER_TYPE_MAN
                            ? ImagesUtils.OPTION_ROUNDED_AVATAR_MAN_BORDER
                            : ImagesUtils.OPTION_ROUNDED_AVATAR_WOMAN_BORDER)
                    .into(imageView);
    }

    protected void loadImageFillWidth(String url, ImageView imageView) {
        loadImage(url, ImagesUtils.DOWNLOAD_ONLY_OPTIONS_FULL_SCREEN, imageView);
    }

    protected void loadImageFillWidthBlur(String url, ImageView imageView) {
        loadImageBlur(url, ImagesUtils.DOWNLOAD_ONLY_OPTIONS_FULL_SCREEN, imageView);
    }

    protected void loadImageLiveStreamFillWidth(String url, ImageView imageView) {
        if (url == null) {
            glide.clear(imageView);
            imageView.setImageResource(R.color.default_image_loading);
        } else
            glide.load(url)
                    .thumbnail(ImagesUtils.THUMBNAIL)
                    .apply(ImagesUtils.OPTION_DEFAULT)
                    .apply(ImagesUtils.DOWNLOAD_ONLY_OPTIONS_FULL_SCREEN)
                    .into(imageView);
    }

    protected void loadImageLiveStreamFillWidthBlur(String url, ImageView imageView) {
        if (url == null) {
            glide.clear(imageView);
            imageView.setImageResource(R.color.default_image_loading);
        } else
            glide.load(url)
                    .thumbnail(ImagesUtils.THUMBNAIL)
                    .apply(ImagesUtils.OPTION_DEFAULT)
                    .apply(ImagesUtils.OPTION_BLUR)
                    .apply(ImagesUtils.DOWNLOAD_ONLY_OPTIONS_FULL_SCREEN)
                    .into(imageView);
    }

    //Local images
    protected void loadImageLocal(String url, ImageView imageView) {
        if (url == null) {
            glide.clear(imageView);
            imageView.setImageResource(R.color.default_image_loading);
        } else
            glide.load(url)
                    .thumbnail(ImagesUtils.THUMBNAIL)
                    .apply(ImagesUtils.OPTION_DEFAULT)
                    .into(imageView);
    }

    protected void loadImage(int idResource, ImageView imageView) {
        imageView.setImageResource(idResource);
    }
}
