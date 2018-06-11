package com.vn.ntsc.ui.profile.media.timeline;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.vn.ntsc.R;
import com.vn.ntsc.repository.TypeView;
import com.vn.ntsc.repository.model.timeline.datas.sub.ListBuzzChild;
import com.vn.ntsc.repository.preferece.UploadSettingPreference;
import com.vn.ntsc.utils.ImagesUtils;
import com.vn.ntsc.widget.adapter.BaseAdapterListener;
import com.vn.ntsc.widget.adapter.BaseViewHolder;
import com.vn.ntsc.widget.adapter.MultifunctionAdapter;
import com.vn.ntsc.widget.views.images.SquareImageView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ThoNh on 11/21/2017.
 */

public class MediaTimelineAdapter extends MultifunctionAdapter<MediaTimelineAdapter.ViewHolder, ListBuzzChild> {

    public interface MediaTimelineEventListener extends BaseAdapterListener {
        void onItemClick(ListBuzzChild bean, int position, View view);
    }

    private MediaTimelineEventListener mListener;

    public MediaTimelineAdapter(MediaTimelineEventListener listener) {
        super(listener);
        this.mListener = listener;
    }

    @Override
    protected MediaTimelineAdapter.ViewHolder onInjectViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_video_audio, parent, false));
    }

    @Override
    protected void onViewReady(final MediaTimelineAdapter.ViewHolder holder, final ListBuzzChild item, final int position) {
        switch (item.buzzType) {
            case TypeView.MediaDetailType.VIDEO_TYPE:
                holder.mBtnPlay.setVisibility(View.VISIBLE);
                ImagesUtils.loadImageSimple(item.thumbnailUrl, holder.mImageView);
                break;

            case TypeView.MediaDetailType.IMAGE_TYPE:
                holder.mBtnPlay.setVisibility(View.GONE);
                ImagesUtils.loadImageSimple(item.thumbnailUrl, holder.mImageView);
                break;

            case TypeView.MediaDetailType.AUDIO_TYPE:
                holder.mBtnPlay.setVisibility(View.GONE);
                ImagesUtils.loadImage(UploadSettingPreference.getInstance().getDefaultAudioImg(), holder.mImageView);
                break;
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onItemClick(item, position, holder.itemView);
                }
            }
        });

    }

    public class ViewHolder extends BaseViewHolder {
        @BindView(R.id.imv_image)
        SquareImageView mImageView;

        @BindView(R.id.btn_play)
        ImageView mBtnPlay;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}