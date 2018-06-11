package com.vn.ntsc.ui.profile.media.createAlbum;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.vn.ntsc.R;
import com.vn.ntsc.repository.model.mediafile.MediaFileBean;
import com.vn.ntsc.utils.ImagesUtils;
import com.vn.ntsc.widget.adapter.BaseAdapterListener;
import com.vn.ntsc.widget.adapter.BaseViewHolder;
import com.vn.ntsc.widget.adapter.MultifunctionAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ThoNh on 11/15/2017.
 */

public class SelectedImageAdapter extends MultifunctionAdapter<SelectedImageAdapter.ViewHolder, MediaFileBean> {

    public interface SelectedImageEventListener extends BaseAdapterListener {
        void onDeselect(MediaFileBean image, int position, View view);
    }


    private SelectedImageEventListener mEventListener;

    public SelectedImageAdapter(SelectedImageEventListener listener) {
        super(listener);
        mEventListener = listener;
    }

    @Override
    protected SelectedImageAdapter.ViewHolder onInjectViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_list_picked_media, parent, false));
    }

    @Override
    protected void onViewReady(SelectedImageAdapter.ViewHolder holder, MediaFileBean item, final int position) {

        final MediaFileBean bean = getData(position);
        ImagesUtils.loadImageSimple(bean.mediaUri,holder.mImageView);
        holder.mRemoveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEventListener != null) {
                    mEventListener.onDeselect(bean, position, v);
                }
            }
        });
    }

    public void remove(MediaFileBean bean) {
        int position = getData().indexOf(bean);
        if (position != -1){
            getData().remove(bean);
            notifyItemRemoved(position);
        }
    }

    public static class ViewHolder extends BaseViewHolder {
        @BindView(R.id.thumbnail_picked_media)
        ImageView mImageView;

        @BindView(R.id.iv_picked_media_remove)
        ImageView mRemoveButton;

        @BindView(R.id.video_play_btn)
        ImageView mBtnPlay; // un use

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            mBtnPlay.setVisibility(View.GONE);
        }
    }
}
