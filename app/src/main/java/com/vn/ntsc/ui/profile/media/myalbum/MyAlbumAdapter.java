package com.vn.ntsc.ui.profile.media.myalbum;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.vn.ntsc.R;
import com.vn.ntsc.repository.model.myalbum.LoadAlbum.LoadAlbumResponse;
import com.vn.ntsc.utils.ImagesUtils;
import com.vn.ntsc.widget.adapter.BaseAdapterListener;
import com.vn.ntsc.widget.adapter.BaseViewHolder;
import com.vn.ntsc.widget.adapter.MultifunctionAdapter;
import com.vn.ntsc.widget.views.textview.TextViewVectorCompat;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ThoNh on 11/14/2017.
 */

public class MyAlbumAdapter extends MultifunctionAdapter<BaseViewHolder, LoadAlbumResponse.DataBean> {

    private static final int TYPE_CREATE_NEW = 1;
    private static final int TYPE_ALBUM_ITEM = 2;
    private static final int TYPE_ALBUM_UPLOADING = 3;

    public interface MyAlbumListener extends BaseAdapterListener {

        public void onCreateAlbum(View view);

        public void onItemClick(LoadAlbumResponse.DataBean dataBean, int position, View view);

        public void onChangePrivacy(LoadAlbumResponse.DataBean dataBean, int position, View view);
    }

    private final boolean mIsOwn;
    private MyAlbumListener mEventListener;

    public MyAlbumAdapter(boolean isOwn, MyAlbumListener listener) {
        super(listener);
        this.mEventListener = listener;
        this.mIsOwn = isOwn;
        if (mIsOwn)
            mData.add(new LoadAlbumResponse.DataBean());
    }


    public void clearData() {
        if (mData != null) {
            mData.clear();
            if (mIsOwn)
                mData.add(new LoadAlbumResponse.DataBean());
            notifyDataSetChanged();
        }
    }

    @Override
    public int onInjectItemViewType(int position) {
        if (mIsOwn && position == 0)
            return TYPE_CREATE_NEW;

        if (mData.get(position).isUploading) {
            return TYPE_ALBUM_UPLOADING;
        }

        return TYPE_ALBUM_ITEM;
    }


    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    @Override
    protected BaseViewHolder onInjectViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_CREATE_NEW)
            return new ViewCreateNewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_create_new_album, parent, false));

        if (viewType == TYPE_ALBUM_UPLOADING) {
            return new ViewUpLoadingHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_create_new_album_uploading, parent, false));
        }

        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_my_album, parent, false));
    }


    @Override
    protected void onViewReady(final BaseViewHolder holder, LoadAlbumResponse.DataBean item, final int position) {

        int itemViewType = getItemViewType(position);

        if (itemViewType == TYPE_CREATE_NEW) {
            final ViewCreateNewHolder createNewHolder = (ViewCreateNewHolder) holder;
            createNewHolder.mName.setText(R.string.create_new_album);
            createNewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mEventListener != null) {
                        mEventListener.onCreateAlbum(createNewHolder.itemView);
                    }
                }
            });
        } else if (itemViewType == TYPE_ALBUM_UPLOADING) {
            final ViewUpLoadingHolder viewHolder = (ViewUpLoadingHolder) holder;
            final LoadAlbumResponse.DataBean data = getData(position);
            viewHolder.mImageView.setAlpha(0.4f);
            if (data != null) {
                viewHolder.mName.setText(data.albumName);
                if (data.imageList != null)
                    ImagesUtils.loadImage(data.imageList.thumbnailUrl, viewHolder.mImageView);
            }
        } else if (itemViewType == TYPE_ALBUM_ITEM) {
            final ViewHolder viewHolder = (ViewHolder) holder;
            final LoadAlbumResponse.DataBean data = getData(position);
            // Show some more option
            if (mIsOwn) {
                viewHolder.mImagePrivacy.setVisibility(View.VISIBLE);
                viewHolder.mImagePrivacy.setVectorDrawableRight(R.drawable.ic_small_arrow_drop_down);

                // Else hide
            } else {
                viewHolder.mImagePrivacy.setVisibility(View.GONE);
                viewHolder.mImagePrivacy.setVectorDrawableRight(0);
            }

            if (data != null) {
                viewHolder.mName.setText(data.albumName);
                viewHolder.mNumberImage.setText(String.valueOf(data.numberImage));
                viewHolder.mImagePrivacy.setVectorDrawableLeft(data.privacy == 0 ? R.drawable.ic_small_privacy_public_24dp_gray : R.drawable.ic_small_privacy_only_me_24dp_gray);

                if (data.imageList != null)
                    ImagesUtils.loadImage(data.imageList.thumbnailUrl, viewHolder.mImageView);

                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mEventListener != null) {
                            mEventListener.onItemClick(data, position, viewHolder.mName);
                        }
                    }
                });

                viewHolder.mImagePrivacy.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mEventListener != null) {
                            mEventListener.onChangePrivacy(data, position, v);
                        }
                    }
                });
            }
        }
    }

    public static class ViewHolder extends BaseViewHolder {

        @BindView(R.id.imv_my_album)
        ImageView mImageView;
        @BindView(R.id.tv_name_album)
        TextView mName;
        @BindView(R.id.tv_number_image)
        TextView mNumberImage;
        @BindView(R.id.imv_privacy)
        TextViewVectorCompat mImagePrivacy;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public static class ViewCreateNewHolder extends BaseViewHolder {

        @BindView(R.id.tv_name_album)
        TextView mName;

        public ViewCreateNewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public static class ViewUpLoadingHolder extends BaseViewHolder {

        @BindView(R.id.imv_my_album)
        ImageView mImageView;
        @BindView(R.id.tv_name_album)
        TextView mName;
        @BindView(R.id.loading)
        ProgressBar mProgressBar;

        public ViewUpLoadingHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setAlpha(0.5f);
        }
    }
}
