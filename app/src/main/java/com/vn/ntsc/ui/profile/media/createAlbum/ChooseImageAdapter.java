package com.vn.ntsc.ui.profile.media.createAlbum;

import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.vn.ntsc.R;
import com.vn.ntsc.app.AppController;
import com.vn.ntsc.repository.model.mediafile.MediaFileBean;
import com.vn.ntsc.repository.preferece.UploadSettingPreference;
import com.vn.ntsc.utils.ImagesUtils;
import com.vn.ntsc.widget.adapter.BaseAdapterListener;
import com.vn.ntsc.widget.adapter.BaseViewHolder;
import com.vn.ntsc.widget.adapter.MultifunctionAdapter;
import com.vn.ntsc.widget.views.images.RecyclingImageView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ThoNh on 11/15/2017.
 */

public class ChooseImageAdapter extends MultifunctionAdapter<BaseViewHolder, MediaFileBean> {

    private static final int TYPE_CAMERA = 1;
    private static final int TYPE_IMAGE_ITEM = 2;

    public interface ChooseImageEventListener extends BaseAdapterListener<MediaFileBean> {
        void onChoose(MediaFileBean bean, int position, View view);

        void onUnChoose(MediaFileBean bean, int position, View view);

        void onOutOfSizeAlbum();

        void onOpenCamera();
    }


    private ChooseImageEventListener mEventListener;

    /*Check if numberChecked > fileSizeValid*/
    private boolean isContinued = true;


    public ChooseImageAdapter(ChooseImageEventListener listener) {
        super(listener);
        mEventListener = listener;
    }

    @Override
    public void setNewData(@Nullable List<MediaFileBean> data) {

        if (data != null) {
            data.add(0, null);
        }
        super.setNewData(data);
    }

    public void unSelected(MediaFileBean bean) {
        int position = getData().indexOf(bean);
        if (position != -1) {
            bean.isCheck = !bean.isCheck;
            notifyItemChanged(position);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? TYPE_CAMERA : TYPE_IMAGE_ITEM;
    }

    @Override
    protected BaseViewHolder onInjectViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_CAMERA:
                return new ViewCameraHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_camera, parent, false));
            case TYPE_IMAGE_ITEM:
                return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_picker_grid_item, parent, false));
            default:
                return null;
        }
    }

    @Override
    protected void onViewReady(BaseViewHolder holder, MediaFileBean item, final int position) {

        int itemViewType = getItemViewType(position);
        final MediaFileBean bean = getData(position);

        switch (itemViewType) {

            case TYPE_CAMERA:
                ViewCameraHolder cameraHolder = (ViewCameraHolder) holder;
                cameraHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mEventListener != null) {
                            mEventListener.onOpenCamera();
                        }
                    }
                });
                break;

            case TYPE_IMAGE_ITEM:
                ViewHolder viewHolder = (ViewHolder) holder;
                ImagesUtils.loadImageSimple(bean.mediaUri, viewHolder.mImageView);
                viewHolder.mCheckBox.setChecked(bean.isCheck);
                viewHolder.mCheckBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (mEventListener != null) {

                            bean.isCheck = !bean.isCheck;

                            if (bean.isCheck) {

                                if (!isContinued) {
                                    mEventListener.onOutOfSizeAlbum();
                                    bean.isCheck = !bean.isCheck;
                                    return;
                                }

                                mEventListener.onChoose(bean, position, v);
                                notifyItemChanged(position);
                            } else {
                                mEventListener.onUnChoose(bean, position, v);
                                notifyItemChanged(position);
                            }

                            isContinued = getNumberItemChecked() < UploadSettingPreference.getInstance().getMaxImageNumber();

                        }
                    }
                });
                break;
        }
    }


    private int getNumberItemChecked() {
        int count = 0;
        if (mData != null)
            for (MediaFileBean entity : mData) {
                if (entity != null && entity.isCheck) {
                    count++;
                }
            }
        return count;
    }

    public class ViewHolder extends BaseViewHolder {

        @BindView(R.id.iv_check_box)
        CheckBox mCheckBox;

        @BindView(R.id.video_play_btn)
        RecyclingImageView mBtnPlay;

        @BindView(R.id.iv_thumbnail)
        RecyclingImageView mImageView;

        @BindView(R.id.tv_time)
        TextView tvTime;


        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            mBtnPlay.setVisibility(View.GONE);
            tvTime.setVisibility(View.GONE);
            mCheckBox.setClickable(true);

            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            layoutParams.height = AppController.SCREEN_WIDTH / 4;
            layoutParams.width = AppController.SCREEN_WIDTH / 4;
        }
    }


    public class ViewCameraHolder extends BaseViewHolder {
        @BindView(R.id.item_camera_iv_custom)
        RecyclingImageView mTedRecyclingImageView;

        public ViewCameraHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
