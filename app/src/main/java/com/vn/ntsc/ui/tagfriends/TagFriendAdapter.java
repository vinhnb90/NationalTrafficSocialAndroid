package com.vn.ntsc.ui.tagfriends;

import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.vn.ntsc.R;
import com.vn.ntsc.repository.model.timeline.datas.sub.ListTagFriendsBean;
import com.vn.ntsc.utils.ImagesUtils;
import com.vn.ntsc.utils.LogUtils;
import com.vn.ntsc.utils.RegionUtils;
import com.vn.ntsc.utils.Utils;
import com.vn.ntsc.widget.adapter.BaseViewHolder;
import com.vn.ntsc.widget.adapter.MultifunctionAdapter;
import com.vn.ntsc.widget.views.textview.TextViewVectorCompat;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * The TagFriendAdapter class setup UI about list friend was favorites by yourself
 *
 * @author Created by Robert on 2017 Sep 14
 */
public class TagFriendAdapter extends MultifunctionAdapter<TagFriendAdapter.ViewHolder, ListTagFriendsBean> implements Filterable {
    private final String TAG = getClass().getSimpleName();

    private RegionUtils regionUtils;

    List<ListTagFriendsBean> dataTag = new ArrayList<>();
    final List<ListTagFriendsBean> dataFinal = new ArrayList<>();

    TagFriendAdapter(TagFriendEventListener listener) {
        super(listener);
    }

    @Override
    protected ViewHolder onInjectViewHolder(ViewGroup parent, int viewType) {
        if (this.regionUtils==null)
            this.regionUtils = new RegionUtils(parent.getContext());
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_tag_friends_favorited, parent, false));
    }

    @Override
    protected void onViewReady(final ViewHolder holder, final ListTagFriendsBean item, final int position) {

        holder.mTxtName.setText(item.userName);
        holder.mTxtAge.setText(String.format(holder.itemView.getContext().getResources().getString(R.string.common_age_2),String.valueOf(item.age)));
        holder.mTxtLocation.setText(regionUtils.getRegionName(item.region));

        ImagesUtils.loadRoundedAvatar(item.avatar, item.gender, holder.mImgAvatar);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((TagFriendEventListener<ListTagFriendsBean>) listener).onItemClick(item, holder.itemView, position);
            }
        });
    }

    public static class ViewHolder extends BaseViewHolder {

        @BindView(R.id.img_avatar)
        ImageView mImgAvatar;
        @BindView(R.id.txt_name)
        TextView mTxtName;
        @BindView(R.id.txt_age)
        TextView mTxtAge;
        @BindView(R.id.txt_location)
        TextViewVectorCompat mTxtLocation;
        @BindView(R.id.tag_friend_item_layout)
        ConstraintLayout mTagFriendItemLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            // Set drawable left for location TextView
        }
    }

    public TagFriendAdapter setDataTag(List<ListTagFriendsBean> dataTag) {
        this.dataTag = dataTag;
        return this;
    }

    public void setDataFinal(List<ListTagFriendsBean> dataFinal) {
        this.dataFinal.clear();
        this.dataFinal.addAll(dataFinal);
    }

    @Override
    public Filter getFilter() {

        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                FilterResults results = new FilterResults();

                final List<ListTagFriendsBean> filteredModelList = new ArrayList<>();

                final List<ListTagFriendsBean> newFilterDataTag = new ArrayList<>();

                boolean isAdded;
                for (ListTagFriendsBean tagItem : dataFinal) {
                    isAdded = true;
                    for (ListTagFriendsBean item : dataTag) {
                        if (tagItem.userId.equals(item.userId)) {
                            isAdded = false;
                        }
                    }

                    if (isAdded) {
                        newFilterDataTag.add(tagItem);
                    }
                }

                if (constraint != null && constraint.length() > 0) {
                    String lowerCaseQuery = constraint.toString().toLowerCase().trim();
                    LogUtils.i(TAG, "Filter : " + lowerCaseQuery);
                    for (ListTagFriendsBean model : newFilterDataTag) {
                        final String text = model.userName.toLowerCase();
                        if (text.contains(lowerCaseQuery)) {
                            filteredModelList.add(model);
                        }
                    }
                } else {
                    LogUtils.i(TAG, "No filter");
                    filteredModelList.addAll(newFilterDataTag);
                }

                LogUtils.i(TAG, "Size filter list : " + filteredModelList.size());
                results.values = filteredModelList;
                results.count = filteredModelList.size();

                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mData.clear();
                mData.addAll((List<ListTagFriendsBean>) results.values);
                notifyDataSetChanged();
            }
        };
    }

}
