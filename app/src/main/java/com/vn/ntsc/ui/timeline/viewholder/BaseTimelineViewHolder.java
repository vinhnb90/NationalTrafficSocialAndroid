package com.vn.ntsc.ui.timeline.viewholder;

import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.AppCompatTextView;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.nankai.designlayout.progress.LoadingIndicatorView;
import com.vn.ntsc.R;
import com.vn.ntsc.repository.model.timeline.TimelineType;
import com.vn.ntsc.repository.model.timeline.datas.BuzzBean;
import com.vn.ntsc.repository.model.timeline.datas.sub.ListTagFriendsBean;
import com.vn.ntsc.repository.preferece.UserPreferences;
import com.vn.ntsc.ui.timeline.core.TimelineListener;
import com.vn.ntsc.utils.Constants;
import com.vn.ntsc.utils.ImagesUtils;
import com.vn.ntsc.utils.LogUtils;
import com.vn.ntsc.utils.Utils;
import com.vn.ntsc.utils.ViewUtils;
import com.vn.ntsc.widget.adapter.BaseViewHolder;
import com.vn.ntsc.widget.views.images.RecyclingImageView;
import com.vn.ntsc.widget.views.timeline.TimelineFooterView;
import com.vn.ntsc.widget.views.timeline.TimelineHeaderView;

