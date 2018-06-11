package com.vn.ntsc.ui.profile.media.albumDetail;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nankai.designlayout.widget.CustomCheckBox;
import com.vn.ntsc.R;
import com.vn.ntsc.repository.model.myalbum.ItemImageInAlbum;
import com.vn.ntsc.utils.ImagesUtils;
import com.vn.ntsc.widget.adapter.BaseAdapterListener;
import com.vn.ntsc.widget.adapter.BaseViewHolder;
import com.vn.ntsc.widget.adapter.MultifunctionAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ThoNh on 11/15/2017.
 */

public class MyAlbumDetailAdapter extends MultifunctionAdapter<BaseViewHolder, ItemImageInAlbum> {

    private static final int TYPE_ADD_NEW = 1;
    private static final int TYPE_ITEM = 2;


    public interface MyAlbumDetailEventListener extends BaseAdapterListener {

        public void onItemClick(ItemImageInAlbum bean, int position, View view);

        public void onAddImage();

        public void onShowPickerDelete();

    }

    private MyAlbumDetailEventListener mEventListener;
    private boolean isShowingPickerDelete = false;
    private boolean mIsOwn = false;


    public MyAlbumDetailAdapter(boolean isOwn, MyAlbumDetailEventListener listener) {
        super(listener);
        mEventListener = listener;
        this.mIsOwn = isOwn;
    }


    public void clearData() {
        if (mData != null) {
            mData.clear();
            addHeaderData(mIsOwn);
            notifyDataSetChanged();
        }
    }

    /**
     * If This Album isOwn, add First Item is : "Add Image"
     * Check method {@see MyAlbumDetail.onViewReady} check data==null --> add modify layout first item
     */
    public void addHeaderData(boolean isOwn) {
        if (mData != null && isOwn)
            mData.add(null);
    }

    @NonNull
    @Override
    public List<ItemImageInAlbum> getData() {
        if (!mIsOwn) {
            return super.getData();
        } else {
            List<ItemImageInAlbum> inAlbums = new ArrayList<>();
            inAlbums.addAll(mData);
            inAlbums.remove(0);
            return inAlbums;          // if isOwn, first Item is: "add_image" --> don't return it
        }
    }

    @Override
    public int onInjectItemViewType(int position) {

        if (mIsOwn && position == 0) {
            return TYPE_ADD_NEW;
        }

        return TYPE_ITEM;
    }

    @Override
    protected BaseViewHolder onInjectViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ADD_NEW) {
            return new ViewAddNewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_item_my_album_detail_add_image, parent, false));
        }

        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_item_my_album_detail, parent, false));
    }

    @Override
    protected void onViewReady(BaseViewHolder holder, ItemImageInAlbum item, int position) {

        ItemImageInAlbum image = getData(position);

        int itemViewType = getItemViewType(position);

        switch (itemViewType) {
            case TYPE_ADD_NEW:

                ((ViewAddNewHolder) holder).itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mEventListener != null) {
                            mEventListener.onAddImage();
                        }
                    }
                });
                break;
            case TYPE_ITEM:
                setEventForCheckBox((ViewHolder) holder, image, position);
                setEventForImage((ViewHolder) holder, image, position);
                if (mIsOwn)
                    holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            showPickerDelete();
                            if (mEventListener != null) {
                                mEventListener.onShowPickerDelete();
                            }
                            return false;
                        }
                    });
                break;
        }
    }

    private void setEventForImage(ViewHolder holder, final ItemImageInAlbum image, final int position) {
        holder.mImageView.setVisibility(View.VISIBLE);
        holder.tvAddImage.setVisibility(View.GONE);
        ImagesUtils.loadImageSimple(image.thumbnailUrl, holder.mImageView);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEventListener != null) {
                    mEventListener.onItemClick(image, position, v);
                }
            }
        });

    }

    private void setEventForCheckBox(ViewHolder holder, final ItemImageInAlbum image, final int position) {
        if (position == 0) holder.mCheckBox.setVisibility(View.GONE);
        holder.mCheckBox.setVisibility(isShowingPickerDelete ? View.VISIBLE : View.GONE);
        holder.mCheckBox.setStatus(image.isSelected);
        holder.mCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                image.isSelected = !image.isSelected;
                notifyItemChanged(position);
            }
        });
    }

    /**
     * Hide checkbox select
     * Set default selected = false for all item
     */
    public void hidePickerDelete() {

        if (isShowingPickerDelete) {
            isShowingPickerDelete = false;

            // remove selected
            // begin from 1 because : first item is "add image"
            for (int i = 1; i < mData.size()-1; i++) {

                if (getData().get(i).isSelected) {
                    getData().get(i).isSelected = false;
                }
            }

            notifyDataSetChanged();
        }
    }

    private void showPickerDelete() {
        if (!isShowingPickerDelete) {
            isShowingPickerDelete = true;
            notifyDataSetChanged();
        }
    }

    public boolean isShowingPickerDelete() {
        return isShowingPickerDelete;
    }

    /**
     * get all image is selected for delete
     *
     * @return list image selected
     */
    public List<ItemImageInAlbum> getImagesSelected() {
        List<ItemImageInAlbum> data = new ArrayList<>();


        // first postion is "add image"
        for (int i = 0; i < mData.size(); i++) {
            if (mData.get(i) != null)
                if (mData.get(i).isSelected) {
                    data.add(mData.get(i));
                }
        }

        return data;
    }

    public List<String> getImagesIdSelected(List<ItemImageInAlbum> images) {
        List<String> data = new ArrayList<>();

        // first postion is "add image"
        for (int i = 0; i < images.size(); i++) {
            if (images.get(i) != null)
                data.add(images.get(i).fileId);
        }

        return data;
    }

    /**
     * Remove all item same inside mData
     *
     * @param images
     */
    public void removeItem(List<ItemImageInAlbum> images) {
        mData.removeAll(images);
        notifyDataSetChanged();
    }


    public class ViewHolder extends BaseViewHolder {
        @BindView(R.id.thumbnail_picked_media)
        ImageView mImageView;

        @BindView(R.id.tv_add_image)
        TextView tvAddImage;

        @BindView(R.id.iv_check_box)
        CustomCheckBox mCheckBox;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            tvAddImage.setVisibility(View.GONE);
            mCheckBox.setVisibility(View.GONE);
        }
    }

    public class ViewAddNewHolder extends BaseViewHolder {

        public ViewAddNewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

        }
    }
}
