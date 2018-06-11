package com.vn.ntsc.ui.blocklst;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.vn.ntsc.R;
import com.vn.ntsc.repository.model.block.blocklst.BlockLstItem;
import com.vn.ntsc.utils.ImagesUtils;
import com.vn.ntsc.widget.adapter.BaseAdapterListener;
import com.vn.ntsc.widget.adapter.BaseViewHolder;
import com.vn.ntsc.widget.adapter.MultifunctionAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ThoNh on 9/22/2017.
 */

public class BlockListAdapter extends MultifunctionAdapter<BlockListAdapter.ViewHolder, BlockLstItem> {

    private IBlockListEvent listener;

    public BlockListAdapter(IBlockListEvent listener) {
        super(null);
        this.listener = listener;
    }

    @Override
    protected ViewHolder onInjectViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_item_block, parent, false));
    }

    @Override
    protected void onViewReady(final ViewHolder holder, final BlockLstItem item, final int position) {
        Context mContext = holder.getViewRoot().getContext();

        final BlockLstItem response = mData.get(position);

        if (response != null) {

            holder.mName.setText(response.userName);

            holder.mUnBlock.setText(item.isBlocked ? R.string.common_blocked : R.string.common_no_block);
            holder.mUnBlock.setBackground(item.isBlocked ? ContextCompat.getDrawable(mContext, R.drawable.bg_button_red) : ContextCompat.getDrawable(mContext, R.drawable.bg_button_gray));
            holder.mUnBlock.setTextColor(item.isBlocked ? mContext.getResources().getColor(R.color.white) : mContext.getResources().getColor(R.color.textColorSecondary));

            ImagesUtils.loadRoundedAvatar(response.avatarUrl,item.gender, holder.mAvatar);

            holder.mUnBlock.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        if (item.isBlocked) {
                            listener.onUnBlockItemClick(view, response, position);
                        } else {
                            listener.onReBlockItemClick(view, response, position);
                        }
                    }
                }
            });

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        listener.onItemClick(view, response, position);
                    }
                }
            });
        }
    }

    public static class ViewHolder extends BaseViewHolder {

        @BindView(R.id.img_avatar)
        ImageView mAvatar;
        @BindView(R.id.tv_name)
        TextView mName;
        @BindView(R.id.btn_unblock)
        TextView mUnBlock;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public interface IBlockListEvent extends BaseAdapterListener<BlockLstItem> {

        void onItemClick(View view, BlockLstItem item, int position);

        void onUnBlockItemClick(View view, BlockLstItem item, int position);

        void onReBlockItemClick(View view, BlockLstItem item, int position);
    }
}
