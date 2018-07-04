package com.vn.ntsc.widget.adapter;

import android.content.ComponentCallbacks2;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.bumptech.glide.Glide;

public class BaseViewHolder extends RecyclerView.ViewHolder {

    public BaseViewHolder(final View view) {
        super(view);
    }

    void onRecycled() {
        Glide.get(itemView.getContext()).trimMemory(ComponentCallbacks2.TRIM_MEMORY_MODERATE);
    }
}
