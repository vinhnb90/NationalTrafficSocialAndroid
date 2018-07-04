package com.vn.ntsc.ui.comments.helper;

import android.support.annotation.Nullable;
import android.view.View;

import com.vn.ntsc.R;
import com.vn.ntsc.utils.Constants;
import com.vn.ntsc.widget.views.images.RecyclingImageView;

import butterknife.BindView;

public class BaseMediaTimelineHelper extends TimelineContentHelper{

    @Nullable
    @BindView(R.id.item_timeline_image_view)
    RecyclingImageView imageView;
    @Nullable
    @BindView(R.id.item_timeline_play_video)
    RecyclingImageView imagePlayView;

    public BaseMediaTimelineHelper(View itemView, int viewType) {
        super(itemView, viewType);
    }

    protected void displayVideoPlayIcon(String type, View view) {
        if (type.equals(Constants.BUZZ_TYPE_FILE_VIDEO) || type.equals(Constants.BUZZ_TYPE_FILE_AUDIO)) {
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.INVISIBLE);
        }
    }

}
