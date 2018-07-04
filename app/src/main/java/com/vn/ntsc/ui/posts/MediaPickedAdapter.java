package com.vn.ntsc.ui.posts;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.vn.ntsc.R;
import com.vn.ntsc.repository.model.mediafile.MediaFileBean;
import com.vn.ntsc.repository.preferece.UploadSettingPreference;
import com.vn.ntsc.utils.ImagesUtils;
import com.vn.ntsc.widget.adapter.BaseViewHolder;
import com.vn.ntsc.widget.adapter.MultifunctionAdapter;
import com.vn.ntsc.widget.mediafile.MediaFilePickedEventListener;
import com.vn.ntsc.widget.views.images.RecyclingImageView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * The MediaPickedAdapter class setup UI about MediaFile was checked
 * Created by Robert on 2017 Sep 08
 */
public class MediaPickedAdapter extends MultifunctionAdapter<MediaPickedAdapter.MediaPickedViewHolder, MediaFileBean> {

    public MediaPickedAdapter(MediaFilePickedEventListener listener) {
        super(listener);
    }

    @Override
    protected MediaPickedViewHolder onInjectViewHolder(ViewGroup parent, int viewType) {

        return new MediaPickedViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_list_picked_media, parent, false));

    }

    @Override
    protected void onViewReady(MediaPickedViewHolder holder, MediaFileBean item, int position) {
        holder.onBindView(position, item, (MediaFilePickedEventListener) listener);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public int getSize() {
        return (mData != null ? mData.size() : 0);
    }

    /**
     * Fetching the mediaItemNeedRemove in MediaFileBean ArrayList to Remove if Equals
     *
     * @param mediaItemNeedRemove the MediaItem need remove in List
     */
    public void removeItem(MediaFileBean mediaItemNeedRemove) {
        if (mData == null || mData.isEmpty() || mediaItemNeedRemove == null) return;

        //Traverse through all item of array
        for (int id = 0; id < mData.size(); id++) {
            //Remove mediaItemNeedRemove if found in ArrayList
            if (mData.get(id).id == mediaItemNeedRemove.id) {
                mData.remove(id);
                break;
            }
        }
    }

    /**
     * Class MediaPickedViewHolder for Media view type
     */
    static class MediaPickedViewHolder extends BaseViewHolder {
        @BindView(R.id.thumbnail_picked_media)
        RecyclingImageView mThumbnail;
        @BindView(R.id.iv_picked_media_remove)
        RecyclingImageView mBtbRemove;
        @BindView(R.id.video_play_btn)
        RecyclingImageView mPlayVideoBtb;

        public MediaPickedViewHolder(View view) {
            super(view);

            ButterKnife.bind(this, itemView);
            mPlayVideoBtb.setVisibility(View.INVISIBLE);
        }

        protected void onBindView(final int position, final MediaFileBean pickerTile, final MediaFilePickedEventListener listener) {

            if (pickerTile.mediaType == MediaFileBean.AUDIO) {

                mPlayVideoBtb.setVisibility(View.INVISIBLE);
                ImagesUtils.loadImage(UploadSettingPreference.getInstance().getDefaultAudioImg(), mThumbnail);

            } else if (pickerTile.mediaType == MediaFileBean.IMAGE) {

                mPlayVideoBtb.setVisibility(View.INVISIBLE);
                ImagesUtils.loadImageLocal(pickerTile.mediaUri, mThumbnail);

            } else if (pickerTile.mediaType == MediaFileBean.VIDEO) {

                mPlayVideoBtb.setVisibility(View.VISIBLE);
                ImagesUtils.loadImageLocal(Uri.parse(pickerTile.mediaUri).getPath(), mThumbnail);
            }
            mBtbRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((MediaFilePickedEventListener<MediaFileBean>) listener).onUnSelectFile(pickerTile, mBtbRemove, position);
                }
            });

        }
    }

    public MediaFileBean getItem(int position) {
        return (mData != null && !mData.isEmpty() ? mData.get(position) : null);
    }

}