import java.util.ArrayList;

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
    @BindView(R.id.item_timeline_header)
    TimelineHeaderView timelineHeaderView;
    @Nullable
    @BindView(R.id.item_timeline_footer)
    TimelineFooterView timelineFooterView;

    @Nullable
    @BindView(R.id.layout_content)
    ConstraintLayout layoutContent;
    @Nullable
    @BindView(R.id.item_timeline_header_event_remove_template)
    RecyclingImageView eventsRemoveTemplate;
    @Nullable
    @BindView(R.id.item_timeline_refresh)
    RecyclingImageView refresh;
    @Nullable
    @BindView(R.id.item_timeline_template_description)
    AppCompatTextView templateDescription;
    @Nullable
    @BindView(R.id.item_timeline_loading)
    LoadingIndicatorView loading;
    @Nullable
    @BindView(R.id.item_timeline_header_description)
    AppCompatTextView description;
    @Nullable
    @BindView(R.id.item_timeline_header_more)
    AppCompatTextView seeMore;
    @Nullable
    @BindView(R.id.item_timeline_approve_buzz)
    View approve;

    public BaseTimelineViewHolder(View itemView, int viewType) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.viewType = viewType;
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
        if (timelineHeaderView != null) {
            timelineHeaderView.setGlide(glide);
            timelineHeaderView.setAvatar(bean);
            timelineHeaderView.setIconEvents(R.drawable.ic_buzz_delete);
            timelineHeaderView.setPrivacy(bean);
            timelineHeaderView.setTitle(bean, viewType);
            timelineHeaderView.setTimeAndLocal(itemView.getResources().getString(R.string.common_now), bean.region);
        }

        if (timelineFooterView != null) {
            String strLikeNumber = String.format(itemView.getResources().getString(R.string.timeline_like), String.valueOf(0));
            String strCommentNumber = String.format(itemView.getResources().getString(R.string.timeline_comment), String.valueOf(0));
            String strShareNumber = String.format(itemView.getResources().getString(R.string.timeline_share), String.valueOf(0));
            timelineFooterView.setLikeNumber(strLikeNumber);
            timelineFooterView.setCommentNumber(strCommentNumber);
            timelineFooterView.setShareNumber(strShareNumber);
        }

        loadImage(R.drawable.ic_buzz_delete, eventsRemoveTemplate);

        if (bean.isError) {
            refresh.setVisibility(View.VISIBLE);
            templateDescription.setVisibility(View.INVISIBLE);
            loading.setVisibility(View.INVISIBLE);
            refresh.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onRetryBuzzDetailRequest(bean.buzzDetailRequest, bean.buzzId, position, view);
                }
            });

        } else {
            refresh.setVisibility(View.INVISIBLE);
            templateDescription.setVisibility(View.VISIBLE);
            loading.setVisibility(View.VISIBLE);
        }

        description.setText(bean.buzzValue);
        description.post(new Runnable() {
            @Override
            public void run() {
                if (description.getLineCount() > 4)
                    seeMore.setVisibility(View.VISIBLE);
                else
                    seeMore.setVisibility(View.GONE);
            }
        });

        //event click icon favorite
        eventsRemoveTemplate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onRemoveStatusTemplate(bean, position, view);
            }
        });
    }

    protected void onBindView(final BuzzBean bean, final int position, final TimelineListener listener) {
        if (bean == null)
            return;
        if (approve != null)
            if (bean.isApproved == Constants.IS_NOT_APPROVED)
                approve.setVisibility(View.VISIBLE);
            else
                approve.setVisibility(View.INVISIBLE);

        //Header view
        if (timelineHeaderView != null) {
            timelineHeaderView.setGlide(glide);
            timelineHeaderView.setAvatar(bean);
            timelineHeaderView.setIconEvents(bean);
            timelineHeaderView.setPrivacy(bean);
            timelineHeaderView.setTitle(bean, viewType);
            timelineHeaderView.setTimeAndLocal(bean);
            timelineHeaderView.setListener(new TimelineHeaderView.HeaderListener() {
                @Override
                public void onEvent(BuzzBean bean, View view) {
                    final boolean isOwn = bean.userId.equals(UserPreferences.getInstance().getUserId());
                    if (isOwn) {
                        listener.onRemoveStatus(bean, position, view);
                    } else {
                        listener.onFavorite(bean, position, view);
                    }
                }

                @Override
                public void onDisplayProfileScreen(String userId, View view) {
                    listener.onDisplayProfileScreen(userId, position, view);
                }

                @Override
                public void onDisplayProfileScreen(BuzzBean bean, View view) {
                    listener.onDisplayProfileScreen(bean, position, view);
                }

                @Override
                public void onDisplayTagFriendsScreen(ArrayList<ListTagFriendsBean> tagList, View view) {
                    listener.onDisplayTagFriendsScreen(tagList, position, view);
                }
            });
        }

        //Footer view
        if (timelineFooterView != null) {
            if (bean.like.isLike == Constants.BUZZ_LIKE_TYPE_UNLIKE)
                timelineFooterView.setIconLke(R.drawable.ic_unlike);
            else
                timelineFooterView.setIconLke(R.drawable.ic_like);
            timelineFooterView.setLikeNumber(bean);
            timelineFooterView.setCommentNumber(bean);
            timelineFooterView.setShareNumber(bean);
            if ((viewType == TimelineType.BUZZ_TYPE_MULTI_BUZZ_LIVE_STREAM || viewType == TimelineType.BUZZ_TYPE_MULTI_BUZZ_VIDEO_1)
                    && bean.listChildBuzzes != null
                    && bean.listChildBuzzes.size() > 0) {
                if (viewType == TimelineType.BUZZ_TYPE_MULTI_BUZZ_LIVE_STREAM) {
                    if (bean.listChildBuzzes.get(0).streamStatus.equals(Constants.LIVE_STREAM_OFF)) {
                        timelineFooterView.setVisibleViewNumber(true);
                        timelineFooterView.setViewNumber(bean.listChildBuzzes.get(0));
                    } else
                        timelineFooterView.setVisibleViewNumber(false);
                } else {
                    timelineFooterView.setVisibleViewNumber(true);
                    timelineFooterView.setViewNumber(bean.listChildBuzzes.get(0));
                }
            }

            timelineFooterView.setListener(new TimelineFooterView.FooterListener() {
                @Override
                public void onShare(BuzzBean bean, View view) {
                    if (bean.isApproved == Constants.IS_APPROVED)
                        listener.onShare(bean, position, view);
                    else
                        listener.onApproval(bean, position, itemView);
                }

                @Override
                public void onShowComment(BuzzBean bean, View view) {
                    if (bean.isApproved == Constants.IS_APPROVED)
                        listener.onDisplayCommentScreen(bean, position, view);
                    else
                        listener.onApproval(bean, position, itemView);
                }

                @Override
                public void onLike(BuzzBean bean, View view) {
                    if (bean.isApproved == Constants.IS_APPROVED)
                        listener.onLike(bean, position, view);
                    else
                        listener.onApproval(bean, position, itemView);
                }
            });
        }

        //Description
        if (description != null) {
            if (!Utils.isEmptyOrNull(bean.buzzValue)) {
                description.setVisibility(View.VISIBLE);
                description.setText(bean.buzzValue);
                description.post(new Runnable() {
                    @Override
                    public void run() {
                        if (seeMore != null) {
                            if (description.getLineCount() > 4) {
                                seeMore.setVisibility(View.VISIBLE);
                                seeMore.setTypeface(null, Typeface.BOLD);
                                seeMore.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        listener.onDisplayCommentScreen(bean, position, timelineFooterView);
                                    }
                                });
                            } else {
                                seeMore.setVisibility(View.GONE);
                            }
                        }
                    }
                });
            } else {
                description.setText("");
                description.setVisibility(View.GONE);
                if (seeMore != null)
                    seeMore.setVisibility(View.GONE);
            }
        } else {
            LogUtils.w(TAG, "description is null!");
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
                    .apply(ImagesUtils.OPTION_DEFAULT)
                    .apply(options)
                    .transition(DrawableTransitionOptions.withCrossFade(ViewUtils.getShortAnimTime(
                            imageView)))
                    .into(imageView);
    }

    protected void loadImageBlur(String url, RequestOptions options, ImageView imageView) {
        if (url == null) {
            glide.clear(imageView);
            imageView.setImageResource(R.color.default_image_loading);
        } else
            glide.load(url)
                    .apply(ImagesUtils.OPTION_DEFAULT)
                    .apply(ImagesUtils.OPTION_BLUR)
                    .apply(options)
                    .transition(DrawableTransitionOptions.withCrossFade(ViewUtils.getShortAnimTime(
                            imageView)))
                    .into(imageView);
    }

    protected void loadImage(String url, ImageView imageView) {
        if (url == null) {
            glide.clear(imageView);
            imageView.setImageResource(R.color.default_image_loading);
        } else
            glide.load(url)
                    .apply(ImagesUtils.OPTION_DEFAULT)
                    .transition(DrawableTransitionOptions.withCrossFade(ViewUtils.getShortAnimTime(
                            imageView)))
                    .into(imageView);
    }

    protected void loadImageBlur(String url, ImageView imageView) {
        if (url == null) {
            glide.clear(imageView);
            imageView.setImageResource(R.color.default_image_loading);
        } else
            glide.load(url)
                    .apply(ImagesUtils.OPTION_DEFAULT)
                    .apply(ImagesUtils.OPTION_BLUR)
                    .transition(DrawableTransitionOptions.withCrossFade(ViewUtils.getShortAnimTime(
                            imageView)))
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
                    .apply(ImagesUtils.OPTION_DEFAULT)
                    .apply(ImagesUtils.DOWNLOAD_ONLY_OPTIONS_FULL_SCREEN)
                    .transition(DrawableTransitionOptions.withCrossFade(ViewUtils.getShortAnimTime(
                            imageView)))
                    .into(imageView);
    }

    protected void loadImageLiveStreamFillWidthBlur(String url, ImageView imageView) {
        if (url == null) {
            glide.clear(imageView);
            imageView.setImageResource(R.color.default_image_loading);
        } else
            glide.load(url)
                    .apply(ImagesUtils.OPTION_DEFAULT)
                    .apply(ImagesUtils.OPTION_BLUR)
                    .apply(ImagesUtils.DOWNLOAD_ONLY_OPTIONS_FULL_SCREEN)
                    .transition(DrawableTransitionOptions.withCrossFade(ViewUtils.getShortAnimTime(
                            imageView)))
                    .into(imageView);
    }

    //Local images
    protected void loadImageLocal(String url, ImageView imageView) {
        if (url == null) {
            glide.clear(imageView);
            imageView.setImageResource(R.color.default_image_loading);
        } else
            glide.load(url)
                    .apply(ImagesUtils.OPTION_DEFAULT)

                    .into(imageView);
    }

    protected void loadImage(int idResource, ImageView imageView) {
        imageView.setImageResource(idResource);
    }
}
