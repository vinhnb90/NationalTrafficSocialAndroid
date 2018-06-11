package com.vn.ntsc.ui.search.result;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.vn.ntsc.R;
import com.vn.ntsc.repository.model.search.MeetPeopleBean;
import com.vn.ntsc.utils.ImagesUtils;
import com.vn.ntsc.utils.RegionUtils;
import com.vn.ntsc.utils.Utils;
import com.vn.ntsc.utils.time.TimeUtils;
import com.vn.ntsc.widget.adapter.BaseAdapterListener;
import com.vn.ntsc.widget.adapter.BaseViewHolder;
import com.vn.ntsc.widget.adapter.MultifunctionAdapter;

import java.text.ParseException;
import java.util.Calendar;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by hnc on 22/08/2017.
 */

public class SearchResultAdapter extends MultifunctionAdapter<SearchResultAdapter.ViewHolder, MeetPeopleBean> {

    private IOnItemClickListener onClickListener;
    private IOnItemMenuClickListener onMenuClickListener;

    public SearchResultAdapter(IOnItemClickListener listener) {
        super(listener);
        this.onClickListener = listener;
    }

    @Override
    protected void onViewReady(final ViewHolder holder, final MeetPeopleBean item, final int position) {

        Context mContext = holder.getViewRoot().getContext();

        String time = mContext.getResources().getString(R.string.common_now);
        try {

            Calendar calendarNow = Calendar.getInstance();

            Calendar calendarSend = Calendar.getInstance(TimeZone.getDefault());
            calendarSend.setTime(TimeUtils.YYYYMMDDHHMMSS.parse(item.isLastLogin));

            time = TimeUtils.getTimelineDif(calendarSend, calendarNow);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        holder.mTxtLocation.setText(RegionUtils.getInstance(mContext).getRegionName(item.region));
        holder.mTxtName.setText(item.userName);
        holder.mTxtTime.setText(time);

        if (item.isFav == 0) {
            holder.mImvFavorite.setBackgroundResource(R.drawable.ic_list_buzz_item_favorite);
        } else {
            holder.mImvFavorite.setBackgroundResource(R.drawable.ic_list_buzz_item_favorited);
        }

        ImagesUtils.loadRoundedAvatar(item.thumbnailUrl, item.gender, holder.mImgAvatar);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onClickListener != null) {
                    onClickListener.onItemClick(holder.itemView, item, position);
                }
            }
        });

        holder.mImvFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onMenuClickListener != null) {
                    onMenuClickListener.onMenuItemClick(holder.mImvFavorite, item, position);
                }
            }
        });
    }

    public void setMenuItemClick(IOnItemMenuClickListener listener) {
        this.onMenuClickListener = listener;
    }

    /**
     * Update background Icon Favorite
     * Call when server callback add favorite success
     *
     * @param position: position of item favorite
     */
    public void updateBackgroundItemFavorite(int position) {
        MeetPeopleBean meetPeopleBean = getData(position);
        if (meetPeopleBean.isFav == 0) {
            meetPeopleBean.isFav = 1;
        } else {
            meetPeopleBean.isFav = 0;
        }
        notifyItemChanged(position);
    }

    @Override
    protected ViewHolder onInjectViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_result, parent, false));
    }

    public static class ViewHolder extends BaseViewHolder {
        @BindView(R.id.img_avatar)
        ImageView mImgAvatar;
        @BindView(R.id.txt_name)
        TextView mTxtName;
        @BindView(R.id.txt_location)
        TextView mTxtLocation;
        @BindView(R.id.txt_time)
        TextView mTxtTime;
        @BindView(R.id.imv_favorite)
        ImageView mImvFavorite;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

            // set drawable left for TextView
            // fix crash app because TextView cannot set drawable vector image below 21
            if (null != mTxtTime)
                Utils.setVectorDrawableLeft(R.drawable.ic_access_time, mTxtTime);
            if (null != mTxtLocation)
                Utils.setVectorDrawableLeft(R.drawable.ic_location_on_black_24dp, mTxtLocation);
        }
    }


    public interface IOnItemClickListener extends BaseAdapterListener {
        void onItemClick(View view, MeetPeopleBean item, int position);
    }

    public interface IOnItemMenuClickListener {
        void onMenuItemClick(View view, MeetPeopleBean item, int position);
    }
}
