package com.vn.ntsc.ui.comments.helper;

import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.vn.ntsc.R;
import com.vn.ntsc.repository.model.timeline.TimelineType;
import com.vn.ntsc.repository.model.timeline.datas.BuzzBean;
import com.vn.ntsc.repository.model.timeline.datas.sub.BuzzSubCommentBean;
import com.vn.ntsc.repository.model.timeline.datas.sub.ListCommentBean;
import com.vn.ntsc.repository.model.timeline.datas.sub.ListTagFriendsBean;
import com.vn.ntsc.repository.preferece.UserPreferences;
import com.vn.ntsc.ui.comments.HeaderCommentListener;
import com.vn.ntsc.utils.Constants;
import com.vn.ntsc.utils.ImagesUtils;
import com.vn.ntsc.utils.Utils;
import com.vn.ntsc.widget.views.timeline.TimelineFooterView;
import com.vn.ntsc.widget.views.timeline.TimelineHeaderView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CommentHeaderHelper {

    private final String TAG = CommentHeaderHelper.class.getSimpleName();

    @TimelineType
    int viewType;
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
    @BindView(R.id.item_timeline_header_description)
    AppCompatTextView description;
    @Nullable
    @BindView(R.id.item_timeline_header_more)
    AppCompatTextView seeMore;
    @Nullable
    @BindView(R.id.item_timeline_approve_buzz)
    View approve;

    protected RequestManager glide;
    protected View itemView;
    protected BuzzBean bean;
    protected HeaderCommentListener listener;

    public CommentHeaderHelper(View itemView, int viewType) {
        this.itemView = itemView;
        ButterKnife.bind(this, itemView);
        this.viewType = viewType;
        this.glide = Glide.with(itemView);
    }

    public void setListener(HeaderCommentListener listener) {
        this.listener = listener;
    }

    public View getItemView() {
        return itemView;
    }

    public BuzzBean getData() {
        return bean;
    }

    public void onBindView(final BuzzBean bean) {
        this.bean = bean;

        if (bean == null)
            return;
        if (approve != null)
            if (bean.isApproved == Constants.IS_NOT_APPROVED)
                approve.setVisibility(View.VISIBLE);
            else
                approve.setVisibility(View.INVISIBLE);

        //Header view
        if (timelineHeaderView != null) {
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
                        listener.onRemoveStatus(bean, view);
                    } else {
                        listener.onFavorite(bean, view);
                    }
                }

                @Override
                public void onDisplayProfileScreen(String userId, View view) {
                    listener.onDisplayProfileScreen(userId, view);
                }

                @Override
                public void onDisplayProfileScreen(BuzzBean bean, View view) {
                    listener.onDisplayProfileScreen(bean, view);
                }

                @Override
                public void onDisplayTagFriendsScreen(ArrayList<ListTagFriendsBean> tagList, View view) {
                    listener.onDisplayTagFriendsScreen(tagList, view);
                }
            });
        }

        //Footer view
        if (timelineFooterView != null) {
            timelineFooterView.setIconLke(0);
            timelineFooterView.setIconComment(0);
            timelineFooterView.setIconShare(0);
            String strLikeNumber = String.format(itemView.getResources().getString(R.string.timeline_like), String.valueOf(bean.like.likeNumber));
            String strCommentNumber = String.format(itemView.getResources().getString(R.string.timeline_comment), String.valueOf(bean.getTotalCommentNumber()));
            String strShareNumber = String.format(itemView.getResources().getString(R.string.timeline_share), String.valueOf(bean.shareNumber));
            timelineFooterView.setLikeNumber(strLikeNumber);
            timelineFooterView.setCommentNumber(strCommentNumber);
            timelineFooterView.setShareNumber(strShareNumber);
        }

        //Description
        if (!Utils.isEmptyOrNull(bean.buzzValue)) {
            description.setVisibility(View.VISIBLE);
            description.setText(bean.buzzValue);
        } else {
            description.setText("");
            description.setVisibility(View.GONE);
            if (seeMore != null)
                seeMore.setVisibility(View.GONE);
        }
    }

    public void increaseShareNumber() {
        ++getData().shareNumber;
        notifyDataSetChanged();
    }

    public void updateFavorite(int isFavorite) {
        getData().isFavorite = isFavorite;
        notifyDataSetChanged();
    }

    public void updateCommentNumber(ListCommentBean bean) {
        getData().commentNumber = bean.commentNumber;
        getData().subCommentNumber = bean.allSubCommentNumber;
    }

    public void updateCommentNumber(BuzzSubCommentBean bean) {
        getData().commentNumber = bean.commentNumber;
        getData().subCommentNumber = bean.allSubCommentNumber;
    }

    public void notifyDataSetChanged() {
        onBindView(getData());
    }

    public void notifyItemSetChanged(BuzzBean bean) {
        this.bean = bean;
        onBindView(getData());
    }

    public void clearReference() {
        bean = null;
        glide = null;
        itemView = null;
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
                    .into(imageView);
    }

    protected void loadImage(String url, ImageView imageView) {
        if (url == null) {
            glide.clear(imageView);
            imageView.setImageResource(R.color.default_image_loading);
        } else
            glide.load(url)
                    .apply(ImagesUtils.OPTION_DEFAULT)
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
