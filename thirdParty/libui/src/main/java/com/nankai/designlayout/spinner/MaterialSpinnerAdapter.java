package com.nankai.designlayout.spinner;

import android.content.Context;

import java.util.List;

/**
 * Created by nankai on 3/30/2017.
 */

public class MaterialSpinnerAdapter<T> extends MaterialSpinnerBaseAdapter {

    private final List<T> items;

    public MaterialSpinnerAdapter(Context context, List<T> items) {
        super(context);
        this.items = items;
    }

    @Override public int getCount() {
        return items.size() - 1;
    }

    @Override public T getItem(int position) {
        if (position >= getSelectedIndex()) {
            return items.get(position + 1);
        } else {
            return items.get(position);
        }
    }

    @Override public T get(int position) {
        return items.get(position);
    }

    @Override public List<T> getItems() {
        return items;
    }

}