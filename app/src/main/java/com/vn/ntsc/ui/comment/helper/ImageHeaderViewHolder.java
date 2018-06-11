package com.vn.ntsc.ui.comment.helper;

import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.vn.ntsc.R;
import com.vn.ntsc.app.AppController;
import com.vn.ntsc.repository.model.timeline.TimelineType;
import com.vn.ntsc.repository.model.timeline.datas.BuzzBean;
import com.vn.ntsc.ui.comment.CommentActivity;
import com.vn.ntsc.ui.mediadetail.timeline.TimelineMediaActivity;
import com.vn.ntsc.utils.Constants;
import com.vn.ntsc.utils.ImagesUtils;
import com.vn.ntsc.widget.views.images.RecyclingImageView;

import butterknife.BindView;

/**
 * Created by nankai on 10/25/2017.
 */

public class ImageHeaderViewHolder extends HeaderViewHolder {

    @Nullable
    @BindView(R.id.item_timeline_image_view)
    RecyclingImageView imageView;
    @Nullable
    @BindView(R.id.item_timeline_image_view_2)
    RecyclingImageView imageView2;
    @Nullable
    @BindView(R.id.item_timeline_image_view_3)
    RecyclingImageView imageView3;
    @Nullable
    @BindView(R.id.item_timeline_image_view_4)
    RecyclingImageView imageView4;
    @Nullable
    @BindView(R.id.item_timeline_image_view_5)
    RecyclingImageView imageView5;

    @Nullable
    @BindView(R.id.item_timeline_play_video)
    ImageView imgPlayVideo;
    @Nullable
    @BindView(R.id.item_timeline_play_video_2)
    ImageView imgPlayVideo2;
    @Nullable
    @BindView(R.id.item_timeline_play_video_3)
    ImageView imgPlayVideo3;
    @Nullable
    @BindView(R.id.item_timeline_play_video_4)
    ImageView imgPlayVideo4;
    @Nullable
    @BindView(R.id.item_timeline_play_video_5)
    ImageView imgPlayVideo5;

    @Nullable
    @BindView(R.id.layout_content)
    ConstraintLayout layoutContent;

    @Nullable
    @BindView(R.id.item_timeline_text_view_more)
    TextView more;

    private String mActivityComeFrom;

    public ImageHeaderViewHolder(int typeHeader, View itemView, int typeView) {
        super(typeHeader, itemView, typeView);
        if (layoutContent.getTag() == null) {
            ViewGroup.LayoutParams layoutParams = layoutContent.getLayoutParams();
            layoutParams.height = AppController.SCREEN_WIDTH;
            layoutParams.width = AppController.SCREEN_WIDTH;
            layoutContent.setTag(this);
        } else {
            layoutContent = (ConstraintLayout) layoutContent.getTag();
        }

        itemView.setTag(this);
    }

    @Override
    public ImageHeaderViewHolder onBindView(CommentActivity activity, final BuzzBean bean) {
        super.onBindView(activity, bean);

       @TimelineType int type = activity.getTypeView(bean);
        if (type == TimelineType.BUZZ_TYPE_MULTI_BUZZ_VIDEO_1
                || type == TimelineType.BUZZ_TYPE_MULTI_BUZZ_AUDIO
                || type == TimelineType.BUZZ_TYPE_MULTI_BUZZ_IMAGE_1) {
            display1Media(bean);
        } else if (type == TimelineType.BUZZ_TYPE_MULTI_BUZZ_2) {
            display2Media(bean);
        } else if (type == TimelineType.BUZZ_TYPE_MULTI_BUZZ_3) {
            display3Media(bean);
        } else if (type == TimelineType.BUZZ_TYPE_MULTI_BUZZ_4) {
            display4Media(bean);
        } else if (type == TimelineType.BUZZ_TYPE_MULTI_BUZZ_5) {
            display5Media(bean);
        } else if (type == TimelineType.BUZZ_TYPE_MULTI_BUZZ_MORE) {
            display5Media(bean);
            more.setText((bean.childNumber - 5) + "+");
        } else {
            display1Media(bean);
        }

        return this;
    }

