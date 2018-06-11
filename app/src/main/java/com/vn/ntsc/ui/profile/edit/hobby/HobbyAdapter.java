package com.vn.ntsc.ui.profile.edit.hobby;

import android.support.v4.util.ArraySet;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vn.ntsc.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by dev22 on 8/23/17.
 */
public class HobbyAdapter extends RecyclerView.Adapter<HobbyHolder> {
    private List<String> listData = new ArrayList<>();
    private ArraySet<Integer> selected = new ArraySet<>();

    @Override
    public HobbyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new HobbyHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_hobby, parent, false));
    }

    @Override
    public void onBindViewHolder(final HobbyHolder holder, int position) {
        if (selected.contains(position))
            holder.item.setChecked(true);
        else
            holder.item.setChecked(false);

        holder.item.setText(listData.get(position));
        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = holder.getAdapterPosition();
                if (selected.contains(pos)) selected.remove(pos);
                else selected.add(pos);
                notifyItemChanged(pos);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    /**
     * update data
     *
     * @param listHobby data to replace
     */
    public void updateData(String[] listHobby) {
        listData.clear();
        Collections.addAll(listData, listHobby);
        notifyDataSetChanged();
    }

    /**
     * @return selected position, multi choice
     */
    public int[] getSelectedPosition() {
        int[] tmp = new int[selected.size()];
        int i = 0;
        for (int v : selected) {
            tmp[i] = v;
            i++;
        }
        return tmp;
    }

    /**
     * restore previous state
     *
     * @param selectedPositions array of selection
     */
    public void setSelectedPosition(int[] selectedPositions) {
        if (selectedPositions != null) {
            selected.clear();
            for (int selection : selectedPositions) {
                selected.add(selection);
            }
            notifyDataSetChanged();
        }
    }
}
