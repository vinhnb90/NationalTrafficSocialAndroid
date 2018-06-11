package com.vn.ntsc.ui.profile.edit.job;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vn.ntsc.R;
import com.vn.ntsc.utils.DimensionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by dev22 on 8/23/17.
 */
public class JobAdapter extends RecyclerView.Adapter<JobHolder> {
    private static final int TYPE_TOP = 0;
    private static final int TYPE_BOTTOM = 1;
    private static final int TYPE_MID = 2;
    private List<String> listJob = new ArrayList<>();
    private int selectedPosition = -1;


    @Override
    public JobHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new JobHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_job, parent, false));
    }

    @Override
    public void onBindViewHolder(final JobHolder holder, int position) {
        int type = getItemViewType(position);
        switch (type) {
            case TYPE_TOP:
                holder.itemView.setBackgroundResource(R.drawable.bg_round_field_top);
                break;
            case TYPE_BOTTOM:
                holder.itemView.setBackgroundResource(R.drawable.bg_round_field_bottom);
                break;
            default:
                holder.itemView.setBackgroundResource(R.drawable.bg_round_field_mid);
                break;
        }

        holder.itemView.setPadding(DimensionUtils.convertDpToPx(16),
                DimensionUtils.convertDpToPx(16),
                DimensionUtils.convertDpToPx(16),
                DimensionUtils.convertDpToPx(16));

        holder.item.setChecked(position == selectedPosition);

        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedPosition != holder.getAdapterPosition() || selectedPosition == -1) {
                    selectedPosition = holder.getAdapterPosition();
                    notifyDataSetChanged();
                } else {
                    selectedPosition = -1;
                    notifyDataSetChanged();
                }
            }
        });
        holder.item.setText(listJob.get(position));
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) return TYPE_TOP;
        else if (position == listJob.size() - 1) return TYPE_BOTTOM;
        else return TYPE_MID;
    }

    @Override
    public int getItemCount() {
        return listJob.size();
    }

    /**
     * update all data
     *
     * @param listJob data to replace
     */
    public void updateData(String[] listJob) {
        this.listJob.clear();
        Collections.addAll(this.listJob, listJob);
        notifyDataSetChanged();
    }

    /**
     * @return selected item (single choice)
     */
    public int getSelectedPosition() {
        return selectedPosition;
    }

    /**
     * restore previous state
     *
     * @param selectedPosition position to set checked
     */
    public void setSelectedPosition(int selectedPosition) {
        this.selectedPosition = selectedPosition;
        notifyItemChanged(selectedPosition);
    }
}
