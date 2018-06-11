package com.vn.ntsc.ui.comment.helper;

import android.content.DialogInterface;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nankai.designlayout.dialog.DialogMaterial;
import com.nankai.designlayout.dialog.enums.Style;
import com.vn.ntsc.R;
import com.vn.ntsc.app.AppController;
import com.vn.ntsc.repository.model.timeline.datas.BuzzBean;
import com.vn.ntsc.services.UserLiveStreamService;
import com.vn.ntsc.ui.comment.CommentActivity;
import com.vn.ntsc.ui.mediadetail.timeline.TimelineMediaActivity;
import com.vn.ntsc.utils.Constants;
import com.vn.ntsc.utils.ImagesUtils;
import com.vn.ntsc.widget.views.images.RecyclingImageView;

import butterknife.BindView;

/**
 * Created by nankai on 10/25/2017.
 */

public class LiveStreamHeaderViewHolder extends HeaderViewHolder {

    @Nullable
    @BindView(R.id.item_timeline_image_view)
    RecyclingImageView imageView;

    @Nullable
    @BindView(R.id.item_timeline_play_video)
    ImageView imgPlayVideo;

    @Nullable
    @BindView(R.id.layout_content)
    ConstraintLayout layoutContent;
    @BindView(R.id.title_time)
    TextView titleTime;
    @Nullable
    @BindView(R.id.view)
    TextView viewCurrent;
    @Nullable
    @BindView(R.id.layout_timeline_live_stream)
    RelativeLayout layoutLiveStream;

    public LiveStreamHeaderViewHolder(int typeHeader, View itemView, int typeView) {
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
    public LiveStreamHeaderViewHolder onBindView(final CommentActivity activity, final BuzzBean bean) {
        super.onBindView(activity, bean);

        viewNumber.setVisibility(View.VISIBLE);
        viewNumber.setText(String.format(imageView.getResources().getString(R.string.timeline_time_view),bean.listChildBuzzes.get(0).viewNumber));

        if (bean.listChildBuzzes.get(0).streamStatus.equals(Constants.LIVE_STREAM_ON)) {
            layoutLiveStream.setVisibility(View.VISIBLE);

            int seconds = Integer.parseInt(bean.listChildBuzzes.get(0).streamDuration);
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

            viewCurrent.setText(bean.listChildBuzzes.get(0).currentViewNumber);

        } else {
            layoutLiveStream.setVisibility(View.GONE);
        }

        if (bean.isApproved == Constants.IS_APPROVED) {
            layoutContent.setAlpha(1f);
        } else {
            layoutContent.setAlpha(0.2f);
        }

        imgPlayVideo.setVisibility(View.VISIBLE);
        ImagesUtils.loadImageLiveStreamFillWidth(bean.listChildBuzzes.get(0).thumbnailUrl, imageView);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (bean.listChildBuzzes.get(0).isApp == Constants.IS_APPROVED) {
                    if (bean.listChildBuzzes.get(0).streamStatus.equals(Constants.LIVE_STREAM_ON))
                        activity.onLiveStreamOption(UserLiveStreamService.Mode.VIEW,
                                bean.buzzId, bean.listChildBuzzes.get(0).streamId,
                                bean.listChildBuzzes.get(0).thumbnailUrl);
                    else {
                        TimelineMediaActivity.launch(activity, v, bean.listChildBuzzes, 0);
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
}