package com.vn.ntsc.ui.profile.media.videoaudio;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vn.ntsc.R;
import com.vn.ntsc.app.AppController;
import com.vn.ntsc.repository.TypeView;
import com.vn.ntsc.repository.model.timeline.datas.sub.ListBuzzChild;
import com.vn.ntsc.repository.preferece.UploadSettingPreference;
import com.vn.ntsc.utils.ImagesUtils;
import com.vn.ntsc.utils.Utils;
import com.vn.ntsc.widget.adapter.BaseAdapterListener;
import com.vn.ntsc.widget.adapter.BaseViewHolder;
import com.vn.ntsc.widget.adapter.MultifunctionAdapter;
import com.vn.ntsc.widget.views.images.RecyclingImageView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ThoNh on 11/20/2017.
 */

public class VideoAudioAdapter extends MultifunctionAdapter<VideoAudioAdapter.ViewHolder, ListBuzzChild> {


    public interface VideoAudioEventListener extends BaseAdapterListener {
        public void onItemClick(List<ListBuzzChild> mData, ListBuzzChild bean, int position, View view);
    }

    private VideoAudioEventListener mListener;

    public VideoAudioAdapter(VideoAudioEventListener listener) {
        super(listener);
        mListener = listener;
    }


    @Override
    protected VideoAudioAdapter.ViewHolder onInjectViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_media_libary, parent, false));
    }

    @Override
    protected void onViewReady(ViewHolder holder, ListBuzzChild item, final int position) {
        final ListBuzzChild bean = getData(position);

        if (bean != null) {
            switch (bean.buzzType) {
                case TypeView.MediaDetailType.VIDEO_TYPE:
                    ImagesUtils.loadImageSimple(bean.thumbnailUrl, holder.mImageView);
                    ImagesUtils.loadImage(R.drawable.ic_video_play_btn,holder.mTypeImage);
                    break;
                case TypeView.MediaDetailType.AUDIO_TYPE:
                    ImagesUtils.loadImage(UploadSettingPreference.getInstance().getDefaultAudioImg(), holder.mImageView);
                    ImagesUtils.loadImage(R.drawable.ic_mic,holder.mTypeImage);
                    break;
            }

            holder.mDuration.setText(Utils.convertSecondToTimeFormat(bean.mDuration));
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                        mListener.onItemClick(mData,bean, position, v);
                }
            }
        });
    }


    public static class ViewHolder extends BaseViewHolder {

        @BindView(R.id.imv_image)
        RecyclingImageView mImageView;

        @BindView(R.id.imv_type_media)
        RecyclingImageView  mTypeImage;
        @BindView(R.id.tv_duration)
        TextView mDuration;
        @BindView(R.id.bottom_layout)
        RelativeLayout mBottomLayout;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            mBottomLayout.setVisibility(View.VISIBLE);
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            layoutParams.height = AppController.SCREEN_WIDTH / 4;
            layoutParams.width = AppController.SCREEN_WIDTH / 4;
        }
    }
}
