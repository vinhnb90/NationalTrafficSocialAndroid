package com.vn.ntsc.ui.posts;

import android.net.Uri;
import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nankai.designlayout.widget.CustomCheckBox;
import com.vn.ntsc.R;
import com.vn.ntsc.core.model.BaseBean;
import com.vn.ntsc.repository.model.mediafile.MediaFileBean;
import com.vn.ntsc.repository.preferece.UploadSettingPreference;
import com.vn.ntsc.utils.ImagesUtils;
import com.vn.ntsc.widget.adapter.BaseViewHolder;
import com.vn.ntsc.widget.adapter.MultifunctionAdapter;
import com.vn.ntsc.widget.views.images.SquareImageView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * The PostStatus Adapter
 * Created by Robert on 2017 Sep 05
 */
public class GalleryAdapter extends MultifunctionAdapter<GalleryAdapter.ViewHolder, MediaFileBean> {
    private static final String TAG = GalleryAdapter.class.getSimpleName();

    public GalleryAdapter(PostStatusEventListener listener) {
        super( listener);
    }

    @Override
    protected ViewHolder onInjectViewHolder(ViewGroup parent, int viewType) {
        return new MediaViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_picker_grid_item, parent, false));
    }

    @Override
    protected void onViewReady(ViewHolder holder, MediaFileBean item, int position) {
        holder.onBindView(position, item, (PostStatusEventListener) listener);
    }

    @Override
    public int getItemViewType(int position) {
        return mData.get(position).mediaType;
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    /**
     * Reorder index order of all MediaFileBean was checked when there is an element unchecked from the list and notify data set changed immediate
     *
     * @param position
     * @param mediaItemUnchecked the current MediaFileBean has been unchecked
     * @author Created by Robert on 2017 Sep 08
     */
    public void reorderUnchecked(int position, MediaFileBean mediaItemUnchecked) {
        if (mData == null || mData.isEmpty() || mediaItemUnchecked == null || position >= mData.size())
            return;

        //Traverse through all item of array
        for (int id = 0; id < mData.size(); id++) {
            MediaFileBean item = mData.get(id);
            //No process If mOrder of item unchecked or mOrder less than mOrder of MediaFileBean current has been unchecked
            if (id == position || item.mOrder == 0 || item.mOrder <= mediaItemUnchecked.mOrder)
                continue;
            item.mOrder--;
            notifyItemChanged(item.pickedPosition);
        }

        mediaItemUnchecked.mOrder = 0;//Reset to zero when unchecked
        notifyItemChanged(mediaItemUnchecked.pickedPosition);
    }

    /**
     * Increase index order of item
     *
     * @param position the index of item necessary change order value in ArrayList
     */
    public void increaseOrderOfItem(int position, int newOrderValue) {
        if (mData == null || mData.isEmpty() || position >= mData.size()) return;
        MediaFileBean currMedia = mData.get(position);
        if (currMedia != null) {
            currMedia.mOrder = newOrderValue;
            currMedia.isCheck = true;
        }
    }

    /**
     * Class BaseMediaTimelineViewHolder for Media view type
     */
    static class MediaViewHolder extends ViewHolder {

        @BindView(R.id.tv_time)
        TextView tvTime;

        public MediaViewHolder(View view) {
            super(view);
            tvTime.setVisibility(View.GONE);
        }

        @Override
        protected void onBindView(int position, MediaFileBean bean, PostStatusEventListener listener) {
            super.onBindView(position, bean, listener);

            if (bean.mediaType == MediaFileBean.AUDIO) {
                tvTime.setVisibility(View.VISIBLE);
                tvTime.setText(bean.duration);
                mPlayVideoBtb.setVisibility(View.INVISIBLE);
                ImagesUtils.loadImageForceFirstFrameGif(UploadSettingPreference.getInstance().getDefaultAudioImg(), mThumbnail);

            } else if (bean.mediaType == MediaFileBean.IMAGE) {
                tvTime.setVisibility(View.INVISIBLE);
                mPlayVideoBtb.setVisibility(View.INVISIBLE);
                ImagesUtils.loadImageLocal(bean.mediaUri, mThumbnail);

            } else if (bean.mediaType == MediaFileBean.VIDEO) {
                tvTime.setVisibility(View.VISIBLE);
                tvTime.setText(bean.duration);
                mPlayVideoBtb.setVisibility(View.VISIBLE);
                ImagesUtils.loadImageLocal(Uri.parse(bean.mediaUri).getPath(), mThumbnail);
            }
        }
    }

    /**
     * Class BaseTimelineViewHolder common for all View type
     */
    static class ViewHolder extends BaseViewHolder {

        @BindView(R.id.media_layout)
        ConstraintLayout mRootFrameLayout;
        @BindView(R.id.iv_thumbnail)
        SquareImageView mThumbnail;
        @BindView(R.id.video_play_btn)
        ImageView mPlayVideoBtb;
        @BindView(R.id.iv_check_box)
        CustomCheckBox mCheckBox;

        private ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mPlayVideoBtb.setVisibility(View.GONE);
        }

        protected void onBindView(final int position, final MediaFileBean bean, final PostStatusEventListener listener) {
            bean.pickedPosition = position;

            mCheckBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((PostStatusEventListener<BaseBean>) listener).onCheckBoxClick(bean, mCheckBox, position);
                }
            });

            mRootFrameLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((PostStatusEventListener<MediaFileBean>) listener).onItemClick(bean, mRootFrameLayout, position);
                }
            });

            if (bean.mOrder > 0) {
                mPlayVideoBtb.setVisibility(View.VISIBLE);
            } else {
                mPlayVideoBtb.setVisibility(View.GONE);
            }
            mCheckBox.setStatus(bean.isCheck);
        }
    }


}
