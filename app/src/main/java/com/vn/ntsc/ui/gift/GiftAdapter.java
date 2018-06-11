package com.vn.ntsc.ui.gift;

import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.vn.ntsc.R;
import com.vn.ntsc.repository.model.gift.Gift;
import com.vn.ntsc.utils.ImagesUtils;
import com.vn.ntsc.widget.adapter.BaseAdapterListener;
import com.vn.ntsc.widget.adapter.BaseViewHolder;
import com.vn.ntsc.widget.adapter.MultifunctionAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by TuanPC on 10/26/2017.
 */
public class GiftAdapter extends MultifunctionAdapter<GiftAdapter.ViewHolder, Gift> {

    private IOnItemClickListener onClickListener;

    public GiftAdapter(IOnItemClickListener listener) {
        super(listener);
        this.onClickListener = listener;
    }

    @Override
    protected void onViewReady(ViewHolder holder, final Gift item, final int position) {
        Point size = new Point();
        WindowManager windowManager = (WindowManager) holder.getViewRoot().getContext()
                .getSystemService(Context.WINDOW_SERVICE);
        if (windowManager != null) {
            Display display = windowManager.getDefaultDisplay();
            display.getSize(size);
            int mMargin = 15;
            int columnSize = (size.x - mMargin * 7) / 3 - 60;
            holder.tvGiftName.setText(item.gift_name);
            holder.imgGift.setMinimumWidth(columnSize);
            holder.imgGift.setMinimumHeight(columnSize);
            ImagesUtils.loadImage(item.gift_url, holder.imgGift);
            holder.imgGift.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickListener.onItemClick(item, position);
                }
            });
        }
    }

    @Override
    protected ViewHolder onInjectViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_choose_gift_to_send, parent, false));
    }

    public class ViewHolder extends BaseViewHolder {
        @BindView(R.id.imgGiftDisplay)
        ImageView imgGift;
        @BindView(R.id.txtName)
        TextView tvGiftName;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public interface IOnItemClickListener extends BaseAdapterListener {
        void onItemClick(Gift item, int position);
    }

}