    private void gotoViewDetail(BuzzBean bean, View view, int firstIndex) {
        TimelineMediaActivity.launch(((AppCompatActivity) view.getContext()), view, bean, firstIndex, CommentActivity.class.getSimpleName());
    }

    private void display1Media(final BuzzBean bean) {
        displayVideoPlayIcon(bean.listChildBuzzes.get(0).buzzType, imgPlayVideo);

        if (bean.listChildBuzzes.get(0).isApp == Constants.IS_APPROVED)
            ImagesUtils.loadImage(bean.listChildBuzzes.get(0).buzzVal, imageView);
        else
            ImagesUtils.loadImageBlur(bean.listChildBuzzes.get(0).buzzVal, imageView);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mActivityComeFrom == null || !mActivityComeFrom.equals(CommentActivity.EXTRA_FROM_TIMELINE_MEDIA_DETAIL)) {
                    gotoViewDetail(bean, v, 0);
                }
            }
        });
    }

    private void display2Media(final BuzzBean bean) {
        display1Media(bean);

        displayVideoPlayIcon(bean.listChildBuzzes.get(1).buzzType, imgPlayVideo2);
        if (bean.listChildBuzzes.get(1).isApp == Constants.IS_APPROVED)
            ImagesUtils.loadImage(bean.listChildBuzzes.get(1).buzzVal, imageView2);
        else
            ImagesUtils.loadImageBlur(bean.listChildBuzzes.get(1).buzzVal, imageView2);

        imageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoViewDetail(bean, v, 1);
            }
        });
    }

    private void display3Media(final BuzzBean bean) {
        display2Media(bean);

        displayVideoPlayIcon(bean.listChildBuzzes.get(2).buzzType, imgPlayVideo3);
        if (bean.listChildBuzzes.get(2).isApp == Constants.IS_APPROVED)
            ImagesUtils.loadImage(bean.listChildBuzzes.get(2).buzzVal, imageView3);
        else
            ImagesUtils.loadImageBlur(bean.listChildBuzzes.get(2).buzzVal, imageView3);

        imageView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoViewDetail(bean, v, 2);
            }
        });
    }

    private void display4Media(final BuzzBean bean) {
        display3Media(bean);

        displayVideoPlayIcon(bean.listChildBuzzes.get(3).buzzType, imgPlayVideo4);
        if (bean.listChildBuzzes.get(3).isApp == Constants.IS_APPROVED)
            ImagesUtils.loadImage(bean.listChildBuzzes.get(3).buzzVal, imageView4);
        else
            ImagesUtils.loadImageBlur(bean.listChildBuzzes.get(3).buzzVal, imageView4);

        imageView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoViewDetail(bean, v, 3);
            }
        });
    }


    private void display5Media(final BuzzBean bean) {
        display4Media(bean);

        displayVideoPlayIcon(bean.listChildBuzzes.get(4).buzzType, imgPlayVideo5);
        if (bean.listChildBuzzes.get(4).isApp == Constants.IS_APPROVED)
            ImagesUtils.loadImage(bean.listChildBuzzes.get(4).buzzVal, imageView5);
        else
            ImagesUtils.loadImageBlur(bean.listChildBuzzes.get(4).buzzVal, imageView5);

        imageView5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoViewDetail(bean, v, 4);
            }
        });
    }

    public void displayVideoPlayIcon(String type, ImageView playVideo) {
        if (type.equals(Constants.BUZZ_TYPE_FILE_VIDEO)
                || type.equals(Constants.BUZZ_TYPE_FILE_AUDIO)) {
            playVideo.setVisibility(View.VISIBLE);
        } else {
            playVideo.setVisibility(View.GONE);
        }
    }

    public void setActivityComeFrom(String activityName) {
        this.mActivityComeFrom = activityName;
    }
}