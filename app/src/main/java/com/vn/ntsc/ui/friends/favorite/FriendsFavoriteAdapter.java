package com.vn.ntsc.ui.friends.favorite;

import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.vn.ntsc.R;
import com.vn.ntsc.repository.model.favorite.FriendsFavoriteBean;
import com.vn.ntsc.utils.ImagesUtils;
import com.vn.ntsc.utils.RegionUtils;
import com.vn.ntsc.utils.Utils;
import com.vn.ntsc.utils.time.TimeUtils;
import com.vn.ntsc.widget.adapter.BaseViewHolder;
import com.vn.ntsc.widget.adapter.MultifunctionAdapter;
import com.vn.ntsc.widget.views.textview.TextViewVectorCompat;

import java.text.ParseException;
import java.util.Calendar;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by hnc on 08/08/2017.
 */

public class FriendsFavoriteAdapter extends MultifunctionAdapter<FriendsFavoriteAdapter.ViewHolder, FriendsFavoriteBean> {

    private RegionUtils regionUtils;

    public FriendsFavoriteAdapter(FavoriteEventClickListener listener) {
        super(listener);
    }

    @Override
    protected ViewHolder onInjectViewHolder(ViewGroup parent, int viewType) {
        if (regionUtils == null)
            regionUtils = new RegionUtils(parent.getContext());
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_friends_favorited, parent, false));
    }

    @Override
    protected void onViewReady(final ViewHolder holder, final FriendsFavoriteBean item, final int position) {
        holder.mTxtName.setText(item.userName);
        holder.mTxtAge.setText(String.format(holder.itemView.getContext().getResources().getString(R.string.common_age_2), String.valueOf(item.age)));
        holder.mTxtTime.setText(item.lastLogin);
        holder.mTxtLocation.setText(regionUtils.getRegionName(item.region));

        ImagesUtils.loadRoundedAvatar(item.thumbnailUrl, item.gender, holder.mImgAvatar);

        try {
            Calendar calendarNow = Calendar.getInstance();

            Calendar calendarSend = Calendar.getInstance(TimeZone.getDefault());
            calendarSend.setTime(TimeUtils.YYYYMMDDHHMMSS.parse(item.lastLogin));

            holder.mTxtTime.setText(TimeUtils.getTimelineDif(calendarSend, calendarNow));
        } catch (ParseException e) {
            e.printStackTrace();
            holder.mTxtTime.setText(R.string.common_now);
        }

        // menu click
        holder.mImvMenuMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((FavoriteEventClickListener<FriendsFavoriteBean>) listener).onMenuItemClick(item, holder.mImvMenuMore, position);
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((FavoriteEventClickListener<FriendsFavoriteBean>) listener).onItemClick(item, holder.itemView, position);
            }
        });
    }

    public static class ViewHolder extends BaseViewHolder {

        @Nullable
        @BindView(R.id.img_avatar)
        ImageView mImgAvatar;
        @Nullable
        @BindView(R.id.txt_name)
        TextView mTxtName;
        @Nullable
        @BindView(R.id.txt_age)
        TextView mTxtAge;
        @Nullable
        @BindView(R.id.txt_time)
        TextViewVectorCompat mTxtTime;
        @Nullable
        @BindView(R.id.txt_location)
        TextViewVectorCompat mTxtLocation;
        @Nullable
        @BindView(R.id.imv_menu_more)
        ImageView mImvMenuMore;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
