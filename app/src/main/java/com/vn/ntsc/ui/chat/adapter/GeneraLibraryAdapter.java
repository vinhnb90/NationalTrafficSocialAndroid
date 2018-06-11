package com.vn.ntsc.ui.chat.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.vn.ntsc.R;
import com.vn.ntsc.repository.TypeView;
import com.vn.ntsc.repository.model.chat.ItemFileChat;
import com.vn.ntsc.repository.preferece.UploadSettingPreference;
import com.vn.ntsc.utils.ImagesUtils;
import com.vn.ntsc.utils.Utils;
import com.vn.ntsc.widget.adapter.BaseAdapterListener;
import com.vn.ntsc.widget.adapter.BaseViewHolder;
import com.vn.ntsc.widget.adapter.MultifunctionAdapter;
import com.vn.ntsc.widget.views.images.SquareImageView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Doremon on 2/28/2018.
 */

public class GeneraLibraryAdapter extends MultifunctionAdapter<GeneraLibraryAdapter.ViewHolder, ItemFileChat> {

    private GeneraLibraryEventListener mGeneraLibraryEventListener;

    public GeneraLibraryAdapter(GeneraLibraryEventListener listener) {
        super(listener);
        mGeneraLibraryEventListener = listener;
    }

    @Override
    protected ViewHolder onInjectViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_genera_lib, parent, false));
    }

    @Override
    protected void onViewReady(ViewHolder holder, ItemFileChat item, final int position) {
        final ItemFileChat itemFileChat = getData(position);
        if (itemFileChat != null) {
            switch (itemFileChat.type) {
                case TypeView.MediaDetailType.IMAGE_TYPE:
                    ImagesUtils.loadImageSimple(itemFileChat.thumbnailUrl, holder.imvImage);
                    holder.bottomLayout.setVisibility(View.GONE);
                    break;

                case TypeView.MediaDetailType.VIDEO_TYPE:
                    holder.bottomLayout.setVisibility(View.VISIBLE);
                    ImagesUtils.loadImageSimple(itemFileChat.thumbnailUrl, holder.imvImage);
                    Glide.with(holder.itemView.getContext()).load(R.drawable.ic_video_play_btn).into(holder.imvTypeMedia);
                    holder.tvDuration.setText(Utils.convertSecondToTimeFormat(itemFileChat.fileDuration));
                    break;

                case TypeView.MediaDetailType.AUDIO_TYPE:
                    holder.bottomLayout.setVisibility(View.VISIBLE);
                    ImagesUtils.loadImage(UploadSettingPreference.getInstance().getDefaultAudioImg(), holder.imvImage);
                    Glide.with(holder.itemView.getContext()).load(R.drawable.ic_mic).into(holder.imvTypeMedia);
                    holder.tvDuration.setText(Utils.convertSecondToTimeFormat(itemFileChat.fileDuration));
                    break;
            }
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mGeneraLibraryEventListener != null) {
                    mGeneraLibraryEventListener.onItemClick(mData, itemFileChat, position, v);
                }
            }
        });
    }

    public class ViewHolder extends BaseViewHolder {
        @BindView(R.id.imv_image)
        SquareImageView imvImage;
        @BindView(R.id.btn_play)
        SquareImageView btnPlay;
        @BindView(R.id.imv_type_media)
        ImageView imvTypeMedia;
        @BindView(R.id.tv_duration)
        TextView tvDuration;
        @BindView(R.id.bottom_layout)
        RelativeLayout bottomLayout;


        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            bottomLayout.setVisibility(View.VISIBLE);
        }
    }

    public interface GeneraLibraryEventListener extends BaseAdapterListener {
        void onItemClick(List<ItemFileChat> mData, ItemFileChat bean, int position, View view);
    }
}
